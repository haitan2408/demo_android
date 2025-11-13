package com.example.demo_2.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.demo_2.dao.StudentDAO
import com.example.demo_2.database.StudentDatabase
import com.example.demo_2.databinding.ActivityAddEditStudentBinding
import com.example.demo_2.model.Student
import com.example.demo_2.viewmodel.StudentViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class AddEditStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditStudentBinding
    private var imageUri: Uri? = null
    private lateinit var studentViewModel: StudentViewModel

    private var editingStudent: Student? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    imageUri = uri
                    binding.imgPreview.load(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        // Nhận dữ liệu Student nếu có
        intent.getStringExtra("student_json")?.let { json ->
            editingStudent = Gson().fromJson(json, Student::class.java)
            editingStudent?.let { student ->
                binding.edtName.setText(student.name)
                binding.edtDob.setText(student.dob)
                binding.edtPoint.setText(student.point.toString())
                if(student.img.isNotEmpty()) {
                    binding.imgPreview.load(File(student.img)) {
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_report_image)
                    }
                    // Chỉ lưu đường dẫn, không set Uri từ File, tránh crash
                    imageUri = null
                }
            }
        }

        // Khôi phục ảnh khi rotate
        savedInstanceState?.getString("imageUri")?.let { uriString ->
            imageUri = Uri.parse(uriString)
            binding.imgPreview.load(imageUri)
        }

        binding.btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePicker.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            saveStudent()
        }

        val edtDob = binding.edtDob
        edtDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format ngày theo yyyy-MM-dd
                edtDob.setText(
                    String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                )
            }, year, month, day)

            datePicker.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imageUri?.let {
            outState.putString("imageUri", it.toString())
        }
    }

    private fun saveStudent() {
        val name = binding.edtName.text.toString().trim()
        val dob = binding.edtDob.text.toString().trim()
        val pointText = binding.edtPoint.text.toString().trim()

        if (name.isEmpty() || dob.isEmpty() || pointText.isEmpty()) {
            binding.edtName.error = "Please fill all fields"
            return
        }

        val point = pointText.toFloatOrNull() ?: 0f
        // Lưu ảnh vào internal storage
        val imgPath = imageUri?.let { saveImageToInternalStorage(it) } ?: editingStudent?.img ?: ""


        val student = Student(
            id = editingStudent?.id ?: 0,
            name = name,
            dob = dob,
            img = imgPath,
            point = point
        )

        CoroutineScope(Dispatchers.IO).launch {
            if(editingStudent != null && editingStudent!!.id > 0){
                studentViewModel.update(student)
            } else {
                studentViewModel.insert(student)
            }
            runOnUiThread { finish() }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri) ?: return ""
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val file = File(filesDir, "student_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        return file.absolutePath
    }


}