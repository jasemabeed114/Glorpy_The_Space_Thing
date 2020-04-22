package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class PilotPowerUp {
    private Rect hitBox;
    private Bitmap bitmap;
    private int x, y;
    private int velocity;
    private int yMax, yMin;
    private int frameWidth;
    private int frameHeight;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    private float scaleFactorX;
    private float scaleFactorY;
    private int randInt;
    private boolean movingUp;
    private boolean movingDown;

    public PilotPowerUp(Context context, int screenX, int screenY, BaseEnemy baseEnemy) {
        x = baseEnemy.getX();
        y = baseEnemy.getY();
        yMax = screenY;
        yMin = 0;
        scaleFactorX = screenScaleX(screenX);
        scaleFactorY = screenScaleY(screenY);
        float bitScale = bitmapScale(scaleFactorX, scaleFactorY);

        // create pilot bitmap based on which pilot was in the cockpit
        if (baseEnemy.getEnemyArtAssetNum() >= 5) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilot_1);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilot_2);
        }

        frameWidth = (int) (32 * bitScale);
        frameHeight = (int) (32 * bitScale);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);
        hitBox = new Rect(x, y, x + frameWidth, y + frameHeight);
        velocity = 5;
        Random random = new Random();
        randInt = random.nextInt(10);
        movingUp = false;
        movingDown = false;
    }

    public void update() {
        x -= velocity * scaleFactorX;
        if (randInt >= 5 && !movingUp && !movingDown) {
            movingDown = true;
        } else if (!movingUp && !movingDown) {
            movingUp = true;
        }
        if (movingDown) {
            y += 1 * scaleFactorY;
        }
        if (movingUp) {
            y -= 1 * scaleFactorY;
        }
        if (movingUp && y <= yMin) {
            movingUp = false;
            movingDown = true;
        }
        if (movingDown && y >= yMax) {
            movingDown = false;
            movingUp = true;
        }

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

    public Rect getHitBox() {
        return hitBox;
    }

    public int getX() {
        return x;
    }

    public int getHealingPower() {
        return 18;
    }
}

