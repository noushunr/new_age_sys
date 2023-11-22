package com.bizify.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View
import android.view.ViewGroup;
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bizify.data.model.AodpList
import com.bizify.data.model.Customers
import com.bizify.data.model.Vehicles
import com.bizify.databinding.ItemCustomerBinding
import com.bizify.databinding.ItemPostBinding
import com.bizify.databinding.ItemVehicleBinding
import com.bizify.interfaces.CustClick
import com.bizify.interfaces.PostClick
import com.bizify.interfaces.VehicleClick

/**
 * Created by Noushad N on 05-06-2022.
 */
class VehicleAdapter(
    private var mContext: Context,
    private var items: MutableList<Vehicles>,
    private val postClick: VehicleClick
) :
    RecyclerView.Adapter<VehicleAdapter.ViewHolder>(), Filterable {
    var filterItems : MutableList<Vehicles> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    fun submitList(items: MutableList<Vehicles>?){
        this.items?.clear()
        this.items?.addAll(items!!)
        filterItems = items!!
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = filterItems!![position]
        holder.binding.title.text = item.veh_Name
        if (item.veh_Brand.isNullOrEmpty() || item.veh_Brand.isNullOrBlank()){
            holder.binding.tvBrand.visibility = View.GONE
        }else{
            holder.binding.tvBrand.visibility = View.VISIBLE
            holder.binding.tvBrand.text = item.veh_Brand
        }
        if (item.veh_Model.isNullOrEmpty() || item.veh_Model.isNullOrBlank()){
            holder.binding.tvModel.visibility = View.GONE
        }else{
            holder.binding.tvModel.visibility = View.VISIBLE
            holder.binding.tvModel.text = item.veh_Model
        }
        if (item.registartion.isNullOrEmpty() || item.registartion.isNullOrBlank()){
            holder.binding.tvRegistration.visibility = View.GONE
        }else{
            holder.binding.tvRegistration.visibility = View.VISIBLE
            holder.binding.tvRegistration.text = item.registartion
        }

        holder.binding.llMain.setOnClickListener {
            postClick.onItemClick(item)
        }

    }

    override fun getItemCount(): Int {
        return filterItems.size
    }

    class ViewHolder(val binding : ItemVehicleBinding) : RecyclerView.ViewHolder(binding?.root!!) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemVehicleBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) filterItems = items else {
                    val filteredList = ArrayList<Vehicles>()
                    items
                        .filter {
                            (it.registartion?.contains(constraint!!,ignoreCase = true)!!)

                        }
                        .forEach { filteredList.add(it) }
                    filterItems = filteredList

                }
                return FilterResults().apply { values = filterItems }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filterItems = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<Vehicles>
                notifyDataSetChanged()
            }
        }
    }

}
