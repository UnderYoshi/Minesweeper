package com.underyo.minesweeper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameInstance {
    Random random = new Random();
    HashMap<Integer, TileFace> intToTileFace = new HashMap<>();

    private Tile[][] gameBoard;
    public final int WIDTH;
    public final int HEIGHT;
    public final int MINES;
    private boolean minesGenerated = false;

    public GameInstance(int width, int height, int mines) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.MINES = mines;
        // sets placeholder empty board based on width & height
        generateNoMineBoard();

        // Initializes intToTileFace HashMap
        for (int i = 0; i <= 8; i++) {
            switch(i) {
                case 0 -> intToTileFace.put(i, TileFace.EMPTY);
                case 1 -> intToTileFace.put(i, TileFace.ONE);
                case 2 -> intToTileFace.put(i, TileFace.TWO);
                case 3 -> intToTileFace.put(i, TileFace.THREE);
                case 4 -> intToTileFace.put(i, TileFace.FOUR);
                case 5 -> intToTileFace.put(i, TileFace.FIVE);
                case 6 -> intToTileFace.put(i, TileFace.SIX);
                case 7 -> intToTileFace.put(i, TileFace.SEVEN);
                case 8 -> intToTileFace.put(i, TileFace.EIGHT);
            }
        }
    }

    /**
     * 
     * @param row selected row
     * @param col selected column
     * @return if player has exploded or not
     */
    public boolean reveal(int row, int col) {
        //System.out.println(String.format("revealing... row: %d, col: %d", row, col));
        boolean returnBool = revealSingle(row, col);
        
        Tile tile = getTile(row, col);
        if (tile.getTileFace() != TileFace.EMPTY) {
            return returnBool;
        }

        ArrayList<Pair> surroundingTiles = getSurroundingTilesPositions(row, col);

        Set<Tile> discoveredTiles = new HashSet<>();

        if (tile.getTileFace() == TileFace.EMPTY) {
            discoveredTiles.addAll(getSurroundingTiles(row, col));
            tile.revealedSurroundingTiles = true;
        }
        
        while (!checkRevealComplete(discoveredTiles)) {
            for (Tile dTile: discoveredTiles) {
                if (!dTile.isRevealed()) {
                    revealSingle(dTile.ROW, dTile.COL);
                }
            }
    
            Set<Tile> newlyDiscoveredTiles = new HashSet<>();
            for (Tile nextTile: discoveredTiles) {
                if (nextTile.getTileFace() == TileFace.EMPTY && !nextTile.revealedSurroundingTiles) {
                    newlyDiscoveredTiles.addAll(getSurroundingTiles(nextTile.ROW, nextTile.COL));
                    nextTile.revealedSurroundingTiles = true;
                }
            }
            discoveredTiles.addAll(newlyDiscoveredTiles);
        }
        return false;
    }

    public boolean revealSingle(int row, int col) {
        if (minesGenerated == false) {
            generateBoard(row, col);
        }

        Tile tile = getTile(row, col);

        if (tile.getTileFace() != (TileFace.UNKNOWN)) {
            return false;
        }

        if (tile.LETHAL == true) {
            tile.reveal(TileFace.EXPLODED);
            return true;
        }

        ArrayList<Pair> surroundingTiles = getSurroundingTilesPositions(row, col);
        int surroundingMinesCount = countMinesPairs(surroundingTiles);
        gameBoard[row][col].reveal(intToTileFace.get(surroundingMinesCount));

        return false;
    }

    private boolean checkRevealComplete(Set<Tile> tiles) {
        boolean complete = true;
        for (Tile tile: tiles) {
            complete = complete && tile.isRevealed();
            if (tile.getTileFace() == TileFace.EMPTY) {
                ArrayList<Tile> surroundingTiles = getSurroundingTiles(tile.ROW, tile.COL);
                for (Tile stile: surroundingTiles) {
                    complete = complete && stile.isRevealed();
                }
            }
        }
        return complete;
    }

    /**
     * 
     * @param row selected row
     * @param col selected column
     * @return if player has exploded or not
     */
    public boolean revealSurrounding(int row, int col) {
        boolean exploded = false;

        ArrayList<Pair> surroundingTiles = getSurroundingTilesPositions(row, col);
        
        for (Pair pair: surroundingTiles) {
            Tile newTile = getTile(pair.row(), pair.col());
            if (newTile.getTileFace() == TileFace.UNKNOWN && !newTile.isRevealed()) {
                exploded = exploded || reveal(pair.row(), pair.col());
            }
        }
        
        return exploded;
    }



    /**
     * Generates a new minesweeper mine layout as a 2d boolean matrix, true = mine, false = no mine
     * First clicked on cell will never have any mines on or around it
     * 
     * @param startRow      row of first clicked on cell
     * @param startCol      column of first clicked on cell
     * 
     */
    public void generateBoard(int startRow, int startCol) {
        minesGenerated = true;
        gameBoard = new Tile[HEIGHT][WIDTH];
        boolean[][] mineBoard = new boolean[HEIGHT][WIDTH];
        int minesLeftOver = MINES;

        while (minesLeftOver > 0) {
            int pickedRow = random.nextInt(HEIGHT);
            int pickedCol = random.nextInt(WIDTH);

            if (!cellsAreNearEachother(pickedRow, pickedCol, startRow, startCol)
                && mineBoard[pickedRow][pickedCol] == false) {
                    mineBoard[pickedRow][pickedCol] = true;
                    minesLeftOver --;
            }
        }
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (mineBoard[i][j]) {
                    gameBoard[i][j] = new Tile(i, j, true);
                } else {
                    gameBoard[i][j] = new Tile(i, j, false);
                }
            }
        }
    }
    public void generateNoMineBoard() {
        gameBoard = new Tile[HEIGHT][WIDTH];
        
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                gameBoard[i][j] = new Tile(i, j, false);
            }
        }
    }

    public ArrayList<Tile> getSurroundingTiles(int row, int col) {
        ArrayList<Pair> pairList = getSurroundingTilesPositions(row, col);
        ArrayList<Tile> tileList = new ArrayList<>();
        for (Pair pair: pairList) {
            tileList.add(getTile(pair.row(), pair.col()));
        }
        return tileList;
    }

    public ArrayList<Pair> getSurroundingTilesPositions(int row, int col) {
        ArrayList<Pair> returnList = new ArrayList<>();
        Pair[][] returnMatrix = new Pair[3][3];

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    Pair pair = new Pair(row + i, col + j);
                    returnMatrix[i + 1][j + 1] = pair;
                    returnList.add(pair);
                }
            }
        }

        // Removes tiles that do not exist from returnlist
        if (isOnTopEdge(row, col)) {
            returnList.remove(returnMatrix[0][0]);
            returnList.remove(returnMatrix[0][1]);
            returnList.remove(returnMatrix[0][2]);
        }
        if (isOnBottomEdge(row, col)) {
            returnList.remove(returnMatrix[2][0]);
            returnList.remove(returnMatrix[2][1]);
            returnList.remove(returnMatrix[2][2]);
        }
        if (isOnLeftEdge(row, col)) {
            returnList.remove(returnMatrix[0][0]);
            returnList.remove(returnMatrix[1][0]);
            returnList.remove(returnMatrix[2][0]);
        }
        if (isOnRightEdge(row, col)) {
            returnList.remove(returnMatrix[0][2]);
            returnList.remove(returnMatrix[1][2]);
            returnList.remove(returnMatrix[2][2]);
        }

        return returnList;
    }

    /**
     * @param tiles     the tiles it will check
     * @return          amount of mines in these tiles / amount of lethal tiles
     */
    public int countMines(ArrayList<Tile> tiles) {
        int count = 0;
        for (Tile tile: tiles) {
            if (tile.LETHAL == true) {
                count++;
            }
        }
        return count;
    }
    public int countMinesPairs(ArrayList<Pair> tilePairs) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Pair pair: tilePairs) {
            tiles.add(gameBoard[pair.row()][pair.col()]);
        }
        return countMines(tiles);
    }

    /**
     * @return true if all non-mine tiles have been revealed
     */
    public boolean checkWinCondition() {
        boolean condition = true;
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if (getTile(row, col).getTileFace() == TileFace.UNKNOWN) {
                    condition = condition && getTile(row, col).isLethal() == true;
                }
            }
        }
        return condition;
    }

    public boolean cellsAreNearEachother(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1;
    }
    public boolean cellsAreNearEachother(Tile tile1, Tile tile2) {
        return Math.abs(tile1.ROW - tile2.ROW) <= 1 && Math.abs(tile1.COL - tile2.COL) <= 1;
    }

    public Tile getTile(int row, int col) {return gameBoard[row][col];}

    public boolean isOnLeftEdge(Tile tile) {return tile.COL == 0;}
    public boolean isOnRightEdge(Tile tile) {return tile.COL == WIDTH - 1;}
    public boolean isOnTopEdge(Tile tile) {return tile.ROW == 0;}
    public boolean isOnBottomEdge(Tile tile) {return tile.ROW == HEIGHT - 1;}

    public boolean isOnLeftEdge(int row, int col) {return col == 0;}
    public boolean isOnRightEdge(int row, int col) {return col == WIDTH - 1;}
    public boolean isOnTopEdge(int row, int col) {return row == 0;}
    public boolean isOnBottomEdge(int row, int col) {return row == HEIGHT - 1;}

    public Tile[][] getGameBoard() {return this.gameBoard;}
}
