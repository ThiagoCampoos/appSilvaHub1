# Plan: Configurar AppDatabase.kt com Room - Task 1.3

Implementação da camada de persistência central do SilvaHub usando Room Database, criando a classe `AppDatabase` que gerencia as entidades (tabelas), DAOs (acesso a dados) e a configuração do banco SQLite local. Esta task estabelece a fundação para toda a camada de dados do aplicativo.

## Steps

1. **Criar entidades placeholder** em `data/local/entity/` - `SalarioEntity`, `ContaFixaEntity`, `GastoEntity` com apenas `@PrimaryKey(autoGenerate = true) val id: Long = 0`

2. **Criar DAOs placeholder** em `data/local/dao/` - `SalarioDao`, `ContaFixaDao`, `GastoDao` com uma query básica de exemplo usando `@Query` e retornando `Flow<>`

3. **Adicionar plugin KSP** ao `build.gradle.kts` - aplicar `alias(libs.plugins.ksp)` e adicionar `ksp(libs.androidx.room.compiler)` nas dependências do `androidMain`

4. **Implementar AppDatabase** em `data/local/database/AppDatabase.kt` - classe abstrata com anotação `@Database(entities = [...], version = 1, exportSchema = true)`, métodos abstratos para DAOs, e companion object com função `create(context: Context)`

5. **Configurar schema export** no `build.gradle.kts` - adicionar `ksp { arg("room.schemaLocation", "$projectDir/schemas") }` no bloco `android.defaultConfig` para gerar versionamento do schema

6. **Validar compilação** - executar `./gradlew :composeApp:compileDebugKotlin` e verificar que o Room gerou os arquivos necessários sem erros, confirmar criação de `schemas/` com o JSON do schema v1

## Further Considerations

1. **Namespace das entidades** - Usar `tableName` explícito nas anotações `@Entity` (ex: `@Entity(tableName = "salarios")`) para evitar nomes de tabelas gerados automaticamente com prefixos indesejados?

2. **Estratégia de thread** - Adicionar `.setQueryExecutor(Executors.newSingleThreadExecutor())` no builder do Room ou confiar no dispatcher padrão do Kotlin Coroutines para operações de banco?

3. **Testes de validação** - Criar um teste instrumental básico em `androidUnitTest/` para verificar criação do banco ou deixar para Fase 2 quando as entidades tiverem campos reais?

4. **Logging de queries** - Adicionar `.setQueryCallback()` para debug durante desenvolvimento ou manter sem logging por enquanto?

## Detailed Implementation Guide

### Step 1: Criar Entidades Placeholder

**Localização:** `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/entity/`

**Arquivos a criar:**
- `SalarioEntity.kt`
- `ContaFixaEntity.kt`
- `GastoEntity.kt`

**Template de implementação:**
```kotlin
package com.example.silvahub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salarios")
data class SalarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    // Campos serão adicionados na Fase 2
)
```

**Justificativa:**
- Uso de `tableName` explícito garante nomes de tabelas previsíveis no SQLite
- `autoGenerate = true` permite inserção sem especificar ID manualmente
- Valor padrão `= 0` indica ao Room para gerar novo ID

**Tempo estimado:** 5 minutos

---

### Step 2: Criar DAOs Placeholder

**Localização:** `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/`

**Arquivos a criar:**
- `SalarioDao.kt`
- `ContaFixaDao.kt`
- `GastoDao.kt`

**Template de implementação:**
```kotlin
package com.example.silvahub.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.silvahub.data.local.entity.SalarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalarioDao {
    // Query placeholder para validar compilação
    @Query("SELECT * FROM salarios LIMIT 1")
    fun obterSalario(): Flow<SalarioEntity?>
}
```

**Justificativa:**
- DAOs como interfaces permite que Room gere implementação automaticamente
- Uso de `Flow<>` para observabilidade reativa (dados atualizam UI automaticamente)
- Query básica permite validar que Room está processando corretamente

**Tempo estimado:** 5 minutos

---

### Step 3: Adicionar Plugin KSP ao build.gradle.kts

**Localização:** `composeApp/build.gradle.kts`

**Mudanças necessárias:**

1. Adicionar plugin KSP no bloco `plugins`:
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp) // NOVO
}
```

2. Adicionar dependências Room no bloco `kotlin.sourceSets`:
```kotlin
sourceSets {
    androidMain.dependencies {
        implementation("androidx.activity:activity-compose:1.9.3")
        
        // Room
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
    }
}
```

3. Adicionar configuração KSP após o bloco `kotlin`:
```kotlin
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.10.0")
}
```

**Justificativa:**
- KSP (Kotlin Symbol Processing) é necessário para Room gerar código em tempo de compilação
- `kspAndroid` específico para Android target no projeto multiplatform
- `room-compiler` processa anotações `@Entity`, `@Dao`, `@Database`

**Tempo estimado:** 3 minutos

---

### Step 4: Implementar AppDatabase

**Localização:** `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/database/AppDatabase.kt`

**Implementação completa:**
```kotlin
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

/**
 * Banco de dados principal do SilvaHub.
 *
 * Responsável por gerenciar todas as entidades e fornecer acesso aos DAOs.
 *
 * @property salarioDao DAO para operações com salários
 * @property contaFixaDao DAO para operações com contas fixas
 * @property gastoDao DAO para operações com gastos
 */
@Database(
    entities = [
        SalarioEntity::class,
        ContaFixaEntity::class,
        GastoEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provê acesso ao DAO de salários
     */
    abstract fun salarioDao(): SalarioDao

    /**
     * Provê acesso ao DAO de contas fixas
     */
    abstract fun contaFixaDao(): ContaFixaDao

    /**
     * Provê acesso ao DAO de gastos
     */
    abstract fun gastoDao(): GastoDao

    companion object {
        /**
         * Nome do arquivo de banco de dados SQLite
         */
        private const val DATABASE_NAME = "silvahub.db"

        /**
         * Cria uma instância do banco de dados.
         *
         * @param context Contexto da aplicação Android
         * @return Instância configurada do AppDatabase
         */
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context = context.applicationContext,
                klass = AppDatabase::class.java,
                name = DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // ⚠️ Apenas para desenvolvimento
                .build()
        }
    }
}
```

**Detalhamento técnico:**

**@Database annotation:**
- `entities`: Array de classes marcadas com `@Entity` que serão tabelas
- `version`: Número inteiro para controle de migrações (incrementar quando schema mudar)
- `exportSchema`: Gera JSON com schema em `schemas/` para versionamento

**fallbackToDestructiveMigration():**
- **USO APENAS EM DESENVOLVIMENTO**
- Apaga e recria o banco quando detecta mudança de versão
- Em produção será substituído por migrações adequadas na Fase 6

**applicationContext:**
- Evita memory leaks usando contexto da aplicação ao invés de Activity

**Tempo estimado:** 10 minutos

---

### Step 5: Configurar Schema Export

**Localização:** `composeApp/build.gradle.kts`

**Adicionar no bloco `android`:**
```kotlin
android {
    namespace = "com.example.silvahub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.silvahub"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    // NOVO: Configuração KSP para Room
    sourceSets {
        getByName("main") {
            java.srcDirs("build/generated/ksp/android/androidDebug/kotlin")
        }
    }
    
    // ...restante do código
}
```

**Adicionar após o bloco `android`:**
```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

**Justificativa:**
- Schema export permite rastrear mudanças no banco de dados via Git
- Facilita criação de migrações comparando versões diferentes
- Documentação automática da estrutura do banco

**Resultado esperado:**
- Arquivo criado em: `composeApp/schemas/com.example.silvahub.data.local.database.AppDatabase/1.json`

**Tempo estimado:** 3 minutos

---

### Step 6: Validar Compilação

**Comandos a executar:**

```bash
# 1. Sync do Gradle para aplicar mudanças
./gradlew --stop
./gradlew :composeApp:clean

# 2. Compilar com KSP ativo
./gradlew :composeApp:kspDebugKotlinAndroid

# 3. Verificar compilação completa
./gradlew :composeApp:compileDebugKotlin

# 4. Build do app
./gradlew :composeApp:assembleDebug
```

**Verificações pós-compilação:**

1. **Arquivos gerados pelo Room:**
   - Verificar em `composeApp/build/generated/ksp/android/androidDebug/kotlin/`
   - Devem existir: `AppDatabase_Impl.kt`, `SalarioDao_Impl.kt`, etc.

2. **Schema exportado:**
   - Verificar arquivo em `composeApp/schemas/.../1.json`
   - Deve conter definição das 3 tabelas (salarios, contas_fixas, gastos)

3. **Sem erros de compilação:**
   - Gradle deve completar com `BUILD SUCCESSFUL`
   - Nenhum erro relacionado a Room ou KSP

**Tempo estimado:** 4 minutos

---

## Critérios de Aceitação

### Checklist de Validação

- [ ] **3 entidades criadas** - `SalarioEntity`, `ContaFixaEntity`, `GastoEntity` em `data/local/entity/`
- [ ] **3 DAOs criados** - `SalarioDao`, `ContaFixaDao`, `GastoDao` em `data/local/dao/`
- [ ] **Plugin KSP adicionado** - Em `build.gradle.kts` no bloco `plugins`
- [ ] **Dependências Room adicionadas** - runtime, ktx, e compiler (via KSP)
- [ ] **AppDatabase implementado** - Classe abstrata em `data/local/database/AppDatabase.kt`
- [ ] **Anotação @Database aplicada** - Com version = 1 e exportSchema = true
- [ ] **3 métodos abstratos de DAO** - salarioDao(), contaFixaDao(), gastoDao()
- [ ] **Companion object com create()** - Função factory para criar instância
- [ ] **fallbackToDestructiveMigration configurado** - Para desenvolvimento
- [ ] **Schema export configurado** - Via KSP args no build.gradle.kts
- [ ] **Projeto compila sem erros** - `./gradlew :composeApp:compileDebugKotlin` com sucesso
- [ ] **Arquivo de schema gerado** - JSON em `schemas/` versão 1

### Estrutura Final de Arquivos

```
composeApp/
├── build.gradle.kts (modificado - KSP plugin e dependências)
├── schemas/
│   └── com.example.silvahub.data.local.database.AppDatabase/
│       └── 1.json (gerado automaticamente)
└── src/
    └── androidMain/
        └── kotlin/
            └── com/
                └── example/
                    └── silvahub/
                        └── data/
                            └── local/
                                ├── dao/
                                │   ├── ContaFixaDao.kt (novo)
                                │   ├── GastoDao.kt (novo)
                                │   └── SalarioDao.kt (novo)
                                ├── database/
                                │   └── AppDatabase.kt (novo)
                                └── entity/
                                    ├── ContaFixaEntity.kt (novo)
                                    ├── GastoEntity.kt (novo)
                                    └── SalarioEntity.kt (novo)
```

---

## Troubleshooting

### Problema: "Cannot find symbol class SalarioEntity"

**Causa:** Entidades não foram criadas antes de compilar AppDatabase

**Solução:**
1. Criar todas as entidades primeiro (Step 1)
2. Executar `./gradlew :composeApp:build --refresh-dependencies`
3. Rebuild do projeto

---

### Problema: "KSP plugin not found"

**Causa:** Plugin KSP não está configurado corretamente em `libs.versions.toml`

**Solução:**
1. Verificar se `ksp = { id = "com.google.devtools.ksp", version = "2.3.0-1.0.30" }` existe em `[plugins]`
2. Sync Gradle novamente

---

### Problema: "Schema export directory is not provided"

**Causa:** Configuração KSP para Room não foi aplicada

**Solução:**
1. Adicionar bloco `ksp { arg("room.schemaLocation", "$projectDir/schemas") }`
2. Limpar build cache: `./gradlew clean`
3. Rebuild

---

### Problema: "Database must have DAOs"

**Causa:** DAOs não foram criados ou métodos abstratos estão ausentes

**Solução:**
1. Criar todas as interfaces DAO (Step 2)
2. Verificar que AppDatabase tem métodos abstratos correspondentes
3. Rebuild

---

## Próximos Passos

Após conclusão desta task:

1. **Task 1.4:** Criar `Color.kt` personalizado para tema financeiro
2. **Task 1.5:** Criar `Theme.kt` com Material3 customizado
3. **Task 1.6:** Configurar Koin para injeção de dependências (incluindo AppDatabase)
4. **Fase 2:** Implementar campos reais nas entidades e métodos completos nos DAOs

---

## Commit Sugerido

```bash
git add .
git commit -m "feat(phase1): configure AppDatabase with Room

- Create placeholder entities (SalarioEntity, ContaFixaEntity, GastoEntity)
- Create placeholder DAOs (SalarioDao, ContaFixaDao, GastoDao)
- Add KSP plugin and Room dependencies to build.gradle.kts
- Configure Room database with version 1
- Add fallbackToDestructiveMigration for development
- Implement database factory method in companion object
- Configure schema export for version control

Related to Task 1.3"
```

---

## Estimativa de Tempo Total

| Subtarefa | Tempo Estimado |
|-----------|----------------|
| Criar entidades placeholder | 5 min |
| Criar DAOs placeholder | 5 min |
| Adicionar plugin KSP | 3 min |
| Implementar AppDatabase | 10 min |
| Configurar schema export | 3 min |
| Testes e validação | 4 min |
| **TOTAL** | **30 min** |

---

## Notas Técnicas

### Por que Room Database?

1. **Type-safe queries** - Erros de SQL detectados em tempo de compilação
2. **Observabilidade** - Integração nativa com Flow/LiveData
3. **Migrações automáticas** - Facilita evolução do schema
4. **Performance** - Otimizado para Android, cache eficiente
5. **Testabilidade** - Fácil criar versões in-memory para testes

### Estratégia de Migrations

**Fase 1 (atual):** `fallbackToDestructiveMigration()` - Apaga e recria

**Fase 6 (produção):**
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE salarios ADD COLUMN valor_liquido REAL NOT NULL DEFAULT 0.0")
    }
}

Room.databaseBuilder(...)
    .addMigrations(MIGRATION_1_2)
    .build()
```

### Alternativas Consideradas

**SQLDelight:** Mais controle sobre SQL, mas menos integrações Android
**Realm:** Performance superior, mas curva de aprendizado maior
**DataStore:** Apenas para preferências simples, não para dados relacionais

**Decisão:** Room pela melhor integração com ecossistema Android e Jetpack Compose

---

**Status:** ⏳ Pronto para implementação  
**Dependências:** Task 1.2 (estrutura de pacotes) ✅  
**Prioridade:** 🔴 Crítica (bloqueia Fase 2)

