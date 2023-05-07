package com.example.tallerpracticoi_dsm.interfaces

import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import retrofit2.Call
import retrofit2.http.GET

interface SchedulesApi {
    @GET("test")
    fun getSchedules(): Call<List<ScheduleDTO>>

}