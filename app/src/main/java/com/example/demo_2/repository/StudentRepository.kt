package com.example.demo_2.repository

import androidx.lifecycle.LiveData
import com.example.demo_2.dao.StudentDAO
import com.example.demo_2.model.Student

class StudentRepository(private val dao: StudentDAO) {

    val allStudents: LiveData<List<Student>> = dao.getAllStudents()

    suspend fun insert(student: Student) = dao.insert(student)
    suspend fun update(student: Student) = dao.update(student)
    suspend fun delete(student: Student) = dao.delete(student)

    suspend fun searchStudentsByName(query: String): LiveData<List<Student>> {
        return dao.searchByName("%$query%")
    }
}