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

    private final Paint paint;
    private final SurfaceHolder ourHolder;
    private final Context context;
    private final int screenX;
    private final int screenY;
    private final ArrayList<Star> stars;
    private final ArrayList<BaseEnemy> baseEnemies;
    private final ArrayList<LaserBlast> laserBlasts;
    private final ArrayList<PilotPowerUp> powerUps;
    private final ArrayList<GraphicElement> graphicElements;
    private final ArrayList<Explosion> explosions;
    private final ArrayList<Missile> missiles;
    private final Activity gameActivity;
    public Glorpy glorpy;
    public ArrayList<FireBall> fireballs = new ArrayList<>();
    public AudioHandler audioHandler;
    volatile boolean playing;
    private boolean canvasIsLocked;
    private Thread gameThread;
    private BigBossBlaster bigBossBlaster;
    private BigBossBetty bigBossBetty;
    private CannonCharging cannonCharging;
    private int difficultyFactor;
    private boolean spawnBoss;
    private boolean thrustPlayed;


    public GameView(Context context, Activity gameActivity, int x, int y) {
        super(context);
        this.context = context;
        audioHandler = new AudioHandler(context);
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        this.gameActivity = gameActivity;
        difficultyFactor = 1;
        stars = new ArrayList<>();
        baseEnemies = new ArrayList<>();
        laserBlasts = new ArrayList<>();
        powerUps = new ArrayList<>();
        graphicElements = new ArrayList<>();
        missiles = new ArrayList<>();
        explosions = new ArrayList<>();
        spawnBoss = false;
        canvasIsLocked = false;
        // handle thrust sound to avoid repeats
        thrustPlayed = false;
        startGame();
        audioHandler.playBattleMusic();
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
        updatePowerUps(powerUps, glorpy);
        updateBigBossBlaster();
        updateGraphics(graphicElements, glorpy);
        updateMissiles(missiles, glorpy);
        updateExplosions(explosions);
        updateBigBossBetty();
        glorpy.update();
    }

    private void draw() {
        Canvas canvas = new Canvas();
        if (ourHolder.getSurface().isValid()) {
            if (!canvasIsLocked) {
                canvas = ourHolder.lockCanvas();
                canvasIsLocked = true;
            }
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
            if (powerUps.size() > 0) {
                for (int i = powerUps.size() - 1; i >= 0; i--) {
                    PilotPowerUp pilot = powerUps.get(i);
                    pilot.draw(canvas, paint);
                }
            }
            if (missiles.size() > 0) {
                for (int i = missiles.size() - 1; i >= 0; i--) {
                    Missile missile = missiles.get(i);
                    missile.animationControl(canvas, paint);
                }
            }
            if (explosions.size() > 0) {
                for (int i = explosions.size() - 1; i >= 0; i--) {
                    Explosion explosion = explosions.get(i);
                    explosion.draw(canvas, paint);
                }
            }
            drawBigBossBlaster(bigBossBlaster, canvas, paint);
            drawBigBossBetty(canvas, paint);
            glorpy.animationControl(canvas, paint);
            drawGraphics(graphicElements, canvas, paint);
            ourHolder.unlockCanvasAndPost(canvas);
            canvasIsLocked = false;
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
        audioHandler.onPause();
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
        audioHandler.onResume();
    }

    private void startGame() {
        glorpy = new Glorpy(context, screenX, screenY);
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

                if (baseEnemy.getHitBox().intersect(glorpy.getHitBox())) {
                    if (!baseEnemy.isDestroyed) {
                        audioHandler.playSmallExplosion();
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
                    Random randGen = new Random();
                    int healthCode = randGen.nextInt(100);
                    if (healthCode <= 10) {
                        powerUps.add(new PilotPowerUp(context, screenX, screenY, baseEnemy));
                    }
                    baseEnemies.remove(i);
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
                }
                if (baseEnemy.timeToShoot() &&
                        baseEnemy.getEnemyAICode() >= 5 && baseEnemy.getX() <= baseEnemy.getFiringRange()) {
                    laserBlasts.add(new LaserBlast(context, baseEnemy.getX(), baseEnemy.getY(), screenX, screenY));
                    audioHandler.playLaserSound();
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
                if (baseEnemies.size() > 0)
                    for (int enemyIndex = baseEnemies.size() - 1; enemyIndex >= 0; enemyIndex--) {
                        final BaseEnemy baseEnemy = baseEnemies.get(enemyIndex);
                        if (fireBall.getHitBox().intersect(baseEnemy.getHitBox())) {
                            // We need to access our UI thread to update the score
                            if (!baseEnemy.isDestroyed) {
                                gameActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GameActivity.updateScore(baseEnemy.getScoreValue());
                                    }
                                });
                                audioHandler.playSmallExplosion();
                                fireBall.updateHealth();
                                baseEnemy.isDestroyed = true;
                            }
                            if (fireBall.getHealth() == 0) {
                                fireballs.remove(fireBall);
                            }
                        }
                    }
                if (bigBossBlaster != null) {
                    if (Rect.intersects(fireBall.getHitBox(), bigBossBlaster.getHitBox())) {
                        bigBossBlaster.updateHealth(glorpy.getFireDamage());
                        audioHandler.playSmallExplosion();
                        if (bigBossBlaster.getHealth() <= 0) {
                            graphicElements.remove(cannonCharging);
                            // set explosion details
                            explosions.add(new Explosion(context, bigBossBlaster.getHitBox(), 3, screenX, screenY, Explosion.LARGE_EXPLOSION));
                            audioHandler.playBigExplosion();
                            powerUps.add(new EvoPowerUp(context, screenX, screenY, bigBossBlaster));
                            // note we need to assign the score value here, as the update score method can potentially be called after bigBossBetty is deleted
                            // this is due to threading lag on occasion (depending on the users phone)
                            final int scoreValue = bigBossBlaster.getScoreValue();
                            gameActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GameActivity.updateScore(scoreValue);
                                }
                            });
                            bigBossBlaster = null;
                        }
                        int yHitPosition = fireBall.getHitBox().centerY();
                        int xHitPosition = fireBall.getX() + fireBall.getFrameWidth();
                        graphicElements.add(new FireDamageGraphic(context, xHitPosition, yHitPosition, screenX, screenY));
                        fireballs.remove(i);

                    }
                }
                if (bigBossBetty != null) {
                    if (Rect.intersects(fireBall.getHitBox(), bigBossBetty.getHitBox())) {
                        bigBossBetty.updateHealth(glorpy.getFireDamage());
                        graphicElements.add(new FireDamageGraphic(context, fireBall.getX() + fireBall.frameWidth, fireBall.getY() + (fireBall.frameHeight / 2), screenX, screenY));
                        audioHandler.playSmallExplosion();
                        if (bigBossBetty.getHealth() <= 0) {
                            explosions.add(new Explosion(context, bigBossBetty.getHitBox(), 6, screenX, screenY, Explosion.LARGE_EXPLOSION));
                            powerUps.add(new EvoPowerUp(context, screenX, screenY, bigBossBetty));
                            powerUps.add(new EvoPowerUp(context, screenX, screenY, bigBossBetty));
                            powerUps.add(new PilotPowerUp(context, screenX, screenY, bigBossBetty));
                            // note we need to assign the score value here, as the update score method can potentially be called after bigBossBetty is deleted
                            // this is due to threading lag on occasion (depending on the users phone)
                            final int scoreVal = bigBossBetty.getScoreValue();
                            gameActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GameActivity.updateScore(scoreVal);
                                }
                            });
                            audioHandler.playBigExplosion();
                            bigBossBetty = null;
                        }
                        fireballs.remove(fireBall);
                    }

                }
                if (missiles.size() > 0) {
                    for (int missileIndex = missiles.size() - 1; missileIndex >= 0; missileIndex--) {
                        final Missile missile = missiles.get(missileIndex);
                        if (Rect.intersects(fireBall.getHitBox(), missile.getHitBox())) {
                            explosions.add(new Explosion(context, missile.getHitBox(), 5, screenX, screenY, Explosion.SMALL_EXPLOSION));
                            missiles.remove(missile);
                            gameActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GameActivity.updateScore(missile.getPointValue());
                                }
                            });
                            fireBall.updateHealth();
                            audioHandler.playSmallExplosion();
                            if (fireBall.getHealth() <= 0) {
                                fireballs.remove(fireBall);
                            }
                        }
                    }
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
            }
            if (laserBlast.getHitBox().intersect(glorpy.getHitBox())) {

                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(laserBlast.getDamage());
                    }
                });

                laserBlasts.remove(i);
            }
        }
    }

    private void updatePowerUps(ArrayList<PilotPowerUp> powerUps, Glorpy glorpy) {
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            final PilotPowerUp powerUp = powerUps.get(i);
            powerUp.update();
            if (powerUp.getX() < 0) {
                powerUps.remove(i);
            }
            if (powerUp.getHitBox().intersect(glorpy.getHitBox())) {
                // using shooting animation for power up consumption
                glorpy.setIsShooting(true);
                audioHandler.playPowerUp();

                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(powerUp.getHealingPower());
                        GameActivity.updateScore(powerUp.getHealingPower());
                    }
                });
                if (powerUp instanceof EvoPowerUp) {
                    DataHolder.freePoints++;
                }
                powerUps.remove(i);
            }
        }
    }

    private int updateDifficulty(int score) {
        int enemyCount = 0;
        if (bigBossBlaster == null && bigBossBetty == null && !spawnBoss) {
            if (score < 100) {
                enemyCount = 1;
            } else if (score < 200) {
                enemyCount = score / 100;
            } else if (score < 2000) {
                enemyCount = 2 + score / 500;
            } else {
                enemyCount = 6 + score / 2000;
            }
            if (score / 1000 >= difficultyFactor) {
                spawnBoss = true;
            }
        } else if (spawnBoss && baseEnemies.size() == 0) {
            if (difficultyFactor < 10) {
                bigBossBlaster = new BigBossBlaster(context, screenX, screenY, glorpy);
                difficultyFactor += 10;
            } else if (difficultyFactor > 10 && difficultyFactor < 20) {
                bigBossBetty = new BigBossBetty(context, screenX, screenY, laserBlasts, missiles, glorpy, audioHandler);
                difficultyFactor += 20;
            } else {
                Random random = new Random();
                enemyCount = score / 7000;
                int randomInt = random.nextInt(100);
                if (randomInt < 51) {
                    bigBossBlaster = new BigBossBlaster(context, screenX, screenY, glorpy);
                    difficultyFactor += 10;
                } else {
                    bigBossBetty = new BigBossBetty(context, screenX, screenY, laserBlasts, missiles, glorpy, audioHandler);
                    difficultyFactor += 20;
                }
            }
            spawnBoss = false;
        }
        return enemyCount;

    }

    private void updateBigBossBlaster() {
        if (bigBossBlaster != null) {
            bigBossBlaster.update();
            if (bigBossBlaster.getCurrentAI() == BigBossBlaster.AI_KAMIKAZE_MODE) {
                if (!thrustPlayed) {
                    audioHandler.playThrust();
                    thrustPlayed = true;
                }
            } else {
                thrustPlayed = false;
            }
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
                        audioHandler.playBigBlast();
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
                    audioHandler.playLaserSound();
                }
            }
            if (Rect.intersects(bigBossBlaster.getHitBox(), glorpy.getHitBox()) && !bigBossBlaster.isRamDamaged()) {
                bigBossBlaster.setRamDamaged(true);
                final int ramDamage = bigBossBlaster.getDamage();
                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(ramDamage);
                    }
                });
            }
        }
    }

    public void updateBigBossBetty() {
        if (bigBossBetty != null) {
            bigBossBetty.update();
            if (Rect.intersects(glorpy.getHitBox(), bigBossBetty.getHitBox()) && !bigBossBetty.didHitGlorpy()) {
                bigBossBetty.setDidHitGlorpy(true);
                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.updateHealth(bigBossBetty.getDamage());
                    }
                });
            }
        }
    }

    private void updateMissiles(ArrayList<Missile> missiles, Glorpy glorpy) {
        if (missiles.size() > 0) {
            for (int i = missiles.size() - 1; i >= 0; i--) {
                final Missile missile = missiles.get(i);
                missile.update();
                if (Rect.intersects(missile.getHitBox(), glorpy.getHitBox())) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GameActivity.updateHealth(missile.getDamage());
                        }
                    });
                    explosions.add(new Explosion(context, glorpy.getHitBox(), 10, screenX, screenY, Explosion.SMALL_EXPLOSION));
                    missiles.remove(i);
                }
                if (missile.getX() < -missile.frameWidth) {
                    missiles.remove(i);
                }
            }

        }
    }

    private void updateGraphics(final ArrayList<GraphicElement> graphicElements, Glorpy glorpy) {
        if (graphicElements.size() > 0) {
            for (int i = graphicElements.size() - 1; i >= 0; i--) {
                final GraphicElement currentElement = graphicElements.get(i);
                currentElement.update();
                if (currentElement.getX() < 0) {
                    graphicElements.remove(i);
                    break;
                }
                if (Rect.intersects(glorpy.getHitBox(), currentElement.getHitBox())) {
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GameActivity.updateHealth(currentElement.getDamage());
                        }
                    });
                    graphicElements.remove(i);
                }
                if (currentElement.isNeedDelete()) {
                    graphicElements.remove(i);
                }
            }
        }
    }

    private void updateExplosions(ArrayList<Explosion> explosions) {
        if (explosions.size() > 0) {
            for (int i = explosions.size() - 1; i >= 0; i--) {
                Explosion explosion = explosions.get(i);
                explosion.update();
                if (explosion.isNeedsDelete()) {
                    explosions.remove(explosion);
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

    private void drawBigBossBetty(Canvas canvas, Paint paint) {
        if (bigBossBetty != null) {
            bigBossBetty.animationControl(canvas, paint);
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

}

