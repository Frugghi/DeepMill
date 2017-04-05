package it.unibo.ai.didattica.mulino.engine;

import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.domain.State;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class TCPMulino {

	public static final int whiteSocket = 5800;
	public static final int blackSocket = 5801;
		
	private State.Checker player;
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	
	public TCPMulino (State.Checker checker) throws IOException {
		player = checker; 
		int tcpPort = 0;
		switch (player) {
			case WHITE:
				tcpPort = whiteSocket;
				break;
			case BLACK:
				tcpPort = blackSocket;
				break;
			default:
				System.exit(3);
		}
		serverSocket = new ServerSocket(tcpPort);
		connectionSocket = serverSocket.accept();
		System.out.println("Player " + player.toString() + " connected!");
		in = new ObjectInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
		out = new ObjectOutputStream(connectionSocket.getOutputStream());
	}
	
	
	public synchronized Action readAction() throws IOException, ClassNotFoundException {
		return (Action) in.readObject();
	}
	
	
	public synchronized void writeState(State aState) throws IOException {
		out.writeObject(aState);
	}
	



	
}
