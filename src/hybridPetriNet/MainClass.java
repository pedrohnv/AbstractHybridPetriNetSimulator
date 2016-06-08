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

public abstract class MainClass {
	/**
	 * This class contains "global" variables, that are used by all classes and
	 * the program itself.
	 * 
	 * If a class must use any of the below variables, extend this class.
	 */
		
	private static double time = 0;
	
	// up to what time should the program run?
	private static double finalTime = 1e2;
	
	// make the timeStep a common divisor of all time constants in the net.
	private static double timeStep = 1e-6;
		
	private static int iteration = 0;
	
	// This is used to end the program, considering a net livelocked.
	private static int maxIterations = (int) 1e6;
		
	/**
	 * accessors 
	 */
	public static double getTime(){return MainClass.time;}
	
	public static double getTimeStep(){return MainClass.timeStep;}
	
	public static double getFinalTime(){return MainClass.finalTime;}
	
	public static int getIteration(){return MainClass.iteration;}
	
	public static int getMaxIterations(){return MainClass.maxIterations;}
	
	/**
	 * mutators 
	 */
	// advance one time step.
	public static void changeTime(double newValue){
		MainClass.time = newValue;}
	
	// advance one iteration
	public static void changeIteration(int newValue){
		MainClass.iteration = newValue;}
	
	public static void changeTimeStep(double newTimeStep){
		MainClass.timeStep = newTimeStep;}
	
	public static void changeFinalTime(double newFinalTime){
		MainClass.finalTime = newFinalTime;}
		
	public static void changeMaxIterations(int newMaxIterations){
		MainClass.maxIterations = newMaxIterations;}
	
	/**
	 * Updaters
	 */
	// advance one time step
	public static void updateTime(){
		MainClass.time += MainClass.timeStep;}
		
	// advance one iteration
	public static void updateIteration(){
		MainClass.iteration += 1;}

	

	
}
