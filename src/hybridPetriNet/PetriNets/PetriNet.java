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

import hybridPetriNet.Evolution;
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
	 *  @param name
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
	 * General and behavior methods
	 */		
	/**
	 * Disable conflicting transitions.
	 * @param place
	 * @param listedArcs
	 */
	private void disableConflictingTransitions(AbstractPlace place,
										ArrayList <AbstractArc> listedArcs){
		
		double markingsAfterFiring = place.getMarkings();
				
		for (AbstractArc arc : listedArcs){
						
			AbstractTransition transition = arc.getTransition();
						
			markingsAfterFiring += ( transition.getFiringFunction() * arc.getWeight() );
					
			// if not valid, disable
			if (! place.checkValidMarkings(markingsAfterFiring)){
				transition.setEnabledStatus(false);
			}
		}
	}
	
	/**
	 * Loop through all places and check if, after a firing, the markings in
	 * the place goes invalid. If yes, disable transition.
	 */
	private void identifyAndSolveConflicts(){
		
		for (AbstractPlace place : this.placeList){
			
			ArrayList <AbstractArc> negativeWeightArcs =
												new ArrayList <AbstractArc>();
			
			ArrayList <AbstractArc> positiveWeightArcs =
												new ArrayList <AbstractArc>();
					
			// make two lists of arcs, ignore zero weight arcs
			for (AbstractArc arc : arcsMap.get(place)){
				if (arc.getWeight() < 0){
					negativeWeightArcs.add(arc);
				}
				else if (arc.getWeight() > 0){
					positiveWeightArcs.add(arc);
				}
			}
			
			/*
			 *  Order arcs by the weight's signal. negative first.
			 *  That is, the arcs that remove markings should fire first.
			 *   
			 *  Shuffle every time because sorting is conservative
			 *  to the position (if two elements are equal).
			 *  
			 *  The sorting is done using the compareTo method.
			 */
			Collections.shuffle(negativeWeightArcs);
			Collections.sort(negativeWeightArcs);
			
			Collections.shuffle(positiveWeightArcs);
			Collections.sort(positiveWeightArcs);
			
			// go through negative weight arcs first
			this.disableConflictingTransitions(place, negativeWeightArcs);
			
			this.disableConflictingTransitions(place, positiveWeightArcs);
		}
	}
	
	/**
	 *  set enabling of every transition in list. Will set to false if disabling
	 *  condition is met; no change otherwise.
	 *  
	 *  This way there is no risk of an arc enabling a transition that
	 *  was previously disabled by another arc.
	 */
	public void testDisablings() {
		
		this.identifyAndSolveConflicts();
		
		for (AbstractArc arcInList : this.arcList) {		
			arcInList.setTransitionStatus();
		}			
	}
	
	/**
	 * Populate the arcsMap with arcs (values) that contains a place (key).
	 */
	public void mapArcs() {
				
		// a key in the arcsMap
		AbstractPlace key;
		
		/*
		 *  This loop set the keys (place) to the map, each with
		 *  empty corresponding values (no arcs yet).
		 */
		for (AbstractPlace placeInList : this.placeList) {			
			key = placeInList;
			
			arcsMap.put(key, new ArrayList <AbstractArc>());	
		}
						
		/*
		 *  Add values to the map;
		 *  
		 *  Loop through all arcs, get its transition (key) and add the arc to
		 *  the map.
		 */		
		for (AbstractArc arcInList : this.arcList) {			
			
			key = arcInList.getPlace();
			
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
		for (AbstractArc arc : this.arcList){
									
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
	 * Print enabled transitions.
	 */
	public void generateLog() {
		
		for (AbstractTransition transition : this.transitionList){
			if (transition.getEnabledStatus()){
				System.out.println( transition.getName() + " fired at "
						+ String.valueOf(Evolution.getIteration())
						+ ", at time " + String.valueOf(Evolution.getTime()) );
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
		// TODO verify if correctly functioning
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
	 *   map arcs;
	 *   test all disablings; (by order of weight, negative first; then
	 *   					 						transition's priority)
	 *   fire enabled transitions;
	 */	
	public void iterateNet() {
		
		this.enableAllTransitions();
				
		this.mapArcs();
		
		this.testDisablings();
		
		this.generateLog();
		
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