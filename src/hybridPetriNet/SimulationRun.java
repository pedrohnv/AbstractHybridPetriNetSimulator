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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import examples.Robot;
import hybridPetriNet.Arcs.Arc;
import hybridPetriNet.Places.Place;
import hybridPetriNet.Transitions.Transition;

/**
 * Run the program. The user should define what he wants:
 * see the markings at every iteration and time step, or at
 * a specific time and iteration.
 * 
 * Restrict this to some places, etc.
 * 
 * The Evolution that is extended has the simulations constants and
 * associated methods: time, iteration, time step, max iterations,
 * final time.
 * 
 * In the example below, three separated nets are created:
 * silo, doser, and lance. They are isolated from each other.
 * These are the "lower" nets.
 * 
 * The fourth net, injectionSystem, the "upper" net, makes connections
 * between the lower nets with some additional places and transitions.
 * This makes the Object Orientation Paradigm applied to Petri nets.
 * 
 * It was chosen to watch specific places: "injection pressure" and 
 * "injection flow". Their markings are plotted.
 */
public abstract class SimulationRun {		
	
	/**
	 * This attribute stores the simulation result as a string.
	 * It is saved in a CSV file when the simulation ends.
	 */
	private static String stringResults;	
	
	/**
	 * This method takes any number of input nets and makes one
	 * single net. This way the firings and priorities are guaranteed
	 * to synchronize.
	 * 
	 * Get the lists of arcs, places and transitions of each net
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
		ArrayList <Place> placeList = new ArrayList <Place>();		
		ArrayList <Transition> transitionList = new ArrayList <Transition>();		
		ArrayList <Arc> arcList = new ArrayList <Arc>();
		
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
		
		return (new PetriNet(placeList, transitionList, arcList));
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
		 strBuilder.append("Place name");
		 strBuilder.append(','); // separator character
		 strBuilder.append("Place index");
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
		 
		for (Place place : net.getPlaces()) {
			 		 
			strBuilder.append(place.getName());
			strBuilder.append(','); // separator character
			 
			strBuilder.append(place.getIndex());
			strBuilder.append(',');
			 
			strBuilder.append(Evolution.getTime());
			strBuilder.append(',');
			 
			strBuilder.append(Evolution.getIteration());
			strBuilder.append(',');
			 
			strBuilder.append(place.getMarkings());
			strBuilder.append('\n'); // new line character
		}		 		 
		stringResults = strBuilder.toString();
	}
	
	/**
	 * Creates a table with every marking in every place, at a given
	 * time and iteration.
	 * @param string to save as csv
	 */
	 private static void generateCsvFile(String csvString)
			 					throws FileNotFoundException{
		 		 
		 // get current working directory (of application)
		 String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
		 
		 PrintWriter printWriter = new PrintWriter(new File(workingDirectory +
				 "Simulation Results.csv"));		 
		 printWriter.write(csvString);		 
		 printWriter.close();
		 
		 System.out.println("csv file created");
	 }
	 
	 /**
	 * Iterate every net. If the max iterations is reached, throw
	 * exception: livelock.
	 * 
	 * set the iteration to zero and loop.
	 */
	 public static void loopIterate(PetriNet parentNet){
		    
			Evolution.setIteration(0);
			
			while (Evolution.getIteration() <= Evolution.getMaxIterations()){				
				/*
				 *  Append the results from the simulation of the parent net into
				 *  stringResults attribute.
				 */
				appendResults(parentNet);
				
				parentNet.iterateNet();
				
				// if deadlocked, break the for loop, start next time step
				if (parentNet.isDeadlocked()){
					
					System.out.println("net deadlocked");
					
					break;
				}					
				Evolution.updateIteration();
			}
	 }
	 
	
	/**
	 * Main program run. Enter any number of nets as argument.
	 * @param nets
	 * @throws FileNotFoundException 
	 */
	public static void simulateNet(PetriNet ... nets) throws FileNotFoundException{
		
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
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO user: customize the program
		
		// declare nets
		PetriNet net = Robot.buildNet();
		
		// set constants
		Evolution.setTimeStep(1);
		Evolution.setMaxIterations(3);
		Evolution.setFinalTime(0);
				
		System.out.println("simulation starting");
		
		stringResults = generateHeader();
		
		// call program run
		SimulationRun.simulateNet(net);
		
		System.out.println("simulation ended");

	}
}
