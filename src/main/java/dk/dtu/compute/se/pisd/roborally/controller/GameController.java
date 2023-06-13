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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

/**
 * This class is responsible for managing the game control logic and state which includes:
 * <ul>
 *     <li>Initializing the {@link Board} and {@link Player}s
 *     <li>Executing the game {@link Phase}s
 *     <li>Handling {@link Player} movements, their {@link CommandCard}s and turns
 *     <li>Performing {@link FieldAction}s which currently includes {@link ConveyorBelt}, {@link Gear} and {@link Checkpoint}
 *     <li>Ensuring proper interaction of all game elements
 *     <li>Rebooting
 *     <li>Checking for win conditions
 * </ul>
 *
 * @author Jakob Jacosen, s204502
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    /**
     * Creates a new GameController with the provided board.
     *
     * @param board the game board
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * Initializes a game with the given number of players and their colors.
     * If no spawn spaces exist on the board, it sets default spawn spaces.
     * It creates players with given colors, sets their spawn spaces, and adds them to the board.
     * Finally, it starts the programming phase.
     *
     * @param playerCount the number of players
     * @param colors      the colors of the players
     */
    public void initialize(int playerCount, List<String> colors) {
        if (board.getSpawnSpaces().isEmpty()) board.setSpawnSpacesDefault(playerCount);

        for (int i = 0; i < playerCount; i++) {
            Player player = new Player(board, colors.get(i), "Player " + (i + 1));
            Space spawnSpace = board.getSpawnSpaces().get(i);
            player.setSpawnSpace(spawnSpace);
            player.setSpace(spawnSpace);
            board.addPlayer(player);
        }
        startProgrammingPhase();
    }

    // Not to be used in the actual game. Just for testing purposes
    public void moveCurrentPlayerToSpace(@NotNull Space space) {

        if (space != null && space.board == board) {
            Player currentPlayer = board.getCurrentPlayer();
            if (currentPlayer != null && space.getPlayer() == null) {
                currentPlayer.setSpace(space);
                int playerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
                board.setCurrentPlayer(board.getPlayer(playerNumber));
            }
        }

    }

    /**
     * Updates the checkpoint for the given player and checks if the player has won the game.
     * It also sets the new spawn space for the player.
     *
     * @param player the player which checkpoint and spawnSpace to update
     * @param space  the space with the checkpoint action and the new spawn space for the player
     * @param number the checkpoint number
     */
    public void updateCheckpoint(@NotNull Player player, Space space, int number) {
        player.updateCheckpoint();
        System.out.println(player.getName() + " - new checkpoint: " + number);

        if (board.getNumberOfCheckpoints() == number) {
            handleWin(player);
        }
        player.setSpawnSpace(space);
    }

    // Currently only prints out who won the game
    public void handleWin(@NotNull Player player) {
        System.out.println(player.getName() + " has won!");
    }

    /**
     * Starts the programming phase by setting the game phase to PROGRAMMING and resetting the step to zero.
     * For each player, if they are rebooting, it stops the rebooting and respawns them. It then clears their
     * program fields and assigns random command cards to their empty card fields.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player.isRebooting()) {
                player.setRebooting(false);
                player.respawn();
            }
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    if (field.getCard() == null)
                        field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Generates a random command card using the {@link Command} enum values.
     *
     * @return the newly generated command card
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Finishes the programming phase by making the program fields invisible, making the first program field
     * visible, and setting the game phase to ACTIVATION. It then resets the current player to the first player
     * and the step to zero.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Makes the program fields for the specified register visible to all players.
     *
     * @param register the register whose fields should be made visible
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }
    /**
     * Makes all cards on the registers of each player invisible
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }
    /**
     * Executes all players' programming cards in their registers in order.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }
    /**
     * Executes a program card in the current players register
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continues the execution of players' programs. If the game is in step mode, it only executes the next step.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }
    /**
     * Handles the conveyor move for players on conveyor belts.
     * It determines the final destination of each player. If two or more players has the same destination then
     * they will not be moved, otherwise the players on the spaces which conditions
     * from <code>doAction()</code> in {@link ConveyorBelt} is satisfied, will be moved.
     * If a player is moved outside of board <code>handleReboot</code> is called on that player
     *
     * @param players the list of players on the conveyor belts
     */
    private void handleConveyorMove(List<Player> players) {

        Map<Space, List<Player>> destinationMap = new HashMap<>();

        //Determine all final destinations of players on conveyor belts
        for (Player player : players) {
            Space source = player.getSpace();
            ConveyorBelt belt = source.getActionType(ConveyorBelt.class);

            if (belt.doAction(this, source)) {
                Space destination = belt.getTarget();
                if (destination == null || destination == board.getDeadSpace()) {
                    handleReboot(player);
                } else {
                    destinationMap.computeIfAbsent(destination, k -> new ArrayList<>()).add(player);
                }
            }
        }


        //Only move players with unique destinations
        destinationMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .forEach(entry -> {
                    Player player = entry.getValue().get(0);
                    Space destination = entry.getKey();
                    player.setSpace(destination);
                });
    }

    /**
     * Executes the field actions for each player's current field if possible.
     * Should be changed in the future to allow for field actions which does not have players on them
     */
    public void executeFieldActions() {

        List<Player> playersOnConveyors = new ArrayList<>();

        for (Player player : board.getPlayers()) {
            Space space = player.getSpace();

            if (space != null) {
                FieldAction action = space.getAction();
                if (action != null) {

                    //Special case for conveyors, must be handled separately
                    if (action instanceof ConveyorBelt) {
                        playersOnConveyors.add(player);
                    } else action.doAction(this, space);

                }
            }

        }
        if (!playersOnConveyors.isEmpty()) handleConveyorMove(playersOnConveyors);
    }
    /**
     * If the current player is not rebooting, it executes the player's program card in the register corresponding to the step.
     * If the card is interactive the command in the register is not executed instead, the game phase is changed to PLAYER_INTERACTION
     */
    private void executeNextStep() {

        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer.isRebooting()) {
            finishCommand();
            return;
        }

        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;

                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }

                finishCommand();

            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }
    /**
     * Sets the next player to the current player and increments the step.
     * If the current player is the last player and the step less than the total number of registers,
     * field actions will be executed and the next card of the next register will be visible.
     * If it is the last step, it starts the programming phase.
     */
    private void finishCommand() {
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1;

        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            int nextStep = board.getStep() + 1;
            if (nextStep < Player.NO_REGISTERS) {
                executeFieldActions();
                makeProgramFieldsVisible(nextStep);
                board.setStep(nextStep);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
    }

    /**
     * Executes the specified command for the given player.
     *
     * @param player the player for whom to execute the command
     * @param command the command to be executed
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {

            switch (command) {
                case FORWARD:
                    this.moveForward(player, player.getHeading());
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }

        }
    }
    /**
     * Used to handle interactive commands.
     * Executes the specified command for the given player
     * If the game is not in step mode it continues the execution of all cards in the rest of every player's registers.
     *
     * @param player the player for whom to execute the command
     * @param option the command to be executed
     */
    public void executeCommandOptionAndContinue(@NotNull Player player, Command option) {

        if (player.board == board && player == board.getCurrentPlayer()) {
            board.setPhase(Phase.ACTIVATION);
            executeCommand(player, option);
            finishCommand();

            //Automatic execution continues after interactive map has been executed
            if (!board.isStepMode() && board.getPhase() == Phase.ACTIVATION) {
                continuePrograms();
            }
        }
    }
    /**
     * Moves the player in the direction of the specified heading if possible - handled by <code>moveToSpace</code>
     *
     * @param player the player to be moved
     * @param heading the direction of movement
     */
    public void moveForward(@NotNull Player player, Heading heading) {
        if (player.board == board && !player.isRebooting()) {
            Space source = player.getSpace();
            Space destination = board.getNeighbour(source, heading);

            try {
                int moveCount = 1; // Used to avoid possible cyclic infinite recursion
                moveToSpace(player, source, destination, heading, moveCount);
            } catch (ImpossibleMoveException e) {
                System.out.println(e);
            }

        }
    }

    /**
     * Moves the player to the specified space in the given direction if the following conditions are <strong>not</strong> met:
     *
     * <ul>
     *     <li> The target space is out of the boards boundaries - handle reboot on the player
     *     <li> The source space has a wall in the same heading of this player's heading
     *     <li> The target space has a wall in the opposite heading of this player's heading
     * </ul>
     * This method is recursively called, to allow for pushing mechanics if the target space has a player,
     * with the new player, the target as the new source, the destination of the other player, but maintaining the same heading.
     * Because of the recursive nature of this method, a moveCount is used to avoid possible infinite cyclic recursion.

     * If the move is not possible, it throws an ImpossibleMoveException.
     *
     * @param player the player to be moved
     * @param source the space from which the player is moving
     * @param destination the space to which the player is moving
     * @param heading the direction of movement
     * @param moveCount the count of moves (used to avoid infinite recursion)
     * @throws ImpossibleMoveException if the move is not possible
     */
    private void moveToSpace(@NotNull Player player, @NotNull Space source, Space destination, @NotNull Heading heading, int moveCount) throws ImpossibleMoveException {
        //Or a pit
        if (destination == board.getDeadSpace()) {
            handleReboot(player);
            return;
        }

        if (source.getWalls().contains(heading) || destination.getWalls().contains(heading.opposing())) return;

        Player other = destination.getPlayer();
        if (other != null) {

            Space otherDestination = board.getNeighbour(destination, heading);

            if (moveCount <= board.getPlayersNumber() && !otherDestination.getWalls().contains(heading.opposing())) {

                moveToSpace(other, destination, otherDestination, heading, moveCount + 1);

            } else {
                throw new ImpossibleMoveException(player, destination, heading);
            }
        }

        player.setSpace(destination);

    }
    /**
     * Handles the rebooting of a player. Sets the player's space to the dead space,
     * status to rebooting and clears the rest of the programming cards in their registers and all their command cards.
     *
     * @param player the player to be rebooted
     */
    private void handleReboot(@NotNull Player player) {

        player.setRebooting(true);
        player.setSpace(board.getDeadSpace());

        int i = player == board.getCurrentPlayer() ? board.getStep() + 1 : board.getStep();

        while (i < Player.NO_REGISTERS) {
            CommandCardField field = player.getProgramField(i);
            field.setCard(null);
            i++;
        }

        for (int j = 0; j < Player.NO_CARDS; j++) {
            CommandCardField field = player.getCardField(j);
            field.setCard(null);
            field.setVisible(true);
        }


    }
    /**
     * Moves the player two steps forward in the current heading.
     *
     * @param player the player to be moved
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player, player.getHeading());
        moveForward(player, player.getHeading());
    }
    /**
     * Turns the player's heading to the right (clockwise).
     *
     * @param player the player to be turned
     */
    public void turnRight(@NotNull Player player) {
        if (player != null && player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }
    /**
     * Turns the player's heading to the left (counterclockwise).
     *
     * @param player the player to be turned
     */
    public void turnLeft(@NotNull Player player) {
        if (player != null && player.board == board) {
            player.setHeading(player.getHeading().prev());
        }
    }
    /**
     * Moves the card from the source field to the target field.
     *
     * @param source the source field
     * @param target the target field
     * @return true if the move is successful, false otherwise
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A custom exception to indicate that a move is not possible.
     */
    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

}
