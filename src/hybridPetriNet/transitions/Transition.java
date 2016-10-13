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
package hybridPetriNet.transitions;

import java.util.concurrent.atomic.AtomicInteger;

import enums.TransitionType;
import hybridPetriNet.places.Place;
import utilities.AdaptedEvaluator;

/**
 * The default is a default transition.
 * <p>
 * A transition is disabled if any disabling function of an arc returns
 * true.
 * <p>
 * The attributes are: name, firing function, priority, enabled status.
 *  <p>
 * A situation of conflict arises when the simultaneous firing of two or
 * more transitions would cause the markings of a place to go out of its
 * capacity. In that case the transitions will fire one by one, in order,
 * from the highest priority to the lowest. If multiple transitions have
 * the same priority, the one that fires is selected randomly.
 * <p>
 * The enabling check of a transition is done in each arc it has connected
 * to itself.
 */
public class Transition implements Comparable <Transition> {
	
	protected String name;
	protected Double firingFunction = 1.0;
	protected Integer priority = 1;
	protected Boolean enabledStatus = true;
	
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);
	
	protected final Integer index;
	
	/**
	 * A identifier of the type of transition; for file saving/opening.
	 */
	protected TransitionType type = TransitionType.DISCRETE;
    
	/**
	 * This variable stores the firing function as a string, if it is not a
	 * constant.
	 * <p>
	 * It is parsed and evaluated at each update call.
	 */
	protected String firingFunctionString;
	
	/**
	 * The expression (string) evaluator
	 */
	protected static AdaptedEvaluator evaluator = new AdaptedEvaluator();
	
	/*
	 * constructors
	 */
	/**
	 * @param name
	 * @param priority
	 * @param firingFunctionString
	 */
	public Transition(String name, int priority, String firingFunctionString) {
		this.name = name;
		this.index = counter.incrementAndGet();
		this.changePriority(priority);
		this.enabledStatus = true;
		this.firingFunctionString = firingFunctionString;
	}
	
	/**
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 1
	 */
	public Transition (String name) {
		this(name, 1, "1.0");
	}	
		
	/*
	 * accessors
	 */
	public static AtomicInteger getCounter() {return counter;}	

	public String getName() {return this.name;}
	
	public double getFiringFunction() {return this.firingFunction;}
	
	public String getFiringFunctionString() {return this.firingFunctionString;}
	
	public int getPriority() {return this.priority;}
	
	public int getIndex() {return this.index;}
	
	public boolean getEnabledStatus() {return this.enabledStatus;}
		
	
	/*
	 * mutators
	 */
	/**
	 *  Change transition status to:
	 *  @param status
	 */
	public void setEnabledStatus(boolean status) {
		this.enabledStatus = status;		
	}
	
	public void changeName(String newName) {this.name = newName;}
	
	/**
	 * The priority must be greater or equal to zero. If input is lower than
	 * zero, priority is set to zero.
	 * @param priority
	 */
	public void changePriority(int newPriority) {
		if (newPriority >= 0){
			this.priority = newPriority;
		}
		else {
			this.priority = 0;
		}
	}
	
	public void changeFiringFunction(double newFiringFunction) {
		this.firingFunction = newFiringFunction;
	}
	
	public void changeFiringFunctionString(String newFiringFunction) {
		this.firingFunctionString = newFiringFunction;
	}
			
	/*
	 * class general methods
	 */	
	/**
	 *  This method is to override the equals method of an object. It
	 *  identifies each transition by its index.
	 */	
	@Override
	public boolean equals(Object other){
		return ((other instanceof Transition) && 
				(this.index.equals( ((Transition) other).getIndex())) );		
	}
	
	@Override
	public int hashCode() {
	    return index == null ? 0 : index;
	}
	
	/**
	 * This method is to compare two transitions based on their priority.<p>
	 * When sort is called, higher priority will come first;
	 * as if a transition with higher priority was "lesser".<p>
	 * It is used to solve conflicts between their firing.
	 */
	public int compareTo(Transition other) {
		return (other.priority - this.priority);
	}	
		
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each TIME advancement.
	 */
	public void timeUpdate(){
		// evaluate firing function
		this.firingFunction = evaluator.evaluate(this.firingFunctionString);
	}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each ITERATION advancement.
	 */
	public void iterationUpdate(){
		// evaluate firing function
		this.firingFunction = evaluator.evaluate(this.firingFunctionString);
	}

	/**
	 * Change the markings in a place.
	 * @param place
	 * @param weight
	 */
	public void fire(Place place, double weight) {

		place.changeMarkings(this.firingFunction, weight);
	}
	
	@Override
	public String toString(){
		String info = type.getLabel() + ";";
		info += name + ";";
		info += index + ";";
		info += firingFunctionString + ";";
		info += priority + ";";
		return info;
	}
}
