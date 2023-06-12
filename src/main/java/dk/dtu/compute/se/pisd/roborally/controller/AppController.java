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

public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        if (gameController != null) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
            if (!stopGame()) {
                return;
            }
        }
        String[] list = LoadBoard.getTracks();
        if (list == null) {
            System.out.println("Couldnt find tracks to load");
            return;
        }

        ChoiceDialog<String> dialog =
                new ChoiceDialog<>(list[0], list);
        dialog.setTitle("Track selection");
        dialog.setHeaderText("Pick a track");

        Optional<String> result = dialog.showAndWait();
        String track = "";
        if (result.isPresent())
            track = result.get();
        System.out.println("Track chosen: " + track);

        int playerCount = 0;

        while (playerCount < 1) playerCount = selectPlayerCount();

        if (result.isPresent()) {
            Board board = LoadBoard.loadBoard(track);
            gameController = new GameController(board);

            board.setCurrentPlayer(board.getPlayer(0));
            board.setStartSpacesDefault(playerCount);
            createPlayers(board,playerCount);

            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);

            System.out.println(board.getSpace(6,6).getAction());
            ConveyorBelt beltSouth = new ConveyorBelt(Heading.EAST);
            board.getSpace(6,6).setAction(beltSouth);
            System.out.println(board.getSpace(6,6).getAction());
        }


        //createAndStartDefault(playerCount);

    }

    //Temporary

    private void createAndStartDefault(int playerCount) {
        Board board = new Board(8, 8);
        gameController = new GameController(board);

        board.setStartSpacesDefault(PLAYER_NUMBER_OPTIONS.size());

        createPlayers(board, playerCount);

        gameController.initializeGame();

        //Remember based on saved state can be other starting phase..
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    private void createPlayers(Board board, int playerCount) {

        if (!board.getPlayers().isEmpty()) return;

        for (int i = 0; i < playerCount; i++) {
            Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
            board.addPlayer(player);
            Space startSpace = board.getStartSpaces()[i];
            player.setSpace(startSpace);
            player.setSpawnSpace(startSpace);

        }


    }

    // End of temporary

    private int selectPlayerCount() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> playerCount = dialog.showAndWait();

        if (playerCount.isPresent()) return playerCount.get();

        //Should not happen
        return -1;
    }

    public void saveGame() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save game");
        dialog.setHeaderText("save as");

        String result = "";
        result = dialog.showAndWait().get();

        if(!result.equals("")) {
            LoadBoard.saveCurrentGame(this.gameController.board, result);
            System.out.println("Saved as " + result);
        }
    }

    public void loadGame() {
        String[] list = LoadBoard.getActiveGames();
        if (list == null) {
            System.out.println("Aint no active games son");
            return;
        }

        ChoiceDialog<String> dialog =
                new ChoiceDialog<>(list[0], list);
        dialog.setTitle("Game selection");
        dialog.setHeaderText("Pick an active game");

        Optional<String> result = dialog.showAndWait();
        String track = "";
        if (result.isPresent())
            track = result.get();
        System.out.println("Du har valgt bane: " + track);

        Board board = LoadBoard.loadActiveBoard(track);
        gameController = new GameController(board);


        roboRally.createBoardView(gameController);
    }



    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
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

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
