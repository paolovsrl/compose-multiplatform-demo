package org.omsi.demoproject.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parameter")
data class Parameter (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name : String
)