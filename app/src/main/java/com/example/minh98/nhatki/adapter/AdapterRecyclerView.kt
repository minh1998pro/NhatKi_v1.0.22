package com.example.minh98.nhatki.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.minh98.nhatki.R
import com.example.minh98.nhatki.fragment.NhatKi

/**
 * Created by minh98 on 23/07/2017.
 */
class AdapterRecyclerView(var itemNhatKis: MutableList<NhatKi>)
    : RecyclerView.Adapter<AdapterRecyclerView.ItemViewHolder>() {
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val nt=itemNhatKis[position]
        holder.txtTieuDeNhatKi.text=nt.tieuDe
        holder.txtNoiDungNhatKi.text=nt.noiDung
        with(holder.txtNoiDungNhatKi){
            when(nt.noiDung.length){
                in 0..20-> setTextSize(60.0F)
                in 21..60 ->setTextSize(40.0F)
                else ->setTextSize(20.0F)
            }
        }
        holder.txtThoiGian.text=nt.time
    }
    
    override fun getItemCount(): Int {
        return itemNhatKis.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val layoutinflater=LayoutInflater.from(parent!!.context)
        val view=layoutinflater.inflate(R.layout.item_nhat_ki,parent,false)
        return ItemViewHolder(view)
    }
    
    
    
    class ItemViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        val txtTieuDeNhatKi: TextView = itemView.findViewById(R.id.txtTieuDeNhatKi) as TextView
        val txtNoiDungNhatKi:TextView= itemView.findViewById(R.id.txtNoiDungNhatKi) as TextView
        val txtThoiGian:TextView= itemView.findViewById(R.id.txtThoiGian) as TextView
        
    }
}