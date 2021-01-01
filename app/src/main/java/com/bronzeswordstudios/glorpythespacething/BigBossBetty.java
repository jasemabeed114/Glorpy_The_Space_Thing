package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Random;

public class BigBossBetty {

    private final int AI_CODE_MACHINE_GUN = 0;
    private final int AI_CODE_MISSILES = 1;
    private final int AI_CODE_ASSAULT = 2;
    private final ArrayList<LaserBlast> laserBlasts;
    private final ArrayList<Missile> missiles;
    Glorpy glorpy;
    Context context;
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
    int frameLengthInMilliseconds;
    long lastFrameChangeTime;
    Rect frameToDraw;
    RectF whereToDraw;
    int maxY;
    int minY;
    int screenY;
    int screenX;
    int startingRange;
    int shotTime;
    long lastShotTime;
    int timeBetweenAI;
    long lastAIChange;
    boolean moveUp;
    boolean moveDown;
    int scoreValue;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    float scaleFactorX;
    float scaleFactorY;
    int damage;
    float bitScale;
    AudioHandler audioHandler;
    private int currentAI;
    private boolean didHitGlorpy;

    public BigBossBetty(Context context, int screenX, int screenY, ArrayList<LaserBlast> laserBlasts, ArrayList<Missile> missiles, Glorpy glorpy, AudioHandler audioHandler) {
        this.context = context;
        this.glorpy = glorpy;
        this.audioHandler = audioHandler;
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        bitScale = bitmapScale(scaleFactorX, scaleFactorY);
        scoreValue = 10000;
        minY = 0;
        maxX = screenX;
        moveUp = false;
        moveDown = false;
        didHitGlorpy = false;
        currentAI = AI_CODE_MACHINE_GUN;
        velocity = 13;
        health = 800;
        damage = -33;
        bitFrames = 3;
        shotTime = 222;
        timeBetweenAI = 15000;
        lastAIChange = System.currentTimeMillis();
        lastShotTime = System.currentTimeMillis();
        frameHeight = (int) (128 * bitScale) * 3;
        frameWidth = (int) (175 * bitScale) * 3;
        startingRange = maxX - (frameWidth);
        Random random = new Random(screenY);
        y = screenY / 2;
        x = screenX + frameWidth;
        maxY = screenY - frameHeight;
        this.screenY = screenY;
        this.screenX = screenX;
        hitBox = new Rect(x, y + (frameHeight / 4), x + frameWidth, y + (3 * frameHeight / 4));
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_boss_betty);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        currentFrame = 0;
        lastFrameChangeTime = 0;
        frameLengthInMilliseconds = 30;
        frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        whereToDraw = new RectF(x, y, x + frameWidth, y + frameHeight);
        this.laserBlasts = laserBlasts;
        this.missiles = missiles;
    }

    public void update() {
        if (x > startingRange && currentAI != AI_CODE_ASSAULT) {
            x -= velocity * scaleFactorX;
        } else {
            switch (currentAI) {

                case AI_CODE_MACHINE_GUN:
                    //setup directions for y movement
                    if (y >= screenY / 2 && !moveUp && !moveDown) {
                        moveDown = true;
                        moveUp = false;
                    } else if (y < screenY / 2 && !moveUp && !moveDown) {
                        moveUp = true;
                        moveDown = false;
                    } else {
                        // note adding to y moves towards the bottom of the screen
                        if (moveDown) {
                            y += velocity / 2 * scaleFactorY;
                            if (y >= maxY) {
                                moveUp = true;
                                moveDown = false;
                            }
                        } else {
                            y -= velocity / 2 * scaleFactorY;
                            if (y <= 0) {
                                moveDown = true;
                                moveUp = false;
                            }
                        }
                    }
                    // manage x movement
                    if (x > maxX / 2) {
                        x -= velocity * scaleFactorX;
                    } else if (x <= maxX / 2) {
                        if (System.currentTimeMillis() - lastShotTime >= shotTime) {
                            laserBlasts.add(new LaserBlast(context, x, y + (frameHeight / 2), screenX, screenY));
                            audioHandler.playLaserSound();
                            lastShotTime = System.currentTimeMillis();
                        }
                    }
                    if (x < (maxX / 2) - 100) {
                        x += velocity * scaleFactorX;
                    }
                    break;
                case AI_CODE_ASSAULT:
                    if (x >= -frameWidth) {
                        x -= velocity * 4 * scaleFactorX;
                    } else {
                        x = maxX + frameWidth;
                        y = glorpy.getY() - (frameHeight / 2);
                        // reset for next attack run
                        didHitGlorpy = false;
                    }
                    break;
                case AI_CODE_MISSILES:
                    // if too far in: reset position for balance
                    if (x < startingRange - frameWidth) {
                        x -= velocity * scaleFactorX;
                        if (x <= -frameWidth) {
                            x = maxX + frameWidth;
                        }
                    } else {
                        if (System.currentTimeMillis() - lastShotTime >= 1000) {
                            Random random = new Random();
                            int randomInt = random.nextInt(100);
                            int yPos;
                            if (randomInt >= 50) {
                                yPos = y + (frameHeight / 4);
                            } else {
                                yPos = y + (3 * frameHeight / 4);
                            }
                            missiles.add(new Missile(context, x, yPos, screenX, screenY, glorpy));
                            audioHandler.playSmallExplosion();
                            lastShotTime = System.currentTimeMillis();
                        }
                    }
                    break;
            }

        }
        if (System.currentTimeMillis() - lastAIChange >= timeBetweenAI) {
            if (currentAI < 2) {
                currentAI++;
            } else {
                currentAI = 0;
            }
            lastAIChange = System.currentTimeMillis();
        }

        hitBox.left = x;
        hitBox.right = x + frameWidth;
        hitBox.top = y + (frameHeight / 4);
        hitBox.bottom = y + (3 * frameHeight / 4);


    }

    void animationControl(Canvas canvas, Paint paint) {
        whereToDraw.set(x, y, x + frameWidth, y + frameHeight);
        getCurrentFrame();
        canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, paint);
    }

    void getCurrentFrame() {
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
    }


    private float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        if (screenX < 1){
            screenX = 1;
        }
        return screenX;
    }

    private float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        if (screenY < 1){
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

    public Rect getHitBox() {
        return hitBox;
    }

    public boolean didHitGlorpy() {
        return didHitGlorpy;
    }

    public int getDamage() {
        return damage;
    }

    public void setDidHitGlorpy(boolean didHitGlorpy) {
        this.didHitGlorpy = didHitGlorpy;
    }

    public void updateHealth(int damage) {
        health += damage;
    }

    public int getHealth() {
        return health;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameWidth() {
        return frameWidth;
    }
}
