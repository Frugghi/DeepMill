package it.unibo.ai.didattica.mulino.gui;

import it.unibo.ai.didattica.mulino.domain.State;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Background extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private Image background;
	private Image white;
	private Image black;
	
	private State aState;

	private int xOffset = 12;
	private int yOffset = 36;
	private int xStep = 32;
	private int yStep = 32;
	
	public Background() {
		super();
		try{
			InputStream input = GUI.class.getResourceAsStream("resources/board.jpg");
			background = ImageIO.read(input);
			input = GUI.class.getResourceAsStream("resources/white.png");
			white = ImageIO.read(input);
			input = GUI.class.getResourceAsStream("resources/black.png");
			black = ImageIO.read(input);
		}
		catch(IOException ie)
		{
			System.out.println(ie.getMessage());
		}
	}
	
	@Override
	public void paint( Graphics g ) { 
	    super.paint(g);
	    
	    g.drawImage(background, 10, 30, null);
	    
	    HashMap<String,State.Checker> board = aState.getBoard();
		for (String pos : board.keySet()) {
			State.Checker checker = board.get(pos);
			int xPos = xOffset + (pos.charAt(0) - 'a')*xStep;
			int yPos = yOffset + (pos.charAt(1)-'0'-7)*(-1)*yStep;
			switch (checker) {
				case WHITE :
					g.drawImage(white, xPos, yPos, null);
					break;
				case BLACK:
					g.drawImage(black, xPos, yPos, null);
					break;
				default:
			}
		}
	}


	public State getaState() { return aState; }
	public void setaState(State aState) { this.aState = aState; }
	
	
	
	
	
	
}
