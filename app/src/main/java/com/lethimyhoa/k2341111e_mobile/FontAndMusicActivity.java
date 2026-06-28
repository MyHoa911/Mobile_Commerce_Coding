package com.lethimyhoa.k2341111e_mobile;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class FontAndMusicActivity extends AppCompatActivity {
    private static final String TAG = "FontAndMusicActivity";
    Button btnPlayAudio1;
    Button btnPlayAudio2;
    TextView txtTitle;
    ListView lvFont;
    ArrayList<String> fonts;
    ArrayAdapter<String> adapterFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_and_music);
        addViews();
        addEvents();
        loadFonts();
    }

    private void loadFonts() {
        try {
            AssetManager assetManager = getAssets();
            String[] fontFiles = assetManager.list("fonts");
            if (fontFiles != null) {
                fonts.clear();
                for (String font : fontFiles) {
                    fonts.add(font);
                }
                adapterFont.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading fonts: " + e.toString());
        }
    }

    private void addEvents() {
        btnPlayAudio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio("musics/audio_success.mp3");
            }
        });
        btnPlayAudio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio("musics/audio_fail.mp3");
            }
        });
        lvFont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                changeFont(i);
            }
        });
    }

    private void changeFont(int i) {
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + adapterFont.getItem(i));
            txtTitle.setTypeface(typeface);
        } catch (Exception e) {
            Log.e(TAG, "Error changing font: " + e.toString());
        }
    }

    private void playAudio(String audioFile) {
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(audioFile);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength()
            );
            assetFileDescriptor.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio: " + e.toString());
        }
    }

    private void addViews() {
        btnPlayAudio1 = findViewById(R.id.btnPlayAudio1);
        btnPlayAudio2 = findViewById(R.id.btnPlayAudio2);
        txtTitle = findViewById(R.id.txtTitle);
        lvFont = findViewById(R.id.lvFont);
        
        fonts = new ArrayList<>();
        adapterFont = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fonts);
        lvFont.setAdapter(adapterFont);
    }
}
