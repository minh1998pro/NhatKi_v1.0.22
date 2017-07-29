package com.example.minh98.nhatki.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.minh98.nhatki.R
import com.example.minh98.nhatki.adapter.AdapterFragment
import com.example.minh98.nhatki.database.SharePreference
import com.example.minh98.nhatki.database.sqlite
import com.example.minh98.nhatki.enums.ToastMode
import com.example.minh98.nhatki.fragment.NhatKi
import com.example.minh98.nhatki.listener.fragmentListener
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    lateinit var viewPager: ViewPager
    lateinit var username: String
    lateinit var userpass: String
    lateinit var listener: fragmentListener
    lateinit var adapterFragment: AdapterFragment
    lateinit var txtTenHienThi: TextView
    lateinit var txtTaiKhoan: TextView
    lateinit var txtConnectState: TextView
    val host: String = "http://it-yeuits.rhcloud.com/"
    val registerTaiKhoan = "registerTaiKhoan.php"
    //val loginTaiKhoan="loginTaiKhoan.php"
    val insertNhatKi = "insertNhatKi.php"
    val getNhatKi = "getNhatKi.php"
    //val updateTaiKhoan="updateTaiKhoan.php"
    val updateThongTinTaiKhoan = "updateThongTinTaiKhoan.php"
    val checkTaiKhoan = "checkTaiKhoan.php"
    val insertThongTinTaiKhoan = "insertThongTinTaiKhoan.php"
    val insert_info_account: String by lazy { getString(R.string.insert_info_account) }
    val add_account_fail: String by lazy { getString(R.string.add_account_fail) }
    val add_account_success: String by lazy { getString(R.string.add_account_success) }
    val notice_online: String by lazy { getString(R.string.notice_online) }
    val notive_offline: String by lazy { getString(R.string.notice_offline) }
    val connect_fail: String by lazy { getString(R.string.connect_fail) }
    val content_empty: String by lazy { getString(R.string.content_empty) }
    val add_nhatki_fail: String by lazy { getString(R.string.add_nhatki_fail) }
    val add_nhatki_success: String by lazy { getString(R.string.add_nhatki_success) }
    val ask_exit_app: String by lazy { getString(R.string.ask_exit_app) }
    val account_wrong: String by lazy { getString(R.string.account_wrong) }
    val logout_fail: String by lazy { getString(R.string.logout_fail) }
    val logout_success: String by lazy { getString(R.string.logout_success) }
    val device_is_offline: String by lazy { getString(R.string.device_is_offline) }
    val asyn_success: String by lazy { getString(R.string.asyn_success) }
    val handlers: Handler = handler()
    var TenHienThi: String = ""
    var TaiKhoan: String = ""
    val sharePreference: SharePreference by lazy {
        SharePreference(this)
    }
    val sqlite: sqlite by lazy {
        sqlite(this, "NhatKi.sqlite", null, 1)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        //kiem tra xem thiet bi co duoc ket noi hay ko(co thi show thong bao)
        checkConnect()
        
        //init username from intent
        username = intent.getStringExtra("username")
        userpass = intent.getStringExtra("userpass")
        
        
        //init toolbar
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        
        
        //init floatbuttonaction(them nhat ki)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            actionShowAddNhatKi()
        }
        
        
        //init connectState
        txtConnectState = findViewById(R.id.connectState) as TextView
        //init drawer,toggle
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        
        
        //init navigationView
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        
        
        //init header view
        val header = navigationView.getHeaderView(0)
        //init thong tin tai khoan trong headerView
        txtTenHienThi = header.findViewById(R.id.txtTenHienThi) as TextView
        txtTaiKhoan = header.findViewById(R.id.txtTaiKhoan) as TextView
        
        initHeaderView()
        
        //init ViewPager,TabLayout
        viewPager = findViewById(R.id.viewPager) as ViewPager
        adapterFragment = AdapterFragment(supportFragmentManager)
        adapterFragment.TaiKhoanUser = username
        viewPager.adapter = adapterFragment
        val tab: TabLayout = findViewById(R.id.tabs) as TabLayout
        tab.setupWithViewPager(viewPager)
        tab.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        
        //done!
    }
    
    private fun isConnected(): Boolean {
        val connectManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED
                || connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED
    }
    
    private fun checkConnect() {

        val fab: FloatingActionButton = findViewById(R.id.fab) as FloatingActionButton
        val snackbar: Snackbar = Snackbar.make(fab, notice_online, Snackbar.LENGTH_INDEFINITE)
        if (isConnected()) {
            
            with(snackbar) {
                setAction("Ok", { dongBoTaiKhoan() })
                setActionTextColor(Color.RED)
                val v: View = view
                (v.findViewById(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.YELLOW)
                show()
            }
        } else {
            with(snackbar) {
                setAction("Ok", { })
                setText(notive_offline)
                setActionTextColor(Color.RED)
                val v: View = view
                (v.findViewById(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.YELLOW)
                show()
            }
        }
    }
    
    
    override fun onStop() {
        super.onStop()
        initHeaderView()
    }
    
    
    override fun onResume() {
        super.onResume()
        initHeaderView()
    }
    
    override fun onDestroy() {
        if (isConnected()) {
            //neu co ket noi thi dong bo du lieu
            dongBoDuLieu().execute()
        }
        super.onDestroy()
    }
    
    fun initListener(listener1: fragmentListener) {
        this.listener = listener1
    }
    
    private fun actionShowAddNhatKi() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        val alertdialog: AlertDialog
        
        val view = layoutInflater.inflate(R.layout.layout_show_add_nhatki, null)
        val editTieuDeNhatKi: EditText = view.findViewById(R.id.editTieuDeNhatKi) as EditText
        val editNoiDungNhatKi: EditText = view.findViewById(R.id.editNoiDungNhatKi) as EditText
        //set su thay doi noi dung nhat ki se thay doi textSize
        editNoiDungNhatKi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //
            }
            
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ss = s!!
                //size text thay doi theo  noi dung
                if (ss.length in 1..20) {
                    editNoiDungNhatKi.textSize = 60.0F
                } else if (ss.length in 21..60) {
                    editNoiDungNhatKi.textSize = 40.0F
                } else {
                    editNoiDungNhatKi.textSize = 20.0F
                }
            }
            
        })
        
        val btnCancel: Button = view.findViewById(R.id.btnCancel) as Button
        val btnOk: Button = view.findViewById(R.id.btnOk) as Button
        alert.setView(view)
        alert.setCancelable(false)
        
        alertdialog = alert.create()
        btnCancel.setOnClickListener { alertdialog.dismiss() }
        
        btnOk.setOnClickListener {
            //tien hanh them nhat ki vao sqlite
            if (editTieuDeNhatKi.text.isEmpty()) {
                editTieuDeNhatKi.error = content_empty
                return@setOnClickListener
            }
            if (editNoiDungNhatKi.text.isEmpty()) {
                editNoiDungNhatKi.error = content_empty
                return@setOnClickListener
            }
            //ok
            if (postNhatKi(editTieuDeNhatKi.text.toString().replace("'", ""), editNoiDungNhatKi.text.toString().replace("'", ""))) {
                toast(add_nhatki_success, ToastMode.SUCCESS)
                alertdialog.dismiss()
                //cap nhat lai danh sach nhat ki
                updateList()
            } else {
                toast(add_nhatki_fail, ToastMode.ERROR)
            }
            
        }
        
        
        alertdialog.show()
    }
    
    private fun postNhatKi(tieude: String, noidung: String): Boolean {
        val thoigian = System.currentTimeMillis().toString()
        return sqlite.insertNhatKi(tieude, noidung, thoigian, username)
    }
    
    private fun toast(s: String, mode: ToastMode) {
        when (mode) {
            ToastMode.SUCCESS -> Toasty.success(this, s, Toast.LENGTH_LONG, true).show()
            ToastMode.ERROR -> Toasty.error(this, s, Toast.LENGTH_LONG, true).show()
            ToastMode.INFO -> Toasty.info(this, s, Toast.LENGTH_LONG, true).show()
            ToastMode.WARNING -> Toasty.warning(this, s, Toast.LENGTH_LONG, true).show()
        }
        Log.e("LOG:", s)
    }
    
    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val alert = AlertDialog.Builder(this)
            with(alert) {
                setTitle("Thông báo")
                setMessage(ask_exit_app)
                setPositiveButton("Thoát", { _, _ -> super.onBackPressed() })
                setNegativeButton("Cancel", { dialog, _ -> dialog.dismiss() })
                create().show()
                
            }
        }
    }
    
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chon_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_gridviewLayout -> sharePreference.set("layoutMode", 0) // 0:gridview
            R.id.item_linearLayout -> sharePreference.set("layoutMode", 1) //1:linearLayout
        }
        updateList()
        return super.onOptionsItemSelected(item)
    }
    
    private fun updateList() {
        this.listener.updateNhatKi()
        viewPager.adapter = adapterFragment
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        
        when (id) {
            R.id.item_Logout -> {
                val alert = AlertDialog.Builder(this)
                with(alert) {
                    setTitle("Thông báo")
                    setMessage(ask_exit_app)
                    setPositiveButton("Thoát", { _, _ ->
                        //neu thoat se reset username o sharepreference
                        if (!sharePreference.set("username", "")) {
                            toast(logout_fail, ToastMode.ERROR)
                        } else {
                            toast(logout_success, ToastMode.ERROR)
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                        }
                    })
                    setNegativeButton("Cancel", { dialog, _ -> dialog.dismiss() })
                    create().show()
                    
                }
            }
            R.id.item_editAccount -> {
                
                startActivityForResult(Intent(this, EditAccountActivity::class.java).apply { putExtra("TaiKhoan", username) }, 0)
            }
            R.id.item_dong_bo_nhatki -> {
                if (isConnected()) {
                    dongBoTaiKhoan()
                } else {
                    toast(device_is_offline, ToastMode.ERROR)
                }
                
            }
            
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    
    
    private fun dongBoTaiKhoan() {
        dongBoTaiKhoanClasss().execute()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == resultCode) {
            initHeaderView()
        }
    }
    
    private fun initHeaderView() {
        //init header view navigationview
        val cursor = sqlite.get1ThongTinByTaiKhoan(username)
        if (cursor.moveToNext()) {
            TenHienThi = cursor.getString(2)
            TaiKhoan = "Username:" + cursor.getString(0)
            userpass = cursor.getString(1)
        }
        txtTaiKhoan.text = TaiKhoan
        txtTenHienThi.text = TenHienThi
        
        //done !
    }
    
    inner class dongBoDuLieu : AsyncTask<Void, Void, Unit>() {
        //TODO NETWORK
        override fun doInBackground(vararg params: Void?): Unit {
            return if (getTKFromServer() == "true") {
                //neu co ton tai tai khoan thi update du lieu
                
                //insertTaiKhoan(registerTaiKhoan)  //khong can insert tai khoan nua
                insertThongTinTaiKhoan(updateThongTinTaiKhoan) //update thong tin tai khoan
                insertNhatKi(insertNhatKi)
            } else {
                //
            }
        }
        
        
    }
    
    inner class dongBoTaiKhoanClasss : AsyncTask<Void, Void, String>() {
        //TODO MAIN
        
        override fun doInBackground(vararg params: Void): String {
            
            if (getTKFromServer() == "true") {
                //neu co ton tai tai khoan thi update du lieu
                
                //insertTaiKhoan(registerTaiKhoan)  //khong can insert tai khoan nua
                insertThongTinTaiKhoan(updateThongTinTaiKhoan) //update thong tin tai khoan
                insertNhatKi(insertNhatKi)
                getNhatKiFromServer()
                Log.e("LOG", "get from server: true")
                return "true"
            } else if (getTKFromServer() == "false") {
                //neu chua ton tai tai khoan nay thi insert du lieu
                insertTaiKhoan(registerTaiKhoan)
                insertThongTinTaiKhoan(insertThongTinTaiKhoan)
                insertNhatKi(insertNhatKi)
                Log.e("LOG", "get from server: false")
                return "false"
            } else if (getTKFromServer() == "error") {
                //neu ton tai tai khoan nhung sai mat khau thi return false
                Log.e("LOG", "get from server: error")
                return "sai"
            }
            Log.e("LOG", "get from server: du lieu khong hop le")
            return "sai" // khong quan trong
        }
        
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result == "false") {
                //neu nhu tai khoan khong dung
                toast(account_wrong, ToastMode.INFO)
            } else if (result == "sai") {
                toast(connect_fail, ToastMode.ERROR)
            } else if (result == "true") {
                toast(asyn_success, ToastMode.SUCCESS)
            }
        }
        
    }
    
    private fun getNhatKiFromServer() {
        //TODO NETWORK
        try {
            val url: URL = URL(host + getNhatKi)//link get1tk
            val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
            val taiKhoan = username
            with(connect) {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "POST"
            }
            val query: String = Uri.Builder()
                    .appendQueryParameter("TaiKhoan", taiKhoan)
                    .build().encodedQuery
            val output: OutputStream = connect.outputStream
            val buffer: BufferedWriter = BufferedWriter(OutputStreamWriter(output, "UTF-8"))
            buffer.write(query)
            buffer.flush()
            buffer.close()
            connect.connect()
            //post du lieu xong
            val resultCode = connect.responseCode
            if (resultCode == HttpURLConnection.HTTP_OK) {
                //ket noi thanh cong
                //lay du lieu tra ve
                val input: InputStream = connect.inputStream
                val buffer: BufferedReader = BufferedReader(InputStreamReader(input))
                //0:khong hop le,1:co hop le
                val result = buffer.readText()
                Log.e("result:", result)
                if (result == "false") {
                    //khong thanh cong hoac khong co nhat ki
                    handlers.sendMessage(Message().apply { obj = "ket qua ket noi server get nhat ki: false" })
                } else {
                    //thanh cong// result tra ve array object json
                    val jsonArray: JSONArray = JSONArray(result) //get jsonarray
                    for (i in 0..jsonArray.length() - 1) {
                        val jsonObject: JSONObject = jsonArray[i] as JSONObject
                        val tieuDe: String = jsonObject.getString("TieuDe")
                        val noiDung: String = jsonObject.getString("NoiDung")
                        val thoiGian: String = jsonObject.getString("ThoiGian")
                        val taiKhoanUser: String = jsonObject.getString("TaiKhoanUser")
                        sqlite.insertNhatKi(tieuDe, noiDung, thoiGian, taiKhoanUser)
                    }
                    handlers.sendMessage(Message().apply {
                        obj = "ket qua ket noi server: insert nhat ki thanh cong"
                        what = 1 //update listNhatKi
                    })
                    
                }
            } else {
                handlers.sendMessage(Message().apply { obj = "khong the ket noi server get nhat ki" })
            }
            Log.e("LOG:", "loiketnoi get nhat ki ")
        } catch (e: Exception) {
            handlers.sendMessage(Message().apply { what = 2 })
            Log.e("getnhatki from server", e.toString())
        }
    }
    
    private fun insertThongTinTaiKhoan(insertThongTinTaiKhoan: String) {
        //TODO NETWORK
        try {
            val url: URL = URL(host + insertThongTinTaiKhoan)//link get1tk
            val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
            val cursor = sqlite.get1ThongTinByTaiKhoan(username)
            val taiKhoan = username
            val matKhau = userpass
            var tenHienThi: String? = ""
            var gioiTinh: String? = ""
            var namSinh: String? = ""
            var queQuan: String? = ""
            var soThich: String? = ""
            while (cursor.moveToNext()) {
                tenHienThi = cursor.getString(2)
                gioiTinh = cursor.getString(3)
                namSinh = cursor.getString(4)
                queQuan = cursor.getString(5)
                soThich = cursor.getString(6)
            }
            
            with(connect) {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "POST"
            }
            val query: String = Uri.Builder()
                    .appendQueryParameter("TaiKhoan", taiKhoan)
                    .appendQueryParameter("MatKhau", matKhau)
                    .appendQueryParameter("TenHienThi", tenHienThi)
                    .appendQueryParameter("GioiTinh", gioiTinh)
                    .appendQueryParameter("NamSinh", namSinh)
                    .appendQueryParameter("QueQuan", queQuan)
                    .appendQueryParameter("SoThich", soThich)
                    .build().encodedQuery
            val output: OutputStream = connect.outputStream
            val buffer: BufferedWriter = BufferedWriter(OutputStreamWriter(output, "UTF-8"))
            buffer.write(query)
            buffer.flush()
            buffer.close()
            connect.connect()
            
            val resultCode = connect.responseCode
            if (resultCode == HttpURLConnection.HTTP_OK) {
                //ket noi thanh cong
                //lay du lieu tra ve
                val input: InputStream = connect.inputStream
                val buffer: BufferedReader = BufferedReader(InputStreamReader(input))
                //0:khong hop le,1:co hop le
                val result = buffer.readLine()
                if (result == "true") {
                    handlers.sendMessage(Message().apply { obj = "$add_account_success:$insert_info_account" })
                } else if (result == "false") {
                    handlers.sendMessage(Message().apply { obj = "$add_account_fail:$insert_info_account" })
                }
            } else {
                handlers.sendMessage(Message().apply { obj = "$connect_fail:$insert_info_account" })
            }
        } catch (e: Exception) {
            handlers.sendMessage(Message().apply { what = 2 })
            Log.e("insertthongtintaikhoan", e.toString())
        }
    }
    
    
    private fun getTKFromServer(): String {
        //TODO NETWORK
        try {
            val url: URL = URL(host + checkTaiKhoan)//link get1tk
            val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
            val taiKhoan = username
            val matKhau = userpass
            with(connect) {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "POST"
            }
            val query: String = Uri.Builder()
                    .appendQueryParameter("TaiKhoan", taiKhoan)
                    .appendQueryParameter("MatKhau", matKhau)
                    .build().encodedQuery
            val output: OutputStream = connect.outputStream
            val buffer: BufferedWriter = BufferedWriter(OutputStreamWriter(output, "UTF-8"))
            buffer.write(query)
            buffer.flush()
            buffer.close()
            connect.connect()
            //post du lieu xong
            val resultCode = connect.responseCode
            if (resultCode == HttpURLConnection.HTTP_OK) {
                //ket noi thanh cong
                //lay du lieu tra ve
                val input: InputStream = connect.inputStream
                val buffer: BufferedReader = BufferedReader(InputStreamReader(input))
                //0:khong hop le,1:co hop le
                val result = buffer.readLine()
                Log.e("result:", result)
                if (result == "true") {
                    handlers.sendMessage(Message().apply { obj = "ket qua ket noi server:get tai khoan from server: true" })
                    return "true"
                } else if (result == "false") {
                    handlers.sendMessage(Message().apply { obj = "ket qua ket noi server:get tai khoan from server: false" })
                    return "false"
                } else if (result == "sai") {
                    handlers.sendMessage(Message().apply { obj = "ket qua ket noi server:get tai khoan from server: sai" })
                    return "sai"
                }
            } else {
                handlers.sendMessage(Message().apply { obj = "khong the ket noi server:get tai khoan from server" })
                return "loiketnoi:get tai khoan from server"
            }
            Log.e("LOG:", "loiketnoi:get tai khoan from server")
            return "loiketnoi"
        } catch (e: Exception) {
            handlers.sendMessage(Message().apply { what = 2 })
            Log.e("gettk from server", e.toString())
            return "loiketnoi"
        }
    }
    
    private fun insertNhatKi(insertNhatKi: String) {
        //TODO NETWORK
        val itemNhatKis: MutableList<NhatKi> = mutableListOf()
        val cursor = sqlite.getNhatKiByTaiKhoanUser(username)
        //init item nhat ki
        while (cursor.moveToNext()) {
            val tieude = cursor.getString(0)
            val noidung = cursor.getString(1)
            val thoigian = cursor.getString(2)
            itemNhatKis.add(NhatKi(tieude, noidung, thoigian))
        }
        
        try {
            for (i in 0..itemNhatKis.size - 1) {
                val tieuDe = itemNhatKis[i].tieuDe
                val noiDung = itemNhatKis[i].noiDung
                val thoiGian = itemNhatKis[i].time
                
                val query: String = Uri.Builder()
                        .appendQueryParameter("TieuDe", tieuDe)
                        .appendQueryParameter("NoiDung", noiDung)
                        .appendQueryParameter("ThoiGian", thoiGian)
                        .appendQueryParameter("TaiKhoanUser", username)
                        .build().encodedQuery
                val url: URL = URL(host + insertNhatKi)
                val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
                
                with(connect) {
                    connectTimeout = 10000
                    readTimeout = 10000
                    requestMethod = "POST"
                }
                val output: OutputStream = connect.outputStream
                val buffer: BufferedWriter = BufferedWriter(OutputStreamWriter(output, "UTF-8"))
                buffer.write(query)
                buffer.flush()
                buffer.close()
                output.close()
                connect.connect()
                
                val resultCode = connect.responseCode
                if (resultCode == HttpURLConnection.HTTP_OK) {
                    //ket noi thanh cong
                    //lay du lieu tra ve
                    val input: InputStream = connect.inputStream
                    val buffer: BufferedReader = BufferedReader(InputStreamReader(input))
                    //0:khong hop le,1:co hop le
                    val result = buffer.readLine()
                    buffer.close()
                    input.close()
                    if (result == "true") {
                        handlers.sendMessage(Message().apply { obj = "$add_nhatki_success:insert nhat ki." })
                    } else if (result == "false") {
                        handlers.sendMessage(Message().apply { obj = "$add_nhatki_fail:insert nhat ki." })
                    }
                } else {
                    handlers.sendMessage(Message().apply { obj = "ket noi that bai:insert nhat ki." })
                }
            }
        } catch (e: Exception) {
            handlers.sendMessage(Message().apply { what = 2 })
            Log.e("insert nhat ki", e.toString())
        }
        
    }
    
    private fun insertTaiKhoan(registerTaiKhoan: String) {
        //TODO NETWORK
        try {
            val url: URL = URL(host + registerTaiKhoan)//link get1tk
            val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
            val taiKhoan = username
            val matKhau = userpass
            with(connect) {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "POST"
            }
            val query: String = Uri.Builder()
                    .appendQueryParameter("TaiKhoan", taiKhoan)
                    .appendQueryParameter("MatKhau", matKhau)
                    .build().encodedQuery
            val output: OutputStream = connect.outputStream
            val buffer: BufferedWriter = BufferedWriter(OutputStreamWriter(output, "UTF-8"))
            buffer.write(query)
            buffer.flush()
            buffer.close()
            connect.connect()
            
            val resultCode = connect.responseCode
            if (resultCode == HttpURLConnection.HTTP_OK) {
                //ket noi thanh cong
                //lay du lieu tra ve
                val input: InputStream = connect.inputStream
                val buffer: BufferedReader = BufferedReader(InputStreamReader(input))
                //0:khong hop le,1:co hop le
                val result = buffer.readLine()
                if (result == "true") {
                    handlers.sendMessage(Message().apply { obj = "$add_account_success:insert tai khoan." })
                } else if (result == "false") {
                    handlers.sendMessage(Message().apply { obj = "$add_account_fail:insert tai khoan." })
                }
            } else {
                handlers.sendMessage(Message().apply { obj = "ket noi that bai:insert tai khoan." })
            }
        } catch (e: Exception) {
            handlers.sendMessage(Message().apply { what = 2 })
            Log.e("insert tai khoan", e.toString())
        }
    }
    
    inner class handler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Log.e("LOG:", msg?.obj.toString())
            //toast(msg?.obj.toString(), ToastMode.INFO)
            if (msg?.what == 1) {
                updateList()
            } else if (msg?.what == 2) {
                showConnectStateView()
            }
        }
    }
    
    private fun showConnectStateView() {
        //khoi tao 2 anim
        val animShow: Animation = AnimationUtils.loadAnimation(this, R.anim.show_connect_state)
        val animHide: Animation = AnimationUtils.loadAnimation(this, R.anim.hide_connect_state)
        //dau tien cho state hien ra
        txtConnectState.visibility = View.VISIBLE
        //start anim show (tu trai qua phai)
        txtConnectState.startAnimation(animShow)
        //set listener, neu ket thuc anim show thi se doi 5 giay sau start animHide
        animShow.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }
            
            override fun onAnimationEnd(animation: Animation?) {
                val coutDown: CountDownTimer = object : CountDownTimer(5000, 1000) {
                    override fun onFinish() {
                        //khi ket thuc thi se hide stateView
                        txtConnectState.startAnimation(animHide)
                    }
                    
                    override fun onTick(millisUntilFinished: Long) {
                        //
                    }
                    
                }
                coutDown.start()
            }
            
            override fun onAnimationStart(animation: Animation?) {
            }
        })
        //neu chay xong animHide thi se hide stateView
        animHide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }
            
            override fun onAnimationEnd(animation: Animation?) {
                txtConnectState.visibility = View.GONE
            }
            
            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }
}
