package com.example.tallerpracticoi_dsm.schedules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.databinding.ActivityCitesListBinding
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateAppointmentBinding
import com.example.tallerpracticoi_dsm.dto.DoctorDTO
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.interfaces.SchedulePayload
import com.example.tallerpracticoi_dsm.interfaces.SchedulesApi
import com.example.tallerpracticoi_dsm.utils.AppLayout
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class CreateAppointment : AppLayout() {
    private lateinit var binding: ActivityCreateAppointmentBinding
    private lateinit var iptDate: EditText
    private val formatter = SimpleDateFormat("dd/MM/yyyy")
    private val timeFormatter = SimpleDateFormat("HH:mm")

    private lateinit var schedulesApi: SchedulesApi
    private lateinit var doctors: List<DoctorDTO>
    private var selectedDoctor: Int? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        val data = intent.extras
        val items = data?.getParcelableArray("doctorsList", DoctorDTO::class.java)
        if(items != null) doctors = items.toList()

        this.schedulesApi = this.getApi(SchedulesApi::class.java)

        startListeners()
        startDropdown()
    }

    private fun startDropdown() {
        val adapter = ArrayAdapter(this, R.layout.dropdown_item_adapter, doctors.map{d -> "${d.name} ${d.lastname}"})
        binding.dpdDoctors.setAdapter(adapter)
        binding.dpdDoctors.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            val selectedItem = adapterView.getItemAtPosition(i)
            selectedDoctor = i
            Toast.makeText(this, "Doctor $selectedItem", Toast.LENGTH_SHORT)
        }
    }

    private fun initDatePicker(editText: EditText, label: TextView) {
        if(editText.error != null && editText.error.isNotEmpty()) editText.error = null
        val calendar = Calendar.getInstance()
        if(editText.text.isNotEmpty()) calendar.time = formatter.parse(editText.text.toString())
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, {
                _, year, month , dayOfMonth ->

            if(year < calendar.get(Calendar.YEAR) || month < calendar.get(Calendar.MONTH) || dayOfMonth < calendar.get(
                    Calendar.DAY_OF_MONTH)) {
                editText.error = getString(R.string.error_input_date)
                return@DatePickerDialog
            }
            val actualMonth = month +1;
            editText.setText("$dayOfMonth/$actualMonth/$year")
            if(label.visibility === View.GONE) label.visibility = View.VISIBLE
        }, year, month, day)
        datePickerDialog.show()
    }
    private fun initTimePicker(editText: EditText, label: TextView) {
        if(editText.error != null && editText.error.isNotEmpty()) editText.error = null
        val calendar = Calendar.getInstance()
        if(editText.text.isNotEmpty()) calendar.time = timeFormatter.parse(editText.text.toString())
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, {
                _, hour, minute ->
            if(label.visibility === View.GONE) label.visibility = View.VISIBLE
            if(label.id == R.id.labelStartTime) binding.iptEndTime.isEnabled = true
            else {
                val values = binding.iptStartTime.text.split(':').map { value -> value.toInt() }
                if(hour <= values[0] && minute < values[1]) {
                    editText.error = getString(R.string.error_end_time)
                    return@TimePickerDialog
                }
            }
            editText.setText("$hour:$minute")
        }, hour, minute, false)
        timePickerDialog.show()
    }
    private fun startListeners() {
        // Date Picker
        val iptDate = binding.inputDate
        val labelDate = binding.labelDate

        iptDate.setOnClickListener { initDatePicker(iptDate, labelDate )}
        iptDate.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) initDatePicker(iptDate, labelDate)}

        // Start Time
        val iptStartTime = binding.iptStartTime
        val labelStartTime = binding.labelStartTime
        iptStartTime.setOnClickListener { initTimePicker(iptStartTime, labelStartTime) }
        iptStartTime.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) initTimePicker(iptStartTime, labelStartTime) }

        // End Time
        val iptEndTime = binding.iptEndTime
        val labelEndTime = binding.labelEndTime
        iptEndTime.setOnClickListener { initTimePicker(iptEndTime, labelEndTime) }
        iptEndTime.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) initTimePicker(iptEndTime, labelEndTime) }

        // End button
        binding.btnCreateSchedule.setOnClickListener {
            if(!binding.inputDate.text.isNotEmpty() || !binding.iptStartTime.text.isNotEmpty() || !binding.iptEndTime.text.isNotEmpty() || !binding.dpdDoctors.text.isNotEmpty()) {
                Toast.makeText(this, R.string.complete_form, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
             calendar.time = formatter.parse(binding.inputDate.text.toString())
            val newSchedule = SchedulePayload("${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DAY_OF_MONTH)}", this.getLocalVar("dui") ?: "", doctors[selectedDoctor!!].dui, binding.iptStartTime.text.toString(), binding.iptEndTime.text.toString())
            val call = this.schedulesApi.newSchedule(newSchedule)
            call.enqueue(object : Callback<ScheduleDTO> {
                override fun onResponse(call: Call<ScheduleDTO>, response: Response<ScheduleDTO>) {
                    if(response.isSuccessful) {
                        Toast.makeText(this@CreateAppointment, R.string.success_create_schedule, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@CreateAppointment, CitesList::class.java)
                        intent.putExtra("itemMenuSelected", R.id.cites)
                        startActivity(intent)
                    }
                    else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error al crear cita de este paciente: $error")
                        Toast.makeText(this@CreateAppointment, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ScheduleDTO>, t: Throwable) {
                    Toast.makeText(this@CreateAppointment, R.string.failed_create_schedule, Toast.LENGTH_SHORT).show()
                }
            })
            return@setOnClickListener
        }
        // Floating Button
        binding.btnReturn.setOnClickListener {
            val intent = Intent(this, CitesList::class.java)
            intent.putExtra("itemMenuSelected", R.id.cites)
            startActivity(intent)
        }
    }
}