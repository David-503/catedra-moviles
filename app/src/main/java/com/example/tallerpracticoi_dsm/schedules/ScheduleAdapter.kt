package com.example.tallerpracticoi_dsm.schedules

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleAdapter(private val context: Context, var appointments: List<ScheduleDTO>): RecyclerView.Adapter<ScheduleAdapter.Holder>() {
    private val formatterDate = SimpleDateFormat("dd/MM/yyyy")
    private val formatterTime = SimpleDateFormat("HH:mm")
    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var lblStartTime: TextView
        var lblDoctor: TextView
        var lblDate: TextView
        var imgChecked: ImageView
        var x = 0f
        var y = 0f
        //lateinit var imgPending: ImageView

        init {
            lblStartTime = itemView!!.findViewById(R.id.lblStartTime)
            lblDoctor = itemView!!.findViewById(R.id.lblDoctor)
            lblDate = itemView!!.findViewById(R.id.lblDate)
            imgChecked = itemView!!.findViewById(R.id.imgChecked)
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
        cal.time = formatterTime.parse(appointment.initial_date)
        holder.lblStartTime.text = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2,'0') + ":" + cal.get(Calendar.MINUTE).toString().padStart(2, '0') + " " + (if(cal.get(Calendar.AM_PM) === 0) "AM" else "PM")
        holder.lblDoctor.text = appointment.doctor
        holder.imgChecked.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.baseline_access_time_24))
        cal.time = formatterDate.parse(appointment.appointment_date)
        holder.lblDate.text =  cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0') + "/" + cal.get(Calendar.MONTH).toString().padStart(2, '0')  + "/" + cal.get(Calendar.YEAR)
        holder.itemView.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event == null) return false;
                when(event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        holder.x = event.x
                        holder.y = event.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val distanceX = event.x - holder.x;
                        val distanceY = event.y - holder.y;

                        holder.itemView.x = holder.itemView.x + distanceX
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