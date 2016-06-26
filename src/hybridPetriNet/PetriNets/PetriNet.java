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
import java.util.concurrent.atomic.AtomicInteger;

import hybridPetriNet.Evolution;
import hybridPetriNet.Arcs.Arc;
import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.Transition;
import hybridPetriNet.Transitions.ContinuousTimeTransition;
import hybridPetriNet.Transitions.TimeDelayedTransition;
import userInteraction.LogText;

/** 
 * This class implements and defines the behavior of the hybrid Petri net.
 * <p>
 * Multiple Petri nets can be created. This way, it is possible to make
 * object oriented net (see the works of Rainer Drath).
 * <p>
 * The advantage of this is that one net can be connected to a second one
 * by a parent net. The low level net may not be needed to know, only
 * the external places (or maybe transitions, no restrictions to that here).
 */
public class PetriNet {
	
	private String name = "Untitled";
	
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);
			
	private final int index;
    
	private boolean deadlocked = false;
	
	private boolean livelocked = false;
	
	/**
	 * The places are the keys.
	 * <p>
	 * The values are a list of arcs that contains the place
	 */
	private Map <Place, ArrayList<Arc> > arcsMap;
		
    /*
     * Array List because of sorting and shuffling methods
     */
	private ArrayList <Place> placeList = new ArrayList <Place>();
	
	private ArrayList <Transition> transitionList = new ArrayList <Transition>();
	
	private ArrayList <Arc> arcList = new ArrayList <Arc>();
	
	/*
	 * constructors
	 */
	/**
	 *  Create empty net with only a name.
	 *  @param name
	 */
	public PetriNet(String netName) {
		this.name = netName;
		this.index = counter.incrementAndGet();
	}
	
	/**
	 * Create a new Petri net from list of places, transitions and arcs.
	 * With name
	 * @param netName
	 * @param placeList
	 * @param transitionList
	 * @param arcList
	 */
	public PetriNet(String netName, ArrayList<Place> placeList, 
		ArrayList<Transition> transitionList, 
		ArrayList<Arc> arcList) {
		
		this.name = netName;
		this.index = counter.incrementAndGet();
		this.placeList = placeList;
		this.transitionList = transitionList;
		this.arcList = arcList;
	}
	
	/**
	 * Create a new Petri net from list of places, transitions and arcs.
	 * Without name.
	 * @param placeList
	 * @param transitionList
	 * @param arcList
	 */
	public PetriNet(ArrayList<Place> placeList, 
		ArrayList<Transition> transitionList, 
		ArrayList<Arc> arcList) {
		
		this.index = counter.incrementAndGet();
		this.placeList = placeList;
		this.transitionList = transitionList;
		this.arcList = arcList;
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
	public ArrayList <Transition> getTransitions() {
		return this.transitionList;
	}
	
	/** 
	 * @return array list of net arcs
	 */
	public ArrayList <Arc> getArcs() { return this.arcList; }
	
	public boolean isDeadlocked(){return this.deadlocked;}
	
	public boolean isLivelocked(){return this.livelocked;}
	
	/*
	 * General and behavior methods
	 */		
	/**
	 * Disable conflicting transitions.
	 * @param place
	 * @param listedArcs
	 * @return 
	 */
	private void disableConflictingTransitions(Place place,
										ArrayList <Arc> listedArcs){
		
		double markingsAfterFiring = place.getMarkings();
		
		for (Arc arc : listedArcs){

			Transition transition = arc.getTransition();
			
			// if enabled, verify
			if (transition.getEnabledStatus()){
				markingsAfterFiring += 
						( transition.getFiringFunction() * arc.getWeight() );			
			}
					
			// if not valid, disable
			if (! place.checkValidMarkings(markingsAfterFiring)){
				
				transition.setEnabledStatus(false);
				
				// undo the firing
				markingsAfterFiring -= 
						( transition.getFiringFunction() * arc.getWeight() );
			}
		}
	}
	
	/**
	 * Loop through all places and check if, after a firing, the markings in
	 * the place goes invalid. If yes, disable transition.
	 */
	private void identifyAndSolveConflicts(){
		
		for (Place place : this.placeList){
			
			ArrayList <Arc> negativeWeightArcs =
												new ArrayList <Arc>();
			
			ArrayList <Arc> positiveWeightArcs =
												new ArrayList <Arc>();
					
			// make two lists of arcs, ignore zero weight arcs
			for (Arc arc : arcsMap.get(place)){
				if (arc.getWeight() < 0){
					negativeWeightArcs.add(arc);
				}
				else if (arc.getWeight() > 0){
					positiveWeightArcs.add(arc);
				}
			}			
			Collections.shuffle(negativeWeightArcs);
			Collections.sort(negativeWeightArcs);
			
			this.disableConflictingTransitions(place, negativeWeightArcs);
			
			
			Collections.shuffle(positiveWeightArcs);
			Collections.sort(positiveWeightArcs);
						
			this.disableConflictingTransitions(place, positiveWeightArcs);
		}
	}
	
	/**
	 *  Set enabling of every transition in list. Will set to false if disabling
	 *  condition is met; no change otherwise.
	 *  <p>
	 *  This way there is no risk of an arc enabling a transition that
	 *  was previously disabled by another arc.
	 */
	private void testDisablings() {
			
		for (Arc arcInList : this.arcList) {		
			arcInList.setTransitionStatus();
		}			
		this.identifyAndSolveConflicts();
	}
	
	/**
	 * Populate the arcsMap with arcs (values) that contains a place (key).
	 */
	private void mapArcs() {
		
		arcsMap = new HashMap < Place, ArrayList<Arc> >();
				
		// a key in the arcsMap
		Place key;
		
		/*
		 *  This loop set the keys (place) to the map, each with
		 *  empty corresponding values (no arcs yet).
		 */
		for (Place placeInList : this.placeList) {			
			key = placeInList;
			
			arcsMap.put(key, new ArrayList <Arc>());	
		}
						
		/*
		 *  Add values to the map;
		 *  
		 *  Loop through all arcs, get its transition (key) and add the arc to
		 *  the map.
		 */		
		for (Arc arcInList : this.arcList) {			
			
			key = arcInList.getPlace();

			arcsMap.get(key).add(arcInList);
		}		
	}
	
	/**
	 * Fire transitions (by priority), testing for disabling in each new
	 * transition.
	 * <p>
	 * Conflicting situations should be solved naturally by doing it that way.
	 */
	private void fireNet(){
		for (Arc arc : this.arcList){
									
			// if enabled, fire
			if (arc.getTransition().getEnabledStatus()){
				/*
				 *  The arcs should be ordered by the weight sign (positive
				 *  first). This way, the arcs that remove markings will
				 *  order the firing first.
				 *  
				 *  This must be done because otherwise, a transition was not
				 *  disabled when it should in case of a conflict.
				 */
				arc.fireTransition();
			}
		}
	}
	
	/**
	 * Print transitions that are firing.
	 */
	private void generateLog() {
		
		for (Transition transition : this.transitionList){
			if (transition.getEnabledStatus()){
				
				LogText.appendMessage( transition.getName() + " fired at iteration "
						+ String.valueOf(Evolution.getIteration())
						+ ", at time " + String.valueOf(Evolution.getTime()) );
			}
		}
	}
	
	/**
	 * Tests for a deadlock. If all transitions are disabled
	 * (enabledStatus = false), then flag a deadlock.
	 * <p>
	 * OR if the only enabled transitions are waiting for time to pass
	 * because of a delay (or the firing function is a function of time,
	 * i.e., continuous transitions); also flag a deadlock.
	 */	
	public void testDeadlock() {	
		
		boolean deadlock = true;
				
		for (Transition transitionInList : this.transitionList) {			
						
			boolean enabled = transitionInList.getEnabledStatus();
			
			boolean continuous = transitionInList instanceof ContinuousTimeTransition;
			
			boolean timeDelayed = transitionInList instanceof TimeDelayedTransition;
			
			boolean notNullFiring = (transitionInList.getFiringFunction() != 0.0);
			
			boolean notNullDelayed = (timeDelayed && notNullFiring);
			
			if ( enabled || continuous || notNullDelayed ){
				// a transition is enabled or waiting time to pass
				deadlock = false;
				break;
			}			
		}		
		this.deadlocked = deadlock;
	}
	
	public void testLivelock(){
		if (Evolution.getIteration() == Evolution.getMaxIterations()) {
						
			this.livelocked = true;
		}
	}
	
	/**
	 *  Set all transitions to enabled(true).
	 *  <p>
	 *  This is done at the beginning of each new iteration.
	 */
	private void enableAllTransitions() {
		
		for (Transition transition : this.transitionList) {
			
			transition.setEnabledStatus(true);
		}
	}
	
	/** 
	 * The iterate method does one iteration over the net:
	 *   <p>- enable all transitions;
	 *   <p>- map arcs;
	 *   <p>- test all disabling while solving conflicts;
	 *   <p>- fire enabled transitions;
	 */	
	public void iterateNet() {
		
		this.enableAllTransitions();
				
		if (this.arcsMap == null){
			this.mapArcs();
		}		
		
		// also tests for conflicts
		this.testDisablings();
		
		this.generateLog();
		
		this.fireNet();
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