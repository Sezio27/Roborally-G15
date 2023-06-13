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

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum representing the different commands that can be issued in the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    FORWARD("Fwd"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    FAST_FORWARD("Fast Fwd"),

    // XXX Assignment P3
    OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT);

    @Expose
    final public String displayName;

    final private List<Command> options;
    /**
     * Constructs a Command with a displayName and a list of options.
     *
     * @param displayName name of the command for display purposes
     * @param options optional commands to be used interactively
     */
    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }
    /**
     * Returns whether the command is interactive.
     *
     * @return boolean indicating if the command is interactive
     */
    public boolean isInteractive() {
        return !options.isEmpty();
    }
    /**
     * Returns the list of options for this command.
     *
     * @return list of command options
     */
    public List<Command> getOptions() {
        return options;
    }

}
