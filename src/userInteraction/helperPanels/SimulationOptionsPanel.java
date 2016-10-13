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
package userInteraction.helperPanels;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hybridPetriNet.Evolution;
import hybridPetriNet.SimulationRun;
import net.miginfocom.swing.MigLayout;

/**
 * A Panel which displays the Simulation Options and allow
 * their editing.
 */
public class SimulationOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTextField maximumIterationsInput = new JTextField(5);
	
    private JTextField timeStepInput = new JTextField(5);
    
    private JTextField endingTimeInput = new JTextField(5);
    
    private JTextField resultsFileNameInput = new JTextField(30);
    
    private JCheckBox chckbxUseFourthOrder = new JCheckBox("Use Fourth order Runge-Kutta");
    
    private static JCheckBox chckbxGenerateCsv = new JCheckBox("Generate csv file");
    	
	public SimulationOptionsPanel() {
		super(new MigLayout("", "[][59.00]", "[][][][][]"));
		
		chckbxUseFourthOrder.setToolTipText("Check this box if you want to use Fourth"
	    		+ " order Runge-Kutta integration. \r\n\r\nBeware that this will "
	    		+ "require more memory and processing power. \r\n\r\nIf left unchecked,"
	    		+ " second order Runge-Kutta is used.");
	    
		// get initial values
		maximumIterationsInput.setText(String.valueOf(Evolution.getMaxIterations()));
		timeStepInput.setText(String.valueOf(Evolution.getTimeStep()));
		endingTimeInput.setText(String.valueOf(Evolution.getFinalTime()));
		resultsFileNameInput.setText(String.valueOf(SimulationRun.getResultsFileName()));
		
	    // add fields
	    chckbxUseFourthOrder.setToolTipText("Check this box if you want to"
	    		+ " generate a csv file with the results of the simulation.");
	    
	    this.add(new JLabel("File name and ending time are only meaningful "
	    		+ "when generating a CSV file."), "cell 0 0 2");
    	this.add(new JLabel("Maximum iterations to consider livelock:"),
				"cell 0 1");
		this.add(maximumIterationsInput, "cell 1 1");
		this.add(new JLabel("Time step:"), "cell 0 2");
		this.add(timeStepInput, "cell 1 2");
		this.add(new JLabel("Ending time:"), "cell 0 3");
		this.add(endingTimeInput, "cell 1 3");
		this.add(new JLabel("Resulting csv file (and path) name:"),
								"cell 0 4");
		this.add(resultsFileNameInput, "cell 0 5 2"); // third number is spam size
		this.add(chckbxUseFourthOrder, "cell 0 6");
		this.add(chckbxGenerateCsv, "cell 0 7");
	}
	
	/**
	 * Show panel and set values. 
	 */
	public void showPanel(){					   
	    int input = JOptionPane.showConfirmDialog(null, this, 
	    			"Enter simulation options", JOptionPane.OK_CANCEL_OPTION);
	    
	    // try to read values
	    if (input == JOptionPane.OK_OPTION) {
	    	try {
		    	this.setConstants();
	    	}
	    	catch (Exception e1){
	    		JOptionPane.showMessageDialog(null, "Some of the values"
	    				+ " were invalid. Please input again.");
	    	}
	    }
	}
		
	/**
	 * Get and Set simulation constants based on current attribute values.
	 */
	public void setConstants(){		
		// get values
    	int maxIterations = Integer.parseInt(maximumIterationsInput.getText());
		double integrationStep = Double.parseDouble(timeStepInput.getText());
		double finalTime = Double.parseDouble(endingTimeInput.getText());
		String ResultFile = resultsFileNameInput.getText();
		
		Evolution.setTimeStep(integrationStep);
		Evolution.setMaxIterations(maxIterations);
		Evolution.setFinalTime(finalTime);
		SimulationRun.setResultsFileName(ResultFile);
		SimulationRun.setGenerateCsv(chckbxGenerateCsv.isSelected());
	}
	
	public boolean useFourthOrderRungeKutta(){
		return this.chckbxUseFourthOrder.isSelected();
	}
	
	public static boolean mustGenerateCsv(){
		return chckbxGenerateCsv.isSelected();
	}
	
}
