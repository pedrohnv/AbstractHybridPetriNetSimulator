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

/**
 * This class contains the evolution variables (time and iteration), that are
 * used by all classes and the program itself.
 */
public abstract class Evolution {
	
	private static double time = 0;
	
	/** Up to what time should the program run?*/
	private static double finalTime = 1;
	
	/** Make the timeStep a common divisor of all time constants in the net.*/
	private static double timeStep = 1;
	/* TODO add dynamic time step. Make it be the smallest value to which a
 	 * change in the net occurs (a transition is enabled or disabled).
 	 */
		
	/** The integration step, should integration be needed.*/
	private static double integrationStep = 1e-6;
	
	private static int iteration = 0;
	
	/** This is used to end the program, considering a net livelocked.*/
	private static int maxIterations = (int) 1e6;
		
	/*
	 * accessors 
	 */
	/** 
	 * @return current time
	 */
	public static double getTime(){return Evolution.time;}
	
	/** 
	 * @return time step
	 */
	public static double getTimeStep(){return Evolution.timeStep;}
	
	/** 
	 * @return ending time
	 */
	public static double getFinalTime(){return Evolution.finalTime;}
	
	/** 
	 * @return current iteration
	 */
	public static int getIteration(){return Evolution.iteration;}
	
	/** 
	 * @return number of iterations to consider livelock.
	 */
	public static int getMaxIterations(){return Evolution.maxIterations;}
	
	/*
	 * mutators 
	 */	
	/** Change the current iteration*/
	public static void setIteration(int newIteration){
		Evolution.iteration = newIteration;}
	
	/** Change the time step*/
	public static void setTimeStep(double newTimeStep){
		if (integrationStep == 0) {
			throw new UnsupportedOperationException(
					"Invalid value, did not change."); 
		} else {Evolution.timeStep = newTimeStep;}
	}
	
	/** Change the ending time*/
	public static void setFinalTime(double newFinalTime){
		Evolution.finalTime = newFinalTime;}
		
	/** Change the maximum number of iterations to consider livelock*/
	public static void setMaxIterations(int newMaxIterations){
		Evolution.maxIterations = newMaxIterations;}

	public static void setIntegrationStep(double integrationStep) {
		if (integrationStep == 0) {
			throw new UnsupportedOperationException(
					"Invalid value, did not change."); 
		} else {Evolution.integrationStep = integrationStep;}
	}
	
	/*
	 * Updaters
	 */
	/** Advance one time step.*/
	public static void updateTime(){
		Evolution.time += Evolution.timeStep;}
		
	/** Advance one iteration.*/
	public static void updateIteration(){
		Evolution.iteration += 1;}

	public static double getIntegrationStep() {
		return integrationStep;
	}
	
		
}
