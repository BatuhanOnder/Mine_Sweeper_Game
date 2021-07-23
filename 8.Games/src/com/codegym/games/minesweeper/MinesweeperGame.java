package com.codegym.games.minesweeper;


import com.codegym.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SQUARE = 9;

    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    private GameObject[][] Board = new GameObject[SQUARE][SQUARE];

    private int countClosedFields = SQUARE * SQUARE;
    private int countFlags;
    private int score;
    private int countMines;
    private boolean isGameStopped;

    @Override
    public void initialize() {
        setScreenSize(SQUARE, SQUARE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        openSquare(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markSquare(x, y);
    }

    private void createGame() {
        for (int y = 0; y < SQUARE; y++) {
            for (int x = 0; x < SQUARE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMines++;
                }
                Board[y][x] = new GameObject(x, y, isMine);
                setCellValueEx(x, y, Color.DARKGRAY, "");
            }
        }

        countMineOnArea();
        countFlags = countMines;
    }

    private void countMineOnArea() {
        for (int y = 0; y < SQUARE; y++) {
            for (int x = 0; x < SQUARE; x++) {
                GameObject gameObject = Board[y][x];

                if (!gameObject.isMine) {
                    gameObject.countMineNeighbors = Math.toIntExact(getNeighbors(gameObject).stream().filter(neighbor -> neighbor.isMine).count());
                }
            }
        }
    }

    private void restart() {
        countClosedFields = SQUARE * SQUARE;
        score = 0;
        setScore(score);
        countMines = 0;
        isGameStopped = false;
        createGame();
    }

    private void openSquare(int x, int y) {
        GameObject gameObject = Board[y][x];

        if (gameObject.isOpen || gameObject.isFlag || isGameStopped) {
            return;
        }

        countClosedFields--;
        gameObject.isOpen = true;
        setCellColor(x, y, Color.ANTIQUEWHITE);

        if (gameObject.isMine) {
            setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
            gameOverMessage();
            return;
        }

        this.score += 5;
        setScore(score);

        if (countClosedFields == countMines) {
            winMessage();
        }

        if (gameObject.countMineNeighbors > 0) {
            setCellValue(x, y, String.valueOf(gameObject.countMineNeighbors));
        } else {
            getNeighbors(gameObject).forEach(neighbor -> openSquare(neighbor.x, neighbor.y));
        }
    }

    private void gameOverMessage() {
        showMessageDialog(Color.WHITE, "GAME OVER", Color.BLACK, 50);
        isGameStopped = true;
    }

    private void winMessage() {
        showMessageDialog(Color.WHITE, "YOU WIN", Color.VIOLET, 50);
        isGameStopped = true;
    }

    private void markSquare(int x, int y) {
        GameObject gameObject = Board[y][x];

        if (gameObject.isOpen || isGameStopped || (countFlags <= 0 && !gameObject.isFlag)) {
            return;
        }

        if (gameObject.isFlag) {
            countFlags++;
            gameObject.isFlag = false;
            setCellValueEx(x, y, Color.DARKGRAY, "");
        } else {
            countFlags--;
            gameObject.isFlag = true;
            setCellValueEx(x, y, Color.DARKGRAY, FLAG);
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1 ; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1 ; x++) {
                if (y < 0 || y >= SQUARE) {
                    continue;
                }
                if (x < 0 || x >= SQUARE) {
                    continue;
                }
                if (x == gameObject.x && y == gameObject.y) {
                    continue;
                }
                result.add(Board[y][x]);
            }
        }
        return result;
    }
}