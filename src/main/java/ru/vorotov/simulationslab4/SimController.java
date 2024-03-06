package ru.vorotov.simulationslab4;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class SimController {
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField ruleField;

    private final int GRID_SIZE = 20;
    private final int CELL_SIZE = 20;
    private final int DEAD_CELL = 0;
    private final int LIVE_CELL = 1;
    private final int ACTIVE_CELL = 2;
    private int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private Random random = new Random();
    private boolean simulationRunning = false;

    @FXML
    public void initialize() {
        drawGrid();
    }

    private void drawGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setStroke(Color.BLACK);
                cell.setFill(Color.WHITE);

                int x = i;
                int y = j;

                cell.setOnMouseClicked(event -> {
                    if (!simulationRunning) {
                        if (grid[x][y] == DEAD_CELL && event.getButton() == MouseButton.PRIMARY) {
                            grid[x][y] = LIVE_CELL;
                            cell.setFill(Color.BLUE);
                        } else if (grid[x][y] == LIVE_CELL && event.getButton() == MouseButton.SECONDARY) {
                            grid[x][y] = DEAD_CELL;
                            cell.setFill(Color.WHITE);
                        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            grid[x][y] = ACTIVE_CELL;
                            cell.setFill(Color.RED);
                        }
                    }
                });

                gridPane.add(cell, j, i);
            }
        }
    }

    @FXML
    public void onStartButtonClick(ActionEvent actionEvent) {
        if (!simulationRunning) {
            simulationRunning = true;
            Thread simulationThread = new Thread(this::runSimulation);
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    @FXML
    public void onStopButtonClick(ActionEvent actionEvent) {
        simulationRunning = false;
    }

    private void runSimulation() {
        while (simulationRunning) {
            updateGrid();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGrid() {
        int[][] newGrid = new int[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int aliveNeighbors = countAliveNeighbors(i, j);

                double stayAliveProbability = 0.7;
                double becomeAliveProbability = 0.2;
                double becomeActiveProbability = 0.1;

                if (grid[i][j] == DEAD_CELL) {
                    if (random.nextDouble() < becomeAliveProbability * (aliveNeighbors / 8.0)) {
                        newGrid[i][j] = LIVE_CELL;
                    } else {
                        newGrid[i][j] = DEAD_CELL;
                    }
                } else if (grid[i][j] == LIVE_CELL) {
                    if (random.nextDouble() < stayAliveProbability) {
                        newGrid[i][j] = LIVE_CELL;
                    } else if (random.nextDouble() < becomeActiveProbability) {
                        newGrid[i][j] = ACTIVE_CELL;
                    } else {
                        newGrid[i][j] = DEAD_CELL;
                    }
                } else if (grid[i][j] == ACTIVE_CELL) {
                    newGrid[i][j] = LIVE_CELL;
                }
            }
        }

        Platform.runLater(() -> {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    grid[i][j] = newGrid[i][j];
                    Rectangle cell = (Rectangle) gridPane.getChildren().get(i * GRID_SIZE + j);
                    if (grid[i][j] == LIVE_CELL) {
                        cell.setFill(Color.BLUE);
                    } else if (grid[i][j] == ACTIVE_CELL) {
                        cell.setFill(Color.RED);
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
                if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE && !(i == x && j == y) && grid[i][j] == LIVE_CELL) {
                    count++;
                }
            }
        }
        return count;
    }

    public void onClearButtonClick(ActionEvent actionEvent) {
    }
}
