package com.lethimyhoa.k2341111e_mobile;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VnExpressRestActivity extends AppCompatActivity {

    private static final String TAG = "VnExpressRestActivity";
    private static final String API_URL = "https://gw.vnexpress.net/cr/?name=tygia_vangv202206";

    TextView txtStatus;
    ListView lvRestData;
    ArrayList<String> restLines;
    ArrayAdapter<String> adapter;
    Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vn_express_rest);
        addViews();
        addEvents();
        loadRestData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        txtStatus = findViewById(R.id.txtStatus);
        lvRestData = findViewById(R.id.lvRestData);
        restLines = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restLines);
        lvRestData.setAdapter(adapter);
        uiHandler = new Handler(Looper.getMainLooper());
    }

    private void addEvents() {
        lvRestData.setOnItemClickListener((parent, view, position, id) -> {
            String item = adapter.getItem(position);
            if (item != null) {
                txtStatus.setText(item);
            }
        });
    }

    private void loadRestData() {
        txtStatus.setText(getString(R.string.str_rest_loading));
        if (!hasInternetConnection()) {
            showFallback(
                    getString(R.string.str_rest_no_network),
                    new String[]{
                            API_URL,
                            getString(R.string.str_rest_no_network),
                            getString(R.string.str_rest_tip)
                    }
            );
            return;
        }

        new Thread(() -> {
            try {
                String response = downloadJson();
                ArrayList<String> lines = parseChartLines(response);
                uiHandler.post(() -> {
                    showLines(getString(R.string.str_rest_loaded, lines.size()), lines);
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed to load VNExpress REST data", e);
                uiHandler.post(() -> showFallback(
                        getString(R.string.str_rest_error),
                        new String[]{
                                API_URL,
                                "Error: " + e.getMessage(),
                                "Tip: Check internet and User-Agent"
                        }
                ));
            }
        }).start();
    }

    private boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void showLines(String status, ArrayList<String> lines) {
        restLines.clear();
        restLines.addAll(lines);
        adapter.notifyDataSetChanged();
        txtStatus.setText(status);
    }

    private void showFallback(String status, String[] lines) {
        ArrayList<String> fallback = new ArrayList<>();
        for (String line : lines) fallback.add(line);
        showLines(status, fallback);
    }

    private String downloadJson() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("GET");
        // BẮT BUỘC: Thêm User-Agent để Server VnExpress không chặn request
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Server trả về mã lỗi: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        connection.disconnect();
        return builder.toString();
    }

    private ArrayList<String> parseChartLines(String jsonText) throws Exception {
        ArrayList<String> result = new ArrayList<>();
        JSONObject root = new JSONObject(jsonText);
        
        JSONObject dataRoot = root.optJSONObject("data");
        if (dataRoot == null) {
            result.add("Lỗi: Không tìm thấy 'data'");
            return result;
        }

        // Lấy key động đầu tiên (ví dụ: tygia_vangv202206)
        java.util.Iterator<String> keys = dataRoot.keys();
        if (!keys.hasNext()) {
            result.add("Lỗi: Key dữ liệu rỗng");
            return result;
        }
        
        String dynamicKey = keys.next();
        JSONObject payload = dataRoot.optJSONObject(dynamicKey);
        
        // VnExpress hay bọc thêm 1 lớp data nữa
        if (payload != null && payload.has("data")) {
            payload = payload.optJSONObject("data");
        }

        if (payload == null) {
            result.add("Lỗi: Không có dữ liệu cho " + dynamicKey);
            return result;
        }

        JSONObject chart = payload.optJSONObject("chart");
        if (chart == null) {
            result.add("Lỗi: Không tìm thấy 'chart'");
            return result;
        }

        // Ưu tiên Hà Nội PNJ, nếu không có lấy key đầu tiên trong chart
        JSONArray dataArray = chart.optJSONArray("ha_noi_pnj");
        if (dataArray == null) {
            java.util.Iterator<String> chartKeys = chart.keys();
            if (chartKeys.hasNext()) {
                dataArray = chart.optJSONArray(chartKeys.next());
            }
        }

        if (dataArray == null || dataArray.length() == 0) {
            result.add("Thông báo: Hiện không có dữ liệu chi tiết.");
            return result;
        }

        result.add("--- THÔNG TIN GIÁ VÀNG ---");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            String date = item.optString("date_label", "N/A");
            String buy = item.optString("buy", "0");
            String sell = item.optString("sell", "0");
            result.add(date + " | Mua: " + buy + " | Bán: " + sell);
        }
        return result;
    }
}
