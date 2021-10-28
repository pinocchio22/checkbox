package com.example.checkbox

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.checkbox.MainPhotoView.Companion.list
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
    private var recyclerAdapter : PagerRecyclerAdapter ?= null

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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setView(view : View, toolbar : androidx.appcompat.widget.Toolbar, bottombar : View) {
        viewPager = view.findViewById(R.id.imgViewPager)
        recyclerAdapter = PagerRecyclerAdapter(this, list, toolbar, bottombar)

        viewPager.adapter = recyclerAdapter
        viewPager.adapter!!.notifyDataSetChanged()
        viewPager.setCurrentItem(index, false)
    }

    override fun onBackPressed() {
        finishActivity()
    }

    @SuppressLint("SimpleDateFromat")
    fun toolbar_text(position : Int) {
        val id = list[position].photo_id

        DBThread.execute {
            val data = vm.getName(this.applicationContext, id)
            MainHandler.post { text_name.text = data }
        }

        DBThread.execute {
            val data = vm.getStringDate(applicationContext, id)
            MainHandler.post { date_name.text = data }
        }

        DBThread.execute {
            val data = vm.getLocation(applicationContext, id)
            MainHandler.post { location_name.text = data }
        }

        DBThread.execute {
            val data = vm.getTags(id)
            MainHandler.post { tag_name.text = data }
        }

        DBThread.execute {
            val data = vm.getFavorite(id)
            if (data) favorite.setImageResource(R.drawable.ic_favorite_checked)
            else favorite.setImageResource(R.drawable.ic_favorite)
        }

        favorite.setOnClickListener {
            DBThread.execute {
                val data = vm.changeFavorite(id)
                if (data) favorite.setImageResource(R.drawable.ic_favorite_checked)
                else favorite.setImageResource(R.drawable.ic_favorite)
            }
        }
    }

    fun getExtra() {
        if (intent.hasExtra("index")) {
            index = intent.getIntExtra("index", 0)
        }
        else {
            Toast.makeText(this, "전달된 이름이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when(p0.itemId) {
            R.id.menu_tag_insert -> {
                insertTag()
            }
            R.id.menu_share -> {
                share()
            }
            R.id.menu_similar -> {
                similarImage()
            }
            R.id.menu_delete -> {
                delete(imgViewPager, mainphoto_toolbar, bottom_photo_menu)
            }
        }
        return true
    }



}








