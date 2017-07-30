package com.example.minh98.diary.register.model

import android.content.Context

import com.example.minh98.diary.model.database.sqlite
import com.example.minh98.diary.register.presenter.IpresenterRegister
import com.example.minh98.diary.register.view.RegisterActivity

/**
 * Created by minh98 on 30/07/2017.
 */
class modelRegister(val presenter: IpresenterRegister, context: RegisterActivity) {
    val context: Context = context
    
    val sqlite: sqlite by lazy {
        sqlite(context, "Diary.sqlite", null, 1)
    }
    
    fun actionRegister(username: String, userpass: String) {
        //register offline,truy xuat sqlite
        if (!sqlite.check1TK(username)) {
            //neu nhu chua ton tai tai khoan trong sqlite
            if (sqlite.insertTK(username, userpass)) { //thanh cong
                
                presenter.registerAccountSuccess(username, userpass)
                
            } else {
                presenter.registerAccountFail()
            }
        } else {
            presenter.registerAccountExists()
        }
    }
    
}