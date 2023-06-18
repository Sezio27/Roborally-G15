/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class BoardView extends VBox implements ViewObserver {

    private Board board;
    private Phase phase;
    private int step;
    private int round;


    private GridPane mainBoardPane;
    private SpaceView[][] spaces;
    private SpaceView deadSpace;
    private PlayersView playersView;

    private VBox statusBox;
    private Label phaseInfo;
    private Label stepInfo;
    private List<Integer> playerCheckpoints = new ArrayList<>();
    private List<Boolean> playerRebooting = new ArrayList<>();
    private HBox checkPointBox;
    private HBox rebootingBox;

    private SpaceEventHandler spaceEventHandler;

    public BoardView(@NotNull GameController gameController) {
        board = gameController.board;

        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController);

        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersView);


        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(gameController);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        statusBox = new VBox();
        checkPointBox = new HBox();
        rebootingBox = new HBox();
        createStatusBox();

        this.getChildren().add(statusBox);

        deadSpace = new SpaceView(board.getDeadSpace());
        board.attach(this);
        update(board);
    }

    public void createStatusBox() {
        checkPointBox.getChildren().add(new Label("Reached Checkpoint:"));
        rebootingBox.getChildren().add(new Label(("Rebooting:\t\t  ")));
        String playerNames = "";
        for (Player player : board.getPlayers()) {

            playerCheckpoints.add(player.getCurrentCheckpoint());
            checkPointBox.getChildren().add(new Label("\t" + player.getCurrentCheckpoint() + " \t"));
            playerRebooting.add(false);
            rebootingBox.getChildren().add((new Label("\t" + "-" + "\t")));

            playerNames += "\t" + player.getName();
        }

        Label boardName = new Label("Map name:\t" + board.getMap());
        Label boardCheckpoints = new Label("Checkpoints:\t" + board.getNumberOfCheckpoints());
        Label players = new Label("\t\t\t\t" + playerNames);


        phase = board.getPhase();
        phaseInfo = new Label("Phase:\t" + phase);
        step = board.getStep();
        stepInfo = new Label("Step:\t" + step);

        statusBox.getChildren().add(boardName);
        statusBox.getChildren().add(boardCheckpoints);
        statusBox.getChildren().add(phaseInfo);
        statusBox.getChildren().add(players);
        statusBox.getChildren().add(checkPointBox);
        statusBox.getChildren().add(rebootingBox);
        statusBox.getChildren().add(stepInfo);
    }

    public void updateStatus() {
        Phase currentPhase = board.getPhase();
        if (phase != currentPhase) {
            phaseInfo = new Label("Phase:\t" + currentPhase);
            statusBox.getChildren().set(2, phaseInfo);
            phase = currentPhase;
        }

        int currentStep = board.getStep();
        if (step != currentStep) {
            stepInfo = new Label("Step:\t" + currentStep);
            statusBox.getChildren().set(6, stepInfo);
            step = currentStep;
        }

        boolean updatedCheckpoint = false;
        boolean playerReboot = false;
        for (int i = 0; i < board.getPlayersNumber(); i++) {

            int playerCheckpoint = board.getPlayer(i).getCurrentCheckpoint();
            if (playerCheckpoint != playerCheckpoints.get(i)) {
                checkPointBox.getChildren().set(i + 1, new Label("\t" + playerCheckpoint + " \t"));
                playerCheckpoints.set(i, playerCheckpoint);
                updatedCheckpoint = true;
            }

            if (board.getPlayer(i).isRebooting() && !playerRebooting.get(i)) {
                rebootingBox.getChildren().set(i+1, new Label("\tY\t"));
                playerRebooting.set(i, true);
                playerReboot = true;
            }

            if (board.getPhase() == Phase.PROGRAMMING) {
                rebootingBox.getChildren().set(i+1, new Label("\t-\t"));
                playerReboot = true;
            }
        }


        if (updatedCheckpoint) statusBox.getChildren().set(4, checkPointBox);
        if (playerReboot) statusBox.getChildren().set(5, rebootingBox);

    }


    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            updateStatus();
        }
    }

    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {

        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source instanceof SpaceView) {
                SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    gameController.moveCurrentPlayerToSpace(space);
                    event.consume();
                }
            }
        }

    }

}
