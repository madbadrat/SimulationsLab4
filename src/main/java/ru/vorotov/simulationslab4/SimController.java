package ru.vorotov.simulationslab4;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class SimController implements Initializable {
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField ruleField;

    private final int GRID_SIZE = 20;
    private final int CELL_SIZE = 20;
    private final int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private Timer timer;
    private Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawGrid();
    }

    private void drawGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setStroke(Color.BLACK);

                if (grid[i][j] == 1) cell.setFill(Color.BLUE);
                else cell.setFill(Color.WHITE);

                int x = i;
                int y = j;

                cell.setOnMouseClicked(event -> {
                    if (grid[x][y] == 0 && event.getButton() == MouseButton.PRIMARY) {
                        grid[x][y] = 1;
                        cell.setFill(Color.BLUE);
                    } else if (grid[x][y] == 1 && event.getButton() == MouseButton.SECONDARY) {
                        grid[x][y] = 0;
                        cell.setFill(Color.WHITE);
                    }
                });

                gridPane.add(cell, j, i);
            }
        }
    }

    public void onStartButtonClick(ActionEvent actionEvent) {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateGrid();
                }
            }, 0, 100);
        }
    }

    private void updateGrid() {
        int[][] newGrid = new int[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int aliveNeighbors = countAliveNeighbors(i, j);

                double stayAliveProbability = 0.7;
                double becomeAliveProbability = 0.2;

                if (grid[i][j] == 1) {
                    if (random.nextDouble() < stayAliveProbability) {
                        newGrid[i][j] = 1;
                    } else {
                        newGrid[i][j] = 0;
                    }
                } else {
                    if (random.nextDouble() < becomeAliveProbability * (aliveNeighbors / 8.0)) {
                        newGrid[i][j] = 1;
                    } else {
                        newGrid[i][j] = 0;
                    }
                }
            }
        }

        Platform.runLater(() -> {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    grid[i][j] = newGrid[i][j];
                    Rectangle cell = (Rectangle) gridPane.getChildren().get(i * GRID_SIZE + j);
                    if (grid[i][j] == 1) {
                        cell.setFill(Color.BLUE);
                    } else {
                        cell.setFill(Color.WHITE);
                    }
                }
            }
        });
    }

    private int countAliveNeighbors(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((i != x || j != y) && i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE && grid[i][j] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public void onStopButtonClick(ActionEvent actionEvent) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void onClearButtonClick(ActionEvent actionEvent) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = 0;
            }
        }
        drawGrid();
    }
}
