package com.example.tallerpracticoi_dsm.interfaces

import com.example.tallerpracticoi_dsm.dto.PatientDTO

import retrofit2.Call
import retrofit2.http.*

interface PatientsApi {
    @GET("patient/list")
    fun getPatients(): Call<List<PatientDTO>>
}