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
package hybridPetriNet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hybridPetriNet.PetriNets.PetriNet;
import hybridPetriNet.Places.Place;
import hybridPetriNet.Arcs.Arc;
import hybridPetriNet.Transitions.Transition;
import userInteraction.LogText;

/**
 * Simulate the Petri net.
 */
public abstract class SimulationRun {
		
	/**
	 * This attribute stores the simulation result as a string.
	 * <p>
	 * It is saved in a CSV file when the simulation ends.
	 */
	private static String stringResults;
	
	private static String ResultsFileName;
	
	/**
	 * Define the name of the file (csv) that is created with the results saved.
	 * @param name
	 */
	public static void setResultsFileName(String name) {
		SimulationRun.ResultsFileName = name;
	}
	
	/**
	 * This method takes any number of input nets and makes one
	 * single net. This way the firings and priorities are guaranteed
	 * to synchronize.
	 * <p>
	 * Get the lists of Arcs, Places and Transitions of each net
	 * and build a unified list of each. To remove duplicates:
	 * <p>
	 * Build a set (this object does not accept duplicates) of each.
	 * 
	 * Clear lists, rebuild from the set with unique elements.
	 *
     * Array List is used because of sorting and shuffling methods
     *
	 */
	public static PetriNet buildTotalNet(PetriNet ... nets){
		
		ArrayList <Place> placeList = new ArrayList <Place>();		
		ArrayList <Transition> transitionList = new ArrayList <Transition>();		
		ArrayList <Arc> arcList = new ArrayList <Arc>();
		
		String names = "untitled"; 
		
		/*
		 *  Possible problems with multithreading because arrayList
		 *  is not thread-synchronized.
		 *  TODO: assure synchronization
		 */
		for (PetriNet oneNet : nets){
			
			placeList.addAll(oneNet.getPlaces());			
			transitionList.addAll(oneNet.getTransitions());			
			arcList.addAll(oneNet.getArcs());
		}
		// sets are used because they do not accept duplicates.
		Set <Place> placeSet = new HashSet<Place>();
		Set <Transition> transitionSet = new HashSet<Transition>();
		Set <Arc> arcSet = new HashSet<Arc>();
		
		placeSet.addAll(placeList);
		transitionSet.addAll(transitionList);
		arcSet.addAll(arcList);
				
		placeList.clear();			
		transitionList.clear();			
		arcList.clear();
				
		placeList.addAll(placeSet);			
		transitionList.addAll(transitionSet);			
		arcList.addAll(arcSet);
		
		// sort Place list by Place's index, to organize the results
		Collections.sort(placeList);		
		
		return (new PetriNet(names , placeList, transitionList, arcList));
	}
	
	/**
	 * This is used to generate the csv file header.
	 */
	private static String generateHeader(){
		/*  TODO (from Java doc): Instances of StringBuilder are not safe for
		 *  use by multiple threads. If such synchronization is required then
		 *  it is recommended that StringBuffer be used.	
		 */
		 StringBuilder strBuilder = new StringBuilder();
		 		 
		 // header
		 strBuilder.append("Place.name");
		 strBuilder.append(','); // separator character
		 strBuilder.append("Place.index");
		 strBuilder.append(',');
		 strBuilder.append("Time");
		 strBuilder.append(',');
		 strBuilder.append("Iteration");
		 strBuilder.append(',');
		 strBuilder.append("Markings");
		 strBuilder.append('\n'); // new line character
		 		 
		 return strBuilder.toString();		 
	}
		
	/**
	 * Append new results to stringResults of Program Run.
	 * @param Petri net being simulated
	 */
	private static void appendResults(PetriNet net){
		/*  TODO (from Java doc): Instances of StringBuilder are not safe for
		 *  use by multiple threads. If such synchronization is required then
		 *  it is recommended that StringBuffer be used.
		 */
		StringBuilder strBuilder = new StringBuilder(stringResults);
		
		for (Place Place : net.getPlaces()) {
			 		 
			strBuilder.append(Place.getName());
			strBuilder.append(','); // separator character
			 
			strBuilder.append(Place.getIndex());
			strBuilder.append(',');
			 
			strBuilder.append(Evolution.getTime());
			strBuilder.append(',');
			 
			strBuilder.append(Evolution.getIteration());
			strBuilder.append(',');
			 
			strBuilder.append(Place.getMarkings());
			strBuilder.append('\n'); // new line character
		}		 		 
		stringResults = strBuilder.toString();
	}
	
	/**
	 * Creates a table with every marking in every Place, at a given
	 * time and iteration.
	 * @param string to save as csv
	 */
	 private static void generateCsvFile(String csvString) {
		 
		 PrintWriter printWriter = null;
		 
		 try {
			printWriter = new PrintWriter(new File(ResultsFileName + ".csv"));
		 }
		 catch (FileNotFoundException e) {
			e.printStackTrace();
		 }	
		 
		 printWriter.write(csvString);		 
		 printWriter.close();
		 
		 LogText.appendMessage("csv file created: " + ResultsFileName);
	 }
	 
	 /**
	 * Iterate net until max iteration is reached.
	 * <p>
	 * set the iteration to zero and loop.
	 */
	 private static void loopIterate(PetriNet parentNet){
		    
		Evolution.setIteration(0);
			
		while (Evolution.getIteration() <= Evolution.getMaxIterations()){				
					
			parentNet.testLivelock();
			
			// if livelocked, break the loop, stop simulation
			if (parentNet.isLivelocked()){
				LogText.appendMessage("livelocked");
				break;
			}
			
			parentNet.testDeadlock();
						
			// if deadlocked, break the loop, stop simulation
			if (parentNet.isDeadlocked()){
				LogText.appendMessage("deadlocked");
				break;
			}
						
			parentNet.iterateNet();		
			
			Evolution.updateIteration();
							
			/*
			 *  Append the results from the simulation of the parent net into
			 *  stringResults attribute.
			 */
			appendResults(parentNet);
							
		}
	 }
	 
	
	/**
	 * Simulate all nets. Enter any number of nets as argument.
	 * @param nets
	 * @throws FileNotFoundException 
	 */
	private static void simulateNet(PetriNet parentNet) {
		
		// append initial state
		appendResults(parentNet);
		
		// will run until the final time is reached
		while(Evolution.getTime() <= Evolution.getFinalTime()) {
						
			if (parentNet.isDeadlocked() || parentNet.isLivelocked()){
				// if deadlocked or livelocked, stop simulation
				break;
			}
			
			loopIterate(parentNet);
			
			Evolution.updateTime();
			
			parentNet.updateElements();
		}		
		// save simulation results to csv file
		generateCsvFile(stringResults);		
	}
	
	/**
	 * Run the program.
	 */
	public static void RunProgram(PetriNet ... nets) {			
		
		LogText.appendMessage("simulation starting");
		
		SimulationRun.stringResults = generateHeader();

		PetriNet parentNet = buildTotalNet(nets);

		// call program run
		SimulationRun.simulateNet(parentNet);
		
		LogText.appendMessage("simulation ended");
	}
	
	/**
	 * A map of the places' markings. To be used during runtime,
	 * especially to update objects.
	 * <p>
	 * If some arbitrary constant must be used in the net, create an
	 * isolated place with the markings value you want to be the constant. 
	 * <p>
	 * (String) key = "place + place.index"
	 * <p>
	 * (Double) value = place.markingss.
	 */
	private static Map <String, Double> markingsMap = new HashMap <String, Double>();
	
	/**
	 * Returns the map of markings.
	 */
	public Map <String, Double> getMarkingsMap() {
		return markingsMap;
	}
	
	/**
	 * Populate map (updating if non-empty).
	 */
	public void populateMarkingsMap(PetriNet net){
		
		String key;
		
		/*
		 *  This loop set the keys ("place" + index) to the map, each with
		 *  the corresponding values (place markings).
		 */
		for (Place place : net.getPlaces()) {		
			
			key = ("place" + place.getIndex());
			
			markingsMap.put(key, place.getMarkings());
		}
	}
	
	/**
	 * Get the value of the markings given a key (place + index).
	 * @param key
	 * @return markings
	 */
	public Double getMarkingValue(String key) {
		return markingsMap.get(key);
	}
	
}
