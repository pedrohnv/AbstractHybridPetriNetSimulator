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
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import hybridPetriNet.places.ContinuousPlace;
import hybridPetriNet.places.ExternalPlace;
import hybridPetriNet.places.Place;
import userInteraction.GraphicInteraction;
import userInteraction.graphicNetElementWrappers.placeFigures.PlaceFigure;
import userInteraction.helperPanels.elementEditingPopup.ElementEditingPopupMenu;
import utilities.Helper;

/**
 * The popup menu that appears to allow edition of a Place.
 */
public class PlaceEditingPopupMenu extends ElementEditingPopupMenu {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new popup menu with elements and actions listeners all set.
	 */
	public PlaceEditingPopupMenu() {
		super();
	}
	
	@Override
	protected void showOptions(JMenuItem item, ActionListener menuListener){
		
		String[] commonOptions = Arrays.copyOfRange(placePopupOptions, 0, 4);
    
		this.showPassedOptions(item, menuListener, commonOptions);
		
		if (((PlaceFigure) GraphicInteraction.figureManager.
				getSelectedFigure()).getPlace() instanceof ContinuousPlace){
			
			createOptionItem(item, menuListener, placePopupOptions[4]);
			
			createOptionItem(item, menuListener, placePopupOptions[6]);
			
		}
		else if (((PlaceFigure) GraphicInteraction.figureManager.
				getSelectedFigure()).getPlace() instanceof ExternalPlace){
			
			createOptionItem(item, menuListener, placePopupOptions[4]);
			
			createOptionItem(item, menuListener, placePopupOptions[5]);

			createOptionItem(item, menuListener, placePopupOptions[7]);
		}
		else {
			// assumed to be a discrete place
			createOptionItem(item, menuListener, placePopupOptions[5]);

			createOptionItem(item, menuListener, placePopupOptions[6]);
		}
	}
	
	@Override
	protected void popupActions(ActionEvent event){	
		Place place = ((PlaceFigure) GraphicInteraction.figureManager.
				getSelectedFigure()).getPlace();
		
		if (event.getActionCommand().equals(placePopupOptions[0])){	        	  
			// Markings
			Double newValue = place.getMarkings();
						
			String input = JOptionPane.showInputDialog(null, "Enter new Marking value",
									newValue);
			
			if (Helper.notNullNorEmpty(input)){
				try {
					newValue = Double.valueOf(input);
				}
				catch (Exception e){
					JOptionPane.showMessageDialog(null, "Invalid input value.");
				}
				
				try {
					place.changeMarkings(newValue);
				}
				catch (UnsupportedOperationException e){
					JOptionPane.showMessageDialog(null, "Value outside of the place's"
													+ " capacity.\ndid not change.");
				}
			}			
		}
		
		else if (event.getActionCommand().equals(placePopupOptions[1])){
			// Capacity			
			CapacityInputPanel inputPanel = new CapacityInputPanel(place.getCapacity());
			
			double[] capacity = inputPanel.showPanel();
		    	
			if (capacity != null){
		    	try {
		    		place.changeCapacity(capacity);
				}
				catch (UnsupportedOperationException e){
					JOptionPane.showMessageDialog(null, "Invalid Input.\n"
						+ "Maximum value is lesser than the minimum "
						+ "value.\nDid not change.");
				}
		    }
		    
		}
		
		else if (event.getActionCommand().equals(placePopupOptions[2])){
			// Place name
			String input = JOptionPane.showInputDialog(null, "Enter new Place"
													+ " name", place.getName());
			if (Helper.notNullNorEmpty(input)){
				if (Helper.notNumber(input)){
					place.changePlaceName(input);
				}
				else {
					JOptionPane.showMessageDialog(null, "Place Name cannot"
										+ " begin with a number!\nDid not change");
				}	
			}
		}
		
		else if (event.getActionCommand().equals(placePopupOptions[3])){
			// Variable Name
			String input = JOptionPane.showInputDialog(null, "Enter new variable"
											+ " name", place.getVariableName());
			boolean validName = Helper.notNumber(input);
			
			if (Helper.notNullNorEmpty(input)){
				if (validName){
					place.changeVariableName(input);
				}
				else {
					JOptionPane.showMessageDialog(null, "Variable Name cannot"
										+ " begin with a number!\nDid not change");
				}
			}
		}
		
		else if (event.getActionCommand().equals(placePopupOptions[4])){
			// Change to Discrete Place
			((PlaceFigure) GraphicInteraction.figureManager.getSelectedFigure()).
				changePlace(new Place(place.getName(), (int) place.getMarkings(),
							place.getCapacity(), place.getVariableName()));
		}
		
		else if (event.getActionCommand().equals(placePopupOptions[5])){
			// Change to Continuous Place
			((PlaceFigure) GraphicInteraction.figureManager.getSelectedFigure()).
				changePlace(new ContinuousPlace(place.getName(), (int) place.
						getMarkings(),place.getCapacity(), place.getVariableName()));
		}
		else if (event.getActionCommand().equals(placePopupOptions[6])){
			// change to External place
			((PlaceFigure) GraphicInteraction.figureManager.getSelectedFigure()).
			changePlace(new ExternalPlace(place.getName()));
		}
		else if (event.getActionCommand().equals(placePopupOptions[7])){
			// select external file// set file to read, ghost transition
			ExternalPlaceCsvInputPanel inputPanel = new ExternalPlaceCsvInputPanel();
			
			int input = inputPanel.showPanel();
			
			if (input == JOptionPane.OK_OPTION) {
				try {
					int timeColumn = inputPanel.getTimeColumn();
					
					int valueColumn = inputPanel.getValueColumn();
					
					File file = inputPanel.getFile();
					
					((ExternalPlace) place).setFileToRead(file, timeColumn,
							valueColumn);
				}
				catch (Exception e){
					JOptionPane.showMessageDialog(null, "An error occurred.");
					e.printStackTrace();
				}
			}
			
		}
	}		
}
