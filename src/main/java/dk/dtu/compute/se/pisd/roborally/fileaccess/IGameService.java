package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface IGameService {
    List<String> findAllBoards();
    List<Board> findAllGames();
    public Board getGameByName(String name);
    public Board getBoardByName(String name);
    boolean addGame(Board game);
    public boolean updateGame(String name, Board game);
    public boolean deleteGameByName(String name);

}
