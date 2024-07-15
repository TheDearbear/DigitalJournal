package com.thedearbear.nnov.account

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocalAccountDao {
    @Query("SELECT COUNT(*) FROM localaccount")
    fun countAll(): Int

    @Query("SELECT * FROM localaccount")
    fun getAll(): List<LocalAccount>

    @Query("SELECT * FROM localaccount where id = :id LIMIT 1")
    fun getById(id: Int): LocalAccount?

    @Query("SELECT * FROM localaccount WHERE expires != null")
    fun getWithExpiration(): List<LocalAccount>

    @Query("SELECT * FROM localaccount WHERE expires = null")
    fun getWithoutExpiration(): List<LocalAccount>

    @Update
    fun update(account: LocalAccount)

    @Insert
    fun insertAll(vararg accounts: LocalAccount)

    @Delete
    fun delete(account: LocalAccount)

    @Query("DELETE FROM localaccount WHERE id = :id")
    fun delete(id: Int)
}