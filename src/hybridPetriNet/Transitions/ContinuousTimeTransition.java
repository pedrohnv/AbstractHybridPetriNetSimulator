/**
The MIT License (MIT)

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
import hybridPetriNet.Places.Place;

/**
 * The continuous time transition fires only in the FIRST ITERATION (when
 * times advances), i.e., the firing function is exclusively a function of
 * time (and not iteration).
 * <p>
 * Set the firing function of this transition as the desired output TIMES
 * the Step time. In other words, this transition will change markings
 * corresponding to its firing function in ONE time step.
 */
public class ContinuousTimeTransition extends Transition {
			
	/**
	 * The inputed firing function is multiplied by the time step during the
	 * simulation.
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 1
	 */
	public ContinuousTimeTransition(String name) {
		super(name);
	}

	/**
	 * The inputed firing function is multiplied by the time step during the
	 * simulation.
	 * @param name
	 * @param priority
	 * @param firingFunction = 1
	 */
	public ContinuousTimeTransition(String name, int priority) {
		super(name, priority);
	}

	/**
	 * The inputed firing function is multiplied by the time step during the
	 * simulation.
	 * @param name
	 * @param priority
	 * @param firingFunction
	 */
	public ContinuousTimeTransition(String name, int priority, double firingFunction) {
		super(name, priority, firingFunction);
	}
	
	/**
	 * The inputed firing function is multiplied by the time step during the
	 * simulation.
	 * @param name
	 * @param firingFunction
	 * @param priority = 1
	 */
	public ContinuousTimeTransition(String name, double firingFunction){
		super(name, firingFunction);
		this.firingFunctionString = String.valueOf( firingFunction );
	}
	
	/**
	 * The inputed firing function is multiplied by the time step during the
	 * simulation.
	 * @param name
	 * @param firingFunction
	 * @param priority = 1
	 */
	public ContinuousTimeTransition(String name, String firingFunctionExpression){
		super(name);
		// firing function is initially (default) set to 1. Before the first
		// iteration it should be updated to it's true value.
		this.firingFunctionString = firingFunctionExpression;
	}
	
	/**
	 * The inputed firing function is multiplied by the time step.
	 * @param name
	 * @param firingFunction
	 * @param priority
	 */
	public ContinuousTimeTransition(String name, String firingFunctionExpression,
								int priority){
		super(name);
		// firing function is initially (default) set to 1. Before the first
		// iteration it should be updated to it's true value.
				
		this.firingFunctionString = firingFunctionExpression;
		this.priority = priority;
	}
		
	/**
	 *  This method is used inside an arc method.
	 *  <br>
	 *  Enables the transition only if it's the zeroth iteration.
	 */
	@Override
	public void setEnabledStatus(boolean status) {
		
		if (Evolution.getIteration() > 0) { 
			this.enabledStatus = false;
		}	
		else if (status){
			this.enabledStatus = true;
		}
	}
	
	/**
	 * The continuous transition's firing does an integration based on it's
	 * firing speed.
	 * <p>
	 * This method multiplies the firing function by the time step.
	 */
	@Override
	public void fire(Place place, double weight) {
		Double adjustedFiringFunction = this.firingFunction * Evolution.getTimeStep(); 
		place.changeMarkings(adjustedFiringFunction, weight);
	}
}
