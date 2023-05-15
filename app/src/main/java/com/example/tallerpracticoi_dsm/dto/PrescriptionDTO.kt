package com.example.tallerpracticoi_dsm.dto

data class PrescriptionDTO (
    val id_drug: Int,
    val id_patient: String,
    val drug: String,
    val dose: String,
    val days: Int,
    val active_at: String?
)