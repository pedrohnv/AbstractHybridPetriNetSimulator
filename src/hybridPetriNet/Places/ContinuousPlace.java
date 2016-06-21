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
 * This is a continuous place. Markings may be any real number.
 */
public class ContinuousPlace extends Place {	
	/*
	 * Constructors
	 */
	/**
	 * @param name
	 * @param markings = 0
	 * @param capacity = [0, +inf]
	 */
	public ContinuousPlace(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param markings
	 * @param capacity = [0, +inf]
	 */
	public ContinuousPlace(String name, double markings) {
		super(name);
		this.markings = markings; // did not call super because it enforces int value to markings.
	}

	/**
	 * @param name
	 * @param markings
	 * @param capacity
	 */
	public ContinuousPlace(String name, double markings, double[] capacity) {
		super(name);
		this.markings = markings;
		this.changeCapacity(capacity); // call mutator
	}
		
	/*
	 * Object methods
	 */
	/**
	 *  This method checks if the new markings will be in the 
	 *  capacity of the place.
	 */
	public boolean checkValidMarkings(double newValue){
		
		boolean valid = true;
		
		if ( (newValue < capacity[0]) || (newValue > capacity[1]) ) {
			valid = false;
		}
		
		return valid;
	}
	
}
