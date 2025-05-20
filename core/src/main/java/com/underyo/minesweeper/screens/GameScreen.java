package com.underyo.minesweeper.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.underyo.minesweeper.Minesweeper;
import com.underyo.minesweeper.objects.GameInstance;
import com.underyo.minesweeper.objects.TileFace;

public class GameScreen implements Screen {

    private int boardStartX = 0;
    private int boardStartY = 0;
    private final int tileSize = 16;

    private boolean renderedOnce = false;

    // Rendering variables
    private ShaderProgram metalShader;
    private Texture normalMap;

    // Cameras and viewports
    private OrthographicCamera screenCam = new OrthographicCamera();
    private OrthographicCamera gameBoardCam = new OrthographicCamera();
    private Viewport screenViewport;
    private Viewport gameBoardViewport;
    private final float PAN_SPEED = 200f;
    private float currentZoom = 1f;
    private final float MIN_ZOOM = 0.06f;
    private final float MAX_ZOOM = 10f;
    

    // Input values
    private float mouseScrollAmount;
    private boolean mouseHeldLeft = false;
    private boolean mouseHeldRight = false;
    private boolean mouseHeldLeftBuffer = false;
    private boolean mouseHeldRightBuffer = false;
    private boolean mouseInGameBoard = false;
    private float boardMouseXprevious;
    private float boardMouseYprevious;
    private float mouseRightLastPressedX;
    private float mouseRightLastPressedY;


    private Minesweeper game;
    private GameInstance gameInstance;

    private String basePath = "sprites/";
    private HashMap<TileFace, Texture> tileImageTextureMap = new HashMap<>();
    private Texture[] backgrounds = new Texture[1];

    public GameScreen(Minesweeper mainGame, GameInstance gameInstance) {
        this.game = mainGame;
        this.gameInstance = gameInstance;

        this.screenViewport = new FitViewport(Minesweeper.VIRTUAL_WIDTH, Minesweeper.VIRTUAL_HEIGHT, screenCam);
        this.gameBoardViewport = new FitViewport(Minesweeper.VIRTUAL_WIDTH, Minesweeper.VIRTUAL_HEIGHT, gameBoardCam);

        // initialize shaders
        ShaderProgram.pedantic = false;
        metalShader = new ShaderProgram(
            Gdx.files.internal("shaders/metal.vert"),
            Gdx.files.internal("shaders/metal.frag")
        );
        if (!metalShader.isCompiled()) {
            Gdx.app.error("Shader", metalShader.getLog());
        }
        normalMap = new Texture("sprites/TileNormal.png");

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
            tileImageTextureMap.put(tile, new Texture(basePath + value));
        }
        
        backgrounds[0] = new Texture("backgrounds/background1.png");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                // amountY > 0 means scroll down; < 0 means scroll up
                mouseScrollAmount = amountY;
                return true;
            }
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    mouseHeldLeft = true;
                } else if (button == Input.Buttons.RIGHT) {
                    mouseHeldRight = true;

                    int mouseX = Gdx.input.getX();
                    int mouseY = Gdx.input.getY();

                    mouseRightLastPressedX = mouseX;
                    mouseRightLastPressedY = mouseY;
                }
                return true;
            }
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    mouseHeldLeft = false;
                } else if (button == Input.Buttons.RIGHT) {
                    mouseHeldRight = false;
                }
                return true;
            }
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (!renderedOnce) {
            renderedOnce = true;
            gameBoardCam.position.set(gameInstance.WIDTH * tileSize / 2, gameInstance.HEIGHT * tileSize / 2, 0);
        }

        float realWidth = gameBoardViewport.getScreenWidth();
        float realHeight = gameBoardViewport.getScreenHeight();

         // Checks mouse position on screen
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        // Checks virtual mouse position over the game board
        Vector3 virtualMouse = new Vector3(mouseX, mouseY, 0);
        gameBoardViewport.unproject(virtualMouse);
        float boardMouseX = virtualMouse.x;
        float boardMouseY = virtualMouse.y;
        //System.out.println(String.format("X: %.5s, Y: %.5s", boardMouseX, boardMouseY));

        // Checks if mouse if on the gameboard:
        if (0 < boardMouseX && boardMouseX < gameInstance.WIDTH * tileSize && 0 < boardMouseY && boardMouseY < gameInstance.HEIGHT * tileSize) {
            mouseInGameBoard = true;
        } else {
            mouseInGameBoard = false;
        }

        for (int i = 0; i < gameInstance.HEIGHT; i++) {
            for (int j = 0; j < gameInstance.WIDTH; j++) {
                gameInstance.getTile(i, j).unhighlight();
            }
        }
        // Handles mouse clicking logic:
        // mouseleft is being held down:
        if (mouseHeldLeft) {
            mouseHeldLeftBuffer = true;
            if (mouseInGameBoard) {
                int tileX = (int)(boardMouseX / tileSize);
                int tileY = (int)(boardMouseY / tileSize);
                if (gameInstance.getTile(tileY, tileX).getTileFace() == TileFace.UNKNOWN) {
                    gameInstance.getTile(tileY, tileX).highlight();
                }
            }
        } else if (mouseHeldLeftBuffer) {
            //mouseleft has been released
            if (mouseInGameBoard) {
                int tileX = (int)(boardMouseX / tileSize);
                int tileY = (int)(boardMouseY / tileSize);
                gameInstance.getTile(tileY, tileX).unhighlight();
                TileFace face = gameInstance.getTile(tileY, tileX).getTileFace();
                if (face == TileFace.ONE
                || face == TileFace.TWO
                || face == TileFace.THREE
                || face == TileFace.FOUR
                || face == TileFace.FIVE
                || face == TileFace.SIX
                || face == TileFace.SEVEN
                || face == TileFace.EIGHT) {
                    gameInstance.revealSurrounding(tileY, tileX);
                } else {
                    gameInstance.reveal(tileY, tileX);
                }
            }
            mouseHeldLeftBuffer = false;
        }

        // mouseright is being held down:
        if (mouseHeldRight) {
            mouseHeldRightBuffer = true;

            float dx = mouseX - boardMouseXprevious;
            float dy = mouseY - boardMouseYprevious;

            float dxReal = dx * (float)Math.pow(currentZoom, 3) * (Minesweeper.VIRTUAL_WIDTH / (realWidth * 1.25f)) * 1.248f;
            float dyReal = dy * (float)Math.pow(currentZoom, 3) * (Minesweeper.VIRTUAL_WIDTH / (realHeight * 1.25f)) * 0.93f;
            gameBoardCam.translate(-dxReal, dyReal);

        } else if (mouseHeldRightBuffer) {
            //mouseright has been released
            if (mouseInGameBoard
            && Math.abs(mouseRightLastPressedX - mouseX) < 10
            && Math.abs(mouseRightLastPressedY - mouseY) < 10) {
                int tileX = (int)(boardMouseX / tileSize);
                int tileY = (int)(boardMouseY / tileSize);
                gameInstance.getTile(tileY, tileX).toggleFlag();
                mouseHeldRightBuffer = false;
            }
        }
        boardMouseXprevious = mouseX;
        boardMouseYprevious = mouseY;
        

        // Checks if mouse wheel is scrolling and adjust zoom
        if (mouseScrollAmount > 0 && (float)Math.pow(currentZoom, 3) <= MAX_ZOOM) {
            currentZoom = currentZoom + 0.02f;
        } else if (mouseScrollAmount < 0 && (float)Math.pow(currentZoom, 3) >= MIN_ZOOM) {
            currentZoom = currentZoom - 0.02f;
        }
        mouseScrollAmount = 0;
        // Sets zoom
        gameBoardCam.zoom = (float)Math.pow(currentZoom, 3); // zooms to the middle
        //gameBoardCam.position.set(lastZoomPosX, lastZoomPosY, 0); // zooms towards mouse

        // Moves the gameboard based on wasd inputs
        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  {dx  = -PAN_SPEED * delta;}
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {dx  =  PAN_SPEED * delta;}
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    {dy  =  PAN_SPEED * delta;}
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  {dy  = -PAN_SPEED * delta;}
        if (dx != 0 || dy != 0) {
            gameBoardCam.position.add(dx, dy, 0);
        }

        gameBoardCam.update();

        // Sets viewport to screen viewport and draws background, hud, etc.
        screenViewport.apply();
        game.batch.setProjectionMatrix(screenCam.combined);
        game.batch.begin();
            game.batch.draw(backgrounds[0], 0, 0);
        game.batch.end();

        // Sets viewport to board and draws game board
        gameBoardViewport.apply();
        game.batch.setProjectionMatrix(gameBoardCam.combined);

        game.batch.begin();
            drawTiles();
        game.batch.end();

    }

    private void drawTiles() {
        for (int i = 0; i < gameInstance.HEIGHT; i++) {
            for (int j = 0; j < gameInstance.WIDTH; j++) {
                drawSingleCell(i, j);
            }
        }
    }

    private void drawSingleCell(int row, int col) {
        int x = boardStartX + (col * tileSize);
        int y = boardStartY + (row * tileSize);

        game.batch.draw(tileImageTextureMap.get(gameInstance.getTile(row, col).getTileFace()), x, y);
    }

    @Override
    public void resize(int width, int height) {
        gameBoardViewport.update(width, height, false);
        screenViewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
