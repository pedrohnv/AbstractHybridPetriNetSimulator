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

/**
 * The default is a default transition.
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
public class Transition extends AbstractTransition {
	    
    /*
	 * constructors
	 */
	public Transition (String name) {
		super(name);
		this.firingFunction = 1;
	}
	
	public Transition (String name, double firingFunction) {
		super(name);
		this.firingFunction = firingFunction;
	}
	
	public Transition (String name, double firingFunction, int priority) {
		super(name);
		this.firingFunction = firingFunction;
		this.priority = priority;
	}
	
	/*
	 * accessors
	 */
	public String getName() {return this.name;}
	
	public double getFiringFunction() {return this.firingFunction;}
	
	public int getPriority() {return this.priority;}
	
	public int getIndex() {return this.index;}
	
	public boolean getEnabledStatus() {return this.enabledStatus;}
		
	/*
	 * mutators
	 */

	/**
	 *  This method is used inside an arc method.
	 */
	public void setEnabledStatus(boolean status) {
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
			
	/*
	 * class general methods
	 */	
	/**
	 *  this method is to override the equals method of an object. It
	 *  identifies each transition by its index.
	 */
	public boolean equals(AbstractTransition other) {
		boolean equality = false;
		
	    if (this.index == other.index) equality = true;
	    
	    return equality;
	}
	
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
	@Override
	public int compareTo(AbstractTransition other) {
		
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
