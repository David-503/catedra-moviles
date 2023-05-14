package com.example.tallerpracticoi_dsm.schedules

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class ScheduleAdapter(private val context: Context, var appointments: List<ScheduleDTO>): RecyclerView.Adapter<ScheduleAdapter.Holder>() {
    private val formatterDate = SimpleDateFormat("yyyy-MM-dd")
    private val formatterTime = SimpleDateFormat("HH:mm:ss")
    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var lblStartTime: TextView
        var lblDoctor: TextView
        var lblDate: TextView
        var imgChecked: ImageView
        var imgDelete: ImageView
         var mainContainer: ConstraintLayout
         var percentage: Guideline
        var x = 0f
        var y = 0f

        init {
            lblStartTime = itemView!!.findViewById(R.id.lblStartTime)
            lblDoctor = itemView!!.findViewById(R.id.lblDoctor)
            lblDate = itemView!!.findViewById(R.id.lblDate)
            imgChecked = itemView!!.findViewById(R.id.imgChecked)
            mainContainer = itemView!!.findViewById(R.id.mainContainer)
            percentage = itemView!!.findViewById(R.id.controlDivision)
            imgDelete = itemView!!.findViewById(R.id.delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.schedule_item_adapter, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val appointment = appointments[position]
        val cal = Calendar.getInstance()
        var percent = 1f;
        cal.time = formatterTime.parse(appointment.initial_date)
        holder.lblStartTime.text = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2,'0') + ":" + cal.get(Calendar.MINUTE).toString().padStart(2, '0') + " " + (if(cal.get(Calendar.AM_PM) === 0) "AM" else "PM")
        holder.lblDoctor.text = appointment.doctor.name
        holder.imgChecked.setImageDrawable(
            if(appointment.status == 1)
                holder.itemView.context.getDrawable(R.drawable.baseline_access_time_24)
            else
                holder.itemView.context.getDrawable(R.drawable.baseline_cancel_24)
        )
        holder.imgDelete.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.baseline_delete_outline_24))
        cal.time = formatterDate.parse(appointment.appointment_date)
        holder.lblDate.text =  cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') + "/" + cal.get(Calendar.MONTH).toString().padStart(2, '0')  + "/" + cal.get(Calendar.YEAR)
        if(appointment.status == 1)
            holder.mainContainer.setOnTouchListener(object: View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if(event == null) return false;
                    when(event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            holder.x = event.x
                            holder.y = event.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val distanceX = event.x - holder.x;
                            if(distanceX> 0) {
                                percent = 1f
                                return false
                            }
                            percent = 1 - abs(distanceX / holder.itemView.width)
                            if(percent >= 0.75)
                                holder.percentage.setGuidelinePercent(percent)
                        }
                        MotionEvent.ACTION_UP -> {
                            if(floor(percent * 100) > 75 ) {
                                holder.percentage.setGuidelinePercent(1f)
                                return false
                            }
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage(R.string.delete_schedule).setCancelable(false)
                                .setPositiveButton(R.string.positive_message) { dialog, id ->
                                    Toast.makeText(context, R.string.success_delete_schedule, Toast.LENGTH_SHORT).show()
                                }
                                .setNegativeButton(R.string.negative_message) { dialog, id ->
                                    Toast.makeText(context, "Process was cancelled", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                    return true
                }
            })
        //if(appointment.date!!.before(Date())) {
        //imgChecked.visibility = View.VISIBLE
        //imgPending.visibility = View.GONE
        //} else {
        //imgPending.visibility = View.VISIBLE
        //imgChecked.visibility = View.GONE
        //}
    }
}
/*class ScheduleAdapter(private val context: Activity, var appointments: List<ScheduleDTO>): ArrayAdapter<ScheduleDTO?>(context, R.layout.activity_cites_list, appointments){
    private val formatterDate = SimpleDateFormat("dd/MM/yyyy")
    private val formatterTime = SimpleDateFormat("HH:mm")
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
        cal.time = formatterTime.parse(appointment.initial_date)
        lblStartTime.text = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2,'0') + ":" + cal.get(Calendar.MINUTE).toString().padStart(2, '0') + " " + (if(cal.get(Calendar.AM_PM) === 0) "AM" else "PM")
        lblDoctor.text = appointment.doctor
        cal.time = formatterDate.parse(appointment.appointment_date)
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
}*/