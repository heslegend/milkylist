package com.lazyambitions.milkylist.ui.main

import android.arch.lifecycle.ViewModel
import android.util.Log
import bolts.Bolts
import com.example.shoppinglist.services.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener as ValueEventListener1


class MainViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val myDatabase: DatabaseReference = database.getReference("server/saving-data/milkylist")
    private val userReference = myDatabase.child("users/YGFAEH7exRgWoRfM4lROKHfMW1O2")
    private val listReference = userReference.child("list")


    var items : ArrayList <Item> = arrayListOf ()
    var listName : String = "milkylist"

    fun writeItem(itemName: String){
        val key = listReference.push().key

        if (key != null) {
            val item = Item(key, itemName, false)
            listReference.child(key).setValue(item)
        }
    }

    fun removeItem(item: Item) {
        val key = item.key
        listReference.child(key!!).removeValue()
    }

    fun changeItem(item: Item, selectionStatus : Boolean) {
        val key = item.key
        item.isSelected = selectionStatus
        listReference.child(key!!).setValue(item)
    }

    fun clearList(): Boolean {
        listReference.removeValue()
        return true
    }

    fun removeSelected() : Boolean {
        for (item in items) {
            if (item.isSelected) {
                val key = item.key
                listReference.child(key!!).removeValue()
            }
        }
        return true
    }

    fun setListName(name : String) : Boolean {
        listName = name
        return true
    }

}
