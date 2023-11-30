package com.bizify.ui.adapter;

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    var DURATION: Long = 100
    private var on_attach = true
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
        holder.binding.tvJobNo.text = "Voucher No: ${item.voucherNo}"
        holder.binding.tvMobile.text = "Mobile No: ${item.mobile}"
        holder.binding.tvPlate.text = "Plate No: ${item.registartion}"
        holder.binding.llMain.setOnClickListener { postClick.onItemClick(item) }
        holder.binding.ivShare.setOnClickListener {
            postClick.onItemShare(item)
        }
//        setAnimation(holder.itemView, position);
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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                on_attach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
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

    private fun setAnimation(itemView: View, i: Int) {
        var i = i
        if (!on_attach) {
            i = -1
        }
        val not_first_item = i == -1
        i = i + 1
        itemView.translationX = -200f
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", -200f, 0f)
        val animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animatorTranslateY.setStartDelay(if (not_first_item) DURATION else i * DURATION)
        animatorTranslateY.duration = (if (not_first_item) 2 else 1) * DURATION
        animatorSet.playTogether(animatorTranslateY, animatorAlpha)
        animatorSet.start()
    }

}
