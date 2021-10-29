package com.example.checkbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater as LayoutInflater1

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-29
 * @desc
 */
class PagerRecyclerAdapter (parivate val context : Context, var list : ArrayList<thumbnailData>, var tb : View, var bt : View) : PagerAdapter() {
    private var layoutInflater : LayoutInflater1? = null
    private var check : Boolean = false

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`    // '===' 는참조 타입의 주소 값을 비교
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater1
        val v = layoutInflater!!.inflate(R.layout.photoview_pager, null)
        val image = v.findViewById<View>(R.id.imgView) as ImageView
        val vp = container as ViewPager
        vp.addView(v, 0)
        ImageLoder.execute(ImageLode(context, image, list[position].photo_id, 0))
        image.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (check == false) {
                    tb.visibility = View.GONE
                    bt.visibility = View.GONE
                    check = true
                } else {
                    tb.visibility = View.VISIBLE
                    bt.visibility = View.VISIBLE
                    check = false
                }
            }
        })
        return v
    }


}











