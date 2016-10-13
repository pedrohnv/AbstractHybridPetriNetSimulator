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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;

/**
 * This class is a figure to represent an arc that is being created (mouse
 * dragging in the GUI).
 */
public class TemporaryArc extends AbstractPetriNetFigure {
	
	private static final long serialVersionUID = 2353314861731584693L;
	
	//Integer xOrigin, yOrigin, xDestiny, yDestiny;
	
	private Point destiny;

	public TemporaryArc(Point point) {
		this.setLocation(point);
		this.order = 2;
		moveFigure(point);
	}

	@Override
	public void draw(Graphics g) {
		((Graphics2D) g).draw(new Line2D.Double(this.getLocation(), this.destiny));
	}
	
	@Override
	public void moveFigure(Point p){
		this.destiny = p;
	}
	
	public void moveFigure(int x, int y) {
		moveFigure(new Point(x,y));
	}
	/**
	 * Set the destiny coordinates equal to the origin coordinate.
	 */
	public void nullify(){		
		this.destiny = this.getLocation();
	}

	public boolean notArc(){return false;}

	@Override
	public boolean validFigure() {		
		return true;
	}

	@Override
	public String toString() {
		return null;
	}
}
