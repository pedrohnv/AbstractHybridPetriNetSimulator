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
package userInteraction.graphicNetElementWrappers.arcFigures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

import enums.ArcType;
import hybridPetriNet.arcs.Arc;
import hybridPetriNet.arcs.InhibitorArc;
import hybridPetriNet.arcs.TestArc;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.Transition;
import userInteraction.GraphicInteraction;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;
import userInteraction.graphicNetElementWrappers.placeFigures.PlaceFigure;
import userInteraction.graphicNetElementWrappers.transitionFigures.TransitionFigure;
import utilities.Helper;
import utilities.LineIterator;

public class ArcFigure extends AbstractPetriNetFigure {

	private static final long serialVersionUID = -3219215206602160017L;

	private Arc arc;
	
	private PlaceFigure placeFigure;
	
	private TransitionFigure transitionFigure;
	
	private Line2D.Double line;
	
	/** 
	 * Parameter are figures and a direction.
	 * @param place
	 * @param transition
	 * @param direction determines the arc's weight sign and, hence, if from
	 * place to transition or the other way.
	 */
	public ArcFigure(PlaceFigure placeFigure, TransitionFigure transitionFigure,
			int direction, ArcType type) {	
		super();
		this.order = 2;
		this.placeFigure = placeFigure;
		this.transitionFigure = transitionFigure;
		
		switch(type){
		case INHIBITOR:
			this.arc = new InhibitorArc(transitionFigure.getTransition(), 
					placeFigure.getPlace());
			break;
			
		case NORMAL:
			if (direction > 0){
				this.arc = new Arc(transitionFigure.getTransition(), 
						placeFigure.getPlace());
			}
			else {
				this.arc = new Arc(placeFigure.getPlace(),
						transitionFigure.getTransition());
			}
			break;
			
		case TEST:
			this.arc = new TestArc(transitionFigure.getTransition(),
					placeFigure.getPlace());
			break;			
		}
	}
		
	/** 
	 * Parameter are figures and a direction.
	 * @param place
	 * @param transition
	 * @param direction determines the arc's weight sign and, hence, if from
	 * place to transition or the other way.
	 */
	public ArcFigure(PlaceFigure placeFigure, TransitionFigure transitionFigure,
			int direction) {	
		this(placeFigure, transitionFigure, direction, ArcType.NORMAL);
	}
		
	@Override
	public void draw(Graphics g) {
		// get center of each figure
		Point2D pointPlace = null;
		Point2D pointTransition = null;
		
		boolean placeNotEncapsulated = placeFigure.notEncapsulated();
		boolean transitionNotEncapsulated = transitionFigure.notEncapsulated();
		
		if (placeNotEncapsulated){
			int xPlace = placeFigure.x;
			int yPlace = placeFigure.y;
			pointPlace = new Point2D.Double(xPlace, yPlace);
		}
		else {
			pointPlace = this.placeFigure.getEnclosingFigureCoordinate();
		}
		
		if (transitionNotEncapsulated){
			int xTransition = transitionFigure.x;
			int yTransition = transitionFigure.y;
			pointTransition = new Point2D.Double(xTransition, yTransition);			
		}
		else {
			pointTransition = this.transitionFigure.getEnclosingFigureCoordinate();
		}
		
		// line from center to center
		this.line = new Line2D.Double(pointPlace, pointTransition);
		
		Point2D middlePoint = Helper.findMiddlePoint(pointPlace, pointTransition);
		
		int offset = 5;
		
		if (this.arc instanceof InhibitorArc){
			g.setColor(Color.GREEN);
			((Graphics2D) g).draw(this.line);
		}
		else if (this.arc instanceof TestArc){
			//creates a copy of the Graphics instance
	        Graphics2D g2d = (Graphics2D) g.create();

	        //set the stroke of the copy, not the original 
	        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, 
	        					BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	        g2d.setStroke(dashed);
	        
	        g2d.draw(this.line);
	        
	        //gets rid of the copy
	        g2d.dispose();	
		}
		else {
			// normal arc			
			if (arc.getWeight() < 0){
				g.setColor(Color.BLUE);
			}
			((Graphics2D) g).draw(this.line);
		}
		g.setColor(Color.BLACK);
		
		String weight = this.arc.getWeightString();
		
		if (placeNotEncapsulated || transitionNotEncapsulated){
			g.drawString(weight, (int) middlePoint.getX(), (int) middlePoint.getY()
					+ offset*3);
		}		
	}
		
	public Arc getArc() {return this.arc;}
	
	public void changeArc(Arc newArc){
		this.arc = newArc;
		// evaluate the weight string
		this.arc.iterationUpdate();
	}
		
	/**
	 * See if given coordinates are points of the arc line.
	 * <p>
	 * Based on what was found here <br>
	 * http://stackoverflow.com/questions/6339328/iterate-through-each-point-on
	 * -a-line-path-in-java
	 */
	@Override
	public boolean selected(Point p){		
		Point2D coordinates = p;
		Point2D current;
		
		// iterate through an offset line (to be easier to select the arc)		
		for (double yoffset = -7.0; yoffset < 7.0; yoffset += 1.0){
			
			for (double xoffset = -7.0; xoffset < 7.0; xoffset += 1.0){		
			Point2D p1 = new Point2D.Double(this.line.getX1()+ xoffset,
												this.line.getY1() + yoffset);
			
			Point2D p2 = new Point2D.Double(this.line.getX2() + xoffset,
												this.line.getY2() + yoffset);
			
			Line2D offsetLine = new Line2D.Double(p1, p2);
			
				for (Iterator<Point2D> it = new LineIterator(offsetLine);
						it.hasNext();) {		    
					current = it.next();
				
					if (current.equals(coordinates)){
						return true;
					}
				}
			}
		}
		return false;
	}	
	
	public boolean notArc(){return false;}
	
	@Override
	public void moveFigure(Point p){} // nothing happens
	
	@Override
	public boolean validFigure() {
		this.updateArc();
		
		boolean noNull = (placeFigure.validFigure() &&
				transitionFigure.validFigure());
		
		boolean exist = (GraphicInteraction.figureManager.contains(placeFigure)) &&
				(GraphicInteraction.figureManager.contains(transitionFigure));
		
		return (noNull && exist);
	}
	
	/** The place or transition objects may change (due to a change of
	 *  type). if so, the arc will refer to null objects. <p>
	 *  This method change the place and transition in the arc by getting
	 *  them from the associated figures.
	 */
	private void updateArc(){
		Place place = this.placeFigure.getPlace();
		Transition transition = this.transitionFigure.getTransition();
		
		this.arc.changePlace(place);
		this.arc.changeTransition(transition);
	}
	
	@Override
	public String toString() {
		// arcs do not need to call the super methods, because they do not have
		// a position nor size.
		String info = "Arc;";
		info += this.placeFigure.getPlace().getIndex() + ";";
		info += this.transitionFigure.getTransition().getIndex() + ";";
		info += this.getArc().toString();
		info += getIndexAsString() + ";";
		return info;
	}
}
