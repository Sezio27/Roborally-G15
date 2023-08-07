package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.List;

import dk.dtu.compute.se.pisd.roborally.fileaccess.IGameService;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Autowired
    private IGameService gameService;

    @GetMapping(value = "/boards")
    public ResponseEntity<List<String>> getBoard()
    {
        List<String> maps = gameService.findAllBoards();
        return ResponseEntity.ok().body(maps);
    }

    @GetMapping(value = "/activeGames")
    public ResponseEntity<List<Board>> getGame()
    {
        List<Board> maps = gameService.findAllGames();
        return ResponseEntity.ok().body(maps);
    }
    @GetMapping("/boards/{name}")
    public ResponseEntity<Board> getSpecificBoard(@PathVariable String name) {
        Board track = gameService.getBoardByName(name);
        return ResponseEntity.ok().body(track);
    }


}
