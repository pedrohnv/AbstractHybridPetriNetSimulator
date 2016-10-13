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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import enums.ArcType;
import enums.PlaceType;
import enums.TransitionType;
import hybridPetriNet.places.ContinuousPlace;
import hybridPetriNet.places.ExternalPlace;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.ContinuousTimeTransition;
import hybridPetriNet.transitions.TimeDelayedTransition;
import hybridPetriNet.transitions.Transition;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;
import userInteraction.graphicNetElementWrappers.NetBox;
import userInteraction.graphicNetElementWrappers.arcFigures.ArcFigure;
import userInteraction.graphicNetElementWrappers.placeFigures.PlaceFigure;
import userInteraction.graphicNetElementWrappers.transitionFigures.TransitionFigure;

/**
 * A class to open and save files. Extends JFileChooser.
 * <p> If new net elements are added, include their saving/opening routine
 * here.
 */
public class FileOpenSave extends JFileChooser{
	
	private static final long serialVersionUID = -5831871264086780162L;
	
	/**
	 * A map to link the index of a figure at the time of saving and it's new 
	 * index when it is loaded. <br>It will be used to create arcs.
	 */
	private Map <Integer, PlaceFigure> placeMap = 
			new HashMap <Integer, PlaceFigure>();
	
	private Map <Integer, TransitionFigure> transitionMap = 
			new HashMap <Integer, TransitionFigure>();
	
	/**
	 * To create Petri Net Objects.
	 */
	private Map <Integer, AbstractPetriNetFigure> figureMap =
			new HashMap <Integer, AbstractPetriNetFigure>();
	
	/**
	 * Prints each Figure of the net into a text file as comma separated
	 * values.
	 * @param figureList
	 * @param fileName
	 * @throws IOException
	 */
	public void saveNetFile(List <AbstractPetriNetFigure> figureList,
							File file) throws IOException{
		
		// arcs last because their place and transition must be created first
		Collections.sort(figureList);
		
		PrintWriter pw = new PrintWriter(file);
		
		for (AbstractPetriNetFigure f : figureList){
			if ((f != null) && (! f.toString().equals("null"))){
				pw.println(f);
			}
		}
		pw.close();
	}
		
	/**
	 * Read a file and recreate a net from it.
	 * @param file
	 * @return figureList
	 * @throws IOException
	 */
	public List <AbstractPetriNetFigure> openNetFile(File file) throws IOException{
		
		FileInputStream fis = new FileInputStream(file);
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 	
		String line; // line being read
		String type; // information being read
		int separatorPosition; // position of the ";" character
		
		List <AbstractPetriNetFigure> figureList = 
				new ArrayList <AbstractPetriNetFigure>();
		
		AbstractPetriNetFigure figure = null;
				
		while ((line = br.readLine()) != null) {
			separatorPosition = line.indexOf(";");			
			
			// the first node identifies which object is being read
			type = line.substring(0, separatorPosition);
			
			if (type.equals("Arc")){
				figure = readArc(line, figureList);
			}
			else if (type.equals("Place")){
				figure = readPlace(line);
			}
			else if (type.equals("Transition")){
				figure = readTransition(line);
			}
			else if (type.equals("Net")){
				figure = readNetBox(line, figureList);
			}
			
			figureList.add(figure);
		}
		br.close();
		
		return figureList;
	}
	
	/**
	 * Read the geometry information of the figure.
	 * @param line
	 * @return x, y, w, h, separator, next
	 */
	private int[] readGeometry(String line){
		int separator = line.indexOf(";") + 1;
		int next = line.indexOf(";", separator);
		String xStr = line.substring(separator, next);
		int x = Integer.valueOf(xStr);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String yStr = line.substring(separator, next);
		int y = Integer.valueOf(yStr);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String wStr = line.substring(separator, next);
		double w = Double.valueOf(wStr);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String hStr = line.substring(separator, next);
		double h = Double.valueOf(hStr);
		
		int[] geom = {x, y, (int) w, (int) h, separator, next};
		return geom;
	}
	
	/**
	 * Info is: Transition; x; y; w; h; type; name; index; firingFunctionString;
	 * priority; figureIndex;
	 * @param line
	 * @return transitionFigure 
	 */
	private AbstractPetriNetFigure readTransition(String line) {

		int[] geometry = readGeometry(line);
		int x = geometry[0];
		int y = geometry[1];
		int w = geometry[2];
		int h = geometry[3];
		int separator = geometry[4];
		int next = geometry[5];
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String type = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String name = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String indexString = line.substring(separator, next);
		int index = Integer.valueOf(indexString);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String firingFunction = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String priorityString = line.substring(separator, next);
		int priority = Integer.valueOf(priorityString);
		
		Transition transition = null;
		
		if (type.equals(TransitionType.DISCRETE.getLabel())){
			transition = new Transition(name, priority, firingFunction);
		}
		else if (type.equals(TransitionType.TIME_DELAYED.getLabel())){
			separator = line.indexOf(";", next) + 1;
			next = line.indexOf(";", separator);
			String delayString = line.substring(separator, next);			
			double delay = Double.valueOf(delayString);
			
			transition = new TimeDelayedTransition(name, priority, firingFunction, 
					delay);
		}
		else if (type.equals(TransitionType.CONTINUOUS.getLabel())){
			transition = new ContinuousTimeTransition(name, priority, firingFunction);
		}
		
		TransitionFigure transitionFigure = new TransitionFigure(x, y,
				w, h, transition);
		
		transitionMap.put(index, transitionFigure);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String figureIndexString = line.substring(separator, next);
		int id = Integer.valueOf(figureIndexString);
		
		figureMap.put(id, transitionFigure);
		
		return transitionFigure;
	}
	
	/**
	 * Info is: Place; x; y; w; h; type; name; index; variableName; markings;
	 * Capacity[0]; Capacity[1]; figureIndex;
	 * @param line
	 * @return placeFigure 
	 */
	private AbstractPetriNetFigure readPlace(String line) {
		int[] geometry = readGeometry(line);
		int x = geometry[0];
		int y = geometry[1];
		int w = geometry[2];
		int h = geometry[3];
		int separator = geometry[4];
		int next = geometry[5];
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String type = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String name = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String indexString = line.substring(separator, next);
		int index = Integer.valueOf(indexString);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String variableName = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String markingsString = line.substring(separator, next);
		double markings = Double.valueOf(markingsString);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String capacity0 = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String capacity1 = line.substring(separator, next);
				
		double[] capacity = {Double.valueOf(capacity0), Double.valueOf(capacity1)};
		
		Place place = null;
		
		if (type.equals(PlaceType.DISCRETE.getLabel())){			
			place = new Place(name, (int) markings, capacity, variableName);
		}
		else if (type.equals(PlaceType.CONTINUOUS.getLabel())){
			place = new ContinuousPlace(name, markings, capacity, variableName);
		}
		else if (type.equals(PlaceType.CONTINUOUS.getLabel())){
			place = new ExternalPlace(name); //TODO load input csv file
		}
		
		PlaceFigure placeFigure = new PlaceFigure(x, y, w, h, place);
		
		placeMap.put(index, placeFigure);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String figureIndexString = line.substring(separator, next);
		int id = Integer.valueOf(figureIndexString);
		
		figureMap.put(id, placeFigure);
				
		return placeFigure;
	}

	/**
	 * Info is: Arc; place.index; transition.index; type; weightString; figureIndex;
	 * @param line
	 * @param figureList 
	 * @return arcFigure 
	 */
	private AbstractPetriNetFigure readArc(String line,
			List<AbstractPetriNetFigure> figureList) {
		int separator = line.indexOf(";") + 1;
		int next = line.indexOf(";", separator);
		String placeIndexString = line.substring(separator, next);
		int placeIndex = Integer.valueOf(placeIndexString);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String transitionIndexString = line.substring(separator, next);
		int transitionIndex = Integer.valueOf(transitionIndexString);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String type = line.substring(separator, next);
				
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String weightString = line.substring(separator, next);
		
		PlaceFigure place = placeMap.get(placeIndex);
		TransitionFigure transition = transitionMap.get(transitionIndex);
		
		ArcType arcType = null;
				
		if (type.equals(ArcType.NORMAL.getLabel())){
			arcType = ArcType.NORMAL;
		}
		else if (type.equals(ArcType.INHIBITOR.getLabel())){
			arcType = ArcType.INHIBITOR;
		}
		else if (type.equals(ArcType.TEST.getLabel())){
			arcType = ArcType.TEST;
		}
		ArcFigure arcFigure = new ArcFigure(place, transition, +1, arcType);
						
		arcFigure.getArc().changeWeightString(weightString);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String figureIndexString = line.substring(separator, next);
		int id = Integer.valueOf(figureIndexString);
		
		figureMap.put(id, arcFigure);
		
		return arcFigure;
	}
	
	/**
	 * Info is: Net; x; y; w; h; ...figures' index...;
	 * @param line
	 * @return net object
	 */
	private AbstractPetriNetFigure readNetBox(String line,
			List<AbstractPetriNetFigure> figureList){
		int[] geometry = readGeometry(line);
		int x = geometry[0];
		int y = geometry[1];
		int w = geometry[2];
		int h = geometry[3];
		int separator = geometry[4];
		int next = geometry[5];
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		String name = line.substring(separator, next);
		
		separator = line.indexOf(";", next) + 1;
		next = line.indexOf(";", separator);
		int boxId = Integer.valueOf(line.substring(separator, next));
		
		int subFiguresId;
		
		List<AbstractPetriNetFigure> containedFigures = 
				new ArrayList<AbstractPetriNetFigure>(30);
		
		while(true){
			try {
				separator = line.indexOf(";", next) + 1;
				next = line.indexOf(";", separator);
				subFiguresId = Integer.valueOf(line.substring(separator, next));
				
				containedFigures.add(figureMap.get(subFiguresId));
			}
			catch (StringIndexOutOfBoundsException e){
				break;
			}
		}
		NetBox boxFig = new NetBox(x, y, w, h, containedFigures, name);
				
		for (int id : figureMap.keySet()){
			if (containedFigures.contains(figureMap.get(id))){
				figureMap.get(id).setEncapsulation(boxFig);
			}
		}
		
		figureMap.put(boxId, boxFig);
		
		return (boxFig);
	}

}
