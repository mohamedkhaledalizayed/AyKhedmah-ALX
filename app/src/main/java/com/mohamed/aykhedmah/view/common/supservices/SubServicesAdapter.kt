package com.mohamed.aykhedmah.view.common.supservices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.subservices.SubData
import com.mohamed.aykhedmah.databinding.BannerAdRowBinding
import com.mohamed.aykhedmah.databinding.ProviderItemBinding

import com.squareup.picasso.Picasso


class SubServicesAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_GENERAL = 0
    private val ITEM_TYPE_BANNER_AD = 1
    var onItemClickListener: OnItemClickListener? = null
    private var mList: ArrayList<Any> = ArrayList()
    private var layoutInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }

        return when (viewType) {
            ITEM_TYPE_BANNER_AD -> {
                val binding: BannerAdRowBinding = DataBindingUtil.inflate(layoutInflater!!, R.layout.banner_ad_row, parent, false)
                MyAdViewHolder(binding)
            }
            else -> {
                val binding: ProviderItemBinding = DataBindingUtil.inflate(layoutInflater!!, R.layout.provider_item, parent, false)
                MyViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM_TYPE_BANNER_AD -> if (mList[position] is AdView) {
                val bannerHolder = holder as MyAdViewHolder
                val adView = mList[position] as AdView
                val adCardView = bannerHolder.itemView as ViewGroup
                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView)
            }
            ITEM_TYPE_GENERAL -> if (mList[position] is SubData) {
                val mHolder = holder as MyViewHolder
                val data: SubData = mList[position] as SubData

                mHolder.binding.serviceName.text = data.title
                mHolder.binding.numberProviders.text = "يوجد لدينا ${data.sub_service_provider_count} مزود خدمة فى ${data.title} يمكنهم خدمتك على اعلى جودة"
                Picasso.get().load(Constants.STORAGE + data.image).placeholder(R.drawable.ic_logo__1_).into(mHolder.binding.image)
                holder.itemView.setOnClickListener {
                    onItemClickListener?.setOnItemClickListener(data.id, data.title)
                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position] is SubData) {
            ITEM_TYPE_GENERAL
        } else {
            ITEM_TYPE_BANNER_AD
        }
    }

    override fun getItemCount() = mList.size

    class MyViewHolder(val binding: ProviderItemBinding) : RecyclerView.ViewHolder(binding.root)

    class MyAdViewHolder(val binding: BannerAdRowBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(list: List<Any>?) {
        list?.let {
            mList.addAll(it)
            notifyDataSetChanged()
        }
    }

    @Suppress("unused")
    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun setOnItemClickListener(id: Int, title: String)
    }
}