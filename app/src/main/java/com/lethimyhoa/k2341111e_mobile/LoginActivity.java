package com.lethimyhoa.k2341111e_mobile;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lethimyhoa.models.ListUserAccount;
import com.lethimyhoa.models.UserAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "K234111ESales.sqlite";
    public static final String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    /*
    declare (khai báo) all view as variables
     * */
    EditText edtUserName;
    EditText edtPassword;
    TextView txtMessage;
    CheckBox chkSaveInfor;
    String shared_pref_key="LoginInfor";
    RadioButton radAdmin,radEmployee;
    Button btnLogin;

    BroadcastReceiver internetStateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        //bất kỳ khi nào internet/mobile dât change state
            //tự bay vào đây
            String action=intent.getAction();
            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {

            }
            Toast.makeText(LoginActivity.this,"Internet/mobile data changing state",
                    Toast.LENGTH_LONG).show();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                btnLogin.setEnabled(true);
            }
            else
            {
                btnLogin.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        addViews();
        checkAndRequestPermissions();
        copyDataBase();
        edtUserName.setText("admin");
        edtPassword.setText("123");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void copyDataBase() {
        try {
            File dbFile = getDatabasePath(DATABASE_NAME);
            if (!dbFile.exists()) {
                if (CopyDBFromAsset()) {
                    Log.d("DB_COPY", "Copy database successful!");
                } else {
                    Log.e("DB_COPY", "Copy database failed!");
                }
            }
        } catch (Exception e) {
            Log.e("DB_COPY", "Error: " + e.getMessage());
        }
    }

    private boolean CopyDBFromAsset() {
        try {
            InputStream inputStream = getAssets().open(DATABASE_NAME);
            File dbFile = getDatabasePath(DATABASE_NAME);
            File dbDir = dbFile.getParentFile();
            if (dbDir != null && !dbDir.exists()) {
                dbDir.mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            Log.e("DB_COPY", "IOException: " + e.getMessage());
            return false;
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 100);
        }
    }

    private void addViews() {
        // nạp bộ nhớ cho các view
        edtUserName=findViewById(R.id.edtUserName);
        edtPassword=findViewById(R.id.edtPassword);
        txtMessage=findViewById(R.id.txtMessage);
        chkSaveInfor=findViewById(R.id.chkSaveInfor);
        radAdmin=findViewById(R.id.radAdmin);
        radEmployee=findViewById(R.id.radEmployee);
        btnLogin=findViewById(R.id.btnLogin);
    }

    public void loginSystem(View view) {
        String username=edtUserName.getText().toString();
        String password=edtPassword.getText().toString();
        // or boolean saved = chkSaveInfor.isChecked();
        boolean saved=false;
        if(chkSaveInfor.isChecked())
            saved=true;
        //call login function
        UserAccount ac= ListUserAccount.login(username,password);
        if(ac!=null) //login sucess!
        {
            ac.setSaved(saved);

            //Save login infor (bất chấp user k lưu thì cx tự động lưu)
            SharedPreferences preferences=getSharedPreferences(shared_pref_key,MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("UserName",username);
            editor.putString("Password",password);
            editor.putBoolean("Saved",saved);
            editor.commit(); //lưu lại thao tác user trên giao diện

            if(ac.getRole().equalsIgnoreCase("admin"))
            {
                Intent intent=new Intent(LoginActivity.this,CategoryActivity.class);
                intent.putExtra("LOGIN_USER", ac);
                startActivity(intent);
            }
            else {
                Intent intent=new Intent(LoginActivity.this,EmployeeAdvancedManagementActivity.class);
                intent.putExtra("Account", ac);
                startActivity(intent);
            }
        }
        else //login failed
        {
            txtMessage.setText(getString(R.string.str_login_failed));
        }

    }

    public void loginSystemOld(View view) {
        String username=edtUserName.getText().toString();
        String password=edtPassword.getText().toString();
        // or boolean saved = chkSaveInfor.isChecked();
        boolean saved=false;
        if(chkSaveInfor.isChecked())
            saved=true;
        if(username.equalsIgnoreCase("admin") &&
            password.equals("123"))
        {
            txtMessage.setText(getString(R.string.str_login_success));

            //Save login infor (bất chấp user k lưu thì cx tự động lưu)
            SharedPreferences preferences=getSharedPreferences(shared_pref_key,MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("UserName",username);
            editor.putString("Password",password);
            editor.putBoolean("Saved",saved);
            editor.commit(); //lưu lại thao tác user trên giao diện

            // Tùy vào role mà hiện màn hình sau khi đăng nhập
            if (radAdmin.isChecked())
            {
                //dĩ nhiên phải check có quyền admin hay không?
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                UserAccount ac = new UserAccount();
                ac.setUsername(username);
                ac.setPassword(password);
                ac.setRole("admin");
                intent.putExtra("USER_LOGIN",ac);
                startActivity(intent);
            }
            else {
                Intent intent=new Intent(LoginActivity.this,EmployeeAdvancedManagementActivity.class);
                startActivity(intent);
            }


        }
        else
        {
            txtMessage.setText(getString(R.string.str_login_failed));
        }
    }

    public void exitSystem(View view) {
        //finish();
        //confirmation for exit:
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Confirm exit");
        builder.setMessage("Are you sure you want to exit?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        //handling user interaction (thiết lập hành vi người dùng)
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences=getSharedPreferences(shared_pref_key,MODE_PRIVATE);
        String username=preferences.getString("UserName","");
        String password=preferences.getString("Password","");
        boolean saved=preferences.getBoolean("Saved",false); //đọc k đc là false
        if(saved==true)
        {
            edtUserName.setText(username);
            edtPassword.setText(password);
        }
        chkSaveInfor.setChecked(saved);

        IntentFilter internetFilter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetStateReceiver,internetFilter);
    }
    //listen if internet has changed?
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetStateReceiver);
    }
}
