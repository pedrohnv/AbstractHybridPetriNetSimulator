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
package userInteraction.helperPanels.elementEditingPopup.placeEditing;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import utilities.Helper;

/**
 * A panel to ask input from the user and return them as arguments to change
 * the capacity of a place.
 */
public class CapacityInputPanel extends JPanel{
	
	private static final long serialVersionUID = -4780552046055601513L;

	private JTextField minimum;
	
	private JTextField maximum;

	/**
	 * Argument is the current capacity of the place. Assumed to be lower and
	 * uper values, respectively.
	 * @param capacity
	 */
	public CapacityInputPanel(double[] capacity) {
		minimum = new JTextField(5);
	    maximum = new JTextField(5);

	    this.setLayout(new MigLayout());
	    this.add(new JLabel("Minimum:"), "cell 0 0");
	    this.add(minimum, "cell 1 0");
	    this.add(new JLabel("Maximum:"), "cell 0 1");
	    this.add(maximum, "cell 1 1");
	    	    
	    minimum.setText(String.valueOf(capacity[0]));
	    maximum.setText(String.valueOf(capacity[1]));	    	    
	}
	
	public double[] showPanel(){
	    Integer input = JOptionPane.showConfirmDialog(null, this,
				"Please enter Capacity range of the place",
       							JOptionPane.OK_CANCEL_OPTION);
	    
		String inputMinimum = minimum.getText();
		String inputMaximum = maximum.getText();
		
		if (input.equals(JOptionPane.OK_OPTION) && 
					Helper.notNullNorEmpty(inputMinimum) && 
								Helper.notNullNorEmpty(inputMaximum)){
		
			Double newMinimum = null;
			Double newMaximum = null;			
			
			try {
				newMinimum = Double.valueOf(inputMinimum);
				newMaximum = Double.valueOf(inputMaximum);
			}
			catch (Exception e){				
				JOptionPane.showMessageDialog(null, "Invalid Input.");
			}
			
			return new double[] {newMinimum, newMaximum};			
		}
		else {
			return null;
		}
	}

}
