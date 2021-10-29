package com.example.checkbox

import android.content.Context
import android.view.View
import androidx.viewpager.widget.PagerAdapter

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-29
 * @desc
 */
class PagerRecyclerAdapter (parivate val context : Context, var list : ArrayList<thumbnailData>, var tb : View, var bt : View) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`    // '===' 는참조 타입의 주소 값을 비교
    }

    override fun getCount(): Int {
        return list.size
    }

}