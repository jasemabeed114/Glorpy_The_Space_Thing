package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CannonCharging extends GraphicElement {

    private final BigBossBlaster bigBossBlaster;

    public CannonCharging(Context context, int positionX, int positionY, int screenX, int screenY, BigBossBlaster bigBossBlaster) {
        super(context, positionX, positionY, screenX, screenY);
        bitFrames = 5;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cannon_charge);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth * bitFrames, frameHeight, false);
        this.bigBossBlaster = bigBossBlaster;
    }

}
