package com.example.dimsumbangkit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dimsumbangkit.data.api.ApiConfig
import com.example.dimsumbangkit.data.api.RegisterResponse
import com.example.dimsumbangkit.databinding.ActivityRegister3Binding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Register3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegister3Binding
    private var selectedProfileImageUri: Uri? = null
    private var palmImageUri: Uri? = null
    private val IMAGE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Register1Activity
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val palmImageUriString = intent.getStringExtra("palmImageUri")
        palmImageUri = palmImageUriString?.let { Uri.parse(it) }

        if (palmImageUri == null) {
            Toast.makeText(this, "No palm image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val profileImageUriString = intent.getStringExtra("profileImageUri")
        selectedProfileImageUri = profileImageUriString?.let { Uri.parse(it) }

        binding.changeProfileButton.setOnClickListener {
            openImageChooser()
        }

        binding.registerButton.setOnClickListener {
            val fullname = binding.fullnameEditText.text.toString()
            val job = binding.jobEditText.text.toString()
            val company = binding.companyEditText.text.toString()
            val instagram = binding.instagramEditText.text.toString()
            val linkedin = binding.linkedinEditText.text.toString()
            val whatsapp = binding.whatsappEditText.text.toString()

            registerUser(username, email, password, fullname, job, company, instagram, linkedin, whatsapp)
        }
    }

    // Handle the result from the gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedProfileImageUri = data.data
            binding.profileImageView.setImageURI(selectedProfileImageUri)

            binding.changeProfileButton.isEnabled = true
        } else {
            Toast.makeText(this, "No profile image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }



    private fun registerUser(
        username: String?, email: String?, password: String?,
        fullname: String, job: String, company: String,
        instagram: String, linkedin: String, whatsapp: String
    ) {
        // Convert text inputs to RequestBody
        val usernameBody = (username ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = (email ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordBody = (password ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
        val fullnameBody = fullname.toRequestBody("text/plain".toMediaTypeOrNull())
        val jobBody = job.toRequestBody("text/plain".toMediaTypeOrNull())
        val companyBody = company.toRequestBody("text/plain".toMediaTypeOrNull())
        val instagramBody = instagram.toRequestBody("text/plain".toMediaTypeOrNull())
        val linkedinBody = linkedin.toRequestBody("text/plain".toMediaTypeOrNull())
        val whatsappBody = whatsapp.toRequestBody("text/plain".toMediaTypeOrNull())

        // Convert selected profile image URI to MultipartBody.Part
        val profileImageFile = File(getRealPathFromURI(selectedProfileImageUri!!))
        val requestFileProfile = profileImageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val profilePicturePart = MultipartBody.Part.createFormData(
            "profilePicture",
            profileImageFile.name,
            requestFileProfile
        )

        // Convert palm image URI to MultipartBody.Part
        val palmImageFile = File(getRealPathFromURI(palmImageUri!!))
        val requestFilePalm = palmImageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val palmImagePart =
            MultipartBody.Part.createFormData("palmImage", palmImageFile.name, requestFilePalm)

        // Call the API
        ApiConfig.getApiService().registerUser(
            usernameBody, emailBody, passwordBody, fullnameBody, jobBody, companyBody,
            instagramBody, linkedinBody, whatsappBody, profilePicturePart, palmImagePart
        ).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@Register3Activity,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@Register3Activity,
                        "Failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@Register3Activity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex) ?: ""
            }
            it.close()
        }
        return filePath
    }
}

