package org.hbird.exchange.movementcontrol;


import org.hbird.exchange.core.Command;
import org.hbird.exchange.core.Parameter;

public class MoveForward extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8419315289862341607L;

	public MoveForward() {};
	
	public MoveForward(String issuedBy) {
		super(issuedBy, "MoveForward", "Move the bot forwards. The distance (in meter) is set by the argument 'Distance'.");
		arguments.add(new Parameter(issuedBy, "Distance", "The distance in meter.", null, "Meter"));
	}

	public MoveForward(String issuedBy, long executionTime) {
		super(issuedBy, "MoveForward", "Move the bot forwards. The distance (in meter) is set by the argument 'Distance'.");
		this.executionTime = executionTime;
	}

}