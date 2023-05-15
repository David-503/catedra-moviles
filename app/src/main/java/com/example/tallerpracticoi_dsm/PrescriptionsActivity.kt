package com.example.tallerpracticoi_dsm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tallerpracticoi_dsm.databinding.ActivityCitesListBinding
import com.example.tallerpracticoi_dsm.databinding.ActivityPrescriptionsBinding
import com.example.tallerpracticoi_dsm.dto.DoctorDTO
import com.example.tallerpracticoi_dsm.dto.PrescriptionDTO
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.interfaces.DoctorsApi
import com.example.tallerpracticoi_dsm.interfaces.PrescriptionsApi
import com.example.tallerpracticoi_dsm.interfaces.SchedulesApi
import com.example.tallerpracticoi_dsm.schedules.CreateAppointment
import com.example.tallerpracticoi_dsm.schedules.PrescriptionAdapter
import com.example.tallerpracticoi_dsm.schedules.ScheduleAdapter
import com.example.tallerpracticoi_dsm.utils.AppLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrescriptionsActivity : AppLayout() {
    private lateinit var binding: ActivityPrescriptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val prescriptionsApi = this.getApi(PrescriptionsApi::class.java)
        binding = ActivityPrescriptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)


        val call = prescriptionsApi.getPrescriptions()

        call.enqueue(object : Callback<List<PrescriptionDTO>> {
            override fun onResponse(
                call: Call<List<PrescriptionDTO>>,
                response: Response<List<PrescriptionDTO>>
            ) {
                if(response.isSuccessful) {
                    val prescriptions = response.body()
                    if(prescriptions != null) {
                        binding.prescriptionList.adapter = PrescriptionAdapter(this@PrescriptionsActivity, prescriptions)
                        binding.prescriptionList.layoutManager = LinearLayoutManager(this@PrescriptionsActivity)
                    }
                    else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error al obtener doctores: $error")
                        Toast.makeText(this@PrescriptionsActivity, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<List<PrescriptionDTO>>, t: Throwable) {
                Log.e("API", "Error al obtener doctores: ${t.message}")
                Toast.makeText(this@PrescriptionsActivity, "Error al obtener las citas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}