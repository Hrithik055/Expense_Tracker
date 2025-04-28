package com.example.expensemanager.view.fragment.bottomSheetFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.adapter.TransactionAdapter
import com.example.expensemanager.databinding.FragmentMonthlyShowBinding
import com.example.expensemanager.mvvm.MyViewModel
import com.example.expensemanager.roomDatabase.MyData
import com.example.expensemanager.utils.ExtensionFun
import com.google.gson.Gson


class MonthlyShowFragment : Fragment() {

    private var _binding: FragmentMonthlyShowBinding? = null
    private val binding get() = _binding!!


    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var viewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        viewModel.allTextData.observe(viewLifecycleOwner, Observer { transaction ->
            transactionAdapter.updateData(transaction)
        })

        transactionAdapter = TransactionAdapter(
            requireContext(),
            ArrayList(),
            viewModel,
            ::trancationOnClick
        )
        binding.transactionRecyclerView.adapter = transactionAdapter
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    private fun trancationOnClick(response : MyData){
        val bundle = Bundle()
        bundle.putString("transactionJson", Gson().toJson(response))
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        Toast.makeText(requireContext(), "data pass adapter", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}