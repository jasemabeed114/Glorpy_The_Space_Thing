package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Missile extends FireBall {

    private final Glorpy glorpy;
    private final int damage;
    private final int velocityX;
    private final int velocityY;
    private final int pointValue;
    private int targetY;

    public Missile(Context context, int positionX, int positionY, int screenX, int screenY, Glorpy glorpy) {
        super(context, positionX, positionY, screenX, screenY);
        this.glorpy = glorpy;
        targetY = glorpy.getY();
        bitFrames = 3;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.missile);
        frameWidth = (int) (120 * bitScale);
        frameHeight = (int) (60 * bitScale);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        velocityX = 25;
        velocityY = 7;
        health = 60;
        damage = -50;
        pointValue = 150;
        hitBox = new Rect(x, y, x + frameWidth, y + frameHeight);
    }

    @Override
    public void update() {
        targetY = glorpy.getY() + (frameHeight / 3);
        x -= (velocityX * scaleFactorX);

        // if missile not in locked on range
        if (!(y >= targetY - 5 && y <= targetY + 5)) {
            if (y > targetY) {
                y -= (velocityY * scaleFactorY);
            } else {
                y += (velocityY * scaleFactorY);
            }
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + frameWidth;
        hitBox.bottom = y + frameHeight;
    }

    public int getDamage() {
        return damage;
    }

    public int getPointValue() {
        return pointValue;
    }
}
