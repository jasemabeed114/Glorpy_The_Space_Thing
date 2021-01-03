package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class FireBall {
    private final int maxX;
    private final int velocity;
    private final int frameLengthInMilliseconds;
    private final Rect frameToDraw;
    private final RectF whereToDraw;
    int bitFrames;
    Rect hitBox;
    Bitmap bitmap;
    int x, y;
    int health;
    int frameWidth;
    int frameHeight;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    float scaleFactorX;
    float scaleFactorY;
    float bitScale;
    private int currentFrame;
    private long lastFrameChangeTime;

    public FireBall(Context context, int positionX, int positionY, int screenX, int screenY) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        x = positionX;
        y = positionY;
        maxX = screenX;
        health = 1 + (DataHolder.powerMod / 15);
        bitFrames = 4;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.glorpy_fireball);
        frameHeight = (int) (175 * bitScale);
        frameWidth = (int) (175 * bitScale);
        velocity = (20 + DataHolder.speedMod / 6) * (int) scaleFactorX;
        // hit box is adjusted for ideal collision detection
        hitBox = new Rect(x + (int) (60 * scaleFactorX), y + (int) (50 * scaleFactorY),
                x + frameWidth - (int) (40 * scaleFactorX),
                y + frameHeight - (int) (60 * scaleFactorY));
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLengthInMilliseconds = 50;
        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(x, 0, x + frameWidth, y + frameHeight);
    }

    public void update() {
        x += velocity;
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + frameWidth - (int) (40 * scaleFactorX);
        hitBox.bottom = y + frameHeight - (int) (60 * scaleFactorY);
    }

    public void animationControl(Canvas canvas, Paint paint) {
        whereToDraw.set(x, y, x + frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
    }

    private void getCurrentFrame() {
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

    private float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        if (screenX < 1) {
            screenX = 1;
        }
        return screenX;
    }

    private float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        if (screenY < 1) {
            screenY = 1;
        }
        return screenY;
    }

    private float bitmapScale(float scaleX, float scaleY) {
        if (scaleX > scaleY) {
            return scaleX;
        } else {
            return scaleY;
        }
    }

    public int getHealth() {
        return health;
    }

    public void updateHealth() {
        health--;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMaxX() {
        return maxX;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public int getFrameWidth() {
        return frameWidth;
    }
}


