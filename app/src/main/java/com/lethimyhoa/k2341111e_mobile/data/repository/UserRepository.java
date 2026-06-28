package com.lethimyhoa.k2341111e_mobile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lethimyhoa.k2341111e_mobile.data.local.dao.UserDao;
import com.lethimyhoa.k2341111e_mobile.data.local.database.AppDatabase;
import com.lethimyhoa.k2341111e_mobile.data.local.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final UserDao userDao;
    private final DatabaseReference firebaseDb;
    private final LiveData<List<UserEntity>> allUsers;

    public UserRepository(UserDao userDao, DatabaseReference firebaseDb) {
        this.userDao = userDao;
        this.firebaseDb = firebaseDb;
        this.allUsers = userDao.getAllUsers();
    }

    public LiveData<List<UserEntity>> getAllUsers() {
        return allUsers;
    }

    public void startSync() {
        firebaseDb.child("contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserEntity> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    try {
                        // Đọc dữ liệu linh hoạt, chấp nhận cả ID là số hoặc chuỗi
                        String id = String.valueOf(userSnapshot.child("id").getValue());
                        String name = userSnapshot.child("name").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String phone = userSnapshot.child("phone").getValue(String.class);

                        if (name != null) {
                            UserEntity user = new UserEntity(id, name, email, phone);
                            userList.add(user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    userDao.clearAndInsert(userList);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error
            }
        });
    }

    public void addUser(UserEntity user) {
        firebaseDb.child("contacts").child(user.getId()).setValue(user);
    }

    public void updateUser(UserEntity user) {
        firebaseDb.child("contacts").child(user.getId()).setValue(user);
    }

    public void deleteUser(String userId) {
        firebaseDb.child("contacts").child(userId).removeValue();
    }
}
