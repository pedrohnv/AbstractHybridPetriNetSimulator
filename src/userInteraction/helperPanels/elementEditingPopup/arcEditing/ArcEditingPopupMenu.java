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
package userInteraction.helperPanels.elementEditingPopup.arcEditing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import hybridPetriNet.arcs.Arc;
import hybridPetriNet.arcs.InhibitorArc;
import hybridPetriNet.arcs.TestArc;
import userInteraction.GraphicInteraction;
import userInteraction.graphicNetElementWrappers.arcFigures.ArcFigure;
import userInteraction.helperPanels.elementEditingPopup.ElementEditingPopupMenu;
import utilities.Helper;

/**
 * The popup menu that appears to allow edition of an Arc.
 */
public class ArcEditingPopupMenu extends ElementEditingPopupMenu {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new popup menu with elements and actions listeners all set.
	 */
	public ArcEditingPopupMenu() {
		super();
	}
		
	@Override
	protected void showOptions(JMenuItem item, ActionListener menuListener){
		Arc arc = ((ArcFigure) GraphicInteraction.figureManager.getSelectedFigure())
				.getArc();
		
		createOptionItem(item, menuListener, arcPopupOptions[0]);
		
		if (arc instanceof InhibitorArc){
			
			createOptionItem(item, menuListener, arcPopupOptions[2]);
			
			createOptionItem(item, menuListener, arcPopupOptions[3]);
		}
		else if (arc instanceof TestArc){
			
			createOptionItem(item, menuListener, arcPopupOptions[1]);
			
			createOptionItem(item, menuListener, arcPopupOptions[3]);
		}
		else {
			// assumed to be a normal arc
			createOptionItem(item, menuListener, arcPopupOptions[1]);
			
			createOptionItem(item, menuListener, arcPopupOptions[2]);
		}
	}
	
	@Override
	protected void popupActions(ActionEvent event){
		Arc arc = ((ArcFigure) GraphicInteraction.figureManager.getSelectedFigure()).getArc();
		
		if (event.getActionCommand().equals(arcPopupOptions[0])){	
			// Weight (and test threshold)
			String input = JOptionPane.showInputDialog(null, "Enter new "
					+ "weight", arc.getWeightString());
			
			if (Helper.notNullNorEmpty(input)){
					arc.changeWeightString(input);
			}
		}
		
		else if (event.getActionCommand().equals(arcPopupOptions[1])){
			((ArcFigure) GraphicInteraction.figureManager.getSelectedFigure()).
			changeArc(new InhibitorArc(arc.getPlace(), arc.getTransition(),
					arc.getWeightString()) );
		}
		
		else if (event.getActionCommand().equals(arcPopupOptions[2])){
			((ArcFigure) GraphicInteraction.figureManager.getSelectedFigure()).
					changeArc( new TestArc(arc.getPlace(), arc.getTransition(),
							arc.getWeightString()) );
		}
		
		else if (event.getActionCommand().equals(arcPopupOptions[3])){
			((ArcFigure) GraphicInteraction.figureManager.getSelectedFigure()).
						changeArc( new Arc(arc.getPlace(), arc.getTransition(),
							arc.getWeightString()) );
		}
	}
	
}
