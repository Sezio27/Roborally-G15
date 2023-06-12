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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.io.*;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String ACTIVEGAMES = "activeGames";
    private static final String JSON_EXT = "json";

    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
            System.out.println("printing default");
        }

        File file = new File("src\\main\\resources\\"+BOARDSFOLDER + "\\" + boardname + "." + JSON_EXT);


        if (!file.exists()) {
            System.out.println("File not found - printing default");
            file = new File("src\\main\\resources\\"+BOARDSFOLDER + "\\" + DEFAULTBOARD + "." + JSON_EXT);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new FileReader(file));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height);
            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.setAction(spaceTemplate.action);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }

            reader.close();
            result.setMap(boardname);
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {}
            }
        }
        return null;
    }

    public static Board loadActiveBoard(String activeGameName){
        File file = new File("src\\main\\resources\\"+ ACTIVEGAMES + "\\" + activeGameName + "." + JSON_EXT);


        if (!file.exists()) {
            System.out.println("File not found - printing default");
            file = new File("src\\main\\resources\\" + BOARDSFOLDER + "\\" + DEFAULTBOARD + "." + JSON_EXT);
        }
        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        String mapName = "";
        List<Player> playerList = null;
        Player current = null;
        Phase phase = null;
        Boolean stepmode = null;
        int step = 0;

        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new FileReader(file));
            Board template = gson.fromJson(reader, Board.class);

            mapName = template.getMap();
            playerList = template.getPlayers();
            current = template.getCurrentPlayer();
            phase = template.getPhase();
            stepmode = template.isStepMode();
            step = template.getStep();

            reader.close();

        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {}
            }
        }

        Board board = loadBoard(mapName);
        for(Player p : playerList) {

            Player player = new Player(board, p.getColor(), p.getName());

            board.addPlayer(player);
            player.setSpace(board.getSpace(p.getSpace().x,p.getSpace().y));
            player.setHeading(p.getHeading());

            for(int i = 0; i < player.NO_REGISTERS; i++)
                player.setProgramField(i,p.getProgramField(i));
            for(int i = 0; i < player.NO_CARDS; i++)
                player.setCardField(i,p.getCardField(i));


            if(player.getName().equals(current.getName()))
                board.setCurrentPlayer(player);

            player.setSpawnSpace(p.getSpawnSpace());

        }
        board.setPhase(phase);
        board.setStepMode(stepmode);
        board.setStep(step);

        return board;
    }

    public static String[] getTracks(){

        File folder = new File("src\\main\\resources\\"+BOARDSFOLDER);
        System.out.println(folder.getAbsolutePath());

        if(folder.listFiles() == null)
            return null;
        String[] files = new String[folder.listFiles().length];
        for(int i = 0; i < folder.listFiles().length; i++)
            files[i] = folder.listFiles()[i].getName().substring(0,folder.listFiles()[i].getName().length()-5);
        return files;
    }
    public static String[] getActiveGames(){
        File folder = new File("src\\main\\resources\\"+ACTIVEGAMES);

        if(folder.listFiles() == null)
            return null;
        String[] files = new String[folder.listFiles().length];
        for(int i = 0; i < folder.listFiles().length; i++)
            files[i] = folder.listFiles()[i].getName().substring(0,folder.listFiles()[i].getName().length()-5);
        return files;

    }


    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || space.getAction() != null) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.action = space.getAction();
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }
    public static void saveCurrentGame(Board board, String name){

        GsonBuilder simpleBuilder = new GsonBuilder().
                excludeFieldsWithoutExposeAnnotation().
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();


        String filename =
                "src\\main\\resources\\" + ACTIVEGAMES + "\\" + name + "." + JSON_EXT;
        FileWriter fileWriter = null;
        JsonWriter writer = null;

        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(board, board.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }


}
