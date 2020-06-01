package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BigBossBlaster extends BaseEnemy {

    static final int AI_POSITION_MODE = 0;
    static final int AI_LASER_MODE = 1;
    static final int AI_CANNON_MODE = 2;
    static final int AI_KAMIKAZE_MODE = 3;
    private int cannonChargeTime;
    private long animationStartTime;
    private long lastCannonShotTime;
    private long attackRunTime;
    private long lastRunTime;
    private boolean moveUp;
    private long laserReload;
    private long lastLaserShot;
    private int currentAI;
    private boolean ramDamaged;
    private int health;

    public BigBossBlaster(Context context, int screenX, int screenY, Glorpy glorpy) {
        super(context, screenX, screenY, glorpy);
        frameHeight = (int) (98 * bitScale);
        frameWidth = (int) (300 * bitScale);
        bitFrames = 4;
        damage = -25;
        health = 300;
        reloadTime = 3000;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_boss_blaster);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        scoreValue = 2500;
        moveUp = true;
        cannonChargeTime = 10000;
        attackRunTime = 20000;
        laserReload = 2000;
        velocity = 20;
        currentAI = 0;
        lastCannonShotTime = System.currentTimeMillis();
        lastRunTime = System.currentTimeMillis();
        lastLaserShot = System.currentTimeMillis();
    }

    @Override
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
        frameToDraw.top = 0;
        frameToDraw.bottom = frameHeight;
    }

    @Override
    void update() {
        switch (currentAI) {
            case AI_POSITION_MODE:
                int bitmapRightEdge = x + frameWidth;
                // make sure all in view
                if (bitmapRightEdge > (maxX - 30)) {
                    x -= 7 * scaleFactorX;
                } else {
                    currentAI = AI_LASER_MODE;
                }
                break;
            case AI_LASER_MODE:
                if (moveUp) {
                    y -= 5 * scaleFactorY;
                    if (y <= 0) {
                        y = 0;
                        moveUp = false;
                    }
                } else {
                    y += 5 * scaleFactorY;
                    if (y >= maxY) {
                        y = maxY;
                        moveUp = true;
                    }
                }
                if (timeToShootCannon()) {
                    currentAI = AI_CANNON_MODE;
                    break;
                }
                if (beginAttackRun()) {
                    currentAI = AI_KAMIKAZE_MODE;
                    break;
                }
                break;
            case AI_CANNON_MODE:
                // hold position, to be handled in GameView since spawning game elements
                break;
            case AI_KAMIKAZE_MODE:
                x -= velocity;
                if (glorpy.getY() > y) {
                    y += 3 * scaleFactorY;
                } else {
                    y -= 3 * scaleFactorY;
                }
                if (x <= 0 - frameWidth) {
                    x = maxX;
                    currentAI = AI_POSITION_MODE;
                    lastRunTime = System.currentTimeMillis();
                    ramDamaged = false;
                }
                break;
        }
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + frameWidth;
        hitBox.bottom = y + frameHeight;
    }

    private boolean timeToShootCannon() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastCannonShotTime;
        return timeElapsed >= cannonChargeTime;
    }

    private boolean beginAttackRun() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastRunTime;
        return timeElapsed >= attackRunTime;
    }

    boolean fireLaser() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastLaserShot;
        return timeElapsed >= laserReload;
    }

    int getFrameHeight() {
        return frameHeight;
    }


    void setLastLaserShot(long lastLaserShot) {
        this.lastLaserShot = lastLaserShot;
    }

    int getCurrentAI() {
        return currentAI;
    }

    void setCurrentAI(int currentAI) {
        this.currentAI = currentAI;
    }

    long getAnimationStartTime() {
        return animationStartTime;
    }

    void setAnimationStartTime(long animationStartTime) {
        this.animationStartTime = animationStartTime;
    }

    void setLastCannonShotTime(long lastCannonShotTime) {
        this.lastCannonShotTime = lastCannonShotTime;
    }

    public boolean isRamDamaged() {
        return ramDamaged;
    }

    public void setRamDamaged(boolean ramDamaged) {
        this.ramDamaged = ramDamaged;
    }

    public void updateHealth(int healthValue) {
        health += healthValue;
    }

    public int getHealth() {
        return health;
    }
}
