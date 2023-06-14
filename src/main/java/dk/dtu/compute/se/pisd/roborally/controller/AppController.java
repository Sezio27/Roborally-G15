package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
/**
 * The AppController class handles the overall game state and user actions from the game menu interface.
 * It allows users to create new games, load and save games, manage player count and game board,
 * and stop the game. It implements the Observer pattern, responding to changes in the game state.
 * This class also handles error messages and provides options to the user when necessary.
 *
 * @author Jakob Jacobsen, s204502
 * @author William Aslak Tonning, s205838
 */
public class AppController implements Observer {
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private int MAX_PLAYERS = 6;
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private RoboRally roboRally;
    private GameController gameController;
    /**
     * Constructs a new AppController with the specified RoboRally instance.
     *
     * @param roboRally the instance of RoboRally that this AppController will control
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }
    /**
     * Starts a new game, prompting the user to select a player count and a board.
     * If the user chose a number not in the array PLAYER_NUMBER_OPTIONS, an alert will be shown and the method will return
     * If there is no boards saved or the default board does not exist,
     * then a default board option will be displayed and the default board will be created and saved
     */
    public void newGame() {
        if (gameController != null) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
            if (!stopGame()) {
                return;
            }
        }

        int playerCount = selectPlayerCount();

        if (playerCount < 2 || playerCount > 6) {
            showAlert("Please select between 2 - 6 players");
            return;
        }
        String[] boards = LoadBoard.getTracks();
        Board board;
        if (boards == null || boards.length < 1 || Arrays.stream(boards).noneMatch(s -> s.equals("defaultboard"))) {
            board = createAndSaveDefault();
        } else {
            board = LoadBoard.loadBoard(selectBoard(boards));
        }

        gameController = new GameController(board);
        gameController.initialize(playerCount, PLAYER_COLORS);
        roboRally.createBoardView(gameController);
    }
    /**
     * Creates a default board when no existing boards are found.
     * The default board has a predefined layout and spawn spaces, depending on the number of players
     * and is saved under the name "defaultboard".
     *
     * @return the default Board object
     */
    private Board createAndSaveDefault() {
        //Temp hack/solution
        String boardName = "defaultboard";
        selectBoard(new String[]{boardName});

        Board board = new Board(8, 8);
        board.setSpawnSpacesDefault(MAX_PLAYERS);

        board.getSpace(3, 3).addWall(Heading.WEST);
        board.getSpace(3, 4).addWall(Heading.WEST);
        board.getSpace(3, 6).addWall(Heading.WEST);
        board.getSpace(2, 0).setAction(new Gear(false));
        board.getSpace(2, 2).setAction(new Gear(true));
        board.getSpace(3, 5).setAction(new ConveyorBelt(Heading.WEST));
        board.getSpace(0, 0).setAction(new ConveyorBelt(Heading.WEST));
        board.getSpace(2, 5).setAction(new ConveyorBelt(Heading.WEST));
        board.getSpace(6, 3).setAction(new ConveyorBelt(Heading.NORTH));
        board.getSpace(4, 6).setAction(new Checkpoint(1));
        board.getSpace(1, 5).setAction(new Checkpoint(2));

        LoadBoard.saveBoard(board, boardName);
        return board;

    }
    /**
     * Prompts the user to select a board for the game from an array of available boards.
     * If the boards array is null, an alert is shown.
     *
     * @param boards an array of board names available for selection
     * @return the name of the selected board
     */
    private String selectBoard(String[] boards) {

        if (boards == null || boards.length == 0) {
            showAlert("No tracks");
            return null;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(boards[0], boards);
        dialog.setTitle("Track selection");
        dialog.setHeaderText("Pick a track");
        Optional<String> result = dialog.showAndWait();
        String track = "";
        if (result.isPresent()) {
            track = result.get();
            System.out.println("Track chosen: " + track);
        }
        return track;
    }
    /**
     * Prompts the user to select the number of players for the game.
     *
     * @return the number of players selected by the user, -1 if no selection is made
     */
    private int selectPlayerCount() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> playerCount = dialog.showAndWait();

        if (playerCount.isPresent()) return playerCount.get();

        return -1;
    }
    /**
     * Displays an alert to the user with the provided message.
     * Primarily used in connection with handling wrong input and stopping an action
     *
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    /**
     * Saves the current game state under a name provided by the user.
     * Notifies the user if they enter an empty name or if the game is being overwritten.
     */
    public void saveGame() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save game");
        dialog.setHeaderText("save as");

        String result = dialog.showAndWait().get();

        if (result.equals("")) {
            showAlert("Please enter a name for the saved game");
            return;
        }

        if (Arrays.asList(LoadBoard.getTracks()).contains(result)) {
            showAlert("Saving and overriding " + result);
        }

        LoadBoard.saveCurrentGame(this.gameController.board, result);
        System.out.println("Saved as " + result);

    }
    /**
     * Loads a game based on the user's selection. Shows an alert if the game could not be loaded.
     */
    public void loadGame() {

        String track = selectBoard(LoadBoard.getActiveGames());

        if (track == null || track.isEmpty()) {
            showAlert("Could not load game");
            return;
        }

        Board board = LoadBoard.loadActiveBoard(track);
        if (board == null) {
            showAlert("Could not load game");
            return;
        }

        gameController = new GameController(board);
        roboRally.createBoardView(gameController);

    }

    /**
     *
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned. - NOT IMPLEMENTED YET
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            //saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }
    /**
     * Closes the application, giving the user the option to save the current game before exiting.
     * Does not exit the application if the user cancels the operation.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }
    /**
     * Returns true if a game is currently running, false otherwise.
     *
     * @return true if a game is currently running, false otherwise
     */
    public boolean isGameRunning() {
        return gameController != null;
    }
    /**
     * Updates the game state based on changes in the game state. Currently not implemented.
     *
     * @param subject the Subject that this Observer is observing
     */
    @Override
    public void update(Subject subject) {

    }


}
