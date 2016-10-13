package userInteraction.helperPanels.elementEditingPopup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import userInteraction.GraphicInteraction;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;
import userInteraction.graphicNetElementWrappers.NetBox;
import utilities.Helper;

public class PetriNetObjectPopupMenu extends ElementEditingPopupMenu {

	private static final long serialVersionUID = -4687062953260827761L;

	public PetriNetObjectPopupMenu() {
		super();
	}

	@Override
	protected void showOptions(JMenuItem item, ActionListener menuListener) {
		createOptionItem(item, menuListener, "Undo Encapsulation");
		createOptionItem(item, menuListener, "Set Name");
	}

	@Override
	protected void popupActions(ActionEvent event) {
		if (event.getActionCommand().equals("Undo Encapsulation")){
			GraphicInteraction.figureManager.undoEncapsulation(
					(NetBox) GraphicInteraction.figureManager.getSelectedFigure());
		}
		else if (event.getActionCommand().equals("Set Name")){
			String input = JOptionPane.showInputDialog(null, "Enter new net name",
					((NetBox) GraphicInteraction.figureManager.
							getSelectedFigure()).getName());
			
			if (Helper.notNullNorEmpty(input)){
				if (Helper.notNumber(input)){
					
					((NetBox) GraphicInteraction.figureManager.
							getSelectedFigure()).setName(input);
				}
				else {
					JOptionPane.showMessageDialog(null, "Name cannot"
							+ " begin with a number!");
				}	
			}
		}
		
	}
	
	/**
	 * Delete this figure and all elements it contains.
	 */
	@Override
	protected void deleteAction(){
		NetBox box = (NetBox) GraphicInteraction.figureManager.getSelectedFigure();
		
		List <AbstractPetriNetFigure> figures = 
				GraphicInteraction.figureManager.getFigures();
		
		synchronized (figures){
			for (AbstractPetriNetFigure f : figures){
				
				if (box.getContainedFigures().contains(f)){
					GraphicInteraction.figureManager.removeFigure(f);
				}
			}
			
			GraphicInteraction.figureManager.removeFigure(box);
		}
		
	}
}
