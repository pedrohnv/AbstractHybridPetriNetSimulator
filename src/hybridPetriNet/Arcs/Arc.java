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

import hybridPetriNet.Places.AbstractPlace;
import hybridPetriNet.Transitions.AbstractTransition;

/** 
 * The default arc is a normal one.
 * 
 * Attributes: name, a place, a transition, weight.
 * 
 * It also has a default Disabling Function method. To customize this
 * function, it is necessary to create a custom method, without overwriting the
 * default. The final disabling function will be a boolean OR function of every
 * disabling function an arc has. Override FinalDisablingFunction().
 * 
 * A transition is disabled if any disabling function returns true. 
 *  
 * If the weight is a function, that function must be external and call
 * the changeWeight method iteratively.
 *  
 * For normal arcs, the weight determines the direction (tough the transition's
 * firing speed must be positive in this interpretation).
 * Place -> transition, weight < 0;
 * Transition -> place, weight > 0.
 */
public class Arc extends AbstractArc {
	
    /*
	 * constructors
	 */
	/**
	 * place to transition
	 * 
	 * @param place
	 * @param transition
	 * @param weight = -1
	 */
	public Arc(AbstractPlace place, AbstractTransition transition) {
		super(place, transition);
		this.weight = -1;
	}
	
	/**
	 * transition to place
	 * @param transition
	 * @param place
	 * @param weight = 1
	 */
	public Arc(AbstractTransition transition, AbstractPlace place) {
		super(place, transition);
		this.weight = 1;
	}
	
	public Arc(AbstractPlace place, AbstractTransition transition, double weight) {
		super(place, transition);
		this.weight = weight;
	}

	public Arc(String name, AbstractPlace place, AbstractTransition transition,
			double weight) {
		super(place, transition);		
		this.name = name;
		this.weight = weight;
	}

	/*
	 * class methods
	 */		
	/**
	 * This function should take all disabling functions of the arc,
	 * iterate over them checking if any returns true. If so, the
	 * transition will be considered disabled (its enabled status will
	 * be false).
	 * 
	 * The final value will be achieved using a boolean OR function.
	 */
	public boolean finalDisablingFunction() {		
		boolean disableTransition = false;
		
		if (this.defaultDisablingFunction()){
			disableTransition = true;					
		}
		
		// put here other disabling functions testing
		
		return disableTransition;
	}
		
	/**
	 *  If there is command to disable, enabled status of transition is
	 *  set to false.
	 *  
	 *  In the behavior of the net, all transitions will have their enabled
	 *  status set to TRUE at the end of each iteration (time step). So
	 *  there is no need to do it here.  
	 */
	public void setTransitionStatus() {		
		
		if (this.finalDisablingFunction()) {		
			
			this.transition.setEnabledStatus(false);			
		}			
	}	

	/**
	 *  This method fires the transition. That is, changes the markings in
	 *  the place in this arc, according to its weight and transition's
	 *  firing function.
	 *  
	 *  if is a timed transition and is not the first iteration, stop
	 *  execution.
	 *  
	 *  if the transition is enabled, fire; else, throw exception.
	 */	
	public void fireTransition() {
		
		if (this.transition.getEnabledStatus()){
		
			this.place.changeMarkings(this.transition.getFiringFunction(),
				this.weight);				
		}
	}
	
	/*
	 * mutators
	 */
	public void changeName(String newName) {this.name = newName;}
		
	public void changeWeight(double newWeight) {this.weight = newWeight;}
	
	public void changePlace(AbstractPlace newPlace) {this.place = newPlace;}
	
	public void changeTransition(AbstractTransition newTransition) {
		this.transition = newTransition;
	}
	
	/**
	 * the update method is used to create a function that changes the
	 * properties of the elements at each TIME ADVANCEMENT.
	 */
	public void update(){}
}
