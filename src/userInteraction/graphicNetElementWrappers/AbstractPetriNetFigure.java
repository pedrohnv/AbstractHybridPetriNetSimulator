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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An abstract representation of a Petri net figure element.
 */
public abstract class AbstractPetriNetFigure extends Rectangle implements 
		Comparable <AbstractPetriNetFigure> {

	private static final long serialVersionUID = 1898468588032135041L;

	private static AtomicInteger counter = new AtomicInteger(0);
	
	private Integer index;

	/**
	 * Flags if the figure is inside a Petri Net Box.
	 */
	private boolean encapsulated = false;
	
	private NetBox enclosingFigure;
	
	/**
	 * natural ordering
	 */
	protected int order = 0;
		
	protected AbstractPetriNetFigure(){
		this.index = counter.incrementAndGet();
	}
	
	protected AbstractPetriNetFigure(int x, int y){
		this();
		this.setLocation(x, y);
	}
	
	/**
	 * Draws the component. This method will be called in the PaintComponent
	 * of the GraphicInteraction (a JPanel).
	 * @param g
	 */
	public abstract void draw(Graphics g);
	
	/**
	 * Update the figure's coordinates.
	 * @param newLocation
	 */
	public void moveFigure(Point newLocation){
		super.setLocation(newLocation);
	}
	
	/**
	 * Move the figure by the specified offset. 
	 * @param dx
	 * @param dy
	 */
	public void moveFigureByOffset(int dx, int dy){
		this.x += dx;
		this.y += dy;
	}
	
	/**
	 * This method determines if this figure was selected. It does so by
	 * looking the inputed coordinates, supposedly the mouse's coordinates.
	 * <p>
	 * Used to edit components, and also to move the figure in the panel.
	 * @param a
	 * @param b
	 */
	public boolean selected(Point p){
		/*
		 *  The number 2 appears dividing and multiply so that the figure is
		 *  drawn centered where the mouse is pointing.
		 */		
		int x = (int) (this.x - getWidth()/2);
		int y = (int) (this.y - getHeight()/2);		
		Point point = new Point(x, y);
		
		int w = (int) getWidth();
		int h = (int) getHeight();		
		Dimension dim = new Dimension(w, h);
		
		Rectangle rect = new Rectangle(point, dim);
		
		if (rect.contains(p)){
			return true;
		}
		else {
			return false;	
		}				
	}
	
	public boolean selected(int x, int y){
		Point p = new Point(x, y);
		return (selected(p));
	}
	
	public Integer getIndex() {return index;}
	
	public String getIndexAsString() {return String.valueOf(index);}
	
	/**
	 * Switches the height and width of the figure (by inverting them by one
	 * another).
	 */
	public void turnFigure(){
		int h = (int) this.getHeight();
		int w = (int) this.getWidth();
		
		this.setSize(h, w);
	}
	
	/**
	 * Verify if the figure is valid. That usually means it has no null element.
	 */
	public abstract boolean validFigure();
		
	@Override
	public int compareTo(AbstractPetriNetFigure o) {			
		int comparator = this.order - o.order;
		
		if (comparator != 0){
			return comparator;
		}
		else {
			return this.index - o.index;
		}
	}

	@Override
	public int hashCode() {
	    return index == null ? 0 : index;
	}
	
	/**
	 * Identifies if two figures are equals by their index.
	 * @param other
	 * @return boolean
	 * @throws NullPointerException if argument is null.
	 */
	@Override
	public boolean equals(Object other){		
		return ( (other instanceof AbstractPetriNetFigure) &&
				this.index.equals( ((AbstractPetriNetFigure) other).index));
	}	
	
	/**
	 * Abstract Petri Net Figure toString method must return all information
	 * to save and open files.
	 */
	@Override
	public String toString(){
		String info = this.x + ";" + this.y + ";";
		info += this.getWidth() + ";" + this.getHeight() + ";";
		return info;
	}
	
	/**
	 * Encapsulate a figure. If enclosingFigure is null, set encapsulation to false.
	 * @param enclosingFigure
	 */
	public void setEncapsulation(NetBox enclosingFigure){
		this.enclosingFigure = enclosingFigure;
		
		if (enclosingFigure != null){
			this.encapsulated = true;
		}
		else {
			this.encapsulated = false;
		}
	}
		
	/**
	 * Return false if this figure is under encapsulation.
	 */
	public boolean notEncapsulated(){
		return !(this.encapsulated);
	}

	/**
	 * Get the location of the enclosing figure that is not (by other figure)
	 * enclosed.
	 * @return upper most enclosing figure's Point coordinates.
	 */
	public Point getEnclosingFigureCoordinate() {
		if (enclosingFigure.notEncapsulated()){
			return enclosingFigure.getLocation();
		}
		else {
			return enclosingFigure.getEnclosingFigureCoordinate();
		}
	}
	
}
