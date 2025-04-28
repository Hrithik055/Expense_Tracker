package com.example.expensemanager.utils

import com.example.expensemanager.R
import com.example.expensemanager.model.Category
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

object ExtensionFun {

    const val income = "INCOME"
    const val expenses = "EXPENSE"

    fun dateFormat() : SimpleDateFormat{
        val myFormat = "dd MMMM, yyyy"
        return  SimpleDateFormat(myFormat, Locale.US)
    }

    private val currencyFormat = DecimalFormat("#,###.##")

    fun format(amount: Double): String {
        return currencyFormat.format(amount)
    }

    private lateinit var  category : ArrayList<Category>
    fun addCategory(){
        category = ArrayList()
        category.add(Category("Salary", R.drawable.salary, R.color.category1))
        category.add(Category("Business", R.drawable.business, R.color.category2))
        category.add(Category("Loan", R.drawable.loan, R.color.category3))
        category.add(Category("Investment", R.drawable.investment, R.color.category4))
        category.add(Category("Rent", R.drawable.rent, R.color.category5))
        category.add(Category("Other", R.drawable.other, R.color.category6))
    }

    fun categoryDetails(categoryName : String): Category? {
        for (i in category){
            if (i.categoryName == categoryName){
                return i
            }
        }
        return null
    }

    fun getAccountColor(accountName: String?) : Int{
        return when(accountName){
            "Cash" ->{
                R.color.cashColor
            }
            "UPI" -> {
                R.color.upiColor
            }
            "Card" ->{
                R.color.cardColor
            }
            "Net Banking" ->{
                R.color.netBankingColor
            }
            "Wallets" ->{
                R.color.walletsColor
            }

            else -> {
                R.color.black
            }
        }

    }
}
