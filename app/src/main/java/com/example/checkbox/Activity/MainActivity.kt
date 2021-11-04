package com.example.checkbox.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.checkbox.*
import com.example.checkbox.db.MediaStore_Dao
import com.example.checkbox.db.PhotoViewModel
import com.example.checkbox.fragment.DateFragment
import com.example.checkbox.fragment.LocationFragment
import com.example.checkbox.fragment.NameFragment
import com.example.checkbox.fragment.TagFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var exitView: View
    private val REQUEST_TAKE_PHOTO = 200
    private var FINISH_INTERVAL_TIME: Long = 1500
    private var backPressedTime: Long = 0
    private var init : Boolean = false
    private lateinit var vm : PhotoViewModel
    private lateinit var observer: ChangeObserver
    lateinit var mCurrentPhotoPath: String

    companion object{
        var folder_type = 3
        var photo_type: Int = 3
        var location_type: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)
        exitView = layoutInflater.inflate(R.layout.exit_layout, null)

        SetHeader()
        init()

        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        DBThread.execute {
            CheckChangeData()
        }

        observer = ChangeObserver(Handler(), this)
        this.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)

        val go_search = findViewById<ImageView>(R.id.main_search_button)
        go_search.setOnClickListener {
            val intent = Intent(this, SearchView::class.java)
            startActivity(intent)
        }

        val go_camera = findViewById<ImageView>(R.id.main_camera_button)
        go_camera.setOnClickListener {
            captureCamera()
            Toast.makeText(this, "카메라 캡쳐", Toast.LENGTH_SHORT).show()
        }
    }

    fun CheckAppFirstExecute():Boolean {
        val pref = getSharedPreferences("IsFirst", Activity.MODE_PRIVATE)
        val isFirst = pref.getBoolean("isFirst", false)
        if (!isFirst)
        { //최초 실행시 true 저장
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.apply()
        }
        return !isFirst
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.favorite -> {
                val intent = Intent(this, MainPhotoView::class.java)
                intent.putExtra("favorite", "favorite")
                startActivityForResult(intent, 300)
            }
            R.id.location_type -> {
                val selectitem = arrayOf<String>("맵으로 보기", "목록으로 보기")
                var select = location_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
                dlg.setTitle("위치별 사진 설정")
                dlg.setSingleChoiceItems(selectitem, location_type) { dialog, i ->
                    when(i) {
                        0 -> select = 0
                        1 -> select = 1
                    }
                }
                dlg.setIcon(R.drawable.ic_tag)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    if(location_type != select) {
                        location_type = select
                    }
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
            R.id.folder_type -> {
                val selectitem = arrayOf<String>("2개씩 보기", "3개씩 보기", "4개씩 보기")
                var select = folder_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
                dlg.setTitle("폴더 목록 설정")
                dlg.setSingleChoiceItems(selectitem, folder_type - 2) { dialog, i ->
                    when(i) {
                        0 -> select = 2
                        1 -> select = 3
                        2 -> select = 4
                    }
                }
                dlg.setIcon(R.drawable.ic_folder)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    if(folder_type != select) {
                        folder_type = select
                        for(fragment: Fragment in supportFragmentManager.fragments) {
                            if (fragment.isVisible) {
                                val tag = fragment.tag
                                lateinit var frag: Fragment
                                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                                supportFragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                when(tag) {
                                    "name" -> {
                                        frag = NameFragment(appbar)
                                    }
                                    "tag" -> {
                                        frag = TagFragment(appbar)
                                    }
                                    "location" -> {
                                        frag = LocationFragment(appbar)
                                    }
                                }
                                transaction.replace(R.id.frame_layout, frag, tag)
                                transaction.addToBackStack(tag)
                                transaction.commit()
                                transaction.isAddToBackStackAllowed
                                break
                            }
                        }

                    }
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
            R.id.photo_type -> {
                val selectitem = arrayOf<String>("2개씩 보기", "3개씩 보기", "4개씩 보기", "5개씩 보기", "6개씩 보기")
                var select = photo_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
                dlg.setTitle("사진 목록 설정")
                dlg.setSingleChoiceItems(selectitem, photo_type - 2) { dialog, i ->
                    when(i) {
                        0 -> select = 2
                        1 -> select = 3
                        2 -> select = 4
                        3 -> select = 5
                        4 -> select = 6
                    }
                }
                dlg.setIcon(R.drawable.ic_image)
                dlg.setPositiveButton("확인") { _, _ ->
                    Toast.makeText(this, "완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    if (photo_type != select) {
                        photo_type = select
                    }
                }
                dlg.setNegativeButton("취소") { _, _ -> }
                dlg.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()

        when(item.itemId){
            R.id.menu_name -> {
                fm.popBackStackImmediate("name", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentA = NameFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentA, "name")
                transaction.addToBackStack("name")
            }
            R.id.menu_tag -> {
                fm.popBackStackImmediate("tag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentB = TagFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentB, "tag")
                transaction.addToBackStack("tag")
            }
            R.id.menu_cal -> {
                fm.popBackStackImmediate("cal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentC = DateFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentC, "cal")
                transaction.addToBackStack("cal")
            }
            R.id.menu_location -> {
                fm.popBackStackImmediate("location", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentD = LocationFragment(appbar)
                transaction.replace(R.id.frame_layout,fragmentD, "location")
                transaction.addToBackStack("location")
            }
        }
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        transaction.isAddToBackStackAllowed

        return true
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1 && supportFragmentManager.findFragmentByTag("name")!!.isVisible) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime
            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onBackPressed()
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        updateBottomMenu(bnv)
    }

    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("name")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("location")

        if (tag1 != null && tag1.isVisible) {navigation.menu.findItem(R.id.menu_name).isChecked =true}
        if (tag2 != null && tag2.isVisible) {navigation.menu.findItem(R.id.menu_tag).isChecked =true}
        if (tag3 != null && tag3.isVisible) {navigation.menu.findItem(R.id.menu_cal).isChecked =true}
        if (tag4 != null && tag4.isVisible) {navigation.menu.findItem(R.id.menu_location).isChecked =true}

    }

    fun init(): Boolean{
        if(!init) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentA = NameFragment(appbar)
            transaction.add(R.id.frame_layout, fragmentA, "name")
            transaction.addToBackStack("name")
            transaction.commit()
            transaction.isAddToBackStackAllowed
            init = true
        }

        if (CheckAppFirstExecute() == true) {
            val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
            dlg.setTitle("안녕하세요") //제목
            dlg.setMessage("Check BOx 가 처음이신가요?\n특징, 위치 추출을 위해 데이터를 연결하세요.\n맵의 경우, 초기 값 설정 과정에서 원활하게 동작하지 않을 수 있습니다.") // 메시지
            dlg.setCancelable(false)
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            })
            dlg.show()
        }
        return true
    }

    private fun captureCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null ) {
            try {
                val photoFile = createImageFile()
                if (photoFile != null) {
                    val providerURI = FileProvider.getUriForFile(this, packageName, photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            } catch (ex: IOException) {
                Log.e("captureCamera Error", ex.toString())
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                Log.i("REQUEST_TAKE_PHOTO", "${Activity.RESULT_OK}" + " " + "${resultCode}")
                if (resultCode == RESULT_OK) {
                    try {
                        galleryAddPic()
                    }catch (e: Exception) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString())
                    }
                } else {
                    Toast.makeText(this@MainActivity, "사진찍기를 취소했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Throws(IOException::class) //IOException 클래스에 대해서는 예외처리
    fun createImageFile(): File? {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = File(
                Environment.getExternalStorageDirectory().toString() + "/Pictures", "Cbox"
        )
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString())
            storageDir.mkdirs() //File.mkdir = 만들고자 하는 디렉토리의 상위 디렉토리가 존재하지 않을 경우, 생성 불가 | File.mkdirs = 만들고자 하는 디렉토리의 상위 디렉토리가 존재하지 않을 경우, 상위 디렉토리까지 생성
        }
        val imageFile = File(storageDir, imageFileName)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun galleryAddPic() {
        Log.i("galleryAddPic", "Call")
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE) // 새로 파일을 만드는것이 아닌 해당 경로의 파일을 객체화
        val f = File(mCurrentPhotoPath)
        val contentUri : Uri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }


    fun CheckChangeData() {
        ChangeCheckThread = ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
        while (!NetworkIsValid(this)) { }
        ChangeCheckThread.execute {
            CheckAddedPhoto()
            CheckDeletedPhoto()
        }
    }

    private fun CheckAddedPhoto() {
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        var lastAddedDate = pref.getLong("lastAddedDate", Long.MIN_VALUE)
        val cursor = vm.getNewlySortedCursor(this, 0)

        if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
                Log.d("데이트값: ", lastAddedDate.toString())

                val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                // 인터넷이 끊길 시 스톱

                while (!NetworkIsValid(this)) { }
                ChangeCheckThread.execute {
                    vm.getFullLocation(this, id)
                    vm.getFavorite(id)
//                    AddTagsByApi(this, id) // 광고
                }

                lastAddedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))
                editor.putLong("lastAddedDate", lastAddedDate)
                editor.apply()
            } while (cursor!!.moveToNext())
            cursor.close()
        }
    }

    private fun CheckDeletedPhoto() {
        val idCursor = vm.getIdCursor()
        if (MediaStore_Dao.cursorIsValid(idCursor)) {
            do {
                vm.CheckIdCursorValid(this, idCursor!!)

            } while (idCursor!!.moveToNext())
            idCursor.close()
        }
    }

    @Suppress("DEPRECATION")    // 'DEPRECATION'에 대한 경고 억제
    private fun NetworkIsValid(context: Context) : Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    result = when(type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }
}