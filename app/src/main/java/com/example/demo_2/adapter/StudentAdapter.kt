package com.example.demo_2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.demo_2.databinding.ItemStudentBinding
import com.example.demo_2.model.Student

class StudentAdapter(
    private val list: List<Student>,
    private val onDelete: (Student) -> Unit,
    private val onEdit: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.tvName.text = student.name
            binding.tvDob.text = student.dob
            binding.tvPoint.text = student.point.toString()
            binding.imgStudent.load(student.img) {
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }

            binding.btnDelete.setOnClickListener {
                onDelete(student)
            }

            // Nút edit
            binding.btnEdit.setOnClickListener {
                onEdit(student)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}