package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class Explosion {
    public static final int SMALL_EXPLOSION = 0;
    public static final int LARGE_EXPLOSION = 1;
    private int explosionCount;
    private final Rect explosionLocation;
    private final ArrayList<GraphicElement> graphicElements;
    private final Context context;
    private boolean needsDelete;
    private final int type;
    private final int screenX;
    private final int screenY;

    public Explosion(Context context, Rect explosionLocation, int explosionCount, int screenX, int screenY, int type) {
        this.context = context;
        this.explosionLocation = explosionLocation;
        this.explosionCount = explosionCount;
        this.graphicElements = new ArrayList<>();
        this.type = type;
        this.screenX = screenX;
        this.screenY = screenY;
        needsDelete = false;
    }

    public void update() {
        if (explosionCount > 0) {
            addGraphic(type);
            explosionCount--;
        }
        for (int i = graphicElements.size() - 1; i >= 0; i--) {
            graphicElements.get(i).update();
            if (graphicElements.get(i).isNeedDelete()) {
                graphicElements.remove(i);
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

    private void addGraphic(int type) {
        Random randInt = new Random();
        int xPos = randInt.nextInt(explosionLocation.right - explosionLocation.left) + explosionLocation.left;
        int yPos = randInt.nextInt(explosionLocation.bottom - explosionLocation.top) + explosionLocation.top;
        if (type == SMALL_EXPLOSION) {
            graphicElements.add(new FireDamageGraphic(context, xPos, yPos, screenX, screenY));
        } else if (type == LARGE_EXPLOSION) {
            graphicElements.add(new LargeExplosionGraphic(context, xPos, yPos, screenX, screenY));
        }
    }
}
