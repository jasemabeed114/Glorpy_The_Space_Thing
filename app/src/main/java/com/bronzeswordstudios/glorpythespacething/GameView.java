package com.bronzeswordstudios.glorpythespacething;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    public Glorpy glorpy;
    public ArrayList<FireBall> fireballs = new ArrayList<>();
    volatile boolean playing;
    private Paint paint;
    private SurfaceHolder ourHolder;
    private Context context;
    private int screenX;
    private int screenY;
    private ArrayList<Star> stars = new ArrayList<>();
    private ArrayList<BaseEnemy> baseEnemies = new ArrayList<>();
    private ArrayList<LaserBlast> laserBlasts = new ArrayList<>();
    private ArrayList<PilotPowerUp> pilotPowerUps = new ArrayList<>();
    private ArrayList<GraphicElement> graphicElements = new ArrayList<>();
    private Thread gameThread;
    private Activity gameActivity;
    private BigBossBlaster bigBossBlaster;
    private CannonCharging cannonCharging;
    private Rect explosionLocation;
    private int explosionCounter;
    private long lastExplosionTime;


    public GameView(Context context, Activity gameActivity, int x, int y) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        this.gameActivity = gameActivity;
        //test boss
        startGame();
    }

    @Override
    public void run() {
        while (playing) {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR: ", "" + e);
            }
            try {
                draw();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR: ", "" + e);
            }
            try {
                control();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR: ", "" + e);
            }
        }
    }

    private void update() {
        for (Star star : stars) {
            star.update();
        }
        updateFireballs(fireballs);
        updateEnemies(baseEnemies, laserBlasts, glorpy);
        updateLasers(laserBlasts, glorpy);
        updatePilots(pilotPowerUps, glorpy);
        updateBigBossBlaster(bigBossBlaster, glorpy);
        updateGraphics(graphicElements, glorpy);
        createExplosion(explosionLocation);
        glorpy.update();
    }

    private void draw() {
        Canvas canvas;
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            for (Star star : stars) {
                canvas.drawBitmap(star.getBitmap(), star.getX(), star.getY(), paint);
            }
            if (fireballs.size() > 0) {
                for (int i = fireballs.size() - 1; i >= 0; i--) {
                    FireBall fireBall = fireballs.get(i);
                    fireBall.animationControl(canvas, paint);
                }
            }
            if (baseEnemies.size() > 0) {
                for (int i = baseEnemies.size() - 1; i >= 0; i--) {
                    BaseEnemy baseEnemy = baseEnemies.get(i);
                    baseEnemy.animationControl(canvas, paint);
                }
            }
            if (laserBlasts.size() > 0) {
                for (int i = laserBlasts.size() - 1; i >= 0; i--) {
                    LaserBlast laserBlast = laserBlasts.get(i);
                    laserBlast.draw(canvas, paint);
                }
            }
            if (pilotPowerUps.size() > 0) {
                for (int i = pilotPowerUps.size() - 1; i >= 0; i--) {
                    PilotPowerUp pilot = pilotPowerUps.get(i);
                    pilot.draw(canvas, paint);
                }
            }
            drawBigBossBlaster(bigBossBlaster, canvas, paint);
            drawGraphics(graphicElements, canvas, paint);
            glorpy.animationControl(canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(13);
        } catch (InterruptedException e) {

        }
        int enemyCount = updateDifficulty(DataHolder.score);

        if (baseEnemies.size() < enemyCount) {
            BaseEnemy baseEnemy = new BaseEnemy(context, screenX, screenY, glorpy);
            baseEnemies.add(baseEnemy);
        }
    }

    public void pause() {
        playing = false;

        try {
            gameThread.join();

        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void startGame() {
        glorpy = new Glorpy(context, screenX, screenY);
        bigBossBlaster = new BigBossBlaster(context, screenX, screenY, glorpy);
        int numStars = 15;
        for (int i = 0; i < numStars; i++) {
            Star star = new Star(context, screenX, screenY);
            stars.add(star);
        }
    }

    private void updateEnemies(ArrayList<BaseEnemy> baseEnemies, ArrayList<LaserBlast> laserBlasts, Glorpy glorpy) {
        if (baseEnemies.size() > 0) {
            for (int i = baseEnemies.size() - 1; i >= 0; i--) {
                final BaseEnemy baseEnemy = baseEnemies.get(i);
                baseEnemy.update();

                // check to see if a fireball has hit an enemy, if so prompt animation and delete both
                if (fireballs.size() > 0) {
                    for (int fireballIndex = fireballs.size() - 1; fireballIndex >= 0; fireballIndex--) {
                        if (fireballs.get(fireballIndex).getHitBox().intersect(baseEnemy.getHitBox())) {

                            // We need to access our UI thread to update the score
                            if (!baseEnemy.isDestroyed) {
                                gameActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GameActivity.updateScore(baseEnemy.getScoreValue());
                                    }
                                });
                            }
                            baseEnemy.isDestroyed = true;
                            Random randGen = new Random();
                            int healthCode = randGen.nextInt(100);
                            if (healthCode <= 15) {
                                pilotPowerUps.add(new PilotPowerUp(context, screenX, screenY, baseEnemy));
                            }
                            fireballs.remove(fireballIndex);
                            break;
                        }
                    }
                }
                if (baseEnemy.getHitBox().intersect(glorpy.gethitBox())) {
                    if (!baseEnemy.isDestroyed) {
                        baseEnemy.isDestroyed = true;

                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GameActivity.updateHealth(baseEnemy.getDamage());
                            }
                        });
                    }
                }
                if (baseEnemy.isDestroyed && baseEnemy.deleteShip) {
                    baseEnemies.remove(i);
                    break;
                }
                if (baseEnemy.getX() < 0 && !baseEnemy.isDestroyed) {
                    // lose points for enemies getting past
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int scoreDeduction = -1 * (baseEnemy.getScoreValue() / 2);
                            GameActivity.updateScore(scoreDeduction);
                        }
                    });

                    baseEnemies.remove(i);
                    break;
                }
                if (baseEnemy.timeToShoot() &&
                        baseEnemy.getEnemyAICode() >= 5 && baseEnemy.getX() <= baseEnemy.getFiringRange()) {
                    laserBlasts.add(new LaserBlast(context, baseEnemy.getX(), baseEnemy.getY(), screenX, screenY));
                    baseEnemy.lastShotTime = System.currentTimeMillis();
                }
            }
        }
    }

    private void updateFireballs(ArrayList<FireBall> fireballs) {
        if (fireballs.size() > 0) {
            for (int i = fireballs.size() - 1; i >= 0; i--) {
                FireBall fireBall = fireballs.get(i);
                fireBall.update();
                if (fireBall.getX() > fireBall.getMaxX()) {
                    fireballs.remove(i);
                }
            }
        }
    }

    private void updateLasers(ArrayList<LaserBlast> laserBlasts, Glorpy glorpy) {
        for (int i = laserBlasts.size() - 1; i >= 0; i--) {
            final LaserBlast laserBlast = laserBlasts.get(i);
            laserBlast.update();
            if (laserBlast.getX() < 0) {
                laserBlasts.remove(i);
                break;
            }
            if (laserBlast.getHitBox().intersect(glorpy.gethitBox())) {

                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(laserBlast.getDamage());
                    }
                });

                laserBlasts.remove(i);
                break;
            }
        }
    }

    private void updatePilots(ArrayList<PilotPowerUp> pilotPowerUps, Glorpy glorpy) {
        for (int i = pilotPowerUps.size() - 1; i >= 0; i--) {
            final PilotPowerUp pilot = pilotPowerUps.get(i);
            pilot.update();
            if (pilot.getX() < 0) {
                pilotPowerUps.remove(i);
                break;
            }
            if (pilot.getHitBox().intersect(glorpy.gethitBox())) {
                glorpy.setIsShooting(true);

                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(pilot.getHealingPower());
                        GameActivity.updateScore(pilot.getHealingPower());
                    }
                });
                pilotPowerUps.remove(i);
                break;
            }
        }
    }

    private int updateDifficulty(int score) {
        int enemyCount;
        if (bigBossBlaster == null) {
            if (score < 100) {
                enemyCount = 1;
            } else if (score < 200) {
                enemyCount = score / 100;
            } else if (score < 2000) {
                enemyCount = 2 + score / 500;
            } else {
                enemyCount = 6 + score / 2000;
            }
        } else enemyCount = 0;
        return enemyCount;

    }

    private void updateBigBossBlaster(BigBossBlaster bigBossBlaster, Glorpy glorpy) {
        if (bigBossBlaster != null) {
            bigBossBlaster.update();
            if (bigBossBlaster.getCurrentAI() == BigBossBlaster.AI_CANNON_MODE) {
                if (!graphicElements.contains(cannonCharging)) {
                    cannonCharging = new CannonCharging(context, bigBossBlaster.getX(),
                            bigBossBlaster.getY() + ((bigBossBlaster.getFrameHeight())
                                    * (int) screenScaleY(screenY)) - CannonCharging.getFrameHeight(screenX, screenY),
                            screenX, screenY, bigBossBlaster);
                    graphicElements.add(cannonCharging);
                    bigBossBlaster.setAnimationStartTime(System.currentTimeMillis());
                } else {
                    if (System.currentTimeMillis() - bigBossBlaster.getAnimationStartTime() >= 1500 || this.bigBossBlaster == null) {
                        graphicElements.remove(cannonCharging);
                        cannonCharging = null;
                        graphicElements.add(new BigBlast(context, bigBossBlaster.getX(),
                                bigBossBlaster.getY(), screenX, screenY));
                        bigBossBlaster.setLastCannonShotTime(System.currentTimeMillis());
                        bigBossBlaster.setLastLaserShot(System.currentTimeMillis());
                        bigBossBlaster.setCurrentAI(BigBossBlaster.AI_LASER_MODE);
                    }
                }

            }
            if (bigBossBlaster.getCurrentAI() == BigBossBlaster.AI_LASER_MODE) {
                if (bigBossBlaster.fireLaser()) {
                    laserBlasts.add(new LaserBlast(context, bigBossBlaster.getX(), bigBossBlaster.getY(), screenX, screenY));
                    bigBossBlaster.setLastLaserShot(System.currentTimeMillis());
                }
            }
            if (Rect.intersects(bigBossBlaster.getHitBox(), glorpy.gethitBox()) && !bigBossBlaster.isRamDamaged()) {
                bigBossBlaster.setRamDamaged(true);
                final int ramDamage = bigBossBlaster.getDamage();
                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(ramDamage);
                    }
                });
            }
            for (int i = 0; i < fireballs.size(); i++) {
                FireBall fireBall = fireballs.get(i);
                if (Rect.intersects(fireBall.getHitBox(), bigBossBlaster.getHitBox())) {
                    bigBossBlaster.updateHealth(glorpy.getFireDamage());
                    if (bigBossBlaster.getHealth() <= 0) {
                        explosionLocation = bigBossBlaster.getHitBox();
                        explosionCounter = 30;
                        lastExplosionTime = System.currentTimeMillis();
                        this.bigBossBlaster = null;
                        final int scoreValue = bigBossBlaster.getScoreValue();
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GameActivity.updateScore(scoreValue);
                            }
                        });
                    }
                    Random randInt = new Random();
                    int yHitPosition = fireBall.getHitBox().centerY() + randInt.nextInt(150) - 75;
                    int xHitPosition = fireBall.getX() + randInt.nextInt(100);
                    graphicElements.add(new FireDamageGraphic(context, xHitPosition, yHitPosition, screenX, screenY));
                    fireballs.remove(i);
                    break;
                }
            }
        }
    }

    private void updateGraphics(final ArrayList<GraphicElement> graphicElements, Glorpy glorpy) {
        if (graphicElements.size() > 0) {
            for (int i = 0; i < graphicElements.size(); i++) {
                final GraphicElement currentElement = graphicElements.get(i);
                graphicElements.get(i).update();
                if (currentElement.getX() < 0) {
                    graphicElements.remove(i);
                    break;
                }
                if (Rect.intersects(glorpy.gethitBox(), currentElement.getHitBox())) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GameActivity.updateHealth(currentElement.getDamage());
                        }
                    });
                    graphicElements.remove(i);
                    break;
                }
                if (currentElement.isNeedDelete()) {
                    graphicElements.remove(i);
                    break;
                }
            }
        }
    }

    private void drawGraphics(ArrayList<GraphicElement> graphicElements, Canvas canvas, Paint paint) {
        if (graphicElements.size() > 0) {
            for (int i = 0; i < graphicElements.size(); i++) {
                graphicElements.get(i).animationControl(canvas, paint);
            }
        }
    }

    private void drawBigBossBlaster(BigBossBlaster bigBossBlaster, Canvas canvas, Paint paint) {
        if (bigBossBlaster != null) {
            bigBossBlaster.animationControl(canvas, paint);
        }
    }

    float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        return screenX;
    }

    float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        return screenY;
    }

    private void createExplosion(Rect location) {
        if (location != null && explosionCounter > 0) {
            if (System.currentTimeMillis() - lastExplosionTime > 100) {
                Random randInt = new Random();
                int xPos = randInt.nextInt(location.right - location.left) + location.left;
                int yPos = randInt.nextInt(location.bottom - location.top) + location.top;
                graphicElements.add(new FireDamageGraphic(context, xPos, yPos, screenX, screenY));
                explosionCounter--;
            }
        }
        if (explosionCounter == 0) {
            explosionLocation = null;
        }
    }


}
