package com.example.minh98.diary.register.view

/**
 * Created by minh98 on 30/07/2017.
 */
interface viewRegister {
    fun etNameEmpty()
    
    fun etPassrordEmpty()
    
    fun etRePasswordEmpty()
    
    fun etRePasswordWrong()
    
    fun noticeExit()
    fun registerAccountSuccess(username: String, userpass: String)
    fun registerAccountFail()
    fun registerAccountExists()
}