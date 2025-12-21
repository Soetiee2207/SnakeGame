package com.example.btl_snake_game.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.AudioAttributes;

public class SoundManager {
    private static SoundManager instance;
    private Context context;
    
    // Background music players
    private MediaPlayer menuMusicPlayer;
    private MediaPlayer gameMusicPlayer;
    
    // Sound effects
    private SoundPool soundPool;
    private int sfxEatId;
    private int sfxDieId;
    private boolean sfxLoaded = false;
    
    private SoundManager() {}
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    public void init(Context context) {
        this.context = context.getApplicationContext();
        initSoundPool();
        loadSoundEffects();
    }
    
    private void initSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        
        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build();
        
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                sfxLoaded = true;
            }
        });
    }
    
    private void loadSoundEffects() {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd("sounds/sfx_eat.wav");
            sfxEatId = soundPool.load(afd, 1);
            afd.close();
            
            afd = context.getAssets().openFd("sounds/sfx_die.wav");
            sfxDieId = soundPool.load(afd, 1);
            afd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Menu background music
    public void playMenuMusic() {
        if (!SettingManager.getInstance().isSoundEnabled()) return;
        
        stopGameMusic();
        
        if (menuMusicPlayer == null) {
            try {
                menuMusicPlayer = new MediaPlayer();
                AssetFileDescriptor afd = context.getAssets().openFd("sounds/01 The Piano.mp3");
                menuMusicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                menuMusicPlayer.setLooping(true);
                menuMusicPlayer.setVolume(0.5f, 0.5f);
                menuMusicPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        
        if (!menuMusicPlayer.isPlaying()) {
            menuMusicPlayer.start();
        }
    }
    
    public void stopMenuMusic() {
        if (menuMusicPlayer != null && menuMusicPlayer.isPlaying()) {
            menuMusicPlayer.pause();
            menuMusicPlayer.seekTo(0);
        }
    }
    
    // Game background music
    public void playGameMusic() {
        if (!SettingManager.getInstance().isSoundEnabled()) return;
        
        stopMenuMusic();
        
        if (gameMusicPlayer == null) {
            try {
                gameMusicPlayer = new MediaPlayer();
                AssetFileDescriptor afd = context.getAssets().openFd("sounds/02 99 Problems.mp3");
                gameMusicPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                gameMusicPlayer.setLooping(true);
                gameMusicPlayer.setVolume(0.5f, 0.5f);
                gameMusicPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        
        if (!gameMusicPlayer.isPlaying()) {
            gameMusicPlayer.start();
        }
    }
    
    public void stopGameMusic() {
        if (gameMusicPlayer != null && gameMusicPlayer.isPlaying()) {
            gameMusicPlayer.pause();
            gameMusicPlayer.seekTo(0);
        }
    }
    
    // Sound effects
    public void playEatSound() {
        if (!SettingManager.getInstance().isSoundEnabled()) return;
        if (sfxLoaded && soundPool != null) {
            soundPool.play(sfxEatId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }
    
    public void playDieSound() {
        if (!SettingManager.getInstance().isSoundEnabled()) return;
        if (sfxLoaded && soundPool != null) {
            soundPool.play(sfxDieId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }
    
    // Pause/Resume all
    public void pauseAll() {
        if (menuMusicPlayer != null && menuMusicPlayer.isPlaying()) {
            menuMusicPlayer.pause();
        }
        if (gameMusicPlayer != null && gameMusicPlayer.isPlaying()) {
            gameMusicPlayer.pause();
        }
    }
    
    public void resumeMusic(boolean isInGame) {
        if (!SettingManager.getInstance().isSoundEnabled()) return;
        
        if (isInGame) {
            if (gameMusicPlayer != null && !gameMusicPlayer.isPlaying()) {
                gameMusicPlayer.start();
            }
        } else {
            if (menuMusicPlayer != null && !menuMusicPlayer.isPlaying()) {
                menuMusicPlayer.start();
            }
        }
    }
    
    // Release resources
    public void release() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.release();
            menuMusicPlayer = null;
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.release();
            gameMusicPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
    
    // Toggle sound on/off
    public void toggleSound(boolean enabled) {
        if (!enabled) {
            pauseAll();
        }
    }
}
