package com.mohamed.aykhedmah.view.common.providerdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.mohamed.aykhedmah.R
import com.mohamed.aykhedmah.data.Constants.STORAGE
import com.mohamed.aykhedmah.data.network.response.providerdetails.UserImage
import com.squareup.picasso.Picasso

class PhotosAdapter (
    private val context: Context
) :
    BaseAdapter() {
    var onItemClickListener: OnItemClickListener? = null
    private var images: ArrayList<UserImage> = ArrayList()
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var imageDelete: ImageView
    private var isEditing: Boolean = false
    override fun getCount(): Int {
        return images.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return 0
    }
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.rowitem, null)
        }

        imageView = convertView!!.findViewById(R.id.image_view)
        imageDelete = convertView.findViewById(R.id.delete_image)

        if (isEditing){
            imageDelete.visibility = View.VISIBLE
        }

        imageDelete.setOnClickListener { onItemClickListener?.setOnItemClickListener(images[position].id) }


        Picasso.get().load(STORAGE +images[position].image).placeholder(R.drawable.ic_picture).into(imageView)
        return convertView
    }

    fun submitList(list: List<UserImage>?, isEdit: Boolean) {

        isEditing = isEdit
        list?.let {
            images.addAll(it)
            notifyDataSetChanged()
        }
    }

    @Suppress("unused")
    fun clear() {
        images.clear()
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun setOnItemClickListener(id: Int)
    }
}