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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.URISyntaxException;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    private void updateWalls() {
        double wallWidth = SPACE_WIDTH / 7;
        double translateVal = (SPACE_WIDTH - wallWidth) / 2;
        double lineLength = SPACE_WIDTH;

        if (space != null && !space.getWalls().isEmpty()) {
            System.out.println("called");
            for (Heading wall: space.getWalls()) {

                Polygon line = new Polygon(0.0, 0.0,
                       lineLength, 0.0,
                       lineLength, wallWidth,
                        0.0, wallWidth);

                switch (wall) {
                    case NORTH:
                        line.setTranslateY(-translateVal);
                        break;
                    case SOUTH:
                        line.setTranslateY(translateVal);
                        break;
                    case EAST:
                        line.setTranslateX(translateVal);
                        line.setRotate(90*wall.ordinal() % 360);
                        break;
                    case WEST:
                        line.setTranslateX(-translateVal);
                        line.setRotate(90*wall.ordinal() % 360);
                        break;
                }

                line.setFill(Color.DARKGOLDENROD);
                this.getChildren().add(line);

            }
        }
    }

    private void updateFieldActions() {
        if (space != null) {

            FieldAction action = space.getAction();

            if (action != null) {
                if (action instanceof ConveyorBelt) {
                    String heading = ((ConveyorBelt) action).getHeading().toString();
                    putIcon("conveyor" + heading + ".png");
                }

                if (action instanceof Checkpoint) {
                    int number = ((Checkpoint) action).getNumber();
                    putIcon("checkpoint" + number + ".png");
                }

                //Continue like this

            }
        }
    }

    /*
    private void updateFieldActions() {
        if (space != null && !space.getActions().isEmpty()) {
            for (FieldAction action: space.getActions()) {

                if (action instanceof ConveyorBelt) {
                        String heading = ((ConveyorBelt) action).getHeading().toString();
                        putIcon("conveyor" + heading + ".png");
                }

                if (action instanceof Checkpoint) {
                    int number = ((Checkpoint) action).getNumber();
                    putIcon("checkpoint" + number + ".png");
                }

                //Continue like this
            }
        }
    }
     */

    private ImageView putIcon(String name) {
        Image icon = new Image(SpaceView.class.getClassLoader().getResource("assets/"+name).toString());
        ImageView imgView = new ImageView(icon);
        imgView.setImage(icon);
        imgView.setFitHeight(SPACE_HEIGHT);
        imgView.setFitWidth(SPACE_WIDTH);
        imgView.setVisible(true);
        this.getChildren().add(imgView);
        return imgView;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();
            updateFieldActions();
            updateWalls();
            updatePlayer();
        }
    }
}
