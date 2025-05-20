package com.underyo.minesweeper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.underyo.minesweeper.objects.GameInstance;
import com.underyo.minesweeper.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Minesweeper extends Game {
    public SpriteBatch batch;

    GameInstance currGameInstance;
    
    public static final int VIRTUAL_WIDTH  = 800;
    public static final int VIRTUAL_HEIGHT = 600;
    

    @Override
    public void create() {
        batch = new SpriteBatch();
        //setScreen(new MainMenuScreen(this));

        currGameInstance = new GameInstance(100, 100, 2000);
        setScreen(new GameScreen(this, currGameInstance));
        System.out.println("done");
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
