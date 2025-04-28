package com.example.expensemanager.mvvm

import androidx.lifecycle.LiveData
import com.example.expensemanager.roomDatabase.MyDao
import com.example.expensemanager.roomDatabase.MyData
import java.util.Calendar

class Repository(private val myDataDao: MyDao) {

    val getAll_TextData: LiveData<List<MyData>> = myDataDao.getAllData()

    suspend fun insert(data: MyData) {
        myDataDao.insert(data)
    }

    suspend fun updateDataRepository(data: MyData){
        myDataDao.updateData(data)
    }
    suspend fun deleteTransactionById(id: Int) {
        myDataDao.deleteData(id)
    }

    fun getDailyData(): LiveData<List<MyData>> {
        val todayInMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return myDataDao.getDailyData(todayInMillis)
    }

    fun getWeeklyData(): LiveData<List<MyData>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -1) // Move back one week
        calendar.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - calendar.get(Calendar.DAY_OF_WEEK))
        val startDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, Calendar.SATURDAY - Calendar.SUNDAY)
        val endDate = calendar.timeInMillis
        return myDataDao.getWeeklyData(startDate, endDate)
    }


}