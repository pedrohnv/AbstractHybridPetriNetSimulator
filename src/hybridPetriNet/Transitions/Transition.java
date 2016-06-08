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
package hybridPetriNet.Transitions;

import java.util.concurrent.atomic.AtomicInteger;

import hybridPetriNet.MainClass;
import hybridPetriNet.Places.Place;

public class Transition extends MainClass implements TransitionInterface{
	/** The default is a default transition.
	 * 
	 * A transition is disabled if any disabling function of an arc returns
	 * true.
	 * 
	 * The attributes are: name, firing function, priority, enabled status.
	 *  
	 * A situation of conflict arises when the simultaneous firing of two or
	 * more transitions would cause the markings of a place to go out of its
	 * capacity. In that case the transitions will fire one by one, in order,
	 * from the highest priority to the lowest. If multiple transitions have
	 * the same priority, the one that fires is selected randomly.
	 * 
	 * The enabling check of a transition is done in each arc it has connected
	 * to itself.
	 */
	
	protected String name = "T";
	protected double firingFunction = 1;
	protected int priority = 0;
	protected boolean enabledStatus = true;
		
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);

	private final int index;
    
    /**
	 * constructors
	 */
	public Transition (String name) {
		this.name = name;
		this.index = counter.incrementAndGet();
	}
	
	public Transition (String name, double firingFunction) {
		this.name = name;
		this.firingFunction = firingFunction;
		this.index = counter.incrementAndGet();
	}
	
	public Transition (String name, double firingFunction, int priority) {
		this.name = name;
		this.firingFunction = firingFunction;
		this.index = counter.incrementAndGet();
		this.priority = priority;
	}
	
	/**
	 * accessors
	 */
	public String getName() {return this.name;}
	
	public double getFiringFunction() {return this.firingFunction;}
	
	public int getPriority() {return this.priority;}
	
	public int getIndex() {return this.index;}
	
	public boolean getEnabledStatus() {return this.enabledStatus;}
		
	/**
	 * mutators
	 */
	public void setEnabledStatus(boolean status) {
		/**
		 *  This method is used inside an arc method.
		 */
		this.enabledStatus = status;			
		
	}
	
	public void changeName(String newName) {this.name = newName;}
	
	/**
	 * If priority is a function, it is recommended to implement the logic in
	 * the main program run.
	 * 
	 * The same for the firing Function...
	 */
	public void changePriority(int newPriority) {this.priority = newPriority;}
	
	public void changefiringFunction(double newFiringFunction) {
		this.firingFunction = newFiringFunction;
	}
			
	/**
	 * class general methods
	 */	
	public void fire(Place place, double weight){
		/**
		 *  This method fires the transition. That is, changes the markings
		 *  in a place according to the arc weight and transition's firing
		 *  function.
		 */
		 if (this.enabledStatus) {
			/**
			 *  if invalid new marking, exception is thrown.
			 */
			 place.changeMarkings(this.firingFunction, weight);			
		}
		else {
			throw new UnsupportedOperationException(
					"Transition not enabled, did not fire.");
		}
	}
	
	@Override
	public boolean equals(Transition other) {
		/**
		 *  this method is to override the equals method of an object. It
		 *  identifies each transition by its index.
		 */
		boolean equality = false;
		
	    if (this.index == other.index) equality = true;
	    
	    return equality;
	}
	
		
	@Override
	public int compareTo(Transition other) {
		/**
		 * This method is to compare two transitions based on their priority. 
		 * It is used to solve conflicts between their firing.
		 * 
		 * From Java.docs:
		 * The compareTo method compares the receiving object with the
		 * specified object and returns a negative integer, 0, or a positive
		 * integer depending on whether the receiving object is less than,
		 * equal to, or greater than the specified object, respectively.
		 * 
		 * THIS METHOD RETURNS THE INVERSE OF THE STATED ABOVE PURPOSEFULLY!
		 * This is because this will be used to sort a list of transitions.
		 * They should be sorted in DESCENDING order, from highest priority to
		 * lowest.
		 */
		if (other == null) throw new NullPointerException(
				"other transition is null");
		
		int comparison = 0; // they are assume equal by default
		
		// THIS IS INVERTED PURPOSEFULLY!
		if (this.priority < other.priority) {comparison = +1;}
		else if (this.priority > other.priority) {comparison = -1;}
		
		return comparison;
	}
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update(){}
		
}
