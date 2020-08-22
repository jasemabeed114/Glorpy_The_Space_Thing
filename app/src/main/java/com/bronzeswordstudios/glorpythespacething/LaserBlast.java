package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class LaserBlast {
    private Rect hitBox;
    private Bitmap bitmap;
    private int x, y;
    private int velocity;
    private int frameWidth;
    private int frameHeight;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    private float scaleFactorX;
    private float scaleFactorY;
    private int damage;

    public LaserBlast(Context context, int positionX, int positionY, int screenX, int screenY) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        float bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        x = positionX;
        y = positionY;
        velocity = (int) (25 * scaleFactorX);
        damage = -15;
        frameWidth = (int) (64 * bitScale);
        frameHeight = (int) (18 * bitScale);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.laser_blast);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);
        hitBox = new Rect(x, y, x + frameWidth, y + frameHeight);
    }

    public void update() {
        x -= velocity;
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + frameWidth;
        hitBox.bottom = y + frameHeight;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, x, y, paint);
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

    public int getX() {
        return x;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public int getDamage() {
        return damage;
    }
}
