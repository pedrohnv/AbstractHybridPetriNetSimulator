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
import hybridPetriNet.Transitions.ContinuousTimeTransition;
import hybridPetriNet.Transitions.TimeDelayedTransition;
import hybridPetriNet.Transitions.Transition;

/** 
 * This class implements and defines the behavior of the hybrid Petri net.
 * 
 * Multiple Petri nets can be created. This way, it is possible to make
 * object oriented net (see the works of Rainer Drath).
 * 
 * The advantage of this is that one net can be connected to a second one
 * by a parent net. The low level net may not be needed to know, only
 * the external places (or maybe transitions, no restrictions to that here).
 */
public class PetriNet extends AbstractPetriNet{
	
	private String name = "Untitled";
	
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);
			
	private final int index;
    
    private boolean deadlocked = false;
	
    /*
     * Array List because of sorting and shuffling methods
     */
	private ArrayList <Place> placeList;
	
	private ArrayList <Transition> transitionList;
	
	private ArrayList <Arc> arcList;
	
	/*
	 * constructors
	 */
	/**
	 *  Create empty net with only a name.
	 */
	public PetriNet(String netName) {
		placeList = new ArrayList <Place>();
		transitionList = new ArrayList <Transition>();
		arcList = new ArrayList <Arc>();
		this.index = counter.incrementAndGet();
	}
	
	/**
	 * Create a new Petri net from list of places, transitions and arcs.
	 * With name
	 * @param netName
	 * @param netPlaces
	 * @param netTransitions
	 * @param netArcs
	 */
		public PetriNet(String netName, ArrayList <Place> netPlaces, 
				ArrayList <Transition> netTransitions, ArrayList <Arc> netArcs) {
			placeList = netPlaces;
			transitionList = netTransitions;
			arcList = netArcs;
			this.index = counter.incrementAndGet();
			this.name = netName;
		}
	
	/**
	 * Create a new Petri net from list of places, transitions and arcs.
	 * Without name
	 * @param netPlaces
	 * @param netTransitions
	 * @param netArcs
	 */
	public PetriNet(ArrayList <Place> netPlaces, 
			ArrayList <Transition> netTransitions, ArrayList <Arc> netArcs) {
		placeList = netPlaces;
		transitionList = netTransitions;
		arcList = netArcs;
		this.index = counter.incrementAndGet();
	}
	
	/*
	 * mutators
	 */
	 /** 
	 * Add a single place to created net.
	 */
	public void addPlace(Place onePlace) {
		this.placeList.add(onePlace);
	}
	
	 /** 
	 * Add a single transition to created net.
	 */
	public void addTransition(Transition oneTransition) {
		this.transitionList.add(oneTransition);
	}
	
	/** 
	 * Add a single arc to created net.
	 */
	public void addArc(Arc oneArc) {
		this.arcList.add(oneArc);
	}
	
	/**
	 * Change the name of the net.
	 * @param name
	 */
	public void changeName(String name) {this.name = name;}
	
	/*
	 * accessors
	 */
	/** 
	 * @return net index
	 */
	public int getIndex() { return this.index; }
	
	/** 
	 * @return net name
	 */
	public String getName() { return this.name; }
	
	/** 
	 * @return array list of net places
	 */
	public ArrayList <Place> getPlaces() { return this.placeList; }
	
	/** 
	 * @return array list of net transitions
	 */
	public ArrayList <Transition> getTransitions() { return this.transitionList; }
	
	/** 
	 * @return array list of net arcs
	 */
	public ArrayList <Arc> getArcs() { return this.arcList; }
	
	public boolean isDeadlocked(){return this.deadlocked;}
	
	/*
	 * Behavior methods
	 * Each of the below methods uses the one above.
	 */	
	/**
	 *  Create a map of arcs (values, as list) connected to a place
	 *  (key: place.index).
	 *  this will be used to solve transition's firing conflicts.
	 */
	public Map<Integer, ArrayList<Arc>> mapArcs() {
		
		Map<Integer, ArrayList<Arc>> orderedArcsByPlace = new HashMap<Integer,
				ArrayList<Arc>>();
		
		/*
		 *  This loop sets the keys (place indexes) to the map, each with
		 *  empty corresponding values (no arcs yet).
		 */
		for (Place placeInList : this.placeList) {			
			orderedArcsByPlace.put(placeInList.getIndex(), new ArrayList <Arc>());
		}
		
		/*
		 *  Add values to the map;
		 *  
		 *  Group arcs by place using the arcsByPlace map.
		 *  
		 *  The below code gets the key (index of the place in the arc) and
		 *   add the arc to the map
		 */
		for (Arc arcInList : this.arcList) {			
			
			orderedArcsByPlace.get(arcInList.getPlace().getIndex()).add(arcInList);
			
			/*
			 * Shuffle list, then sort. Because, in sorting, equal elements
			 * will be in the same order (their position in the array do
			 * not change in the sorting when they are equivalent).
			 * 
			 * Elements with same priority must have random order. 
			 * 
			 * Order list of connected transitions to a place by the transitions'
			 * priority (descending order).
			 * This ordering is done after every new arc is added to the map.
			 * 
			 * The overridden compareTo() method of the Transition Class assures
			 *  this descending order implementation.
			 */
			Collections.shuffle(orderedArcsByPlace.get(arcInList.getPlace().getIndex() ) );
			Collections.sort(orderedArcsByPlace.get(arcInList.getPlace().getIndex() ) );
		}
		return orderedArcsByPlace;
	}
	
	/**
	 *  set enabling of every transition in list. Will set to false if disabling
	 *  condition is met; no change otherwise.
	 *  
	 *  This way there is no risk of an arc enabling a transition that
	 *  was previously disabled by another arc.
	 */
	public void setEnablings(ArrayList <Arc> listedArcs) {
		for (Arc arcInList : listedArcs) {			
			arcInList.setTransitionStatus();
		}			
	}

	/**
	 * Tests for a deadlock. If all transitions are disabled
	 * (enabledStatus = false), then flag a deadlock.
	 * 
	 * OR if the only enabled transitions are waiting for time to pass
	 * because of a delay (or the firing function is a function of time,
	 * i.e., continuous transitions); also flag a deadlock.
	 */	
	public void testDeadlock() {	
		boolean deadlock = true;
		
		for (Transition transitionInList : this.transitionList) {			
			
			if ( transitionInList.getEnabledStatus() || (
					
					(transitionInList instanceof ContinuousTimeTransition) ||
					
					((transitionInList instanceof TimeDelayedTransition) &&
							(transitionInList.getFiringFunction() != 0.0)) ) ){

				// a transition is enabled
				deadlock = false;				
			}			
		}
		this.deadlocked = deadlock;
	}

	/**
	 * Fire enabled transitions, by place and order of priority.
	 * 
	 * The input arcsByPlace is a map that lists all transitions connected
	 * to a place (identified by its index). The arcs in each list should be 
	 * ordered by the transitions' priority. 
	 */
	public void fireNet(Map<Integer, ArrayList<Arc>> orderedArcsByPlace){
		/*
		 *  get ordered (by transition priority) list of arcs associated to
		 *   a single place
		 */
		for (int placeId : orderedArcsByPlace.keySet() ) {			
			ArrayList <Arc> listedArcs = orderedArcsByPlace.get(placeId);
			
			/*
			 * Fire every transition.
			 * 
			 *  if invalid new marking, exception is thrown.
			 */
			for (Arc placeAssociatedArc : listedArcs) {
				
				placeAssociatedArc.setTransitionStatus();
				
				try {
					/*
					 *  The called method tests if the transition is enabled 
					 *  before firing, for redundancy.
					 */
					placeAssociatedArc.fireTransition();					
				}
				catch (UnsupportedOperationException exception){
					/*
					 *  Solve conflicts through by-passing all next transitions.
					 */
					continue;					
				}
			}
		}
	}

	/**
	 *  set all transitions to enabled(true)
	 *  
	 *  This is done at the beginning of each new iteration.
	 */
	public void enableAllTransitions() {
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
	 *   verify deadlock;
	 *   fire enabled transitions (re-checking the enabling);
	 *    	solve conflicts, should them arise
	 */	
	public void iterateNet() {
		
		this.enableAllTransitions();
		
		/*
		 * The mapping must be done at every iteration because the priorities 
		 * of the transitions may change.
		 */
		Map<Integer, ArrayList<Arc>> arcsByPlace =  mapArcs(); 
		
		// check the disabling of every transition
		this.setEnablings(this.arcList);
		
		this.testDeadlock();
		
		if (! this.deadlocked){
			// net is NOT deadlocked
			// do the firing by priority of the transitions
			this.fireNet(arcsByPlace);
		}
		else {
			System.out.println("deadlocked");
		}
	}
	
	public void updateElements() {
		// update all elements in the net
		for (Place place : this.placeList){
			place.update();
		}
		for (Transition transition : this.transitionList){
			transition.update();
		}
		for (Arc arc : this.arcList){
			arc.update();
		}
	}
}