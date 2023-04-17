package com.example.tallerpracticoi_dsm.dto

import java.util.Calendar
import java.util.Date

class ScheduleDTO {

    var doctor: String? = null
    var date: Date?= null
    var startTime: Date? = null
    var endTime: Date? = null

    fun key(key: String?) {}
    constructor()

    constructor(doctor: String?, date: Date?, startTime: Date?, endTime: Date?) {
        this.doctor = doctor
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "doctor" to doctor,
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
        )
    }
}