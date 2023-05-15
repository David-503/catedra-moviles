package com.example.tallerpracticoi_dsm.interfaces

import com.example.tallerpracticoi_dsm.dto.PrescriptionDTO
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PrescriptionsApi {
    @GET("prescriptions/list/{patientId}")
    fun getPrescriptions(@Path("patientId") patientId: String): Call<List<PrescriptionDTO>>

    @POST("prescriptions/start/{id}")
    fun startPrescription(@Path("id") prescriptionId: Int): Call<PrescriptionDTO>
}