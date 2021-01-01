package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class GraphicElement {
    // a simple class to provide base code for animated game objects,
    // other than enemies, that appear during the game
    int damage;
    int bitFrames;
    Rect hitBox;
    Bitmap bitmap;
    int x, y;
    int frameWidth;
    int frameHeight;
    int currentFrame;
    long lastFrameChangeTime;
    int frameLengthInMilliseconds;
    Rect frameToDraw;
    RectF whereToDraw;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    float scaleFactorX;
    float scaleFactorY;
    boolean needDelete;
    Context context;
    float bitScale;

    public GraphicElement(Context context, int positionX, int positionY, int screenX, int screenY) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        this.context = context;
        bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        // bitFrames must be updated per game object's sprite sheet. How many frames does it have?
        bitFrames = 5;
        // bitmap to be updated in the child class
        bitmap = null;
        // frame sizes to be updated in child class
        frameWidth = 50 * (int) bitScale;
        frameHeight = 50 * (int) bitScale;
        x = positionX;
        y = positionY;
        currentFrame = 0;
        lastFrameChangeTime = 0;
        hitBox = new Rect(x, y, x + frameWidth, y + frameHeight);
        // may have to be adjusted based on desired animation frame rate
        frameLengthInMilliseconds = 100;
        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(x, y, x + frameWidth, y + frameHeight);
        needDelete = false;
        damage = 0;

    }


    private static float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        if (screenX < 1){
            screenX = 1;
        }
        return screenX;
    }

    private static float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        if (screenY < 1){
            screenY = 1;
        }
        return screenY;
    }

    private static float bitmapScale(float scaleX, float scaleY) {
        if (scaleX > scaleY) {
            return scaleX;
        } else {
            return scaleY;
        }
    }

    public static int getFrameHeight(int screenX, int screenY) {
        float scaleFactorX = screenScaleX((float) screenX);
        float scaleFactorY = screenScaleY((float) screenY);
        float bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        // number needs to match initial frameHeight
        int frameHeight = 50 * (int) bitScale;
        return frameHeight;
    }

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

    public void animationControl(Canvas canvas, Paint paint) {
        whereToDraw.set(x, y, x + frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
    }

    public void update() {
        //update must be overridden based on graphic requirement
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + frameWidth;
        hitBox.bottom = y + frameHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public boolean isNeedDelete() {
        return needDelete;
    }
}
