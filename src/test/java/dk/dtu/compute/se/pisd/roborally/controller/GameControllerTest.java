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
    void Gears() {
    }

    @Test
    void OutOfMap(){
        Board board = gameController.board;

        Space startSpace1 = board.getStartSpaces()[0];
        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(1,8));

        gameController.moveForward(player1, Heading.SOUTH);
        gameController.finishProgrammingPhase();
        gameController.executePrograms();

        Assertions.assertEquals(player1.getSpace(), startSpace1);
    }

    @Test
    void clearClardRegister(){

    }

    @Test
    void visibleOnMap(){
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Assertions.assertNotEquals(player1.getSpace(), board.getSpace(i, j));
            }
        }
    }

    @Test
    void order(){
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        board.putCheckPoint(board.getSpace(3,3));
        player1.setSpace(board.getSpace(1,3));
        player2.setSpace(board.getSpace(0,2));
        gameController.startProgrammingPhase();

        Assertions.assertEquals(board.getPlayer(0), board.getCurrentPlayer());
    }

    @Test
    void checkPointIfConditionNotMet(){
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);

        board.putCheckPoint(board.getSpace(4,4));
        player1.setSpace(board.getSpace(4,3));
        gameController.moveForward(player1,Heading.SOUTH);

        Assertions.assertNotEquals(board.getPlayer(0).getCurrentCheckpoint(), 1);
    }
    @Test
    void playerWin(){
       Board board = gameController.board;
       Player player1 = board.getPlayer(0);

       player1.setSpace(board.getSpace(1,1));
       gameController.moveForward(player1,Heading.SOUTH);
       gameController.executeFieldActions();

       player1.setSpace(board.getSpace(3,2));
       gameController.moveForward(player1,Heading.NORTH);
       gameController.executeFieldActions();

       player1.setSpace(board.getSpace(3,3));
       gameController.moveForward(player1,Heading.SOUTH);
       gameController.executeFieldActions();

       player1.setSpace(board.getSpace(5,5));
       gameController.moveForward(player1,Heading.SOUTH);
       gameController.executeFieldActions();

       Assertions.assertEquals(board.getPlayer(0).getCurrentCheckpoint(), board.getNumberOfCheckpoints());
    }
}

