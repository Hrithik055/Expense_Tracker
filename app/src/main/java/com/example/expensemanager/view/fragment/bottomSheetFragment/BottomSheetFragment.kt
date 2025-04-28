package com.example.expensemanager.view.fragment.bottomSheetFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.R
import com.example.expensemanager.adapter.AccountAdapter
import com.example.expensemanager.adapter.AccountClickListener
import com.example.expensemanager.adapter.CategoryAdapter
import com.example.expensemanager.adapter.CategoryClickListener
import com.example.expensemanager.databinding.FragmentBottomSheetBinding
import com.example.expensemanager.databinding.ListDialogBinding
import com.example.expensemanager.model.Account
import com.example.expensemanager.model.Category
import com.example.expensemanager.model.Transaction
import com.example.expensemanager.mvvm.MyViewModel
import com.example.expensemanager.roomDatabase.MyData
import com.example.expensemanager.utils.ExtensionFun
import com.example.expensemanager.utils.ExtensionFun.dateFormat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.Date

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding
    private var cal: Calendar = Calendar.getInstance()
    private lateinit var textInputEditText: TextInputEditText
    private lateinit var database: DatabaseReference
    private var isIncomeSelected = true
    private var isExpenseSelected = true
    private var lastTransactionId = 1
    private lateinit var viewModel: MyViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var transactionData: MyData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInputEditText = view.findViewById<TextInputLayout>(R.id.textInputEditText)
            ?.findViewById(R.id.selectdate)!!
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        binding!!.incomeBtn.setOnClickListener {
            setButtonProperties(
                binding!!.incomeBtn, R.drawable.income_selector, R.color.colorPrimary
            )
            setButtonProperties(
                binding!!.expenseBtn, R.drawable.default_selector, R.color.textColor
            )
            isIncomeSelected = true
            isExpenseSelected = false
        }

        binding!!.expenseBtn.setOnClickListener {
            setButtonProperties(binding!!.expenseBtn, R.drawable.expense_selector, R.color.redColor)
            setButtonProperties(binding!!.incomeBtn, R.drawable.default_selector, R.color.textColor)
            isExpenseSelected = true
            isIncomeSelected = false
        }

        binding!!.selectdate.setOnClickListener {
            showDatePickerDialog()
        }

        binding!!.categorybottm.setOnClickListener {
            showCategoryDialog()
        }

        binding!!.account.setOnClickListener {
            showAccountDialog()
        }

        //data received for fragment 1
        val jsonNote = arguments?.getString("transactionJson")
        if (jsonNote != null) {
            transactionData = Gson().fromJson(jsonNote, MyData::class.java)
            // Populate UI with existing data
            populateUIWithData(transactionData)
            binding?.saveTransaction?.text = "Update Tranaction"
        }

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        lastTransactionId = sharedPreferences.getInt("lastTransactionId",1)
        Log.d("lastIDGET", "lastId $lastTransactionId")
        database = Firebase.database.reference
        binding!!.saveTransaction.setOnClickListener {
            // Get user input data
            val edDate = binding!!.selectdate.text.toString().trim()
            val edAmount = binding!!.amount.text.toString().trim()
            val edCategory = binding!!.categorybottm.text.toString().trim()
            val edAccount = binding!!.account.text.toString().trim()
            val edNote = binding!!.note.text.toString().trim()
            val transactionType = if (isIncomeSelected) ExtensionFun.income else ExtensionFun.expenses
            if (transactionData != null) {
                // Update existing transaction
                updateTransaction(transactionData!!, transactionType , edDate, edAmount, edCategory, edAccount, edNote)
            } else {
                // Add new transaction
                addNewTransaction(edDate, edAmount, edCategory, edAccount, edNote)
            }
        }

    }
    private fun populateUIWithData(transactionData: MyData?) {
        // Populate UI elements with existing data
        binding?.amount?.setText(transactionData?.transactionAmount.toString())
        binding?.categorybottm?.setText(transactionData?.transactionCategory)
        binding?.account?.setText(transactionData?.transactionAccount)
        binding?.note?.setText(transactionData?.transactionNote)
        val formattedDate = dateFormat().format(transactionData?.transactionDate)
        binding?.selectdate?.setText(formattedDate)
        if (transactionData?.transactionType == ExtensionFun.income) {
            setButtonProperties(
                binding?.incomeBtn ?: return,
                R.drawable.income_selector,
                R.color.colorPrimary
            )
        } else if (transactionData?.transactionType == ExtensionFun.expenses) {
            setButtonProperties(
                binding?.expenseBtn ?: return,
                R.drawable.expense_selector,
                R.color.redColor
            )
        }
        binding?.saveTransaction?.text = "Update Transaction"
    }



    // Helper function to update an existing transaction
    private fun updateTransaction(
        transactionData: MyData, tType : String, edDate: String, edAmount: String,
        edCategory: String, edAccount: String, edNote: String
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("transactions")
        val transactionRef = firebaseDatabase.child(transactionData.id.toString())
        val updatedTransaction = Transaction(
            tType,
            edCategory,
            edAccount,
            edNote,
            transactionData.id,
            edAmount.toDouble(),
            Date(edDate)
        )
        transactionRef.setValue(updatedTransaction).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Data Updated", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
                // Update Room database
                val updatedEntity = MyData(
                    updatedTransaction.transactionId,
                    updatedTransaction.transactionType,
                    updatedTransaction.transactionCategory,
                    updatedTransaction.transactionAccount,
                    updatedTransaction.transactionNote,
                    updatedTransaction.transactionAmount,
                    updatedTransaction.transactionDate.time
                )
                viewModel.updateTransaction(updatedEntity)
                Toast.makeText(requireContext(), "Room data updated", Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to Update Data", Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }
    }

    // Helper function to add a new transaction
    private fun addNewTransaction(
        edDate: String, edAmount: String, edCategory: String, edAccount: String, edNote: String
    ) {
        // Add new transaction to Firebase Realtime Database
        val newTransactionId = ++ lastTransactionId
        // Update SharedPreferences with lastTransactionId
        val editor = sharedPreferences.edit()
        editor.putInt("lastTransactionId", newTransactionId)
        editor.apply()
        editor.commit()

        val transactionType = if (isIncomeSelected) ExtensionFun.income else ExtensionFun.expenses
        val newTransaction = Transaction(
            transactionType,
            edCategory,
            edAccount,
            edNote,
            newTransactionId,
            edAmount.toDouble(),
            Date(edDate)
        )

        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("transactions")
        val transactionRef = firebaseDatabase.child(newTransactionId.toString())
        transactionRef.setValue(newTransaction).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Data Saved", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()

                // Add new transaction to Room database
                val newEntity = MyData(
                    newTransactionId,
                    newTransaction.transactionType,
                    newTransaction.transactionCategory,
                    newTransaction.transactionAccount,
                    newTransaction.transactionNote,
                    newTransaction.transactionAmount,
                    newTransaction.transactionDate.time
                )
                viewModel.addNewTransaction(newEntity)
                Toast.makeText(requireContext(), "Room data saved", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to Save Data", Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }
    }

    private fun showAccountDialog() {
        val dialogBinding = ListDialogBinding.inflate(layoutInflater)
        val accountDialog = AlertDialog.Builder(requireContext()).create()
        accountDialog.setView(dialogBinding.root)
        val accountList = ArrayList<Account>()
        accountList.add(Account("Cash", 0))
        accountList.add(Account("UPI", 0))
        accountList.add(Account("Card", 0))
        accountList.add(Account("Net Banking", 0))
        accountList.add(Account("Wallets", 0))

        val accountAdapter =
            AccountAdapter(requireContext(), accountList, object : AccountClickListener {
                override fun onAccountSelected(account: Account) {
                    binding?.account?.setText(account.accountName)
                    accountDialog?.dismiss()
                }
            })
        dialogBinding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.recyclerview.adapter = accountAdapter
        accountDialog.show()
    }

    private fun showCategoryDialog() {
        val dialogBinding = ListDialogBinding.inflate(layoutInflater)
        val categoryDialog = AlertDialog.Builder(requireContext()).create()
        categoryDialog.setView(dialogBinding.root)
        val category = ArrayList<Category>()
        category.add(Category("Salary", R.drawable.salary, R.color.category1))
        category.add(Category("Business", R.drawable.business, R.color.category2))
        category.add(Category("Loan", R.drawable.loan, R.color.category3))
        category.add(Category("Investment", R.drawable.investment, R.color.category4))
        category.add(Category("Rent", R.drawable.rent, R.color.category5))
        category.add(Category("Other", R.drawable.other, R.color.category6))
        val categoryAdapter =
            CategoryAdapter(requireContext(), category, object : CategoryClickListener {
                override fun onCategorySelected(categoryName: Category) {
                    binding?.categorybottm?.setText(categoryName.categoryName)
                    categoryDialog?.dismiss()
                }
            })
        dialogBinding.recyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        dialogBinding.recyclerview.adapter = categoryAdapter
        categoryDialog.show()
    }

    private fun showDatePickerDialog() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        DatePickerDialog(
            requireActivity(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        textInputEditText.setText(dateFormat().format(cal.time))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setButtonProperties(button: Button, backgroundDrawable: Int, textColor: Int) {
        button.apply {
            background = requireContext().getDrawable(backgroundDrawable)
            setTextColor(requireContext().getColor(textColor))
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}