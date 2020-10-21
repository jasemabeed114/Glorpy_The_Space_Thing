package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class EvoPowerUp extends PilotPowerUp {

    public EvoPowerUp(Context context, int screenX, int screenY, BaseEnemy bossEnemy) {
        super(context, screenX, screenY, bossEnemy);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.evo_cube);
        healingPower = 50;
        frameWidth = (int) (64 * bitScale);
        frameHeight = (int) (64 * bitScale);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);

    }

    public EvoPowerUp(Context context, int screenX, int screenY, BigBossBetty bigBossBetty) {
        super(context, screenX, screenY, bigBossBetty);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.evo_cube);
        healingPower = 50;
        frameWidth = (int) (64 * bitScale);
        frameHeight = (int) (64 * bitScale);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, frameHeight, false);
    }

}
