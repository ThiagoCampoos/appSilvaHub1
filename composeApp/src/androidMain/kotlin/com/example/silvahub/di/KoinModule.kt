package com.example.silvahub.di

import com.example.silvahub.data.local.database.AppDatabase
import org.koin.dsl.module

var appModule = module {
    single { AppDatabase.create(get()) }
    single { get<AppDatabase>().contaFixaDao() }
    single { get<AppDatabase>().salarioDao() }
    single { get<AppDatabase>().gastoDao() }
}