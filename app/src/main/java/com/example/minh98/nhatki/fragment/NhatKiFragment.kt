package com.example.minh98.nhatki.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.minh98.nhatki.R
import com.example.minh98.nhatki.database.SharePreference
import com.example.minh98.nhatki.activities.MainActivity
import com.example.minh98.nhatki.adapter.AdapterRecyclerView
import com.example.minh98.nhatki.listener.fragmentListener
import com.example.minh98.nhatki.database.sqlite
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
data class NhatKi(var tieuDe:String,var noiDung:String,var time:String)

class NhatKiFragment : Fragment(),fragmentListener {
    var number:Int=0
    var TaiKhoanUser:String=""
    var adapterRecyclerView:AdapterRecyclerView?=null
    val sqlite: sqlite by lazy {
        sqlite(context, "NhatKi.sqlite", null, 1)
    }
    val sharepreference: SharePreference by lazy {
        SharePreference(context)
    }
    val date_format:String by lazy { getString(R.string.date_format) }
    override fun updateNhatKi() {
        initItemNhatKis(number)
    }
    
    val itemNhatKis= mutableListOf<NhatKi>()

    companion object {
        fun newInstance(sectionNumber: Int,TaiKhoanUser:String): NhatKiFragment {
            val fragment = NhatKiFragment()
            val args = Bundle()
            args.putInt("number", sectionNumber)
            args.putString("TaiKhoanUser",TaiKhoanUser)
            fragment.arguments=args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        this.number=arguments.getInt("number")
        this.TaiKhoanUser=arguments.getString("TaiKhoanUser")
        val intMode=sharepreference.get("layoutMode",0)
        initItemNhatKis(number)
        val view:View=inflater!!.inflate(R.layout.fragment_nhat_ki, container, false)
        val list:RecyclerView= view.findViewById(R.id.list) as RecyclerView
        (activity as MainActivity).initListener(this)
        adapterRecyclerView = AdapterRecyclerView(itemNhatKis)
        
        with(list){
            setHasFixedSize(true)
            itemAnimator=DefaultItemAnimator()
            if(intMode==1){
                layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            }else if(intMode==0){
                layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            }
            //
            //addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            adapter=adapterRecyclerView
            if(itemNhatKis.size>0){
                smoothScrollToPosition(0)
            } //scroll len nhat ki dau tien (nhat ki moi nhat)
        }
        
        return view
    }
    
    private fun  initItemNhatKis(int: Int) {
        itemNhatKis.clear() //lam moi lai danh sach nhat ki
        with(itemNhatKis){
            val cursor=sqlite.getNhatKiByTaiKhoanUser(TaiKhoanUser)
            while(cursor.moveToNext()){
                val tieude=cursor.getString(0)
                val noidung=cursor.getString(1)
                val thoigianL=cursor.getString(2).toLong()
                val dateFormat=SimpleDateFormat(date_format, Locale("vi"))
                val thoigian=dateFormat.format(Date(thoigianL))
                val timenow=System.currentTimeMillis()
                /**
                 * tuy tung dieu kien se add cac nhat ki thich hop
                 */
                if(int==0){
                    //add trong ngay
                    
                    if(timenow-thoigianL<24*60*60*1000){
                        add(0,NhatKi(tieude,noidung,thoigian))
                    }
                }else if(int ==1){
                    //add trong tuan
                    if(timenow-thoigianL<24*60*60*1000*7){
                        add(0,NhatKi(tieude,noidung,thoigian))
                    }
                }else{
                    //add tat ca
                    add(0,NhatKi(tieude,noidung,thoigian))
                }
            }
        }
        
        adapterRecyclerView?.notifyDataSetChanged()
    }
    
}// Required empty public constructor
