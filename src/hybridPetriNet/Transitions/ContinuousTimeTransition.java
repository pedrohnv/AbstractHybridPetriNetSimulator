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
	 * Keys to retrieve values from the markingsMap of a Petri net, which
	 * may be used to store constants.
	 * <p>
	 * This variable is used to make the firing function of the transition
	 * a function of the markings in a place (can be multiple).
	 */
	private String[] variableKeys;
	
	/**
	 * This variable stores the firing function as a string, if it is not a
	 * constant.
	 * <p>
	 * It is parsed and evaluated at each update call.
	 */
	String firingFunctionString;
	
	/**
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 1
	 */
	public ContinuousTimeTransition(String name) {
		super(name);
		this.firingFunctionString = "1";
	}

	/**
	 * @param name
	 * @param priority
	 * @param firingFunction = 1
	 */
	public ContinuousTimeTransition(String name, int priority) {
		super(name, priority);
		this.firingFunctionString = "1";
	}

	/**
	 * @param name
	 * @param priority
	 * @param firingFunction
	 */
	public ContinuousTimeTransition(String name, int priority, double firingFunction) {
		super(name, priority, firingFunction);
		this.firingFunctionString = String.valueOf(firingFunction);
	}
	
	/**
	 * @param name
	 * @param firingFunction
	 * @param priority = 1
	 */
	public ContinuousTimeTransition(String name, double firingFunction){
		super(name, firingFunction);
		this.firingFunctionString = String.valueOf(firingFunction);
	}
	
	/**
	 * @param name
	 * @param firingFunction
	 * @param priority = 1
	 */
	public ContinuousTimeTransition(String name, String firingFunctionExpression){
		super(name);
		this.firingFunctionString = String.valueOf(firingFunction);
		this.firingFunction = 
	}
		
	/**
	 *  This method fires the transition. That is, changes the markings in
	 *  a place according to the arc weight and transition's firing.
	 *  <p>
	 *  The transition will fire only in the first iteration.
	 */	
	public void fire(Place place, double weight){
		
		 if (this.enabledStatus && (Evolution.getIteration() == 0)) {
			/*
			 *  if invalid new marking, exception is thrown.
			 */
			 place.changeMarkings(this.firingFunction, weight);			
		}
		else {
			throw new UnsupportedOperationException(
					"Transition not enabled, did not fire.");
		}		 
	}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	@Override
	public void update(){
		
	}
	
	/**
	 * Get the variables' keys.
	 * @return keys
	 */
	public String[] getVariableKeys() {
		return this.variableKeys;
	}
	
}
