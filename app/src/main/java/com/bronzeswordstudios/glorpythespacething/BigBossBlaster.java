package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BigBossBlaster extends BaseEnemy {

    private boolean attacking = false;
    private long timeSinceLastAttack = 0;

    public BigBossBlaster(Context context, int screenX, int screenY, Glorpy glorpy) {
        super(context, screenX, screenY, glorpy);
        frameHeight = (int) (200 * bitScale);
        frameWidth = (int) (275 * bitScale);
        bitFrames = 4;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_boss_blaster);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        scoreValue = 2500;
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
        // todo: finish writing AI
    void update() {
        if (x > firingRange) {
            x--;
        } else if (!attacking && timeSinceLastAttack > 10000) {

        }
    }
}
