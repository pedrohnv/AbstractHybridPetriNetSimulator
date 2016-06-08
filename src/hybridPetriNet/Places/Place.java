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

import hybridPetriNet.MainClass;

public class Place extends MainClass implements PlaceInterface {
	/** The default place is a continuous place.
	 * 
	 * Attributes: name, markings, capacity.
	 * 
	 * The markings are doubles.
	 * 
	 * The capacity attribute sets both the lower and upper bound
	 * to the capacity, i.e., the minimum and maximum values the
	 * markings may have (included).
	 * capacity = [lower, upper]. Defaults are zero and infinity.
	 * markings may assume the lower and upper values.
	 */
	
	protected String  name;
	protected double markings = 0;
	protected double inf = Double.POSITIVE_INFINITY;
	protected double[] capacity = {0.0, inf};
		
	// atomic integer because of multithreading
	private static AtomicInteger counter = new AtomicInteger(0);
		
	private final int index;
	
    /** 
     * constructors
     */   
    public Place(String name){
		this.name = name;
		this.index = counter.incrementAndGet();
	}
	
	public Place(String name, double markings){
		this.name = name;
		this.markings = markings;
		this.index = counter.incrementAndGet();
	}
	
	public Place(String name, double markings, double[] capacity){
		this.name = name;
		this.markings = markings;
		this.capacity = capacity;
		this.index = counter.incrementAndGet();
	}
	
	/**
	 * accessors
	 */
	public String getName() {return this.name;}
	
	public double getMarkings() {return this.markings;}
	
	public double[] getCapacity() {return this.capacity;}
	
	public int getIndex() {return this.index;}
			
	/**
	 * class general methods
	 */	
	
	public double newMarkingsValue(double firingFunction, double weight){
		/**
		 *  this method returns the new values the markings of a place will
		 *  have after the firing of a transition.
		 *  
		 *  This method does not change the markings of a place. It is 
		 *  used for testing
		 */
		
		double newMarkingsValue = markings + weight*firingFunction;
		return newMarkingsValue;
	}
	
	public boolean checkValidMarkings(double newValue){
		/**
		 *  this method checks if the new markings will be in the 
		 *  capacity of the place.
		 */
		boolean valid = true;
		
		if ( (newValue < capacity[0]) || (newValue > capacity[1]) ) {
			valid = false;
		}
		
		return valid;
	}
	
	@Override
	public boolean equals(Place other) {
		/**
		 *  this method is to override the equals method of an object.
		 *   It identifies each place by its index.
		 */
		boolean equality = false;
		
	    if (this.index == other.index) equality = true;
	    
	    return equality;
	}
	
	
	/**
	 * mutators
	 */
	public void changeMarkings(double firingFunction, double weight){
		/** 
		 * This method changes the markings of a place. It is a mutator.
		 * 
		 * It tests if the new value is valid. If not, throws an exception.
		 */
				
		double newValue = this.newMarkingsValue(firingFunction, weight);
						
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
	 * capacity must be an array of doubles with two elements.
	 * @param newCapacity = [min, max]
	 * 
	 * If capacity is a function, it is recommended to implement the logic in
	 * the main program run.
	 */
	public void changeCapacity(double[] newCapacity) {
		if ((newCapacity.length == 2) && (newCapacity[1] > newCapacity[0]) ){
			this.capacity = newCapacity;
			}
		
		// throw exception if not of length two, or min > max.
		else {throw new UnsupportedOperationException(
				"Invalid capacity length, did not change.");}
	}
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update() {}
}