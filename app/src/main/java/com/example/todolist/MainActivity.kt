package com.example.todolist

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity(), OnItemClick {

    val list = mutableListOf<ToDoListData>()
    val c = Calendar.getInstance()
    val month: Int = c.get(Calendar.MONTH)
    val year: Int = c.get(Calendar.YEAR)
    val day: Int = c.get(Calendar.DAY_OF_MONTH)
    var cal = Calendar.getInstance()
    private val listAdapter = ListAdapter(list, this)
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ToDoListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[ToDoListViewModel::class.java]

        binding.rvTodoList.layoutManager = LinearLayoutManager(this)
        binding.rvTodoList.adapter = listAdapter
        binding.vieModel = viewModel
        viewModel.getPreviousList()

        viewModel.toDoList.observe(this, androidx.lifecycle.Observer {
            //list.addAll(it)
            if (it == null)
                return@Observer
            list.clear()
            val tempList = mutableListOf<ToDoListData>()
            it.forEach {
                tempList.add(
                    ToDoListData(
                        title = it.title,
                        date = it.date,
                        time = it.time,
                        indexDb = it.id,
                        isShow = it.isShow
                    )
                )
            }
            list.addAll(tempList)
            listAdapter.notifyDataSetChanged()
            viewModel.position = -1;

            viewModel.toDoList.value = null
        })

        viewModel.toDoListData.observe(this, androidx.lifecycle.Observer {
            if (viewModel.position != -1) {
                list.set(viewModel.position, it)
                listAdapter.notifyItemChanged(viewModel.position)
            } else {
                list.add(it)
                listAdapter.notifyDataSetChanged()
            }
            viewModel.position = -1;
        })

        binding.etdate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                binding.etdate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                viewModel.month = monthOfYear
                viewModel.year = year
                viewModel.day = dayOfMonth
            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()

        }
        binding.etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                this.cal.set(Calendar.HOUR_OF_DAY, hour)
                this.cal.set(Calendar.MINUTE, minute)
                viewModel.hour = hour
                viewModel.minute = minute
                binding.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))

            }
            this.cal = cal
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onItemClick(v: View, position: Int) {

        val selectedItem = list[position]

        val builder = AlertDialog.Builder(this)

        builder.setMessage(selectedItem.title)
        builder.setPositiveButton("Edit") { dialog, which ->
            viewModel.title.set(selectedItem.title)
            viewModel.date.set(selectedItem.date)
            viewModel.time.set(selectedItem.time)
            viewModel.position = position
            viewModel.index = selectedItem.indexDb

            // this edit text is for editing note
            val editText = EditText(this)
            editText.isFocusable = true
            builder.setView(editText)
        }
        builder.setNegativeButton("Delete") { dialog, which ->
            viewModel.delete(selectedItem.indexDb)
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onStop() {
        super.onStop()
    }
}
