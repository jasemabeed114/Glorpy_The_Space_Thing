package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

public class BaseEnemy {

    public long lastShotTime;
    public boolean isDestroyed;
    public boolean deleteShip;
    Glorpy glorpy;
    private int bitFrames;
    private Rect hitBox;
    private Bitmap bitmap;
    private int x, y;
    private int maxX;
    private int health;
    private int velocity;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame;
    private long lastFrameChangeTime;
    private int frameLengthInMilliseconds;
    private int shotTimeInMilliseconds;
    private Rect frameToDraw;
    private RectF whereToDraw;
    private int maxY;
    private int minY;
    private Random typeGenerator = new Random();
    private int enemyArtAssetNum;
    private int enemyAICode;
    private int firingRange;
    private boolean moveUp;
    private boolean moveDown;
    private int scoreValue;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    private float scaleFactorX;
    private float scaleFactorY;
    private int damage;

    public BaseEnemy(Context context, int screenX, int screenY, Glorpy glorpy) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        float bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        scoreValue = 55;
        minY = 0;
        maxX = screenX;
        x = screenX + (int) (50 * scaleFactorX);
        moveUp = false;
        moveDown = false;
        isDestroyed = false;
        deleteShip = false;
        velocity = 5;
        health = 100;
        damage = -17;
        bitFrames = 8;
        firingRange = maxX - (int) (300 * scaleFactorX);
        this.glorpy = glorpy;

        //picks fighter sprites randomly on generation. This allows for variety
        enemyArtAssetNum = typeGenerator.nextInt(10);
        enemyAICode = typeGenerator.nextInt(10);
        if (enemyArtAssetNum >= 5) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.uesf_fighter1);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.uesf_fighter2);
        }

        frameHeight = (int) (100 * bitScale);
        frameWidth = (int) (175 * bitScale);
        maxY = screenY - frameHeight;
        //hit box is adjusted for ideal collision detection
        hitBox = new Rect(x + (int) (25 * scaleFactorX), y + (int) (25 * scaleFactorY),
                x + frameWidth - (int) (25 * scaleFactorX),
                y + frameHeight - (int) (25 * scaleFactorY));
        Random random = new Random();
        y = random.nextInt(maxY);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        currentFrame = 0;
        lastFrameChangeTime = 0;
        lastShotTime = 0;
        frameLengthInMilliseconds = 50;
        shotTimeInMilliseconds = 7000;
        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(x, 0, x + frameWidth, y + frameHeight);

    }

    public void update() {
        if (enemyAICode >= 5) {
            if (x > firingRange) {
                x -= velocity * scaleFactorX;
            }
            if (x <= maxX) {
                if (!moveDown && !moveUp) {
                    //if no direction set yet, establish initial direction
                    if (y > maxY / 2) {
                        moveUp = true;
                    } else {
                        moveDown = true;
                    }
                }
                if (moveUp) {
                    y -= velocity * scaleFactorY;
                    if (y <= minY) {
                        y = minY;
                        moveUp = false;
                        moveDown = true;
                    }
                }
                if (moveDown) {
                    y += velocity * scaleFactorY;
                    if (y >= maxY) {
                        y = maxY;
                        moveDown = false;
                        moveUp = true;
                    }
                }
            }
        } else {
            x -= (velocity * scaleFactorX) + enemyAICode + (3 * scaleFactorX);
            if (DataHolder.score > 2000) {
                int glorpyY = glorpy.getY();
                // create kamikazes if far enough in game
                if (y > glorpyY + 2) {
                    y -= 1 * scaleFactorY;
                } else if (y < glorpyY - 2) {
                    y += 1 * scaleFactorY;
                }
            }
        }
        hitBox.left = x + (int) (25 * scaleFactorX);
        hitBox.top = y + (int) (25 * scaleFactorY);
        hitBox.right = x + frameWidth - (int) (25 * scaleFactorX);
        hitBox.bottom = y + frameHeight - (int) (25 * scaleFactorY);
    }

    public void animationControl(Canvas canvas, Paint paint) {
        whereToDraw.set(x, y, x + frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
    }

    private void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (isDestroyed) {
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                currentFrame++;
                if (currentFrame >= bitFrames) {
                    currentFrame = 7;
                    deleteShip = true;
                }
            }
        } else {
            currentFrame = 0;
        }
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
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

    public int getY() {
        return y;
    }

    public int getShotTimeInMilliseconds() {
        return shotTimeInMilliseconds;
    }

    public int getEnemyAICode() {
        return enemyAICode;
    }

    public int getFiringRange() {
        return firingRange;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getDamage() {
        return damage;
    }

    public int getEnemyArtAssetNum() {
        return enemyArtAssetNum;
    }
}
