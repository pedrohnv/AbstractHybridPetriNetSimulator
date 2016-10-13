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
package userInteraction;

import javax.swing.JOptionPane;

import enums.EditingMode;
import hybridPetriNet.Evolution;
import hybridPetriNet.arcs.Arc;
import hybridPetriNet.petriNets.PetriNet;
import utilities.LogText;

/**
 * The original SimulationRun was built in a time there were no Graphic
 * User Interface. As it may still be used, it was decided to build a new
 * class to execute the simulation with a graphic animated context.
 */
public class SimulationGraphicRun implements Runnable {
	
	private GraphicInteraction mainGUI;
	
	public SimulationGraphicRun(GraphicInteraction graphicInteraction){
		mainGUI = graphicInteraction;
	}

	long simulationSpeed;
	
	@Override
	public void run() {
		while ( (mainGUI.editingMode() == EditingMode.SIMULATE) && 
				(mainGUI.playSimulation()) ){
			simulationSpeed = mainGUI.simulationSpeed();
			
			SimulationGraphicRun.forwardIteration(mainGUI.netToSimulate());
			
			mainGUI.repaint();
			
			try {
				Thread.sleep(simulationSpeed);
			} catch (InterruptedException e) {
				//e.printStackTrace(); do nothing
			}
		}			
	}
	
	/**
	 * Do one iteration step forward. Advances time if certain
	 * condition is met.
	 * @param net
	 */
	public static void forwardIteration(PetriNet net){
		net.iterationUpdateElements();
		
		net.testLivelock();
		
		if (net.isLivelocked()){
			LogText.appendMessage("livelocked");
			// update the time
			Evolution.updateTime();
			Evolution.setIteration(0);
			return; // stop execution
		}
		try {
			net.iterateNet();
		}
		catch (UnsupportedOperationException e){
			JOptionPane.showMessageDialog(null, "An error occurred. Probably "
					+ "It could be a transition forcing a invalid value in\n"
					+ "a place; like when a continuous time transition "
					+ "is connected to a discrete place.");
		}
		net.testDeadlock();
		
		if (net.isDeadlocked()){
			if (net.testWaitingTimePassing()){
				// advance time
				Evolution.updateTime();
				Evolution.setIteration(0);
				return;
			}
			else {
				LogText.appendMessage("deadlocked");				
			}			
		}
		Evolution.updateIteration();
	}
	
	/**
	 * Iterate the net backwards.
	 * @param net
	 */
	public static void backwardIteration(PetriNet net){
		// invert the sign of the weight of every arc
		invertArcsWeight(net);
		
		if (Evolution.getIteration() == 0){
			net.timeUpdateElements();
		}
		else {
			net.iterationUpdateElements();
		}
		
		try {
			net.iterateNet();
		}
		catch (UnsupportedOperationException e){
			JOptionPane.showMessageDialog(null, "An error occurred. Probably "
					+ "It could be a transition forcing a invalid value in\n"
					+ "a place; like when a continuous time transition "
					+ "is connected to a discrete place.");
		}
		net.testDeadlock();
		
		if (net.isDeadlocked()){
			if (net.testWaitingTimePassing()){
				Evolution.setIteration(0);
			}
		}		
		Evolution.reverseIteration();
		
		undoArcsWeightInversion(net);
	}
	
	private static void invertArcsWeight(PetriNet net){
		for (Arc arc : net.getArcs()){
			// this method, in test arcs, makes the change to the test threshold
			String weight = arc.getWeightString();
			
			arc.changeWeightString("-(" + weight + ")");
		}
	}
	
	private static void undoArcsWeightInversion(PetriNet net){
		for (Arc arc : net.getArcs()){			
			String weight = arc.getWeightString();
			
			int len = weight.length() - 1;

			arc.changeWeightString(weight.substring(2, len));
		}
	}
		
}
