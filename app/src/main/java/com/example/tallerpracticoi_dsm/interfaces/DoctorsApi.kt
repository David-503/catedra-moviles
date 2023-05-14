package com.example.tallerpracticoi_dsm.interfaces

import com.example.tallerpracticoi_dsm.dto.DoctorDTO
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import retrofit2.Call
import retrofit2.http.GET

interface DoctorsApi {

    @GET("doctor/list")
    fun getDoctors(): Call<List<DoctorDTO>>
}