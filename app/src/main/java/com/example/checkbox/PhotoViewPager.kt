package com.example.checkbox

import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.photoview_frame.*
import java.lang.Exception
import java.util.zip.Inflater

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-28
 * @desc
 */
class PhotoViewPager : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var vm : PhotoViewModel
    private lateinit var text_name : AppCompatTextView
    private lateinit var favorite : ImageView
    private lateinit var tag_name : AppCompatTextView
    private lateinit var date_name : AppCompatTextView
    private lateinit var location_name : AppCompatTextView
    private lateinit var Inflater : LayoutInflater
    private lateinit var viewPager : ViewPager

    private var index = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.photoview_frame)
        val view : View = findViewById(R.id.imgViewPager)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        getExtra()
        text_name = findViewById(R.id.imgView_text)
        date_name = findViewById(R.id.imgView_date)
        location_name = findViewById(R.id.imgView_location)
        tag_name = findViewById(R.id.imgView_tag)
        favorite = findViewById(R.id.favorite)

        bottom_photo_menu.setOnNavigationItemSelectedListener(this)
        try {
            setView(view, mainphoto_toolbar, bottom_photo_menu)
        } catch (e : Exception) {
            Toast.makeText(this, "위치 데이터 초기 설정중 입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show()
        }

        toolbar_text(index)
        Inflater = LayoutInflater.from(this)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mainphoto_toolbar!!.visibility = View.VISIBLE
                bottom_photo_menu.visibility = View.VISIBLE
            }

            override fun onPageSelected(position: Int) {
                index = position
                toolbar_text(position)
            }
        })
    }



}








