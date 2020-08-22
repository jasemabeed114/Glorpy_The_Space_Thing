package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Glorpy {

    int bitFrames;
    private Rect hitBox;
    private Bitmap bitmap;
    private int x, y;
    private boolean movingUp;
    private boolean movingDown;
    private boolean isShooting;
    private int maxY;
    private int minY;
    private int health;
    private int velocity;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame;
    private long lastFrameChangeTime;
    private int frameLengthInMilliseconds;
    private Rect frameToDraw;
    private RectF whereToDraw;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    private float scaleFactorX;
    private float scaleFactorY;
    private int fireDamage;

    public Glorpy(Context context, int screenX, int screenY) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        float bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        x = (int) (50 * scaleFactorX);
        y = screenY / 2;
        health = 100 + DataHolder.lifeMod;
        bitFrames = 6;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.glorpy);
        frameHeight = (int) (125 * bitScale);
        frameWidth = (int) (165 * bitScale);
        hitBox = new Rect(x, y, frameWidth - ((int) (50 * scaleFactorX)), frameHeight);
        maxY = screenY - frameHeight;
        minY = 0;
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLengthInMilliseconds = 50;
        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(x, 0, x + frameWidth, frameHeight);
        fireDamage = -10 - (DataHolder.powerMod/2);


    }

    public void update() {
        if (movingDown) {
            velocity = (int) ((-10 - (DataHolder.speedMod/3)) * scaleFactorY);
        } else if (movingUp) {
            velocity = (int) ((10 + (DataHolder.speedMod/3)) * scaleFactorY);
        } else {
            velocity = 0;
        }
        y -= velocity;
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + frameWidth - (int) (50 * scaleFactorX);
        hitBox.bottom = y + frameHeight;
    }

    public void animationControl(Canvas canvas, Paint paint) {
        //to be put in the draw method in the game loop
        whereToDraw.set(x, y, frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);

    }

    private void getCurrentFrame() {

        long time = System.currentTimeMillis();
        if (isShooting) {
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                currentFrame++;
                if (currentFrame >= bitFrames) {
                    currentFrame = 0;
                    isShooting = false;
                }
            }
        } else {
            currentFrame = 0;
        }
        //update the left and right values of the source of
        //the next frame on the sprite sheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
        frameToDraw.top = 0;
        frameToDraw.bottom = frameHeight;

    }

    private float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        return screenX;
    }

    private float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        return screenY;
    }

    private float bitmapScale(float scaleX, float scaleY) {
        if (scaleX > scaleY) {
            return scaleX;
        } else {
            return scaleY;
        }
    }


    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getHealth() {
        return health;
    }

    public Rect gethitBox() {
        return hitBox;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public void setIsShooting(boolean isShooting) {
        this.isShooting = isShooting;
    }

    public int getFireDamage() {
        return fireDamage;
    }
}
