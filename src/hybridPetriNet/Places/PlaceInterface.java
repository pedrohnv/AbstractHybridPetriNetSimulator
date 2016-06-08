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

public interface PlaceInterface {
	/**
	 * Place interface with methods that all Places must implement.
	 */
	
	/**
	 * The changeMarkings should only do the change if the new marking value is valid,
	 * i.e., inside the capacity of the place.
	 * @param firingFunction
	 * @param weight
	 */
	void changeMarkings(double firingFunction, double weight);
	
	/**
	 * Checks if a place is the same to another (usually using an unique index).
	 * @param other
	 * @return boolean
	 */
	boolean equals(Place other);
	
	/**
	 * check if the new values a place might assume is inside the capacity.
	 * @param newValue
	 * @return boolean
	 */
	boolean checkValidMarkings(double newValue);
	
	/**
	 * The index should be unique for each place.
	 * @return int
	 */
	int getIndex();
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each ITERATION.
	 */
	void update();
}
