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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import hybridPetriNet.Arcs.Arc;
import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.ContinuousTransition;
import hybridPetriNet.Transitions.TimedTransition;
import hybridPetriNet.Transitions.Transition;

public final class PetriNet extends MainClass implements BehaviorInterface{
	/**
	 * This class may not be extended.
	 * 
	 * This class implements and defines the behavior of the hybrid Petri net.
	 * 
	 * Multiple Petri nets can be created. This way, it is possible to make
	 * object oriented net (see the works of Drath).
	 * 
	 * The advantage of this is that one can be connected to a second one
	 * by a parent net. The low level net may not be needed to know, only
	 * the external places (or maybe transitions, no restrictions to that here).
	 */
	private String name = "PN";
	
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);

    private final int index;
    
    public boolean deadlocked = false;
	
	private ArrayList <Place> placeList;
	
	private ArrayList <Transition> transitionList;
	
	private ArrayList <Arc> arcList;
	
	/**
	 * constructors
	 */
	// create empty net
	public PetriNet(String netName) {
		placeList = new ArrayList <Place>();;
		transitionList = new ArrayList <Transition>();;
		arcList = new ArrayList <Arc>();;
		this.index = counter.incrementAndGet();
	}
	
	/**
	 * Create a new Petri net from list of places, transitions and arcs.
	 * @param netPlaces
	 * @param netTransitions
	 * @param netArcs
	 */
	// without name
	public PetriNet(ArrayList <Place> netPlaces, 
			ArrayList <Transition> netTransitions, ArrayList <Arc> netArcs) {
		placeList = netPlaces;
		transitionList = netTransitions;
		arcList = netArcs;
		this.index = counter.incrementAndGet();
	}
	
	// with name
	public PetriNet(String netName, ArrayList <Place> netPlaces, 
			ArrayList <Transition> netTransitions, ArrayList <Arc> netArcs) {
		placeList = netPlaces;
		transitionList = netTransitions;
		arcList = netArcs;
		this.index = counter.incrementAndGet();
		this.name = netName;
	}
	/**
	 * mutators
	 * 
	 * Add single elements to created net.
	 * @return 
	 */
	public void addPlace(Place onePlace) {
		this.placeList.add(onePlace);
	}
	
	public void addTransition(Transition oneTransition) {
		this.transitionList.add(oneTransition);
	}
	
	public void addArc(Arc oneArc) {
		this.arcList.add(oneArc);
	}
	
	public void changeName(String name) {this.name = name;}
	
	/**
	 * accessors
	 */
	int getIndex() { return this.index; }
	
	String getName() { return this.name; }
	
	ArrayList <Place> getPlaces() { return this.placeList; }
	
	ArrayList <Transition> getTransitions() { return this.transitionList; }
	
	ArrayList <Arc> getArcs() { return this.arcList; }
	
	/**
	 * Behavior methods
	 * Each of the below methods uses the one above.
	 */	
	public Map<Integer, ArrayList<Arc>> mapArcs() {
		/**
		 *  Create a map of arcs (values, as list) connected to a place
		 *  (key: place.index).
		 *  this will be used to solve transition's firing conflicts.
		 */
		Map<Integer, ArrayList<Arc>> arcsByPlace = new HashMap<Integer,
				ArrayList<Arc>>();
		
		for (Place placeInList : this.placeList) {
			/**
			 *  This loop sets the keys (place indexes) to the map, each with
			 *  empty corresponding values (no arcs yet).
			 */
			arcsByPlace.put(placeInList.getIndex(), new ArrayList <Arc>());
		}
		
		for (Arc arcInList : this.arcList) {			
			/**
			 *  Add values to the map;
			 *  
			 *  Group arcs by place using the arcsByPlace map.
			 *  
			 *  The below code gets the key (index of the place in the arc) and
			 *   add the arc to the map
			 */
			arcsByPlace.get(arcInList.getPlace().getIndex()).add(arcInList);
			
			/**
			 * Shuffle list, then sort. Because, in sorting, equal elements
			 * will be in the same order (their position in the array do
			 * not change in the sorting when they are equivalent).
			 * 
			 * Elements with same priority must have random order. 
			 * 
			 * Order list of connected transitions to a place by the transitions'
			 *  priority (descending order).
			 * This ordering is done after every new arc is added to the map.
			 * 
			 * The overridden compareTo() method of the Transition Class assures
			 *  this descending order implementation.
			 */
			Collections.shuffle(arcsByPlace.get(arcInList.getPlace().getIndex() ) );
			Collections.sort(arcsByPlace.get(arcInList.getPlace().getIndex() ) );
		}
		return arcsByPlace;
	}
	
	public void setEnablings() {
		for (Arc arcInList : this.arcList) {
			/**
			 *  set enabling of every transition. Will set to false if disabling
			 *  condition is met; no change otherwise.
			 *  
			 *  This way there is no risk of an arc enabling a transition that
			 *  was previously disabled by another arc.
			 */
			arcInList.setTransitionStatus();
		}			
	}
	
	public void testDeadlock() {
		/**
		 * Tests for a deadlock. If all transitions are disabled
		 * (enabledStatus = false), then flag a deadlock.
		 * 
		 * OR if the only enabled transitions are waiting for time to pass
		 * because of a delay (or the firing function is a function of time,
		 * i.e., continuous transitions); also flag a deadlock.
		 */		
		boolean deadlock = false;
		
		for (Transition transitionInList : this.transitionList) {
			/**
			 * The IF conditions below could have been put in a single
			 * IF statement. This was not done because of readability.
			 */
			
			if (transitionInList.getEnabledStatus()){
				// no transition is enabled
				deadlock = true;
			}
			else if (transitionInList instanceof ContinuousTransition) {
				// waiting for time advancement
				deadlock = true;
			}
			else if ( (transitionInList instanceof TimedTransition) &&
							(transitionInList.getFiringFunction() != 0.0) ){
				// waiting for time advancement, delay not reached
				deadlock = true;
			}
		}		
		this.deadlocked = deadlock;
	}
	
	public void fireNet(Map<Integer, ArrayList<Arc>> arcsByPlace){
		/**
		 * Fire enabled transitions, by place and order of priority.
		 * 
		 * The input arcsByPlace is a map that lists all transitions connected
		 * to a place (identified by its index). The arcs in each list should be 
		 * ordered by the transitions' priority. 
		 */
		for (int placeId : arcsByPlace.keySet() ) {
			/**
			 *  get ordered (by transition priority) list of arcs associated to
			 *   a place
			 */
			ArrayList <Arc> listedArcs = arcsByPlace.get(placeId);
			
			for (Arc placeAssociatedArc : listedArcs) {
				/**
				 * Fire every transition.
				 * 
				 *  if invalid new marking, exception is thrown.
				 */
				try {
					placeAssociatedArc.fireTransition();
					/**
					 *  The called method tests if the transition is enabled 
					 *  before firing, for redundancy.
					 */
				}
				catch (UnsupportedOperationException exception){
					/**
					 *  Solve conflicts through by-passing all next transitions.
					 */
					continue;
				}
			}
		}
	}
	
	public void enableAllTransitions() {
		/**
		 *  set all transitions to enabled(true)
		 *  
		 *  This is done at the beginning of each new iteration.
		 */
		for (Transition transition : this.transitionList) {
			transition.setEnabledStatus(true);
		}		
	}
	
	/** 
	 * The iterate method does one iteration over the net:
	 *   enable all transitions;
	 *   group arcs by place
	 *       order them by the transition's priority in descending order;
	 *   disable transitions (if condition is met);
	 *   fire enabled transitions;
	 *       solve conflicts, should them arise
	 */	
	public void iterateNet() {
		this.enableAllTransitions();
		
		/**
		 * The mapping must be done at every iteration because the priorities 
		 * of the transitions may change.
		 */
		Map<Integer, ArrayList<Arc>> arcsByPlace =  mapArcs(); 
		
		this.setEnablings();
		
		if (! this.deadlocked){
			// net is NOT deadlocked
			// do the firing by priority of the transitions
			this.fireNet(arcsByPlace);
		}					
	}
	
	public void updateElements() {
		// update all elements in the net
		for (Place place :this.placeList){
			place.update();
		}
		for (Transition transition :this.transitionList){
			transition.update();
		}
		for (Arc arc :this.arcList){
			arc.update();
		}
	}
}