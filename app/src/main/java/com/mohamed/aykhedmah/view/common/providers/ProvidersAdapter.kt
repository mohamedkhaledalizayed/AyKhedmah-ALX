package com.land.services.view.user.providerserviceslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants
import com.mohamed.aykhedmah.data.network.response.providers.Data
import com.mohamed.aykhedmah.databinding.BannerAdRowBinding
import com.mohamed.aykhedmah.databinding.ProviderLayoutBinding
import com.squareup.picasso.Picasso


class ProvidersAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                val binding: ProviderLayoutBinding = DataBindingUtil.inflate(layoutInflater!!, R.layout.provider_layout, parent, false)
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
            ITEM_TYPE_GENERAL -> if (mList[position] is Data) {
                val mHolder = holder as MyViewHolder
                val data: Data = mList[position] as Data

                mHolder.binding.serviceName.text = data.user_data.name
                mHolder.binding.rateBar.rating = data.user_data.rate.toString().toFloat()
                if (data.user_data.zone.isNullOrEmpty()){
                    mHolder.binding.address.text = data.user_data.area.title
                }else{
                    mHolder.binding.address.text = data.user_data.area.title + ", " + data.user_data.zone?:""
                }

                    Picasso.get().load(Constants.STORAGE + data.user_data.logo).placeholder(R.drawable.ic_logo__1_).into(mHolder.binding.image)


                holder.itemView.setOnClickListener {
                    onItemClickListener?.setOnItemClickListener(data.user_id)
                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position] is Data) {
            ITEM_TYPE_GENERAL
        } else {
            ITEM_TYPE_BANNER_AD
        }
    }

    override fun getItemCount() = mList.size

    class MyViewHolder(val binding: ProviderLayoutBinding) : RecyclerView.ViewHolder(binding.root)

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
        fun setOnItemClickListener(id: Int)
    }
}
