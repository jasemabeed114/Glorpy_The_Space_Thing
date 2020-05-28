package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BigBlast extends GraphicElement {


    public BigBlast(Context context, int positionX, int positionY, int screenX, int screenY) {
        super(context, positionX, positionY, screenX, screenY);
        bitFrames = 13;
        frameWidth = 100;
        frameHeight = 100;
        frameLengthInMilliseconds = 50;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_boss_blast);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        damage = -65;
    }

    @Override
    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= bitFrames) {
                currentFrame = 12;
            }
        }
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
        frameToDraw.top = 0;
        frameToDraw.bottom = frameHeight;
    }

    @Override
    public void update() {
        if (currentFrame == 12) {
            x -= 80;
        }
        if (x < 0) {
            needDelete = true;
        }
        super.update();
    }
}
