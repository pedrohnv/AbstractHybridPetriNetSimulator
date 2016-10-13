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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;
import userInteraction.graphicNetElementWrappers.NetBox;
import userInteraction.graphicNetElementWrappers.SelectionRectangle;
import userInteraction.graphicNetElementWrappers.arcFigures.ArcFigure;
import userInteraction.graphicNetElementWrappers.arcFigures.TemporaryArc;
import userInteraction.graphicNetElementWrappers.placeFigures.PlaceFigure;
import userInteraction.graphicNetElementWrappers.transitionFigures.TransitionFigure;

/**
 * A class to manage all figures of the net. It is a helper class used by the
 * GraphicInteraction class GUI.
 */
public class NetFigureManager {

	/**
	 * All created figures.
	 */
	private List<AbstractPetriNetFigure> figureList = 
			Collections.synchronizedList(new ArrayList <AbstractPetriNetFigure>());

	/**
	 * The figure that is currently considered selected.
	 */
	private AbstractPetriNetFigure selectedFigure = null;

	private TemporaryArc temporaryArc = null;
			
	/**
	 * If the passed coordinates is inside one figure, set it as selected;
	 * no change to the selected figure otherwise.
	 * @param point
	 */
	public synchronized boolean selectFigure(Point point){
		// iterate over arcs last
		Collections.sort(figureList);
		
		for (AbstractPetriNetFigure f : figureList) {
			// see if the coordinate corresponds to a figure
			if ( (f != null) && f.notEncapsulated() && (f.selected(point)) ){
				this.selectedFigure = f;
				return true;
			}
		}
		return false;
	}
			
	/**
	 * Create a temporary arc to draw while dragging the mouse.
	 * @param point
	 */
	public synchronized void createTemporaryArc(Point point){
		this.setTemporaryArc(new TemporaryArc(point));
		
		this.figureList.add(getTemporaryArc());
	}

	public synchronized void nullifyTemporaryArc(){		
		getTemporaryArc().nullify();
		
		figureList.remove(this.getTemporaryArc());
		
		setTemporaryArc(null);
	}

	/**
	 * Set the selected figure to null; remove from figure list if it is an
	 * instance of SelectionRectangle.
	 */
	public synchronized void nullifySelectedFigure(){
		if (this.selectedFigure instanceof SelectionRectangle){
			figureList.remove(this.selectedFigure);
		}
		this.selectedFigure = null;
	}
	
	/**
	 * Create a definitive arc, but some conditions must be met.
	 * <p>
	 * This method gets the selected figure and set it as an origin figure.
	 * Then, with the inputed point, selects a new figure. 
	 * <br>
	 * One must be a transition and the other a place. If so, the arc figure is
	 * created. 
	 * @param point
	 */
	public synchronized void createDefinitiveArc(Point point){

		AbstractPetriNetFigure originFigure = this.selectedFigure;
		
		this.selectedFigure = null;
		
		selectFigure(point);
		if (this.selectedFigure != null){
			
			boolean originIsPlace = originFigure instanceof PlaceFigure;
			boolean originIsTransition = originFigure instanceof TransitionFigure;
			
			boolean selectedIsPlace = this.selectedFigure instanceof PlaceFigure;
			boolean selectedIsTransition = this.selectedFigure instanceof
					TransitionFigure;

			if (originIsPlace & selectedIsTransition){
				PlaceFigure place = (PlaceFigure) originFigure;
				TransitionFigure transition = (TransitionFigure) this.selectedFigure;
				
				figureList.add(new ArcFigure(place, transition, -1));
			}
			else if (originIsTransition & selectedIsPlace){
				PlaceFigure place = (PlaceFigure) this.selectedFigure;
				TransitionFigure transition = (TransitionFigure) originFigure;
				
				figureList.add(new ArcFigure(place, transition, +1));
			}
		}
	}
	
	/**
	 * Move selected figure to given point's coordinates.
	 * @param point
	 */
	public void moveSelected(Point point){
		this.selectedFigure.moveFigure(point);
	}
	
	/**
	 * @return selectedFigure
	 */
	public AbstractPetriNetFigure getSelectedFigure() {		
		return selectedFigure;
	}
	
	/**
	 * Returns if there is a figure that is considered selected or not.
	 * @return boolean
	 */
	public boolean hasSelectedFigure(){
		return (this.selectedFigure != null);
	}
	
	public synchronized void addFigure(AbstractPetriNetFigure f){
		this.figureList.add(f);
	}
	
	public synchronized void addFigure(List <AbstractPetriNetFigure> f){
		for (AbstractPetriNetFigure figure : f){
			addFigure(figure);
		}
	}

	/**
	 * Remove all passed figures from list.
	 * @param figures
	 */
	public synchronized void removeFigure(List<AbstractPetriNetFigure> figures){		
		figureList.removeAll(figures);
	}
	
	/**
	 * Removes a figure from the list.
	 * @param figure
	 */
	public synchronized void removeFigure(AbstractPetriNetFigure figure){		
		figureList.remove(figure);
	}
	
	/**
	 * Deletes the current selected figure.
	 */
	public synchronized void removeSelectedFigure(){
		figureList.remove(selectedFigure);
	}
	
	/**
	 * Switches the height and width of the figure (by inverting them by one
	 * another). Used by transitions.
	 */
	public void turnSelectedFigure(){
		selectedFigure.turnFigure();
	}
	
	public boolean contains(AbstractPetriNetFigure f){
		return figureList.contains(f);
	}

	public TemporaryArc getTemporaryArc() {
		return temporaryArc;
	}

	public void setTemporaryArc(TemporaryArc temporaryArc) {
		this.temporaryArc = temporaryArc;
	}
	
	/**
	 * Create a selection rectangle, if it does not exists already.
	 * @param e initial point
	 */
	public synchronized void beginMultipleSelection(Point e){
		
		if ( ! makingMultipleSelection() ){		
			this.selectedFigure = (new SelectionRectangle(e));
			
			this.figureList.add(selectedFigure);
		}				
	}
	
	/**
	 * Select all figures inside the Selection Rectangle.
	 * @param e initial point
	 */
	public void addFiguresToMultipleSelectionList(Point e){
		((SelectionRectangle) selectedFigure).selectMultiple(figureList);
	}
	
	/**
	 * Return true if multiple selection is being made, AND if the list
	 * of selected figures is empty.<br>
	 * False otherwise. 
	 * @return true or false
	 */
	public boolean makingMultipleSelection(){
		return (selectedFigure instanceof SelectionRectangle);
	}
	
	public synchronized void clearFigures(){
		figureList.clear();
	}
	
	public synchronized List<AbstractPetriNetFigure> getFigures(){
		return this.figureList;
	}
	
	/**
	 * Set encapsulation of all figures contained by box object, also add
	 * it to figure list.
	 * @param box
	 */
	public synchronized void encapsulateElements(NetBox box){
		
		for (AbstractPetriNetFigure f : box.getContainedFigures()){
			if (f.notEncapsulated()){ // already encapsulated?
				f.setEncapsulation(box);
			}
		}
		this.figureList.add(box);
	}
	
	/**
	 * Undo the encapsulation and delete the box (but not the figures in it).
	 * @param box
	 */
	public synchronized void undoEncapsulation(NetBox box){
		
		for (AbstractPetriNetFigure f : this.getFigures()){
			if (box.getContainedFigures().contains(f)){
				f.setEncapsulation(null);
			}
		}
		this.figureList.remove(box);
	}
	
}
