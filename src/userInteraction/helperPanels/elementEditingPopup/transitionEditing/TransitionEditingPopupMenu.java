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
package userInteraction.helperPanels.elementEditingPopup.transitionEditing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import hybridPetriNet.transitions.ContinuousTimeTransition;
import hybridPetriNet.transitions.TimeDelayedTransition;
import hybridPetriNet.transitions.Transition;
import userInteraction.GraphicInteraction;
import userInteraction.graphicNetElementWrappers.transitionFigures.TransitionFigure;
import userInteraction.helperPanels.elementEditingPopup.ElementEditingPopupMenu;
import utilities.Helper;

/**
 * The popup menu that appears to allow edition of a Transition.
 */
public class TransitionEditingPopupMenu extends ElementEditingPopupMenu {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new popup menu with elements and actions listeners all set.
	 */
	public TransitionEditingPopupMenu() {
		super();
	}
		
	@Override
	protected void showOptions(JMenuItem item, ActionListener menuListener){
		Transition transition = ((TransitionFigure) GraphicInteraction.
						figureManager.getSelectedFigure()).getTransition();
		
		createOptionItem(item, menuListener, transitionPopupOptions[7]);
		
		String[] commonOptions = Arrays.copyOfRange(transitionPopupOptions, 0, 3);
		
		showPassedOptions(item, menuListener, commonOptions);
		
		if (transition instanceof ContinuousTimeTransition){			
			createOptionItem(item, menuListener, transitionPopupOptions[3]);
			
			createOptionItem(item, menuListener, transitionPopupOptions[4]);
		}
		else if (transition instanceof TimeDelayedTransition){			
			createOptionItem(item, menuListener, transitionPopupOptions[6]);
			
			createOptionItem(item, menuListener, transitionPopupOptions[3]);
			
			createOptionItem(item, menuListener, transitionPopupOptions[5]);			
		}		
		else {
			// assumed to be a discrete transition
			createOptionItem(item, menuListener, transitionPopupOptions[4]);
			
			createOptionItem(item, menuListener, transitionPopupOptions[5]);
		}
	}
	
	@Override
	protected void popupActions(ActionEvent event){
		Transition transition = ((TransitionFigure) GraphicInteraction.
						figureManager.getSelectedFigure()).getTransition();
		
		if (event.getActionCommand().equals(transitionPopupOptions[0])){	        	  
			// Transition Name
			String input = JOptionPane.showInputDialog(null, "Enter new "
								+ "Transition name", transition.getName());
			
			if (Helper.notNullNorEmpty(input)){
				if (Helper.notNumber(input)){
					transition.changeName(input);
				}
				else {
					JOptionPane.showMessageDialog(null, "Transition Name cannot"
										+ " begin with a number!\nDid not change");
				}
			}
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[1])){	        	  
			// Firing Function
			String input = JOptionPane.showInputDialog(null, "Enter new "
					+ "firing function", transition.getFiringFunctionString());
			
			transition.changeFiringFunctionString(input);
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[2])){	        	  
			// Priority
			int newValue = transition.getPriority();
			
			String input = JOptionPane.showInputDialog(null, "Enter new "
												+ "priority", newValue);
			
			if (Helper.notNullNorEmpty(input)){
				try {
					newValue = Integer.valueOf(input);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Invalid input. It must be"
											+ " an integer number. Did not change.");
				}
				
				try {
					transition.changePriority(newValue);
				}
				catch (UnsupportedOperationException e){
					JOptionPane.showMessageDialog(null, "Invalid input.\nIt must be"
							+ " greater or equal to zero.");
				}					
			}					
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[3])){	        	  
			// Change to Discrete Transition			
			((TransitionFigure) GraphicInteraction.figureManager.getSelectedFigure()).
					changeTransition(new Transition(transition.getName(),
										transition.getPriority(),
										transition.getFiringFunctionString()));
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[4])){	        	  
			// Change to Time Delayed Transition
			((TransitionFigure) GraphicInteraction.figureManager.getSelectedFigure()).
					changeTransition(new TimeDelayedTransition(transition.getName(),
							transition.getPriority(),transition.getFiringFunctionString()));
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[5])){	        	  
			// Change to Continuous Time Transition
			((TransitionFigure) GraphicInteraction.figureManager.getSelectedFigure()).
					changeTransition(new ContinuousTimeTransition(transition.getName(),
							transition.getPriority(),
							transition.getFiringFunctionString()));
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[6])){
			// delay
			Double newValue = ((TimeDelayedTransition) transition).getDelay();
			
			String input = JOptionPane.showInputDialog(null, "Enter new "
												+ "delay time", newValue);
			
			if (Helper.notNullNorEmpty(input)){
				try {
					newValue = Double.valueOf(input);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Invalid input.\nIt must be"
							+ " a number greater than zero.");
				}
				try {
					((TimeDelayedTransition) transition).changeDelay(newValue);
				}
				catch (UnsupportedOperationException e){
					JOptionPane.showMessageDialog(null, "Invalid input.\nIt must be"
							+ " a number greater than zero.");
				}	
			}	
		}
		
		else if (event.getActionCommand().equals(transitionPopupOptions[7])){
			GraphicInteraction.figureManager.turnSelectedFigure();
		}
				
	}
	
}
