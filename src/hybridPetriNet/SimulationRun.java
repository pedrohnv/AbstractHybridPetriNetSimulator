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

import examples.Simple;
import hybridPetriNet.Arcs.AbstractArc;
import hybridPetriNet.PetriNets.PetriNet;
import hybridPetriNet.Places.AbstractPlace;
import hybridPetriNet.Transitions.AbstractTransition;

/**
 * Simulate the Petri nets.
 */
public abstract class SimulationRun {		
	
	/**
	 * This attribute stores the simulation result as a string.
	 * It is saved in a CSV file when the simulation ends.
	 */
	private static String stringResults;
	
	private static String ResultsFileName;
	
	/**
	 * This method takes any number of input nets and makes one
	 * single net. This way the firings and priorities are guaranteed
	 * to synchronize.
	 * 
	 * Get the lists of AbstractArcs, AbstractPlaces and AbstractTransitions of each net
	 * and build a unified list of each. To remove duplicates:
	 * 
	 * Build a set (this object does not accept duplicates) of each.
	 * 
	 * Clear lists, rebuild from the set with unique elements.
	 *
     * Array List is used because of sorting and shuffling methods
     *
	 */
	protected static PetriNet buildTotalNet(PetriNet ... nets){		
		
		ArrayList <AbstractPlace> placeList = new ArrayList <AbstractPlace>();		
		ArrayList <AbstractTransition> transitionList = new ArrayList <AbstractTransition>();		
		ArrayList <AbstractArc> arcList = new ArrayList <AbstractArc>();
		
		String names = ""; 
		
		/*
		 *  Possible problems with multithreading because arrayList
		 *  is not thread-synchronized.
		 *  TODO: assure synchronization
		 */
		for (PetriNet oneNet : nets){
			
			placeList.addAll(oneNet.getPlaces());			
			transitionList.addAll(oneNet.getTransitions());			
			arcList.addAll(oneNet.getArcs());
			
			names += (" + " + oneNet.getName()); 
		}
		
		Set <AbstractPlace> placeSet = new HashSet<AbstractPlace>();
		Set <AbstractTransition> transitionSet = new HashSet<AbstractTransition>();
		Set <AbstractArc> arcSet = new HashSet<AbstractArc>();
		
		placeSet.addAll(placeList);
		transitionSet.addAll(transitionList);
		arcSet.addAll(arcList);
				
		placeList.clear();			
		transitionList.clear();			
		arcList.clear();
				
		placeList.addAll(placeSet);			
		transitionList.addAll(transitionSet);			
		arcList.addAll(arcSet);
		
		// sort AbstractPlace list by AbstractPlace's index, to organize the results
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
	 * 
	 * @param Petri net being simulated
	 */
	private static void appendResults(PetriNet net){
		/*  TODO (from Java doc): Instances of StringBuilder are not safe for
		 *  use by multiple threads. If such synchronization is required then
		 *  it is recommended that StringBuffer be used.	
		 */
		StringBuilder strBuilder = new StringBuilder(stringResults);
		
		for (AbstractPlace AbstractPlace : net.getPlaces()) {
			 		 
			strBuilder.append(AbstractPlace.getName());
			strBuilder.append(','); // separator character
			 
			strBuilder.append(AbstractPlace.getIndex());
			strBuilder.append(',');
			 
			strBuilder.append(Evolution.getTime());
			strBuilder.append(',');
			 
			strBuilder.append(Evolution.getIteration());
			strBuilder.append(',');
			 
			strBuilder.append(AbstractPlace.getMarkings());
			strBuilder.append('\n'); // new line character
		}		 		 
		stringResults = strBuilder.toString();
	}
	
	/**
	 * Creates a table with every marking in every AbstractPlace, at a given
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
		 
		 System.out.println("csv file created");
	 }
	 
	 /**
	 * Iterate net until max iteration is reached.
	 * 
	 * set the iteration to zero and loop.
	 */
	 public static void loopIterate(PetriNet parentNet){
		    
		Evolution.setIteration(0);
		
		// append initial state
		appendResults(parentNet);
			
		while (Evolution.getIteration() < Evolution.getMaxIterations()){				

			Evolution.updateIteration();
			
			parentNet.iterateNet();			
				
			/*
			 *  Append the results from the simulation of the parent net into
			 *  stringResults attribute.
			 */
			appendResults(parentNet);
			
			//parentNet.testDeadlock();
			
			// if deadlocked, break the for loop, start next time step
			if (parentNet.isDeadlocked()){
				break;
			}					
		}
	 }
	 
	
	/**
	 * Simulate all nets. Enter any number of nets as argument.
	 * @param nets
	 * @throws FileNotFoundException 
	 */
	public static void simulateNet(PetriNet ... nets) {
		
		PetriNet parentNet = buildTotalNet(nets);
		
		// will run until the final time is reached
		while(Evolution.getTime() <= Evolution.getFinalTime()) {
			
			loopIterate(parentNet);
			
			Evolution.updateTime();
			
			parentNet.updateElements();
		}
		// save simulation results to csv file
		generateCsvFile(stringResults);
	}
	
	/**
	 * Run the program
	 */
	public static void RunProgram(PetriNet ... nets) {
		
				
		// declare nets
		PetriNet parentNet = Simple.buildNet();

		ResultsFileName = parentNet.getName();
		
		// set constants
		Evolution.setTimeStep(1);
		Evolution.setMaxIterations(10);
		Evolution.setFinalTime(0);
				
		System.out.println("simulation starting");
		
		stringResults = generateHeader();
		
		// call program run
		SimulationRun.simulateNet(parentNet);
		
		System.out.println("simulation ended");

	}
	
	/**
	 * Main execution
	 */
	public static void main(String[] args){
		// TODO user: customize the program run
		
		PetriNet net = Simple.buildNet();
		
		RunProgram(net);
	}
}
