package com.example.expensemanager.roomDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: MyData)

    @Query("SELECT * FROM myData WHERE transactionDate >= :date ORDER BY id  DESC")
    fun getDailyData(date: Long): LiveData<List<MyData>>

    @Query("SELECT * FROM myData WHERE transactionDate >= :startDate AND transactionDate <= :endDate ORDER BY id  DESC")
    fun getWeeklyData(startDate: Long, endDate: Long): LiveData<List<MyData>>

    @Query("SELECT * FROM myData ORDER BY id  DESC")
    fun getAllData(): LiveData<List<MyData>>

    @Update
    suspend fun updateData(data: MyData)

    @Query("DELETE FROM myData WHERE id = :id")
    suspend fun deleteData(id: Int)
}
