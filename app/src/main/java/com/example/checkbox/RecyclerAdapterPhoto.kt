package com.example.checkbox

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.MainPhotoView.Companion.checkboxList

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
        var thumbnail : ImageView = itemView!!.findViewById(R.id.thumbnail)
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

            checkbox.isChecked = checkboxList[num].checked
            checkbox.setOnClickListener {
                if (checkbox.isChecked) {
                    checkboxList[num].checked = true
                }
                else {
                    checkboxList[num].checked = false
                }
            }
            // 이미지를 생성하여 삽입하는 작업 필요함
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

    fun setPhotoSize(size : Int, padiing_size : Int) {
        this.size = size
        this.padding_size = padding_size
        notifyDataSetChanged()
    }

    fun setThumbnailList(list : ArrayList<thumbnailData>?) {
        if (list.isNullOrEmpty()) this.list = arrayListOf()
        else {
            var thisIndex = 0
            for(pData in list) {
                do {
                    val pre = if (thisIndex < this.list.size) {
                        pData.data.compareTo(this.list[thisIndex].data)
                    }
                    else { Int.MIN_VALUE }
                    // pre > 0 : 이전 데이터가 사라진 경우
                    if (pre > 0) {
                        if (this.list[thisIndex].photo_id != pData.photo_id) {
                            this.list[thisIndex].photo_id = pData.photo_id
                            checkboxList[thisIndex].id = pData.photo_id
                            MainHandler.post { notifyItemChanged(thisIndex) }
                        }
                        ++thisIndex
                        break
                    }
                    // 삽입
                    else {
                        this.list.add(thisIndex, pData)

                        checkboxList.add(thisIndex, checkboxData(pData.photo_id, false))
                        MainHandler.post { notifyItemInserted(thisIndex) }
                        ++thisIndex
                        break
                    }
                } while (true)
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














