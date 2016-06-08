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

public interface ArcInterface extends Comparable<Arc>{
	/**
	 * Arc interface with all methods an Arc must implement.
	 */
	
	/**
	 * Every arc must keep the default disabling function of all arcs.
	 * @return
	 */
	boolean defaultDisablingFunction();
	
	/**
	 * The final disabling function check every disabling function
	 * of the arc.
	 * @return
	 */
	boolean finalDisablingFunction();
	
	/**
	 * Changes the enabled status of the transition in the arc.
	 */
	void setTransitionStatus();
	
	/**
	 * Fire the transition, causing a change in the place based on the weight
	 * of the arc and firing function of the transition.
	 * @param timeStep
	 */
	void fireTransition();
	
	/**
	 * Checks if an arc is the same as another, usually by an unique index.	
	 * @param other
	 * @return
	 */
	boolean equals(Arc other);
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each ITERATION.
	 */
	void update();
}
