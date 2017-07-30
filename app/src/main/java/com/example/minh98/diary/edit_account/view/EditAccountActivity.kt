package com.example.minh98.diary.edit_account.view

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.*
import com.example.minh98.diary.R
import com.example.minh98.diary.enums.ToastMode
import com.example.minh98.diary.model.database.sqlite
import es.dmoral.toasty.Toasty

class EditAccountActivity : AppCompatActivity() {
    lateinit var txtTaiKhoan: TextView
    lateinit var editMatKhau: EditText
    lateinit var editTenHienThi: EditText
    lateinit var editNamSinh: EditText
    lateinit var editQueQuan: EditText
    lateinit var editSoThich: EditText
    lateinit var btnNam: RadioButton
    lateinit var btnNu: RadioButton
    lateinit var btnEditAccount: Button
    lateinit var toolbar: Toolbar
    var TaiKhoan: String = ""
    var MatKhau: String = ""
    var TenHienThi: String? = ""
    var GioiTinh: String? = ""
    var NamSinh: String? = ""
    var QueQuan: String? = ""
    var SoThich: String? = ""
    val sqlite: sqlite by lazy {
        sqlite(this, "Diary.sqlite", null, 1)
    }
    val change_info_success: String by lazy { getString(R.string.change_info_success) }
    val change_info_fail: String by lazy { getString(R.string.change_info_fail) }
    val info_account: String by lazy { getString(R.string.info_account) }
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
        
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        
        
        initView()
        TaiKhoan = intent.getStringExtra("TaiKhoan")
        Log.e("get taikhoan edit", TaiKhoan)
        //lay thong tin tai khoan tu sqlite
        val cursor = sqlite.get1ThongTinByTaiKhoan(TaiKhoan)
        while (cursor.moveToNext()) {
            txtTaiKhoan.text = "Tài Khoản:" + cursor.getString(0)
            editMatKhau.setText(cursor.getString(1))
            editTenHienThi.setText(cursor.getString(2))
            if(cursor.getString(3) == "Nam"){
                btnNam.isChecked =true
            }else{
                btnNu.isChecked =true
            }
            editNamSinh.setText(cursor.getString(4))
            editQueQuan.setText(cursor.getString(5))
            editSoThich.setText(cursor.getString(6))
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    
    fun initView() {
        txtTaiKhoan = findViewById(R.id.txtTaiKhoan) as TextView
        editMatKhau = findViewById(R.id.editMatKhau) as EditText
        editTenHienThi = findViewById(R.id.editTenHienThi) as EditText
        editNamSinh = findViewById(R.id.editNamSinh) as EditText
        editQueQuan = findViewById(R.id.editQueQuan) as EditText
        editSoThich = findViewById(R.id.editSoThich) as EditText
        btnNam = findViewById(R.id.btnNam) as RadioButton
        btnNu = findViewById(R.id.btnNu) as RadioButton
        btnEditAccount = findViewById(R.id.btnEditAccount) as Button
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = info_account
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setSubtitleTextColor(Color.WHITE)
        
        btnEditAccount.setOnClickListener {
            TaiKhoan = txtTaiKhoan.text.toString().trim().split(":")[1].replace("'", "") //format TaiKhoan:user
            MatKhau = editMatKhau.text.toString().trim().replace("'", "")
            TenHienThi = editTenHienThi.text.toString().trim().replace("'", "")
            GioiTinh = if (btnNam.isChecked) "Nam" else "Nu"
            NamSinh = editNamSinh.text.toString().trim().replace("'", "")
            QueQuan = editQueQuan.text.toString().trim().replace("'", "")
            SoThich = editSoThich.text.toString().trim().replace("'", "")
            
            if (sqlite.updateThongTinTaiKhoan(TaiKhoan, MatKhau, TenHienThi, GioiTinh, NamSinh, QueQuan, SoThich)
                    && sqlite.updateTK(TaiKhoan, MatKhau)) {
                toast(change_info_success, ToastMode.SUCCESS)
                setResult(0)
                finish()
            } else {
                toast(change_info_fail, ToastMode.ERROR)
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
}
