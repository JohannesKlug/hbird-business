package org.hbird.business.navigation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.log4j.Logger;
import org.hbird.exchange.navigation.Location;
import org.hbird.exchange.navigation.OrbitPredictionRequest;
import org.hbird.exchange.navigation.OrbitalState;
import org.hbird.exchange.navigation.Satellite;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.EventDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.PVCoordinates;

/** 
 * Processor of 'OrbitalPredictionRequests', implemented based on the OREKIT open source
 * tool. 
 * 
 * For an example of how to use, see https://www.orekit.org/forge/projects/orekit/repository/revisions/4decf40db88b02ce82ec9cac7629536c701ac535/entry/src/tutorials/fr/cs/examples/propagation/VisibilityCheck.java
 * 
 * NOTE NOTE NOTE NOTE
 * 
 * The following system property should be set orekit.data.path=[path to UTC-TAI.history file]
 * 
 * 
 */
public class OrbitPredictor {

	private static org.apache.log4j.Logger LOG = Logger.getLogger(OrbitPredictor.class);

	protected String name = "OrbitPredictor";

	/** FIXME I don't know what this does but OREKIT needs it...*/
	protected double maxcheck = 1.;

	/** gravitation coefficient */
	protected Double mu = 3.986004415e+14; 

	/** flattening */
	protected Double f  =  1.0 / 298.257223563; 

	/** equatorial radius in meter */
	protected Double ae = 6378137.0; 

	protected List<Object> results = new ArrayList<Object>();

	protected Map<String, OrbitalState> lastKnownOrbitalState = new HashMap<String, OrbitalState>();

	/** The exchange must contain a OrbitalState object as the in-body. 
	 * @throws OrekitException */
	@Handler
	public List<Object> predictOrbit(@Body OrbitPredictionRequest request) throws OrekitException {

		results.clear();

		/** If the position of this satellite has been set to null, then try to use the last know position. */
		if (request.getPosition() == null) {
			if (lastKnownOrbitalState.containsKey(request.getSatellite().getName())) {
				request.setPosition(lastKnownOrbitalState.get(request.getSatellite().getName()).position);
			}
		}

		/** If the velocity of this satellite has been set to null, then try to use the last know velocity. */
		if (request.getVelocity() == null) {
			if (lastKnownOrbitalState.containsKey(request.getSatellite().getName())) {
				request.setVelocity(lastKnownOrbitalState.get(request.getSatellite().getName()).velocity);
			}
		}

		if (request.getPosition() == null || request.getVelocity() == null) {
			LOG.error("Received orbital propagation request with empty position and velocity attributes. No known orbital state exist for satellite " + request.getSatellite().getName());
		}
		else {
			// Inertial frame			
			Vector3D position = new Vector3D((Double) request.getPosition().p1, (Double) request.getPosition().p2, (Double) request.getPosition().p3);
			Vector3D velocity = new Vector3D((Double) request.getVelocity().p1, (Double) request.getVelocity().p2, (Double) request.getVelocity().p3);
			PVCoordinates pvCoordinates = new PVCoordinates(position, velocity);

			AbsoluteDate initialDate = new AbsoluteDate(new Date(request.getStarttime()), TimeScalesFactory.getUTC());

			Frame inertialFrame = FramesFactory.getEME2000();

			// Initial date
			Orbit initialOrbit = new KeplerianOrbit(pvCoordinates, inertialFrame, initialDate, mu);

			Propagator propagator = new KeplerianPropagator(initialOrbit);

			/** Create dataset identifier based on the time. */
			String datasetidentifier = (new Date()).toString();

			/** Register the visibility events for the requested locations. */
			for (Location location : request.getLocations()) {
				GeodeticPoint point = new GeodeticPoint((Double) location.p1, (Double) location.p2, (Double) location.p3);				
				BodyShape earth = new OneAxisEllipsoid(ae, f, FramesFactory.getITRF2005());
				TopocentricFrame sta1Frame = new TopocentricFrame(earth, point, location.getName());

				/** Register the injector that will send the detected events, for this location, to the propagator. */
				EventDetector sta1Visi = new LocationContactEventInjector(maxcheck, location.getThresholdElevation(), sta1Frame, request.getSatellite(), location, datasetidentifier, this);
				propagator.addEventDetector(sta1Visi);				
			}

			OrbitalStateInjector injector = new OrbitalStateInjector(datasetidentifier, this, request.getName(), request.getDescription(), request.getSatellite());
			injector.setDatasetidentifier(request.getSatellite().getName() + "/" + (new Date()).toGMTString());			

			propagator.setMasterMode(request.getStepSize(), injector);			
			propagator.propagate(new AbsoluteDate(initialDate, request.getDeltaPropagation()));
		}		

		return results;
	}

	public void addResult(Object result) {
		this.results.add(result);
	}


	/**
	 * Method to record the last known orbital states of satellites. Is used to simplify the 
	 * orbit request submission; if no orbital state is provided by the client, then the 
	 * module automatically try to progress the orbit based on the last known orbit.
	 * 
	 * @param orbitalState The latest orbital state.
	 */
	public void recordOrbitalState(@Body OrbitalState orbitalState) {
		lastKnownOrbitalState.put(orbitalState.satelitte.getName(), orbitalState);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
