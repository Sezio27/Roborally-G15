package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
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
            Space spawnSpace = board.getSpace(0,i);
            board.addSpawnSpace(spawnSpace);
            player.setSpace(spawnSpace);
            player.setSpawnSpace(spawnSpace);
        }

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
        Space startSpace1 = board.getSpawnSpaces().get(0);
        Space startSpace2 = board.getSpawnSpaces().get(1);

        Assertions.assertEquals(player1.getSpace(), startSpace1);
        Assertions.assertEquals(player2.getSpace(), startSpace2);

    }

    @Test
    void pushSuccessful() {

        Board board = gameController.board;

        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);
        player1.setSpace(board.getSpace(4,1));
        player2.setSpace(board.getSpace(4,2));

        gameController.moveForward(player1,Heading.SOUTH);

        Assertions.assertEquals(player2.getSpace(), board.getSpace(4,3));

    }

    @Test
    void pushUnsuccesful(){
        Board board = gameController.board;

        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);
        player1.setSpace(board.getSpace(4,1));
        player2.setSpace(board.getSpace(4,2));

        board.getSpace(4,3).addWall(Heading.NORTH);
        gameController.moveForward(player1,Heading.SOUTH);

        Assertions.assertEquals(player2.getSpace(), board.getSpace(4,2));
    }

    @Test
    void moveToWallSpaceSucces(){
        Board board = gameController.board;

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));

        board.getSpace(4,2).addWall(Heading.EAST);
        gameController.moveForward(player1,Heading.SOUTH);

        Assertions.assertEquals(player1.getSpace(), board.getSpace(4,2));
    }

    @Test
    void moveToWallSpaceUnsuccesful(){
        Board board = gameController.board;

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));

        board.getSpace(4,2).addWall(Heading.NORTH);
        gameController.moveForward(player1,Heading.SOUTH);

        Assertions.assertEquals(player1.getSpace(), board.getSpace(4,1));
    }

    @Test
    void ConveyorSuccesful(){
        Board board = gameController.board;
        ConveyorBelt beltSouth = new ConveyorBelt(Heading.SOUTH);

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));

        board.getSpace(4,2).setAction(beltSouth);
        beltSouth.doAction(gameController,board.getSpace(4,3));
        gameController.moveForward(player1, Heading.SOUTH);

        Assertions.assertEquals(player1.getSpace(), board.getSpace(4,2));
    }
}

