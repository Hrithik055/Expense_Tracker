package com.example.expensemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.databinding.AccountCategoryBinding
import com.example.expensemanager.model.Account

interface AccountClickListener {
    fun onAccountSelected(account: Account)
}

class AccountAdapter(private val context: Context, private val accountList: ArrayList<Account>,
                     private val accountClickListener: AccountClickListener) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AccountCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding  = AccountCategoryBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val account = accountList[position]
        holder.binding.accounted.text = account.accountName
        holder.itemView.setOnClickListener {
            accountClickListener.onAccountSelected(account)
        }
    }

}