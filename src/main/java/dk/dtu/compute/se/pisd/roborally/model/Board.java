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
package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * Represents the playing board. The board contains information about
 * the board's dimensions, the individual spaces, players on the board, the current player, game phase, number of check points
 * and the status of the game.
 * The board notifies observers of changes in its state.
 *
 * @author Jakob Jacobsen, s204502
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    @Expose
    private final List<Player> players = new ArrayList<>();

    @Expose
    private Player current;
    @Expose
    private Phase phase = INITIALISATION;
    @Expose
    private int step = 0;
    @Expose
    private boolean stepMode;

    private int numberOfCheckpoints;
    @Expose
    private List<Space> spawnSpaces = new ArrayList<>();

    private Space deadSpace;

    @Expose
    private String map;

    //for online games
    @Expose
    private String gameName;
    /**
     * Constructs a new board with the specified dimensions and name.
     *
     * @param width the width of the board
     * @param height the height of the board
     * @param boardName the name of the board
     */
    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        map = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        deadSpace = new Space(this,-1,-1);
        numberOfCheckpoints = 0;

        this.stepMode = false;

    }
    /**
     * Constructs a new board with the specified dimensions and a default name.
     *
     * @param width the width of the board
     * @param height the height of the board
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    /**
     * Sets the default spawn spaces for the specified maximum number of players.
     * The default is on the first x coordinate and the y is the variable changing depending on the number of players
     *
     * @param maxPlayer the maximum number of players indicating how many spawn spaces should be added
     */
    public void setSpawnSpacesDefault(int maxPlayer) {
        for (int i = 0; i < maxPlayer; i++) {
            spawnSpaces.add(spaces[0][i]);
        }
    }
    /**
     * Returns a list of the spawn spaces on this board.
     *
     * @return a List of the spawn spaces
     */
    public List<Space> getSpawnSpaces() {
        return spawnSpaces;
    }
    /**
     * Sets the number of checkpoints on this board to the specified value.
     *
     * @param i the new number of checkpoints
     */
    public void setNumberOfCheckpoints(int i){
        numberOfCheckpoints = i;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }
    /**
     * Returns the space at the specified coordinates.
     *
     * @param x the x-coordinate of the space
     * @param y the y-coordinate of the space
     * @return the space at the specified coordinates, or the dead space if the coordinates are invalid
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return deadSpace;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }
    /**
     * Adds the specified player to the game, if they aren't already a player.
     *
     * @param player the player to add to the game
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }
    /**
     * Sets the current player of the game to the specified player.
     *
     * @param player the player to set as the current player
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }
    /**
     * Returns the current phase of the game.
     *
     * @return the current phase of the game
     */
    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getNumberOfCheckpoints() {
        return numberOfCheckpoints;
    }

    public int getStep() {
        return step;
    }
    /**
     * Sets the current step of the game to the specified value.
     *
     * @param step the new step of the game
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }
    /**
     * Returns the number of the player on the board.
     *
     * @param player the player to get the number of
     * @return the number of this player on the board
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * If it is outside the boundaries of the board it will return the deadSpace
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; deadSpace if neighbour is outside boundaries of the board
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y++;
                break;
            case WEST:
                x--;
                break;
            case NORTH:
                y--;
                break;
            case EAST:
                x++;
                break;
        }

        return getSpace(x, y);
    }
    /**
     * Returns the designated dead space for this board.
     * Dead space is a special kind of space where player robots are placed when they are destroyed.
     *
     * @return the dead space for this board
     */
    public Space getDeadSpace() {
        return deadSpace;
    }

    public Space[][] getSpaces() {
        return spaces;
    }
    /**
     * Returns a message indicating the current status of the game.
     *
     * @return a String representing the current status of the game
     */
    public String getStatusMessage() {
        return
         "Map: " + boardName +
                ", phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep() +
                ", current checkpoint: " + getCurrentPlayer().getCurrentCheckpoint();


    }



    public void setMap(String m) {
        map = m;
    }

    public String getMap() {
        return map;
    }
    /**
     * Adds the specified space to the list of spawn spaces.
     *
     * @param space the space to add to the list of spawn spaces
     */
    public void addSpawnSpace(Space space) {
        spawnSpaces.add(space);
    }

    public void setGameName(String name){gameName = name;}
    public String getGameName(){return gameName;}
    public String getBoardName(){return boardName;}
}
