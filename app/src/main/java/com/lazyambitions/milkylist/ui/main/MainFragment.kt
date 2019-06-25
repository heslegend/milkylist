package com.lazyambitions.milkylist.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.shoppinglist.services.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lazyambitions.milkylist.R
import kotlinx.android.synthetic.main.dialog_add_item.*
import kotlinx.android.synthetic.main.main_fragment.*
import com.example.shoppinglist.adapter.RecyclerAdapter as RecyclerAdapter

class MainFragment : Fragment(), ChildEventListener {

    interface MainActivityCallback{
        fun openLogin()
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    private val database = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val myDatabase: DatabaseReference = database.getReference("server/saving-data/milkylist")
    private val userReference = myDatabase.child("users/YGFAEH7exRgWoRfM4lROKHfMW1O2")
    private val listReference = userReference.child("list")

    var mainactivitycallback : MainActivityCallback? = null

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { return inflater.inflate(R.layout.main_fragment, container, false) }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        listReference.addChildEventListener(this)

        shoppingListView.layoutManager = LinearLayoutManager(this.activity)

        adapter = RecyclerAdapter(
            viewModel.items,
            this.activity,
            { item -> viewModel.removeItem(item)},
            { item, isSelected -> viewModel.changeItem(item, isSelected)})

        shoppingListView.adapter = adapter

        addButtonClick.setOnClickListener { onAddButtonClick(activity as Context) }


        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuClear: MenuItem? = menu?.findItem(R.id.buttonClearList)
        val menuDeleteCheckedItems = menu?.findItem(R.id.buttonDeleteItems)
        val menuLogout = menu?.findItem(R.id.buttonLogout)
        val menuListSettings = menu?.findItem(R.id.buttonListSettings)

        menuClear?.setOnMenuItemClickListener { viewModel.clearList() }
        menuDeleteCheckedItems?.setOnMenuItemClickListener { viewModel.removeSelected() }
        menuListSettings?.setOnMenuItemClickListener { createListSettingsDialog()}
        menuLogout?.setOnMenuItemClickListener { onLogoutButtonClick(context) }
    }


    private fun onAddButtonClick(context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_add_item)
        dialog.setCanceledOnTouchOutside(true)
        dialog.editTextItemInput.requestFocus()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.show()


        dialog.buttonAddItem.setOnClickListener {
            viewModel.writeItem(dialog.editTextItemInput.text.toString())
            dialog.dismiss()
        }
    }


    private fun onLogoutButtonClick(context: Context?) : Boolean {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show()
        mainactivitycallback?.openLogin()
        return FirebaseAuth.getInstance().currentUser == null
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainactivitycallback = context as MainActivityCallback
    }


    override fun onDetach() {
        super.onDetach()
        mainactivitycallback = null
        viewModel.items.clear()
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

        if (dataSnapshot.getValue(Item::class.java) != null) {
            val updatedItem = dataSnapshot.getValue(Item::class.java)!!

            if (updatedItem !in viewModel.items) {
                viewModel.items.add(updatedItem)
                adapter.notifyDataSetChanged()

            } else Log.d("dataSnapShot", "item = null")
        }
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
        if (dataSnapshot.getValue(Item::class.java) != null) {
            val changedChild: Item? = dataSnapshot.getValue(Item::class.java)

            val selectionStatus : Boolean = changedChild!!.isSelected
            val itemKey = changedChild.key



            for (item in viewModel.items)
                if (item.key == itemKey) {
                    item.isSelected = selectionStatus
                    adapter.notifyDataSetChanged()
                    return
                }
        }
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.getValue(Item::class.java) != null) {
            val removedChild: Item? = dataSnapshot.getValue(Item::class.java)
            val itemKey = removedChild!!.key

            for (item in viewModel.items)
                if (item.key == itemKey) {
                    viewModel.items.remove(item)
                    adapter.notifyDataSetChanged()
                    return
                }
            println("onChildRemoved")
        }
    }

    override fun onCancelled(p0: DatabaseError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createListSettingsDialog() : Boolean{
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Change List Title")
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK"){ _, _ ->  viewModel.setListName(input.text.toString())}
        builder.setNegativeButton("CANCEL") { _, _ ->  }
        val dialog = builder.create()

        dialog.show()
        return true
    }

}
