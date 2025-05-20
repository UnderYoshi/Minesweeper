package com.underyo.minesweeper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.underyo.minesweeper.Minesweeper;

public class MainMenuScreen implements Screen {

    private Minesweeper game;
    private Viewport viewport = new FitViewport(Minesweeper.VIRTUAL_WIDTH, Minesweeper.VIRTUAL_HEIGHT);

    private Texture exitButtonActive = new Texture("ui/exit_button_active.png");
    private Texture exitButtonInactive = new Texture("ui/exit_button_inactive.png");
    private Texture startButtonActive = new Texture("ui/start_button_active.png");
    private Texture startButtonInactive = new Texture("ui/start_button_inactive.png");
    private Texture settingsButtonActive = new Texture("ui/settings_button_active.png");
    private Texture settingsButtonInactive = new Texture("ui/settings_button_inactive.png");

    public MainMenuScreen(Minesweeper mainGame) {
        this.game = mainGame;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        game.batch.begin();
        game.batch.draw(exitButtonInactive, 100, 100);
        game.batch.end();
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
}