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
import java.util.List;

import javax.swing.JMenuItem;

import userInteraction.GraphicInteraction;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;
import userInteraction.graphicNetElementWrappers.NetBox;
import userInteraction.graphicNetElementWrappers.SelectionRectangle;

public class MultipleSelectionPopupMenu extends ElementEditingPopupMenu {

	private static final long serialVersionUID = 1L;

	public MultipleSelectionPopupMenu() {
		super();
	}

	@Override
	protected void showOptions(JMenuItem item, ActionListener menuListener) {
		createOptionItem(item, menuListener, "Encapsulate Elements");
	}

	@Override
	protected void popupActions(ActionEvent event) {
		if (event.getActionCommand().equals("Encapsulate Elements")){
			
			SelectionRectangle SR = (SelectionRectangle) 
					GraphicInteraction.figureManager.getSelectedFigure();
			
			if (! SR.getContainedFigures().isEmpty()){
				NetBox PetriNetBox = new NetBox(SR.getLocation(),
						SR.getContainedFigures());
				
				GraphicInteraction.figureManager.encapsulateElements(PetriNetBox);				
			}
			
		}
	}
	
	/**
	 * Delete all figures inside the selection rectangle.
	 */
	@Override
	protected void deleteAction(){
		SelectionRectangle SR = (SelectionRectangle) 
						GraphicInteraction.figureManager.getSelectedFigure();
		
		List <AbstractPetriNetFigure> containedFigures = SR.getContainedFigures();
		
		GraphicInteraction.figureManager.removeFigure(containedFigures);
	}

}
