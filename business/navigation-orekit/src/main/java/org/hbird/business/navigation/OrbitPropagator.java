package org.hbird.business.navigation;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Body;
import org.hbird.exchange.navigation.OrbitPredictionRequest;
import org.hbird.exchange.navigation.OrbitalState;
import org.hbird.exchange.navigation.Location;

/**
 * Component for generating orbital prediction requests at intervals.
 * 
 * An orbital request must define;
 * - name. The name of the prediction request. The same name will be set on the orbital states generated. Can be used to distinguish between for example 'Measured' and 'Predicted' orbital states.
 * - satellite. The satellite for which the prediction is being made.
 * - initialOrbitalState. The state from which the orbit should be predicted. Includes the time, the position and the velocity.
 * - List of Locations. A list of locations for which contact events (established / lost) should be created.
 * - delta propagation. The period for which the prediction should be made.
 * - step size. The size of each step. The number of generated Orbital States will thus be the delta propagation / step size.
 * 
 * Of these, the initialOrbitalState will typically change between each prediction request, i.e. a each prediction
 * will typically be done from the current time, position and velocity. To perform orbital predictions, the scheduler 
 * must thus continuously be (re)configured with the latest state. This can be done in different ways
 * - Orbital states can be routed to the classes 'procesOrbitalState' method. The latest received orbital state will
 *   thereafter be used for the next prediction. 
 * - The position can be routed to the method 'processPosition'. The new position and its timestamp will be set on the
 *   orbital state.
 * - The velocity can be routed to the method 'processVelocity'. The new velocity and its timestamp will be set on the
 *   orbital state.
 * 
 * Notice that the prediction is not performed each time a new orbital state, position or velocity is routed to the
 * bean, but based on a schedule as described below.
 *
 */
public class OrbitPropagator {

	/** Unique UUID */
	private static final long serialVersionUID = -4787275022054480174L;

	/** The locations for which contact events should be calculated. */
	protected List<Location> locations = new ArrayList<Location>();

	/** The period for which a propagation shall be calculated. The number of orbit predictions will be
	 * deltapropagation / stepSize. */
	protected Double deltaPropagation = 3600.;

	/** The interval between each prediction. */
	protected Double stepSize = 60.;

	/** Name of the orbital request. Will also be used for each orbital state generated from a 
	 * request. */
	protected String name = "Propagated";

	/** Time stamp (ms) of the last prediction. */
	protected long lastPrediction = 0l;

	/** Frequency (ms) at which predictions should be made. */
	protected long predictionInterval = 24 * 60 * 60 * 1000;

	/**
	 *  Method to create a prediction request. The request will only be generated if 
	 *  the current orbital state has been set (i.e. an initial state from which the 
	 *  prediction can be made) and if the prediction interval has expired.
	 * 
	 * @param exchange The exchange triggering the event. The IN body will be set to the 
	 * request, or the route will be stopped.
	 */
	public OrbitPredictionRequest propagate(@Body OrbitalState state) {
		synchronized(this) {
			OrbitPredictionRequest request = new OrbitPredictionRequest(name, state.getSatelitte(), state, locations);
			request.deltaPropagation = deltaPropagation; 
			request.stepSize = stepSize;
			
			return request;
		}
	}

	/**
	 * Adds a position to the list of positions for which contact events should be generated.
	 * 
	 * @param location
	 */
	public void addLocation(@Body Location location) {
		synchronized(this) {
			locations.add(location);		
		}
	}

	public void removeLocation(@Body Location location) {
		synchronized(this) {
			locations.remove(location);		
		}
	}

	/**
	 * Sets the interval between predictions. 
	 * 
	 * @param predictionInterval The new interval (ms) to be used
	 */
	public void setPredictionInterval(long predictionInterval) {
		this.predictionInterval = predictionInterval;
	}

	/**
	 * Sets the interval for which predictions shall be made.
	 * 
	 * @param deltaPropagation
	 */
	public void setDeltaPropagation(Double deltaPropagation) {
		this.deltaPropagation = deltaPropagation;
	}

	/**
	 * Sets the size of each step when performing predictions.
	 * 
	 * @param stepSize Size (ms) of each step.
	 */
	public void setStepSize(Double stepSize) {
		this.stepSize = stepSize;
	}
}
