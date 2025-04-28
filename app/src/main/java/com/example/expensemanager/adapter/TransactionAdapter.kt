package com.example.expensemanager.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.R
import com.example.expensemanager.databinding.TransactionRowBinding
import com.example.expensemanager.model.Category
import com.example.expensemanager.mvvm.MyViewModel
import com.example.expensemanager.roomDatabase.MyData
import com.example.expensemanager.utils.ExtensionFun
import com.google.firebase.database.FirebaseDatabase


class TransactionAdapter(
    private val context: Context, private val transactionList: ArrayList<MyData>,
    private var viewModel: MyViewModel,
    private val transactionOnClick: (MyData) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TransactionRowBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.binding.transactionAmount.text = transaction.transactionAmount.toString()
        holder.binding.tranactionLabel.text = transaction.transactionAccount
        holder.binding.tranactionLabel.backgroundTintList =
            context.getColorStateList(ExtensionFun.getAccountColor(transaction.transactionAccount))
        holder.binding.transactionDate.text =
            ExtensionFun.dateFormat().format(transaction.transactionDate)
        holder.binding.transactionType.text = transaction.transactionCategory

        val transactionCategory: Category? =
            transaction.transactionCategory?.let { ExtensionFun.categoryDetails(it) }
        holder.binding.transactionImage.setImageResource(transactionCategory!!.categoryImage)
        holder.binding.transactionImage.backgroundTintList =
            context.getColorStateList(transactionCategory.categoryColor)

        if (transaction.transactionType == ExtensionFun.income) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.colorPrimary))
        } else if (transaction.transactionType == (ExtensionFun.expenses)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.redColor))
        }

        transaction.let {
            holder.bind(it)
        }

        holder.binding.deleteImage.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                    val sharedPreferences =
                        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("lastTransactionId")
                    editor.apply()
                    //firebase
                    val firebaseReference = FirebaseDatabase.getInstance().reference.child("transactions").child(transaction.id.toString())
                    firebaseReference.removeValue().addOnSuccessListener {
                        Log.d("Firebase", "Data deleted successfully from Firebase")
                    }.addOnFailureListener { exception ->
                        Log.e("Firebase", "Error deleting data from Firebase: ${exception.message}")
                    }

//                    val key = transactionList[position].id
//                    val dbRf = Firebase.database.getReference("transactions").child(key.toString())
//                    dbRf.removeValue().addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            notifyItemRemoved(position)
//                            notifyDataSetChanged()
//                            Toast.makeText(context, "Remove Data", Toast.LENGTH_SHORT).show()
//                        }
//                    }.addOnFailureListener {
//                        Toast.makeText(context, "Field Firebase Delete Data", Toast.LENGTH_SHORT)
//                            .show()
//
//                    }
                    // Delete from Room database
                    viewModel.deleteTransactionById(transaction.id)
                    transactionList.removeAt(position)
                    notifyItemRemoved(position)

                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    inner class ViewHolder(val binding: TransactionRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(click: MyData) {
            binding.updateImage.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Confirm Update")
                    .setMessage("Are you sure you want to update this transaction?")
                    .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                        transactionOnClick(click)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }
                    .show()

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<MyData>) {
        transactionList.clear()
        transactionList.addAll(data)
        notifyDataSetChanged()
    }
}
