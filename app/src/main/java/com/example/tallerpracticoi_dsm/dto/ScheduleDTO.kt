package com.example.tallerpracticoi_dsm.dto

import java.util.Calendar
import java.util.Date

data class ScheduleDTO (
    val id_medical_appointment: Int,
    val code_medical_appointment: String,
    val patient: String,
    val doctor: String,
    val status: Int,
    val appointment_date: String,
    val initial_date: String,
    val finalization_date: String,
    val created_at: String,
    val updated_at: String
)