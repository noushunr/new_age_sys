package com.bizify.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView
import com.bizify.data.model.AodpList
import com.bizify.data.model.Customers
import com.bizify.databinding.ItemCustomerBinding
import com.bizify.databinding.ItemPostBinding
import com.bizify.interfaces.CustClick
import com.bizify.interfaces.PostClick

/**
 * Created by Noushad N on 05-06-2022.
 */
class CustomerAdapter(
    private var mContext: Context,
    private var items: MutableList<Customers>,
    private val postClick: CustClick
) :
    RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    fun submitList(items: List<Customers>?){
        this.items?.clear()
        this.items?.addAll(items!!)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = items!![position]
        holder.binding.title.text = item.csName
        if (item.email.isNullOrEmpty() || item.email.isNullOrBlank()){
            holder.binding.tvEmail.visibility = View.GONE
        }else{
            holder.binding.tvEmail.visibility = View.VISIBLE
            holder.binding.tvEmail.text = item.email
        }
        if (item.mobile.isNullOrEmpty() || item.mobile.isNullOrBlank()){
            holder.binding.tvMobile.visibility = View.GONE
        }else{
            holder.binding.tvMobile.visibility = View.VISIBLE
            holder.binding.tvMobile.text = item.mobile
        }

        holder.binding.llMain.setOnClickListener {
            postClick.onItemClick(item)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val binding : ItemCustomerBinding) : RecyclerView.ViewHolder(binding?.root!!) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCustomerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


    }

}
