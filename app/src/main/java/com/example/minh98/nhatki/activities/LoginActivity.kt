package com.example.minh98.nhatki.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.minh98.nhatki.R
import com.example.minh98.nhatki.database.SharePreference
import com.example.minh98.nhatki.database.sqlite
import com.example.minh98.nhatki.enums.ToastMode
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    
    
    lateinit var editUserName: EditText
    lateinit var editUserPass: EditText
    lateinit var btnLogin: Button
    lateinit var cbLoginOnline: CheckBox
    var username: String = ""
    var userpass: String = ""
    val handlers: Handler = handler()
    var loginCondition: Boolean = false
    //it-yeuits.rhcloud.com/
    //192.168.43.52/nhatki/
    val host: String = "http://it-yeuits.rhcloud.com/"
    val checkTaiKhoan = "checkTaiKhoan.php"
    val getThongTinTaiKhoan = "getThongTinTaiKhoan.php"
    val sharePreference: SharePreference by lazy {
        SharePreference(this)
    }
    val sqlite: sqlite by lazy {
        sqlite(this, "NhatKi.sqlite", null, 1)
    }
    val txtRegister: TextView by lazy {
        findViewById(R.id.txtRegister) as TextView
    }
    val save_info_success: String by lazy { getString(R.string.save_info_success) }
    val save_info_fail: String by lazy { getString(R.string.save_info_fail) }
    val info_empty: String by lazy { getString(R.string.info_empty) }
    val login_success: String by lazy { getString(R.string.login_success) }
    val login_fail: String by lazy { getString(R.string.login_fail) }
    val cant_save_sharepreference: String by lazy { getString(R.string.cant_save_sharepreference) }
    val not_exists_username: String by lazy { getString(R.string.not_exists_username) }
    val exists_username: String by lazy { getString(R.string.exists_username) }
    val pass_empty: String by lazy { getString(R.string.pass_empty) }
    val pass_wrong: String by lazy { getString(R.string.pass_wrong) }
    val continue_use_account: String by lazy { getString(R.string.continue_use_account) }
    val ask_exit_app: String by lazy { getString(R.string.ask_exit_app) }
    val login_offline: String by lazy { getString(R.string.login_offline) }
    
    fun initView() {
        editUserName = findViewById(R.id.editUserName) as EditText
        editUserPass = findViewById(R.id.editUserPass) as EditText
        btnLogin = findViewById(R.id.btnLogin) as Button
        cbLoginOnline = findViewById(R.id.cb_login_online) as CheckBox
        if (!isConnected()) {
            cbLoginOnline.isEnabled = false
            cbLoginOnline.text = login_offline
        }
        btnLogin.setOnClickListener { actionLogin() }
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
    
    private fun isConnected(): Boolean {
        val connectManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED
                || connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED
    }
    
    private fun actionLogin() {
        username = editUserName.text.toString().replace("'", "").trim()
        userpass = editUserPass.text.toString().replace("'", "").trim()
        when {
            username.isEmpty() -> {
                editUserName.error = info_empty
                return
            }
            userpass.isEmpty() -> {
                editUserPass.error = info_empty
                return
            }
        }
        
        
        if (cbLoginOnline.isChecked) {
            getTKFromServer(username, userpass) //tien hanh dang nhap online
        } else {
            //dang nhap offline, truy xuat sqlite
            loginCondition = login(username, userpass)
            if (loginCondition) {
                
                //save tai khoan vao sharepreference de lan dang nhap sau co the login tiep
                if (!sharePreference.set("username", username)) {
                    //toast(cant_save_sharepreference, ToastMode.WARNING)
                    Log.e("luu tai khoan", cant_save_sharepreference)
                } else {
                    //toast(save_info_success, ToastMode.SUCCESS)
                    Log.e("luu tai khoan", save_info_success)
                }
                
                toast(login_success, ToastMode.SUCCESS)
                //truyen 2 tham so la user,pass cho mainactivity su dung
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra("username", username)
                    putExtra("userpass", userpass)
                })
                finish()
            } else {
                toast(login_fail, ToastMode.ERROR)
            }
        }
        
        
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        
        
        initView()
        checkAccount()
    }
    
    
    private fun checkAccount() {
        val username = sharePreference.get("username", "").replace("'", "").trim()
        if (username == "") {
            //chua ton tai username, khong lam gi ca
            //toast(not_exists_username, ToastMode.INFO)
            Log.e("check account", not_exists_username)
        } else {
            //co ton tai username, show dialog hoi co muon dang nhap tiep khong
            //toast(exists_username, ToastMode.INFO)
            Log.e("check account", exists_username)
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)
            
            val v: View = layoutInflater.inflate(R.layout.layout_dialog_login, null)
            val txtTitle = v.findViewById(R.id.txtTitle) as TextView
            val editUserPass = v.findViewById(R.id.editUserPass) as EditText
            val btnChangeAccount = v.findViewById(R.id.btnChangeAccount) as Button
            val btnOk = v.findViewById(R.id.btnOk) as Button
            
            
            txtTitle.text = "$continue_use_account: $username"
            
            
            val alertdialog: AlertDialog
            //set properties
            alert.setView(v)
            alert.setCancelable(false)
            alertdialog = alert.create()
            
            btnChangeAccount.setOnClickListener {
                alertdialog?.dismiss()
                sharePreference.set("username", "")//reset username
            }
            
            btnOk.setOnClickListener {
                //tien hanh dang nhap offline , truy xuat sqlite
                val userpass = editUserPass.text.toString().replace("'", "").trim()
                if (userpass.isEmpty()) {
                    editUserPass.error = pass_empty
                    return@setOnClickListener
                } else {
                    if (login(username, userpass)) {
                        toast(login_success, ToastMode.SUCCESS)
                        
                        alertdialog.dismiss()
                        val it = Intent(this@LoginActivity, MainActivity::class.java)
                        //truyen user,pass cho mainactivity va thoat activity hien tai
                        it.putExtra("username", username)
                        it.putExtra("userpass", userpass)
                        startActivity(it)
                        finish()
                    } else {
                        editUserPass.error = pass_wrong
                        toast(login_fail, ToastMode.ERROR)
                        
                    }
                }
            }
            alertdialog.show()
        }
        
    }
    
    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
        with(alert) {
            setTitle("Thông báo")
            setMessage(ask_exit_app)
            setPositiveButton("Thoát", { _, _ -> super.onBackPressed() })
            setNegativeButton("Cancel", { dialog, _ -> dialog.dismiss() })
            create().show()
            
        }
    }
    
    private fun login(username: String, userpass: String): Boolean {
        return sqlite.get1TK(username, userpass).moveToNext()
    }
    
    private fun getTKFromServer(username: String, userpass: String) {
        xuly().execute(username, userpass)
    }
    
    inner class xuly : AsyncTask<String, Unit, Unit>() {
        override fun doInBackground(vararg params: String?) {
            checkTaiKhoanFromServer(params[0]!!, params[1]!!)
            getThongTinTaiKhoanFromServer(params[0]!!, params[1]!!)
        }
        
    }
    
    private fun checkTaiKhoanFromServer(taikhoan: String, matkhau: String) {
        try {
            val url: URL = URL(host + checkTaiKhoan)//link get1tk
            val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
            val taiKhoan = taikhoan
            val matKhau = matkhau
            
            
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
                val result = buffer.readLine()
                Log.e("getTKfromserver:", result)
                if (result == "true") {
                    handlers.sendMessage(Message().apply { obj = "true" })
                } else if (result == "false") {
                    handlers.sendMessage(Message().apply { obj = "false" })
                } else if (result == "sai") {
                    handlers.sendMessage(Message().apply { obj = "sai" })
                }
            } else {
                handlers.sendMessage(Message().apply { obj = "loi ket noi: get tai khoan from server" })
            }
            
        } catch (e: Exception) {
            handlers.sendMessage(Message().apply { obj = "Ket noi that bai" })
            Log.e("gettk from server", e.toString())
        }
    }
    
    private fun getThongTinTaiKhoanFromServer(taikhoan: String, matkhau: String) {
        try {
            val url: URL = URL(host + getThongTinTaiKhoan)//link get1tk
            val connect: HttpURLConnection = url.openConnection() as HttpURLConnection
            val taiKhoan = taikhoan
            val matkhau = matkhau
            with(connect) {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "POST"
            }
            val query: String = Uri.Builder()
                    .appendQueryParameter("TaiKhoan", taiKhoan)
                    .appendQueryParameter("MatKhau", matkhau)
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
                Log.e("getthongtintaikhoan:", result)
                if (result == "false") {
                    //khong thanh cong hoac khong co nhat ki
                    Log.e("getthongtintaikhoan", "ket qua ket noi server get thong tin tai khoan: false")
                } else {
                    //thanh cong// result tra veobject json
                    val jsonObject: JSONObject = JSONObject(result) //get jsonObject
                    
                    val TaiKhoan: String = jsonObject.getString("TaiKhoan")
                    val MatKhau: String = jsonObject.getString("MatKhau")
                    val TenHienThi: String = jsonObject.getString("TenHienThi")
                    val GioiTinh: String = jsonObject.getString("GioiTinh")
                    val NamSinh: String = jsonObject.getString("NamSinh")
                    val QueQuan: String = jsonObject.getString("QueQuan")
                    val SoThich: String = jsonObject.getString("SoThich")
                    sqlite.insertThongTinTaiKhoan(TaiKhoan, MatKhau, TenHienThi, GioiTinh, NamSinh, QueQuan, SoThich)
                    Log.e("getthongtintaikhoan", "them thong tin tai khoan thanh cong")
                }
            } else {
                Log.e("getthongtintaikhoan", "khong the ket noi")
            }
        } catch (e: Exception) {
            Log.e("getthongtintaikhoan", e.toString())
        }
    }
    
    inner class handler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val result = msg?.obj.toString()
            if (result == "true"
                    || result == "false"
                    || result == "sai") {
                
                loginCondition = result == "true"
                if (loginCondition) {
                    actionRegister(username, userpass)
                    toast(login_success, ToastMode.SUCCESS)
                    
                    if (!sharePreference.set("username", username)) {
                        //toast(cant_save_sharepreference, ToastMode.WARNING)
                        Log.e("luu thong tin", cant_save_sharepreference)
                    } else {
                        //toast(save_info_success, ToastMode.SUCCESS)
                        Log.e("luu thong tin", save_info_success)
                    }
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("username", username)
                        putExtra("userpass", userpass)
                    })
                    finish()
                } else {
                    toast(login_fail, ToastMode.ERROR)
                }
            }
            
        }
    }
    
    private fun actionRegister(username: String, userpass: String) {
        //register offline,truy xuat sqlite
        if (!sqlite.check1TK(username)) {
            //neu nhu chua ton tai tai khoan trong sqlite
            if (sqlite.insertTK(username, userpass)
                    ) { //thanh cong
                
                //toast(register_account_success, ToastMode.SUCCESS)
                Log.e("login", "register_account_success")
//                startActivity(Intent(this, MainActivity::class.java).apply {
//                    putExtra("username",username)
//                    putExtra("userpass",userpass)
//                })
//                finish()
            } else {
                //toast(register_account_fail, ToastMode.ERROR)
                Log.e("login", "register_account_fail")
            }
        } else {
            //toast(accout_exists, ToastMode.INFO)
            Log.e("login", "account_exists")
        }
        
    }
    
}
