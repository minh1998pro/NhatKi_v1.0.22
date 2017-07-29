package com.example.minh98.nhatki.database

import android.content.Context

/**
 * Created by minh98 on 22/07/2017.
 */
class SharePreference(val context: Context){
    val preference by lazy {
        context.getSharedPreferences("default",Context.MODE_PRIVATE)
    }
    fun<T>set(key:String,value:T):Boolean{
        try{
            with(preference.edit()){
                when(value){
                    is Boolean ->putBoolean(key,value)
                    is Int ->putInt(key,value)
                    is Float ->putFloat(key,value)
                    is String ->putString(key,value)
                    is Long ->putLong(key,value)
                    else ->return false
                }
            }.commit()
            return true
        }catch (e:Exception){
            return false
        }
    }
    fun<T>get(key:String,default:T)
            :T= with(preference){
            val res:Any=when(default){
                is Boolean ->getBoolean(key,default)
                is Int ->getInt(key,default)
                is Float ->getFloat(key,default)
                is String ->getString(key,default)
                is Long ->getLong(key,default)
                else ->return default
            }
            res as T
        }
        
    
    
    
    
    
    
    
}