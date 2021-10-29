package com.example.checkbox

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-29
 * @desc
 */
class RecyclerAdapterDialog (val context : Activity?, var list : ArrayList<thumbnailData>, val itemClick : (thumbnailData) -> Unit) : RecyclerView.Adapter<RecyclerAdapterDialog.Holder>() {

    private var size = 200
    private var padding_size = 200
    private val checkboxSet : HashSet<Long> = hashSetOf()
    private var checktempList = arrayListOf<Boolean>()

    inner class Holder(itemView : View?) : RecyclerView.ViewHolder(itemView!!) {
        var thumbnail : ImageView = itemView!!.findViewById<ImageView>(R.id.thumbnail_similarimg)
        var checkbox : CheckBox = itemView!!.findViewById(R.id.checkbox_similarimg)

        fun bind(data : thumbnailData, position : Int) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            thumbnail.setImageResource(0)
            ImageLoder.execute (ThumbnailLoad(this, thumbnail, data.photo_id))

            checkbox.isChecked = checktempList[position]

            itemView.setOnClickListener {itemClick(data) }

            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    checkboxSet.add(data.photo_id)
                    checktempList[position] = true
                } else {
                    checkboxSet.remove(data.photo_id)
                    checktempList[position] = false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.thumbnail_similarview, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position >= checktempList.size)
            checktempList.add(position, false)
        holder.bind(list[position], position)
    }



}