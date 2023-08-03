package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
@Service
public class GameService implements IGameService{
    ArrayList<String> boards = new ArrayList<String>();
    ArrayList<Board> games = new ArrayList<Board>();

    public GameService() throws IOException {
        boards = new ArrayList<>(List.of(LoadBoard.getTracks()));
    }

    @Override
    public List<String> findAllBoards() {
        return boards;
    }

    @Override
    public List<Board> findAllGames() {
        for(String name : LoadBoard.getActiveGames()) {
            GsonBuilder simpleBuilder = new GsonBuilder().
                    registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>())
                    .excludeFieldsWithoutExposeAnnotation();
            Gson gson = simpleBuilder.create();

            String jsonString = gson.toJson(LoadBoard.loadActiveBoard(name));
            Board board = gson.fromJson(jsonString, Board.class);

            games.add(board);
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
                return LoadBoard.loadBoard(mapName);
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
}
