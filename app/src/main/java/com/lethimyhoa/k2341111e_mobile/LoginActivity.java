package com.lethimyhoa.k2341111e_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    /*
    declare (khai báo) all view as variables
     * */
    EditText edtUserName;
    EditText edtPassword;
    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();
        edtUserName.setText("admin");
        edtPassword.setText("123");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
//        nạp bộ nhớ cho các view
        edtUserName=findViewById(R.id.edtUserName);
        edtPassword=findViewById(R.id.edtPassword);
        txtMessage=findViewById(R.id.txtMessage);
    }

    public void loginSystem(View view) {
        String username=edtUserName.getText().toString();
        String password=edtPassword.getText().toString();
        if(username.equalsIgnoreCase("admin") &&
            password.equals("123"))
        {
            txtMessage.setText(getString(R.string.str_login_success));

            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        else
        {
            txtMessage.setText(getString(R.string.str_login_failed));
        }
    }

    public void exitSystem(View view) {
        finish();
    }
}