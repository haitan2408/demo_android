package com.example.demo_2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo_2.adapter.StudentAdapter
import com.example.demo_2.databinding.ActivityMainBinding
import com.example.demo_2.model.Student
import com.example.demo_2.viewmodel.StudentViewModel
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: StudentViewModel
    private var studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        val adapter = StudentAdapter(
            studentList,
            onDelete = { student -> showDeleteDialog(student) },
            onEdit = { student ->
                val intent = Intent(this, AddEditStudentActivity::class.java)
                val json = Gson().toJson(student)  // chuyển student thành JSON
                intent.putExtra("student_json", json)
                startActivity(intent)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.allStudents.observe(this) { list ->
            studentList.clear()
            studentList.addAll(list)
            adapter.notifyDataSetChanged()
        }

        // Demo thêm student
        binding.fabAdd.setOnClickListener {
                val intent = Intent(this, AddEditStudentActivity::class.java)
                startActivity(intent)
        }
    }

    private fun showDeleteDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Xóa sinh viên")
            .setMessage("Bạn có chắc muốn xóa ${student.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.delete(student)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

}