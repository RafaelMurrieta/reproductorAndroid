package com.murrydev.reproductor;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

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
}