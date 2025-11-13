package com.example.demo_2.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dob: String,
    val img: String, // lưu đường dẫn hoặc URI ảnh
    val point: Float
)