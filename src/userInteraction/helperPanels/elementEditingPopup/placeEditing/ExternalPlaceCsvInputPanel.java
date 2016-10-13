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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * A panel to prompt the user to input a csv file, a time and reference value
 * columns to pass as arguments to a External Place.
 */
public class ExternalPlaceCsvInputPanel extends JPanel {

	private static final long serialVersionUID = -8662384856004730369L;
	
	private File file;
	
	private JTextField timeField;
	
	private JTextField valueField;
	
	public ExternalPlaceCsvInputPanel() {
		super(new MigLayout("", "[][]", "[][][]"));

		// lay components
		timeField = new JTextField(3);
	    valueField = new JTextField(3);

	    this.setLayout(new MigLayout());
	    this.add(new JLabel("time column:"), "cell 0 0");
	    this.add(timeField, "cell 1 0");
	    this.add(new JLabel("value column:"), "cell 0 1");
	    this.add(valueField, "cell 1 1");
		
	    JButton button_setFile = new JButton("Select file");
	    button_setFile.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		setFileButtonAction();
	    	}
	    });
	    
	    this.add(button_setFile, "cell 0 2");
	}

	public int showPanel(){
		return JOptionPane.showConfirmDialog(null, this, 
    			"Enter parameters", JOptionPane.OK_CANCEL_OPTION);
	}
	
	public File getFile(){
		return file;		
	}
	
	public int getTimeColumn(){
		return Integer.valueOf(timeField.getText());		
	}
	
	public int getValueColumn(){
		return Integer.valueOf(valueField.getText());		
	}
	
	private void setFileButtonAction() {
		JFileChooser chooser = new JFileChooser();		
		int returnVal = chooser.showOpenDialog(null);
		   
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	file = chooser.getSelectedFile();	    	
	    }
	}
}
