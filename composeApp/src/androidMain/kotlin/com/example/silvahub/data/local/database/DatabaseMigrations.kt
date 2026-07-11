package com.example.silvahub.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `cartoes` (
                    `id` INTEGER NOT NULL,
                    `limite_centavos` INTEGER NOT NULL,
                    `dia_fechamento` INTEGER NOT NULL,
                    `dia_vencimento` INTEGER NOT NULL,
                    `data_criacao` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent(),
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `faturas` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `cartao_id` INTEGER NOT NULL,
                    `mes_referencia` TEXT NOT NULL,
                    `data_fechamento` INTEGER NOT NULL,
                    `data_vencimento` INTEGER NOT NULL,
                    `valor_pago_centavos` INTEGER NOT NULL,
                    `status` TEXT NOT NULL,
                    FOREIGN KEY(`cartao_id`) REFERENCES `cartoes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_faturas_cartao_id_mes_referencia` " +
                    "ON `faturas` (`cartao_id`, `mes_referencia`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_faturas_cartao_id` ON `faturas` (`cartao_id`)",
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `recorrencias_cartao` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `cartao_id` INTEGER NOT NULL,
                    `descricao` TEXT NOT NULL,
                    `valor_centavos` INTEGER NOT NULL,
                    `categoria` TEXT NOT NULL,
                    `dia_cobranca` INTEGER NOT NULL,
                    `ativa` INTEGER NOT NULL,
                    `data_inicio` INTEGER NOT NULL,
                    `data_cancelamento` INTEGER,
                    FOREIGN KEY(`cartao_id`) REFERENCES `cartoes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_recorrencias_cartao_cartao_id` " +
                    "ON `recorrencias_cartao` (`cartao_id`)",
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `compras_cartao` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `cartao_id` INTEGER NOT NULL,
                    `recorrencia_id` INTEGER,
                    `mes_referencia_cobranca` TEXT,
                    `descricao` TEXT NOT NULL,
                    `valor_total_centavos` INTEGER NOT NULL,
                    `categoria` TEXT NOT NULL,
                    `data` INTEGER NOT NULL,
                    `tipo` TEXT NOT NULL,
                    `total_parcelas` INTEGER,
                    `estornada` INTEGER NOT NULL,
                    `data_criacao` INTEGER NOT NULL,
                    FOREIGN KEY(`cartao_id`) REFERENCES `cartoes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`recorrencia_id`) REFERENCES `recorrencias_cartao`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_compras_cartao_cartao_id` ON `compras_cartao` (`cartao_id`)",
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_compras_cartao_recorrencia_id_mes_referencia_cobranca` " +
                    "ON `compras_cartao` (`recorrencia_id`, `mes_referencia_cobranca`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_compras_cartao_recorrencia_id` " +
                    "ON `compras_cartao` (`recorrencia_id`)",
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `parcelas_cartao` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `compra_id` INTEGER NOT NULL,
                    `fatura_id` INTEGER NOT NULL,
                    `numero_parcela` INTEGER NOT NULL,
                    `valor_centavos` INTEGER NOT NULL,
                    FOREIGN KEY(`compra_id`) REFERENCES `compras_cartao`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`fatura_id`) REFERENCES `faturas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_parcelas_cartao_compra_id` ON `parcelas_cartao` (`compra_id`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_parcelas_cartao_fatura_id` ON `parcelas_cartao` (`fatura_id`)",
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `pagamentos_fatura` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `fatura_id` INTEGER NOT NULL,
                    `valor_centavos` INTEGER NOT NULL,
                    `data` INTEGER NOT NULL,
                    `estornado` INTEGER NOT NULL,
                    `data_estorno` INTEGER,
                    `data_criacao` INTEGER NOT NULL,
                    FOREIGN KEY(`fatura_id`) REFERENCES `faturas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_pagamentos_fatura_fatura_id` " +
                    "ON `pagamentos_fatura` (`fatura_id`)",
            )
        }
    }
}
