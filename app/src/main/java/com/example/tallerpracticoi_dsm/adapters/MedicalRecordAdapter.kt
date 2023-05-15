package com.example.tallerpracticoi_dsm.adapters

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
import com.example.tallerpracticoi_dsm.dto.PatientDTO
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

class MedicalRecordAdapter(private val context: Context, var patients: List<PatientDTO>): RecyclerView.Adapter<MedicalRecordAdapter.Holder>() {

    fun <T> getApi(clazz: Class<T>): T {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", Credentials.basic("admin", "admin123"))
                    .build()
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.9/api/patients.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Crea una instancia del servicio que utiliza la autenticacion HTTP basica
        return retrofit.create(clazz)
    }
    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var lblNombre: TextView
        var lblFecha: TextView
        var lblDui: TextView
        var lblCorreo: TextView
        var lblDireccion: TextView


        init {
            lblNombre = itemView!!.findViewById(R.id.lblNombre)
            lblFecha = itemView!!.findViewById(R.id.lblFecha)
            lblDui = itemView!!.findViewById(R.id.lblDui)
            lblCorreo = itemView!!.findViewById(R.id.lblCorreo)
            lblDireccion = itemView!!.findViewById(R.id.lblDireccion)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.medical_item_adapter, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val patient = patients[position]
        //cal.time = formatterTime.parse(appointment.initial_date) as Date
        holder.lblNombre.text = patient.name
        holder.lblFecha.text = patient.birthdate
        holder.lblDui.text = patient.dui
        holder.lblCorreo.text = patient.email
        holder.lblDireccion.text = patient.address

    }
}
