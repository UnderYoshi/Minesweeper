package com.underyo.minesweeper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.underyo.minesweeper.Minesweeper;
import com.underyo.minesweeper.objects.GameInstance;
import com.underyo.minesweeper.screens.helpers.ErrorDisplay;

public final class MainMenuScreen implements Screen {

    private static MainMenuScreen INSTANCE;

    private Minesweeper game;
    private Viewport viewport = new FitViewport(Minesweeper.VIRTUAL_WIDTH, Minesweeper.VIRTUAL_HEIGHT);

    private Texture[] menuBackgrounds = new Texture[1];
    

    private Texture exitButtonActive = new Texture("ui/exit_button_active.png");
    private Texture exitButtonInactive = new Texture("ui/exit_button_inactive.png");
    private Texture startButtonActive = new Texture("ui/start_button_active.png");
    private Texture startButtonInactive = new Texture("ui/start_button_inactive.png");
    private Texture settingsButtonActive = new Texture("ui/settings_button_active.png");
    private Texture settingsButtonInactive = new Texture("ui/settings_button_inactive.png");

    private Texture rightButtonActive = new Texture("ui/right_button_active.png");
    private Texture rightButtonInactive = new Texture("ui/right_button_inactive.png");
    private Texture leftButtonActive = new Texture("ui/left_button_active.png");
    private Texture leftButtonInactive = new Texture("ui/left_button_inactive.png");

    BitmapFont font;
    BitmapFont errorFont;
    BitmapFont titleFont;
    ErrorDisplay errorDisplay;

    private int buttonScale = 5;
    private int smallButtonScale = 4;

    private int buttonWidth = 64 * buttonScale;
    private int buttonHeight = 16 * buttonScale;

    private int smallButtonSize = 16 * smallButtonScale;

    private MainMenuScreen(Minesweeper mainGame) {
        this.game = mainGame;
        menuBackgrounds[0] = new Texture("backgrounds/menu/background1.png");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/ByteBounce.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 60;
        font = generator.generateFont(params);
        params.size = 120;
        errorFont = generator.generateFont(params);
        params.size = 130;
        titleFont = generator.generateFont(params);
        generator.dispose();

        errorDisplay = new ErrorDisplay(errorFont);


    }

    public static MainMenuScreen getInstance(Minesweeper mainGame) {
        if(INSTANCE == null) {
            INSTANCE = new MainMenuScreen(mainGame);
        }
        return INSTANCE;
    }

    /**
     * Resets the main menu to default settings
     */
    public void reset() {

    }

    @Override
    public void show() {
        
    }

    private boolean lastFramePressed = false;
    private int boardWidth = 10;
    private int boardHeight = 10;
    private int minesAmount = 20;

    private final HoldRepeater boardWidthUpRepeater = new HoldRepeater(+1);
    private final HoldRepeater boardWidthDownRepeater = new HoldRepeater(-1);
    private final HoldRepeater boardHeightUpRepeater = new HoldRepeater(+1);
    private final HoldRepeater boardHeightDownRepeater = new HoldRepeater(-1);
    private final HoldRepeater minesUpRepeater = new HoldRepeater(+1);
    private final HoldRepeater minesDownRepeater = new HoldRepeater(-1);

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
    
        
        int realMouseX = Gdx.input.getX();
        int realMouseY = Gdx.input.getY();
        Vector3 virtualMouse = new Vector3(realMouseX, realMouseY, 0);
        viewport.unproject(virtualMouse);
        float mouseX = virtualMouse.x;
        float mouseY = virtualMouse.y;

        boolean mouseHeld = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean mouseJustReleased = !mouseHeld && lastFramePressed;
        boolean mouseJustClicked = Gdx.input.justTouched();


        int exitButtonPosX = ((Minesweeper.VIRTUAL_WIDTH/2 - buttonWidth/2));
        int exitButtonPosY = 10;
        boolean mouseOnExitButton = (mouseX > exitButtonPosX && mouseX < exitButtonPosX + buttonWidth && mouseY > exitButtonPosY && mouseY < exitButtonPosY + buttonHeight);

        int startButtonPosX = ((Minesweeper.VIRTUAL_WIDTH - buttonWidth) / 2);
        // int startButtonPosY = ((Minesweeper.VIRTUAL_HEIGHT - buttonHeight) / 2);
        int startButtonPosY = 90;
        boolean mouseOnStartButton = (mouseX > startButtonPosX && mouseX < startButtonPosX + buttonWidth && mouseY > startButtonPosY && mouseY < startButtonPosY + buttonHeight);

        int smallButtonOffset = 155;
        int buttonPadding = 10;

        int boardWidthUpButtonPosX = Minesweeper.VIRTUAL_WIDTH / 2 - smallButtonSize/2 + smallButtonOffset;
        int boardWidthUpButtonPosY = Minesweeper.VIRTUAL_HEIGHT / 2 - smallButtonSize / 2 + smallButtonSize + buttonPadding;
        boolean mouseOnBoardWidthUpButton = (mouseX > boardWidthUpButtonPosX && mouseX < boardWidthUpButtonPosX + smallButtonSize && mouseY > boardWidthUpButtonPosY && mouseY < boardWidthUpButtonPosY + smallButtonSize);

        int boardWidthDownButtonPosX = Minesweeper.VIRTUAL_WIDTH / 2 - smallButtonSize/2 - smallButtonOffset;
        int boardWidthDownButtonPosY = Minesweeper.VIRTUAL_HEIGHT / 2 - smallButtonSize / 2 + smallButtonSize + buttonPadding;
        boolean mouseOnBoardWidthDownButton = (mouseX > boardWidthDownButtonPosX && mouseX < boardWidthDownButtonPosX + smallButtonSize && mouseY > boardWidthDownButtonPosY && mouseY < boardWidthDownButtonPosY + smallButtonSize);

        int boardHeightUpButtonPosX = Minesweeper.VIRTUAL_WIDTH / 2 - smallButtonSize/2 + smallButtonOffset;
        int boardHeightUpButtonPosY = Minesweeper.VIRTUAL_HEIGHT / 2 - smallButtonSize / 2;
        boolean mouseOnBoardHeightUpButton = (mouseX > boardHeightUpButtonPosX && mouseX < boardHeightUpButtonPosX + smallButtonSize && mouseY > boardHeightUpButtonPosY && mouseY < boardHeightUpButtonPosY + smallButtonSize);

        int boardHeightDownButtonPosX = Minesweeper.VIRTUAL_WIDTH / 2 - smallButtonSize/2 - smallButtonOffset;
        int boardHeightDownButtonPosY = Minesweeper.VIRTUAL_HEIGHT / 2 - smallButtonSize / 2;
        boolean mouseOnBoardHeightDownButton = (mouseX > boardHeightDownButtonPosX && mouseX < boardHeightDownButtonPosX + smallButtonSize && mouseY > boardHeightDownButtonPosY && mouseY < boardHeightDownButtonPosY + smallButtonSize);

        int minesUpButtonPosX = Minesweeper.VIRTUAL_WIDTH / 2 - smallButtonSize/2 + smallButtonOffset;
        int minesUpButtonPosY = Minesweeper.VIRTUAL_HEIGHT / 2 - smallButtonSize / 2 - smallButtonSize - buttonPadding;
        boolean mouseOnMinesUpButton = (mouseX > minesUpButtonPosX && mouseX < minesUpButtonPosX + smallButtonSize && mouseY > minesUpButtonPosY && mouseY < minesUpButtonPosY + smallButtonSize);

        int minesDownButtonPosX = Minesweeper.VIRTUAL_WIDTH / 2 - smallButtonSize/2 - smallButtonOffset;
        int minesDownButtonPosY = Minesweeper.VIRTUAL_HEIGHT / 2 - smallButtonSize / 2 - smallButtonSize - buttonPadding;
        boolean mouseOnMinesDownButton = (mouseX > minesDownButtonPosX && mouseX < minesDownButtonPosX + smallButtonSize && mouseY > minesDownButtonPosY && mouseY < minesDownButtonPosY + smallButtonSize);

        GlyphLayout titleLayout = new GlyphLayout();
        GlyphLayout layout1 = new GlyphLayout();
        GlyphLayout layout2 = new GlyphLayout();
        GlyphLayout layout3 = new GlyphLayout();
        String widthText = "Width: " + boardWidth;
        String heightText = "Height: " + boardHeight;
        String minesText = "Mines: " + minesAmount;
        String titleText = "Minesweeper";

        titleLayout.setText(titleFont, titleText);

        layout1.setText(font, widthText);
        layout2.setText(font, heightText);
        layout3.setText(font, minesText);
        game.batch.begin(); 
        // draw buttons (long ass function)
        {
            game.batch.draw(menuBackgrounds[0], 0, 0);
            // Rendering buttons
            if (mouseOnExitButton && mouseHeld) {
                game.batch.draw(exitButtonActive, exitButtonPosX, exitButtonPosY, buttonWidth, buttonHeight);
            } else {
                game.batch.draw(exitButtonInactive, exitButtonPosX, exitButtonPosY, buttonWidth, buttonHeight);
            }

            if (mouseOnStartButton && mouseHeld) {
                game.batch.draw(startButtonActive, startButtonPosX, startButtonPosY, buttonWidth, buttonHeight);
            } else {
                game.batch.draw(startButtonInactive, startButtonPosX, startButtonPosY, buttonWidth, buttonHeight);
            }

            if (mouseOnBoardWidthUpButton && mouseHeld) {
                game.batch.draw(rightButtonActive, boardWidthUpButtonPosX, boardWidthUpButtonPosY, smallButtonSize, smallButtonSize);
            } else {
                game.batch.draw(rightButtonInactive, boardWidthUpButtonPosX, boardWidthUpButtonPosY, smallButtonSize, smallButtonSize);
            }

            if (mouseOnBoardWidthDownButton && mouseHeld) {
                game.batch.draw(leftButtonActive, boardWidthDownButtonPosX, boardWidthDownButtonPosY, smallButtonSize, smallButtonSize);
            } else {
                game.batch.draw(leftButtonInactive, boardWidthDownButtonPosX, boardWidthDownButtonPosY, smallButtonSize, smallButtonSize);
            }

            if (mouseOnBoardHeightUpButton && mouseHeld) {
                game.batch.draw(rightButtonActive, boardHeightUpButtonPosX, boardHeightUpButtonPosY, smallButtonSize, smallButtonSize);
            } else {
                game.batch.draw(rightButtonInactive, boardHeightUpButtonPosX, boardHeightUpButtonPosY, smallButtonSize, smallButtonSize);
            }

            if (mouseOnBoardHeightDownButton && mouseHeld) {
                game.batch.draw(leftButtonActive, boardHeightDownButtonPosX, boardHeightDownButtonPosY, smallButtonSize, smallButtonSize);
            } else {
                game.batch.draw(leftButtonInactive, boardHeightDownButtonPosX, boardHeightDownButtonPosY, smallButtonSize, smallButtonSize);
            }

            if (mouseOnMinesUpButton && mouseHeld) {
                game.batch.draw(rightButtonActive, minesUpButtonPosX, minesUpButtonPosY, smallButtonSize, smallButtonSize);
            } else {
                game.batch.draw(rightButtonInactive, minesUpButtonPosX, minesUpButtonPosY, smallButtonSize, smallButtonSize);
            }

            if (mouseOnMinesDownButton && mouseHeld) {
                game.batch.draw(leftButtonActive, minesDownButtonPosX, minesDownButtonPosY, smallButtonSize, smallButtonSize);
            } else {
                game.batch.draw(leftButtonInactive, minesDownButtonPosX, minesDownButtonPosY, smallButtonSize, smallButtonSize);
            }
        }

        font.draw(game.batch, widthText, Minesweeper.VIRTUAL_WIDTH / 2 - layout1.width / 2, boardWidthUpButtonPosY + smallButtonSize/2 + 8);
        font.draw(game.batch, heightText, Minesweeper.VIRTUAL_WIDTH / 2- layout2.width / 2, boardHeightUpButtonPosY + smallButtonSize/2 + 8);
        font.draw(game.batch, minesText, Minesweeper.VIRTUAL_WIDTH / 2- layout3.width / 2, minesUpButtonPosY + smallButtonSize/2 + 8);
        titleFont.draw(game.batch, titleText, Minesweeper.VIRTUAL_WIDTH / 2- titleLayout.width / 2, Minesweeper.VIRTUAL_HEIGHT - 80);

        errorDisplay.render(game.batch, Gdx.graphics.getDeltaTime(),
                    Minesweeper.VIRTUAL_WIDTH,
                    Minesweeper.VIRTUAL_HEIGHT);
        game.batch.end();

        
        // Button actions
        if (mouseOnExitButton && mouseJustReleased) {
            Gdx.app.exit();
        }
        else if (mouseOnStartButton && mouseJustReleased) {
            String startString = startGame(boardWidth, boardHeight, minesAmount);
            if (startString != null) {
                errorDisplay.displayError(startString);
            }
        } else {
            // For each UI +/- button, call the associated repeater
            int dw = boardWidthUpRepeater.update(mouseOnBoardWidthUpButton && mouseHeld, mouseOnBoardWidthUpButton && mouseJustClicked, delta)
                + boardWidthDownRepeater.update(mouseOnBoardWidthDownButton && mouseHeld, mouseOnBoardWidthDownButton && mouseJustClicked, delta);
            if (dw != 0) {
                boardWidth = Math.max(3, Math.min(1000, boardWidth + dw));
            }

            int dh = boardHeightUpRepeater.update(mouseOnBoardHeightUpButton && mouseHeld, mouseOnBoardHeightUpButton && mouseJustClicked, delta)
                + boardHeightDownRepeater.update(mouseOnBoardHeightDownButton && mouseHeld, mouseOnBoardHeightDownButton && mouseJustClicked, delta);
            if (dh != 0) {
                boardHeight = Math.max(3, Math.min(1000, boardHeight + dh));
            }

            int dm = minesUpRepeater.update(mouseOnMinesUpButton && mouseHeld, mouseOnMinesUpButton && mouseJustClicked, delta)
                + minesDownRepeater.update(mouseOnMinesDownButton && mouseHeld, mouseOnMinesDownButton && mouseJustClicked, delta);
            if (dm != 0) {
                minesAmount = Math.max(1, Math.min(100000 - 10, minesAmount + dm));
            }
        }
        

        lastFramePressed = mouseHeld;
    }

    /**
     * 
     * @param width board width
     * @param height board height
     * @return error text if game could not be started, otherwise switches scene
     */
    private String startGame(int width, int height, int mines) {

        if (boardHeight * boardWidth - 9 < minesAmount) { // error cases

            return "Invalid Input";
        }

        GameInstance currGameInstance = new GameInstance(width, height, mines);
        game.setScreen(new GameScreen(game, currGameInstance));
        return "";
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        
    }


    private static class HoldRepeater {
        private final int direction; // +1 for up, -1 for down

        // tuning values (feel free to tweak)
        private final float initialDelay = 0.25f;   // time before first repeated step after initial click
        private final float initialInterval = 0.20f; // first repeat interval
        private final float accelFactor = 0.93f;    // multiply interval by this each repeat ( <1 => faster)
        private final float minInterval = 0.01f;    // fastest interval

        private float timeSinceLast = 0f;
        private float nextInterval = initialDelay;
        // constructor
        HoldRepeater(int direction) {
            this.direction = direction;
            resetHold();
        }

        public void resetHold() {
            timeSinceLast = 0f;
            nextInterval = initialDelay;
        }

        /**
         * Call every frame with whether the button is currently pressed & whether it was just clicked.
         * Returns an integer delta (can be negative) representing how many steps to apply this frame.
         */
        public int update(boolean isPressed, boolean justClicked, float delta) {
            if (!isPressed) {
                // reset when not pressed
                resetHold();
                return 0;
            }

            int totalSteps = 0;

            if (justClicked) {
                // immediate single step on press
                totalSteps += direction;
                // prepare for repeats after initialDelay
                timeSinceLast = 0f;
                nextInterval = initialDelay;
                return totalSteps;
            }

            // accumulate time and emit as many repeats as fit into the elapsed time (handles lag)
            timeSinceLast += delta;
            while (timeSinceLast >= nextInterval) {
                totalSteps += direction;
                timeSinceLast -= nextInterval;
                // speed up future repeats
                nextInterval = Math.max(minInterval, nextInterval * accelFactor);
            }
            return totalSteps;
        }
    }
}