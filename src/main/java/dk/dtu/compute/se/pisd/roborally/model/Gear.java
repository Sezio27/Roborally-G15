package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

public class Gear extends FieldAction{

    private boolean clockwise;
    public Gear(boolean clockwise){
        this.clockwise = clockwise;
    }


    public boolean doAction(@NotNull GameController gameController, @NotNull Space space){
        Player player = space.getPlayer();

        if (player == null) return false;

        Heading heading = player.getHeading();
        if (clockwise) {
            player.setHeading(heading.next());
        } else {
            player.setHeading(heading.prev());
        }
        return true;
    }

}
