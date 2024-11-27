package com.example.dimsumbangkit.data.api

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
        @field:SerializedName("error")
        val error: Boolean,
        @field:SerializedName("Registration Successful")
        val message: String
    )