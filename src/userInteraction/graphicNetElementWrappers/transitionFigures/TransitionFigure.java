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
package userInteraction.graphicNetElementWrappers.transitionFigures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import hybridPetriNet.transitions.ContinuousTimeTransition;
import hybridPetriNet.transitions.TimeDelayedTransition;
import hybridPetriNet.transitions.Transition;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;

/**
 * Graphic object of a transition. <p>
 * It stores a transition.
 */
public class TransitionFigure extends AbstractPetriNetFigure {
		
	private static final long serialVersionUID = -1333221599609476918L;
	
	private Transition transition;
	
	public TransitionFigure(int x, int y, int w, int h, Transition transition){
		super(x, y);
		this.setSize(w, h);
		this.transition = transition;
		this.order = 1;
	}
	
	public TransitionFigure(Point p){
		this(p.x, p.y, 30, 12, new Transition("T"));
	}
	
	@Override
	public void draw(Graphics g) {		
		int x = (int) (getX() - getWidth()/2);
		int y = (int) (getY() - getHeight()/2);
		
		int w = (int) getWidth();
		int h = (int) getHeight();
		
		int offset = 2;
		if (this.transition instanceof ContinuousTimeTransition){
			g.setColor(Color.WHITE);
			g.fillRect(x, y, w, h);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, w, h);
		}		
		else if (this.transition instanceof TimeDelayedTransition){
			g.setColor(Color.BLUE);
			g.fillRect(x, y, w, h);
			g.setColor(Color.BLACK);
			double delayNumber = ((TimeDelayedTransition) this.transition).getDelay();
			String delay = String.valueOf(delayNumber);
			g.drawString(delay, x + offset*10, y);
		}
		
		else {
			g.fillRect(x, y, w, h);
		}
		String firingFunction = this.transition.getFiringFunctionString();		
		g.drawString(firingFunction, x, y + h + offset*10);
		
		String name = this.transition.getName();		
		g.drawString(name, x, y - offset);
	}
	
	public Transition getTransition(){return this.transition;}
	
	public void changeTransition(Transition newTransition){
		this.transition = newTransition;
	}

	public boolean notArc(){return true;}
	
	@Override
	public boolean validFigure() {
		return (getTransition() != null);
	}

	@Override
	public int compareTo(AbstractPetriNetFigure o) {
		return 0;
	}
	
	@Override
	public String toString() {
		String info = "Transition;";
		info += super.toString();
		info += this.getTransition().toString();
		info += getIndexAsString() + ";";
		return info;
	}
}
