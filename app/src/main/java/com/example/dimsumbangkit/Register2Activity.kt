package com.example.dimsumbangkit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dimsumbangkit.databinding.ActivityRegister2Binding

class Register2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegister2Binding
    private var currentImageUri: Uri? = null
    private lateinit var utils: Utils

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            binding.uploadImageView.setImageURI(uri)
            currentImageUri = uri
        }
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        utils = Utils(applicationContext)

        binding.cardUploadImage.setOnClickListener {
            showImageUploadOptions()
        }
        binding.nextButton.setOnClickListener {
            navigateToRegister3Activity()
        }
    }

    private fun showImageUploadOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkPermissionsAndStartCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun checkPermissionsAndStartCamera() {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
            }
        }
    }

    private fun startCamera() {
        currentImageUri = utils.getImageUri()
        if (currentImageUri != null) {
            launcherIntentCamera.launch(currentImageUri!!)
        }
    }

    private fun openGallery() {
        launcherIntentGallery.launch("image/*")
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.uploadImageView.setImageURI(uri)
        }
    }

    private fun navigateToRegister3Activity() {
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        val intent = Intent(this, Register3Activity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        intent.putExtra("palmImageUri", currentImageUri?.toString())
        startActivity(intent)
    }
}
