package com.nietupski.contaseproblemas;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import static android.media.MediaPlayer.create;

public class Musica extends Service {
    private static final String TAG = null;
    public static MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        player = create(this, R.raw.musica);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {
        player.stop();
        player.release();
    }

}

