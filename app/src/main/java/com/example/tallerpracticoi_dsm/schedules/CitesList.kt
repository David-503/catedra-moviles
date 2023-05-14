package com.example.tallerpracticoi_dsm.schedules

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.databinding.ActivityCitesListBinding
import com.example.tallerpracticoi_dsm.dto.DoctorDTO
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.interfaces.DoctorsApi
import com.example.tallerpracticoi_dsm.interfaces.SchedulesApi
import com.example.tallerpracticoi_dsm.utils.AppLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class CitesList : AppLayout() {
    private lateinit var binding: ActivityCitesListBinding
    private lateinit var doctorsList: List<DoctorDTO>
    private var schedulesList: List<ScheduleDTO> = listOf()


    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
    override fun onCreate(savedInstanceState: Bundle?) {

        val data = intent.extras
        val doctorsApi = this.getApi(DoctorsApi::class.java)
        val schedulesApi = this.getApi(SchedulesApi::class.java)
        binding = ActivityCitesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)


        val call = doctorsApi.getDoctors()
        val schedulesCall = schedulesApi.getSchedules()

        call.enqueue(object : Callback<List<DoctorDTO>> {
            override fun onResponse(
                call: Call<List<DoctorDTO>>,
                response: Response<List<DoctorDTO>>
            ) {
                if(response.isSuccessful) {
                    val doctors = response.body()
                    if(doctors != null) {
                        doctorsList = doctors
                    }
                    else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error al obtener doctores: $error")
                        Toast.makeText(this@CitesList, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<List<DoctorDTO>>, t: Throwable) {
                Log.e("API", "Error al obtener doctores: ${t.message}")
                Toast.makeText(this@CitesList, "Error al obtener las citas", Toast.LENGTH_SHORT).show()
            }
        })
        schedulesCall.enqueue(object : Callback<List<ScheduleDTO>> {
            override fun onResponse(
                call: Call<List<ScheduleDTO>>,
                response: Response<List<ScheduleDTO>>
            ) {
                if(response.isSuccessful) {
                    schedulesList = response.body()!!
                    println("***************************")
                    println(schedulesList)
                    println("************")
                    binding.scheduleList.adapter = ScheduleAdapter(this@CitesList, schedulesList)
                    binding.scheduleList.layoutManager = LinearLayoutManager(this@CitesList)
                }
            }

            override fun onFailure(call: Call<List<ScheduleDTO>>, t: Throwable) {
                Log.e("Appointments List", "error is ${t.message}")
            }
        })
        binding.newSchedule.setOnClickListener {
            val intent = Intent(this, CreateAppointment::class.java)
            intent.putExtra("itemMenuSelected", R.id.cites)
            intent.putExtra("doctorsList", doctorsList.toTypedArray())
            startActivity(intent)
        }
    }

}