package com.dicoding.courseschedule.ui.add

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddCourseActivity : AppCompatActivity(),TimePickerFragment.DialogTimeListener {

    private lateinit var viewModel: AddCourseViewModel
    private var startTime: String = ""
    private var endTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        val factory = AddViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddCourseViewModel::class.java]
        supportActionBar?.title = "Add Course"

        viewModel.saved.observe(this){
            if (it.getContentIfNotHandled() == true){
                onBackPressed()
            } else {
                Toast.makeText(applicationContext, "Time empty", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun startTimePick(view: View) {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, "startPicker")
    }

    fun endTimePick(view: View) {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, "endPicker")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        when(tag){
            "startPicker" -> {
                findViewById<TextView>(R.id.tv_add_start_time).text = timeFormat.format(calendar.time)
                startTime = timeFormat.format(calendar.time)

            }
            "endPicker" -> {
                findViewById<TextView>(R.id.tv_add_end_time).text = timeFormat.format(calendar.time)
                endTime = timeFormat.format(calendar.time)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_insert -> {
                val edtCourseName = findViewById<TextInputEditText>(R.id.edt_add_course_name).text.toString().trim()
                val edtLecturer = findViewById<TextInputEditText>(R.id.add_ed_lecture).text.toString().trim()
                val edtNote = findViewById<TextInputEditText>(R.id.edt_add_note).text.toString().trim()
                val day = findViewById<Spinner>(R.id.spinner_day).selectedItemPosition

                when{
                    edtCourseName.isEmpty() -> {
                        findViewById<TextInputEditText>(R.id.edt_add_course_name).error = "Field can not be blank"
                    }
                    edtLecturer.isEmpty() -> {
                        findViewById<TextInputEditText>(R.id.add_ed_lecture).error = "Field can not be blank"
                    }
                    edtNote.isEmpty() -> {
                        findViewById<TextInputEditText>(R.id.edt_add_note).error = "Field can not be blank"
                    }
                    else -> {
                        viewModel.insertCourse(edtCourseName, day, startTime, endTime, edtLecturer, edtNote)
                        Toast.makeText(applicationContext, "Add new course success", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }


}