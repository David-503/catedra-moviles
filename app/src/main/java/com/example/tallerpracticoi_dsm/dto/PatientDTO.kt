package com.example.tallerpracticoi_dsm.dto

data class PatientDTO (
    val dui: String,
    val name: String,
    val lastname: String,
    val email: String,
    val address: String,
    val birthdate: String,
    val gender: Int,
    val email_verified_at: String?,
    val created_at: String?,
    val updated_at: String?,
    val id_type_user: Int
)