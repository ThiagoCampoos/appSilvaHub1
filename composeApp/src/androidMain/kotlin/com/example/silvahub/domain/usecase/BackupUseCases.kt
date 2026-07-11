package com.example.silvahub.domain.usecase

import com.example.silvahub.data.backup.BackupRepository

class ExportarBackupUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(): String = backupRepository.exportToJson()
}

class ImportarBackupUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(json: String) = backupRepository.importFromJson(json)
}
