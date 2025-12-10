package com.underyo.minesweeper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.underyo.minesweeper.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Minesweeper extends Game {

    public SpriteBatch batch;

    // GameInstance currGameInstance;
    
    public static final int VIRTUAL_WIDTH  = 800;
    public static final int VIRTUAL_HEIGHT = 600;
    

    @Override
    public void create() {
        batch = new SpriteBatch();

        setScreen(MainMenuScreen.getInstance(this));

        // // create a new game
        // GameInstance currGameInstance = new GameInstance(1000, 1000, 2);
        // setScreen(new GameScreen(this, currGameInstance));
        // System.out.println("done");
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
