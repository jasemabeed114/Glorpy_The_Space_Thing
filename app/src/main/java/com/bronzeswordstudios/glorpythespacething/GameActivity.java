package com.bronzeswordstudios.glorpythespacething;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class GameActivity extends AppCompatActivity {

    public static final int HEALTH_NUM_ID = 2837465;
    private static final int SCORE_NUM_ID = 2856723;
    public static FrameLayout totalView;
    private static Activity activity;
    private static GameView gameView;

    private static void checkEndCondition(int health) {
        if (health <= 0) {
            Intent intent = new Intent(activity, GameOverActivity.class);
            DBHelper dbHelper = new DBHelper(activity);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
            SQLiteDatabase localDb = dbHelper.getWritableDatabase();
            long newRowID = localDb.update(DataHolder.DataEntry.TABLE_NAME, contentValues, null, null);
            activity.startActivity(intent);
            activity.finish();

        }
    }

    public static void updateScore(int scoredPoints) {
        TextView scoreNumView = totalView.findViewById(SCORE_NUM_ID);
        int score = Integer.parseInt(scoreNumView.getText().toString());
        score += scoredPoints;

        if (score < 0) {
            score = 0;
        }

        scoreNumView.setText(String.valueOf(score));
        DataHolder.score = score;

    }

    public static void updateHealth(int healthChange) {
        TextView healthNumView = totalView.findViewById(HEALTH_NUM_ID);
        int currentHealth = Integer.parseInt(healthNumView.getText().toString());
        currentHealth += healthChange;
        // make sure we stay in range
        if (currentHealth > (100 + DataHolder.lifeMod)) {
            currentHealth = 100 + DataHolder.lifeMod;
        }
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        healthNumView.setText(String.valueOf(currentHealth));
        // alert the user of low health
        if (currentHealth <= 50) {
            healthNumView.setTextColor(Color.RED);
        } else {
            healthNumView.setTextColor(Color.WHITE);
        }
        checkEndCondition(currentHealth);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final int DOWN_BUTTON_ID = 2257611;
        final int SCORE_TEXT_ID = 2872821;
        final int HEALTH_TEXT_ID = 9872635;
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Display display = getWindowManager().getDefaultDisplay();
        activity = this;
        final Point point = new Point();
        display.getSize(point);
        final int adjustedY = point.y - 150;
        gameView = new GameView(this, GameActivity.this, point.x, adjustedY);
        float screenScaleX = screenScaleX(point.x);
        float screenScaleY = screenScaleY(adjustedY);
        float bitScale = bitmapScale(screenScaleX, screenScaleY);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.geo);

        // begin creating the layout
        totalView = new FrameLayout(this);
        RelativeLayout userHUD = new RelativeLayout(this);
        RelativeLayout.LayoutParams HUDlayoutParams = new
                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        userHUD.setLayoutParams(HUDlayoutParams);

        // fire button
        ImageButton fireButton = new ImageButton(this);
        fireButton.setImageResource(R.drawable.fireball);
        fireButton.setBackground(null);
        RelativeLayout.LayoutParams fireButtonParams = new RelativeLayout.LayoutParams(250, 150);
        fireButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fireButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        fireButton.setLayoutParams(fireButtonParams);

        // control buttons
        ImageButton upButton = new ImageButton(this);
        ImageButton downButton = new ImageButton(this);
        upButton.setImageResource(R.drawable.baseline_arrow_upward_black_18dp);
        downButton.setImageResource(R.drawable.baseline_arrow_downward_black_18dp);
        RelativeLayout.LayoutParams upButtonParams = new RelativeLayout.LayoutParams(200, 150);
        RelativeLayout.LayoutParams downButtonParams = new RelativeLayout.LayoutParams(200, 150);
        upButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        downButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        downButton.setId(DOWN_BUTTON_ID);
        downButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        downButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        upButtonParams.addRule(RelativeLayout.RIGHT_OF, DOWN_BUTTON_ID);
        upButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        downButtonParams.setMargins(36, 0, 0, 0);
        upButton.setLayoutParams(upButtonParams);
        downButton.setLayoutParams(downButtonParams);

        // controller background
        View controllerBackground = new View(this);
        RelativeLayout.LayoutParams ctrlBackLayoutParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 150);
        controllerBackground.setBackgroundColor(Color.GRAY);
        controllerBackground.setAlpha(0.15f);
        ctrlBackLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        controllerBackground.setLayoutParams(ctrlBackLayoutParam);

        // score views
        TextView scoreTextView = new TextView(this);
        TextView scoreNumberView = new TextView(this);
        scoreTextView.setTypeface(typeface);
        scoreNumberView.setTypeface(typeface);
        scoreTextView.setTextSize(16 * bitScale);
        scoreNumberView.setTextSize(16 * bitScale);
        scoreTextView.setText(R.string.score_text);
        scoreNumberView.setText("0");
        scoreTextView.setTextColor(Color.WHITE);
        scoreNumberView.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams scoreTextLayoutParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams scoreNumLayoutParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scoreTextLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        scoreTextLayoutParam.addRule(RelativeLayout.LEFT_OF, SCORE_NUM_ID);
        scoreTextLayoutParam.setMargins(0, 0, 36, 0);
        scoreTextView.setId(SCORE_TEXT_ID);
        scoreNumberView.setId(SCORE_NUM_ID);
        scoreNumLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        scoreNumLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        scoreNumLayoutParam.setMargins(0, 0, 24, 0);
        scoreTextView.setLayoutParams(scoreTextLayoutParam);
        scoreNumberView.setLayoutParams(scoreNumLayoutParam);

        // heath view
        TextView healthText = new TextView(this);
        healthText.setTypeface(typeface);
        healthText.setTextColor(Color.WHITE);
        healthText.setId(HEALTH_TEXT_ID);
        healthText.setText(R.string.health_text);
        healthText.setTextSize(16 * bitScale);
        RelativeLayout.LayoutParams healthTextParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView healthNum = new TextView(this);
        healthNum.setTypeface(typeface);
        healthNum.setId(HEALTH_NUM_ID);
        healthNum.setTextSize(16 * bitScale);
        healthNum.setTextColor(Color.WHITE);
        int healthTotal = 100 + DataHolder.lifeMod;
        healthNum.setText(String.valueOf(healthTotal));
        RelativeLayout.LayoutParams healthNumParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        healthTextParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        healthTextParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        healthTextParams.setMargins(36, 0, 0, 0);
        healthNumParams.setMargins(24, 0, 0, 0);
        healthNumParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        healthNumParams.addRule(RelativeLayout.RIGHT_OF, HEALTH_TEXT_ID);
        healthNum.setLayoutParams(healthNumParams);
        healthText.setLayoutParams(healthTextParams);


        // set layout
        userHUD.addView(controllerBackground);
        userHUD.addView(fireButton);
        userHUD.addView(upButton);
        userHUD.addView(downButton);
        userHUD.addView(scoreTextView);
        userHUD.addView(scoreNumberView);
        userHUD.addView(healthNum);
        userHUD.addView(healthText);
        totalView.addView(gameView);
        totalView.addView(userHUD);
        setContentView(totalView);

        fireButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!gameView.glorpy.isShooting()) {
                    gameView.fireballs.add(new FireBall(GameActivity.this,
                            gameView.glorpy.getX(), gameView.glorpy.getY(), point.x, adjustedY));
                    gameView.audioHandler.playFireBallSound();
                    gameView.glorpy.setIsShooting(true);

                }
                return true;
            }
        });


        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameView.glorpy.setMovingUp(true);
                    gameView.glorpy.setMovingDown(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameView.glorpy.setMovingDown(false);
                    gameView.glorpy.setMovingUp(false);
                }
                return true;
            }
        });

        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gameView.glorpy.setMovingUp(false);
                    gameView.glorpy.setMovingDown(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    gameView.glorpy.setMovingDown(false);
                    gameView.glorpy.setMovingUp(false);
                }
                return true;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        DBHelper dbHelper = new DBHelper(this);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataHolder.DataEntry.POINTS_VALUE, DataHolder.freePoints);
        SQLiteDatabase localDb = dbHelper.getWritableDatabase();
        long newRowID = localDb.update(DataHolder.DataEntry.TABLE_NAME, contentValues, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onStop() {
        gameView.audioHandler.soundPoolRelease();
        super.onStop();
    }

    private float screenScaleX(float screenX) {
        screenX = screenX / 1920f;
        return screenX;
    }

    private float screenScaleY(float screenY) {
        screenY = screenY / 930f;
        return screenY;
    }

    private float bitmapScale(float scaleX, float scaleY) {
        if (scaleX > scaleY) {
            return scaleX;
        } else {
            return scaleY;
        }
    }

}
