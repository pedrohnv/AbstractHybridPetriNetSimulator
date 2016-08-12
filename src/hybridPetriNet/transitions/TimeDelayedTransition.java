/**
 * The MIT License (MIT)

Copyright (c) 2016 Pedro Henrique Nascimento Vieira

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */
package hybridPetriNet.transitions;

import enums.TransitionType;
import hybridPetriNet.Evolution;

/**
 * This transition is one that fires after a time delay.
 * The enabled time is updated in the update method, which
 * should be called at every new time advancement.
 * <p>
 * After enabled for the delay time, it can fire at every ITERATION.
 * <p>
 * new attributes: enabled time, delay.
 * <p>
 * Though this transition can function as a normal one (setting the delay
 * to zero), it is highly discouraged because of the extra code that is
 * run in the setEnabledStatus and update methods, i.e., greater processing
 * time in each.
 */
public class TimeDelayedTransition extends Transition {

	/**
	 * How much time the transition is enabled
	 */
	protected double enabledTime = 0;
	
	/**
	 * How much time must be enabled before switching to new firing function
	 */
	protected double delay = 1;
	
	/**
	 * @param name
	 * @param delay = 1
	 * @param firingFunction
	 * @param priority
	 */
	public TimeDelayedTransition(String name, int priority, String firingFunction,
			double delay) {
		super(name, priority, firingFunction);
		this.delay = delay;
		this.type = TransitionType.TIME_DELAYED;
	}

	/**
	 * @param name
	 * @param delay = 1
	 * @param firingFunction
	 * @param priority
	 */
	public TimeDelayedTransition(String name, int priority, String firingFunction) {
		this(name, priority, firingFunction, 1.0);
	}

	/**
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 0
	 * @param delayedFiringFunction = 1
	 * @param delay = 1
	 */
	public TimeDelayedTransition(String name) {
		this(name, 1, "1.0", 1.0);
	}
	
	/*
	 * accessors
	 */	
	public double getEnabledTime() {return this.enabledTime;}
	
	public double getDelay() {return this.delay;}
	
	/*
	 * mutators
	 */
	public void changeEnabledTime(double newValue) {
		this.enabledTime = newValue;}
	
	public void changeDelay(double newValue) {
		if (newValue > 0){
			this.delay = newValue;
		}
		else {
			throw new UnsupportedOperationException("delay must be greater than zero!");
		}
	}
	
	/**
	 *  This method is used inside an arc method.
	 */
	@Override
	public void setEnabledStatus(boolean status) {
		
		if ((status) && (this.enabledTime >= this.delay)){		
			this.enabledStatus = status;
		}
		// order to disable
		else if (!status){			
			this.enabledTime = 0.0;
			this.enabledStatus = status;
		}
		
		// if set to disabled, zero the firing function and enabled time 		
	}

	@Override
	public void timeUpdate(){
		// evaluate firing function
		this.firingFunction = evaluator.evaluate(this.firingFunctionString);
		
		this.enabledTime += Evolution.getTimeStep();
	}
	
	@Override
	public String toString(){
		String info = super.toString();
		info += delay + ";";
		return info;
	}
}
