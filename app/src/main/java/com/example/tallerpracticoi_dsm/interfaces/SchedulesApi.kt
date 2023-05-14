package com.example.tallerpracticoi_dsm.interfaces

import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class SchedulePayload(
    val Date: String,
    val SelectedPatientDui: String,
    val SelectedDoctorDui: String,
    val InitialHour: String,
    val FinalHour: String
)
interface SchedulesApi {
    @GET("citas/list")
    fun getSchedules(): Call<List<ScheduleDTO>>

    @POST("citas/crear")
    fun newSchedule(@Body payload: SchedulePayload): Call<ScheduleDTO>

}