package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class GameService implements IGameService{
    ArrayList<String> boards = new ArrayList<>();
    ArrayList<Board> games = new ArrayList<Board>();

    public GameService() {
        boards.addAll(Arrays.asList(LoadBoard.getTracks()));
    }

    @Override
    public List<String> findAllBoards() {

        return boards;
    }

    @Override
    public List<Board> findAllGames() {
        for(String name : LoadBoard.getActiveGames()) {
            games.add(recursionDeleter(LoadBoard.loadActiveBoard(name)));
        }
        return games;
    }

    @Override
    public Board getGameByName(String name) {
        for(Board game : games) {
            if(game.getGameName().equals(name)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Board getBoardByName(String name) {
        for(String mapName : boards) {
            if(mapName.equals(name)) {
                Board temp = LoadBoard.loadBoard(mapName);
                Player p = new Player(temp,"blue","temp");
                temp.addPlayer(p);
                temp.setCurrentPlayer(p);
                return recursionDeleter(temp);
            }
        }
        return null;
    }

    @Override
    public boolean addGame(Board game) {
        games.add(game);
        return true;
    }

    @Override
    public boolean updateGame(String name, Board game) {
        return false;
    }

    @Override
    public boolean deleteGameByName(String name) {
        return false;
    }

    public Board recursionDeleter(Board b) {
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>())
                .excludeFieldsWithoutExposeAnnotation();
        Gson gson = simpleBuilder.create();
        return gson.fromJson(gson.toJson(b), Board.class);
    }

}
