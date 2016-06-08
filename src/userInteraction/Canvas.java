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

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JRadioButton;
import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
 class Canvas {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Canvas window = new Canvas();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Canvas() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 458, 385);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnArchive = new JMenu("Archive");
		menuBar.add(mnArchive);
		
		JMenuItem mntmSaveNet = new JMenuItem("Save net");
		mnArchive.add(mntmSaveNet);
		
		JMenuItem mntmOpenNet = new JMenuItem("Open net");
		mnArchive.add(mntmOpenNet);
		
		JMenuItem mntmAbout = new JMenuItem("ABOUT");
		mnArchive.add(mntmAbout);
		
		JRadioButton rdbtnPlace = new JRadioButton("Place");
		menuBar.add(rdbtnPlace);
		
		JRadioButton rdbtnTransition = new JRadioButton("Transition");
		menuBar.add(rdbtnTransition);
		
		JRadioButton rdbtnArc = new JRadioButton("Arc");
		menuBar.add(rdbtnArc);
		
		JFormattedTextField frmtdtxtfldTimeStep = new JFormattedTextField();
		frmtdtxtfldTimeStep.setText("time step");
		menuBar.add(frmtdtxtfldTimeStep);
		
		JRadioButton btnBack = new JRadioButton("Back");
		menuBar.add(btnBack);
		
		JToggleButton btnPlayPause = new JToggleButton("Play/Pause");
		menuBar.add(btnPlayPause);
		
		JRadioButton btnStep = new JRadioButton("Step");
		menuBar.add(btnStep);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTree tree = new JTree();
		scrollPane.setRowHeaderView(tree);
	}

}
