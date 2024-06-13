package com.murrydev.reproductor;

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

    Button btnplay_pause, btn_repetir, btnlike,btnvolume;
    MediaPlayer mp;
    ImageView iv;
    int repetir = 2, posicion = 0;
    private MediaPlayer[] vectormp;
    private SeekBar seekBar,volumenBar;

    private boolean heartbol = false;
    private Handler handler = new Handler();
    private AudioManager audioManager;
    private List<Integer> shuffleList;
    private boolean isShuffle = false;
    private int shufflePos = 0;


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
    }

    private void setButtonColorFilter(Button button, int color) {
        button.getBackground().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
    }

    private void inicializarMediaPlayer() {
        vectormp = new MediaPlayer[3];
        vectormp[0] = MediaPlayer.create(this, R.raw.dusttodust);
        vectormp[1] = MediaPlayer.create(this, R.raw.sabbuttre);
        vectormp[2] = MediaPlayer.create(this, R.raw.lomalodeserbueno);
        for (MediaPlayer player : vectormp) {
            player.setOnPreparedListener(mp -> seekBar.setMax(mp.getDuration()));
        }
    }

    public void expandSeekBar(View view) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) volumenBar.getLayoutParams();
        if (params.width == 800){
            params.width = 1;
        }else {
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
            seekBar.setProgress(vectormp[posicion].getCurrentPosition());
        }
        handler.postDelayed(this::updateSeekBar, 1000);
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
            shufflePos = 0; // Reset shuffle position
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
        isShuffle = !isShuffle; // Toggle shuffle mode
        if (isShuffle) {
            Toast.makeText(this, "Shuffle Mode On", Toast.LENGTH_SHORT).show();
            shuffleList = new ArrayList<>();
            for (int i = 0; i < vectormp.length; i++) {
                shuffleList.add(i);
            }
            Collections.shuffle(shuffleList);
            shufflePos = shuffleList.indexOf(posicion); // Ensure the current song position is synchronized
        } else {
            Toast.makeText(this, "Shuffle Mode Off", Toast.LENGTH_SHORT).show();
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
        vectormp[posicion].stop();
        liberarMediaPlayer();
        inicializarMediaPlayer();
        if (isShuffle) {
            shufflePos = (shufflePos + 1) % shuffleList.size();
            posicion = shuffleList.get(shufflePos);
        } else {
            posicion = (posicion + 1) % vectormp.length;
        }
        vectormp[posicion].start();
        setImageResource(posicion);
        updateSeekBar();
    }

    public void before(View view) {
        btnlike.setBackgroundResource(R.drawable.heartborder);
        vectormp[posicion].stop();
        liberarMediaPlayer();
        inicializarMediaPlayer();
        if (isShuffle) {
            shufflePos = (shufflePos - 1 + shuffleList.size()) % shuffleList.size();
            posicion = shuffleList.get(shufflePos);
        } else {
            posicion = (posicion - 1 + vectormp.length) % vectormp.length;
        }
        vectormp[posicion].start();
        setImageResource(posicion);
        updateSeekBar();
    }


    public void liked(View view){
        if (!heartbol){
            btnlike.setBackgroundResource(R.drawable.heart);
            heartbol = true;
        }else{
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
    }

}
