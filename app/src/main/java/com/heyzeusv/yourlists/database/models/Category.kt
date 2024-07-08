package com.heyzeusv.yourlists.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(@PrimaryKey val name: String)
