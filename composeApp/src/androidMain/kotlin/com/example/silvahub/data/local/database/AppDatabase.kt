package com.example.silvahub.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.silvahub.data.local.dao.ContaFixaDao
import com.example.silvahub.data.local.dao.GastoDao
import com.example.silvahub.data.local.dao.SalarioDao
import com.example.silvahub.data.local.entity.ContaFixaEntity
import com.example.silvahub.data.local.entity.GastoEntity
import com.example.silvahub.data.local.entity.SalarioEntity
import com.example.silvahub.data.local.entity.SalarioExtraEntity


@Database(
    entities = [SalarioEntity::class, ContaFixaEntity::class, GastoEntity::class, SalarioExtraEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun salarioDao(): SalarioDao
    abstract fun contaFixaDao(): ContaFixaDao
    abstract fun gastoDao(): GastoDao

    companion object{
        fun create(context: Context) : AppDatabase{
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "silvahub.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}


