package com.example.checkbox

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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
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

        observer = ChangeObserver(Handler(), this)
        this.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)

        val go_search = findViewById<ImageView>(R.id.main_search_button)
        go_search.setOnClickListener {
            val intent = Intent(this, SearchView::class.java)
            startActivity(intent)
        }

        val go_carmera = findViewById<ImageView>(R.id.main_camera_button)
        go_carmera.setOnClickListener {
//            captureCamera()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        val fm = supportFragmentManager
//        val transaction: FragmentTransaction = fm.beginTransaction()
//
//        when(p0.itemId){
//            R.id.menu_name -> {
//                fm.popBackStackImmediate("name", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentA = NameFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentA, "name")
//                transaction.addToBackStack("name")
//            }
//            R.id.menu_tag -> {
//                fm.popBackStackImmediate("tag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentB = TagFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentB, "tag")
//                transaction.addToBackStack("tag")
//            }
//            R.id.menu_cal -> {
//                fm.popBackStackImmediate("cal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentC = DateFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentC, "cal")
//                transaction.addToBackStack("cal")
//            }
//            R.id.menu_location -> {
//                fm.popBackStackImmediate("location", FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragmentD = LocationFragment(appbar)
//                transaction.replace(R.id.frame_layout,fragmentD, "location")
//                transaction.addToBackStack("location")
//            }
//        }
//        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        transaction.commit()
//        transaction.isAddToBackStackAllowed

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

    @Throws(IOException::class)
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

    @Suppress("DEPRECATION")
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