package com.example.tallerpracticoi_dsm.schedules

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.R
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleAdapter(private val context: Activity, var appointments: List<ScheduleDTO>): ArrayAdapter<ScheduleDTO?>(context, R.layout.activity_cites_list, appointments){
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val layoutInflater = context.layoutInflater

        var rowView: View? = null
        rowView = view ?: layoutInflater.inflate(R.layout.schedule_item_adapter, null)
        val lblStartTime = rowView!!.findViewById<TextView>(R.id.lblStartTime)
        val lblDoctor = rowView!!.findViewById<TextView>(R.id.lblDoctor)
        val lblDate = rowView!!.findViewById<TextView>(R.id.lblDate)
        val imgChecked = rowView!!.findViewById<ImageView>(R.id.imgChecked)
        //val imgPending = rowView!!.findViewById<ImageView>(R.id.imgPending)
        val appointment = appointments[position]
        val cal = Calendar.getInstance()
        cal.time = appointment.startTime
        lblStartTime.text = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2,'0') + ":" + cal.get(Calendar.MINUTE).toString().padStart(2, '0') + " " + (if(cal.get(Calendar.AM_PM) === 0) "AM" else "PM")
        lblDoctor.text = appointment.doctor
        cal.time = appointment.date
        lblDate.text =  cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') + "/" + cal.get(Calendar.MONTH).toString().padStart(2, '0')  + "/" + cal.get(Calendar.YEAR)
        //if(appointment.date!!.before(Date())) {
            //imgChecked.visibility = View.VISIBLE
            //imgPending.visibility = View.GONE
        //} else {
            //imgPending.visibility = View.VISIBLE
            //imgChecked.visibility = View.GONE
        //}
        return rowView
    }
}