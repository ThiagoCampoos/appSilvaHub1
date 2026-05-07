# AGENTS.md

## Project Snapshot
- Single-module Kotlin Multiplatform project with `:composeApp` as the only included module (`settings.gradle.kts`).
- Runtime target is currently Android only (`androidTarget {}` in `composeApp/build.gradle.kts`), even though KMP scaffolding exists.
- UI entrypoint is `MainActivity` at `composeApp/src/androidMain/kotlin/com/example/silvahub/util/MainActivity.kt` and it renders `App()`.
- `App()` in `composeApp/src/androidMain/kotlin/com/example/silvahub/App.kt` applies `SilvaHubTheme` and renders `ui/navigation/AppNavHost.kt`; current nav host is a minimal shell that creates `ui/screens/configuracoes/ConfiguracoesViewModel.kt` and renders `ui/screens/configuracoes/ConfiguracoesScreen.kt`.

## Architecture Reality (Current vs Planned)
- Current implemented layers are local persistence (`data/local/*`), repository implementations (`data/repository/*`), domain models/repositories/use cases (`domain/*`), and one wired UI feature (`ui/screens/configuracoes/*`).
- Room database lives in `data/local/database/AppDatabase.kt` with entities `SalarioEntity`, `ContaFixaEntity`, `GastoEntity` and DB name `silvahub.db`.
- `SalarioDao` and `ContaFixaDao` already expose CRUD and `Flow` queries (`data/local/dao/*.kt`), while `GastoDao` is still an empty placeholder.
- `SalarioEntity` and `ContaFixaEntity` already model business fields (`valor`, `mesReferencia`, `nome`, `diaVencimento`, `ativa`), while `GastoEntity` is still `id`-only.
- `domain/*` and `data/repository/*` are populated: repository interfaces and implementations are connected through mappers (`data/repository/SalarioMapper.kt`, `data/repository/ContaFixaMapper.kt`) and use cases in `domain/usecase/*`.
- UI scaffold includes `ui/navigation/AppNavHost.kt`, `ui/screens/configuracoes/*`, and `ui/screens/home/HomeScreen.kt`; `ui/screens/gastos/` remains a placeholder.
- Treat `TASKS.md` as roadmap/planning context; use source files as truth for what is implemented.

## Build, Test, and Local Dev Workflow
- Android debug build command: `./gradlew :composeApp:assembleDebug` (Windows CMD/PowerShell: `.\gradlew.bat :composeApp:assembleDebug`).
- Unit tests live in `composeApp/src/androidUnitTest/...`; baseline test file is `ComposeAppAndroidUnitTest.kt`.
- Toolchain requirement is strict: AGP `8.7.3` + Gradle `8.14.3` requires JDK 11+ (build fails on Java 8).
- Kotlin/Java target is 11 (`JvmTarget.JVM_11` and Android compile options in `composeApp/build.gradle.kts`).
- Gradle performance flags are enabled in `gradle.properties` (`configuration-cache`, build cache, 3G heap).

## Conventions You Should Follow Here
- Keep package roots under `com.example.silvahub` and mirror feature folders already created in `androidMain`.
- Prefer dependency aliases from `gradle/libs.versions.toml`; check there before adding hardcoded versions.
- Existing domain naming is Portuguese (`Salario`, `ContaFixa`, `Gasto`); follow this vocabulary for consistency.
- `composeApp/build.gradle.kts` currently pulls Activity + Room via version-catalog aliases (`libs.androidx.activity.compose`, `libs.androidx.room.runtime`, `libs.androidx.room.ktx`, `libs.androidx.room.compiler`); keep new dependencies aligned with `gradle/libs.versions.toml`.
- Manifest declares activity as `.util.MainActivity` (`composeApp/src/androidMain/AndroidManifest.xml`), so refactors must update both file path and manifest.
- Room setup currently uses `fallbackToDestructiveMigration()` in `AppDatabase.create(context)`; preserve or explicitly replace with migrations when schema evolves.

## Integration Points and Cross-Component Notes
- KSP is enabled and wired to Room compiler (`ksp(libs.androidx.room.compiler)`), so schema/DAO changes rely on generated sources.
- App startup currently flows `SilvaHubApp (startKoin) -> MainActivity -> App() -> AppNavHost() -> ConfiguracoesScreen()`; no Navigation Compose graph is wired yet.
- `AppNavHost.kt` currently resolves use cases via `KoinJavaComponent.get(...)` and manually instantiates `ConfiguracoesViewModel` with `remember { ... }` instead of using Navigation Compose destinations or a DI ViewModel delegate.
- Version catalog defines Navigation Compose, Koin, and Kotlinx Serialization; Koin is integrated at runtime (`SilvaHubApp` + `di/KoinModule.kt`), while Navigation Compose and Kotlinx Serialization are not yet wired in runtime code.
- `ui/components/Greeting.kt` calls `util/getPlatform()`; platform abstraction is minimal and Android-specific today.

## Repo Gotchas for Agents
- `.gitignore` no longer ignores markdown files; updates to `AGENTS.md` and `TASKS.md` are tracked normally.
- There is a stray text file `composeApp/src/androidMain/kotlin/com/example/silvahub/Sync`; treat as artifact, not app source.
