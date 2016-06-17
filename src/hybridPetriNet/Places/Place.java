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

/**
 * The default place is a discrete place.
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
public class Place extends AbstractPlace {
		
	/* 
     * constructors
     */   
	/**
	 * 
	 * @param name
	 * @param markings = 0
	 * @param capacity = [0, +inf]
	 */
    public Place(String name){
		super(name);
		this.markings = 0;
		this.capacity = new double[] {0.0, Double.POSITIVE_INFINITY};
	}
	
    /**
     * 
     * @param name
     * @param markings
     * @param capacity = [0, +inf]
     */
	public Place(String name, int markings){
		super(name);
		this.markings = markings;
		this.capacity = new double[] {0.0, Double.POSITIVE_INFINITY};
	}
	
	/**
	 * 
	 * @param name
	 * @param markings
	 * @param capacity
	 */
	public Place(String name, int markings, double[] capacity){
		super(name);
		this.markings = markings;
		this.changeCapacity(capacity); // call mutator
	}
	
	/*
	 * class general methods
	 */	
	/**
	 *  This method checks if the new markings will be in the capacity 
	 *  of the place.
	 *  
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
	
	public void changeName(String newName) {this.name = newName;}
			
	/**
	 * capacity must be an array of doubles with two elements.
	 * @param newCapacity = [min, max]
	 * 
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
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update() {}
	
}