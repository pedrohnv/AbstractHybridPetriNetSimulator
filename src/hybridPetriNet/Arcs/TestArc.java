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
package hybridPetriNet.Arcs;

import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.Transition;

public class TestArc extends Arc {
	/**
	 * This is an test arc.
	 * 
	 * It has the attribute threshold, which is used in the threshold 
	 * disabling function.
	 * 
	 * If markings < threshold, the transition is disabled.
	 */
	
	protected double testThreshold = 1; 
	
	public TestArc(String name, Place place, Transition transition) {
		super(name, place, transition, 0.0);
	}

	public TestArc(String name, Place place, Transition transition, 
			double testThreshold) {
		super(name, place, transition, 0.0);
		this.testThreshold = testThreshold;
	}
	
	public boolean thresholdDisablingFunction() {
		/** The test arc disabling function: tests if the markings in the
		 *  place are SMALLER than a threshold.
		 */
		boolean disableTransition = false; 
		
		if  (this.place.getMarkings() < testThreshold) {
			disableTransition = true;
		}
		
		return disableTransition; 
	}
	
	public boolean finalDisablingFunction() {
		/**
		 * this function should take all disabling functions of the arc,
		 * iterate over them checking if any returns true. If so, the
		 * transition will be considered disabled (its enabled status will
		 * be false).
		 * 
		 * The final value will be achieved using a boolean OR function.
		 */
		
		boolean disableTransition = false;
		
		if (this.defaultDisablingFunction()){
			disableTransition = true;
		}
		
		if (this.thresholdDisablingFunction()){
			disableTransition = true;
		}
		
		// put here other disabling functions testing
		
		return disableTransition;
	}

}
