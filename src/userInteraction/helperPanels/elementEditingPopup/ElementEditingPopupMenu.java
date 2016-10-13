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
package userInteraction.helperPanels.elementEditingPopup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import userInteraction.GraphicInteraction;

/**
 * The popup menu that appears to allow edition of a Petri net element.
 */
public abstract class ElementEditingPopupMenu extends JPopupMenu {
				
	private static final long serialVersionUID = 1L;
	
	/*
	 * Problem with visibility if the options are put in the sub classes...
	 */
	/**
	 * "0. Transition Name", "1. Firing Function", "2. Priority",
	 * "3. Change to Discrete Transition", "4. Change to Time Delayed Transition",
	 * "5. Change to Continuous Time Transition", "6. Delay", "7. Turn"
	 */
	protected String[] transitionPopupOptions = {"Transition Name", "Firing Function",
								"Priority", "Change to Discrete Transition",
										"Change to Time Delayed Transition",
					"Change to Continuous Time Transition", "Delay", "Turn"};

	/**
	 * "0. Weight", "1. Change to Inhibitor Arc", "2. Change to Test Arc",
	 * "3. Change to Normal Arc"
	 */
	protected String[] arcPopupOptions = {"Weight", "Change to Inhibitor Arc",
								"Change to Test Arc", "Change to Normal Arc"};
	
	/**
	 * "0. Markings", "1. Capacity", "2. Place Name", "3. Variable Name",
	 * "4. Change to Discrete Place", "5. Change to Continuous Place", 
	 * "6. Change to External Place", "7. Set file to read from"
	 */
	protected String[] placePopupOptions = {"Markings", "Capacity", "Place Name",
			"Variable Name", "Change to Discrete Place",
			"Change to Continuous Place", "Change to External Place",
			"Set file to read from"};
	
	/**
	 * Creates a new popup menu with elements and actions listeners all set.
	 */
	public ElementEditingPopupMenu() {
		super();
		this.popup();
	}
		
	/**
	 * Popup menu logic. It displays some options based on the selected
	 * figure. It also does the logic when an option is selected.
	 * <p>
	 * This method is called when the Release Right Mouse Button event
	 * happens.
	 */
	private void popup(){
	    ActionListener menuListener = new ActionListener() {
	    	// add actions to each event (item)
	    	public void actionPerformed(ActionEvent event) {
	    		if (event.getActionCommand().equals("Delete")){
	    			deleteAction();
	    		}
	    		// other actions
	    		popupActions(event);
	    		
	    		GraphicInteraction.figureManager.nullifySelectedFigure();
	            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(getInvoker());
	            topFrame.repaint();
	        };
	    };
	    
	    // add items
	    JMenuItem item = null;
	    createOptionItem(new JMenuItem("Delete"), menuListener, "Delete");
	    
	    this.addSeparator();
	    this.setBorder(new BevelBorder(BevelBorder.RAISED));

	    // other items
	    showOptions(item, menuListener);
	}
	
	/**
	 * Show the options of the popup menu. Delete is already shown.
	 */
	protected abstract void showOptions(JMenuItem item, ActionListener menuListener);
	
	/**
	 * What happens when each option is selected. Delete is already included.
	 */ 
	protected abstract void popupActions(ActionEvent event);
	
	/**
	 * Create an option item.
	 * @param item
	 * @param menuListener
	 * @param tag
	 */
	protected void createOptionItem(JMenuItem item, ActionListener menuListener,
			String tag){
		
		this.add(item = new JMenuItem(tag));
		item.setHorizontalTextPosition(JMenuItem.RIGHT);
		item.addActionListener(menuListener);	
	}
		
	/**
	 * Show the passed options (a String varags), adding action listeners.
	 * <p>
	 * What happens when each option is selected must be added manually for
	 * each.
	 * @param item
	 * @param menuListener
	 * @param options
	 */
	protected void showPassedOptions(JMenuItem item, ActionListener menuListener,
														String... options){
		for (String opt : options){
			createOptionItem(item, menuListener, opt);
		}
	}
	
	/**
	 * Delete the selected figure object.
	 */
	protected void deleteAction(){
		GraphicInteraction.figureManager.removeSelectedFigure();
	}
}
