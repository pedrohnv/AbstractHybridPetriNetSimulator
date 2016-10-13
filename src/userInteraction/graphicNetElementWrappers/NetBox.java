package userInteraction.graphicNetElementWrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to encapsulate many components. Functions like an object net.
 */
public class NetBox extends AbstractPetriNetFigure {

	private static final long serialVersionUID = -5833523494769806793L;
	
	private String name = "Petri Net Object";
	
	private List <AbstractPetriNetFigure> containedFigures =
			new ArrayList <AbstractPetriNetFigure>();

	public NetBox(int x, int y, int w, int h,
			List <AbstractPetriNetFigure> containedFigures, String name) {
		super(x,y);
		this.setSize(w, h);
		this.setName(name);
		this.containedFigures = containedFigures;
		this.order = 5;
	}
	
	public NetBox(Point p, List <AbstractPetriNetFigure> containedFigures) {
		this(p.x, p.y, 50, 50, containedFigures, "Petri Net Object");		
	}
	
	public NetBox(Point p, List <AbstractPetriNetFigure> containedFigures,
			String name) {
		this(p.x, p.y, 50, 50, containedFigures, name);
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawRect(x, y, width, height);
		
		g.drawString(this.name, x, y + 60);
	}
	
	@Override
	public boolean selected(Point p){		
		if (this.contains(p)){
			return true;
		}
		else {
			return false;	
		}
	}

	@Override
	public boolean validFigure() {
		return (! this.getContainedFigures().isEmpty());
	}
		
	public List <AbstractPetriNetFigure> getContainedFigures(){
		return this.containedFigures;
	}
	
	@Override
	public String toString(){
		String info = "Net;";
		info += super.toString();
		info += this.getName() + ";";
		info += this.getIndexAsString() + ";";
		for (AbstractPetriNetFigure f : containedFigures){
			info += f.getIndex() + ";";
		}		
		return info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
}
