package com.lethimyhoa.k2341111e_mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class NewsActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 100;
    
    private static final String API_KEY = "AIzaSyCrjHG2nGoMKcuFDJ1LCS_UqfuZ85ZcZGs";
    private static final String CX = "65213fa93bc284436";

    EditText edtNewsX;
    Button btnRecognition, btnSendGoogle;
    TextView txtGoogleResult;
    android.os.Handler uiHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainNews), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtNewsX = findViewById(R.id.edtNewsX);
        btnRecognition = findViewById(R.id.btnRecognition);
        btnSendGoogle = findViewById(R.id.btnSendGoogle);
        txtGoogleResult = findViewById(R.id.txtGoogleResult);
    }

    private void addEvents() {
        // Sự kiện Recognition: Chuyển giọng nói thành văn bản
        btnRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        // Sự kiện Gửi Google
        btnSendGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToGoogle();
            }
        });
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.str_news_speech_prompt));

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.str_news_no_speech), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                // Đưa text nhận diện được vào khung tin tức X
                edtNewsX.setText(result.get(0));
            }
        }
    }

    private void sendToGoogle() {
        String query = edtNewsX.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, getString(R.string.str_news_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        txtGoogleResult.setText(getString(R.string.str_news_sending, query));
        
        new Thread(() -> {
            try {
                String result = searchGoogle(query);
                uiHandler.post(() -> txtGoogleResult.setText(result));
            } catch (Exception e) {
                uiHandler.post(() -> txtGoogleResult.setText("Lỗi kết nối hoặc API: " + e.getMessage()));
            }
        }).start();
    }

    private String searchGoogle(String query) throws Exception {
        String urlString = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY +
                "&cx=" + CX + "&q=" + java.net.URLEncoder.encode(query, "UTF-8");
        
        java.net.URL url = new java.net.URL(urlString);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            // Đọc chi tiết lỗi từ ErrorStream
            java.io.InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(errorStream));
                StringBuilder errRes = new StringBuilder();
                String l;
                while ((l = br.readLine()) != null) errRes.append(l);
                br.close();
                // Phân tích lỗi để lấy message ngắn gọn
                try {
                    org.json.JSONObject errJson = new org.json.JSONObject(errRes.toString());
                    return "Lỗi Google: " + errJson.getJSONObject("error").getString("message");
                } catch (Exception e) {
                    return "Lỗi " + responseCode + ": " + errRes.toString();
                }
            }
            return "Lỗi từ server Google: " + responseCode + " " + conn.getResponseMessage();
        }

        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return parseGoogleResults(response.toString(), query);
    }

    private String parseGoogleResults(String jsonResponse, String query) throws Exception {
        org.json.JSONObject root = new org.json.JSONObject(jsonResponse);
        org.json.JSONArray items = root.optJSONArray("items");

        if (items == null || items.length() == 0) {
            return "Không tìm thấy kết quả nào cho: " + query;
        }

        StringBuilder resultText = new StringBuilder();
        resultText.append(getString(R.string.str_news_result_header)).append("\n\n");

        for (int i = 0; i < items.length(); i++) {
            org.json.JSONObject item = items.getJSONObject(i);
            String title = item.optString("title");
            String link = item.optString("link");
            String snippet = item.optString("snippet");

            resultText.append(i + 1).append(". ").append(title).append("\n");
            resultText.append("Link: ").append(link).append("\n");
            resultText.append("Mô tả: ").append(snippet).append("\n\n");
            
            if (i >= 4) break; // Lấy 5 kết quả đầu tiên
        }

        return resultText.toString();
    }
}
