package it.unibo.ai.didattica.mulino.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.mulino.actions.Action;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.engine.TCPMulino;

/**
 * Classe astratta di un client per il gioco del mulino
 * 
 * @author Federico Chesani, Andrea Galassi
 *
 */
public abstract class MulinoClient {

	private State.Checker player;
	private Socket playerSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public MulinoClient(State.Checker player) throws UnknownHostException, IOException {
		this.player = player;
		int port = 0;
		switch (player) {
		case WHITE:
			port = TCPMulino.whiteSocket;
			break;
		case BLACK:
			port = TCPMulino.blackSocket;
			break;
		default:
			System.exit(5);
		}
		playerSocket = new Socket("localhost", port);
		out = new ObjectOutputStream(playerSocket.getOutputStream());
		in = new ObjectInputStream(new BufferedInputStream(playerSocket.getInputStream()));
	}

	/**
	 * Scrive un'azione al server di gioco
	 */
	public void write(Action action) throws IOException, ClassNotFoundException {
		out.writeObject(action);
	}

	/**
	 * Legge lo stato attuale dal server
	 */
	public State read() throws ClassNotFoundException, IOException {
		return (State) in.readObject();
	}

	public State.Checker getPlayer() {
		return player;
	}

	public void setPlayer(State.Checker player) {
		this.player = player;
	}

}
