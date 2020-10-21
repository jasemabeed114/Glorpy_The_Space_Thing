package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;

public class GameOverBackground extends MainBackgroundView {
    AudioHandler audioHandler;
    boolean audioPlayed;
    private final ArrayList<GameOverFighter> gameOverFighters;
    private GlorpyKO glorpyKO;

    public GameOverBackground(Context context, int screenX, int screenY) {
        super(context, screenX, screenY);
        gameOverFighters = new ArrayList<>();
        gameOverFighters.add(new GameOverFighter(context, -300, screenY + 300, screenX, screenY, GameOverFighter.FIGHTER_STANDARD));
        gameOverFighters.add(new GameOverFighter(context, screenX + 300, screenY + 300, screenX, screenY, GameOverFighter.FIGHTER_SPINNING));
        glorpyKO = new GlorpyKO(context, screenX, screenY, 0, screenY);
        audioHandler = new AudioHandler(context);
        audioPlayed = false;
    }

    @Override
    void update() {
        super.update();
        if (glorpyKO != null) {
            glorpyKO.update();
            if (glorpyKO.getY() < -glorpyKO.getFrameHeight()) {
                glorpyKO = null;
            }
        }
        if (gameOverFighters.size() > 0) {
            for (int i = gameOverFighters.size() - 1; i >= 0; i--) {
                GameOverFighter gameOverFighter = gameOverFighters.get(i);
                gameOverFighter.update();
                if (!audioPlayed && gameOverFighter.getY() <= screenY) {
                    audioHandler.playThrust();
                    audioPlayed = true;
                }
                if (gameOverFighter.isNeedDelete()) {
                    gameOverFighters.remove(gameOverFighter);
                }
            }
        }
    }

    @Override
    void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            for (Star star : stars) {
                canvas.drawBitmap(star.getBitmap(), star.getX(), star.getY(), paint);
            }
            if (glorpyKO != null) {
                canvas.drawBitmap(glorpyKO.getBitmap(), glorpyKO.getX(), glorpyKO.getY(), paint);
            }
            for (GameOverFighter gameOverFighter : gameOverFighters) {
                gameOverFighter.animationControl(canvas, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }
}
