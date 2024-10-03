package org.omsi.demoproject.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("products")
    var list: List<Int>
)