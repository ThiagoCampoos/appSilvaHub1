package com.example.silvahub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salarios")
data class SalarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)
