package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FireDamageGraphic extends GraphicElement {

    public FireDamageGraphic(Context context, int positionX, int positionY, int screenX, int screenY) {
        super(context, positionX, positionY, screenX, screenY);
        bitFrames = 9;
        frameWidth = (int) (100 * bitScale);
        frameHeight = (int) (100 * bitScale);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireball_hit);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
    }

    @Override
    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= bitFrames) {
                currentFrame = 9;
                needDelete = true;
            }
        }
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
        frameToDraw.top = 0;
        frameToDraw.bottom = frameHeight;
    }
}
