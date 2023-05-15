package com.example.tallerpracticoi_dsm.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DoctorDTO(
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
    val id_type_user: Int,
    //val phones: Array<String>,
) : Parcelable
