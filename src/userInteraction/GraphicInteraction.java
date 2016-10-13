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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import enums.EditingMode;
import hybridPetriNet.Evolution;
import hybridPetriNet.SimulationRun;
import hybridPetriNet.arcs.Arc;
import hybridPetriNet.petriNets.PetriNet;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.Transition;
import userInteraction.graphicNetElementWrappers.AbstractPetriNetFigure;
import userInteraction.graphicNetElementWrappers.NetBox;
import userInteraction.graphicNetElementWrappers.SelectionRectangle;
import userInteraction.graphicNetElementWrappers.arcFigures.ArcFigure;
import userInteraction.graphicNetElementWrappers.placeFigures.PlaceFigure;
import userInteraction.graphicNetElementWrappers.transitionFigures.TransitionFigure;
import userInteraction.helperPanels.SimulationOptionsPanel;
import userInteraction.helperPanels.elementEditingPopup.ElementEditingPopupMenu;
import userInteraction.helperPanels.elementEditingPopup.MultipleSelectionPopupMenu;
import userInteraction.helperPanels.elementEditingPopup.PetriNetObjectPopupMenu;
import userInteraction.helperPanels.elementEditingPopup.arcEditing.ArcEditingPopupMenu;
import userInteraction.helperPanels.elementEditingPopup.placeEditing.PlaceEditingPopupMenu;
import userInteraction.helperPanels.elementEditingPopup.transitionEditing.TransitionEditingPopupMenu;

import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * The main GUI of the program.
 */
public class GraphicInteraction extends JPanel implements MouseListener,
MouseMotionListener {

	private static final long serialVersionUID = 7184698354720648149L;

	public static NetFigureManager figureManager = new NetFigureManager();
	
	private EditingMode editingMode = EditingMode.SELECT;
	
	private JFrame frame = new JFrame(); // the main application frame
	
	private JSlider slider = new JSlider();
	
	private volatile JScrollPane scroller;
	
	private boolean playSimulation = false;
	
	private PetriNet net;
	
	private Thread simulationThread = new Thread();
	
	private JTextField textFieldCurrentTime;
	
	/**
	 * A popup menu to edit each Petri net Element.
	 */
	private ElementEditingPopupMenu popupMenu;
	
	/**
	 * A panel to display and edit the simulation options.
	 */
	private SimulationOptionsPanel optionsPanel = new SimulationOptionsPanel();

	// getters	
	public boolean playSimulation(){
		return playSimulation;		
	}
	
	public EditingMode editingMode(){
		return editingMode;		
	}
	
	public long simulationSpeed(){
		return slider.getValue();		
	}
	
	public PetriNet netToSimulate(){
		return net;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            try {
	                new GraphicInteraction();

	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    });
	}

	/**
	 * Create the application.
	 */
	public GraphicInteraction() {
		this.setLayout(new BorderLayout(0, 0));
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		frame.setBackground(Color.GRAY);
	    frame.getContentPane().setBackground(Color.WHITE);
	    frame.setBounds(100, 100, 1200, 600);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    this.setBackground(Color.WHITE);
	    this.setForeground(Color.BLACK);
	    
	    this.addToolBars();
	    
	    // create a scroll pane that contains this panel
	    scroller = new JScrollPane(this);
	    scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.
	    										VERTICAL_SCROLLBAR_AS_NEEDED);
	    scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.
	    										HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    scroller.setWheelScrollingEnabled(true);
	    
	    // scrolling speed
	    scroller.getVerticalScrollBar().setUnitIncrement(16);
	    scroller.getHorizontalScrollBar().setUnitIncrement(16);	    
	    
	    // put the scroll pane in the content pane of the main frame
	    frame.getContentPane().add(scroller, BorderLayout.CENTER);
	    frame.setVisible(true);
	}
	
	/**
	 * Layout tool bars, buttons and other components in the main frame.
	 */
	private void addToolBars(){
		JToolBar toolBar = new JToolBar();
	    frame.getContentPane().add(toolBar, BorderLayout.NORTH);
	    toolBar.setFloatable(false);
	    toolBar.setBackground(Color.WHITE);
	    
	    JToolBar toolBar_archive = new JToolBar();
	    toolBar_archive.setFloatable(false);
	    toolBar_archive.setBackground(Color.WHITE);
	    toolBar.add(toolBar_archive);
	    
	    JButton button_New = new JButton("");
	    button_New.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		newButtonAction();
	    	}
	    });
	    button_New.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/new.png")));
	    button_New.setToolTipText("New Petri Net");
	    button_New.setBackground(Color.WHITE);
	    toolBar_archive.add(button_New);
	    
	    JButton button_Open = new JButton("");
	    button_Open.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		openButtonAction();
	    	}
	    });
	    button_Open.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/open.png")));
	    button_Open.setToolTipText("Load");
	    button_Open.setBackground(Color.WHITE);
	    toolBar_archive.add(button_Open);
	    
	    JButton button_Save = new JButton("");
	    button_Save.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		saveButtonAction();
	    	}
	    });
	    button_Save.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/save.png")));
	    button_Save.setToolTipText("Save");
	    button_Save.setBackground(Color.WHITE);
	    toolBar_archive.add(button_Save);
	    
	    JButton button_About = new JButton("");
	    button_About.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		aboutButtonAction();	    		
	    	}
	    });
	    button_About.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/about.png")));
	    button_About.setToolTipText("ABOUT");
	    button_About.setBackground(Color.WHITE);
	    toolBar_archive.add(button_About);
	    
	    JToolBar toolBar_editor = new JToolBar();
	    toolBar_editor.setBackground(Color.WHITE);
	    toolBar.add(toolBar_editor);
	    
	    JButton button_Select = new JButton("");
	    button_Select.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		selectButtonAction();	    		
	    	}
	    });
	    button_Select.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/select.png")));
	    button_Select.setToolTipText("select item");
	    button_Select.setBackground(Color.WHITE);
	    toolBar_editor.add(button_Select);
	    
	    JButton button_Arc = new JButton("");
	    button_Arc.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		arcButtonAction();
	    	}
	    });
	    button_Arc.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/arc.png")));
	    button_Arc.setToolTipText("new Arc");
	    button_Arc.setBackground(Color.WHITE);
	    toolBar_editor.add(button_Arc);
	    
	    JButton button_Place = new JButton("");
	    button_Place.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		placeButtonAction();
	    	}
	    });
	    button_Place.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/place.png")));
	    button_Place.setToolTipText("new Place");
	    button_Place.setBackground(Color.WHITE);
	    toolBar_editor.add(button_Place);
	    
	    JButton button_Transition = new JButton("");
	    button_Transition.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		transitionButtonAction();
	    	}
	    });
	    button_Transition.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/transition.png")));
	    button_Transition.setToolTipText("new Transition");
	    button_Transition.setBackground(Color.WHITE);
	    toolBar_editor.add(button_Transition);
	    
	    JToolBar toolBar_simulation = new JToolBar();
	    toolBar_simulation.setBackground(Color.WHITE);
	    toolBar.add(toolBar_simulation);
	    
	    JButton button_Stop = new JButton("");
	    button_Stop.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		stopButtonAction();
	    		editingMode = EditingMode.SELECT;
	    		Evolution.reset();
	    	}
	    });
	    button_Stop.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/stop.png")));
	    button_Stop.setToolTipText("Reset time and iteration to zero. No change"
	    		+ "to places' markings.");
	    button_Stop.setBackground(Color.WHITE);
	    toolBar_simulation.add(button_Stop);
	    
	    JButton button_Backward = new JButton("");
	    button_Backward.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		backwardButtonAction();
	    	}
	    });
	    button_Backward.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/step backward.png")));
	    button_Backward.setToolTipText("Step Backward");
	    button_Backward.setBackground(Color.WHITE);
	    toolBar_simulation.add(button_Backward);
	    
	    JButton button_Pause = new JButton("");
	    button_Pause.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		pauseButtonAction();
	    	}
	    });
	    button_Pause.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/pause.png")));
	    button_Pause.setToolTipText("Pause");
	    button_Pause.setBackground(Color.WHITE);
	    toolBar_simulation.add(button_Pause);
	    
	    JButton button_Play = new JButton("");
	    button_Play.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) { 
	    		playButtonAction();	    		
	    	}
	    });
	    button_Play.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/play.png")));
	    button_Play.setToolTipText("Play");
	    button_Play.setBackground(Color.WHITE);
	    toolBar_simulation.add(button_Play);
	    
	    JButton button_Forward = new JButton("");
	    button_Forward.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		forwardButtonAction();
	    	}
	    });
	    button_Forward.setIcon(new ImageIcon(GraphicInteraction.class.getResource(
	    		"/userInteraction/icons/step forward.png")));
	    button_Forward.setToolTipText("Step Forward");
	    button_Forward.setBackground(Color.WHITE);
	    toolBar_simulation.add(button_Forward);
	    
	    Component horizontalStrut = Box.createHorizontalStrut(10);
	    toolBar_simulation.add(horizontalStrut);
	    
	    JButton btnOptions = new JButton("Simulation Options");
	    toolBar_simulation.add(btnOptions);
	    btnOptions.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		optionsButtonAction();
	    	}
	    });
	    btnOptions.setFont(new Font("Tahoma", Font.PLAIN, 12));
	    
	    Component horizontalStrut_1 = Box.createHorizontalStrut(10);
	    toolBar_simulation.add(horizontalStrut_1);
	    
	    JLabel lblSimulationSpeed = new JLabel("Simulation Speed:");
	    toolBar_simulation.add(lblSimulationSpeed);
	    slider.setToolTipText("The speed of the simulatiom,"
	    					+ " defines the pause time between firings"
	    					+ " from 1000 to 0 milliseconds.");
	    
	    toolBar_simulation.add(slider);
	    slider.setMinorTickSpacing(50);
	    slider.setMaximum(1000);
	    slider.setValue(500);
	    slider.setMajorTickSpacing(100);
	    slider.setPaintTicks(true);
	    slider.setInverted(true);	 
	    
	    Component horizontalStrut_2 = Box.createHorizontalStrut(10);
	    toolBar_simulation.add(horizontalStrut_2);
	    
	    JLabel lblCurrentTime = new JLabel("Current Time:");
	    toolBar_simulation.add(lblCurrentTime);
	    
	    textFieldCurrentTime = new JTextField();
	    textFieldCurrentTime.setHorizontalAlignment(SwingConstants.LEFT);
	    textFieldCurrentTime.setEditable(false);
	    toolBar_simulation.add(textFieldCurrentTime);
	    textFieldCurrentTime.setColumns(5);
	}
	
	/*
	 * The methods bellow are called by event listeners, like button
	 * actions.
	 */		
	/**
	 * New file button.
	 */
	private void newButtonAction(){
		int selection = JOptionPane.showConfirmDialog(frame, "Delete all"
								+ " current drawings?", "New Petri Net",
											JOptionPane.YES_NO_OPTION);
		if (selection == JOptionPane.YES_OPTION){
			figureManager.clearFigures();
		    
		    this.setPreferredSize(null); // reset this panel to default size
		    this.revalidate();
		    
		    Evolution.reset();
		    repaint();
		}
	}
	
	/**
	 * Open file button. External places' csv file must be reloaded into each,
	 * manually. 
	 */
	private void openButtonAction(){
		/* loading a net will put it on top of the existing one, not excluding
		what has already been placed. */
		
		FileOpenSave fileManager = new FileOpenSave();

		// open a file chooser, call processor
		int returnVal = fileManager.showOpenDialog(this);
				   
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	        File file = fileManager.getSelectedFile();
	        try {
				figureManager.addFigure(fileManager.openNetFile(file));
	        }
	        catch (Exception e){
	        	e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Could not open file");
			}
	        repaint();
	    }
	}
	
	/**
	 * Save file button.
	 */
	private void saveButtonAction(){
		figureManager.nullifySelectedFigure();

		FileOpenSave fileManager = new FileOpenSave();

		// open a file chooser, call processor
		int returnVal = fileManager.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fileManager.getSelectedFile();
				fileManager.saveNetFile(figureManager.getFigures(), file);
			}
			catch (Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Could not save file");
			}
		}			
	}

	/**
	 * About info button.
	 */
	private void aboutButtonAction(){
		JOptionPane.showMessageDialog(null, "Abstract Hybrid Petri Net "
				+ "Simulator.\nDeveloped by Pedro Henrique Nascimento "
				+ "Vieira under the MIT license, 2016.\nGithub");
	}
	
	private void selectButtonAction(){
		if (editingMode != EditingMode.SIMULATE){
			editingMode = EditingMode.SELECT;
			
			this.adequateCursor();		    
		}		
	}
	
	private void arcButtonAction(){
		if (editingMode != EditingMode.SIMULATE){
			editingMode = EditingMode.ARC;
			
			this.adequateCursor();
		}		
	}

	private void placeButtonAction(){
		if (editingMode != EditingMode.SIMULATE){
			editingMode = EditingMode.PLACE;

			this.adequateCursor();
		}
	}

	private void transitionButtonAction(){
		if (editingMode != EditingMode.SIMULATE){
			editingMode = EditingMode.TRANSITION;

			this.adequateCursor();	
		}
	}
	
	private void stopButtonAction(){
		/*
		 * If the simulation is stopped, the csv file used by the external
		 * places should be reloaded. Currently it must be done manually.
		 * TODO make it automatic.
		 */
		editingMode = EditingMode.SELECT;

		this.adequateCursor();
		
		playSimulation = false;
		
		if (simulationThread.isAlive()){
			simulationThread.interrupt();
		}
		repaint();
	}
	
	private void backwardButtonAction(){
		// do one iteration backwards over the net
		this.net = buildNetFromFigures();
		
		// use fourth order Runge-Kutta?
		net.setFourthOrderRungeKutta(optionsPanel.useFourthOrderRungeKutta());
		
		SimulationGraphicRun.backwardIteration(this.net);
		
		repaint();	
	}
	
	private void pauseButtonAction(){
		playSimulation = false;		
	}
	
	private void playButtonAction(){
		editingMode = EditingMode.SIMULATE;

		this.adequateCursor();
		
		this.net = buildNetFromFigures();
		
		if (SimulationOptionsPanel.mustGenerateCsv()){
			// will only generate the csv file
			JOptionPane.showMessageDialog(null, "A csv file will be generated.");
			
			SimulationRun.RunProgram(this.net);
			
			JOptionPane.showMessageDialog(null, "Simulation done!");
			
			repaint();
		}
		else {
			// will do an animated simulation
			playSimulation = true;
			
			// check if simulation is not already running
			if (! simulationThread.isAlive()){
				// start new thread that will execute the simulation
				simulationThread = new Thread(new SimulationGraphicRun(this));
				
				simulationThread.start();
			}
		}
	}
	
	private void forwardButtonAction(){
		// do one iteration over the net	    		
		this.net = buildNetFromFigures();
		
		// use fourth order Runge-Kutta?
		if (optionsPanel != null){
			net.setFourthOrderRungeKutta(optionsPanel.useFourthOrderRungeKutta());
		}
		else {
			net.setFourthOrderRungeKutta(false);
		}
		
		SimulationGraphicRun.forwardIteration(this.net);
		
		repaint();
	}
	
	private void optionsButtonAction(){		
		optionsPanel.showPanel();
	}
		
	/**
	 * See if given figure is outside of this Panel bounds. If affirmative,
	 * resize this Panel.<p>
	 * If the figure is outside of the bounds in a negative direction,
	 * it's (center) is reset to coordinate 20. No resize happens.
	 * @param figure
	 */
	private void resizePanel(AbstractPetriNetFigure f){
		boolean outside = false;
		
		int increment = 250; // an offset
		int width = this.getWidth();
	    int height = this.getHeight();
		
	    // for X coordinate	    
		if (f.x < 0){
    		f.x = 20;
    		
    	} else if ((f.getX() > width)){
    		width = f.x + increment;
    		outside = true;
    	}
		
	    // for Y coordinate    		    	
    	if (f.getY() < 0){	    			    	
    		f.y = 20;
    		
    	} else if (f.getY() > height){
    		height = f.y + increment;
    		outside = true;
    	}
    	
    	// need to resize?
    	if (outside) {
    		this.setPreferredSize(new Dimension(width, height));
    		this.revalidate();
    	}
	}
	
	/**
	 * Set the cursor icon according to the current editing mode.
	 */
	private void adequateCursor(){		    
        Cursor cursor = editingMode.getAssociatedCursor();
        		
		this.setCursor(cursor);
	}
		
	/**
	 * Display a popup menu according to the figure in the mouse event location,
	 * if any.
	 * @param e mouse event
	 */
	private void displayPopupMenu(MouseEvent e){
		if (figureManager.getSelectedFigure() instanceof PlaceFigure){
			popupMenu = new PlaceEditingPopupMenu();
		}
		else if (figureManager.getSelectedFigure() instanceof TransitionFigure){
			popupMenu = new TransitionEditingPopupMenu();
		}
		else if (figureManager.getSelectedFigure() instanceof ArcFigure){
			popupMenu = new ArcEditingPopupMenu();
		}
		else if (figureManager.getSelectedFigure() instanceof SelectionRectangle){
			popupMenu = new MultipleSelectionPopupMenu();
		}
		else if (figureManager.getSelectedFigure() instanceof NetBox){
			popupMenu = new PetriNetObjectPopupMenu();
		}
		popupMenu.show(GraphicInteraction.this, e.getX(), e.getY());
	}

	/**
	 * Loop through all figures in the figure manager and build a net.
	 * @return PetriNet
	 */
	public PetriNet buildNetFromFigures(){
		// build Petri net
		List<Place> placeList = new ArrayList<Place>();	    		
		List<Transition> transitionList = new ArrayList<Transition>();
		List<Arc> arcList = new ArrayList<Arc>();
		
		List <AbstractPetriNetFigure> figures = figureManager.getFigures();
		
		for (AbstractPetriNetFigure f : figures){
			/*
			 *  Cast figure in list to get it's element.
			 *  
			 *  Could have built an abstract getElement() method in the
			 *  AbstractPetriNetFigure.
			 *  
			 *  But the current way is more explicit on which kind
			 *  of element you are getting. This also would require
			 *  the creation of an AbstractPetriNetElement which
			 *  Place, Transition, and Arcs extends...
			 */
			if (f instanceof PlaceFigure){
				placeList.add(((PlaceFigure) f).getPlace());
			}
			else if (f instanceof TransitionFigure){
				transitionList.add(((TransitionFigure) f).getTransition());
			}
			else if (f instanceof ArcFigure){
				arcList.add(((ArcFigure) f).getArc());
			}
		}
		return (new PetriNet(placeList, transitionList, arcList));
	}
		
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		List<AbstractPetriNetFigure> figures = figureManager.getFigures();
		
	    List<AbstractPetriNetFigure> invalidFigures = 
	    		new ArrayList<AbstractPetriNetFigure>();
	    
	    g.clearRect(0, 0, getWidth(), getHeight());
	    
	    synchronized (figures){
	    	for (AbstractPetriNetFigure f : figures) {
		    	if (f.validFigure()){
		    		
		    		if (f.notEncapsulated()){
		    			this.resizePanel(f);
			    		f.draw(g);
		    		}
		    	}
		    	else {
		    		invalidFigures.add(f);
		    	}
		    }
	    	figureManager.removeFigure(invalidFigures);
	    }
	    // update current time text
	    textFieldCurrentTime.setText( String.valueOf(Evolution.getTime()) );
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)){

			switch(editingMode){
			case ARC:
				break;
				
			case PLACE:
				figureManager.addFigure( new PlaceFigure(e.getPoint()) );
				break;
				
			case SELECT:
				break;
				
			case SIMULATE:
				break;
				
			case TRANSITION:
				figureManager.addFigure( new TransitionFigure(e.getPoint()) );
				break;
						
			}
			figureManager.nullifySelectedFigure();
		}		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) &&
				(figureManager.getTemporaryArc() != null)){
			figureManager.createDefinitiveArc(e.getPoint());
			
			figureManager.nullifyTemporaryArc();
		}		
		else if (SwingUtilities.isRightMouseButton(e) &&  
				figureManager.hasSelectedFigure()){

			displayPopupMenu(e);
		}
		else if(figureManager.makingMultipleSelection()){
			figureManager.addFiguresToMultipleSelectionList(e.getPoint());
		}
		
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (figureManager.hasSelectedFigure()){
			
			switch(editingMode){
			case ARC:
				if (figureManager.getTemporaryArc() == null){
					// create temporary arc
					figureManager.createTemporaryArc(e.getPoint());
				}
				else {
					// move temporary arc
					figureManager.getTemporaryArc().moveFigure(e.getPoint());
				}
				break;
				
			case PLACE:
				break;
				
			case SELECT:					
				if (SwingUtilities.isLeftMouseButton(e)){
					
					figureManager.moveSelected(e.getPoint());
	    		}
				break;
				
			case SIMULATE:
				break;
				
			case TRANSITION:
				break;
			}			
		}
		else {
			figureManager.beginMultipleSelection(e.getPoint());
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (! figureManager.makingMultipleSelection()){		
			figureManager.nullifySelectedFigure();
			
			figureManager.selectFigure(e.getPoint());
		}
	}

	// unused mouse events	
	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
