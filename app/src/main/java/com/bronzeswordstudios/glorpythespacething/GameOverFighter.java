package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GameOverFighter extends GraphicElement {
    public static final int FIGHTER_STANDARD = 0;
    public static final int FIGHTER_SPINNING = 1;
    private int speedX;
    private final int speedY;
    private final int shrinkSpeed;
    private final long timeTillSlowDown;
    private final long lastSlowdownTime;
    private final int type;

    public GameOverFighter(Context context, int positionX, int positionY, int screenX, int screenY, int type) {
        super(context, positionX, positionY, screenX, screenY);
        // note these are inverse because we are in portrait mode
        speedY = 25;
        this.type = type;
        lastSlowdownTime = System.currentTimeMillis();
        timeTillSlowDown = 500;
        shrinkSpeed = 7;
        frameWidth = 500;
        frameHeight = 500;
        if (type == FIGHTER_STANDARD) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over_fighter1);
            bitFrames = 4;
            bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
            speedX = 13;
            frameLengthInMilliseconds = 100;
        }
        if (type == FIGHTER_SPINNING) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_over_fighter2);
            bitFrames = 8;
            bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
            speedX = 15;
            frameLengthInMilliseconds = 50;
        }

    }

    @Override
    public void update() {
        if (type == FIGHTER_STANDARD) {
            y -= speedY;
            x += speedX;
        }
        if (type == FIGHTER_SPINNING) {
            y -= speedY;
            x -= speedX;
        }
        if (timeTillSlowDown <= System.currentTimeMillis() - lastSlowdownTime) {
            if (frameWidth > 5 && frameHeight > 5) {
                frameWidth -= shrinkSpeed;
                frameHeight -= shrinkSpeed;
                bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
            } else {
                needDelete = true;
            }
        }
    }

    @Override
    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= bitFrames) {
                currentFrame = 0;
            }
        }
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
        frameToDraw.top = 0;
        frameToDraw.bottom = frameHeight;
    }

    @Override
    public void animationControl(Canvas canvas, Paint paint) {
        whereToDraw.set(x, y, x + frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
    }
}
