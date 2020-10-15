package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GlorpyKO extends Star {

    int frameWidth, frameHeight;

    public GlorpyKO(Context context, int screenX, int screenY, int positionX, int positionY) {
        super(context, screenX, screenY);
        x = positionX;
        y = positionY;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.glorpy_ko);
        frameHeight = 128;
        frameWidth = 128;
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);
    }

    @Override
    public void update() {
        y -= 4;
        x += 2;
    }

    public int getFrameHeight() {
        return frameHeight;
    }
}
