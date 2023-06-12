package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private final int NUMBER_OF_PLAYERS = 6;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Player player = new Player(board, null, "Player " + (i + 1));
            board.addPlayer(player);
        }
        gameController.initializeGame(6);
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void startingSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);
        Space startSpace1 = board.getStartSpaces()[0];
        Space startSpace2 = board.getStartSpaces()[1];

        Assertions.assertEquals(player1.getSpace(), startSpace1);
        Assertions.assertEquals(player2.getSpace(), startSpace2);

    }

    @Test
    void pushSuccessful() {
        Board board = gameController.board;


        board.getSpace(2,3).addWall(Heading.SOUTH);


    }


}

