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

import hybridPetriNet.MainClass;
import hybridPetriNet.Places.Place;

public class ContinuousTransition extends Transition {
	/**
	 * the continuous transition fires only in the FIRST ITERATION (when
	 * times advances), i.e., the firing function is exclusively a function of
	 * time (and not iteration).
	 * 
	 * Set the firing function of this transition as the desired output TIMES
	 * the Step time. In other words, this transition will change markings
	 * corresponding to its firing function in ONE time step.
	 */
		
	public ContinuousTransition(String name) {
		super(name);
	}

	public ContinuousTransition(String name, double firingFunction) {
		super(name, firingFunction);
	}

	public ContinuousTransition(String name, double firingFunction, int priority) {
		super(name, firingFunction, priority);
	}
		
		
	public void fire(Place place, double weight){
		/**
		 *  This method fires the transition. That is, changes the markings in
		 *  a place according to the arc weight and transition's firing.
		 *  
		 *  The transition will fire only in the first iteration.
		 */
		 if (this.enabledStatus && (MainClass.getIteration() == 0)) {
			/**
			 *  if invalid new marking, exception is thrown.
			 */
			 place.changeMarkings(this.firingFunction, weight);			
		}
		else {
			throw new UnsupportedOperationException(
					"Transition not enabled, did not fire.");
		}
	}
}
