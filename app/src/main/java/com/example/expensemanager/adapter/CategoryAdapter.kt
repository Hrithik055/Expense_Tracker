package com.example.expensemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.databinding.SimpleCategoryDialogBinding
import com.example.expensemanager.model.Category

interface CategoryClickListener {
  fun onCategorySelected(categoryName: Category)
}

class CategoryAdapter(
    private val context: Context,
    private val categoryList: ArrayList<Category>,
    private val categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SimpleCategoryDialogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SimpleCategoryDialogBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.categoryDialogText.text = category.categoryName
        holder.binding.categoryDialogImage.setImageResource(category.categoryImage)
        holder.binding.categoryDialogImage.backgroundTintList = ContextCompat.getColorStateList(context, category.categoryColor)

        holder.itemView.setOnClickListener {
            categoryClickListener.onCategorySelected(category)
        }
    }
}