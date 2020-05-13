package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MainBackgroundView extends SurfaceView implements Runnable {

    int screenX, screenY;
    Context context;
    ArrayList<Star> stars = new ArrayList<>();
    volatile boolean running = true;
    Paint paint;
    SurfaceHolder holder;
    private Thread backgroundThread;

    public MainBackgroundView(Context context, int x, int y) {
        super(context);
        this.context = context;
        paint = new Paint();
        holder = getHolder();
        screenX = x;
        screenY = y;
        startAnimation();
    }

    @Override
    public void run() {
        while (running) {
            update();
            draw();
            control();
        }
    }

    private void draw() {
        Canvas canvas;
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            for (Star star : stars) {
                canvas.drawBitmap(star.getBitmap(), star.getX(), star.getY(), paint);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void update() {
        for (Star star : stars) {
            star.update();
        }
    }

    private void control() {
        try {
            Thread.sleep(13);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", "" + e);
        }
    }

    private void startAnimation() {
        int numStars = 25;
        for (int i = 0; i < numStars; i++) {
            Star star = new Star(context, screenX, screenY);
            stars.add(star);
        }
    }

    public void resume() {
        running = true;
        backgroundThread = new Thread(this);
        backgroundThread.start();
    }

    public void pause() {
        running = false;
        try {
            backgroundThread.join();
        } catch (Exception e) {

        }
    }

}
