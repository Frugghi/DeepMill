package it.unibo.ai.didattica.mulino.gui;

import it.unibo.ai.didattica.mulino.domain.State;

import javax.swing.JFrame;

public class GUI {
	
	Background mainFrame;
	
	
	public GUI() {
		super();
		initGUI();
		show();
	}
	
	
	public void update(State aState) {
		mainFrame.setaState(aState);
		mainFrame.repaint();
	}
	
	
	private void initGUI() {
		mainFrame = new Background();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(280, 300);
	}
	
	private void show() {
        //Display the window.
        //mainFrame.pack();
        mainFrame.setVisible(true);
	}
	
	
	
	
	
}
