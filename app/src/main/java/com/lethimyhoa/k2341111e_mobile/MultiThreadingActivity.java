package com.lethimyhoa.k2341111e_mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MultiThreadingActivity extends AppCompatActivity {
    EditText edtButton;
    TextView txtPercent;
    ProgressBar progressBarPercent;
    LinearLayout linearLayoutButton;
    Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_threading);
        addViews();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtButton=findViewById(R.id.edtButton);
        txtPercent=findViewById(R.id.txtPercent);
        progressBarPercent=findViewById(R.id.progressBarPercent);
        linearLayoutButton=findViewById(R.id.linearLayoutButton);
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            // tiểu trình sẽ gửi thông điệp ve cho Main Thread thông qua message
            int percent=message.arg1;
            int value=message.arg2;
            txtPercent.setText(String.valueOf(percent) + "%");
            progressBarPercent.setProgress(percent);
            Button button=new Button(MultiThreadingActivity.this);
            button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            button.setText(String.valueOf(value));
            linearLayoutButton.addView(button);
            return false;
        }
    });

    public void processLongTimeTask(View view) {
        int n=Integer.parseInt(edtButton.getText().toString());
        //tạo tiểu trình để chạy long time task:
        Thread thread=new Thread(() -> {
                /*không được phép truy xuất tới bất kỳ biến View (control) nào trong đây
                * vì trong này là chạy Longtime task (dạng background)
                * Muốn truy xuất giao diện để cập nhật Visualization thì gửi thông điệp qua cho Main Thread làm*/
                for(int i=1;i<=n;i++)
                {
                    //tạo giá trị cho đối tượng
                    int value = random.nextInt(100);
                    //lấy địa chỉ của MainThread
                    Message message=handler.obtainMessage();
                    //gán giá trị mình tạo ra cho thông điệp đó
                    message.arg1=i*n/100; //percent
                    message.arg2=value; //value
                    //gửi thông điệp trở lại cho Mainthread để xử lý Visualization
                    handler.sendMessage(message);
                    try {
                        //cần phải sleep để tránh quá tải CPU
                        //cx như cho các tiểu trình kahsc xen vào lúc nghỉ để xử lý
                        Thread.sleep(100);
                        } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                }
        });
        thread.start();
    }
}