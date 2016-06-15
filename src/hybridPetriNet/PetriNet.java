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

import hybridPetriNet.Arcs.AbstractArc;
import hybridPetriNet.Places.AbstractPlace;
import hybridPetriNet.Transitions.AbstractTransition;
import hybridPetriNet.Transitions.ContinuousTimeTransition;
import hybridPetriNet.Transitions.TimeDelayedTransition;

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
		
	/*
	 * constructors
	 */
	/**
	 *  Create empty net with only a name.
	 */
	public PetriNet(String netName) {
		super(netName);
	}
	
	/**
	 * Create a new Petri net from list of places, transitions and arcs.
	 * With name
	 * @param netName
	 * @param placeList
	 * @param transitionList
	 * @param arcList
	 */
	public PetriNet(String netName, ArrayList<AbstractPlace> placeList, 
		ArrayList<AbstractTransition> transitionList, 
		ArrayList<AbstractArc> arcList) {
		super(netName, placeList, transitionList, arcList);
	}
	
	/*
	 * mutators
	 */
	 /** 
	 * Add a single place to created net.
	 */
	public void addPlace(AbstractPlace onePlace) {
		this.placeList.add(onePlace);
	}
	
	 /** 
	 * Add a single transition to created net.
	 */
	public void addTransition(AbstractTransition oneTransition) {
		this.transitionList.add(oneTransition);
	}
	
	/** 
	 * Add a single arc to created net.
	 */
	public void addArc(AbstractArc oneArc) {
		this.arcList.add(oneArc);
	}
	
	/**
	 * Change the name of the net.
	 * @param name
	 */
	public void changeName(String name) {this.name = name;}
	
	/*
	 * Behavior methods
	 * Each of the below methods uses the one above.
	 */	
	/**
	 *  Create a map of arcs (values, as list) connected to a transition
	 *  (key: transition).
	 *  
	 *  This will be used to solve transition's firing conflicts.
	 */
	public Map < AbstractTransition, ArrayList<AbstractArc> > mapArcs() {
		
		Map < AbstractTransition, ArrayList<AbstractArc> > orderedTransitions = 
				new HashMap < AbstractTransition, ArrayList<AbstractArc> >();
		
		// a key in the map
		AbstractTransition key;
		
		/*
		 *  This loop sets the keys (transition indexes) to the map, each with
		 *  empty corresponding values (no places yet).
		 */
		for (AbstractTransition transitionInList : this.transitionList) {			
			key = transitionInList;
			
			orderedTransitions.put(key, new ArrayList <AbstractArc>());
		}
						
		/*
		 *  Add values to the map;
		 *  
		 *  Loop through all arcs. Get the transition index (a key) and add
		 *  the place to the map.
		 */		
		for (AbstractArc arcInList : this.arcList) {			
			
			key = arcInList.getTransition();
			
			orderedTransitions.get(key).add(arcInList);
			
			/*
			 * Shuffle list, then sort. Because, in sorting, equal elements
			 * will be in the same order (their position in the array do
			 * not change in the sorting when they are equivalent).
			 * 
			 * Elements with same priority must have random order. 
			 * 
			 * Order list of connected transitions to a place by the 
			 * transitions' priority (descending order).
			 * This ordering is done after every new transition is added to
			 * the map.
			 * 
			 * The overridden compareTo() method of the Transition Class
			 * assures this descending order implementation.
			 */						
			Collections.shuffle( orderedTransitions.get(key) );
			Collections.sort( orderedTransitions.get(key) );
		}
		return orderedTransitions;
	}
		
	/**
	 *  set enabling of every transition in list. Will set to false if disabling
	 *  condition is met; no change otherwise.
	 *  
	 *  This way there is no risk of an arc enabling a transition that
	 *  was previously disabled by another arc.
	 */
	public void setEnablings(ArrayList <AbstractArc> listedArcs) {
		for (AbstractArc arcInList : listedArcs) {			
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
		
		for (AbstractTransition transitionInList : this.transitionList) {			
			
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
	 * Fire enabled transitions, by order of priority.
	 * 
	 * The input orderedTransitions is a map that lists all arcs connected
	 * to a transition (identified by its index).
	 */
	public void fireNet(Map <AbstractTransition, ArrayList<AbstractArc>>
							arcsByTransition){
		
		// get ordered (by transition priority) list of arcs.
		for (AbstractTransition oneTransition : arcsByTransition.keySet() ) {	
			
			ArrayList<AbstractArc> listedArcs = arcsByTransition.get(oneTransition);
			
			/*
			 * Fire every transition.
			 * 
			 *  if invalid new marking, exception is thrown.
			 */
			for (AbstractArc transitionAssociatedArc : listedArcs) {
				
				transitionAssociatedArc.setTransitionStatus();
				
				try {
					/*
					 *  The called method tests if the transition is enabled 
					 *  before firing, for redundancy.
					 */
					transitionAssociatedArc.fireTransition();					
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
		for (AbstractTransition transition : this.transitionList) {
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
		Map < AbstractTransition, ArrayList<AbstractArc> >
								arcsByTransition =  mapArcs(); 
		
		// check the disabling of every transition
		this.setEnablings(this.arcList);
		
		this.testDeadlock();
		
		if (! this.deadlocked){
			// net is NOT deadlocked
			// do the firing by priority of the transitions
			this.fireNet(arcsByTransition);
		}
		else {
			System.out.println("deadlocked");
		}
	}
	
	public void updateElements() {
		// update all elements in the net
		for (AbstractPlace place : this.placeList){
			place.update();
		}
		for (AbstractTransition transition : this.transitionList){
			transition.update();
		}
		for (AbstractArc arc : this.arcList){
			arc.update();
		}
	}
}