package com.example.minh98.diary.register.presenter

/**
 * Created by minh98 on 30/07/2017.
 */
interface IpresenterRegister {
    fun registerAccountSuccess(username: String, userpass: String)
    fun registerAccountFail()
    
    fun registerAccountExists()
}