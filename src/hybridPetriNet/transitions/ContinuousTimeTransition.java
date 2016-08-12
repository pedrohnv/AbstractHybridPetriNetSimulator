/**
he MIT License (MIT)

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
import hybridPetriNet.places.Place;

/**
 * The continuous time transition fires only in the FIRST ITERATION (when
 * times advances), i.e., the firing function is exclusively a function of
 * time (and not iteration).
 */
public class ContinuousTimeTransition extends Transition {
		
	/**
	 * The inputed firing function is multiplied by the time step.
	 * @param name
	 * @param firingFunction
	 * @param priority
	 */
	public ContinuousTimeTransition(String name, int priority,
										String firingFunctionExpression){
		super(name, priority, firingFunctionExpression);
		this.type = TransitionType.CONTINUOUS;
	}
	
	/**
	 * The inputed firing function is multiplied by the time step during the
	 * simulation.
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 1
	 */
	public ContinuousTimeTransition(String name) {
		this(name, 1, "1.0");		
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
	 * The firing is done via integration in the StateSpace class.
	 */
	@Override
	public void fire(Place place, double weight) {
		// the firing is done via integration in the state space class
		// leave this method blank
	}
}
