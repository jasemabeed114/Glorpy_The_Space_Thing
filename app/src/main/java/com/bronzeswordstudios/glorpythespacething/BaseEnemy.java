package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

class BaseEnemy {

    long lastShotTime;
    boolean isDestroyed;
    boolean deleteShip;
    Glorpy glorpy;
    int bitFrames;
    Rect hitBox;
    Bitmap bitmap;
    int x, y;
    int maxX;
    int health;
    int velocity;
    int frameWidth;
    int frameHeight;
    int currentFrame;
    long lastFrameChangeTime;
    int frameLengthInMilliseconds;
    int reloadTime;
    Rect frameToDraw;
    RectF whereToDraw;
    int maxY;
    int minY;
    Random typeGenerator = new Random();
    int enemyArtAssetNum;
    int enemyAICode;
    int firingRange;
    boolean moveUp;
    boolean moveDown;
    int scoreValue;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    float scaleFactorX;
    float scaleFactorY;
    int damage;
    float bitScale;

    BaseEnemy(Context context, int screenX, int screenY, Glorpy glorpy) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        bitScale = bitmapScale(scaleFactorX, scaleFactorY);
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
        reloadTime = 7000;
        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(x, 0, x + frameWidth, y + frameHeight);

    }

    void update() {
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

    void animationControl(Canvas canvas, Paint paint) {
        whereToDraw.set(x, y, x + frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
    }

    void getCurrentFrame() {
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

    float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        return screenX;
    }

    float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        return screenY;
    }

    float bitmapScale(float scaleX, float scaleY) {
        if (scaleX > scaleY) {
            return scaleX;
        } else {
            return scaleY;
        }
    }

    boolean timeToShoot() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastShotTime;
        return timeElapsed >= reloadTime;
    }

    Rect getHitBox() {
        return hitBox;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getreloadTime() {
        return reloadTime;
    }

    int getEnemyAICode() {
        return enemyAICode;
    }

    int getFiringRange() {
        return firingRange;
    }

    int getScoreValue() {
        return scoreValue;
    }

    int getDamage() {
        return damage;
    }

    int getEnemyArtAssetNum() {
        return enemyArtAssetNum;
    }
}
