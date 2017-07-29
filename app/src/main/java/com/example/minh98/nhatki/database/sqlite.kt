package com.example.minh98.nhatki.database

import android.content.Context
import android.database.Cursor
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by minh98 on 22/07/2017.
 */
class sqlite : SQLiteOpenHelper {
    
    
    lateinit var database: SQLiteDatabase
    
    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int)
            : super(context, name, factory, version) {
        
        //quera tao bao du lieu
        queryData("""CREATE  TABLE  IF NOT EXISTS "TaiKhoan"
        ("TaiKhoan" TEXT PRIMARY KEY  NOT NULL
        , "MatKhau" TEXT NOT NULL )""")
        
        queryData("""CREATE  TABLE  IF NOT EXISTS "ThongTinTaiKhoan"
        ("TaiKhoan" TEXT PRIMARY KEY  NOT NULL
        , "MatKhau" TEXT NOT NULL
        , "TenHienThi" TEXT
        , "GioiTinh" TEXT
        , "NamSinh" TEXT
        , "QueQuan" TEXT
        , "SoThich" TEXT)""")
        queryData("""CREATE  TABLE  IF NOT EXISTS "NhatKi"
        ("TieuDe" TEXT
        , "NoiDung" TEXT
        , "ThoiGian" TEXT PRIMARY KEY  NOT NULL
        , "TaiKhoanUser" TEXT NOT NULL )""")
    }
    
    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory, version: Int, errorHandler: DatabaseErrorHandler) : super(context, name, factory, version, errorHandler) {}
    
    fun insertTK(tk: String, pass: String): Boolean {
        if (!get1TK(tk, pass).moveToNext()) {
            //neu nhu chua co tai khoan nay
            try {
                queryData("INSERT INTO `TaiKhoan` VALUES('$tk','$pass')")
                insertThongTinTaiKhoan(tk, pass,null ,null, null, null, null)
                return true
            } catch (e: Exception) {
                return false
            }
        } else {
            //neu da ton tai tai khoan nay
            return false
        }
    }
    
    fun insertNhatKi(tieude: String, noidung: String, thoigian: String, taikhoanuser: String): Boolean {
        try {
            queryData("INSERT INTO `NhatKi` VALUES('$tieude','$noidung','$thoigian','$taikhoanuser')")
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    fun getNhatKiByTaiKhoanUser(taikhoanuser: String): Cursor {
        database = writableDatabase
        val s = "SELECT * FROM `NhatKi` WHERE `TaiKhoanUser`='$taikhoanuser' ORDER BY `ThoiGian`"
        val cursor = database.rawQuery(s, null)
        return cursor
    }
    
    fun updateTK(username: String, pass: String): Boolean {
        try {
            queryData("UPDATE `TaiKhoan` SET `MatKhau`='$pass' WHERE `TaiKhoan`='$username'")
            queryData("""UPDATE `ThongTinTaiKhoan` SET `MatKhau`='$pass' WHERE `TaiKhoan`='$username'""")
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    fun insertThongTinTaiKhoan(taikhoan: String, matkhau: String, tenhienthi: String?,gioitinh:String?, namsinh: String?, quequan: String?, sothich: String?) {
        queryData("""INSERT INTO `ThongTinTaiKhoan` VALUES('$taikhoan','$matkhau','$tenhienthi','$gioitinh','$namsinh','$quequan','$sothich')""")
    }
    
    fun updateThongTinTaiKhoan(taikhoan: String, matkhau: String, tenhienthi: String?,gioitinh: String?, namsinh: String?, quequan: String?, sothich: String?): Boolean {
        try {
            queryData("""UPDATE `ThongTinTaiKhoan`
        SET
        `MatKhau`='$matkhau'
        ,`TenHienThi`='$tenhienthi'
        ,`GioiTinh`='$gioitinh'
        ,`NamSinh`='$namsinh'
        ,`QueQuan`='$quequan'
        ,`SoThich`='$sothich'
        WHERE `TaiKhoan`='$taikhoan'""")
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    
    fun queryData(s: String) {
        database = writableDatabase
        database.execSQL(s)
    }
    
    fun get1ThongTinByTaiKhoan(taikhoan: String): Cursor {
        database = writableDatabase
        val s = "SELECT * FROM `ThongTinTaiKhoan` WHERE `TaiKhoan`='$taikhoan'"
        val cursor = database.rawQuery(s, null)
        return cursor
    }
    
    fun get1TK(tk: String, pass: String): Cursor {
        database = writableDatabase
        val s = "SELECT * FROM `TaiKhoan` WHERE `TaiKhoan`='$tk' AND `MatKhau`='$pass'"
        
        val cursor = database.rawQuery(s, null)
        return cursor
    }
    
    fun check1TK(tk: String): Boolean {
        database = writableDatabase
        val s = "SELECT * FROM `TaiKhoan` WHERE `TaiKhoan`='$tk'"
        val cursor = database.rawQuery(s, null)
        return cursor.moveToNext()
    }
    
    fun getThongTinTaiKhoan(s: String): Cursor {
        database = writableDatabase
        val cursor = database.rawQuery(s, null)
        return cursor
    }
    
    fun getData(s: String): Cursor {
        database = writableDatabase
        val cursor = database.rawQuery(s, null)
        
        return cursor
        
    }
    
    override fun onCreate(db: SQLiteDatabase) {}
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        
    }
}