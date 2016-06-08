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

import hybridPetriNet.MainClass;
import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.Transition;

public class Arc extends MainClass implements ArcInterface{
	/** The default arc is a normal one.
	 * 
	 * Attributes: name, a place, a transition, weight.
	 * 
	 * It also has a default Disabling Function method. To customize this
	 * function, it is necessary to create a custom method, without overwriting the
	 * default. The final disabling function will be a boolean OR function of every
	 * disabling function an arc has. Override FinalDisablingFunction().
	 * 
	 * A transition is disabled if any disabling function returns true. 
	 *  
	 * If the weight is a function, that function must be external and call
	 * the changeWeight method iteratively.
	 *  
	 * For normal arcs, the weight determines the direction.
	 * Place -> transition, weight < 0;
	 * Transition -> place, weight > 0.
	 */
	
	protected String name = "A";
	protected double weight = 1;
	protected Place place;
	protected Transition transition;
	
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);

	private int index;
	
    /**
	 * constructors
	 */	
	public Arc(Place place, Transition transition) {
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();
	}
	
	public Arc(Place place, Transition transition, double weight) {
		this.place = place;
		this.transition = transition;
		this.weight = weight;
		this.index = counter.incrementAndGet();
	}
	
	public Arc(String name, Place place, Transition transition) {
		this.name = name;
		this.place = place;
		this.transition = transition;
		this.index = counter.incrementAndGet();
	}
	
	public Arc(String name, Place place, Transition transition,
			double weight) {
		this.name = name;
		this.place = place;
		this.transition = transition;
		this.weight = weight;
		this.index = counter.incrementAndGet();
	}
	
	/**
	 * accessors
	 */
	public String getName() {return this.name;}
	
	public Place getPlace() {return this.place;}
	
	public Transition getTransition() {return this.transition;}
	
	public int getIndex() {return this.index;}
	
	public double getWeight() {return this.weight;}
		
	/**
	 * class methods
	 */
	public final boolean defaultDisablingFunction() {
		/** The default disabling function tests if the markings of the place,
		 * after the firing of the associated transition, will remain inside
		 * the capacity of the place.
		 */
		
		double newValue = this.place.newMarkingsValue(
				this.transition.getFiringFunction(), this.weight);
		
		return this.place.checkValidMarkings(newValue);
	}	
	
	public boolean finalDisablingFunction() {
		/**
		 * this function should take all disabling functions of the arc,
		 * iterate over them checking if any returns true. If so, the
		 * transition will be considered disabled (its enabled status will
		 * be false).
		 * 
		 * The final value will be achieved using a boolean OR function.
		 */
		
		boolean disableTransition = false;
		
		if (this.defaultDisablingFunction()){
			disableTransition = true;
		}
		
		// put here other disabling functions testing
		
		return disableTransition;
	}
	
	public void setTransitionStatus() {
		/**
		 *  In the behavior of the net, all transitions will have their enabled
		 *  status set to TRUE at the end of each iteration (time step). So
		 *  there is no need to do it here.
		 *  
		 *  If there is command to disable, enabled status of transition is
		 *  set to false.
		 */
		if (this.finalDisablingFunction()) {			
			this.transition.setEnabledStatus(false);
		}
		
	}
	
	public void fireTransition() {
		/**
		 *  This method fires the transition. That is, changes the markings in
		 *  the place in this arc, according to its weight and transition's
		 *  firing function.
		 *  
		 *  if is a timed transition and is not the first iteration, stop
		 *  execution.
		 *  
		 *  if the transition is enabled, fire; else, throw exception.
		 */		
		this.transition.fire(this.place, this.weight);		
	}
	
	@Override
	public boolean equals(Arc other) {
		/**
		 *  this method is to override the equals method of an object. It 
		 *  identifies each arc by its index.
		 */
		boolean equality = false;
		
	    if (this.index == other.index) equality = true;
	    
	    return equality;
	}
	
	@Override
	public int compareTo(Arc other) {
		/**
		 * This method is to compare two arcs based on the priority of their
		 * transitions. It is used to solve
		 * conflicts between the firing of the transitions.
		 * 
		 * From Java.docs:
		 * The compareTo method compares the receiving object with the specified
		 * object and returns a negative integer, 0, or a positive integer 
		 * depending on whether the receiving object is less than, equal to, or
		 * greater than the specified object, respectively.
		 * 
		 * THE CALLED METHOD RETURNS THE INVERSE of THE STATED ABOVE 
		 * PURPOSEFULLY! This is because this will be used to sort a list of
		 *  arcs based on their transitions. They should be sorted in DESCENDING
		 *  order, from highest priority to lowest.
		 */
		if (other == null) throw new NullPointerException("other arc is null");
		
		int comparison = this.transition.compareTo(other.transition);
		
		return comparison;
	}
	
	/**
	 * mutators
	 */
	public void changeName(String newName) {this.name = newName;}
		
	/**
	 * If weight is a function, it is recommended to implement the logic in
	 * the main program run.
	 */
	public void changeWeight(double newWeight) {this.weight = newWeight;}
	
	public void changePlace(Place newPlace) {this.place = newPlace;}
	
	public void changeTransition(Transition newTransition) {
		this.transition = newTransition;
	}
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update(){
		
	}
}