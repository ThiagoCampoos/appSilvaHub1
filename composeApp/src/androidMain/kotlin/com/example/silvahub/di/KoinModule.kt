package com.example.silvahub.di

import com.example.silvahub.data.repository.ContaFixaRepositoryImpl
import com.example.silvahub.data.repository.SalarioRepositoryImpl
import com.example.silvahub.data.local.database.AppDatabase
import com.example.silvahub.domain.repository.ContaFixaRepository
import com.example.silvahub.domain.repository.SalarioRepository
import com.example.silvahub.domain.usecase.AdicionarContaFixaUseCase
import com.example.silvahub.domain.usecase.DeletarContaFixaUseCase
import com.example.silvahub.domain.usecase.ObterContasFixasUseCase
import com.example.silvahub.domain.usecase.ObterSalarioDoMesUseCase
import com.example.silvahub.domain.usecase.ObterUltimoSalarioUseCase
import com.example.silvahub.domain.usecase.SalvarSalarioUseCase
import org.koin.dsl.module

var appModule = module {
    single { AppDatabase.create(get()) }
    single { get<AppDatabase>().contaFixaDao() }
    single { get<AppDatabase>().salarioDao() }
    single { get<AppDatabase>().gastoDao() }

    single<SalarioRepository> { SalarioRepositoryImpl(get()) }
    single<ContaFixaRepository> { ContaFixaRepositoryImpl(get()) }

    factory { SalvarSalarioUseCase(get()) }
    factory { ObterSalarioDoMesUseCase(get()) }
    factory { ObterUltimoSalarioUseCase(get()) }
    factory { AdicionarContaFixaUseCase(get()) }
    factory { ObterContasFixasUseCase(get()) }
    factory { DeletarContaFixaUseCase(get()) }
}