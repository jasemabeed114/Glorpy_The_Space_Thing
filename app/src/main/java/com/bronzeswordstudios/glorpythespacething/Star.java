package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Star {

    private final int maxX;
    private final int maxY;
    // designed for a phone that is x:1920 by y:1080 so we must scale to other screens
    // to maintain play style
    private final float scaleFactorX;
    private final float scaleFactorY;
    Bitmap bitmap;
    int speed;
    int x, y;

    public Star(Context context, int screenX, int screenY) {
        scaleFactorX = screenScaleX((float) screenX);
        scaleFactorY = screenScaleY((float) screenY);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star_jat);
        maxX = screenX;
        maxY = screenY;
        Random numGenerator = new Random();
        speed = numGenerator.nextInt((int) (5 * scaleFactorX)) + 1;
        x = numGenerator.nextInt(maxX);
        y = numGenerator.nextInt(maxY);

    }


    public void update() {
        x -= speed;

        if (x < 0) {
            Random numGenerator = new Random();
            x = maxX;
            y = numGenerator.nextInt(maxY);
            speed = numGenerator.nextInt((int) (5 * scaleFactorX)) + (int) (5 * scaleFactorX);
        }

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
