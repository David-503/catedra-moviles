package com.example.tallerpracticoi_dsm
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tallerpracticoi_dsm.databinding.ActivityMedicalRecordBinding
import com.example.tallerpracticoi_dsm.adapters.MedicalRecordAdapter
import com.example.tallerpracticoi_dsm.dto.PatientDTO

import com.example.tallerpracticoi_dsm.interfaces.PatientsApi

import com.example.tallerpracticoi_dsm.utils.AppLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class MedicalRecord : AppLayout() {
    private lateinit var binding: ActivityMedicalRecordBinding

    private lateinit var patientsList: List<PatientDTO>


    override fun onCreate(savedInstanceState: Bundle?) {

        val patientsApi = this.getApi2(PatientsApi::class.java)
        binding = ActivityMedicalRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)


        val call = patientsApi.getPatients()

        call.enqueue(object : Callback<List<PatientDTO>> {
            override fun onResponse(
                call: Call<List<PatientDTO>>,
                response: Response<List<PatientDTO>>
            ) {
                if(response.isSuccessful) {
                    val patients = response.body()
                    if(patients != null) {
                        binding.patientList.adapter = MedicalRecordAdapter(this@MedicalRecord, patients)
                        binding.patientList.layoutManager = LinearLayoutManager(this@MedicalRecord)
                    }
                    else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error al obtener pacientes 1: $error")
                        Toast.makeText(this@MedicalRecord, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<List<PatientDTO>>, t: Throwable) {
                Log.e("API", "Error al obtener pacientes 2: ${t.message}")
                Toast.makeText(this@MedicalRecord, "Error al obtener los pacientes", Toast.LENGTH_SHORT).show()
            }
        })
    }


}