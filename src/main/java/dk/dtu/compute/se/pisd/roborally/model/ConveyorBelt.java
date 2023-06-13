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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a ConveyorBelt action on a game field.
 *
 * @author Jakob Jacobsen, s204502
 *
 */
public class ConveyorBelt extends FieldAction {
    private Heading heading;

    private Space target;
    /**
     * Constructs a ConveyorBelt action with a specified heading.
     *
     * @param heading the heading of this ConveyorBelt action
     */
    public ConveyorBelt(Heading heading) {
        this.heading = heading;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }
    /**
     * Returns the target Space of this ConveyorBelt action.
     *
     * @return the target Space
     */
    public Space getTarget() {
        return target;
    };
    /**
     * Executes the ConveyorBelt action on a given Space in a specified GameController.
     * Moves the player to the target space if it is reachable and not occupied.
     *
     * @param gameController the GameController to execute the action in
     * @param space the Space to execute the action on
     * @return true if the action is successfully executed, false otherwise
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {

        target = gameController.board.getNeighbour(space, heading);

        if (target != null) {
            boolean reachable = !target.getWalls().contains(heading.opposing());
            boolean notOccupied = target.getPlayer() == null;
            return notOccupied && reachable;
        }
        //Meaning it has moved the player outside board
        return true;
    }
}
