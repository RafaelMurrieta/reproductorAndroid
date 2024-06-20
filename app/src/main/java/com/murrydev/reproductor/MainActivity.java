package com.murrydev.reproductor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnplay_pause, btn_repetir, btnlike, btnvolume;
    MediaPlayer mp;
    ImageView iv;
    int repetir = 2, posicion = 0;
    private MediaPlayer[] vectormp;
    private SeekBar seekBar, volumenBar;
    private boolean heartbol = false;
    private Handler handler = new Handler();
    private AudioManager audioManager;
    private List<Integer> shuffleList;
    private boolean isShuffle = false;
    private int shufflePos = 0;
    private BroadcastReceiver volumeReceiver;

    private TextView timebefore, timeafter;
    private Button btnrandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnplay_pause = findViewById(R.id.play);
        btn_repetir = findViewById(R.id.repeat);
        iv = findViewById(R.id.cover);
        seekBar = findViewById(R.id.seekBar);
        btnlike = findViewById(R.id.likebtn);
        volumenBar = findViewById(R.id.volumenBar);
        btnvolume = findViewById(R.id.btnvolume);
        timebefore = findViewById(R.id.timebefore);
        timeafter = findViewById(R.id.timeafter);
        btnrandom = findViewById(R.id.random);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        volumenBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumenBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
                int volumenActual = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (volumenActual == 0){
                    btnvolume.setBackgroundResource(R.drawable.volumenoff);
                }else{
                    btnvolume.setBackgroundResource(R.drawable.volumen);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setButtonColorFilter(btnplay_pause, R.color.white);
        setButtonColorFilter(findViewById(R.id.before), R.color.white);
        setButtonColorFilter(findViewById(R.id.next), R.color.white);
        setButtonColorFilter(findViewById(R.id.stop), R.color.white);
        setButtonColorFilter(findViewById(R.id.repeat), R.color.white);

        inicializarMediaPlayer();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && vectormp[posicion] != null) {
                    vectormp[posicion].seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        volumeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    volumenBar.setProgress(currentVolume);
                    if (currentVolume == 0) {
                        btnvolume.setBackgroundResource(R.drawable.volumenoff);
                    } else {
                        btnvolume.setBackgroundResource(R.drawable.volumen);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(volumeReceiver, filter);
    }

    private void setButtonColorFilter(Button button, int color) {
        button.getBackground().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
    }

    private void inicializarMediaPlayer() {
        vectormp = new MediaPlayer[11];
        vectormp[0] = MediaPlayer.create(this, R.raw.dusttodust);
        vectormp[1] = MediaPlayer.create(this, R.raw.sabbuttre);
        vectormp[2] = MediaPlayer.create(this, R.raw.lomalodeserbueno);
        vectormp[3] = MediaPlayer.create(this, R.raw.batcountry);
        vectormp[4] = MediaPlayer.create(this, R.raw.dullknives);
        vectormp[5] = MediaPlayer.create(this, R.raw.evolve);
        vectormp[6] = MediaPlayer.create(this, R.raw.hailtotheking);
        vectormp[7] = MediaPlayer.create(this, R.raw.hycad);
        vectormp[8] = MediaPlayer.create(this, R.raw.mamasaid);
        vectormp[9] = MediaPlayer.create(this, R.raw.trapperunderice);
        vectormp[10] = MediaPlayer.create(this, R.raw.turnthepage);

        for (int i = 0; i < vectormp.length; i++) {
            MediaPlayer player = vectormp[i];
            player.setOnPreparedListener(mp -> seekBar.setMax(mp.getDuration()));
            player.setOnCompletionListener(mp -> siguiente(null));
        }
    }


    public void expandSeekBar(View view) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) volumenBar.getLayoutParams();
        if (params.width == 800) {
            params.width = 1;
        } else {
            params.width = 800;
        }
        volumenBar.setLayoutParams(params);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(volumenBar.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(volumenBar.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.applyTo(constraintLayout);
    }

    private void updateSeekBar() {
        if (vectormp[posicion] != null) {
            int currentPosition = vectormp[posicion].getCurrentPosition();
            int totalDuration = vectormp[posicion].getDuration();
            int remainingTime = totalDuration - currentPosition;

            seekBar.setProgress(currentPosition);
            timebefore.setText(formatTime(currentPosition));
            timeafter.setText(formatTime(remainingTime));
        }
        handler.postDelayed(this::updateSeekBar, 1000);
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void PLayPause(View view) {
        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause();
            btnplay_pause.setBackgroundResource(R.drawable.play);
            Toast.makeText(this, "Pause", Toast.LENGTH_LONG).show();
        } else {
            vectormp[posicion].start();
            btnplay_pause.setBackgroundResource(R.drawable.pause);
            Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();
            updateSeekBar();
        }
    }

    public void stop(View view) {
        if (vectormp[posicion] != null) {
            vectormp[posicion].stop();
            liberarMediaPlayer();
            inicializarMediaPlayer();
            posicion = 0;
            shufflePos = 0;
            btnplay_pause.setBackgroundResource(R.drawable.play);
            iv.setBackgroundResource(R.drawable.blackalbum);
            Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
        }
    }

    private void liberarMediaPlayer() {
        for (int i = 0; i < vectormp.length; i++) {
            if (vectormp[i] != null) {
                vectormp[i].release();
                vectormp[i] = null;
            }
        }
    }

    public void shuffle(View view) {
        isShuffle = !isShuffle;
        if (isShuffle) {
            Toast.makeText(this, "Modo aleatorio activado", Toast.LENGTH_SHORT).show();
            btnrandom.setBackgroundTintList(getResources().getColorStateList(R.color.gray, null));
            shuffleList = new ArrayList<>();
            for (int i = 0; i < vectormp.length; i++) {
                shuffleList.add(i);
            }
            Collections.shuffle(shuffleList);
            shufflePos = shuffleList.indexOf(posicion);
        } else {
            btnrandom.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));
            Toast.makeText(this, "Modo aleatorio desactivado", Toast.LENGTH_SHORT).show();
        }
    }

    public void repetir(View view) {
        if (repetir == 1) {
            btn_repetir.setBackgroundResource(R.drawable.repeats);
            vectormp[posicion].setLooping(false);
            repetir = 2;
            Toast.makeText(this, "No repetir", Toast.LENGTH_LONG).show();
        } else {
            btn_repetir.setBackgroundResource(R.drawable.norepeat);
            vectormp[posicion].setLooping(true);
            repetir = 1;
            Toast.makeText(this, "Repetir", Toast.LENGTH_LONG).show();
        }
    }

    public void siguiente(View view) {
        btnlike.setBackgroundResource(R.drawable.heartborder);
        btnplay_pause.setBackgroundResource(R.drawable.pause);
        vectormp[posicion].stop();
        vectormp[posicion].reset();
        liberarMediaPlayer();
        inicializarMediaPlayer();
        if (isShuffle) {
            int nuevaPosicion;
            do {
                nuevaPosicion = (int) (Math.random() * vectormp.length);
            } while (nuevaPosicion == posicion);
            posicion = nuevaPosicion;
        } else {
            posicion = (posicion + 1) % vectormp.length;
        }
        vectormp[posicion].start();
        setImageResource(posicion);
        updateSeekBar();
    }


    public void before(View view) {
        btnlike.setBackgroundResource(R.drawable.heartborder);
        btnplay_pause.setBackgroundResource(R.drawable.pause);
        int currentPosition = vectormp[posicion].getCurrentPosition();

        // Si han pasado menos de 2 segundos, cambia a la canción anterior
        if (currentPosition <= 2000) {
            vectormp[posicion].stop();
            liberarMediaPlayer();
            inicializarMediaPlayer();
            if (isShuffle) {
                int nuevaPosicion;
                do {
                    nuevaPosicion = (int) (Math.random() * vectormp.length);
                } while (nuevaPosicion == posicion);

                posicion = nuevaPosicion;
            } else {
                posicion = (posicion - 1 + vectormp.length) % vectormp.length;
            }
            vectormp[posicion].start();
            setImageResource(posicion);
            updateSeekBar();
        } else {
            // Si han pasado más de 2 segundos, regresa al inicio de la canción actual
            vectormp[posicion].seekTo(0);
        }
    }



    public void liked(View view) {
        if (!heartbol) {
            btnlike.setBackgroundResource(R.drawable.heart);
            heartbol = true;
        } else {
            btnlike.setBackgroundResource(R.drawable.heartborder);
            heartbol = false;
        }
    }

    private void setImageResource(int posicion) {
        switch (posicion) {
            case 0:
                iv.setImageResource(R.drawable.queenofthemurderscene);
                break;
            case 1:
                iv.setImageResource(R.drawable.blackalbum);
                break;
            case 2:
                iv.setImageResource(R.drawable.lomalodeserbueno);
                break;
            case 3:
                iv.setImageResource(R.drawable.cityofevil);
                break;
            case 4:
                iv.setImageResource(R.drawable.queenofthemurderscene);
                break;
            case 5:
                iv.setImageResource(R.drawable.error);
                break;
            case 6:
                iv.setImageResource(R.drawable.hailtothekingal);
                break;
            case 7:
                iv.setImageResource(R.drawable.keepmefed);
                break;
            case 8:
                iv.setImageResource(R.drawable.load);
                break;
            case 9:
                iv.setImageResource(R.drawable.ridethelighnting);
                break;
            case 10:
                iv.setImageResource(R.drawable.garageinc);
                break;
            default:
                iv.setImageResource(R.drawable.queenofthemurderscene);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liberarMediaPlayer();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(volumeReceiver);
    }
}
