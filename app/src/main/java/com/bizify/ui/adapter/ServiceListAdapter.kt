package com.bizify.ui.adapter;

import android.content.Context;
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView
import com.bizify.data.model.AodpList
import com.bizify.data.model.CreateJobResponse
import com.bizify.data.model.Services
import com.bizify.databinding.ItemPostBinding
import com.bizify.databinding.ItemServicesBinding
import com.bizify.interfaces.PostClick
import com.bizify.interfaces.ServiceItem

/**
 * Created by Noushad N on 05-06-2022.
 */
class ServiceListAdapter(
    private var mContext: Context,
    private var items: MutableList<Services>,
    private val serviceItem: ServiceItem
) :
    RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    fun submitList(items: List<Services>?){
        this.items?.clear()
        this.items?.addAll(items!!)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = items!![position]
        holder.binding.etService.text = item.itemName
        holder.binding.etUnitPrice.setText(item.unitPrice)
        holder.binding.etQuantity.setText("${item.quantity}")
        holder.binding.ivClose.setOnClickListener {
            serviceItem.onItemRemove(holder.adapterPosition)
        }
        holder.binding.etQuantity.setOnClickListener {
            var quantity = holder.binding.etQuantity.text.toString()
            serviceItem.onItemAdd(holder.adapterPosition,quantity,true,item.unitName!!)
        }
        holder.binding.etUnitPrice.setOnClickListener {
            var unitPrice = holder.binding.etUnitPrice.text.toString()
            serviceItem.onItemAdd(holder.adapterPosition,unitPrice,false,item.unitName!!)
        }
//        holder.binding.tvDate.text = formatDate(item.date!!)
//        Glide.with(mContext)
//            .load(item.url)
//            .thumbnail(Glide.with(mContext).load(R.mipmap.ic_launcher))
//            .into(holder.binding.ivUser)
//        holder?.binding?.root?.setOnClickListener{
//            postClick.onItemClick(item)
//        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val binding : ItemServicesBinding) : RecyclerView.ViewHolder(binding?.root!!) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemServicesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


    }

}
