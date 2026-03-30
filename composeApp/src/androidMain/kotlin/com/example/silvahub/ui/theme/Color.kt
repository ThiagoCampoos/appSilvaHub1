package com.example.silvahub.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────
// Primária — Verde (Saldo Positivo)
// ─────────────────────────────────────────────────────────────
val PrimaryGreen      = Color(0xFF2E7D32)  // Verde escuro — botões, destaques principais
val PrimaryGreenLight = Color(0xFF66BB6A)  // Verde claro  — modo claro, estados hover
val PrimaryGreenDark  = Color(0xFF1B5E20)  // Verde muito escuro — modo escuro

// ─────────────────────────────────────────────────────────────
// Secundária — Vermelho (Saldo Negativo / Gastos)
// ─────────────────────────────────────────────────────────────
val SecondaryRed      = Color(0xFFC62828)  // Vermelho base  — alertas, gastos
val SecondaryRedLight = Color(0xFFEF5350)  // Vermelho claro — chips, badges
val SecondaryRedDark  = Color(0xFF7F0000)  // Vermelho escuro — modo escuro

// ─────────────────────────────────────────────────────────────
// Neutras — Fundo e Superfícies
// ─────────────────────────────────────────────────────────────
val BackgroundLight = Color(0xFFFAFAFA)  // Fundo da tela em modo claro
val BackgroundDark  = Color(0xFF212121)  // Fundo da tela em modo escuro
val SurfaceLight    = Color(0xFFFFFFFF)  // Cards/sheets em modo claro
val SurfaceDark     = Color(0xFF303030)  // Cards/sheets em modo escuro

// ─────────────────────────────────────────────────────────────
// Textos
// ─────────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFF212121)  // Texto principal     — títulos, labels
val TextSecondary = Color(0xFF757575)  // Texto secundário    — subtítulos, hints
val TextTertiary  = Color(0xFFBDBDBD)  // Texto desabilitado  — placeholders

// ─────────────────────────────────────────────────────────────
// Status
// ─────────────────────────────────────────────────────────────
val StatusSuccess = Color(0xFF388E3C)  // Verde médio   — operação bem-sucedida
val StatusError   = Color(0xFFD32F2F)  // Vermelho      — falha, erro
val StatusWarning = Color(0xFFF57C00)  // Laranja       — atenção / aviso
val StatusInfo    = Color(0xFF1976D2)  // Azul          — informação neutra
