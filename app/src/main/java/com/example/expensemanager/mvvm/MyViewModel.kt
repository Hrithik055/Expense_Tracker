package com.example.expensemanager.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.roomDatabase.MyDao
import com.example.expensemanager.roomDatabase.MyDatabase
import com.example.expensemanager.roomDatabase.MyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: MyDao
    private val repository: Repository
    val allTextData: LiveData<List<MyData>>

    init {
        val database = MyDatabase.getDatabase(application)
        dao = database.myDao()
        repository = Repository(dao)
        allTextData = repository.getAll_TextData
    }

    fun getDailyData(): LiveData<List<MyData>> {
        return repository.getDailyData()
    }
    fun getWeeklyData(): LiveData<List<MyData>> {
        return repository.getWeeklyData()
    }

    fun addNewTransaction(data: MyData) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(data)
    }

    fun updateTransaction(data: MyData) = viewModelScope.launch(Dispatchers.IO){
        repository.updateDataRepository(data)
    }

    fun deleteTransactionById(id: Int) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }
}