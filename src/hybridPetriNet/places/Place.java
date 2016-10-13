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
package hybridPetriNet.places;

import java.util.concurrent.atomic.AtomicInteger;

import enums.PlaceType;

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
			
	protected final Integer index;
	
	/**
	 * This field should be unique to each object. Used to be more convenient
	 * to refer to the markings as variables in equations.
	 * <p>
	 * Default is "p + index".
	 */
	private String variableName;
	
	/**
	 * A identifier of the type of place; for file saving/opening.
	 */
	protected PlaceType type = PlaceType.DISCRETE;
	
	/* 
     * constructors
     */
	/**
	 * The input markings is cast to an integer.
	 * @param name
	 * @param markings
	 * @param capacity
	 */
	public Place(String name, int markings, double[] capacity, String variableName){
		this.index = counter.incrementAndGet();
    	this.name = name;
    	this.changeCapacity(capacity);
    	this.changeMarkings(markings);		
		this.changeVariableName(variableName);
	}
	
	/**
	 * @param name
	 * @param markings = 0
	 * @param capacity = [0, +inf]
	 */
	public Place(String name){
		this(name, 0, new double[] {0.0, Double.POSITIVE_INFINITY}, defaultName());
	}

	/**
	 * Generates a default VARIABLE name for the place.
	 * @return p + index
	 */
	private static String defaultName(){
		String variableName = "p" + counter.incrementAndGet();
		// must decrement the counter because it will be further incremented
		// when the index is set.
		counter.decrementAndGet();
		return variableName;
	}

	/*
	 * accessors
	 */
	public String getName() {return this.name;}
	
	public double getMarkings() {return this.markings;}
	
	public double[] getCapacity() {return this.capacity;}
	
	public int getIndex() {return this.index;}
	
	public static AtomicInteger getCounter() {return counter;}
	
	public String getVariableName() {return this.variableName;}
		
	/*
	 * class general methods
	 */	
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
		
		if ( (newValue % 1) == 0 && (newValue >= this.getCapacity()[0]) && 
				(newValue <= this.getCapacity()[1]) ){
			/*
			 *  if newValue is integer, (newValue % 1) = 0, i.e., the rest 
			 *  of the integer division will be zero.
			 */			
			return true;		
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns an index that indicates if given value is below, above, or
	 * inside the capacity of the place, (-1, +1, 0 respectively).
	 */
	public int verifyFitting(double newValue) {
		int pointer;
		
		if (newValue < this.getCapacity()[0]){
			pointer = -1;
		}
		else if (newValue > this.getCapacity()[1]){
			pointer = +1;
		}
		else {
			pointer = 0;
		}
		return pointer;
	}
	
	/*
	 * mutators
	 */
	/** 
	 * This method changes the new markings of a place; i.e., the values
	 * it will have by the end of the iteration. This signature is based
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
	 * This method changes sets the new markings of a place.
	 * <p>
	 * It tests if the new value is valid. If not, throws an exception.
	 */
	public void changeMarkings(double newValue){		
										
		if (checkValidMarkings(newValue)){
			this.markings = (int) newValue;
		}
		else {
			throw new UnsupportedOperationException(
					"Invalid new markings value, did not change.");
		}
	}
			
	public void changePlaceName(String newName) {this.name = newName;}
	
	public void changeVariableName(String newName){
		this.variableName = newName;
	}

	/**
	 * Capacity must be an array of doubles with two elements.
	 * @param newCapacity = [min, max]
	 * <p>
	 * If capacity is a function, it is recommended to implement the logic in
	 * the update method.
	 */
	public void changeCapacity(double[] newCapacity) {
	
		if ((newCapacity.length == 2) && (newCapacity[1] >= newCapacity[0]) ){
			this.capacity = newCapacity;
		}		
		// throw exception if not of length two, or min > max.
		else {throw new UnsupportedOperationException(
				"Invalid capacity, did not change.");}
	}
	

	public void changeCapacity(double minimum, double maximum) {
		double[] newCapacity = {minimum,maximum};
		this.changeCapacity(newCapacity);	
	}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each TIME advancement.
	 */
	public void timeUpdate() {}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each ITERATION advancement.
	 */
	public void iterationUpdate() {}
	
	/**
	 *  This method is to override the equals method of an object.
	 *  It identifies each place by its index.
	 */
	public boolean equals(Object other) {		
		return ((other instanceof Place) &&
				(this.index.equals( ((Place) other).index)) );
	}
	
	@Override
	public int hashCode() {
	    return index == null ? 0 : index;
	}
    	
	/**
	 * To organize results in the simulation
	 * @param other
	 * @return
	 */
	public int compareTo(Place other) {
		return ( this.getIndex() - other.getIndex() );
	}
	
	@Override
	public String toString(){
		String information = type.getLabel() + ";";
		information += name + ";";
		information += index + ";";
		information += variableName + ";";
		information += markings + ";";
		information += capacity[0] + ";" + capacity[1] + ";";
		return information;
	}
	
}