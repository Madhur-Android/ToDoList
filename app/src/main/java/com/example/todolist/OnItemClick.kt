package com.example.todolist

import android.view.View

interface OnItemClick {

    fun onItemClick(v: View, position: Int)
}