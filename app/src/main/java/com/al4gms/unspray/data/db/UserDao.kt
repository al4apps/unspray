package com.al4gms.unspray.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.al4gms.unspray.data.modelsdb.user.UserContract
import com.al4gms.unspray.data.modelsdb.user.UserDB

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUsers(users: List<UserDB>)

    @Query("DELETE FROM ${UserContract.TABLE_NAME}")
    suspend fun clear()

    @Transaction
    suspend fun refresh(users: List<UserDB>) {
        clear()
        saveUsers(users)
    }

    @Query("SELECT * FROM ${UserContract.TABLE_NAME} WHERE ${UserContract.Columns.ID} = :id")
    suspend fun getUser(id: String): UserDB
}
