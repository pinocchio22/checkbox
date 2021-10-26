package com.example.checkbox

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_photoview.*
import kotlinx.android.synthetic.main.schedule_insert.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-14
 * @desc
 */
class MainPhotoView : AppCompatActivity() {

    private lateinit var recyclerAdapter: RecyclerAdapterPhoto
    private lateinit var recyclerView : RecyclerView
    var radiobtck: Boolean = false
    private lateinit var vm : PhotoViewModel
    private var mLastClickTime : Long = 0
    private var delete_check = 0

    companion object {
        var list = arrayListOf<thumbnailData>()
        var checkboxList = arrayListOf<checkboxData>()
        var photo_type: Int = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoview)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        radiobtck = radiobt.isChecked
        SetHeader()
        recyclerView = findViewById(R.id.photo_recyclerView)
        setView(arrayListOf())
        getExtra()

        updown_Listener(recyclerView)
        updownEvent()

        photolist_delete.setOnClickListener{
            btck(1)
        }
        photolist_deletecancel.setOnClickListener {
            btck(0)
        }
        radiobt.setOnClickListener {
            btck2()
        }
        photolist_deleteok.setOnClickListener {
            deletePhotoDlg()
        }
    }

    fun deletePhotoDlg() {
        val warningBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
            this, // 경고 다이얼로그
        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
        )
        warningBuilder.setTitle("알림")   //제목
        warningBuilder.setMessage("체크된 사진들을 모두 삭제합니다. \n정말 삭제하시겠습니까?")  //메세지
        warningBuilder.setCancelable(false)
        warningBuilder.setPositiveButton(
            "확인", DialogInterface.OnClickListener { dialog, which ->
                var i = 0
                var j = 0
                while (checkboxList.size != 0 && i < checkboxList.size) {
                    if (checkboxList[i].checked) {
                        val temp = checkboxList[i].id
                        list.removeAt(i)
                        checkboxList.removeAt(i)
                        recyclerAdapter.notifyDataSetChanged()
                        j = 1
                    }
                    else
                        i++
                }
                if (j == 1)
                    Toast.makeText(this, "사진이 삭제 완료 되었습니다.", Toast.LENGTH_SHORT)
                else
                    Toast.makeText(this, "삭제할 사진을 체크해주세요", Toast.LENGTH_SHORT)
            }
        )
        warningBuilder.setNegativeButton(
            "취소", DialogInterface.OnClickListener{ dialog, which ->
                dialog.cancel()
            })
        val dlgWarning = warningBuilder.create()
        dlgWarning.show()
    }

    private fun btck(n : Int) {
        appbar2.visibility = View.VISIBLE
        appbar2.setExpanded(true,true)
        DBThread.execute {
            MainHandler.post {
                recyclerAdapter.updateCheckbox(n)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
        if (n==1) {
            photolist_delete.visibility = View.GONE
            photolist_deleteok.visibility = View.VISIBLE
            photolist_deletecancel.visibility = View.VISIBLE
            radiobt.visibility = View.VISIBLE
        }
        else {
            photolist_delete.visibility = View.VISIBLE
            photolist_deleteok.visibility = View.GONE
            photolist_deletecancel.visibility = View.GONE
            radiobt.visibility = View.GONE
        }
    }

    private fun btck2() {
        if(radiobtck == true) {
            recyclerAdapter.setCheckAll(false)
            radiobt.isChecked = false
            radiobtck = false
        }
        else {
            recyclerAdapter.setCheckAll(true)
            radiobtck = true
        }
    }

    private fun setView(lst : ArrayList<thumbnailData>) {
        recyclerAdapter = RecyclerAdapterPhoto(this, lst) {
            thumbnailData, num -> if (SystemClock.elapsedRealtime() - mLastClickTime > 300) {
                val intent = Intent(this, PhotoViewModel::class.java)
            intent.putExtra("index", num)
            startActivityForResult(intent, 100)
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        recyclerView.adapter = recyclerAdapter
        list = recyclerAdapter.getThumbnailList()
        val lm = GridLayoutManager(MainPhotoView(), photo_type)
        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val size = width / row - 2 * padding

        recyclerAdapter.setPhotoSize(size, padding)
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.photo_toolbar)
        toolbar.bringToFront()
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(photo_type, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    val doc = data!!.getIntExtra("index", 0)
                    recyclerView.scrollToPosition(doc)
                }
            }
        }
    }

    fun getExtra() {
        checkboxList.clear()
        list.clear()
        val getname : String?
        val title_type : ImageView = findViewById(R.id.title_type)
        val title : TextView = findViewById(R.id.title_name)

        when {
            intent.hasExtra("dir_name") -> {
                getname = intent.getStringExtra("dir_name")!!

                DBThread.execute {
                    getOpenDirByCursor(vm, vm.getOpenNameDirCursor(applicationContext, getname))
                }

                title_type.setImageResource(R.drawable.ic_folder)
                title.text = File(getname).name
            }

            intent.hasExtra("location_name") -> {
                getname = intent.getStringExtra("location_name")!!

                val liveData = vm.getOpenLocationDirIdList(getname)
                liveData.observe(this, androidx.lifecycle.Observer { idList ->
                    DBThread.execute {
                        getOpenDirByIdList(vm, idList)
                    }
                })

                title_type.setImageResource(R.drawable.ic_location)
                title.text = getname
            }

            intent.hasExtra("data_name") -> {
                val cal = intent.getSerializableExtra("date_name") as Date
                val calendar = Calendar.getInstance()

                calendar.time = cal
                DBThread.execute {
                    getOpenDirByCursor(vm, vm.getOpenDateDirCursor(applicationContext, calendar))
                }

                val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                getname = formatter.format(calendar.time)

                title_type.setImageResource(R.drawable.ic_cal)
                title.text = getname
            }

            intent.hasExtra("tag_name") -> {
                getname = intent.getStringExtra("tag_name")!!

                val liveData = vm.getOpenTagDirIdList(getname)
                liveData.observe(this, androidx.lifecycle.Observer { idList ->
                    DBThread.execute {
                        getOpenDirByIdList(vm, idList)
                    }
                })

                title_type.setImageResource(R.drawable.ic_tag)
                title.text = getname
            }

            intent.hasExtra("file_name") -> {
                var filename = intent.getStringExtra("file_name")!!

                    DBThread.execute {
                        getOpenDirByIdList(vm, vm.getOpenFileDirCursor(applicationContext, filename))
                    }

                title_type.setImageResource(R.drawable.ic_name)
                if (filename.length >= 23) {
                    filename = filename.substring(0, 23)
                    filename += ".."
                }
                title.text = filename
            }

            intent.hasExtra("favorite") -> {
                val liveData = vm.getOpenFavoriteDirIdList()
                var templist = listOf<Long>()
                liveData.observe(this, androidx.lifecycle.Observer { idList ->
                    if (idList != templist) {
                        DBThread.execute {
                            getOpenDirByIdList(vm, idList)
                            MainHandler.post {
                                setView(list)
                                setPhotoSize(photo_type, 2)
                            }
                        }
                    }
                })

                title_type.setImageResource(R.drawable.ic_favorite_checked)
                title.text = "즐겨찾기"
            }

            intent.hasExtra("search_date") -> {
                val date = intent.getStringExtra("search_date")!!
                val cal : Calendar = Calendar.getInstance()
                cal.set(date.substring(0, 4).toInt(), date.substring(6, 8).toInt() - 1, date.substring(10, 12).toInt(), 0, 0, 0)
                DBThread.execute {
                        getOpenDirByIdList(vm, vm.getOpenDateDirCursor(applicationContext, cal))
                }
                title_type.setImageResource(R.drawable.ic_cal)
                title.text = date
            }
        }
    }
    
    fun updown_Listener(view : RecyclerView?) {
        up_button.setOnClickListener {
            view?.scrollToPosition(0)
        }

        down_button.setOnClickListener {
            view?.scrollToPosition(recyclerAdapter.getSize() - 1)
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun finishActivity() {
        val intent = Intent()
        if (delete_check == 1)
            intent.putExtra("delete_check", delete_check)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun updownEvent() {
        updown_Listener(recyclerView)
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            var temp = 0
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (temp == 1) {
                    super.onScrolled(recyclerView, dx, dy)
                    up_button.visibility = View.GONE
                    down_button.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                up_button.visibility = View.VISIBLE
                down_button.visibility = View.VISIBLE
                temp = 1
            }
        }
        this.recyclerView.setOnScrollListener(onScrollListener)
    }

    private fun getOpenDirByCursor(vm : PhotoViewModel, cursor : Cursor?) {
        if (vm.CursorIsValid(cursor)) {
            do {
                val data = vm.getThumbnailDataByCursor(cursor!!)
                recyclerAdapter.addThumbnailList(data)
            } while (cursor!!.moveToNext())
            cursor.close()
            MainHandler.post { setView(list)
            setPhotoSize(photo_type, 2)}
        }
    }








    




}