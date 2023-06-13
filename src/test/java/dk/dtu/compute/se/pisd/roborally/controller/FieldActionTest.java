package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class FieldActionTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private final int NUMBER_OF_PLAYERS = 4;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Player player = new Player(board, null, "Player " + (i + 1));
            board.addPlayer(player);
        }
        gameController.initialize(4, Arrays.asList("red", "green", "blue", "orange"));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void ConveyorSuccesful(){
        Board board = gameController.board;
        ConveyorBelt beltSouth = new ConveyorBelt(Heading.SOUTH);

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));


        board.getSpace(4,2).setAction(beltSouth);
        gameController.moveForward(player1, Heading.SOUTH);
        gameController.executeFieldActions();


        Assertions.assertEquals(player1.getSpace(), board.getSpace(4,3));
    }

    @Test
    void ConveyorUnsuccesful1(){
        Board board = gameController.board;
        ConveyorBelt beltSouth = new ConveyorBelt(Heading.SOUTH);

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));

        board.getSpace(4,2).setAction(beltSouth);
        board.getSpace(4,3).addWall(Heading.NORTH);
        gameController.moveForward(player1, Heading.SOUTH);
        gameController.executeFieldActions();

        Assertions.assertNotEquals(player1.getSpace(), board.getSpace(4,3));
    }

    @Test
    void ConveyorUnsuccesful2(){
        Board board = gameController.board;
        ConveyorBelt beltSouth = new ConveyorBelt(Heading.SOUTH);

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));
        Player player2 = board.getPlayer(1);
        player2.setSpace(board.getSpace(4,2));

        board.getSpace(4,2).setAction(beltSouth);
        gameController.moveForward(player1, Heading.SOUTH);
        gameController.executeFieldActions();

        Assertions.assertNotEquals(player1.getSpace(), board.getSpace(4,3));
    }

    @Test
    void ConveyorUnsuccesful3(){
        Board board = gameController.board;
        ConveyorBelt beltSouth = new ConveyorBelt(Heading.SOUTH);

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));
        Player player2 = board.getPlayer(1);
        player2.setSpace(board.getSpace(4,2));
        Player player3 = board.getPlayer(2);
        player3.setSpace(board.getSpace(4,3));
        Player player4 = board.getPlayer(3);
        player4.setSpace(board.getSpace(4,4));


        for (int i = 0; i < 3; i++) {
            board.getSpace(4,1+i).setAction(beltSouth);
        }
        gameController.moveForward(player1, Heading.SOUTH);
        gameController.executeFieldActions();

        Assertions.assertNotEquals(player1.getSpace(), board.getSpace(4,5));
    }
    @Test
    void gearTest(){
        Board board = gameController.board;
        Gear gearNorth = new Gear(false);

        Player player1 = board.getPlayer(0);
        player1.setSpace(board.getSpace(4,1));
        board.getSpace(4,2).setAction(gearNorth);

        Heading expectedHeading = player1.getHeading().prev();
        gameController.moveForward(player1, Heading.SOUTH);
        gameController.executeFieldActions();
        System.out.println(player1.getHeading());
        Assertions.assertEquals(player1.getHeading(), expectedHeading);
    }
}

