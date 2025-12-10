package com.underyo.minesweeper.screens.helpers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ErrorDisplay {
    private String message;
    private float alpha = 0f;
    private final float FADE_SPEED = 0.5f;
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();

    private boolean active = false;

    public ErrorDisplay(BitmapFont font) {
        this.font = font;
    }

    /** Call this to display an error once */
    public void displayError(String errorMessage) {
        this.message = errorMessage;
        this.alpha = 1f; // fully visible
        this.active = true;
    }

    public void render(SpriteBatch batch, float delta, float screenWidth, float screenHeight) {
        if (!active || alpha <= 0f) return;

        layout.setText(font, message);
        float x = screenWidth / 2f - layout.width / 2f;
        float y = screenHeight / 2f + layout.height / 2f;

        font.setColor(1f, 0f, 0f, alpha);
        font.draw(batch, layout, x, y);

        // fade out
        alpha -= FADE_SPEED * delta;
        if (alpha <= 0f) {
            alpha = 0f;
            active = false; // finished
        }
    }
}