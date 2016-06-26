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

/**
 * This is an inhibitor arc.
 * <p>
 * It is very similar to a test arc, except that the testing is done if the
 * markings are greater than a threshold.
 * <p>
 * If markings > threshold, the transition is disabled.
 */
public class InhibitorArc extends TestArc {

	/**
	 * @param place
	 * @param transition
	 * @param testThreshold
	 */
	public InhibitorArc(Place place, Transition transition,
							double testThreshold) {
		super(place, transition, testThreshold);
	}

	/**
	 * @param place
	 * @param transition
	 * @param testThresold = 1
	 */
	public InhibitorArc(Place place, Transition transition) {
		super(place, transition);
	}

	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param testThresold = 1
	 */
	public InhibitorArc(String name, Place place,
			Transition transition) {
		super(name, place, transition);
	}

	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param testThreshold
	 */
	public InhibitorArc(String name, Place place,
			Transition transition, double testThreshold) {
		super(name, place, transition, testThreshold);
	}
	
	/** 
	 * The inhibitor arc disabling function: tests if the markings in the
	 * place are GREATER or equal than a threshold.
	 */
	@Override
	public boolean thresholdDisablingFunction() {

		boolean disableTransition = false; 
		
		if  (this.place.getMarkings() >= testThreshold) {
			disableTransition = true;
		}
		
		return disableTransition; 
	}
		

}
