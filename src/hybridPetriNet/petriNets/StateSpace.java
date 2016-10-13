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
package hybridPetriNet.petriNets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hybridPetriNet.Evolution;
import hybridPetriNet.arcs.Arc;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.ContinuousTimeTransition;
import hybridPetriNet.transitions.Transition;
import utilities.AdaptedEvaluator;

/**
 * A helper class that creates and stores the time state space of the net.
 * <br>
 * It does the firing by Runge Kutta integration (fourth order).
 */
public class StateSpace {
	
	private AdaptedEvaluator evaluator = new AdaptedEvaluator();
	
	private List<Place> placeList;
		
	private Map <Place, List<Arc>> arcsMap;
	
	/*
	 *  key is variable name, value is equation string that defines a 'K'
	 *  each corresponds to an index in the Runge Kutta model
	 */
	private Map <String, String> stateSpaceK1;
	
	private Map <String, String> stateSpaceK2;
	
	private Map <String, String> stateSpaceK3;
	
	private Map <String, String> stateSpaceK4;
	
	private Map <String, String> finalStateSpace; 
	
	/** 
	 * @param placeList
	 * @param transitionList
	 * @param arcList
	 */
	public StateSpace(List<Place> placeList,
								Map <Place, List<Arc> > arcsMap) {
		this.placeList = placeList;
		this.arcsMap = arcsMap;
		int mapSize = placeList.size();
		this.stateSpaceK1 = new HashMap<String, String>(mapSize, (float) 0.9);		
		this.stateSpaceK2 = new HashMap<String, String>(mapSize, (float) 0.9);		
		this.stateSpaceK3 = new HashMap<String, String>(1, 1);		
		this.stateSpaceK4 = new HashMap<String, String>(1, 1);		
		this.finalStateSpace = new HashMap<String, String>(mapSize); 
}
	
	/**
	 * Constructs the equation string that defines the K1.
	 * It assumes the transition status was already set.
	 * @param place
	 * @return changeEquation
	 */
	private String constructFirstKEquation(Place place){
				
		// the equation that defines the total change in the place
		String K1 = "((0";
		
		for (Arc arc : this.arcsMap.get(place)){
			
			Transition transition = arc.getTransition();
			
			if ((transition instanceof ContinuousTimeTransition) &&
											transition.getEnabledStatus()){
				
				K1 = K1.concat("+(");
				
				K1 = K1.concat(transition.getFiringFunctionString());
				
				K1 = K1.concat(")*(");
				
				K1 = K1.concat(arc.getWeightString());
				
				K1 = K1.concat(")");
			}
		}
		K1 = K1.concat(")*");

		String dt = String.valueOf(Evolution.getTimeStep());  
		
		K1 = K1.concat(dt);
		
		K1 = K1.concat(")");
				
		return K1;
	}
	
	/**
	 * Taking a place (variable name) and respective K1, replace each variable
	 * by itself plus it's (K1)/2.
	 * <p>
	 * E.g.: (x) -> (x + (K1_x)/2). 
	 * @param place
	 * @param K1
	 * @return
	 */
	private String constructSecondKEquation(Place place){
		
		// K1 of the place variable
		String K1 = this.stateSpaceK1.get(place.getVariableName());
		
		// K2 will be K1 with each variable replaced by it's respective K1 coef.
		String K2 = K1;
		
		String variableName;
		
		String replaceName;
		
		for (Place placeVariable : this.placeList){
			
			variableName = placeVariable.getVariableName();
			
			// K1 of the corresponding place variable
			K1 = this.stateSpaceK1.get(variableName);
			
			replaceName = "(" + variableName + "+(" + K1 + "/2))";
			
			K2 = K2.replaceAll(variableName, replaceName);
		}
		return K2;
	}
	
	private String constructThirdKEquation(Place place){

		// K1 of the place variable
		String K1 = this.stateSpaceK1.get(place.getVariableName());
		
		String K2;
		
		// K3 will be K1 with each variable replaced by it's respective K2 coef.
		String K3 = K1;
		
		String variableName;
		
		String replaceName;
		
		for (Place placeVariable : this.placeList){
			
			variableName = placeVariable.getVariableName();
			
			// K2 of the corresponding place variable
			K2 = this.stateSpaceK2.get(variableName);
			
			replaceName = "(" + variableName + "+(" + K2 + "/2))";
			
			K3 = K3.replaceAll(variableName, replaceName);
		}
		return K3;
	}
	
	private String constructFourthKEquation(Place place){

		// K1 of the place variable
		String K1 = this.stateSpaceK1.get(place.getVariableName());
		
		String K3;
		
		// K3 will be K1 with each variable replaced by it's respective K2 coef.
		String K4 = K1;
		
		String variableName;
		
		String replaceName;
		
		for (Place placeVariable : this.placeList){
			
			variableName = placeVariable.getVariableName();
			
			// K2 of the corresponding place variable
			K3 = this.stateSpaceK3.get(variableName);
			
			replaceName = "(" + variableName + "+" + K3 + ")";
			
			K4 = K4.replaceAll(variableName, replaceName);
		}		
		return K4;
	}
	
	/**
	 * Creates the final state space model with fourth order model.
	 */
	public void constructModelFourthOrder(){
		
		// K1
		for (Place place : this.placeList){
			
			String K1 = constructFirstKEquation(place);
			
			stateSpaceK1.put(place.getVariableName(), K1);			
		}
		
		// K2
		for (Place place : this.placeList){
			
			String K2 = constructSecondKEquation(place);
			
			stateSpaceK2.put(place.getVariableName(), K2);			
		}		
		
		// K3
		for (Place place : this.placeList){
			
			String K3 = constructThirdKEquation(place);
			
			stateSpaceK3.put(place.getVariableName(), K3);			
		}
		
		// K4
		for (Place place : this.placeList){
			
			String K4 = constructFourthKEquation(place);
			
			stateSpaceK4.put(place.getVariableName(), K4);			
		}
		
		// final
		for (Place place : this.placeList){
			
			String variableName = place.getVariableName();
			
			String K1 = stateSpaceK1.get(variableName);
			
			String K2 = stateSpaceK2.get(variableName);
			
			String K3 = stateSpaceK3.get(variableName);
			
			String K4 = stateSpaceK4.get(variableName);
			
			String equation = variableName +
				"+(" + K1 + "+(" + K2 + "+" + K3 + ")*2 + " + K4 + ")/6";
			
			finalStateSpace.put(variableName, equation);			
		}
		
	}
	
	/**
	 * Creates the final state space model with second order model.
	 */
	public void constructModelSecondOrder(){
		
		// K1
		for (Place place : this.placeList){
			
			String K1 = constructFirstKEquation(place);
			
			stateSpaceK1.put(place.getVariableName(), K1);			
		}
		
		// K2
		for (Place place : this.placeList){
			
			String K2 = constructSecondKEquation(place);
			
			stateSpaceK2.put(place.getVariableName(), K2);			
		}		
				
		// final
		for (Place place : this.placeList){
			
			String variableName = place.getVariableName();
			
			String K2 = stateSpaceK2.get(variableName);
						
			String equation = variableName + "+" + K2;
			
			finalStateSpace.put(variableName, equation);
		}
		
	}
	
	/**
	 * Do the integration, changing the values of the markings in the places.
	 * <p>
	 * rk4 is a boolean parameter that indicates the use of fourth order (true)
	 * or second order (false) Runge-Kutta.
	 * @param rk4 
	 */
	public void integrate(boolean rk4){
		
		if (rk4){
			this.constructModelFourthOrder();
		}
		else{
			this.constructModelSecondOrder();
		}
		String key;
		
		for (Place place : this.placeList) {
			
			key = place.getVariableName();
			
			// the state space equation already includes the current marking value
			Double newMarking = evaluator.evaluate(finalStateSpace.get(key));
			
			place.changeMarkings(newMarking);
		}
		this.nullify();
	}
	
	/**
	 * Nullify all state space maps in hope to save memory.
	 */
	private void nullify(){
		stateSpaceK1 = null;
		stateSpaceK2 = null;
		stateSpaceK3 = null;
		stateSpaceK4 = null;
		finalStateSpace = null;
	}
	
}
