package com.lethimyhoa.k2341111e_mobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lethimyhoa.k2341111e_mobile.data.local.entity.UserEntity;
import com.lethimyhoa.k2341111e_mobile.ui.adapter.UserAdapter;
import com.lethimyhoa.k2341111e_mobile.ui.viewmodel.MainViewModel;
import com.lethimyhoa.k2341111e_mobile.utils.NetworkMonitor;
import com.lethimyhoa.models.UserAccount;

import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private UserAdapter adapter;
    private RecyclerView rvUsers;
    private View statusIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo ViewModel sớm để kiểm tra Last Screen
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 1. Khôi phục màn hình cuối cùng nếu là khởi động mới
        if (savedInstanceState == null) {
            String lastScreen = viewModel.getLastScreen();
            if (lastScreen != null && !lastScreen.isEmpty() && !lastScreen.equals(getClass().getName())) {
                try {
                    Intent intent = new Intent(this, Class.forName(lastScreen));
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo Views
        initViews();
        
        // Cài đặt RecyclerView
        setupRecyclerView();

        // Quan sát dữ liệu (MVVM)
        observeViewModel();

        // Giữ logic cũ từ Intent
        handleIntentData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        rvUsers = findViewById(R.id.rvUsers);
        statusIndicator = findViewById(R.id.statusIndicator);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        findViewById(R.id.btnAddUser).setOnClickListener(v -> {
            viewModel.createFakeUser(); // Thêm user ảo lên Firebase
            Toast.makeText(this, "Đang gửi User lên Firebase...", Toast.LENGTH_SHORT).show();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.syncData();
            // Tự động tắt sau 2 giây hoặc khi có dữ liệu mới
            rvUsers.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter();
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        adapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(UserEntity user) {
                viewModel.saveLastSelectedUser(user.getId());
                showEditDialog(user);
            }

            @Override
            public void onUserLongClick(UserEntity user) {
                showDeleteConfirmDialog(user);
            }
        });
    }

    private void showEditDialog(UserEntity user) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);

        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    user.setName(etName.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    user.setPhone(etPhone.getText().toString());
                    viewModel.updateUser(user);
                    Toast.makeText(MainActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmDialog(UserEntity user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteUser(user.getId());
                    Toast.makeText(MainActivity.this, "Deleting...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        // 1. Tự động cập nhật danh sách (Lấy từ Room Cache trước, sau đó đồng bộ Firebase)
        viewModel.getAllUsers().observe(this, users -> {
            adapter.submitList(users);
            if (users == null || users.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        // 2. Tự động nhận diện trạng thái Mạng
        viewModel.getNetworkStatus().observe(this, status -> {
            if (status == NetworkMonitor.NetworkStatus.AVAILABLE) {
                statusIndicator.setBackgroundColor(Color.GREEN);
            } else {
                statusIndicator.setBackgroundColor(Color.RED);
            }
        });

        // 3. Khôi phục vị trí cuộn khi mở lại App
        viewModel.getLastScrollPos().observe(this, pos -> {
            if (pos != null && pos > 0) {
                rvUsers.postDelayed(() -> rvUsers.scrollToPosition(pos), 200);
            }
        });
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        UserAccount ac = (UserAccount) intent.getSerializableExtra("LOGIN_USER");
        TextView txtDisplayName = findViewById(R.id.txtDisplayName);
        String displayName = (ac != null && ac.getDisplayName() != null) ? ac.getDisplayName() : "Guest";
        txtDisplayName.setText(getString(R.string.str_hey_display_name, displayName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lưu màn hình hiện tại là màn hình cuối cùng
        viewModel.saveLastScreen(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lưu vị trí cuộn hiện tại vào SharedPreferences
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvUsers.getLayoutManager();
        if (layoutManager != null) {
            int position = layoutManager.findFirstVisibleItemPosition();
            viewModel.saveScrollPosition(position);
        }
    }

    // --- Các phương thức chuyển màn hình cũ của bạn ---
    public void openCalculatorApp(View view) {
        viewModel.saveLastScreen(CalculatorActivity.class.getName());
        startActivity(new Intent(this, CalculatorActivity.class));
    }

    public void openCategoryActivity(View view) {
        viewModel.saveLastScreen(CategoryActivity.class.getName());
        startActivity(new Intent(this, CategoryActivity.class));
    }

    public void openProductActivity(View view) {
        viewModel.saveLastScreen(ProductActivity.class.getName());
        startActivity(new Intent(this, ProductActivity.class));
    }

    public void openNewsActivity(View view) {
        viewModel.saveLastScreen(NewsActivity.class.getName());
        startActivity(new Intent(this, NewsActivity.class));
    }

    public void openSmsSpyware(View view) {
        viewModel.saveLastScreen(SMSSpywareActivity.class.getName());
        startActivity(new Intent(this, SMSSpywareActivity.class));
    }

    public void openMultiThreadingObject(View view) {
        viewModel.saveLastScreen(MultiThreadingObjectActivity.class.getName());
        startActivity(new Intent(this, MultiThreadingObjectActivity.class));
    }

    public void openVnExpressRest(View view) {
        viewModel.saveLastScreen(VnExpressRestActivity.class.getName());
        startActivity(new Intent(this, VnExpressRestActivity.class));
    }

    public void openFontAndMusic(View view) {
        viewModel.saveLastScreen(FontAndMusicActivity.class.getName());
        startActivity(new Intent(this, FontAndMusicActivity.class));
    }

    public void close_app(View view) {
        finish();
    }
}
