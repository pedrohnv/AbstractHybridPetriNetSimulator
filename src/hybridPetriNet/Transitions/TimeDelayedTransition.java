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
package hybridPetriNet.Transitions;

import hybridPetriNet.Evolution;

/**
 * This transition is one that fires after a time delay.
 * The enabled time is updated in the update method, which
 * should be called at every new time advancement.
 * 
 * After enabled for the delay time, it can fire at every ITERATION.
 * 
 * new attributes: delayed firing function (the value of the firing function
 * after the delay has passed), enabled time, delay.
 * 
 * Though this transition can function as a normal one (setting the delay
 * to zero), it is highly discouraged because of the extra code that is
 * run in the setEnabledStatus and update methods, i.e., greater processing
 * time in each.
 */
public class TimeDelayedTransition extends Transition {

	/**
	 * New value of firing function after delayed time has passed
	 */
	protected double delayedFiringFunction = 1;
	
	/**
	 * How much time the transition is enabled
	 */
	protected double enabledTime = 0;
	
	/**
	 * How much time must be enabled before switching to new firing function
	 */
	protected double delay = 1;
	
	/**
	 * 
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 0
	 * @param delayedFiringFunction = 1
	 * @param delay = 1
	 */
	public TimeDelayedTransition(String name) {
		super(name);		
		this.firingFunction = 0.0;
	}

	/**
	 * 
	 * @param name
	 * @param delay
	 * @param priority = 1
	 * @param firingFunction = 0
	 * @param delayedFiringFunction = 1
	 */
	public TimeDelayedTransition(String name, double delay) {
		super(name);
		this.delayedFiringFunction = this.firingFunction;
		this.firingFunction = 0.0;
		this.delay = delay;
	}	
	
	/**
	 * 
	 * @param name
	 * @param delay
	 * @param delayedFiringFunction
	 * @param priority = 1
	 * @param firingFunction = 0
	 */
	public TimeDelayedTransition(String name, double delay, 
				double delayedFiringFunction) {
		super(name);
		this.delayedFiringFunction = this.firingFunction;
		this.firingFunction = 0.0;
		this.delay = delay;
	}
	
	/**
	 * 
	 * @param name
	 * @param delay
	 * @param delayedFiringFunction
	 * @param priority
	 * @param firingFunction = 0
	 */
	public TimeDelayedTransition(String name, double delay, 
				double delayedFiringFunction, int priority) {
		super(name);
		this.delayedFiringFunction = this.firingFunction;
		this.firingFunction = 0.0;
		this.delay = delay;
		this.priority = priority;
	}
	
	/**
	 * Constructor for when a transition that fires as normal, then change the 
	 * firing speed after a delayed time.
	 * @param name
	 * @param delay
	 * @param delayedFiringFunction
	 * @param priority
	 * @param firingFunction
	 */
	public TimeDelayedTransition(String name, double delay, 
				double delayedFiringFunction, int priority,
				double firingFunction) {
		super(name);
		this.delayedFiringFunction = this.firingFunction;
		this.firingFunction = 0.0;
		this.delay = delay;
		this.priority = priority;
	}
	
	/*
	 * accessors
	 */
	public double getDelayedFiringFunction() {return this.delayedFiringFunction;}
	
	public double getEnabledTime() {return this.enabledTime;}
	
	public double getDelay() {return this.delay;}
	
	/*
	 * mutators
	 */
	public void changeDelayedFiringFunction(double newValue) {
		this.delayedFiringFunction = newValue;}
	
	public void changeEnabledTime(double newValue) {
		this.enabledTime = newValue;}
	
	public void changeDelay(double newValue) {
		this.delay = newValue;}

	/**
	 *  This method is used inside an arc method.
	 */
	public void setEnabledStatus(boolean status) {
		this.enabledStatus = status;
		
		if (! status) { 
			this.firingFunction = 0.0;
			this.enabledTime = 0.0;
			}
		// if set to disabled, zero the firing function and enabled time 
		
	}

	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update(){
		if (this.enabledStatus) {
		this.enabledTime += Evolution.getTimeStep();
		}
		
		if (this.enabledTime >= this.delay) {
			this.firingFunction = this.delayedFiringFunction;
		} 
		else {this.firingFunction = 0.0; }
	}
}
