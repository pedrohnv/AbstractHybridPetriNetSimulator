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
package hybridPetriNet.Arcs;

import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.Transition;

/**
 * This is a test arc.
 * <p>
 * It has the attribute threshold, which is used in the threshold 
 * disabling function.
 * <p>
 * The weight must be zero.
 * <p>
 * If markings < threshold, the transition is disabled.
 * <p>
 * An inhibitor arc's logic can be made by setting the threshold lower than
 * zero; unless it is wanted that the threshold be exactly zero. Then
 * explicitly use an inhibitor arc.
 */
public class TestArc extends Arc {
	
	protected Double testThreshold = 1.0;
	protected String testThresholdString;
	
	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param testThreshold
	 */
	public TestArc(String name, Place place, Transition transition, 
			Double testThreshold) {
		super(name, place, transition, 0.0);
		this.testThreshold = testThreshold;
		this.testThresholdString = testThreshold.toString();
	}
	
	/** 
	 * @param place
	 * @param transition
	 * @param testThreshold
	 */
	public TestArc(Place place, Transition transition, Double testThreshold) {
		super(place, transition, 0.0);
		this.testThreshold = testThreshold;
		this.testThresholdString = testThreshold.toString();
	}
	
	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param testThreshold
	 */
	public TestArc(String name, Place place, Transition transition, 
			String testThreshold) {
		super(name, place, transition, 0.0);
		this.testThresholdString = testThreshold;
	}
	
	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param testThreshold
	 * @param variableName
	 */
	public TestArc(String name, Place place, Transition transition, 
			String testThreshold, String variableName) {
		super(name, place, transition, 0.0);
		this.testThresholdString = testThreshold;
		this.variableName = variableName;
	}
	
	/** 
	 * @param place
	 * @param transition
	 * @param testThreshold
	 */
	public TestArc(Place place, Transition transition, String testThreshold) {
		super(place, transition, 0.0);
		this.testThresholdString = testThreshold;
	}
	
	/**	
	 * @param place
	 * @param transition
	 * @param testThresold = 1
	 */
	public TestArc(Place place, Transition transition) {
		super(place, transition, 0.0);
		this.testThresholdString = this.weight.toString();
	}
	
	/**
	 * @param name
	 * @param place
	 * @param transition
	 * @param testThresold = 1
	 */
	public TestArc(String name, Place place, Transition transition) {
		super(name, place, transition, 0.0);
		this.testThresholdString = this.weight.toString();
	}
	
	/** 
	 * The test arc disabling function: tests if the markings in the
	 * place are SMALLER than a threshold.
	 */
	public boolean thresholdDisablingFunction() {

		boolean disableTransition = false; 
		
		if  (this.place.getMarkings() <= testThreshold) {
			disableTransition = true;
		}
		
		return disableTransition; 
	}
	
	/**
	 * This function should take all disabling functions of the arc,
	 * iterate over them checking if any returns true. If so, the
	 * transition will be considered disabled (its enabled status will
	 * be false).
	 * <p>
	 * The final value will be achieved using a boolean OR function.
	 */
	@Override
	public boolean finalDisablingFunction() {		
		boolean disableTransition = false;
		
		if (this.defaultDisablingFunction()){
			disableTransition = true;
		}
		
		if (this.thresholdDisablingFunction()){
			disableTransition = true;
		}
		
		// Override put here other disabling functions testing
		
		return disableTransition;
	}
	
	/**
	 * The update method is used to create a function that changes the
	 * properties of the elements at each ITERATION advancement.
	 */
	public void iterationUpdate() {
		// evaluate the weight (if it is a function).
		this.testThreshold = evaluator.evaluate(this.testThresholdString);
	}
	
	/**
	 * In a test arc, the get weight returns the test threshold (as if it was
	 * the weight).
	 */
	@Override
	public Double getWeight(){return this.testThreshold;}
	
	/**
	 * In a test arc, the test threshold is treated as the weight.
	 */
	@Override
	public String getWeightString(){return this.testThresholdString;}
	
	/**
	 * In a test arc, the test threshold is treated as the weight.
	 */
	@Override
	public void changeWeight(double w){
		this.testThreshold = w;
		this.testThresholdString = String.valueOf(w);
	}
	
	/**
	 * In a test arc, the test threshold is treated as the weight.
	 */
	@Override
	public void changeWeightString(String w){
		this.testThresholdString = w;
	}
}
