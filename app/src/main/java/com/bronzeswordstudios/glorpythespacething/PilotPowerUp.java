package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class PilotPowerUp {
    // used as parent class for evo power ups
    private final Rect hitBox;
    private final int velocity;
    private final int yMax;
    private final int yMin;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    private final float scaleFactorX;
    private final float scaleFactorY;
    Bitmap bitmap;
    int frameWidth;
    int frameHeight;
    int healingPower;
    float bitScale;
    Random random;
    private int x, y;
    private int randInt;
    private boolean movingUp;
    private boolean movingDown;

    public PilotPowerUp(Context context, int screenX, int screenY, BaseEnemy baseEnemy) {
        x = baseEnemy.getX();
        y = baseEnemy.getY();
        scaleFactorX = screenScaleX(screenX);
        scaleFactorY = screenScaleY(screenY);
        bitScale = bitmapScale(scaleFactorX, scaleFactorY);

        // create pilot bitmap based on which pilot was in the cockpit
        if (baseEnemy.getEnemyArtAssetNum() >= 5) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilot_1);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilot_2);
        }

        frameWidth = (int) (64 * bitScale);
        frameHeight = (int) (64 * bitScale);
        yMax = screenY - frameHeight;
        yMin = 0;
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);
        hitBox = new Rect(x, y, x + frameWidth, y + frameHeight);
        velocity = 5;
        healingPower = 18;
        random = new Random();
        randInt = random.nextInt(10);
        movingUp = false;
        movingDown = false;
    }

    public PilotPowerUp(Context context, int screenX, int screenY, BigBossBetty bigBossBetty) {
        random = new Random();
        int randX = random.nextInt(bigBossBetty.getFrameWidth()) + bigBossBetty.getX();
        int randY = random.nextInt(bigBossBetty.getFrameHeight()) + bigBossBetty.getY();
        x = randX;
        y = randY;
        scaleFactorX = screenScaleX(screenX);
        scaleFactorY = screenScaleY(screenY);
        bitScale = bitmapScale(scaleFactorX, scaleFactorY);

        // create pilot bitmap based on which pilot was in the cockpit
        randInt = random.nextInt(100);
        if (randInt < 50) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilot_1);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pilot_2);
        }

        frameWidth = (int) (64 * bitScale);
        frameHeight = (int) (64 * bitScale);
        yMax = screenY - frameHeight;
        yMin = 0;
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);
        hitBox = new Rect(x, y, x + frameWidth, y + frameHeight);
        velocity = 5;
        healingPower = 18;
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
        return healingPower;
    }
}

