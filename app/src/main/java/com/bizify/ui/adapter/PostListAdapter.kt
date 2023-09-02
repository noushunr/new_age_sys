package com.bizify.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView
import com.bizify.data.model.AodpList
import com.bizify.data.model.CreateJobResponse
import com.bizify.databinding.ItemPostBinding
import com.bizify.interfaces.PostClick

/**
 * Created by Noushad N on 05-06-2022.
 */
class PostListAdapter(
    private var mContext: Context,
    private var items: MutableList<CreateJobResponse>,
    private val postClick: PostClick
) :
    RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent)

    fun submitList(items: List<CreateJobResponse>?){
        this.items?.clear()
        this.items?.addAll(items!!)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item = items!![position]
        holder.binding.post = item
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

    class ViewHolder(val binding : ItemPostBinding) : RecyclerView.ViewHolder(binding?.root!!) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


    }

}
