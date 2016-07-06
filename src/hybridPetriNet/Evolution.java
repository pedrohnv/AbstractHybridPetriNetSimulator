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
	
	private static Double time = 0.0;
	
	/** Up to what time should the program run?*/
	private static Double finalTime = 10.0;
	
	/** 
	 * Make the timeStep a common divisor of all time constants in the net.
	 * <br>
	 * It is a integration step that defines the smallest time advancement
	 * captured by the net. Smaller means more accuracy, but greater processing
	 * time.
	 * <p>
	 * Some errors may appear for using a value too small. 
	 */
	private static Double timeStep = 1e-10;
	
	/* TODO add dynamic time step. Make it be the smallest value to which a
 	 * change in the net occurs (a transition is enabled or disabled).
 	 */
		
	private static Integer iteration = 0;
	
	/** This is used to end the program, considering a net livelocked.*/
	private static Integer maxIterations = (int) 1e6;
		
	/*
	 * accessors 
	 */
	/** 
	 * @return current time
	 */
	public static Double getTime(){return Evolution.time;}
	
	/** 
	 * @return time step
	 */
	public static Double getTimeStep(){return Evolution.timeStep;}
	
	/** 
	 * @return ending time
	 */
	public static double getFinalTime(){return Evolution.finalTime;}
	
	/** 
	 * @return current iteration
	 */
	public static Integer getIteration(){return Evolution.iteration;}
	
	/** 
	 * @return number of iterations to consider livelock.
	 */
	public static Integer getMaxIterations(){return Evolution.maxIterations;}
	
	/*
	 * mutators 
	 */	
	/**
	 * Set current time and iteration to zero.
	 */
	public static void reset() {
		Evolution.iteration = 0;
		Evolution.time = 0.0;
	}
	
	/** Change the current iteration*/
	public static void setIteration(Integer newIteration){
		Evolution.iteration = newIteration;}
	
	/** Change the time step*/
	public static void setTimeStep(double newTimeStep){
		if (timeStep == 0) {
			throw new UnsupportedOperationException(
					"Invalid value, did not change."); 
		} else {Evolution.timeStep = newTimeStep;}
	}
	
	/** Change the ending time*/
	public static void setFinalTime(double newFinalTime){
		Evolution.finalTime = newFinalTime;}
		
	/** Change the maximum number of iterations to consider livelock*/
	public static void setMaxIterations(Integer newMaxIterations){
		Evolution.maxIterations = newMaxIterations;}
		
	/*
	 * Updaters
	 */
	/** Advance one time step.*/
	public static void updateTime(){
		Evolution.time += Evolution.timeStep;}
		
	/** Advance one iteration.*/
	public static void updateIteration(){
		Evolution.iteration += 1;}
		
}
