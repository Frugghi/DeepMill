package it.unibo.ai.didattica.mulino.DeepMill.debug.fx;

import com.sun.security.ntlm.Server;
import it.unibo.ai.didattica.mulino.DeepMill.debug.DebugSecurityManager;
import it.unibo.ai.didattica.mulino.DeepMill.debug.ProxyGUI;
import it.unibo.ai.didattica.mulino.DeepMill.debug.StateUI;
import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.engine.TCPMulino;
import it.unibo.ai.didattica.mulino.gui.Background;
import it.unibo.ai.didattica.mulino.gui.GUI;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Engine implements Initializable, StateUI {

    private SimpleBooleanProperty isMatchRunning = new SimpleBooleanProperty(false);

    @FXML private Circle a1;
    @FXML private Circle a4;
    @FXML private Circle a7;
    @FXML private Circle b2;
    @FXML private Circle b4;
    @FXML private Circle b6;
    @FXML private Circle c3;
    @FXML private Circle c4;
    @FXML private Circle c5;
    @FXML private Circle d1;
    @FXML private Circle d2;
    @FXML private Circle d3;
    @FXML private Circle d5;
    @FXML private Circle d6;
    @FXML private Circle d7;
    @FXML private Circle e3;
    @FXML private Circle e4;
    @FXML private Circle e5;
    @FXML private Circle f2;
    @FXML private Circle f4;
    @FXML private Circle f6;
    @FXML private Circle g1;
    @FXML private Circle g4;
    @FXML private Circle g7;
    @FXML private ChoiceBox playerChoice;
    @FXML private ChoiceBox phaseChoice;
    @FXML private Button startButton;

    public void start(ActionEvent event) {
        this.isMatchRunning.setValue(true);

        int white = 0;
        int black = 0;
        State state = new State();
        for (String position : state.getPositions()) {
            Circle circle = this.string2circle(position);
            if (this.isWhite(circle)) {
                state.getBoard().put(position, State.Checker.WHITE);
                white++;
            } else if (this.isBlack(circle)) {
                state.getBoard().put(position, State.Checker.BLACK);
                black++;
            } else if (this.isEmpty(circle)) {
                state.getBoard().put(position, State.Checker.EMPTY);
            }
        }

        if (white > 9 || black > 9) {
            return;
        }

        if (this.phaseChoice.getSelectionModel().getSelectedItem().equals("Phase 1")) {
            state.setWhiteCheckers(9 - white);
            state.setBlackCheckers(9 - black);

            if (white == 9 && black == 9) {
                state.setCurrentPhase(State.Phase.SECOND);
            } else {
                state.setCurrentPhase(State.Phase.FIRST);
            }
        } else if (this.phaseChoice.getSelectionModel().getSelectedItem().equals("Phase 2")) {
            state.setWhiteCheckers(0);
            state.setBlackCheckers(0);

            state.setCurrentPhase(State.Phase.SECOND);
        } else if (this.phaseChoice.getSelectionModel().getSelectedItem().equals("Phase 3")) {
            state.setWhiteCheckers(0);
            state.setBlackCheckers(0);

            if (white == 3 || black == 3) {
                state.setCurrentPhase(State.Phase.FINAL);
            } else if (white < 3 || black < 3) {
                state.setWhiteCheckers(9 - white);
                state.setBlackCheckers(9 - black);

                state.setCurrentPhase(State.Phase.FIRST);
            } else {
                state.setCurrentPhase(State.Phase.SECOND);
            }
        }

        state.setWhiteCheckersOnBoard(white);
        state.setBlackCheckersOnBoard(black);

        State.Checker player = this.playerChoice.getSelectionModel().getSelectedItem().equals("White") ? State.Checker.WHITE : State.Checker.BLACK;

        System.setSecurityManager(null);
        final it.unibo.ai.didattica.mulino.engine.Engine server = new it.unibo.ai.didattica.mulino.engine.Engine(60, 16);
        try {
            Field playerField = it.unibo.ai.didattica.mulino.engine.Engine.class.getDeclaredField("currentPlayer");
            playerField.setAccessible(true);
            playerField.set(server, player);

            Field stateField = it.unibo.ai.didattica.mulino.engine.Engine.class.getDeclaredField("currentState");
            stateField.setAccessible(true);
            stateField.set(server, state);

            Field guiField = it.unibo.ai.didattica.mulino.engine.Engine.class.getDeclaredField("theGui");
            guiField.setAccessible(true);

            GUI theGui = (GUI)guiField.get(server);
            Field mainFrameField = it.unibo.ai.didattica.mulino.gui.GUI.class.getDeclaredField("mainFrame");
            mainFrameField.setAccessible(true);
            Background mainFrame = (Background)mainFrameField.get(theGui);
            mainFrame.setVisible(false);
            mainFrame.dispose();

            guiField.set(server, new ProxyGUI(this));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        new Thread(){
            @Override
            public void run() {
                try {
                    System.setSecurityManager(new DebugSecurityManager());
                    server.run();
                } catch (SecurityException e) {
                    System.out.println("The game has finished...");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Closing the sockets...");
                try {
                    Field whiteSocketField = it.unibo.ai.didattica.mulino.engine.Engine.class.getDeclaredField("whiteSocket");
                    whiteSocketField.setAccessible(true);
                    closeSockets((TCPMulino)whiteSocketField.get(server));

                    Field blackSocketField = it.unibo.ai.didattica.mulino.engine.Engine.class.getDeclaredField("blackSocket");
                    blackSocketField.setAccessible(true);
                    closeSockets((TCPMulino)blackSocketField.get(server));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Engine.this.isMatchRunning.setValue(false);
                    }
                });
            }
        }.start();
    }

    private void closeSockets(TCPMulino playerSocket) throws NoSuchFieldException, IllegalAccessException {
        Field serverSocketField = it.unibo.ai.didattica.mulino.engine.TCPMulino.class.getDeclaredField("serverSocket");
        serverSocketField.setAccessible(true);

        Field connectionSocketField = it.unibo.ai.didattica.mulino.engine.TCPMulino.class.getDeclaredField("connectionSocket");
        connectionSocketField.setAccessible(true);

        if (playerSocket != null) {
            ServerSocket serverSocket = (ServerSocket)serverSocketField.get(playerSocket);
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {

                }
            }

            Socket connectionSocket = (Socket)connectionSocketField.get(playerSocket);
            if (connectionSocket != null) {
                try {
                    connectionSocket.close();
                } catch (Exception e) {

                }
            }
        }
    }

    public void handleCircleClick(MouseEvent event) {
        if (this.isMatchRunning.getValue()) {
            return;
        }

        Circle circle = (Circle) event.getSource();
        if (event.getClickCount() == 1) {
            switch (event.getButton()) {
                case PRIMARY:
                    this.setWhite(circle);
                    break;
                case SECONDARY:
                    this.setBlack(circle);
                    break;
                case MIDDLE:
                    this.setEmpty(circle);
                    break;
            }
        } else {
            this.setEmpty(circle);
        }
    }

    private boolean isBlack(Circle position) {
        return position.getStyle().contains("-fx-fill: black; -fx-opacity: 1");
    }

    private boolean isWhite(Circle position) {
        return position.getStyle().contains("-fx-fill: white; -fx-opacity: 1");
    }

    private boolean isEmpty(Circle position) {
        return position.getStyle().contains("-fx-opacity: 0");
    }

    private void setBlack(Circle position) {
        position.setStyle("-fx-fill: black; -fx-opacity: 1");
    }

    private void setWhite(Circle position) {
        position.setStyle("-fx-fill: white; -fx-opacity: 1");
    }

    private void setEmpty(Circle position) {
        position.setStyle("-fx-opacity: 0");
    }

    private Circle string2circle(String position) {
        switch (position.toLowerCase()) {
            case "a1": return this.a1;
            case "a4": return this.a4;
            case "a7": return this.a7;
            case "b2": return this.b2;
            case "b4": return this.b4;
            case "b6": return this.b6;
            case "c3": return this.c3;
            case "c4": return this.c4;
            case "c5": return this.c5;
            case "d1": return this.d1;
            case "d2": return this.d2;
            case "d3": return this.d3;
            case "d5": return this.d5;
            case "d6": return this.d6;
            case "d7": return this.d7;
            case "e3": return this.e3;
            case "e4": return this.e4;
            case "e5": return this.e5;
            case "f2": return this.f2;
            case "f4": return this.f4;
            case "f6": return this.f6;
            case "g1": return this.g1;
            case "g4": return this.g4;
            case "g7": return this.g7;
            default:
                return null;
        }
    }

    public void update(final State currentState) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                for (String position : currentState.getPositions()) {
                    Circle circle = Engine.this.string2circle(position);
                    switch (currentState.getBoard().get(position)) {
                        case WHITE:
                            Engine.this.setWhite(circle);
                            break;
                        case BLACK:
                            Engine.this.setBlack(circle);
                            break;
                        case EMPTY:
                            Engine.this.setEmpty(circle);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.playerChoice.setItems(FXCollections.observableArrayList("White", "Black"));
        this.playerChoice.getSelectionModel().selectFirst();
        this.playerChoice.disableProperty().bind(this.isMatchRunning);

        this.phaseChoice.setItems(FXCollections.observableArrayList("Phase 1", "Phase 2", "Phase 3"));
        this.phaseChoice.getSelectionModel().selectFirst();
        this.phaseChoice.disableProperty().bind(this.isMatchRunning);

        this.startButton.disableProperty().bind(this.isMatchRunning);
    }

}
