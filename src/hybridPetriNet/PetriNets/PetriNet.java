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
package hybridPetriNet.PetriNets;

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
	
	/**
	 *  Map of transitions (key) to list of places that contains it (values).
	 */
	private Map < AbstractTransition, ArrayList<AbstractArc> > arcsMap = 
				new HashMap < AbstractTransition, ArrayList<AbstractArc> >();
	
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
		
		super(netName);
		this.arcList = arcList;
		this.transitionList = transitionList;
		this.placeList = placeList;
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
	 * General and behavior methods
	 */	

	/**
	 * Calls the fireTransition in all arcs that contain a transition.
	 * @param arcsWithTransition
	 */
	public void fireByArcs(ArrayList <AbstractArc> arcsWithTransition){
		
		for (AbstractArc arc : arcsWithTransition){
			arc.fireTransition();	
		}		
	}
	
	/**
	 * Shuffle then sort list of transitions of the net. Call this before
	 * each firing.
	 */
	public void orderTransitions() {
		/*
		 * Shuffle list, then sort. Because in sorting, equal elements
		 * will be in the same order (their position in the array do
		 * not change in the sorting when they are equivalent).
		 * 
		 * Elements with same priority must have random order. 
		 * 
		 * Order list of transitions by their priority (in descending order).
		 * 
		 * This ordering must be done at each iteration (the priorities may
		 * change), before the firing of the net.
		 * 
		 * The overridden compareTo() method of the Transition Class
		 * assures this descending order implementation.
		 */		
		Collections.shuffle(this.transitionList);
		Collections.sort(this.transitionList);
	}
	
	/**
	 *  set enabling of every transition in list. Will set to false if disabling
	 *  condition is met; no change otherwise.
	 *  
	 *  This way there is no risk of an arc enabling a transition that
	 *  was previously disabled by another arc.
	 */
	public void testDisablings(ArrayList <AbstractArc> listedArcs) {
		for (AbstractArc arcInList : listedArcs) {			
			arcInList.setTransitionStatus();
		}			
	}
	
	/**
	 * Populate the arcsMap with arcs (values) that contains a transition (key).
	 */
	public void mapArcs() {
				
		// a key in the arcsMap
		AbstractTransition key;
		
		/*
		 *  This loop set the keys (transition) to the map, each with
		 *  empty corresponding values (no arcs yet).
		 */
		for (AbstractTransition transitionInList : this.transitionList) {			
			key = transitionInList;
			
			arcsMap.put(key, new ArrayList <AbstractArc>());
		}
						
		/*
		 *  Add values to the map;
		 *  
		 *  Loop through all arcs, get its transition (key) and add the arc to
		 *  the map.
		 */		
		for (AbstractArc arcInList : this.arcList) {			
			
			key = arcInList.getTransition();
			
			arcsMap.get(key).add(arcInList);
		}
	}
	
	/**
	 * Fire transitions (by priority), testing for disabling in each new
	 * transition.
	 * 
	 * Conflicting situations should be solved naturally by doing it that way.
	 */
	public void fireNet(){
		for (AbstractTransition transition : this.transitionList) {
			
			testDisablings(arcsMap.get(transition));
			
			// if enabled, fire
			if (transition.getEnabledStatus()){
				fireByArcs(arcsMap.get(transition));
			}
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
	 *   order transitions;
	 *   map arcs;
	 *   fire net;
	 */	
	public void iterateNet() {
		
		this.enableAllTransitions();
		
		this.orderTransitions();
		
		this.mapArcs();
		
		this.fireNet();
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