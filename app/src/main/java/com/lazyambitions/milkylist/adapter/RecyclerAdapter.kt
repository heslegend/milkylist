package com.example.shoppinglist.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.example.shoppinglist.services.Item
import com.google.firebase.database.core.view.Change
import com.lazyambitions.milkylist.R
import kotlinx.android.synthetic.main.shopping_list_item.view.*
import kotlin.math.absoluteValue


class RecyclerAdapter(private val items: ArrayList<Item>,
                      private val context: Context?, val onRemoveItem : (Item) -> Unit, val onChangeItem: (Item, Boolean) -> Unit)
    : RecyclerView.Adapter <RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shopping_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textSingleItem.text = items[position].itemName
        setStrikedThrough(holder, items[position].isSelected)
        holder.checkBoxSingleItem.isChecked = items[position].isSelected
        holder.checkBoxSingleItem.setOnClickListener {setCheckBoxState(items[position])
        }
        holder.textSingleItem.setOnLongClickListener { createDeleteDialog(items[position])}
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textSingleItem: TextView = itemView.textViewSingleItem
        var checkBoxSingleItem: CheckBox = itemView.checkBoxSingleItem


    }

    private fun setCheckBoxState(item: Item ) {
         if (!item.isSelected) {
            onChangeItem(item, true)
        } else {
            onChangeItem(item, false)
        }
    }

    private fun setStrikedThrough(holder: ViewHolder, isSelected : Boolean) {
        if (isSelected) {
            holder.textSingleItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.textSingleItem.setTextColor(Color.parseColor("#ff80cbc4"))
            holder.textSingleItem.textSize = 14.0F
        } else {
            holder.textSingleItem.paintFlags = 0
            holder.textSingleItem.setTextColor(Color.parseColor("#b3ffffff"))
            holder.textSingleItem.textSize = 16.0F
        }
    }

    fun createDeleteDialog(item: Item) : Boolean{
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Delete Item?")
        builder.setPositiveButton("OK") { _, _ -> onRemoveItem(item)}
        builder.setNegativeButton("CANCEL") { _, _ ->
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show() }
        val dialog = builder.create()

        dialog.show()
        return true
    }
}


