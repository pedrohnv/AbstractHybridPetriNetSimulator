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
package userInteraction.graphicNetElementWrappers;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by the NetFigureManager. <p>
 * It have a list of figures that are inside it's bounds.
 * A move order to this figures will propagate to all figures in the list
 */
public class SelectionRectangle extends AbstractPetriNetFigure {

	private static final long serialVersionUID = -8170762894025472471L;
	
	private List <AbstractPetriNetFigure> containedFigures =
			new ArrayList <AbstractPetriNetFigure>();
		
	private Point endPoint;	
	
	public SelectionRectangle(Point p) {
		super(p.x, p.y);
		endPoint = p;
		this.order = 4;
	}
	
	@Override
	public void moveFigure(Point p){
		/*
		 * TODO works as intended only when moving in positive directions...
		 */
		if (containedFigures.isEmpty()){
			endPoint = p; // update the end point
			int w = endPoint.x - this.x;
			int h = endPoint.y - this.y;
			this.setSize(w, h);
		}
		else {
			// calculate the offset from p to endPoint
			int dx = p.x - this.getLocation().x;
			int dy = p.y - this.getLocation().y;
			
			this.setLocation(p);
						
			// move all contained figures by offset
			for (AbstractPetriNetFigure f : containedFigures){				
				f.moveFigureByOffset(dx, dy);
			}
		}
		
	}
	
	/**
	 * Move all contained figures.
	 * @param p
	 */
	public void moveContainedFigures(Point p){
		for (AbstractPetriNetFigure f : containedFigures){
			f.moveFigureByOffset(p.x, p.y);
		}
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		 
	    Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, 
	        				BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	    g2d.setStroke(dashed);
	    
	    g2d.draw(this);
	    
	    g2d.dispose();
	}

	@Override
	public boolean validFigure() {
		return true;
	}
	
	/**
	 * Loop through the figure list and check which figures are inside the
	 * bounds of the SelectedFigures.<p>
	 * Add those figures to the contained figures list (attribute).
	 * @param figureList
	 */
	public void selectMultiple(List<AbstractPetriNetFigure> figureList){
		
		if (containedFigures.isEmpty()){
			for (AbstractPetriNetFigure f : figureList){
				
				if (super.contains(f) && ! this.equals(f) && f.notEncapsulated()){
					// will not add itself and figures encapsulated
					containedFigures.add(f);
				}
			}	
		}
		
	}
	
	public List <AbstractPetriNetFigure> getContainedFigures(){
		return this.containedFigures;
	}
	
	@Override
	public String toString() {
		return null;
	}
}
