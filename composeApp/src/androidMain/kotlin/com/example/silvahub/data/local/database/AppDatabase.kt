package com.example.silvahub.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.silvahub.data.local.dao.CartaoDao
import com.example.silvahub.data.local.dao.ContaFixaDao
import com.example.silvahub.data.local.dao.FaturaDao
import com.example.silvahub.data.local.dao.GastoDao
import com.example.silvahub.data.local.dao.OrcamentoDao
import com.example.silvahub.data.local.dao.PagamentoFaturaDao
import com.example.silvahub.data.local.dao.SalarioDao
import com.example.silvahub.data.local.dao.SalarioExtraDao
import com.example.silvahub.data.local.entity.CartaoEntity
import com.example.silvahub.data.local.entity.CompraCartaoEntity
import com.example.silvahub.data.local.entity.ContaFixaEntity
import com.example.silvahub.data.local.entity.FaturaEntity
import com.example.silvahub.data.local.entity.GastoEntity
import com.example.silvahub.data.local.entity.OrcamentoEntity
import com.example.silvahub.data.local.entity.PagamentoFaturaEntity
import com.example.silvahub.data.local.entity.ParcelaCartaoEntity
import com.example.silvahub.data.local.entity.RecorrenciaCartaoEntity
import com.example.silvahub.data.local.entity.SalarioEntity
import com.example.silvahub.data.local.entity.SalarioExtraEntity

@Database(
    entities = [
        SalarioEntity::class,
        ContaFixaEntity::class,
        GastoEntity::class,
        SalarioExtraEntity::class,
        OrcamentoEntity::class,
        CartaoEntity::class,
        FaturaEntity::class,
        CompraCartaoEntity::class,
        ParcelaCartaoEntity::class,
        RecorrenciaCartaoEntity::class,
        PagamentoFaturaEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun salarioDao(): SalarioDao
    abstract fun contaFixaDao(): ContaFixaDao
    abstract fun gastoDao(): GastoDao
    abstract fun salarioExtraDao(): SalarioExtraDao
    abstract fun orcamentoDao(): OrcamentoDao
    abstract fun cartaoDao(): CartaoDao
    abstract fun faturaDao(): FaturaDao
    abstract fun pagamentoFaturaDao(): PagamentoFaturaDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "silvahub.db",
            )
                .addMigrations(DatabaseMigrations.MIGRATION_4_5)
                .build()
        }
    }
}
