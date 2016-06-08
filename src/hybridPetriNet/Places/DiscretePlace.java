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

public class DiscretePlace extends Place {

	/**
	 * Constructors
	 */
	public DiscretePlace(String name) {
		super(name);
	}

	public DiscretePlace(String name, int markings) {
		super(name, markings);
	}

	public DiscretePlace(String name, int markings, double[] capacity) {
		super(name, markings, capacity);
	}
		
	/**
	 * Object methods
	 */
	public boolean checkValidMarkings(double newValue){
		/**
		 *  this method checks if the new markings will be in the capacity 
		 *  of the place.
		 *  
		 *  As this is a discrete place, the new markings must be an integer.
		 */
		boolean valid = true;
		
		if ( (newValue < this.getCapacity()[0]) || 
				(newValue > this.getCapacity()[1]) &&
				( (newValue % 1) != 0) ) {
			/**
			 *  if newValue is integer, (newValue % 1) = 0, i.e., the rest 
			 *  of the integer division will be zero.
			 */
			valid = false;
		}
		return valid;
	}
	
}
