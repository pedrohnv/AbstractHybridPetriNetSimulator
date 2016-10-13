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
package hybridPetriNet.petriNets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import hybridPetriNet.Evolution;
import hybridPetriNet.arcs.Arc;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.ContinuousTimeTransition;
import hybridPetriNet.transitions.TimeDelayedTransition;
import hybridPetriNet.transitions.Transition;
import utilities.AdaptedEvaluator;
import utilities.LogText;

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
	 * Use fourth (true) or second (false) order Runge-Kutta.
	 */
	private Boolean fourthOrderRungeKutta = false;
	
	/**
	 * The places are the keys.
	 * <p>
	 * The values are a list of arcs that contains the place
	 */
	private Map <Place, List<Arc> > arcsMap;
		
    /*
     * Array List because of sorting and shuffling methods
     */
	private List <Place> placeList = new ArrayList <Place>();
	
	private List <Transition> transitionList = new ArrayList <Transition>();
	
	private List <Arc> arcList = new ArrayList <Arc>();
	
	/**
	 * A map of the places' markings. To be used during runtime
	 * to update objects.
	 * <p>
	 * If some arbitrary constant must be used in the net, create an
	 * isolated place with the markings value you want to be the constant. 
	 * <p>
	 * (String) key = place's variable name
	 * <p>
	 * (Double) value = place's markings.
	 */
	private Map <String, Double> markingsMap = new HashMap <String, Double>();
		
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
	public PetriNet(String netName, List<Place> placeList, 
		List<Transition> transitionList, List<Arc> arcList) {
		
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
	public PetriNet(List<Place> placeList, List<Transition> transitionList, 
										List<Arc> arcList) {
		
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
	public List <Place> getPlaces() { return this.placeList; }
	
	/** 
	 * @return array list of net transitions
	 */
	public List <Transition> getTransitions() {
		return this.transitionList;
	}
	
	/** 
	 * @return array list of net arcs
	 */
	public List <Arc> getArcs() { return this.arcList; }
	
	public boolean isDeadlocked(){return this.deadlocked;}
	
	public boolean isLivelocked(){return this.livelocked;}
	
	/*
	 * General and behavior methods
	 */		
	/**
	 * Disable conflicting transitions, while checking the new marking value.
	 * @param place
	 * @param listedArcs
	 * @return markingsAfterFiring
	 */
	private double disableConflictingTransitions(Place place, double initialMarkings,
												ArrayList <Arc> listedArcs){
		
		double markingsAfterFiring = initialMarkings;
		
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
		return markingsAfterFiring;
	}
	
	/**
	 * Returns a list of arcs that contains given place, and have the same
	 * weight signum as the reference's signum.
	 * @param place
	 * @param reference
	 * @return shuffled then sorted list of arcs
	 */
	private ArrayList<Arc> getArcsByWeightSignum(Place place, double reference) {
		
		ArrayList <Arc> arcList = new ArrayList <Arc>();
		
		reference = Math.signum(reference);
		
		for (Arc arc : arcsMap.get(place)){
			
			double weightSignal = Math.signum(arc.getWeight());
			
			if (weightSignal == reference){
				arcList.add(arc);
			}
		}
		
		Collections.shuffle(arcList);
		Collections.sort(arcList);
		
		return arcList;
	}
	
	/**
	 * Loop through all places and check if, after a firing, the markings in
	 * the place goes invalid. If yes, disable transition.
	 */
	private void identifyAndSolveConflicts(){
		
		for (Place place : this.placeList){
			
			// make two lists of arcs, ignore zero weight arcs
			ArrayList <Arc> negativeWeightArcs = getArcsByWeightSignum(place, -1);
			
			ArrayList <Arc> positiveWeightArcs = getArcsByWeightSignum(place, +1);
			
			/*
			 *  TODO as it is, the firing is not considered simultaneous per se.
			 *  As such, the negative weight arcs' transitions fire first,
			 *  then does the positive weight arcs' transitions.
			 */
			
			double markingsAfterFiring = this.disableConflictingTransitions(place,
									place.getMarkings(), negativeWeightArcs);

			this.disableConflictingTransitions(place, markingsAfterFiring,
														positiveWeightArcs);
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
	}
	
	/**
	 * Populate the arcsMap with arcs (values) that contains a place (key).
	 */
	private void mapArcs() {
		
		arcsMap = new HashMap < Place, List<Arc> >();
				
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
		
		if ( (Evolution.getIteration() == 0) && (Evolution.getTime() > 0) ) {
			this.timeIntegrate();
			
			// after integration, disable time transitions			
			for (Transition transition : this.transitionList){
						
				if (transition instanceof ContinuousTimeTransition){
					transition.setEnabledStatus(false);
				}
			}
		}		
		
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
	 * <p>
	 * This functions also declares if waitingTime to pass or not.
	 */	
	public void testDeadlock() {
		
		boolean deadlock = true;
				
		for (Transition transitionInList : this.transitionList) {			
					
			if ( transitionInList.getEnabledStatus() ){
				// a transition is enabled or waiting time to pass
				deadlock = false;
				break;
			}						
		}		
		this.deadlocked = deadlock;
	}
	
	/**
	 * Test if the deadlock is due to transitions waiting for time to pass.
	 * <br>
	 * Should be called if a deadlock is detected.
	 * <br>
	 * If true to timeWaiting, signals false to a deadlock.
	 * <p>
	 * If new kind of transitions, that deadlock on iterations but not on time
	 * (i.e., waits for time to pass before enabling), is added, override this
	 * method to include them.
	 */
	public boolean testWaitingTimePassing() {
		
		boolean waiting = false;
		
		for (Transition transitionInList : this.transitionList) {			
			
			if ( transitionInList instanceof ContinuousTimeTransition ||
					transitionInList instanceof TimeDelayedTransition ){
				
				waiting = true;
				this.deadlocked = false;
				break;
			}						
		}			
		return waiting;		
	}
	
	/**
	 * Sees if the maximum iteration was reached.
	 */
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
		
		this.mapArcs();
		
		this.testDisablings();
		
		this.identifyAndSolveConflicts();
		
		this.generateLog();
		
		this.fireNet();
	}
	
	/**
	 * If time is greater than zero, and iteration = 0, call this method.
	 * <p>
	 * It creates the State Space model of the net and do an integration of one
	 * time step, changing the markings of the places in the process.
	 */
	private void timeIntegrate() {
		
		StateSpace SS = new StateSpace(this.placeList, this.arcsMap);
		
		// will only consider enabled transitions.
		SS.integrate(fourthOrderRungeKutta);
		
		this.timeUpdateElements();
	}
	
	/**
	 * Update all elements at each time.
	 */
	public void timeUpdateElements() {
		
		this.populateMarkingsMap();
		
		// shows the evaluator the map in which it should look for variables
		AdaptedEvaluator.setMap(this.markingsMap);
		
		// update all elements in the net
		for (Place place : this.placeList){
			place.timeUpdate();
		}
		for (Transition transition : this.transitionList){
			transition.timeUpdate();
		}
		for (Arc arc : this.arcList){
			arc.timeUpdate();
		}
	}
	
	/**
	 * Update all elements at each iteration.
	 */
	public void iterationUpdateElements() {
		
		this.populateMarkingsMap();
		
		// shows the evaluator the map in which it should look for variables
		AdaptedEvaluator.setMap(this.markingsMap);
		
		// update all elements in the net
		for (Place place : this.placeList){
			place.iterationUpdate();
		}
		for (Transition transition : this.transitionList){
			transition.iterationUpdate();
		}
		for (Arc arc : this.arcList){
			arc.iterationUpdate();
		}
	}
	
	/**
	 * Populate map (updating if non-empty).
	 * <br>
	 * This gets called in each update (both time and iteration, which is
	 * redundant, but whatever...).
	 */
	private void populateMarkingsMap(){
		
		String key;
		
		/*
		 *  This loop set the keys to the map, each with
		 *  the corresponding values (place markings).
		 */
		for (Place place : this.placeList) {		
			
			key = place.getVariableName();
			
			this.markingsMap.put(key, place.getMarkings());
		}
	}

	public Boolean getFourthOrderRungeKutta() {
		return fourthOrderRungeKutta;
	}

	public void setFourthOrderRungeKutta(Boolean fourthOrderRungeKutta) {
		this.fourthOrderRungeKutta = fourthOrderRungeKutta;
	}
	
}