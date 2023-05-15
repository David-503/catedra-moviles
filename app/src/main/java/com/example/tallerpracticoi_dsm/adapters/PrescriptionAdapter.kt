package com.example.tallerpracticoi_dsm.schedules

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tallerpracticoi_dsm.PrescriptionsActivity
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.dto.PrescriptionDTO
import com.example.tallerpracticoi_dsm.interfaces.PrescriptionsApi
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionAdapter(private val context: Context, var prescriptions: List<PrescriptionDTO>): RecyclerView.Adapter<PrescriptionAdapter.Holder>() {
    private val formatterDate = SimpleDateFormat("yyyy-MM-dd")
    private val formatterTime = SimpleDateFormat("HH:mm:ss")


    fun <T> getApi(clazz: Class<T>): T {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", Credentials.basic("prueba", "prueba"))
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Crea una instancia del servicio que utiliza la autenticacion HTTP basica
        return retrofit.create(clazz)
    }
    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var lblDrug: TextView
        var lblDays: TextView
        var lblDose: TextView
        var lblFrom: TextView
        var lblTo: TextView
        var lblRestDays: TextView
        var btnStartPrescription: Button
        var containerFromTo: LinearLayout
        var lblDayParams: TextView

        init {
            lblDrug = itemView!!.findViewById(R.id.lblDrug)
            lblDays = itemView!!.findViewById(R.id.days)
            lblDose = itemView!!.findViewById(R.id.lblDose)
            lblRestDays = itemView!!.findViewById(R.id.lblRestDays)
            lblFrom = itemView!!.findViewById(R.id.lblFrom)
            lblTo = itemView!!.findViewById(R.id.lblTo)
            btnStartPrescription = itemView!!.findViewById(R.id.btnStartPrescription)
            lblDayParams = itemView!!.findViewById(R.id.lblParam)
            containerFromTo = itemView!!.findViewById(R.id.container_from_to)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.prescription_item_adapter, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return prescriptions.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val prescription = prescriptions[position]
        val from = Calendar.getInstance()
        if(prescription.active_at !== null) {
            from.time = formatterDate.parse(prescription.active_at) as Date
            val to = Date().time + (prescription.days * 24 * 60 * 60 * 1000)
            val distance =  to - from.time.time
            val dayDiff = if(distance < 0) 0 else (distance / (1000 * 60 * 60 * 24))
            holder.lblRestDays.text = dayDiff.toString()
            holder.btnStartPrescription.visibility = View.GONE
            holder.containerFromTo.visibility = View.VISIBLE
            holder.lblFrom.text = "${from.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') }/${(from.get(Calendar.MONTH)+1).toString().padStart(2, '0')}/${from.get(Calendar.YEAR)}"
            from.time = Date(to)
            holder.lblTo.text = "${from.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') }/${(from.get(Calendar.MONTH)+1).toString().padStart(2, '0')}/${from.get(Calendar.YEAR)}"
        }
        else {
            holder.lblRestDays.text = ""
            holder.lblDayParams.text = context.getString(R.string.not_started_prescription)
            holder.btnStartPrescription.visibility = View.VISIBLE
            holder.containerFromTo.visibility = View.GONE
        }
        //cal.time = formatterTime.parse(appointment.initial_date) as Date
        holder.lblDrug.text = prescription.drug
        holder.lblDays.text = prescription.days.toString()
        holder.lblDose.text = prescription.dose

        holder.btnStartPrescription.setOnClickListener {
            val prescriptionApi = this.getApi(PrescriptionsApi::class.java)
            val call = prescriptionApi.startPrescription(prescription.id_drug)
            call.enqueue(object : Callback<PrescriptionDTO> {
                override fun onResponse(
                    call: Call<PrescriptionDTO>,
                    response: Response<PrescriptionDTO>
                ) {
                    if(response.isSuccessful) {
                        Toast.makeText(context, R.string.success_started_prescription, Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, PrescriptionsActivity::class.java)
                        intent.putExtra("itemMenuSelected", R.id.prescriptions)
                        context.startActivity(intent)
                    } else {
                        Log.e("Prescription", "No se ha podido iniciar: ${response.message()}")

                    }
                }

                override fun onFailure(call: Call<PrescriptionDTO>, t: Throwable) {
                    Log.e("Prescription", "No se ha podido iniciar: ${t.message}")
                }
            })
        }
    }
}
