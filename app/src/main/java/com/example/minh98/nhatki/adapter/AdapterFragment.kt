package com.example.minh98.nhatki.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.minh98.nhatki.fragment.NhatKiFragment

/**
 * Created by minh98 on 23/07/2017.
 */
class AdapterFragment(fm: FragmentManager?)
    : FragmentPagerAdapter(fm) {
    var TaiKhoanUser:String=""
    
    override fun getItem(position: Int): Fragment {
        return NhatKiFragment.newInstance(position,TaiKhoanUser)
    }
    
    override fun getCount(): Int {
        return 3
    }
    
    override fun getPageTitle(position: Int): CharSequence {
        return when(position){
            0->"Trong Ngày"
            1->"Trong Tuần"
            2->"Tất Cả"
            else->"Trong Ngày"
        }
    }
}