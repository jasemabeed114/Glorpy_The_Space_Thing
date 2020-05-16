package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BigBossBlaster extends BaseEnemy {

    private boolean cannonCharged;
    private int cannonChargeTime;
    private long cannonShotTime;
    private long lastCannonShotTime;
    private long attackRunTime;
    private long lastRunTime;
    private boolean directionEstablished;
    private int direction;

    public BigBossBlaster(Context context, int screenX, int screenY, Glorpy glorpy) {
        super(context, screenX, screenY, glorpy);
        frameHeight = (int) (200 * bitScale);
        frameWidth = (int) (275 * bitScale);
        bitFrames = 4;
        reloadTime = 3000;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_boss_blaster);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        scoreValue = 2500;
        cannonCharged = false;
        directionEstablished = false;
        cannonChargeTime = 10000;
        attackRunTime = 20000;
        velocity = 20;
        direction = 0;
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
    }

    @Override
    void update() {
        if (x > firingRange) {
            x--;
        } else if (!timeToShootCannon() && beginAttackRun()) {
            x -= velocity;
            if (!directionEstablished) {
                establishDirection();
            } else if (direction > 0 && x > 0) {
                y += velocity;
            } else if (direction < 0 && x > 0) {
                y -= velocity;
            } else if (x <= 0) {
                x = maxX;
                directionEstablished = false;
                lastRunTime = System.currentTimeMillis();
            }
        }
        else if (timeToShootCannon()){
            //todo: need code to spawn cannon fire

        }
        else {
            //todo: write code to move up and down
        }
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

    private void establishDirection() {
        if (glorpy.getY() > y) {
            directionEstablished = true;
            direction = 1;
        } else {
            directionEstablished = true;
            direction = -1;
        }
    }
}
