package com.example.checkbox.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.*
import com.example.checkbox.Activity.MainPhotoView.Companion.checkboxList
import com.example.checkbox.db.checkboxData
import com.example.checkbox.db.thumbnailData

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-14
 * @desc
 */
class RecyclerAdapterPhoto(val context: Activity?, var list: ArrayList<thumbnailData>, val itemClick : (thumbnailData, Int) -> Unit) : RecyclerView.Adapter<RecyclerAdapterPhoto.Holder>()
{
    private lateinit var view : View
    private var padding_size = 200
    private var size : Int = 200
    private var ck = 0
    private var ck2 = 0

    inner class Holder(itemView : View?) : RecyclerView.ViewHolder(itemView!!) {
        var thumbnail : ImageView = itemView!!.findViewById(R.id.thumbnail_img)
        var checkbox : CheckBox = itemView!!.findViewById(R.id.checkbox)

        fun bind(data : thumbnailData, num : Int) {
            val layoutParam = thumbnail.layoutParams as ViewGroup.MarginLayoutParams
            thumbnail.layoutParams.width = size
            thumbnail.layoutParams.height = size
            layoutParam.setMargins(padding_size, padding_size, padding_size, padding_size)

            if (ck == 1) {
                checkbox.visibility = View.VISIBLE
            }
            else
                checkbox.visibility = View.GONE

            if (num >= checkboxList.size)
                checkboxList.add(num, checkboxData(data.photo_id, false))

            thumbnail.setImageResource(0)
            ImageLoder.execute(ThumbnailLoad(this, thumbnail, data.photo_id))

            checkbox.isChecked = checkboxList[num].checked
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    checkboxList[num].checked = true
                }
                else {
                    checkboxList[num].checked = false
                }
            }
            // ???????????? ???????????? ???????????? ?????? ?????????
            itemView.setOnClickListener { itemClick(data, num) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        view = LayoutInflater.from(context).inflate(R.layout.thumbnail_imgview, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position], position)
    }

    fun setPhotoSize(size : Int, padding_size : Int) {
        this.size = size
        this.padding_size = padding_size
        notifyDataSetChanged()
    }

    fun setThumbnailList(list : ArrayList<thumbnailData>?) {
        if(list.isNullOrEmpty()) this.list = arrayListOf()
        else {
            var thisIndex = 0
            for(pData in list) {
                do {
                    val pre = if(thisIndex < this.list.size) {
                        pData.data.compareTo(this.list[thisIndex].data)
                    }
                    else { Int.MIN_VALUE }

                    //pre > 0 : ?????? ???????????? ????????? ??????
                    if(pre > 0) {
                        this.list.removeAt(thisIndex)
                        checkboxList.removeAt(thisIndex)
                        MainHandler.post { notifyItemRemoved(thisIndex) }
                        //???????????? ???????????????
                        continue
                    }
                    //????????? ??? ??????
                    else if(pre == 0) {
                        if(this.list[thisIndex].photo_id != pData.photo_id) {
                            this.list[thisIndex].photo_id = pData.photo_id
                            checkboxList[thisIndex].id = pData.photo_id
                            MainHandler.post{ notifyItemChanged(thisIndex) }
                        }
                        ++thisIndex
                        break
                    }
                    //??????
                    else {
                        this.list.add(thisIndex, pData)

                        checkboxList.add(thisIndex, checkboxData(pData.photo_id, false))
                        MainHandler.post{ notifyItemInserted(thisIndex) }
                        ++thisIndex
                        break
                    }
                } while(true)
            }
        }
    }

    fun setCheckAll(boolean: Boolean) {
        for (ckbox in checkboxList) {
            if (ckbox.checked == !boolean)
                ckbox.checked = boolean
        }
        MainHandler.post{ notifyDataSetChanged() }
    }

    fun getThumbnailList() : ArrayList<thumbnailData> {
        return list
    }

    fun addThumbnailList(data : thumbnailData) {
        list.add(data)
        checkboxList.add(checkboxData(data.photo_id, false))
    }

    fun getSize() : Int {
        return list.size
    }

    fun updateCheckbox(n: Int) {
        ck = n
    }

    fun updateCheckbox2(n: Int) {
        ck2 = n
    }
}














