package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LargeExplosionGraphic extends GraphicElement {

    public LargeExplosionGraphic(Context context, int positionX, int positionY, int screenX, int screenY) {
        super(context, positionX, positionY, screenX, screenY);
        bitFrames = 8;
        frameHeight = 128;
        frameWidth = 175;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_explosion);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);

    }

    @Override
    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= bitFrames) {
                currentFrame = 8;
                needDelete = true;
            }
        }
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
        frameToDraw.top = 0;
        frameToDraw.bottom = frameHeight;
    }
}
