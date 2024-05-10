package com.android.partagix.model.item

import android.location.Location
import com.android.partagix.model.category.Category
import com.android.partagix.model.visibility.Visibility
import java.io.File

data class Item(
    val id: String,
    val category: Category,
    val name: String,
    val description: String,
    val visibility: Visibility,
    val quantity: Long,
    val location: Location,
    val idUser: String = "",
    val imageId: File = File.createTempFile("default_image", null)
)
