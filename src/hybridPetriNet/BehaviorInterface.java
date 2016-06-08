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

public interface BehaviorInterface {
	/**
	 * Methods the Petri net must have to define its behavior.
	 */
	Map<Integer, ArrayList<Arc>> mapArcs();
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each ITERATION.
	 */
	void updateElements();
	
	void setEnablings();
	
	void testDeadlock();
	
	void fireNet(Map<Integer, ArrayList<Arc>> arcsByPlace);
	
	void enableAllTransitions();
	
	/** 
	 * The iterate method does one iteration over the net:
	 *   import net;
	 *   update elements attributes
	 *   groups arcs by place
	 *      order them by the transition's priority in descending order;
	 *   disable transitions;
	 *   fire enabled transitions;
	 *     solve conflicts, should them arise
	 *   enable all transitions;
	 */		
	void iterateNet();
}
