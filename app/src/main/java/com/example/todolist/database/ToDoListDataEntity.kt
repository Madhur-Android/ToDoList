package com.example.todolist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todolist")
data class ToDoListDataEntity (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "title" ) var title : String ="",
    @ColumnInfo(name = "date" ) var date : String="",
    @ColumnInfo(name = "time" ) var time : String="",
    @ColumnInfo(name = "isShow" ) var isShow : Int
)