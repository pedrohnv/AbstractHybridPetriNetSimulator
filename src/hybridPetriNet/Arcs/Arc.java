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
package hybridPetriNet.Arcs;

import java.util.concurrent.atomic.AtomicInteger;

import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.Transition;
import utilities.AdaptedEvaluator;

/** 
 * The default arc is a normal one.
 * <p>
 * Attributes: name, a place, a transition, weight.
 * <p>
 * It also has a default Disabling Function method. To customize this
 * function, it is necessary to create a custom method, without overwriting the
 * default. The final disabling function will be a boolean OR function of every
 * disabling function an arc has. Override FinalDisablingFunction().
 * <p>
 * A transition is disabled if any disabling function returns true. 
 * <p>
 * If the weight is a function, that function must be external and call
 * the changeWeight method iteratively.
 * <p> 
 * For normal arcs, the weight determines the direction (tough the transition's
 * firing speed must be positive in this interpretation).
 * <p>
 * Place -> transition, weight < 0;
 * <p>
 * Transition -> place, weight > 0.
 */
public class Arc implements Comparable <Arc> {
	
	protected String name;
	protected Double weight;
	protected Place place;
	protected Transition transition;

	/**
	 * Weight stored as a String. To be evaluated at each update.
	 */
	protected String weightString;
	
	/**
	 * The expression (string) evaluator
	 */
	protected static AdaptedEvaluator evaluator = new AdaptedEvaluator();
	
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);

	/*
	 *  All objects extending the  super class should have a unique
	 *  index.
	 */
	protected Integer index;
	
    /*
	 * constructors
	 */
	/**
	 * place to transition
	 * 
	 * @param place
	 * @param transition
	 * @param weight = -1
	 */
	public Arc(Place place, Transition transition) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();
		this.weight = -1.0;
		this.weightString = "-1.0";
	}
	
	/**
	 * transition to place
	 * 
	 * @param transition
	 * @param place
	 * @param weight = +1
	 */
	public Arc(Transition transition, Place place) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();
		this.weight = 1.0;
		this.weightString = "1.0";
	}
	
	/**
	 * @param place
	 * @param transition
	 * @param weight
	 */
	public Arc(Place place, Transition transition, Double weight) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();
		this.weight = weight;
		this.weightString = weight.toString();
	}
	
	/**
	 * @param place
	 * @param transition
	 * @param weight
	 */
	public Arc(Place place, Transition transition, String weight) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();
		this.weightString = weight;
	}

	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param weight
	 */
	public Arc(String name, Place place, Transition transition, Double weight) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();	
		this.name = name;
		this.weight = weight;
		this.weightString = weight.toString();
	}
	
	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param weight
	 */
	public Arc(String name, Place place, Transition transition, String weight) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();	
		this.name = name;
		this.weightString = weight;
	}
	
	
	/*
	 * accessors
	 */
	public static AtomicInteger getCounter() {return counter;}
	
	public String getName() {return this.name;}
	
	public Place getPlace() {return this.place;}
	
	public Transition getTransition() {return this.transition;}
	
	public Integer getIndex() {return this.index;}
	
	public Double getWeight() {return this.weight;}
	
	
	/*
	 * mutators
	 */
	public void changeName(String newName) {this.name = newName;}
		
	public void changeWeight(double newWeight) {this.weight = newWeight;}
	
	public void changePlace(Place newPlace) {this.place = newPlace;}
	
	public void changeTransition(Transition newTransition) {
		this.transition = newTransition;
	}
	
	
	/*
	 * General methods
	 */
	/**
	 * Check if arc already exists (an arc that connect the same place and
	 * transition as another).
	 */
	@SuppressWarnings("unused")
	private void checkExistance(){
		//TODO
	}
	
	/** 
	 * The default disabling function tests if the markings of the place,
	 * after the firing of the associated transition, will remain inside
	 * the capacity of the place.
	 */
	protected final boolean defaultDisablingFunction() {		
		// the final declaration prevents overriding
		double newValue = this.place.newMarkingsValue(
				this.transition.getFiringFunction(), this.weight);
				
		boolean disable = !( this.place.checkValidMarkings(newValue) );
		
		return disable;
	}
	
	/**
	 * This function should take all disabling functions of the arc,
	 * iterate over them checking if any returns true. If so, the
	 * transition will be considered disabled (its enabled status will
	 * be false).
	 * <p>
	 * The final value will be achieved using a boolean OR function.
	 */	
	public boolean finalDisablingFunction() {		
		boolean disableTransition = false;
		
		if (this.defaultDisablingFunction()){
			disableTransition = true;					
		}
		
		// Override put here other disabling functions testing
		
		return disableTransition;
	}
	
	/**
	 *  If there is command to disable, enabled status of transition is
	 *  set to false.
	 *  <p>
	 *  In the behavior of the net, all transitions will have their enabled
	 *  status set to TRUE at the beginning of each iteration.
	 */
	public final void setTransitionStatus() {
		
		if (this.finalDisablingFunction()) {
			
			this.transition.setEnabledStatus(false);
		}			
	}
	
	/**
	 * This method compares two arcs based on the signal of their weight,
	 * then by their transition's priority.
	 */
	public int compareTo(Arc other) {
		
		if ((other == null) || (this == null))
			throw new NullPointerException("an arc is null");
				
		return this.transition.compareTo(other.transition);
	}
	
	/**
	 *  This method is to override the equals method of an object. It 
	 *  identifies each arc by its index.
	 */
	public boolean equals(Arc other) {
		
		boolean equality = false;
		
	    if (this.index == other.index) equality = true;
	    
	    return equality;
	}
		
	/**
	 *  This method fires the transition. That is, changes the markings in
	 *  the place in this arc, according to its weight and transition's
	 *  firing function.
	 *  <p>
	 *  if the transition is enabled, fire.
	 */	
	public void fireTransition() {
		
		if (this.transition.getEnabledStatus()){
			
			this.transition.fire(this.place, this.weight);			
		}
	}
		
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each TIME advancement.
	 */
	public void timeUpdate(){
		// evaluate the weight (if it is a function).
		this.weight = evaluator.evaluate(this.weightString);
	}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each ITERATION advancement.
	 */
	public void iterationUpdate() {
		// evaluate the weight (if it is a function).
		this.weight = evaluator.evaluate(this.weightString);
	}
	
}
