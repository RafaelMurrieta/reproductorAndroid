package com.murrydev.reproductor;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnplay_pause, btn_repetir;

    MediaPlayer mp;
    ImageView iv;
    int repetir = 2, posicion = 0;
    MediaPlayer vectormp[] = new MediaPlayer[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnplay_pause = findViewById(R.id.play);
        btn_repetir = findViewById(R.id.repeat);
        iv = findViewById(R.id.cover);

        vectormp[0] = MediaPlayer.create(this, R.raw.dusttodust);
        vectormp[1] = MediaPlayer.create(this, R.raw.sabbuttre);
        vectormp[2] = MediaPlayer.create(this, R.raw.lomalodeserbueno);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
    public void PLayPause(View view){
        if (vectormp[posicion].isPlaying()){
            vectormp[posicion].pause();
            btnplay_pause.setBackgroundResource(R.drawable.play);
            Toast.makeText(this, "Pause", Toast.LENGTH_LONG).show();
        }else{
            vectormp[posicion].start();
            btnplay_pause.setBackgroundResource(R.drawable.pause);
            Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();
        }
    }

    public void stop(View view){
        if (vectormp[posicion] != null){
            vectormp[posicion].stop();
            vectormp[0] = MediaPlayer.create(this, R.raw.dusttodust);
            vectormp[1] = MediaPlayer.create(this, R.raw.sabbuttre);
            vectormp[2] = MediaPlayer.create(this, R.raw.lomalodeserbueno);
            posicion = 0;
            btnplay_pause.setBackgroundResource(R.drawable.play);
            iv.setBackgroundResource(R.drawable.blackalbum);
            Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
        }
    }

    public  void  repetir(View view){
        if (repetir == 1){
            btn_repetir.setBackgroundResource(R.drawable.repeats);
            vectormp[posicion].setLooping(false);
            repetir = 2;
            Toast.makeText(this, "No repetir", Toast.LENGTH_LONG).show();
        }else{
            btn_repetir.setBackgroundResource(R.drawable.norepeat);
            vectormp[posicion].setLooping(true);
            repetir = 1;
            Toast.makeText(this, "Repetor", Toast.LENGTH_LONG).show();
        }
    }

    public  void siguiente(View view){
        if (posicion < vectormp.length -1){
            if (vectormp[posicion].isPlaying()){
                vectormp[posicion].stop();
                posicion++;
                vectormp[posicion].start();
                if (posicion == 0){iv.setImageResource(R.drawable.queenofthemurderscene);}
                else if (posicion == 1) {iv.setImageResource(R.drawable.blackalbum);}
                else{iv.setImageResource(R.drawable.lomalodeserbueno);}
            }else{
                posicion++;
                if (posicion == 0){iv.setImageResource(R.drawable.queenofthemurderscene);}
                else if (posicion == 1) {iv.setImageResource(R.drawable.blackalbum);}
                else{iv.setImageResource(R.drawable.lomalodeserbueno);}
            }
        }else{
            Toast.makeText(this, "No hay canciones", Toast.LENGTH_LONG).show();
        }
    }
}