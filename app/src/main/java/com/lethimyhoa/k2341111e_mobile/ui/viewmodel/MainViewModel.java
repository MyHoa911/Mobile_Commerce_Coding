package com.lethimyhoa.k2341111e_mobile.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.FirebaseDatabase;
import com.lethimyhoa.k2341111e_mobile.data.local.database.AppDatabase;
import com.lethimyhoa.k2341111e_mobile.data.local.entity.UserEntity;
import com.lethimyhoa.k2341111e_mobile.data.repository.UserRepository;
import com.lethimyhoa.k2341111e_mobile.utils.NetworkMonitor;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<UserEntity>> allUsers;
    private final NetworkMonitor networkMonitor;
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Integer> lastScrollPos = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        repository = new UserRepository(db.userDao(), FirebaseDatabase.getInstance().getReference());
        allUsers = repository.getAllUsers();
        networkMonitor = new NetworkMonitor(application);
        sharedPreferences = application.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        
        repository.startSync();
        loadScrollPosition();
    }

    public LiveData<List<UserEntity>> getAllUsers() {
        return allUsers;
    }

    public LiveData<NetworkMonitor.NetworkStatus> getNetworkStatus() {
        return networkMonitor.getNetworkStatus();
    }

    public LiveData<Integer> getLastScrollPos() {
        return lastScrollPos;
    }

    public void saveScrollPosition(int position) {
        sharedPreferences.edit().putInt("last_scroll_pos", position).apply();
        lastScrollPos.setValue(position);
    }

    public void saveLastScreen(String className) {
        sharedPreferences.edit().putString("last_screen", className).apply();
    }

    public String getLastScreen() {
        return sharedPreferences.getString("last_screen", "");
    }

    public void saveLastViewedContent(String content) {
        sharedPreferences.edit().putString("last_content", content).apply();
    }

    public String getLastViewedContent() {
        return sharedPreferences.getString("last_content", "");
    }

    public void saveLastSelectedUser(String userId) {
        sharedPreferences.edit().putString("last_user_id", userId).apply();
    }

    public String getLastSelectedUserId() {
        return sharedPreferences.getString("last_user_id", "");
    }

    public void deleteUser(String userId) {
        repository.deleteUser(userId);
    }

    public void updateUser(UserEntity user) {
        repository.updateUser(user);
    }

    public void syncData() {
        repository.startSync();
    }

    private void loadScrollPosition() {
        int pos = sharedPreferences.getInt("last_scroll_pos", 0);
        lastScrollPos.setValue(pos);
    }

    public void createFakeUser() {
        String id = String.valueOf(System.currentTimeMillis());
        UserEntity newUser = new UserEntity(id, "User " + id, "user" + id + "@gmail.com", "0901234567");
        repository.addUser(newUser);
    }
}
