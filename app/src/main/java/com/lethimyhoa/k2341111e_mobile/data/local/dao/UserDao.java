package com.lethimyhoa.k2341111e_mobile.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.lethimyhoa.k2341111e_mobile.data.local.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserEntity> users);

    @Query("DELETE FROM users")
    void deleteAll();

    @Transaction
    default void clearAndInsert(List<UserEntity> users) {
        deleteAll();
        insertAll(users);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);
}
