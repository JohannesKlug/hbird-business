package org.hbird.business.configurator;

import org.apache.camel.model.ProcessorDefinition;
import org.hbird.business.validation.limits.BaseLimitChecker;
import org.hbird.business.validation.limits.LowerLimitChecker;
import org.hbird.business.validation.limits.StaticLimitChecker;
import org.hbird.business.validation.limits.UpperLimitChecker;
import org.hbird.exchange.configurator.StartLimitComponent;
import org.hbird.exchange.validation.Limit;
import org.hbird.exchange.validation.Limit.eLimitType;

public class LimitCheckComponentBuilder extends ComponentBuilder {

	protected String inCheckRoute  = StandardEndpoints.monitoring + "?selector=name='PARAMETERNAME'";
	protected String inEnableRoute = StandardEndpoints.monitoring + "?selector=name='LIMITNAME_SWITCH'";
	protected String inUpdateRoute = StandardEndpoints.monitoring + "?selector=name='LIMITNAME_UPDATE'";
	protected String outParameters = StandardEndpoints.monitoring;

	@Override
	public void doConfigure() {

		StartLimitComponent request = (StartLimitComponent) this.request;

		Limit limit = (Limit) request.getArguments().get("limit");
		if (limit.type == eLimitType.Lower) {
			createRoute(limit.limitOfParameter, new LowerLimitChecker(limit));			
		}
		else if (limit.type == eLimitType.Upper) {
			createRoute(limit.limitOfParameter, new UpperLimitChecker(limit));			
		}
		else if (limit.type == eLimitType.Static) {
			createRoute(limit.limitOfParameter, new StaticLimitChecker(limit));			
		}
	}
	
	protected void createRoute(String parameter, BaseLimitChecker limit) {
		
		/** Create the route for limit checking. */
		ProcessorDefinition route = from(inCheckRoute.replaceAll("PARAMETERNAME", parameter))
		.bean(limit, "processParameter");
		addInjectionRoute(route);

		/** Create the route for enabling/disabling limit checking. */
		route = from(inEnableRoute.replaceAll("LIMITNAME", limit.getLimit().getName()))
		.bean(limit, "processEnabled");
		addInjectionRoute(route);
		
		/** Create the route for changing the limit value. */
		route = from(inUpdateRoute.replaceAll("LIMITNAME", limit.getLimit().getName()))
		.bean(limit, "processLimit");
		addInjectionRoute(route);
		
		/** Route for commands to this component, i.e. configuration commands. */
		from(StandardEndpoints.commands + "?" + addDestinationSelector(getComponentName())).bean(defaultCommandHandler, "receiveCommand");
	}

	public String getInCheckRoute() {
		return inCheckRoute;
	}

	public void setInCheckRoute(String inCheckRoute) {
		this.inCheckRoute = inCheckRoute;
	}

	public String getInEnableRoute() {
		return inEnableRoute;
	}

	public void setInEnableRoute(String inEnableRoute) {
		this.inEnableRoute = inEnableRoute;
	}

	public String getInUpdateRoute() {
		return inUpdateRoute;
	}

	public void setInUpdateRoute(String inUpdateRoute) {
		this.inUpdateRoute = inUpdateRoute;
	}

	public String getOutParameters() {
		return outParameters;
	}

	public void setOutParameters(String outParameters) {
		this.outParameters = outParameters;
	}
}
