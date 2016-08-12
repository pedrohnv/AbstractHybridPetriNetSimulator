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
import java.util.HashSet;
import java.util.Set;

import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.Transition;
import hybridPetriNet.arcs.Arc;
import hybridPetriNet.petriNets.PetriNet;
import utilities.LogText;

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
	
	private static String resultsFileName;
	
	/**
	 * Generate a csv file?
	 */
	private static Boolean generateCsv;
		
	/**
	 * Define the name of the file (csv) that is created with the results saved.
	 * @param name
	 */
	public static void setResultsFileName(String name) {
		SimulationRun.resultsFileName = name;
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
		
		String name = "untitled"; 
		
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
		
		return (new PetriNet(name, placeList, transitionList, arcList));
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
	 * <p>
	 * Only a time multiple of the timeSampling is appended.
	 * @param Petri net being simulated
	 */
	private static void appendResults(PetriNet net){
		/*  TODO (from Java doc): Instances of StringBuilder are not safe for
		 *  use by multiple threads. If such synchronization is required then
		 *  it is recommended that StringBuffer be used.
		 */
		
		if (SimulationRun.generateCsv){
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
	}
	
	/**
	 * Creates a table with every marking in every Place, at a given
	 * time and iteration.
	 * @param string to save as csv
	 */
	 private static void generateCsvFile(String csvString) {
		 
		 if (SimulationRun.generateCsv){
			 PrintWriter printWriter = null;
			 
			 try {
				printWriter = new PrintWriter(new File(resultsFileName + ".csv"));
			 }
			 catch (FileNotFoundException e) {
				e.printStackTrace();
			 }	
			 
			 printWriter.write(csvString);		 
			 printWriter.close();
			 
			 LogText.appendMessage("csv file created: " + resultsFileName);
		 }
	 }
	 	 
	 /**
	 * Iterate net until max iteration is reached.
	 * <p>
	 * set the iteration to zero and loop.
	 */
	 private static void loopIterate(PetriNet parentNet, long pause){
		    
		Evolution.setIteration(0);
			
		while (Evolution.getIteration() <= Evolution.getMaxIterations()){				
			
			parentNet.iterationUpdateElements();
			
			parentNet.testLivelock();
						
			// if livelocked, break the loop, stop simulation
			if (parentNet.isLivelocked()){
				LogText.appendMessage("livelocked");
				break;
			}
			
			parentNet.iterateNet();	
			
			parentNet.testDeadlock();
						
			// if deadlocked, break the loop, stop simulation
			if (parentNet.isDeadlocked()){

				if (parentNet.testWaitingTimePassing()) {
					// the deadlock is due to a transition waiting time to pass
					// the testWaitingTimePassing signals false to the deadlock
					break;
				}
				else {
					LogText.appendMessage("deadlocked");
					break;
				}
			}
			
			// pause simulation for a time
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			Evolution.updateIteration();
							
			/*
			 *  Append the results from the simulation of the parent net into
			 *  stringResults attribute.
			 */
			appendResults(parentNet);							
		}
	 }
		
	/**
	 * Simulate net with pause time in milliseconds.
	 * @param nets
	 * @param pause time
	 * @throws FileNotFoundException 
	 */
	private static void simulateNet(PetriNet parentNet, long pause) {
		
		// append initial state
		appendResults(parentNet);
		
		// will run until the final time is reached
		while(Evolution.getTime() <= Evolution.getFinalTime()) {
			
			// a call to time update the net is done in it's timeIntegrate method
			
			if (parentNet.isDeadlocked()){
				// if deadlocked, stop simulation
				break;
			}
			
			loopIterate(parentNet, pause);
			
			Evolution.updateTime();			
		}		
		// save simulation results to csv file
		generateCsvFile(stringResults);		
	}
	
	/**
	 * Run the program. Genetrate csv with no pause.
	 */
	public static void RunProgram(PetriNet ... nets) {
		RunProgram(0, true, nets);
	}
	
	/**
	 * Run the program simulating all nets.
	 * @param pause time in milliseconds
	 * @param csv generate csv file
	 * @param nets
	 */
	public static void RunProgram(long pause, boolean csv, PetriNet ... nets) {			
		
		SimulationRun.generateCsv = csv;
		
		LogText.appendMessage("simulation starting");
		
		SimulationRun.stringResults = generateHeader();

		PetriNet parentNet = buildTotalNet(nets);

		// call program run
		SimulationRun.simulateNet(parentNet, pause);
		
		LogText.appendMessage("simulation ended");
	}
	
	public static void setGenerateCsv(boolean b){
		generateCsv = b;
	}
	
	public static String getResultsFileName(){
		return resultsFileName;
	}
		
}
