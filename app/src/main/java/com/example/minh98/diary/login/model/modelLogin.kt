package com.example.minh98.diary.login.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.minh98.diary.R
import com.example.minh98.diary.login.enums.str
import com.example.minh98.diary.login.presenter.IpresenterLogin
import com.example.minh98.diary.model.database.sqlite
import com.example.minh98.diary.model.sharepreference.SharePreference
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by minh98 on 30/07/2017.
 */
class modelLogin(presenter: IpresenterLogin, context: Context) {
    val presenter: IpresenterLogin = presenter
    val context: Context = context
    //it-yeuits.rhcloud.com/
    //192.168.43.52/nhatki/
    val host: String = "http://192.168.43.52/nhatki/"
    val checkTaiKhoan = "checkTaiKhoan.php"
    val getThongTinTaiKhoan = "getThongTinTaiKhoan.php"
    val handlers: Handler = handler()
    
    var name: String = ""
    var pass: String = ""
    
    
    val save_info_success: String by lazy { context.getString(R.string.save_info_success) }
    val save_info_fail: String by lazy { context.getString(R.string.save_info_fail) }
    val info_empty: String by lazy { context.getString(R.string.info_empty) }
    val login_success: String by lazy { context.getString(R.string.login_success) }
    val login_fail: String by lazy { context.getString(R.string.login_fail) }
    val cant_save_sharepreference: String by lazy { context.getString(R.string.cant_save_sharepreference) }
    val not_exists_username: String by lazy { context.getString(R.string.not_exists_username) }
    val exists_username: String by lazy { context.getString(R.string.exists_username) }
    val pass_empty: String by lazy { context.getString(R.string.pass_empty) }
    val pass_wrong: String by lazy { context.getString(R.string.pass_wrong) }
    val continue_use_account: String by lazy { context.getString(R.string.continue_use_account) }
    val ask_exit_app: String by lazy { context.getString(R.string.ask_exit_app) }
    val login_offline: String by lazy { context.getString(R.string.login_offline) }
    val name_empty: String by lazy { context.getString(R.string.username_empty) }
    
    
    val sharePreference: SharePreference by lazy {
        SharePreference(context)
    }
    val sqlite: sqlite by lazy {
        sqlite(context, "Diary.sqlite", null, 1)
    }
    
    fun isConnected(): Boolean {
        val connectManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED
                || connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED
        
    }
    
    fun getUsername(): String {
        return sharePreference.get("username", "")
    }
    
    fun login(username: String, userpass: String): Boolean {
        return sqlite.get1TK(username, userpass).moveToNext()
    }
    
    fun setUsername(s: String) {
        sharePreference.set("username", s)
    }
    
    fun set(s: String, username: String): Boolean {
        return sharePreference.set(s, username)
    }
    
    fun getTKFromServer(username: String, userpass: String) {
        name = username
        pass = userpass
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
    
    fun getString(enums: str): String {
        return when (enums) {
            str.ask_exit_app -> ask_exit_app
            str.cant_save_sharepreference -> cant_save_sharepreference
            str.continue_use_account -> continue_use_account
            str.exists_username -> exists_username
            str.info_empty -> info_empty
            str.login_fail -> login_fail
            str.login_offline -> login_offline
            str.login_success -> login_success
            str.not_exists_username -> not_exists_username
            str.pass_empty -> pass_empty
            str.pass_wrong -> pass_wrong
            str.save_info_fail -> save_info_fail
            str.save_info_success -> save_info_success
            str.name_empty -> name_empty
        }
    }
    
    inner class handler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val result = msg?.obj.toString()
            if (result == "true"
                    || result == "false"
                    || result == "sai") {
                
                val loginCondition = result == "true"
                presenter.setLoginConditions(loginCondition)
                if (loginCondition) {
                    actionRegister(name, pass)
                    presenter.loginSuccess()
                    
                    if (!sharePreference.set("username", name)) {
                        //toast(cant_save_sharepreference, ToastMode.WARNING)
                        Log.e("luu thong tin", cant_save_sharepreference)
                    } else {
                        //toast(save_info_success, ToastMode.SUCCESS)
                        Log.e("luu thong tin", save_info_success)
                    }
                    presenter.startMain(name, pass)
                } else {
                    presenter.loginFail()
                }
            }
            
        }
    }
    
    private fun actionRegister(username: String, userpass: String) {
        //register offline,truy xuat sqlite
        if (!sqlite.check1TK(username)) {
            //neu nhu chua ton tai tai khoan trong sqlite
            if (sqlite.insertTK(username, userpass)) {
                Log.e("login", "register_account_success")
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