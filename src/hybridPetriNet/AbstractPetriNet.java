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
package hybridPetriNet;

import java.util.ArrayList;
import java.util.Map;

import hybridPetriNet.Arcs.Arc;

/**
 * Define methods the Petri net must have to define its behavior.
 */
public abstract class AbstractPetriNet {
	
	/**
	 *  Create a map of arcs (values, as list) connected to a place
	 *  (key: place.index).
	 *  this will be used to solve transition's firing conflicts.
	 */
	abstract Map<Integer, ArrayList<Arc>> mapArcs();
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	abstract void updateElements();
	
	/**
	 *  set enabling of every transition in list. Will set to false if disabling
	 *  condition is met; no change otherwise.
	 *  
	 *  This way there is no risk of an arc enabling a transition that
	 *  was previously disabled by another arc.
	 */
	abstract void setEnablings(ArrayList <Arc> listedArcs);
	
	/**
	 * Tests for a deadlock. If all transitions are disabled
	 * (enabledStatus = false), then flag a deadlock.
	 * 
	 * OR if the only enabled transitions are waiting for time to pass
	 * because of a delay (or the firing function is a function of time,
	 * i.e., continuous transitions); also flag a deadlock.
	 */
	abstract void testDeadlock();
	
	/**
	 * Fire enabled transitions, by place and order of priority.
	 * 
	 * The input arcsByPlace is a map that lists all transitions connected
	 * to a place (identified by its index). The arcs in each list should be 
	 * ordered by the transitions' priority. 
	 */
	abstract void fireNet(Map<Integer, ArrayList<Arc>> arcsByPlace);
	
	/**
	 *  set all transitions to enabled(true)
	 *  
	 *  This is done at the beginning of each new iteration.
	 */
	abstract void enableAllTransitions();
	
	/** 
	 * The iterate method does one iteration over the net:
	 *   enable all transitions;
	 *   group arcs by place
	 *       order them by the transition's priority in descending order;
	 *   disable transitions (if condition is met);
	 *   fire enabled transitions;
	 *       solve conflicts, should them arise
	 */		
	abstract void iterateNet();
}
