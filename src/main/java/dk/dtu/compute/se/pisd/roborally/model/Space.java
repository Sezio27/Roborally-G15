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
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    private List<Heading> walls = new ArrayList<>();

    private FieldAction action;

    /* Currently not supporting multiple field actions on a single space
     private List<FieldAction> actions = new ArrayList<>();
     */
    @Expose
    public final int x;
    @Expose
    public final int y;

    private Player player;


    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {

        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;

            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }
    public List<Heading> getWalls() {
        return walls;
    }

    public void addWall(Heading heading) {
        if ( ! walls.contains(heading)) {
            walls.add(heading);
            notifyChange();
        }
    }

    public void setAction(FieldAction action) {
        this.action = action;
    }

    public FieldAction getAction() {
        return action;
    }

    public <T extends FieldAction> T getActionType(Class<T> type) {
        if (type.isInstance(action)) {
            return type.cast(action);
        } else {
            return null;
        }
    }

    /* Methods supporting multiple FieldActions

    public <T extends FieldAction> T getAction(Class<T> type) {
        return actions.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    public List<FieldAction> getActions() {
        return actions;
    }


    public void addFieldAction (FieldAction action) {
        if (!actions.contains(action)) {
            actions.add(action);
            notifyChange();
        }
    }

     */

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
