package com.example.newagesys.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newagesys.R
import com.example.newagesys.data.model.AodpList
import com.example.newagesys.databinding.ItemPostBinding
import com.example.newagesys.interfaces.PostClick
import com.example.newagesys.utils.formatDate
import java.text.SimpleDateFormat
import java.util.*
import java.util.List

/**
 * Created by Noushad N on 05-06-2022.
 */
class PostListAdapter(
    private var mContext: Context,
    private var items: MutableList<AodpList>,
    private val postClick: PostClick
) :
    RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    fun submitList(items: MutableList<AodpList>?){
        this.items?.clear()
        this.items?.addAll(items!!)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = items!![position]
        holder.binding.title.text = item.title
        holder.binding.tvDate.text = formatDate(item.date!!)

        Glide.with(mContext)
            .load(item.url)
            .thumbnail(Glide.with(mContext).load(R.mipmap.ic_launcher))
            .into(holder.binding.ivUser)
        holder?.binding?.root.setOnClickListener{
            postClick.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    class ViewHolder(val binding : ItemPostBinding) : RecyclerView.ViewHolder(binding?.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


    }

}
