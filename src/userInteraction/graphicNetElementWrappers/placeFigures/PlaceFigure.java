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
package userInteraction.graphicNetElementWrappers.placeFigures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.DecimalFormat;

import hybridPetriNet.places.ContinuousPlace;
import hybridPetriNet.places.ExternalPlace;
import hybridPetriNet.places.Place;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;

/**
 * A graphic object of a place.
 * <p>
 * It stores a referenced place.
 */
public class PlaceFigure extends AbstractPetriNetFigure {
	
	private static final long serialVersionUID = 4550953748196017353L;
	
	private Place place;
	
	public PlaceFigure(int x, int y, int w, int h, Place place){
		super(x, y);
		this.setSize(w, h);
		this.place = place;
		this.order = 0;
	}
	
	public PlaceFigure(Point p, Place place){
		this(p.x, p.y, 30, 30, place);
	}	
	
	public PlaceFigure(Point p){
		this(p, new Place("P"));
	}
		
	public Place getPlace() {
		return place;
	}
	
	public void changePlace(Place newPlace){
		this.place = newPlace;
	}
	
	public int getDiameter(){return (int) this.getHeight();}
	
	public int getRadius(){return this.getDiameter()/2;}

	@Override
	public void draw(Graphics g) {
		int r = getRadius();
		int d = getDiameter();
		int x = this.x - r;
		int y = this.y - r;
		
		g.setColor(Color.WHITE);
		g.fillOval(x, y, d, d);		
		
		if (this.place instanceof ContinuousPlace) {			
			int r2 = r/2;
			int d2 = d/2;
			// inner circle
			g.fillOval(x + r2, y + r2, d2, d2);
			g.setColor(Color.BLACK);
			g.drawOval(x + r2, y + r2, d2, d2);
		}
		else if (this.place instanceof ExternalPlace) {
			g.setColor(Color.BLACK);
			g.drawLine(x, y, x + d, y + d);
		}
		g.setColor(Color.BLACK);
		g.drawOval(x, y, d, d);
		
		int offset = 40;
		
		DecimalFormat df = new DecimalFormat("#.###");
		
		String markings = String.valueOf( df.format(place.getMarkings()) );		
		g.drawString(markings, x, y + offset);
		
		String variable = place.getVariableName();		
		g.drawString(variable, x + offset, y + offset/2);
		
		String placeName = place.getName();		
		g.drawString(placeName, x, y);
	}
	
	public boolean notArc(){return true;}
	
	@Override
	public boolean validFigure() {
		return (getPlace() != null);
	}

	@Override
	public String toString() {
		String info = "Place;";
		info += super.toString();
		info += this.getPlace().toString();
		info += getIndexAsString() + ";";
		return info;
	}
}
