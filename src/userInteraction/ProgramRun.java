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
package userInteraction;

import hybridPetriNet.*;

public class ProgramRun extends MainClass {
	/**
	 * Run the program. The user should define what he wants:
	 * see the markings at every iteration and time step, or at
	 * a specific time and iteration.
	 * 
	 * Restrict this to some places, etc.
	 * 
	 * The MainClass that is extended has the simulations constants and
	 * associated methods: time, iteration, time step, max iterations,
	 * final time.
	 * 
	 * In the example below, three separated nets are created:
	 * silo, doser, and lance. They are isolated from each other.
	 * These are the "lower" nets.
	 * 
	 * The fourth net, injectionSystem, the "upper" net, makes connections
	 * between the lower nets with some additional places and transitions.
	 * This makes the Object Orientation Paradigm applied to Petri nets.
	 * 
	 * It was chosen to watch specific places: "injection pressure" and 
	 * "injection flow". Their markings are plotted.
	 */
	
			
	// Create empty Petri nets
	static PetriNet silo = new PetriNet("Silo");
	static PetriNet doser = new PetriNet("Doser");
	static PetriNet lance = new PetriNet("Lance");
	static PetriNet injectionSystem = new PetriNet("Injection System");
			
	// main program run
	public static void main(String[] args){
		
		// add elements to each net
		
		
		
		// will run until the final time is reached
		while(MainClass.getTime() <= MainClass.getFinalTime()) {
			
			/**
			 * Iterate every net. If the max iterations is reached, throw
			 * exception: livelock.
			 * 
			 * set the iteration to zero and loop.
			 */
			for (MainClass.changeIteration(0);
					MainClass.getIteration() <= MainClass.getMaxIterations();){
				
				if (MainClass.getIteration() == MainClass.getMaxIterations()){
					throw new UnsupportedOperationException(
							"Max iterations reached, net is livelocked.");
				}
							
				silo.iterateNet();
				doser.iterateNet();
				lance.iterateNet();
				injectionSystem.iterateNet();
								
				// if all nets are deadlocked, break
				if (silo.deadlocked && doser.deadlocked && 
						lance.deadlocked && injectionSystem.deadlocked){
					// TODO answer: implement this IF in the iterateNet method?
					break;
				}
				
				MainClass.updateIteration();
			}
			MainClass.updateTime();
			
			silo.updateElements();
			doser.updateElements();
			lance.updateElements();
			injectionSystem.updateElements();
			
		}
	}
	
}
