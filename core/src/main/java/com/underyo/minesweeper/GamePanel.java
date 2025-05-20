package com.underyo.minesweeper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.underyo.minesweeper.objects.GameInstance;
import com.underyo.minesweeper.objects.Tile;
import com.underyo.minesweeper.objects.TileFace;

public class GamePanel extends JComponent {

    private Graphics2D g2;
    public BufferedImage image;
    private int boardStartX = 100;
    private int boardStartY = 100;
    private int tileSize = 16;

    private HashMap<TileFace, String> tileValueMap = new HashMap<>();
    String basePath = "/Sprites/";
    private HashMap<TileFace, String> tileImagePathMap = new HashMap<>();
    Scanner scanner = new Scanner(System.in);

    public GamePanel(int width, int height) {
        // initializes tileValueMap
        for (TileFace tile: TileFace.values()) {
            String value = " ";
            switch (tile) {
                case UNKNOWN -> value = " ";
                case FLAG -> value = "F";
                case EMPTY -> value = "0";
                case ONE -> value = "1";
                case TWO -> value = "2";
                case THREE -> value = "3";
                case FOUR -> value = "4";
                case FIVE -> value = "5";
                case SIX -> value = "6";
                case SEVEN -> value = "7";
                case EIGHT -> value = "8";
                case MINE -> value = "X";
                case EXPLODED -> value = "*";
            }
            tileValueMap.put(tile, value);
        }
        // initializes tileImagePathMap
        for (TileFace tile: TileFace.values()) {
            String value = "";
            switch (tile) {
                case UNKNOWN -> value = "TileUnknown.png";
                case FLAG -> value = "TileFlag.png";
                case EMPTY -> value = "TileEmpty.png";
                case ONE -> value = "Tile1.png";
                case TWO -> value = "Tile2.png";
                case THREE -> value = "Tile3.png";
                case FOUR -> value = "Tile4.png";
                case FIVE -> value = "Tile5.png";
                case SIX -> value = "Tile6.png";
                case SEVEN -> value = "Tile7.png";
                case EIGHT -> value = "Tile8.png";
                case MINE -> value = "TileMine.png";
                case EXPLODED -> value = "TileExploded.png";
            }
            tileImagePathMap.put(tile, value);
        }
    }


    public void start() {
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();

        newGame(10, 5, 20);
    }

    public void newGame(int width, int height, int mines) {

        GameInstance game = new GameInstance(width, height, mines);

        drawBackground();
        drawGame(game);
        render();
        printGame(game);
        boolean won = false;
        while (!won) {
            waitForInput(game);
            drawBackground();
            drawGame(game);
            render();
            printGame(game);

            if (game.checkWinCondition()) {
                won = true;
                System.out.println("YOU WON!!!!!!!!!!!!!!!!!");
            }
        }
    }

    private void drawBackground() {
        g2.setColor(new Color(100, 30, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawGame(GameInstance game) {
        for (int i = 0; i < game.HEIGHT; i++) {
            for (int j = 0; j < game.WIDTH; j++) {
                drawSingleCell(i, j, game);
            }
        }
    }

    private void drawSingleCell(int row, int col, GameInstance game) {
        int x = boardStartX + (col * tileSize);
        int y = boardStartY + (row * tileSize);
        Image cellIcon;
        if (game.getTile(row, col) != null) {
            cellIcon = new ImageIcon(getClass().getResource(basePath + tileImagePathMap.get(game.getTile(row, col).getTileFace()))).getImage();
        } else {
            System.out.println("oop");
            cellIcon = new ImageIcon(getClass().getResource(basePath + tileImagePathMap.get(TileFace.UNKNOWN))).getImage();
        }
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(x, y);
        g2.drawImage(cellIcon, 0, 0, null);
        g2.setTransform(oldTransform);
    }

    private void render() {
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        repaint();
    }

    private void printGame(GameInstance game) {
        for (Tile[] row: game.getGameBoard()) {
            for (Tile tile: row) {
                if (tile != null) {
                    System.out.print("[" + tileValueMap.get(tile.getTileFace()) + "]");
                } else {
                    System.out.print("[ ]");
                }
            }
            System.out.println();
        }
    }

    private void waitForInput(GameInstance game) {
        System.out.print("Enter row and col: ");
        int row = scanner.nextInt();
        int col = scanner.nextInt();

        game.reveal(row, col);

    }
}
