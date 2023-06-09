package com.example.mealmenu.ui.customdialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.example.mealmenu.MainActivity.Companion.ListStatus
import com.example.mealmenu.ui.viewmodel.GenericViewModel
import com.example.mealmenu.databinding.EditOptionsBinding
import com.example.mealmenu.databinding.MenueditoptionBinding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EditItemsDialog(context: Context, viewModel: GenericViewModel, item: String, fromwhere :String, arg1:String?, arg2 :String?) : Dialog(context) {
    private lateinit var binding : EditOptionsBinding
    private lateinit var menueditbinding : MenueditoptionBinding
    private var listener: OnEditItemSelectedListener? = null
    init {
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
        window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding = EditOptionsBinding.inflate(layoutInflater)
        menueditbinding = MenueditoptionBinding.inflate(layoutInflater)

        if (fromwhere == "FromMenu")
        {
               setContentView(menueditbinding.root)
            menueditbinding.title.text = item
            menueditbinding.delete.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.deleteMenuItem(item,arg1!!,arg2!!)
                   listener!!.onEditItemSelected(item)
                }
                dismiss()
            }
        }
        else if (fromwhere =="FromList"){
            setContentView(binding.root)
            binding.title.text = item
            binding.edit.setOnClickListener {
                ListStatus =1
             //   val customAddDishesDialog =
               //     AddDishesDialog(context, viewModel, null, null, item, listener!!)
               // customAddDishesDialog.show()
               // dismiss()
                Log.d("Outside", "Edit")
            }
            binding.remove.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.deletedDishes(item)
                    listener!!.onEditItemSelected(item)
                }
                dismiss()
            }
        }
        else
        {
            setContentView(binding.root)
            binding.title.text = item
            binding.edit.setOnClickListener {
                ListStatus =0
               // val customAddDishesDialog =
                 //   AddDishesDialog(context, viewModel, null, null, item, listener!!)
               // customAddDishesDialog.show()
                //dismiss()
            }
            binding.remove.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.deleteTemplatebyTemplateName(item)
                    listener!!.onEditItemSelected(item)
                }
                dismiss()
            }
        }
    }


    interface OnEditItemSelectedListener {
        fun onEditItemSelected(item: String)
    }

    fun setOnEditItemSelectedListener(listener: OnEditItemSelectedListener) {
        this.listener = listener
    }

}