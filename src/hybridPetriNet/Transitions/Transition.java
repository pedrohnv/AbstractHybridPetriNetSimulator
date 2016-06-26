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
	protected double firingFunction = 1;
	protected int priority = 1;
	protected boolean enabledStatus = true;
		
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);
	
	/*
	 *  All objects extending the Abstract super class should have a unique
	 *  index.
	 */
	protected final int index;
    /*
	 * constructors
	 */
	/**
	 * @param name
	 * @param priority = 1
	 * @param firingFunction = 1
	 */
	public Transition (String name) {
		this.name = name;
		this.index = counter.incrementAndGet();
		this.priority = 1;
		this.firingFunction = 1;
		this.enabledStatus = true;
	}
	
	/**
	 * @param name
	 * @param priority
	 * @param firingFunction = 1
	 */
	public Transition (String name, int priority) {
		this.name = name;
		this.index = counter.incrementAndGet();
		this.priority = priority;
		this.firingFunction = 1;
		this.enabledStatus = true;
	}
	
	/**
	 * @param name
	 * @param firingFunction
	 * @param priority = 1
	 */
	public Transition (String name, double firingFunction) {
		this.name = name;
		this.index = counter.incrementAndGet();
		this.priority = 1;
		this.firingFunction = firingFunction;
		this.enabledStatus = true;
	}
	
	/**
	 * @param name
	 * @param priority
	 * @param firingFunction
	 */
	public Transition (String name, int priority, double firingFunction) {
		this.name = name;
		this.index = counter.incrementAndGet();
		this.firingFunction = firingFunction;
		this.priority = priority;
		this.enabledStatus = true;
	}
	
	/*
	 * accessors
	 */
	public static AtomicInteger getCounter() {return counter;}	

	public String getName() {return this.name;}
	
	public double getFiringFunction() {return this.firingFunction;}
	
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
	 * If priority is a function, it is recommended to implement the logic in
	 * the main program run.
	 * <p>
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
	 *  This method is to override the equals method of an object. It
	 *  identifies each transition by its index.
	 */
	public boolean equals(Transition other) {
		boolean equality = false;
		
	    if (this.index == other.index) equality = true;
	    
	    return equality;
	}
	
	/**
	 * This method is to compare two transitions based on their priority.
	 * <p>
	 * It is used to solve conflicts between their firing.
	 */
	public int compareTo(Transition other) {
		
		if ((other == null) || (this == null)) throw new NullPointerException(
				"a transition is null");
		/*
		 * when sort is called, higher priority will come first;
		 * as if a transition with higher priority was "lesser".
		 */
		return (other.priority - this.priority);
	}	
		
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update(){}
		
}
