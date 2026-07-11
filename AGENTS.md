# AGENTS.md

## Project Snapshot
- Single-module Kotlin Multiplatform project with `:composeApp` as the only included module (`settings.gradle.kts`).
- Runtime target is currently Android only (`androidTarget {}` in `composeApp/build.gradle.kts`), even though KMP scaffolding exists.
- UI entrypoint is `MainActivity` at `composeApp/src/androidMain/kotlin/com/example/silvahub/util/MainActivity.kt` and it renders `App()`.
- `App()` applies `SilvaHubTheme` (Material You dinâmico + preferência de tema via DataStore) and renders `ui/navigation/AppNavHost.kt` with Navigation Compose type-safe routes and bottom bar (Home, Gastos, Histórico, Configurações).

## Architecture Reality (Current)
- Clean Architecture layers: local persistence (`data/local/*`), repositories (`data/repository/*`), domain models/use cases (`domain/*`), UI screens/ViewModels (`ui/screens/*`).
- Room DB `silvahub.db` version 4 with entities: `SalarioEntity`, `ContaFixaEntity`, `GastoEntity` (parcelas/recorrência), `SalarioExtraEntity`, `OrcamentoEntity`. Still uses `fallbackToDestructiveMigration()` during development; prefer real Migrations when schema stabilizes for production data.
- Features: salário + extras, contas fixas, gastos (à vista/parcelado/recorrente), dashboard com saldo e gasto diário sugerido, histórico com insights, orçamentos por categoria, gráficos, backup JSON, export CSV, widget Glance, notificações de vencimento (WorkManager).
- DI via Koin (`di/KoinModule.kt`) with `viewModel { }` and `koinViewModel()` in Compose.
- Enums de gasto vivem em `domain/model/` (`ECategoriaGasto`, `ETipoGasto`).

## Build, Test, and Local Dev Workflow
- Android debug build: `./gradlew :composeApp:assembleDebug` (Windows: `.\gradlew.bat :composeApp:assembleDebug`).
- Unit tests: `./gradlew :composeApp:testDebugUnitTest` — use cases em `androidUnitTest`.
- Toolchain: AGP `8.7.3` + Gradle `8.14.3` requires JDK 11+.
- Kotlin/Java target 11.

## Conventions
- Package root `com.example.silvahub`; Portuguese domain vocabulary (`Salario`, `ContaFixa`, `Gasto`, `Orcamento`).
- Prefer dependency aliases from `gradle/libs.versions.toml`.
- Money formatting via `util/MoneyFormat`; month helpers via `util/DateUtils`.

## Integration Points
- Navigation Compose type-safe (`AppRoute` + kotlinx.serialization).
- DataStore preferences for theme and last backup timestamp.
- Glance widget `SaldoGlanceWidget` + deep link `ACTION_NOVO_GASTO` opens gastos sheet.
- WorkManager `ContasVencimentoWorker` for daily due-date notifications.
- Firebase remains out of scope (local-only app).
