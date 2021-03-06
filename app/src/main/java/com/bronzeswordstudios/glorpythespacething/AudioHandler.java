package com.bronzeswordstudios.glorpythespacething;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

public class AudioHandler {
    private final AudioManager audioManager;
    private final Context context;
    private MediaPlayer battleMusic;
    private SoundPool soundPool;
    private int bigBlastID;
    private int bigExplosionID;
    private int fireballID;
    private int glorpyPowerUpID;
    private int laserID;
    private int smallExplosionID;
    private int thrustID;
    private AudioAttributes audioAttributes;

    public AudioHandler(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        bigBlastID = soundPool.load(context, R.raw.big_blast, 1);
        bigExplosionID = soundPool.load(context, R.raw.big_explosion, 1);
        fireballID = soundPool.load(context, R.raw.fireball, 1);
        glorpyPowerUpID = soundPool.load(context, R.raw.glorpy_powerup, 1);
        laserID = soundPool.load(context, R.raw.laser, 1);
        smallExplosionID = soundPool.load(context, R.raw.small_explosion, 1);
        thrustID = soundPool.load(context, R.raw.thrust, 1);
        this.context = context;
    }

    public void playLaserSound() {
        soundPool.play(laserID, 0.25f, 0.25f, 0, 0, 1.0f);
    }

    public void playFireBallSound() {
        soundPool.play(fireballID, 0.25f, 0.25f, 0, 0, 1.0f);
    }


    public void playBattleMusic() {
        battleMusic = MediaPlayer.create(context, R.raw.glorpy_battlewav);
        battleMusic.setLooping(true);
        battleMusic.setVolume(0.8f, 0.8f);
        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    battleMusic.stop();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    battleMusic.release();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    battleMusic.start();
                }
            }
        };
        int focusResult;
        // Don't use depreciated API for audioFocusRequest starting in OREO (API 26 +)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes).setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(onAudioFocusChangeListener).build();
            focusResult = audioManager.requestAudioFocus(focusRequest);
        } else {
            focusResult = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (focusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            battleMusic.start();
        }
    }

    public void playSmallExplosion() {
        soundPool.play(smallExplosionID, 0.25f, 0.25f, 0, 0, 1.0f);
    }

    public void playPowerUp() {
        soundPool.play(glorpyPowerUpID, 0.50f, 0.50f, 0, 0, 1.0f);
    }

    public void playBigBlast() {
        soundPool.play(bigBlastID, 0.25f, 0.25f, 0, 0, 1.0f);
    }

    public void playBigExplosion() {
        soundPool.play(bigExplosionID, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void playThrust() {
        soundPool.play(thrustID, 0.25f, 0.25f, 0, 0, 1.0f);
    }

    public void onPause() {
        battleMusic.pause();
    }

    public void soundPoolRelease() {
        soundPool.release();
        soundPool = null;
    }

    public void onResume() {
        battleMusic.start();
    }

    public void onRestart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        bigBlastID = soundPool.load(context, R.raw.big_blast, 1);
        bigExplosionID = soundPool.load(context, R.raw.big_explosion, 1);
        fireballID = soundPool.load(context, R.raw.fireball, 1);
        glorpyPowerUpID = soundPool.load(context, R.raw.glorpy_powerup, 1);
        laserID = soundPool.load(context, R.raw.laser, 1);
        smallExplosionID = soundPool.load(context, R.raw.small_explosion, 1);
        thrustID = soundPool.load(context, R.raw.thrust, 1);
    }
}
