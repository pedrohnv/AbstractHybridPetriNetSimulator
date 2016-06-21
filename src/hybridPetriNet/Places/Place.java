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
package hybridPetriNet.Places;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default place is a discrete place.
 * <p>
 * Attributes: name, markings, capacity.
 * <p>
 * The markings are doubles.
 * <p>
 * The capacity attribute sets both the lower and upper bound
 * to the capacity, i.e., the minimum and maximum values the
 * markings may have (included).
 * capacity = [lower, upper]. Defaults are zero and infinity.
 * markings may assume the lower and upper values.
 */
public class Place implements Comparable<Place> {
		
	protected String name;
	protected double markings;
	protected double[] capacity;
	
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
	 * @param markings = 0
	 * @param capacity = [0, +inf]
	 */
    public Place(String name){
    	this.index = counter.incrementAndGet();
    	this.name= name;
		this.markings = 0;
		this.capacity = new double[] {0.0, Double.POSITIVE_INFINITY};
	}
	
    /**
     * @param name
     * @param markings
     * @param capacity = [0, +inf]
     */
	public Place(String name, int markings){
		this.index = counter.incrementAndGet();
    	this.name= name;
		this.markings = markings;
		this.capacity = new double[] {0.0, Double.POSITIVE_INFINITY};
	}
	
	/**
	 * @param name
	 * @param markings
	 * @param capacity
	 */
	public Place(String name, int markings, double[] capacity){
		this.index = counter.incrementAndGet();
    	this.name= name;
		this.markings = markings;
		this.changeCapacity(capacity); // call mutator
	}

	
	/*
	 * accessors
	 */
	public String getName() {return this.name;}
	
	public double getMarkings() {return this.markings;}
	
	public double[] getCapacity() {return this.capacity;}
	
	public int getIndex() {return this.index;}
	
	public static AtomicInteger getCounter() {return counter;}

	
	/*
	 * class general methods
	 */	
	/**
	 *  This method is to override the equals method of an object.
	 *  It identifies each place by its index.
	 */
	public boolean equals(Place other) {		
		
		boolean equality = false;
		
	    if (this.index == other.index){ equality = true;}
	    
	    return equality;
	}
    
    /**
	 *  This method returns the new values the markings of a place will
	 *  have after the firing of a transition.
	 *  <p>
	 *  This method does not change the markings of a place. It is 
	 *  used for testing.
	 *  @return double
	 */
	public double newMarkingsValue(double firingFunction, double weight){		
			
		return (this.markings + weight*firingFunction);
	}
    
	/**
	 *  This method checks if the new markings will be in the capacity 
	 *  of the place.
	 *  <p>
	 *  As this is a discrete place, the new markings must be an integer.
	 */	
	public boolean checkValidMarkings(double newValue){
		boolean valid = false;
		
		if ( (newValue % 1) == 0 &&
		(newValue >= this.getCapacity()[0]) && 
		(newValue <= this.getCapacity()[1]) ){
			/*
			 *  if newValue is integer, (newValue % 1) = 0, i.e., the rest 
			 *  of the integer division will be zero.
			 */			
			valid = true;		
		}
		return valid;
	}
	
	/*
	 * mutators
	 */	
	/** 
	 * This method changes the new markings of a place; i.e., the values
	 * it will have by the end of the simulation. This signature is based
	 * on the firing of a transition.
	 * <p>
	 * It tests if the new value is valid. If not, throws an exception.
	 */
	public void changeMarkings(double firingFunction, double weight){		
				
		double newValue = this.newMarkingsValue(firingFunction, weight);
						
		if (checkValidMarkings(newValue)){
			this.markings = newValue;
		}
		else {
			throw new UnsupportedOperationException(
					"Invalid new markings value, did not change.");
		}
	}
	
	/** 
	 * This method changes the new markings of a place; i.e., the values
	 * it will have by the end of the simulation.
	 * <p>
	 * It tests if the new value is valid. If not, throws an exception.
	 */
	public void changeMarkings(double newValue){		
										
		if (checkValidMarkings(newValue)){
			this.markings = newValue;
		}
		else {
			throw new UnsupportedOperationException(
					"Invalid new markings value, did not change.");
		}
	}
			
	public void changeName(String newName) {this.name = newName;}
			
	/**
	 * Capacity must be an array of doubles with two elements.
	 * @param newCapacity = [min, max]
	 * <p>
	 * If capacity is a function, it is recommended to implement the logic in
	 * the update method.
	 */
	public void changeCapacity(double[] newCapacity) {
	
		if ((newCapacity.length == 2) && (newCapacity[1] > newCapacity[0]) ){
			this.capacity = newCapacity;
			}
		
		// throw exception if not of length two, or min > max.
		else {throw new UnsupportedOperationException(
				"Invalid capacity, did not change.");}
	}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update() {}
	
	/**
	 * To organize results in the simulation
	 * @param other
	 * @return
	 */
	public int compareTo(Place other) {
		
		if ((other == null) || (this == null))
			throw new NullPointerException("a place is null");
		
		return ( this.getIndex() - other.getIndex() );
	}
	
}