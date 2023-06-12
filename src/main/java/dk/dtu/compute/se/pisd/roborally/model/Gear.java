package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

public class Gear extends FieldAction{


    public Gear(){}


    public boolean doAction(@NotNull GameController gameController, @NotNull Space space){
        Player player = space.getPlayer();
        player.setHeading(player.getHeading().prev());
        //Currently only going clockwise
        return true;
    }

}
