package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class Explosion {
    private int explosionCount;
    private Rect explosionLocation;
    private ArrayList<GraphicElement> graphicElements;
    private Context context;
    private boolean needsDelete;

    public Explosion(Context context, Rect explosionLocation, int explosionCount, int screenX, int screenY) {
        this.context = context;
        this.explosionLocation = explosionLocation;
        this.explosionCount = explosionCount;
        this.graphicElements = new ArrayList<>();
        Random randInt = new Random();
        needsDelete = false;
        for (int i = 0; i <= explosionCount; i++) {
            int xPos = randInt.nextInt(explosionLocation.right - explosionLocation.left) + explosionLocation.left;
            int yPos = randInt.nextInt(explosionLocation.bottom - explosionLocation.top) + explosionLocation.top;
            graphicElements.add(new FireDamageGraphic(context, xPos, yPos, screenX, screenY));
        }
    }

    public void update() {
        for (int i = graphicElements.size() - 1; i >= 0; i--) {
            graphicElements.get(i).update();
            if (graphicElements.get(i).isNeedDelete()) {
                graphicElements.remove(i);
                break;
            }
        }
        if (graphicElements.size() == 0) {
            needsDelete = true;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        for (int i = graphicElements.size() - 1; i >= 0; i--) {
            graphicElements.get(i).animationControl(canvas, paint);
        }
    }

    public boolean isNeedsDelete() {
        return needsDelete;
    }
}
