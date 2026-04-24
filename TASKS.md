
# SilvaHub - Estrutura de Tarefas por Fases

> **Documento de rastreamento de tarefas para o App de Finanças para Desenvolvedores**
> 
> Última atualização: 2026-02-19  
> Status Geral: ⏳ Planejamento Completo - Pronto para Iniciar Fase 1

---

## 📋 Visão Geral das Fases

| Fase | Título | Duração | Status         | Prioridade |
|------|--------|---------|----------------|-----------|
| 1 | Setup e Fundação | 2-3 dias | ⏳ Iniciada      | 🔴 Crítica |
| 2 | Gestão de Salário e Contas Fixas | 3-4 dias | ⏳ Não Iniciada | 🔴 Crítica |
| 3 | Gastos Rápidos | 2-3 dias | ⏳ Não Iniciada | 🟡 Alta |
| 4 | Dashboard e Cálculos Financeiros | 3-4 dias | ⏳ Não Iniciada | 🟡 Alta |
| 5 | Navegação e Histórico | 2-3 dias | ⏳ Não Iniciada | 🟡 Alta |
| 6 | Recursos Avançados e Polimento | 3-4 dias | ⏳ Não Iniciada | 🟢 Média |

**Estimativa Total:** 15-21 dias de desenvolvimento  
**Fases Críticas:** Fase 2 e Fase 4 (maior complexidade)  
**Buffer Recomendado:** +20% para imprevistos e refatorações

---

## 🚀 FASE 1: Setup e Fundação

**Duração:** 2-3 dias  
**Status:** ⏳ Não Iniciada  
**Objetivos Principais:**
- ✅ Adicionar dependências (Room, Navigation Compose, Koin, Kotlinx Serialization)
- ✅ Criar estrutura completa de pacotes da arquitetura Clean Architecture
- ✅ Configurar `AppDatabase` com Room
- ✅ Implementar Theme customizado com Material3

**Entregável Final:** Projeto compilando com estrutura base e tema funcional

---

### Task 1.1: Atualizar libs.versions.toml com Dependências

**Descrição:** Adicionar todas as dependências necessárias ao arquivo de gerenciamento de versões

**Subtarefas:**
- [x] Adicionar versão do Room (runtime, compiler, ktx)
- [x] Adicionar versão do Navigation Compose
- [x] Adicionar versão do Koin (core, android)
- [x] Adicionar versão do Kotlinx Serialization
- [x] Adicionar versão do Lifecycle ViewModel Compose
- [x] Adicionar versão do Material3
 
**Dependências:** Nenhuma  
**Critério de Aceitação:**
- [x] `libs.versions.toml` compilando sem erros
- [x] Todas as versões definidas e atualizadas em `build.gradle.kts`
- [x] Projeto sincroniza corretamente no Android Studio

**Commit Sugerido:** `feat(phase1): add dependencies to libs.versions.toml`

---

### Task 1.2: Criar Estrutura de Pacotes da Arquitetura

**Descrição:** Criar todos os pacotes base da Clean Architecture em `androidMain/kotlin/com/example/silvahub/`

**Estrutura de Pacotes:**
```
com.example.silvahub/
├── data/
│   ├── local/
│   │   ├── entity/
│   │   ├── dao/
│   │   └── database/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── ui/
│   ├── theme/
│   ├── navigation/
│   ├── screens/
│   │   ├── home/
│   │   ├── configuracoes/
│   │   └── gastos/
│   └── components/
├── di/
├── util/
└── App.kt
```

**Subtarefas:**
- [x] Criar pacote `data` e subpacotes (`local/entity`, `local/dao`, `local/database`, `repository`)
- [x] Criar pacote `domain` e subpacotes (`model`, `repository`, `usecase`)
- [x] Criar pacote `ui` e subpacotes (`theme`, `navigation`, `screens/*`, `components`)
- [x] Criar pacote `di` para injeção de dependências
- [x] Criar pacote `util` para extensões e utilitários

**Estimativa:** 15 minutos  ********
**Dependências:** Task 1.1 (compilação deve estar OK)  
**Critério de Aceitação:**
- [x] Todos os 13+ pacotes criados
- [x] Estrutura visível no Android Studio
- [x] Projeto compila sem erros de pacote

**Commit Sugerido:** `feat(phase1): create project package structure`

---

### Task 1.3: Configurar AppDatabase.kt com Room

**Descrição:** Implementar a classe `AppDatabase` com configuração do Room e migrations strategy

**Subtarefas:**
- [x] Criar arquivo `AppDatabase.kt` em `data/local/database/`
- [x] Adicionar anotação `@Database` com versão 1
- [x] Definir entidades vazias (placeholder para `SalarioEntity`, `ContaFixaEntity`, `GastoEntity`)
- [x] Adicionar DAOs abstract (placeholder)
- [x] Configurar `fallbackToDestructiveMigration()` para desenvolvimento
- [x] Adicionar companion object com função para criar instância do banco

**Código Esperado:**
```kotlin
@Database(
    entities = [SalarioEntity::class, ContaFixaEntity::class, GastoEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun salarioDao(): SalarioDao
    abstract fun contaFixaDao(): ContaFixaDao
    abstract fun gastoDao(): GastoDao

    companion object {
        fun create(context: Context): AppDatabase {
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
```

**Estimativa:** 30 minutos  
**Dependências:** Task 1.2 (estrutura de pacotes)  
**Critério de Aceitação:**
- [x] `AppDatabase.kt` criado e compilando
- [x] Anotações Room aplicadas corretamente
- [x] Função `create()` implementada
- [x] Nenhum erro de compilação

**Commit Sugerido:** `feat(phase1): configure AppDatabase with Room`

---

### Task 1.4: Criar Color.kt Personalizado

**Descrição:** Definir paleta de cores customizada para o tema financeiro

**Subtarefas:**
- [x] Criar arquivo `Color.kt` em `ui/theme/`
- [x] Definir cores primárias (verde para saldo positivo)
- [x] Definir cores secundárias (vermelho para saldo negativo/gastos)
- [x] Definir cores neutras (cinza para fundo, preto/branco para texto)
- [x] Definir cores de status (sucesso, erro, aviso, info)

**Cores Sugeridas:**
```kotlin
// Primária (Verde - Saldo Positivo)
val PrimaryGreen = Color(0xFF2E7D32)
val PrimaryGreenLight = Color(0xFF66BB6A)
val PrimaryGreenDark = Color(0xFF1B5E20)

// Secundária (Vermelho - Saldo Negativo)
val SecondaryRed = Color(0xFFC62828)
val SecondaryRedLight = Color(0xFFEF5350)
val SecondaryRedDark = Color(0xFF7F0000)

// Neutras
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF212121)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF303030)

// Textos
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
val TextTertiary = Color(0xFFBDBDBD)
```

**Estimativa:** 20 minutos  
**Dependências:** Task 1.2 (estrutura de pacotes)  
**Critério de Aceitação:**
- [x] `Color.kt` criado com todas as cores
- [x] Nomeação clara e consistente
- [x] Compilando sem erros

**Commit Sugerido:** `feat(phase1): create custom color palette`

---

### Task 1.5: Criar Theme.kt Personalizado

**Descrição:** Implementar o tema Material3 customizado com cores definidas e tipografia

**Subtarefas:**
- [x] Criar arquivo `Theme.kt` em `ui/theme/`
- [x] Implementar `LightColorScheme()` com cores do `Color.kt`
- [x] Implementar `DarkColorScheme()` com variações para modo escuro
- [x] Criar composable `SilvaHubTheme()` que aplica o tema
- [x] Adicionar suporte a modo claro/escuro automático
- [x] Integrar com `Type.kt` (tipografia - criar depois)

**Estrutura Esperada:**
```kotlin
private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryRed,
    background = BackgroundLight,
    surface = SurfaceLight,
    // ... outras cores
)

@Composable
fun SilvaHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SilvaHubTypography,
        content = content
    )
}
```

**Estimativa:** 30 minutos  
**Dependências:** Task 1.4 (Color.kt)  
**Critério de Aceitação:**
- [X] `Theme.kt` criado e compilando
- [X] Suporta Light e Dark mode
- [X] Aplicável via `SilvaHubTheme { }`

**Commit Sugerido:** `feat(phase1): create Material3 theme`

---

### Task 1.6: Criar Type.kt para Tipografia

**Descrição:** Definir tipografia customizada seguindo Material3 design guidelines

**Subtarefas:**
- [x] Criar arquivo `Type.kt` em `ui/theme/`
- [x] Definir `displayLarge`, `displayMedium`, `displaySmall` para títulos grandes
- [x] Definir `headlineLarge`, `headlineMedium`, `headlineSmall` para headings
- [x] Definir `titleLarge`, `titleMedium`, `titleSmall` para títulos de seções
- [x] Definir `bodyLarge`, `bodyMedium`, `bodySmall` para corpo de texto
- [x] Definir `labelLarge`, `labelMedium`, `labelSmall` para labels

**Tipografia Sugerida:**
```kotlin
val SilvaHubTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp
    ),
)
```

**Estimativa:** 25 minutos  
**Dependências:** Task 1.2 (estrutura de pacotes)  
**Critério de Aceitação:**
- [x] `Type.kt` criado com tipografia completa
- [x] Compilando sem erros
- [x] Referenciado em `Theme.kt`

**Commit Sugerido:** `feat(phase1): create typography styles`

---

### Task 1.7: Criar App.kt Principal

**Descrição:** Implementar a composable `App` que será o ponto de entrada da aplicação

**Subtarefas:**
- [x] Criar arquivo `App.kt` em `com/example/silvahub/`
- [x] Criar composable `App()` que encapsula o tema
- [x] Configurar navegação básica (placeholder para NavHost)
- [x] Envolver com `SilvaHubTheme`
- [x] Adicionar Surface como background

**Estrutura Esperada:**
```kotlin
@Composable
fun App() {
    SilvaHubTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // NavHost será adicionado na Fase 5
            HomeScreen() // Placeholder
        }
    }
}
```

**Estimativa:** 20 minutos  
**Dependências:** Task 1.5 (Theme.kt)  
**Critério de Aceitação:**
- [x] `App.kt` criado e compilando
- [x] Encapsula o tema corretamente
- [x] Pronto para integração de navegação

**Commit Sugerido:** `feat(phase1): create main App composable`

---

### Task 1.8: Atualizar MainActivity.kt para Usar App

**Descrição:** Integrar a composable `App` na `MainActivity`

**Subtarefas:**
- [x] Abrir `MainActivity.kt` em `androidMain/`
- [x] Manter classe que estende `ComponentActivity`
- [x] No `onCreate()`, usar `setContent { App() }`
- [x] Remover qualquer composable antigo de placeholder
- [x] Testar compilação

**Estrutura Esperada:**
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
```

**Estimativa:** 15 minutos  
**Dependências:** Task 1.7 (App.kt)  
**Critério de Aceitação:**
- [x] `MainActivity.kt` atualizado
- [x] Aplicação compila sem erros
- [x] Pronto para teste no emulador

**Commit Sugerido:** `feat(phase1): integrate App composable in MainActivity`

---

### Task 1.9: Configurar Koin para Injeção de Dependências

**Descrição:** Configurar módulo Koin básico para gerenciar dependências

**Subtarefas:**
- [x] Criar arquivo `KoinModule.kt` em `di/`
- [x] Definir módulo `appModule`
- [x] Registrar `AppDatabase` como singleton
- [x] Registrar DAOs como singletons (vazios por enquanto)
- [x] Criar `App.kt` em `androidMain/` (Application class) para inicializar Koin
- [x] Adicionar `android:name` em `AndroidManifest.xml` apontando para a classe Application

**Código Esperado:**
```kotlin
// KoinModule.kt
val appModule = module {
    single { AppDatabase.create(get()) }
    single { get<AppDatabase>().salarioDao() }
    single { get<AppDatabase>().contaFixaDao() }
    single { get<AppDatabase>().gastoDao() }
}

// SilvaHubApp.kt
class SilvaHubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SilvaHubApp)
            modules(appModule)
        }
    }
}
```

**Estimativa:** 30 minutos  
**Dependências:** Task 1.3 (AppDatabase.kt)  
**Critério de Aceitação:**
- [x] `KoinModule.kt` criado com módulo completo
- [x] `SilvaHubApp` classe criada e compilando
- [x] `AndroidManifest.xml` atualizado
- [x] Projeto compila sem erros

**Commit Sugerido:** `feat(phase1): setup Koin dependency injection`

---

### Task 1.10: Criar Arquivo de Documentação FASE_1.md

**Descrição:** Documentar objetivos alcançados, decisões arquiteturais e próximos passos da Fase 1

**Subtarefas:**
- [x] Criar pasta `docs/phases/` se não existir
- [x] Criar arquivo `FASE_1_Setup.md`
- [x] Documentar objetivos completados
- [x] Explicar decisões arquiteturais (por que Koin, por que Clean Architecture)
- [x] Listar dependências adicionadas e versões
- [x] Criar seção de próximos passos (Fase 2)
- [x] Adicionar diagrama de estrutura de pacotes (ASCII ou referência)

**Estrutura Esperada:**
```markdown
# FASE 1: Setup e Fundação

## Objetivos Alcançados
- ✅ Estrutura de pacotes criada
- ✅ Room Database configurado
- ✅ Tema Material3 customizado
- ✅ Koin configurado

## Decisões Arquiteturais
### Padrão de Arquitetura
...

### Biblioteca de DI
...

## Dependências Adicionadas
...

## Próximos Passos (Fase 2)
...
```

**Estimativa:** 30 minutos  
**Dependências:** Todas as tasks anteriores  
**Critério de Aceitação:**
- [x] `FASE_1_Setup.md` criado em `docs/phases/`
- [x] Documentação clara e completa
- [x] Referencia todas as decisões técnicas

**Commit Sugerido:** `docs(phase1): add phase 1 documentation`

---

### Task 1.11: Teste de Compilação Completo

**Descrição:** Garantir que toda a Fase 1 compila sem erros e funciona

**Subtarefas:**
- [x] Executar `./gradlew clean` para limpar builds anteriores
- [x] Executar `./gradlew build` para compilar o projeto completo
- [x] Verificar ausência de erros e warnings críticos
- [x] Testar no emulador/dispositivo (build debug)
- [x] Verificar se o app abre sem crashes

**Estimativa:** 15 minutos  
**Dependências:** Todas as tasks anteriores  
**Critério de Aceitação:**
- [x] `./gradlew build` executa com sucesso
- [x] App compila sem erros
- [x] App abre no emulador/dispositivo
- [x] Nenhum crash ao iniciar

**Commit Sugerido:** `test(phase1): verify full compilation and app launch`

---

### Task 1.12: Criar .gitignore Atualizado (Se Necessário)

**Descrição:** Garantir que arquivos gerados pelo Room, Koin e build não são commitados

**Subtarefas:**
- [x] Verificar existência de `.gitignore`
- [x] Adicionar padrões para arquivos gerados (build/, .gradle/)
- [x] Adicionar padrões para IDE (.idea/, *.iml)
- [x] Garantir que `local.properties` está ignorado

**Estimativa:** 10 minutos  
**Dependências:** Nenhuma crítica  
**Critério de Aceitação:**
- [x] `.gitignore` contém padrões corretos
- [x] Arquivos gerados não aparecem em `git status`

**Commit Sugerido:** `chore(phase1): update gitignore`

---

## ✅ Checklist de Conclusão - Fase 1

- [x] Task 1.1: libs.versions.toml atualizado
- [x] Task 1.2: Estrutura de pacotes criada
- [x] Task 1.3: AppDatabase configurado
- [x] Task 1.4: Color.kt personalizado
- [x] Task 1.5: Theme.kt customizado
- [x] Task 1.6: Type.kt implementado
- [x] Task 1.7: App.kt criado
- [x] Task 1.8: MainActivity.kt atualizado
- [x] Task 1.9: Koin configurado
- [x] Task 1.10: Documentação FASE_1.md
- [x] Task 1.11: Teste de compilação OK
- [x] Task 1.12: .gitignore atualizado

**Tempo Total Estimado:** 2-3 dias  
**Status:** ⏳ Pronto para Iniciar

---

---

## 🏦 FASE 2: Gestão de Salário e Contas Fixas

**Duração:** 3-4 dias  
**Status:** ⏳ Não Iniciada  
**Pré-requisitos:** Fase 1 Completa  
**Objetivos Principais:**
- ✅ Criar entidades `SalarioEntity` e `ContaFixaEntity` com anotações Room
- ✅ Implementar DAOs com queries específicas
- ✅ Desenvolver repositórios e use cases
- ✅ Criar `ConfiguracoesScreen` com formulários
- ✅ Implementar `ConfiguracoesViewModel` com validações

**Entregável Final:** Tela de configurações salvando dados no Room com validação completa

---

### Task 2.1: Criar SalarioEntity.kt

**Descrição:** Definir entidade Room para armazenar informações de salário

**Subtarefas:**
- [ ] Criar arquivo `SalarioEntity.kt` em `data/local/entity/`
- [ ] Adicionar campos: `id` (Long, PrimaryKey), `valor` (Double), `mesReferencia` (String, formato "YYYY-MM"), `dataCriacao` (Long)
- [ ] Adicionar anotações Room (`@Entity`, `@PrimaryKey`, `@ColumnInfo`)
- [ ] Criar data class com valores padrão

**Código Esperado:**
```kotlin
@Entity(tableName = "salarios")
data class SalarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val valor: Double,
    val mesReferencia: String, // "2024-02"
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis()
)
```

**Estimativa:** 15 minutos  
**Dependências:** Fase 1 completa  
**Critério de Aceitação:**
- [ ] `SalarioEntity.kt` criado com todas as anotações
- [ ] Compilando sem erros
- [ ] Data class com construtor padrão

**Commit Sugerido:** `feat(phase2): create SalarioEntity`

---

### Task 2.2: Criar ContaFixaEntity.kt

**Descrição:** Definir entidade Room para armazenar contas fixas (despesas recorrentes)

**Subtarefas:**
- [ ] Criar arquivo `ContaFixaEntity.kt` em `data/local/entity/`
- [ ] Adicionar campos: `id` (Long, PrimaryKey), `nome` (String), `valor` (Double), `diaVencimento` (Int), `ativa` (Boolean), `dataCriacao` (Long)
- [ ] Adicionar anotações Room
- [ ] Criar data class

**Código Esperado:**
```kotlin
@Entity(tableName = "contas_fixas")
data class ContaFixaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val valor: Double,
    @ColumnInfo(name = "dia_vencimento")
    val diaVencimento: Int, // 1-31
    val ativa: Boolean = true,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis()
)
```

**Estimativa:** 15 minutos  
**Dependências:** Fase 1 completa  
**Critério de Aceitação:**
- [ ] `ContaFixaEntity.kt` criado com todas as anotações
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): create ContaFixaEntity`

---

### Task 2.3: Criar GastoEntity.kt

**Descrição:** Definir entidade Room para armazenar gastos individuais

**Subtarefas:**
- [ ] Criar arquivo `GastoEntity.kt` em `data/local/entity/`
- [ ] Criar enum `TipoGasto` com valores: `RAPIDO`, `FIXO`
- [ ] Criar enum `CategoriaGasto` com valores: `ALIMENTACAO`, `TRANSPORTE`, `LAZER`, `SAUDE`, `EDUCACAO`, `OUTROS`
- [ ] Adicionar campos: `id` (Long, PrimaryKey), `descricao` (String), `valor` (Double), `categoria` (CategoriaGasto), `data` (Long), `tipo` (TipoGasto), `dataCriacao` (Long)

**Código Esperado:**
```kotlin
enum class TipoGasto {
    RAPIDO, FIXO
}

enum class CategoriaGasto {
    ALIMENTACAO, TRANSPORTE, LAZER, SAUDE, EDUCACAO, OUTROS
}

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val categoria: CategoriaGasto,
    val data: Long,
    val tipo: TipoGasto = TipoGasto.RAPIDO,
    @ColumnInfo(name = "data_criacao")
    val dataCriacao: Long = System.currentTimeMillis()
)
```

**Estimativa:** 20 minutos  
**Dependências:** Fase 1 completa  
**Critério de Aceitação:**
- [ ] `GastoEntity.kt` criado com enums e anotações
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): create GastoEntity with enums`

---

### Task 2.4: Implementar SalarioDao.kt

**Descrição:** Criar DAO com queries para operações de salário

**Subtarefas:**
- [ ] Criar arquivo `SalarioDao.kt` em `data/local/dao/`
- [ ] Implementar `@Insert` para inserir novo salário
- [ ] Implementar `@Update` para atualizar salário
- [ ] Implementar `@Delete` para deletar salário
- [ ] Implementar query `getSalarioDoMes(mesAno: String): Flow<SalarioEntity?>`
- [ ] Implementar query `getTodosSalarios(): Flow<List<SalarioEntity>>`
- [ ] Implementar query `getUltimoSalario(): Flow<SalarioEntity?>`

**Código Esperado:**
```kotlin
@Dao
interface SalarioDao {
    @Insert
    suspend fun inserir(salario: SalarioEntity)

    @Update
    suspend fun atualizar(salario: SalarioEntity)

    @Delete
    suspend fun deletar(salario: SalarioEntity)

    @Query("SELECT * FROM salarios WHERE mes_referencia = :mesAno LIMIT 1")
    fun getSalarioDoMes(mesAno: String): Flow<SalarioEntity?>

    @Query("SELECT * FROM salarios ORDER BY data_criacao DESC")
    fun getTodosSalarios(): Flow<List<SalarioEntity>>

    @Query("SELECT * FROM salarios ORDER BY data_criacao DESC LIMIT 1")
    fun getUltimoSalario(): Flow<SalarioEntity?>
}
```

**Estimativa:** 20 minutos  
**Dependências:** Task 2.1 (SalarioEntity)  
**Critério de Aceitação:**
- [ ] `SalarioDao.kt` criado com todas as queries
- [ ] Compilando sem erros Room
- [ ] Retorna `Flow<>` para reatividade

**Commit Sugerido:** `feat(phase2): create SalarioDao with queries`

---

### Task 2.5: Implementar ContaFixaDao.kt

**Descrição:** Criar DAO com queries para operações de contas fixas

**Subtarefas:**
- [ ] Criar arquivo `ContaFixaDao.kt` em `data/local/dao/`
- [ ] Implementar CRUD completo (@Insert, @Update, @Delete)
- [ ] Implementar query `getContasFixasAtivas(): Flow<List<ContaFixaEntity>>`
- [ ] Implementar query `getTotalContasFixas(): Flow<Double>`
- [ ] Implementar query `getTodasAsContas(): Flow<List<ContaFixaEntity>>`
- [ ] Implementar query `getContaPorId(id: Long): Flow<ContaFixaEntity?>`

**Código Esperado:**
```kotlin
@Dao
interface ContaFixaDao {
    @Insert
    suspend fun inserir(conta: ContaFixaEntity)

    @Update
    suspend fun atualizar(conta: ContaFixaEntity)

    @Delete
    suspend fun deletar(conta: ContaFixaEntity)

    @Query("SELECT * FROM contas_fixas WHERE ativa = 1 ORDER BY dia_vencimento ASC")
    fun getContasFixasAtivas(): Flow<List<ContaFixaEntity>>

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM contas_fixas WHERE ativa = 1")
    fun getTotalContasFixas(): Flow<Double>

    @Query("SELECT * FROM contas_fixas ORDER BY dia_vencimento ASC")
    fun getTodasAsContas(): Flow<List<ContaFixaEntity>>

    @Query("SELECT * FROM contas_fixas WHERE id = :id")
    fun getContaPorId(id: Long): Flow<ContaFixaEntity?>
}
```

**Estimativa:** 25 minutos  
**Dependências:** Task 2.2 (ContaFixaEntity)  
**Critério de Aceitação:**
- [ ] `ContaFixaDao.kt` criado com todas as queries
- [ ] Compilando sem erros
- [ ] Queries otimizadas com índices se necessário

**Commit Sugerido:** `feat(phase2): create ContaFixaDao with queries`

---

### Task 2.6: Implementar GastoDao.kt

**Descrição:** Criar DAO com queries para operações de gastos

**Subtarefas:**
- [ ] Criar arquivo `GastoDao.kt` em `data/local/dao/`
- [ ] Implementar CRUD completo
- [ ] Implementar query `getGastosPorPeriodo(dataInicio: Long, dataFim: Long): Flow<List<GastoEntity>>`
- [ ] Implementar query `getGastosPorCategoria(categoria: CategoriaGasto): Flow<List<GastoEntity>>`
- [ ] Implementar query `getTotalGastosDoMes(mesAno: String): Flow<Double>`
- [ ] Implementar query `getUltimosGastos(limit: Int = 10): Flow<List<GastoEntity>>`

**Código Esperado:**
```kotlin
@Dao
interface GastoDao {
    @Insert
    suspend fun inserir(gasto: GastoEntity)

    @Update
    suspend fun atualizar(gasto: GastoEntity)

    @Delete
    suspend fun deletar(gasto: GastoEntity)

    @Query("""
        SELECT * FROM gastos 
        WHERE data BETWEEN :dataInicio AND :dataFim 
        ORDER BY data DESC
    """)
    fun getGastosPorPeriodo(dataInicio: Long, dataFim: Long): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE categoria = :categoria ORDER BY data DESC")
    fun getGastosPorCategoria(categoria: CategoriaGasto): Flow<List<GastoEntity>>

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM gastos WHERE data BETWEEN :dataInicio AND :dataFim")
    fun getTotalGastosPorPeriodo(dataInicio: Long, dataFim: Long): Flow<Double>

    @Query("SELECT * FROM gastos ORDER BY data DESC LIMIT :limit")
    fun getUltimosGastos(limit: Int): Flow<List<GastoEntity>>
}
```

**Estimativa:** 25 minutos  
**Dependências:** Task 2.3 (GastoEntity)  
**Critério de Aceitação:**
- [ ] `GastoDao.kt` criado com todas as queries
- [ ] Compilando sem erros
- [ ] Suporta filtros por período e categoria

**Commit Sugerido:** `feat(phase2): create GastoDao with queries`

---

### Task 2.7: Criar Modelos de Domínio

**Descrição:** Criar data classes de domínio (separadas das entidades Room)

**Subtarefas:**
- [ ] Criar arquivo `Salario.kt` em `domain/model/`
- [ ] Criar arquivo `ContaFixa.kt` em `domain/model/`
- [ ] Criar arquivo `Gasto.kt` em `domain/model/`
- [ ] Adicionar extensões para converter Entity → Domain Model
- [ ] Criar arquivo `ResumoFinanceiro.kt` (para Fase 4)

**Código Esperado:**
```kotlin
// domain/model/Salario.kt
data class Salario(
    val id: Long,
    val valor: Double,
    val mesReferencia: String,
    val dataCriacao: Long
)

// Extensão para conversão
fun SalarioEntity.toDomain() = Salario(
    id = id,
    valor = valor,
    mesReferencia = mesReferencia,
    dataCriacao = dataCriacao
)
```

**Estimativa:** 20 minutos  
**Dependências:** Tasks 2.1, 2.2, 2.3 (Entities)  
**Critério de Aceitação:**
- [ ] Modelos criados em `domain/model/`
- [ ] Extensões de conversão implementadas
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): create domain models`

---

### Task 2.8: Criar Interfaces de Repositório

**Descrição:** Definir contratos (interfaces) dos repositórios

**Subtarefas:**
- [ ] Criar arquivo `SalarioRepository.kt` em `domain/repository/`
- [ ] Criar arquivo `ContaFixaRepository.kt` em `domain/repository/`
- [ ] Definir métodos abstratos para operações (insert, update, delete, query)
- [ ] Usar tipos de domínio (não Entity)
- [ ] Retornar `Flow<>` para reatividade

**Código Esperado:**
```kotlin
// domain/repository/SalarioRepository.kt
interface SalarioRepository {
    suspend fun salvarSalario(salario: Salario)
    suspend fun atualizarSalario(salario: Salario)
    suspend fun deletarSalario(id: Long)
    fun getSalarioDoMes(mesAno: String): Flow<Salario?>
    fun getTodosSalarios(): Flow<List<Salario>>
    fun getUltimoSalario(): Flow<Salario?>
}
```

**Estimativa:** 20 minutos  
**Dependências:** Task 2.7 (Domain Models)  
**Critério de Aceitação:**
- [ ] Interfaces criadas em `domain/repository/`
- [ ] Métodos bem definidos
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): create repository interfaces`

---

### Task 2.9: Implementar Repositórios de Dados

**Descrição:** Implementar as interfaces de repositório com chamadas aos DAOs

**Subtarefas:**
- [ ] Criar arquivo `SalarioRepositoryImpl.kt` em `data/repository/`
- [ ] Criar arquivo `ContaFixaRepositoryImpl.kt` em `data/repository/`
- [ ] Injetar DAOs via constructor
- [ ] Implementar métodos chamando DAO e convertendo de Entity para Domain
- [ ] Tratar exceções e logging

**Código Esperado:**
```kotlin
// data/repository/SalarioRepositoryImpl.kt
class SalarioRepositoryImpl(
    private val dao: SalarioDao
) : SalarioRepository {
    override suspend fun salvarSalario(salario: Salario) {
        dao.inserir(salario.toEntity())
    }

    override fun getSalarioDoMes(mesAno: String): Flow<Salario?> {
        return dao.getSalarioDoMes(mesAno).map { it?.toDomain() }
    }
    // ... outros métodos
}
```

**Estimativa:** 30 minutos  
**Dependências:** Tasks 2.4, 2.5, 2.8 (DAOs e interfaces)  
**Critério de Aceitação:**
- [ ] Repositórios implementados em `data/repository/`
- [ ] Conversão Entity ↔ Domain funcionando
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): implement repository classes`

---

### Task 2.10: Criar Use Cases

**Descrição:** Implementar casos de uso para salário e contas fixas

**Subtarefas:**
- [ ] Criar arquivo `SalvarSalarioUseCase.kt` em `domain/usecase/`
- [ ] Criar arquivo `ObterSalarioDoMesUseCase.kt` em `domain/usecase/`
- [ ] Criar arquivo `GerenciarContasFixasUseCase.kt` em `doma****in/usecase/`
- [ ] Cada use case recebe repositório via constructor
- [ ] Implementar lógica de negócio e validações

**Código Esperado:**
```kotlin
// domain/usecase/SalvarSalarioUseCase.kt
class SalvarSalarioUseCase(
    private val salarioRepository: SalarioRepository
) {
    suspend operator fun invoke(salario: Salario) {
        require(salario.valor > 0) { "Salário deve ser maior que zero" }
        salarioRepository.salvarSalario(salario)
    }
}
```

**Estimativa:** 30 minutos  
**Dependências:** Task 2.9 (Repositórios)  
**Critério de Aceitação:**
- [ ] Use cases criados em `domain/usecase/`
- [ ] Validações de negócio implementadas
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): create use cases`

---

### Task 2.11: Criar ConfiguracoesViewModel

**Descrição:** Implementar ViewModel para gerenciar estado da tela de configurações

**Subtarefas:**
- [ ] Criar arquivo `ConfiguracoesViewModel.kt` em `ui/screens/configuracoes/`
- [ ] Estender `ViewModel()`
- [ ] Injetar use cases via constructor
- [ ] Criar `StateFlow<UiState>` para gerenciar estado
- [ ] Implementar `fun salvarSalario(valor: Double)`
- [ ] Implementar `fun adicionarContaFixa(conta: ContaFixa)`
- [ ] Implementar `fun editarContaFixa(conta: ContaFixa)`
- [ ] Implementar `fun deletarContaFixa(id: Long)`
- [ ] Adicionar tratamento de erros

**Código Esperado:**
```kotlin
data class ConfiguracoesUiState(
    val salarioMensal: Double? = null,
    val contasFixas: List<ContaFixa> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ConfiguracoesViewModel(
    private val salvarSalarioUseCase: SalvarSalarioUseCase,
    private val obterSalarioUseCase: ObterSalarioDoMesUseCase,
    private val gerenciarContasFixasUseCase: GerenciarContasFixasUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConfiguracoesUiState())
    val uiState: StateFlow<ConfiguracoesUiState> = _uiState.asStateFlow()

    fun salvarSalario(valor: Double) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                // Salvar lógica
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
    // ... outros métodos
}
```

**Estimativa:** 45 minutos  
**Dependências:** Task 2.10 (Use cases)  
**Critério de Aceitação:**
- [ ] `ConfiguracoesViewModel.kt` criado
- [ ] StateFlow gerenciando UiState
- [ ] Métodos de ação funcionando
- [ ] Tratamento de erros implementado

**Commit Sugerido:** `feat(phase2): create ConfiguracoesViewModel`

---

### Task 2.12: Criar ConfiguracoesScreen

**Descrição:** Implementar tela Compose com formulários para salário e contas fixas

**Subtarefas:**
- [ ] Criar arquivo `ConfiguracoesScreen.kt` em `ui/screens/configuracoes/`
- [ ] Criar composable `ConfiguracoesScreen(viewModel: ConfiguracoesViewModel)`
- [ ] Implementar seção de "Salário Mensal" com TextField
- [ ] Implementar seção de "Contas Fixas" com LazyColumn
- [ ] Criar Dialog/BottomSheet para adicionar/editar conta fixa
- [ ] Adicionar validação de valores monetários
- [ ] Adicionar botões de ação (Salvar, Deletar, Editar)
- [ ] Adicionar feedback visual (loading, sucesso, erro)

**Componentes Necessários:**
- [ ] `MoneyTextField` - TextField formatado para valores monetários
- [ ] `ItemContaFixa` - Item para lista de contas
- [ ] Dialog de adicionar/editar conta

**Estimativa:** 1 hora  
**Dependências:** Task 2.11 (ViewModel)  
**Critério de Aceitação:**
- [ ] `ConfiguracoesScreen.kt` criado e compilando
- [ ] Formulário de salário funcional
- [ ] Lista de contas fixas com CRUD
- [ ] Validações de input implementadas
- [ ] Feedback visual de sucesso/erro

**Commit Sugerido:** `feat(phase2): create ConfiguracoesScreen`

---

### Task 2.13: Criar Componentes Reutilizáveis

**Descrição:** Implementar componentes que serão usados em múltiplas telas

**Subtarefas:**
- [ ] Criar arquivo `MoneyTextField.kt` em `ui/components/`
- [ ] Implementar formatação de valores monetários em tempo real
- [ ] Validar entrada para apenas números e ponto decimal
- [ ] Criar arquivo `ItemContaFixa.kt` em `ui/components/`
- [ ] Criar arquivo `DialogContaFixa.kt` em `ui/components/`
- [ ] Adicionar suporte a tema (cores e tamanhos dinâmicos)

**Código Esperado:**
```kotlin
@Composable
fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Valor",
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            // Validar e formatar
            val formatted = newValue.filter { it.isDigit() || it == '.' }
            onValueChange(formatted)
        },
        // ... outras props
    )
}
```

**Estimativa:** 45 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Componentes criados em `ui/components/`
- [ ] Compilando sem erros
- [ ] Reutilizáveis em múltiplas telas

**Commit Sugerido:** `feat(phase2): create reusable UI components`

---

### Task 2.14: Integrar Repositórios com Koin

**Descrição:** Registrar repositórios e use cases no módulo Koin

**Subtarefas:**
- [ ] Abrir `KoinModule.kt`
- [ ] Registrar `SalarioRepositoryImpl` como singleton
- [ ] Registrar `ContaFixaRepositoryImpl` como singleton
- [ ] Registrar todos os use cases como singletons
- [ ] Registrar `ConfiguracoesViewModel` como factory
- [ ] Testar injeção de dependências

**Código Esperado:**
```kotlin
val appModule = module {
    // ... existente

    single<SalarioRepository> { SalarioRepositoryImpl(get()) }
    single<ContaFixaRepository> { ContaFixaRepositoryImpl(get()) }
    
    factory { SalvarSalarioUseCase(get()) }
    factory { ObterSalarioDoMesUseCase(get()) }
    factory { GerenciarContasFixasUseCase(get()) }
    
    viewModel { ConfiguracoesViewModel(get(), get(), get()) }
}
```

**Estimativa:** 15 minutos  
**Dependências:** Tasks 2.9, 2.10, 2.11  
**Critério de Aceitação:**
- [ ] Koin module atualizado
- [ ] Projeto compila sem erros de DI
- [ ] ViewModels podem ser injetados

**Commit Sugerido:** `feat(phase2): register repositories and viewmodels in Koin`

---

### Task 2.15: Adicionar Testes Unitários

**Descrição:** Implementar testes para ViewModel e Use Cases

**Subtarefas:**
- [ ] Criar teste para `SalvarSalarioUseCase` (validação de entrada)
- [ ] Criar teste para `ConfiguracoesViewModel` (fluxo de salvar)
- [ ] Usar MockK para mockar repositórios
- [ ] Implementar testes de sucesso e erro
- [ ] Adicionar cobertura de testes mínima (70%)

**Estimativa:** 1 hora  
**Dependências:** Tasks 2.10, 2.11  
**Critério de Aceitação:**
- [ ] Testes criados em `androidUnitTest/`
- [ ] Testes passando com sucesso
- [ ] Cobertura adequada

**Commit Sugerido:** `test(phase2): add unit tests for viewmodel and usecases`

---

### Task 2.16: Atualizar AppDatabase.kt com Entidades Reais

**Descrição:** Atualizar a classe AppDatabase com as entidades criadas

**Subtarefas:**
- [ ] Abrir `AppDatabase.kt`
- [ ] Remover placeholders de entidades
- [ ] Adicionar `SalarioEntity`, `ContaFixaEntity`, `GastoEntity` reais
- [ ] Aumentar versão do banco para 2 (se necessário para migrations)
- [ ] Testar compilação

**Estimativa:** 10 minutos  
**Dependências:** Tasks 2.1, 2.2, 2.3  
**Critério de Aceitação:**
- [ ] `AppDatabase.kt` atualizado com entidades reais
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase2): update AppDatabase with real entities`

---

### Task 2.17: Documentação FASE_2.md

**Descrição:** Documentar objetivos alcançados, decisões arquiteturais e próximos passos

**Subtarefas:**
- [ ] Criar/atualizar `FASE_2_Salario_Contas.md` em `docs/phases/`
- [ ] Documentar entidades criadas
- [ ] Documentar queries de DAOs
- [ ] Listar repositórios e use cases
- [ ] Adicionar diagramas (relação de entidades)
- [ ] Próximos passos (Fase 3)

**Estimativa:** 30 minutos  
**Dependências:** Todas as tasks da Fase 2  
**Critério de Aceitação:**
- [ ] Documentação completa em `docs/phases/`

**Commit Sugerido:** `docs(phase2): add phase 2 documentation`

---

### Task 2.18: Teste de Integração Completo

**Descrição:** Testar fluxo completo de salvar salário e conta fixa

**Subtarefas:**
- [ ] Compile e deploy no emulador
- [ ] Navegar até tela de configurações
- [ ] Inserir salário mensal
- [ ] Adicionar conta fixa
- [ ] Verificar salvamento no banco (Room)
- [ ] Verificar carregamento dos dados
- [ ] Testar edição e deleção

**Estimativa:** 30 minutos  
**Dependências:** Todas as tasks anteriores  
**Critério de Aceitação:**
- [ ] App compila e executa sem erros
- [ ] Dados salvam e carregam corretamente
- [ ] CRUD funcional

**Commit Sugerido:** `test(phase2): verify complete integration flow`

---

## ✅ Checklist de Conclusão - Fase 2

- [ ] Task 2.1: SalarioEntity criada
- [ ] Task 2.2: ContaFixaEntity criada
- [ ] Task 2.3: GastoEntity criada
- [ ] Task 2.4: SalarioDao implementado
- [ ] Task 2.5: ContaFixaDao implementado
- [ ] Task 2.6: GastoDao implementado
- [ ] Task 2.7: Modelos de domínio criados
- [ ] Task 2.8: Interfaces de repositório definidas
- [ ] Task 2.9: Repositórios implementados
- [ ] Task 2.10: Use cases criados
- [ ] Task 2.11: ConfiguracoesViewModel implementado
- [ ] Task 2.12: ConfiguracoesScreen criada
- [ ] Task 2.13: Componentes reutilizáveis criados
- [ ] Task 2.14: Koin atualizado
- [ ] Task 2.15: Testes unitários adicionados
- [ ] Task 2.16: AppDatabase atualizado
- [ ] Task 2.17: Documentação FASE_2.md
- [ ] Task 2.18: Teste de integração OK

**Tempo Total Estimado:** 3-4 dias  
**Status:** ⏳ Pronto após Fase 1

---

---

## 💰 FASE 3: Gastos Rápidos

**Duração:** 2-3 dias  
**Status:** ⏳ Não Iniciada  
**Pré-requisitos:** Fase 2 Completa  
**Objetivos Principais:**
- ✅ Implementar `GastoDao` completo (já iniciado em Fase 2)
- ✅ Desenvolver `GastosViewModel`
- ✅ Criar componentes de UI para input de gastos rápidos
- ✅ Integrar FloatingActionButton na HomeScreen
- ✅ Implementar Dialog/BottomSheet para entry de gastos

**Entregável Final:** Funcionalidade de adicionar gastos rápidos completamente operacional

---

### Task 3.1: Criar GastosRepositoryImpl

**Descrição:** Implementar repositório para operações de gastos

**Subtarefas:**
- [ ] Criar arquivo `GastoRepository.kt` em `domain/repository/`
- [ ] Definir interface com métodos CRUD e queries
- [ ] Criar arquivo `GastoRepositoryImpl.kt` em `data/repository/`
- [ ] Implementar métodos usando `GastoDao`
- [ ] Adicionar conversão Entity → Domain Model

**Estimativa:** 30 minutos  
**Dependências:** Task 2.6 (GastoDao)  
**Critério de Aceitação:**
- [ ] `GastoRepository` interface criada
- [ ] `GastoRepositoryImpl` implementado
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase3): create GastoRepository`

---

### Task 3.2: Criar Use Cases de Gastos

**Descrição:** Implementar use cases para gerenciar gastos

**Subtarefas:**
- [ ] Criar `SalvarGastoUseCase.kt`
- [ ] Criar `AtualizarGastoUseCase.kt`
- [ ] Criar `DeletarGastoUseCase.kt`
- [ ] Criar `ObterGastosPorPeriodoUseCase.kt`
- [ ] Criar `ObterGastosPorCategoriaUseCase.kt`
- [ ] Adicionar validações de negócio

**Estimativa:** 40 minutos  
**Dependências:** Task 3.1 (GastoRepository)  
**Critério de Aceitação:**
- [ ] Use cases criados em `domain/usecase/`
- [ ] Validações implementadas
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase3): create gasto use cases`

---

### Task 3.3: Criar GastosViewModel

**Descrição:** Implementar ViewModel para gerenciar estado de gastos

**Subtarefas:**
- [ ] Criar `GastosViewModel.kt` em `ui/screens/gastos/`
- [ ] Criar `GastosUiState` data class
- [ ] Implementar `StateFlow<GastosUiState>` para estado
- [ ] Implementar método `salvarGasto(gasto: Gasto)`
- [ ] Implementar método `deletarGasto(id: Long)`
- [ ] Implementar método para filtrar por período e categoria
- [ ] Adicionar tratamento de erros

**Estimativa:** 45 minutos  
**Dependências:** Task 3.2 (Use cases)  
**Critério de Aceitação:**
- [ ] ViewModel criado e compilando
- [ ] StateFlow gerenciando estado
- [ ] Métodos de ação funcionando

**Commit Sugerido:** `feat(phase3): create GastosViewModel`

---

### Task 3.4: Criar CategoriaDropdown Composable

**Descrição:** Implementar dropdown para seleção de categoria de gasto

**Subtarefas:**
- [ ] Criar arquivo `CategoriaDropdown.kt` em `ui/components/`
- [ ] Usar `DropdownMenu` do Compose
- [ ] Adicionar ícones para cada categoria
- [ ] Implementar lógica de seleção
- [ ] Adicionar temas (cores dinâmicas)

**Estimativa:** 30 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Dropdown criado e funcional
- [ ] Mostra ícones para categorias
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase3): create CategoriaDropdown component`

---

### Task 3.5: Criar GastoRapidoDialog Composable

**Descrição:** Implementar Dialog para entrada rápida de gastos

**Subtarefas:**
- [ ] Criar arquivo `GastoRapidoDialog.kt` em `ui/screens/gastos/`
- [ ] Implementar campos: `MoneyTextField` para valor, `TextField` para descrição, `CategoriaDropdown` para categoria
- [ ] Adicionar botões "Salvar" e "Cancelar"
- [ ] Validar entrada antes de salvar
- [ ] Adicionar suporte a dismiss (clique fora)
- [ ] Callback para retornar dados ao ViewModel

**Estimativa:** 45 minutos  
**Dependências:** Tasks 3.3, 3.4 (ViewModel, componentes)  
**Critério de Aceitação:**
- [ ] Dialog criado e compilando
- [ ] Campos validados
- [ ] Callback funcionando

**Commit Sugerido:** `feat(phase3): create GastoRapidoDialog`

---

### Task 3.6: Criar GastosScreen com FAB

**Descrição:** Implementar tela de gastos com FloatingActionButton para adicionar novo gasto

**Subtarefas:**
- [ ] Criar arquivo `GastosScreen.kt` em `ui/screens/gastos/`
- [ ] Adicionar FAB na parte inferior direita
- [ ] Ao clicar FAB, abrir `GastoRapidoDialog`
- [ ] Implementar lista de gastos recentes
- [ ] Adicionar filtros (período, categoria)
- [ ] Implementar swipe-to-delete (opcional para Fase 3, prioritário para Fase 4)

**Estimativa:** 1 hora  
**Dependências:** Tasks 3.5, 3.3 (Dialog, ViewModel)  
**Critério de Aceitação:**
- [ ] Tela criada e compilando
- [ ] FAB funcional
- [ ] Dialog abre ao clicar FAB
- [ ] Lista de gastos mostrada

**Commit Sugerido:** `feat(phase3): create GastosScreen with FAB`

---

### Task 3.7: Integrar Repositórios e ViewModels em Koin

**Descrição:** Registrar repositórios de gastos e ViewModel em Koin

**Subtarefas:**
- [ ] Abrir `KoinModule.kt`
- [ ] Registrar `GastoRepositoryImpl` como singleton
- [ ] Registrar todos os use cases de gasto como factories
- [ ] Registrar `GastosViewModel` como factory
- [ ] Testar injeção

**Estimativa:** 15 minutos  
**Dependências:** Tasks 3.1, 3.2, 3.3  
**Critério de Aceitação:**
- [ ] Koin module atualizado
- [ ] Projeto compila sem erros

**Commit Sugerido:** `feat(phase3): register gasto dependencies in Koin`

---

### Task 3.8: Adicionar Testes Unitários para GastosViewModel

**Descrição:** Implementar testes para ViewModel de gastos

**Subtarefas:**
- [ ] Criar testes para `SalvarGastoUseCase` (validação de valor positivo)
- [ ] Criar testes para `GastosViewModel` (fluxo de salvar)
- [ ] Testar erro ao tentar salvar gasto inválido
- [ ] Usar MockK para mockar repositórios

**Estimativa:** 45 minutos  
**Dependências:** Tasks 3.2, 3.3  
**Critério de Aceitação:**
- [ ] Testes criados e passando
- [ ] Cobertura mínima 70%

**Commit Sugerido:** `test(phase3): add unit tests for gastos`

---

### Task 3.9: Integração com HomeScreen (Placeholder)

**Descrição:** Adicionar botão/navegação temporária para acessar GastosScreen

**Subtarefas:**
- [ ] Criar placeholder de `HomeScreen.kt` em `ui/screens/home/`
- [ ] Adicionar botão para navegar para `GastosScreen` (será integrado com Navigation na Fase 5)
- [ ] Compilar e testar

**Estimativa:** 20 minutos  
**Dependências:** Task 3.6 (GastosScreen)  
**Critério de Aceitação:**
- [ ] HomeScreen criado
- [ ] Navegação para GastosScreen funcional (por enquanto)

**Commit Sugerido:** `feat(phase3): create placeholder HomeScreen`

---

### Task 3.10: Documentação FASE_3.md

**Descrição:** Documentar componentes de gastos, use cases e próximos passos

**Subtarefas:**
- [ ] Criar `FASE_3_Gastos_Rapidos.md` em `docs/phases/`
- [ ] Documentar Dialog de entrada
- [ ] Documentar componentes criados
- [ ] Próximos passos (Fase 4)

**Estimativa:** 30 minutos  
**Dependências:** Todas as tasks da Fase 3  
**Critério de Aceitação:**
- [ ] Documentação completa

**Commit Sugerido:** `docs(phase3): add phase 3 documentation`

---

## ✅ Checklist de Conclusão - Fase 3

- [ ] Task 3.1: GastoRepository criado
- [ ] Task 3.2: Use cases de gastos criados
- [ ] Task 3.3: GastosViewModel implementado
- [ ] Task 3.4: CategoriaDropdown criado
- [ ] Task 3.5: GastoRapidoDialog criado
- [ ] Task 3.6: GastosScreen com FAB criada
- [ ] Task 3.7: Koin atualizado
- [ ] Task 3.8: Testes unitários adicionados
- [ ] Task 3.9: HomeScreen placeholder criada
- [ ] Task 3.10: Documentação FASE_3.md

**Tempo Total Estimado:** 2-3 dias  
**Status:** ⏳ Pronto após Fase 2

---

---

## 📊 FASE 4: Dashboard e Cálculos Financeiros

**Duração:** 3-4 dias  
**Status:** ⏳ Não Iniciada  
**Pré-requisitos:** Fase 3 Completa  
**Objetivos Principais:**
- ✅ Criar `ResumoFinanceiro` data class e cálculos
- ✅ Implementar `CalcularSaldoUseCase` e `ObterResumoFinanceiroUseCase`
- ✅ Desenvolver `HomeScreen` com cards de resumo
- ✅ Adicionar indicadores visuais (ProgressBar animada, cores dinâmicas)
- ✅ Criar lista de gastos recentes com swipe-to-delete

**Entregável Final:** Dashboard completo mostrando resumo financeiro em tempo real

---

### Task 4.1: Criar ResumoFinanceiro Data Class

**Descrição:** Definir modelo que consolida dados financeiros

**Subtarefas:**
- [ ] Criar arquivo `ResumoFinanceiro.kt` em `domain/model/`
- [ ] Adicionar campos: `salarioMensal`, `totalContasFixas`, `totalGastos`, `saldoDisponivel`, `percentualGasto`
- [ ] Adicionar cálculos como propriedades computadas

**Código Esperado:**
```kotlin
data class ResumoFinanceiro(
    val salarioMensal: Double,
    val totalContasFixas: Double,
    val totalGastos: Double,
    val saldoDisponivel: Double = salarioMensal - totalContasFixas - totalGastos,
    val percentualGasto: Double = if (salarioMensal > 0) (totalGastos / salarioMensal) * 100 else 0.0
)
```

**Estimativa:** 15 minutos  
**Dependências:** Fase 2 completa  
**Critério de Aceitação:**
- [ ] Data class criada com cálculos
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase4): create ResumoFinanceiro model`

---

### Task 4.2: Criar CalcularSaldoUseCase

**Descrição:** Implementar lógica de cálculo de saldo disponível

**Subtarefas:**
- [ ] Criar arquivo `CalcularSaldoUseCase.kt` em `domain/usecase/`
- [ ] Receber repositórios de salário, contas fixas e gastos
- [ ] Implementar lógica: Saldo = Salário - Contas Fixas - Gastos
- [ ] Retornar um `Flow<Double>` observável

**Estimativa:** 25 minutos  
**Dependências:** Task 4.1 (ResumoFinanceiro)  
**Critério de Aceitação:**
- [ ] Use case criado
- [ ] Compilando sem erros
- [ ] Retorna `Flow<Double>`

**Commit Sugerido:** `feat(phase4): create CalcularSaldoUseCase`

---

### Task 4.3: Criar ObterResumoFinanceiroUseCase

**Descrição:** Implementar use case que combina todos os flows para gerar resumo

**Subtarefas:**
- [ ] Criar arquivo `ObterResumoFinanceiroUseCase.kt` em `domain/usecase/`
- [ ] Combinar flows de salário, contas fixas e gastos do mês
- [ ] Implementar usando `combine()` ou `zip()`
- [ ] Retornar `Flow<ResumoFinanceiro>`

**Código Esperado:**
```kotlin
class ObterResumoFinanceiroUseCase(
    private val salarioRepository: SalarioRepository,
    private val contaFixaRepository: ContaFixaRepository,
    private val gastoRepository: GastoRepository
) {
    operator fun invoke(): Flow<ResumoFinanceiro> {
        return combine(
            salarioRepository.getSalarioDoMes(getMesAtual()),
            contaFixaRepository.getTotalContasFixas(),
            gastoRepository.getTotalGastosDoMes(getMesAtual())
        ) { salario, totalContas, totalGastos ->
            ResumoFinanceiro(
                salarioMensal = salario?.valor ?: 0.0,
                totalContasFixas = totalContas,
                totalGastos = totalGastos
            )
        }
    }
}
```

**Estimativa:** 30 minutos  
**Dependências:** Task 4.1, 4.2  
**Critério de Aceitação:**
- [ ] Use case criado
- [ ] Usa `combine()` para reatividade
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase4): create ObterResumoFinanceiroUseCase`

---

### Task 4.4: Criar HomeViewModel

**Descrição:** Implementar ViewModel para gerenciar estado do dashboard

**Subtarefas:**
- [ ] Criar arquivo `HomeViewModel.kt` em `ui/screens/home/`
- [ ] Criar `HomeUiState` data class
- [ ] Injetar `ObterResumoFinanceiroUseCase`
- [ ] Criar `StateFlow<HomeUiState>` para estado
- [ ] Implementar lógica de carregamento de resumo
- [ ] Adicionar tratamento de erros

**Código Esperado:**
```kotlin
data class HomeUiState(
    val resumo: ResumoFinanceiro? = null,
    val gastos: List<Gasto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val obterResumoUseCase: ObterResumoFinanceiroUseCase,
    private val obterGastosUseCase: ObterUltimosGastosUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    private fun carregarDados() {
        viewModelScope.launch {
            try {
                obterResumoUseCase().collect { resumo ->
                    _uiState.update { it.copy(resumo = resumo) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
```

**Estimativa:** 40 minutos  
**Dependências:** Task 4.3 (Use case)  
**Critério de Aceitação:**
- [ ] ViewModel criado
- [ ] StateFlow funcionando
- [ ] Carrega resumo automaticamente

**Commit Sugerido:** `feat(phase4): create HomeViewModel`

---

### Task 4.5: Criar CardResumoSalario Composable

**Descrição:** Implementar card que mostra salário do mês

**Subtarefas:**
- [ ] Criar arquivo `CardResumoSalario.kt` em `ui/components/`
- [ ] Usar `Card` do Material3
- [ ] Mostrar valor formatado (moeda)
- [ ] Adicionar ícone de dinheiro
- [ ] Aplicar cores do tema

**Estimativa:** 20 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Card criado e compilando
- [ ] Valor formatado como moeda

**Commit Sugerido:** `feat(phase4): create CardResumoSalario`

---

### Task 4.6: Criar CardContasFixas Composable

**Descrição:** Implementar card que mostra total de contas fixas

**Subtarefas:**
- [ ] Criar arquivo `CardContasFixas.kt` em `ui/components/`
- [ ] Mostrar total e quantidade de contas
- [ ] Adicionar ícone apropriado
- [ ] Implementar navegação para gerenciar contas (será implementado em Fase 5)

**Estimativa:** 20 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Card criado e compilando

**Commit Sugerido:** `feat(phase4): create CardContasFixas`

---

### Task 4.7: Criar CardGastos Composable

**Descrição:** Implementar card que mostra total de gastos do mês

**Subtarefas:**
- [ ] Criar arquivo `CardGastos.kt` em `ui/components/`
- [ ] Mostrar total de gastos do mês
- [ ] Mostrar % em relação ao salário
- [ ] Cor dinâmica (verde se < 50%, amarelo se 50-80%, vermelho se > 80%)

**Estimativa:** 25 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Card criado
- [ ] Cores dinâmicas funcionando

**Commit Sugerido:** `feat(phase4): create CardGastos`

---

### Task 4.8: Criar CardSaldoDisponivel Composable

**Descrição:** Implementar card que mostra saldo disponível

**Subtarefas:**
- [ ] Criar arquivo `CardSaldoDisponivel.kt` em `ui/components/`
- [ ] Mostrar saldo com cor:
  - Verde se saldo > 0
  - Vermelho se saldo < 0
- [ ] Adicionar ícone dinâmico
- [ ] Mostrar valor grande (destaque)

**Estimativa:** 25 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Card criado
- [ ] Cores dinâmicas implementadas

**Commit Sugerido:** `feat(phase4): create CardSaldoDisponivel`

---

### Task 4.9: Criar IndicadorPercentual Composable

**Descrição:** Implementar barra de progresso animada para % de gasto

**Subtarefas:**
- [ ] Criar arquivo `IndicadorPercentual.kt` em `ui/components/`
- [ ] Usar `LinearProgressIndicator`
- [ ] Implementar animação de transição
- [ ] Cor dinâmica baseada em percentual
- [ ] Adicionar label com percentual

**Código Esperado:**
```kotlin
@Composable
fun IndicadorPercentual(
    percentual: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress = animateFloatAsState(
        targetValue = percentual / 100f,
        animationSpec = tween(durationMillis = 800)
    )
    
    val cor = when {
        percentual < 50 -> Color.Green
        percentual < 80 -> Color.Yellow
        else -> Color.Red
    }

    LinearProgressIndicator(
        progress = animatedProgress.value,
        modifier = modifier.fillMaxWidth(),
        color = cor
    )
}
```

**Estimativa:** 30 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Barra criada
- [ ] Animação funcionando
- [ ] Cores dinâmicas

**Commit Sugerido:** `feat(phase4): create IndicadorPercentual`

---

### Task 4.10: Criar ItemGasto Composable

**Descrição:** Implementar item para listar gastos na HomeScreen

**Subtarefas:**
- [ ] Criar arquivo `ItemGasto.kt` em `ui/components/`
- [ ] Mostrar descrição, valor, categoria, data
- [ ] Usar `ListItem` do Material3
- [ ] Adicionar ícone de categoria
- [ ] Cor dinâmica para valor (vermelho = negativo)

**Estimativa:** 25 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] Item criado e compilando

**Commit Sugerido:** `feat(phase4): create ItemGasto`

---

### Task 4.11: Implementar Swipe-to-Delete

**Descrição:** Adicionar funcionalidade de deslize para deletar gasto

**Subtarefas:**
- [ ] Usar `SwipeToDismiss` do Compose Material
- [ ] Implementar callback `onDelete`
- [ ] Adicionar confirmação visual antes de deletar
- [ ] Implementar undo (opcional)

**Estimativa:** 30 minutos  
**Dependências:** Task 4.10 (ItemGasto)  
**Critério de Aceitação:**
- [ ] Swipe funcional
- [ ] Delete implementado

**Commit Sugerido:** `feat(phase4): implement swipe-to-delete`

---

### Task 4.12: Implementar Atualizar Tela com Pull-to-Refresh

**Descrição:** Adicionar funcionalidade de pull-to-refresh

**Subtarefas:**
- [ ] Usar `SwipeRefresh` ou implementar com `RefreshState`
- [ ] Adicionar callback para recarregar dados
- [ ] Mostrar loading indicator

**Estimativa:** 20 minutos  
**Dependências:** Task 4.4 (HomeViewModel)  
**Critério de Aceitação:**
- [ ] Pull-to-refresh funcionando

**Commit Sugerido:** `feat(phase4): implement pull-to-refresh`

---

### Task 4.13: Implementar HomeScreen Completa

**Descrição:** Integrar todos os cards e componentes na HomeScreen

**Subtarefas:**
- [ ] Atualizar `HomeScreen.kt`
- [ ] Adicionar `CardResumoSalario`
- [ ] Adicionar `CardContasFixas`
- [ ] Adicionar `CardGastos` com `IndicadorPercentual`
- [ ] Adicionar `CardSaldoDisponivel`
- [ ] Adicionar lista de `ItemGasto` com swipe-to-delete
- [ ] Adicionar estados de loading e erro
- [ ] Implementar pull-to-refresh

**Estimativa:** 1 hora  
**Dependências:** Tasks 4.5 a 4.12  
**Critério de Aceitação:**
- [ ] HomeScreen compilando
- [ ] Todos os cards mostrados
- [ ] Lista de gastos funcional

**Commit Sugerido:** `feat(phase4): implement complete HomeScreen`

---

### Task 4.14: Integrar Koin com Novos Use Cases

**Descrição:** Registrar novos use cases em Koin

**Subtarefas:**
- [ ] Abrir `KoinModule.kt`
- [ ] Registrar `CalcularSaldoUseCase`
- [ ] Registrar `ObterResumoFinanceiroUseCase`
- [ ] Registrar `HomeViewModel` como factory

**Estimativa:** 10 minutos  
**Dependências:** Tasks 4.2, 4.3, 4.4  
**Critério de Aceitação:**
- [ ] Koin atualizado
- [ ] Compilando sem erros

**Commit Sugerido:** `feat(phase4): register home usecases in Koin`

---

### Task 4.15: Adicionar Testes Unitários

**Descrição:** Implementar testes para cálculos financeiros

**Subtarefas:**
- [ ] Testar `CalcularSaldoUseCase` (saldo correto)
- [ ] Testar `ObterResumoFinanceiroUseCase` (combine funciona)
- [ ] Testar `HomeViewModel` (estado correto)
- [ ] Caso edge: salário zero, saldo negativo

**Estimativa:** 45 minutos  
**Dependências:** Tasks 4.2, 4.3, 4.4  
**Critério de Aceitação:**
- [ ] Testes criados e passando
- [ ] Edge cases cobertos

**Commit Sugerido:** `test(phase4): add unit tests for financial calculations`

---

### Task 4.16: Criar UseCase para Obter Últimos Gastos

**Descrição:** Implementar use case que retorna últimos gastos para a lista

**Subtarefas:**
- [ ] Criar `ObterUltimosGastosUseCase.kt`
- [ ] Limit padrão de 10 gastos
- [ ] Retornar `Flow<List<Gasto>>`

**Estimativa:** 15 minutos  
**Dependências:** Task 3.1 (GastoRepository)  
**Critério de Aceitação:**
- [ ] Use case criado
- [ ] Compilando

**Commit Sugerido:** `feat(phase4): create ObterUltimosGastosUseCase`

---

### Task 4.17: Documentação FASE_4.md

**Descrição:** Documentar dashboard, cálculos e próximos passos

**Subtarefas:**
- [ ] Criar `FASE_4_Dashboard.md` em `docs/phases/`
- [ ] Documentar cálculos de saldo e percentual
- [ ] Documentar componentes de cards
- [ ] Adicionar diagramas de fluxo de dados
- [ ] Screenshots do dashboard

**Estimativa:** 30 minutos  
**Dependências:** Todas as tasks da Fase 4  
**Critério de Aceitação:**
- [ ] Documentação completa

**Commit Sugerido:** `docs(phase4): add phase 4 documentation`

---

## ✅ Checklist de Conclusão - Fase 4

- [ ] Task 4.1: ResumoFinanceiro criado
- [ ] Task 4.2: CalcularSaldoUseCase implementado
- [ ] Task 4.3: ObterResumoFinanceiroUseCase implementado
- [ ] Task 4.4: HomeViewModel criado
- [ ] Task 4.5: CardResumoSalario criado
- [ ] Task 4.6: CardContasFixas criado
- [ ] Task 4.7: CardGastos criado
- [ ] Task 4.8: CardSaldoDisponivel criado
- [ ] Task 4.9: IndicadorPercentual criado
- [ ] Task 4.10: ItemGasto criado
- [ ] Task 4.11: Swipe-to-delete implementado
- [ ] Task 4.12: Pull-to-refresh implementado
- [ ] Task 4.13: HomeScreen completa
- [ ] Task 4.14: Koin atualizado
- [ ] Task 4.15: Testes unitários adicionados
- [ ] Task 4.16: ObterUltimosGastosUseCase criado
- [ ] Task 4.17: Documentação FASE_4.md

**Tempo Total Estimado:** 3-4 dias  
**Status:** ⏳ Pronto após Fase 3

---

---

## 🧭 FASE 5: Navegação e Histórico

**Duração:** 2-3 dias  
**Status:** ⏳ Não Iniciada  
**Pré-requisitos:** Fase 4 Completa  
**Objetivos Principais:**
- ✅ Implementar Navigation Compose com rotas
- ✅ Criar `HistoricoScreen` com filtros
- ✅ Adicionar `BottomNavigationBar`
- ✅ Implementar telas de detalhes/edição
- ✅ Integrar navegação com todas as telas

**Entregável Final:** Navegação fluida entre todas as telas com histórico completo

---

### Task 5.1: Criar Screen Sealed Class

**Descrição:** Definir rotas da aplicação

**Subtarefas:**
- [ ] Criar arquivo `Screen.kt` em `ui/navigation/`
- [ ] Definir sealed class `Screen`
- [ ] Criar rota para `Home`, `Configuracoes`, `Historico`, `DetalhesGasto(id: Long)`
- [ ] Opcionalmente usar `@Serializable` para type-safe navigation

**Código Esperado:**
```kotlin
sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Configuracoes : Screen()

    @Serializable
    data object Historico : Screen()

    @Serializable
    data class DetalhesGasto(val id: Long) : Screen()
}
```

**Estimativa:** 15 minutos  
**Dependências:** Fase 1 completa  
**Critério de Aceitação:**
- [ ] `Screen.kt` criado
- [ ] Rotas definidas
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): create navigation Screen routes`

---

### Task 5.2: Criar Navigation Host

**Descrição:** Implementar NavHost para gerenciar navegação

**Subtarefas:**
- [ ] Criar arquivo `Navigation.kt` em `ui/navigation/`
- [ ] Implementar composable `AppNavHost(navController: NavHostController)`
- [ ] Definir rotas e screens para cada destino
- [ ] Adicionar transições de entrada/saída (opcional)

**Código Esperado:**
```kotlin
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(navController = navController)
        }
        composable<Screen.Configuracoes> {
            ConfiguracoesScreen(navController = navController)
        }
        composable<Screen.Historico> {
            HistoricoScreen(navController = navController)
        }
        composable<Screen.DetalhesGasto> { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            DetalhesGastoScreen(gastoId = id, navController = navController)
        }
    }
}
```

**Estimativa:** 30 minutos  
**Dependências:** Task 5.1 (Screen.kt)  
**Critério de Aceitação:**
- [ ] NavHost criado
- [ ] Rotas configuradas
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): create AppNavHost`

---

### Task 5.3: Integrar NavHost em App.kt

**Descrição:** Colocar NavHost na composable App principal

**Subtarefas:**
- [ ] Abrir `App.kt`
- [ ] Criar `rememberNavController()`
- [ ] Integrar `AppNavHost()` dentro do tema
- [ ] Remover telas placeholder

**Estimativa:** 15 minutos  
**Dependências:** Task 5.2 (Navigation.kt)  
**Critério de Aceitação:**
- [ ] App.kt atualizado
- [ ] NavHost funcional
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): integrate NavHost in App`

---

### Task 5.4: Criar BottomNavigationBar

**Descrição:** Implementar barra de navegação inferior

**Subtarefas:**
- [ ] Criar arquivo `BottomNavigationBar.kt` em `ui/navigation/`
- [ ] Usar `NavigationBar` e `NavigationBarItem` do Material3
- [ ] Adicionar 3 abas: Home, Histórico, Configurações
- [ ] Adicionar ícones apropriados
- [ ] Implementar callback de navegação

**Código Esperado:**
```kotlin
@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen is Screen.Home,
            onClick = { onScreenSelected(Screen.Home) }
        )
        // ... outros items
    }
}
```

**Estimativa:** 25 minutos  
**Dependências:** Task 1.5 (Theme)  
**Critério de Aceitação:**
- [ ] BottomNavigationBar criado
- [ ] 3 abas funcionando
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): create BottomNavigationBar`

---

### Task 5.5: Integrar BottomNavigationBar em App

**Descrição:** Adicionar navegação inferior ao layout principal

**Subtarefas:**
- [ ] Abrir `App.kt` ou criar `MainScreen.kt`
- [ ] Usar `Scaffold` do Material3
- [ ] Adicionar `BottomNavigationBar` no parâmetro `bottomBar`
- [ ] Implementar lógica para detectar tela atual

**Estimativa:** 20 minutos  
**Dependências:** Tasks 5.3, 5.4  
**Critério de Aceitação:**
- [ ] BottomNavigationBar visível
- [ ] Navegação funcional
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): integrate BottomNavigationBar in App`

---

### Task 5.6: Criar HistoricoScreen

**Descrição:** Implementar tela com lista completa de gastos

**Subtarefas:**
- [ ] Criar arquivo `HistoricoScreen.kt` em `ui/screens/historico/`
- [ ] Criar `HistoricoViewModel` para gerenciar estado
- [ ] Implementar LazyColumn com gastos
- [ ] Adicionar agrupamento por data (optional)
- [ ] Adicionar filtro por período (mês atual, anterior, personalizado)
- [ ] Adicionar filtro por categoria
- [ ] Adicionar campo de busca por descrição

**Subtarefas do ViewModel:**
- [ ] `HistoricoUiState` data class
- [ ] StateFlow para estado
- [ ] Métodos: `filtrarPorPeriodo()`, `filtrarPorCategoria()`, `buscarPorDescricao()`
- [ ] Usar `ObterGastosUseCase` ou similar

**Estimativa:** 1 hora  
**Dependências:** Task 3.2 (Use cases de gastos)  
**Critério de Aceitação:**
- [ ] HistoricoScreen criado
- [ ] Filtros funcionando
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): create HistoricoScreen with filters`

---

### Task 5.7: Criar DetalhesGastoScreen

**Descrição:** Implementar tela para visualizar/editar detalhes de um gasto

**Subtarefas:**
- [ ] Criar arquivo `DetalhesGastoScreen.kt` em `ui/screens/detalhes/`
- [ ] Receber `gastoId` como parâmetro
- [ ] Carregar gasto do banco
- [ ] Mostrar campos: descrição, valor, categoria, data
- [ ] Permitir edição
- [ ] Botão para deletar gasto (com confirmação)
- [ ] Botão para voltar

**Estimativa:** 45 minutos  
**Dependências:** Tasks 3.2, 4.10 (Use cases, ItemGasto)  
**Critério de Aceitação:**
- [ ] Tela criada
- [ ] Carrega gasto corretamente
- [ ] Edição e deleção funcionam

**Commit Sugerido:** `feat(phase5): create DetalhesGastoScreen`

---

### Task 5.8: Criar EditarContaFixaScreen

**Descrição:** Implementar tela para editar contas fixas

**Subtarefas:**
- [ ] Criar arquivo `EditarContaFixaScreen.kt` em `ui/screens/configuracoes/`
- [ ] Receber `contaFixaId` como parâmetro
- [ ] Carregar conta do banco
- [ ] Permitir edição de nome, valor, dia vencimento
- [ ] Botão para ativar/desativar
- [ ] Botão para deletar
- [ ] Salvar mudanças

**Estimativa:** 40 minutos  
**Dependências:** Task 2.10 (Use cases)  
**Critério de Aceitação:**
- [ ] Tela criada
- [ ] Edição funcional

**Commit Sugerido:** `feat(phase5): create EditarContaFixaScreen`

---

### Task 5.9: Adicionar Navegação de Voltar Consistente

**Descrição:** Garantir que todas as telas têm botão "voltar" funcional

**Subtarefas:**
- [ ] Adicionar `BackButton` em `DetalhesGastoScreen` e `EditarContaFixaScreen`
- [ ] Usar `navController.popBackStack()`
- [ ] Testar navegação de volta em todas as telas

**Estimativa:** 20 minutos  
**Dependências:** Tasks 5.6, 5.7, 5.8  
**Critério de Aceitação:**
- [ ] Voltar funciona em todas as telas
- [ ] Sem crashes

**Commit Sugerido:** `feat(phase5): add consistent back navigation`

---

### Task 5.10: Implementar Deep Links (Opcional)

**Descrição:** Permitir abrir app com links deep (ex: silvahub://gasto/123)

**Subtarefas:**
- [ ] Configurar deep links em `AndroidManifest.xml`
- [ ] Adicionar `deepLinks` em composable routes
- [ ] Testar com `adb shell am start`

**Estimativa:** 30 minutos  
**Dependências:** Task 5.2 (Navigation)  
**Critério de Aceitação:**
- [ ] Deep links funcionam (opcional)

**Commit Sugerido:** `feat(phase5): implement optional deep links`

---

### Task 5.11: Adicionar Transições de Navegação Animadas

**Descrição:** Implementar animações ao navegar entre telas

**Subtarefas:**
- [ ] Adicionar `enterTransition` e `exitTransition` em composables
- [ ] Usar `slideInHorizontally()` e `slideOutHorizontally()`
- [ ] Testar animações

**Estimativa:** 25 minutos  
**Dependências:** Task 5.2 (Navigation)  
**Critério de Aceitação:**
- [ ] Transições animadas visíveis
- [ ] Suave e fluida

**Commit Sugerido:** `feat(phase5): add animated navigation transitions`

---

### Task 5.12: Testar Fluxos de Navegação

**Descrição:** Testar todos os fluxos de navegação da aplicação

**Subtarefas:**
- [ ] Testar Home → Histórico → Detalhes → Voltar
- [ ] Testar Home → Configurações → Editar Conta → Voltar
- [ ] Testar BottomNavigationBar
- [ ] Testar que estado é preservado ao navegar
- [ ] Testar back press

**Estimativa:** 30 minutos  
**Dependências:** Todas as tasks de navegação  
**Critério de Aceitação:**
- [ ] Todos os fluxos funcionam
- [ ] Sem crashes

**Commit Sugerido:** `test(phase5): verify all navigation flows`

---

### Task 5.13: Integrar Koin com ViewModels de Navegação

**Descrição:** Registrar novos ViewModels em Koin

**Subtarefas:**
- [ ] Registrar `HistoricoViewModel` como factory
- [ ] Registrar `DetalhesGastoViewModel` como factory
- [ ] Registrar `EditarContaFixaViewModel` como factory

**Estimativa:** 15 minutos  
**Dependências:** Tasks 5.6, 5.7, 5.8  
**Critério de Aceitação:**
- [ ] Koin atualizado
- [ ] Compilando

**Commit Sugerido:** `feat(phase5): register navigation viewmodels in Koin`

---

### Task 5.14: Documentação FASE_5.md

**Descrição:** Documentar estrutura de navegação

**Subtarefas:**
- [ ] Criar `FASE_5_Navegacao.md` em `docs/phases/`
- [ ] Documentar rotas e navegação
- [ ] Adicionar diagrama de fluxo de navegação

**Estimativa:** 25 minutos  
**Dependências:** Todas as tasks da Fase 5  
**Critério de Aceitação:**
- [ ] Documentação completa

**Commit Sugerido:** `docs(phase5): add phase 5 documentation`

---

## ✅ Checklist de Conclusão - Fase 5

- [ ] Task 5.1: Screen routes criadas
- [ ] Task 5.2: NavHost implementado
- [ ] Task 5.3: NavHost integrado em App
- [ ] Task 5.4: BottomNavigationBar criado
- [ ] Task 5.5: BottomNavigationBar integrado
- [ ] Task 5.6: HistoricoScreen criada
- [ ] Task 5.7: DetalhesGastoScreen criada
- [ ] Task 5.8: EditarContaFixaScreen criada
- [ ] Task 5.9: Navegação de voltar implementada
- [ ] Task 5.10: Deep links configurados (opcional)
- [ ] Task 5.11: Transições animadas implementadas
- [ ] Task 5.12: Fluxos de navegação testados
- [ ] Task 5.13: Koin atualizado
- [ ] Task 5.14: Documentação FASE_5.md

**Tempo Total Estimado:** 2-3 dias  
**Status:** ⏳ Pronto após Fase 4

---

---

## 🎨 FASE 6: Recursos Avançados e Polimento

**Duração:** 3-4 dias  
**Status:** ⏳ Não Iniciada  
**Pré-requisitos:** Fase 5 Completa  
**Objetivos Principais:**
- ✅ Implementar gráficos (Vico Charts)
- ✅ Adicionar DataStore para preferências
- ✅ Sistema de notificações locais
- ✅ Exportação de dados (CSV)
- ✅ Polimento de UI/UX
- ✅ Testes finais

**Entregável Final:** App completo com features extras e UX refinada, pronto para produção

---

### Task 6.1: Integrar Biblioteca de Gráficos (Vico)

**Descrição:** Adicionar dependência Vico Charts ao projeto

**Subtarefas:**
- [ ] Abrir `libs.versions.toml`
- [ ] Adicionar dependência do Vico Charts
- [ ] Sincronizar Gradle

**Estimativa:** 10 minutos  
**Dependências:** Fase 5 completa  
**Critério de Aceitação:**
- [ ] Vico adicionado ao projeto
- [ ] Compilando

**Commit Sugerido:** `feat(phase6): add Vico Charts dependency`

---

### Task 6.2: Criar Gráfico de Pizza para Categorias

**Descrição:** Implementar gráfico de pizza mostrando distribuição de gastos por categoria

**Subtarefas:**
- [ ] Criar `GraficoCategorias.kt` em `ui/components/`
- [ ] Usar `PieChart` do Vico
- [ ] Dados: gastos por categoria do mês
- [ ] Cores baseadas em temas
- [ ] Adicionar legenda

**Estimativa:** 45 minutos  
**Dependências:** Task 6.1 (Vico)  
**Critério de Aceitação:**
- [ ] Gráfico criado e compilando
- [ ] Dados mostrados corretamente

**Commit Sugerido:** `feat(phase6): create category pie chart`

---

### Task 6.3: Criar Gráfico de Barras para Evolução Mensal

**Descrição:** Implementar gráfico de barras mostrando gastos por mês

**Subtarefas:**
- [ ] Criar `GraficoEvolucao.kt` em `ui/components/`
- [ ] Usar `BarChart` do Vico
- [ ] Dados: últimos 6 meses
- [ ] Animações ao carregar
- [ ] Tooltip com valores

**Estimativa:** 45 minutos  
**Dependências:** Task 6.1 (Vico)  
**Critério de Aceitação:**
- [ ] Gráfico criado
- [ ] Dados mostrados corretamente

**Commit Sugerido:** `feat(phase6): create evolution bar chart`

---

### Task 6.4: Criar GraficosScreen

**Descrição:** Implementar tela com gráficos de análise

**Subtarefas:**
- [ ] Criar `GraficosScreen.kt` em `ui/screens/graficos/`
- [ ] Adicionar `GraficoCategorias`
- [ ] Adicionar `GraficoEvolucao`
- [ ] Criar ViewModel para carregar dados
- [ ] Adicionar filtros (período, categoria)

**Estimativa:** 45 minutos  
**Dependências:** Tasks 6.2, 6.3  
**Critério de Aceitação:**
- [ ] Tela criada
- [ ] Gráficos mostrados

**Commit Sugerido:** `feat(phase6): create GraficosScreen`

---

### Task 6.5: Adicionar Rota de Gráficos na Navegação

**Descrição:** Integrar tela de gráficos na navegação principal

**Subtarefas:**
- [ ] Adicionar rota `Screen.Graficos` em `Screen.kt`
- [ ] Adicionar item `BottomNavigationBar` para gráficos
- [ ] Adicionar composable em `AppNavHost`

**Estimativa:** 15 minutos  
**Dependências:** Tasks 5.5, 6.4  
**Critério de Aceitação:**
- [ ] Navegação para gráficos funcional

**Commit Sugerido:** `feat(phase6): add graphs screen to navigation`

---

### Task 6.6: Implementar DataStore para Preferências

**Descrição:** Adicionar e configurar Proto DataStore para salvar preferências

**Subtarefas:**
- [ ] Adicionar dependências DataStore em `libs.versions.toml`
- [ ] Criar `PreferencesRepository.kt` em `domain/repository/`
- [ ] Implementar `PreferencesRepositoryImpl.kt` em `data/`
- [ ] Preferências: tema (claro/escuro/sistema), moeda, notificações ativadas

**Estimativa:** 45 minutos  
**Dependências:** Fase 2 completa  
**Critério de Aceitação:**
- [ ] DataStore configurado
- [ ] Preferências salvando e carregando

**Commit Sugerido:** `feat(phase6): implement DataStore preferences`

---

### Task 6.7: Criar PreferencesViewModel

**Descrição:** Implementar ViewModel para gerenciar preferências

**Subtarefas:**
- [ ] Criar `PreferencesViewModel.kt` em `ui/screens/preferencias/`
- [ ] StateFlow para cada preferência
- [ ] Métodos para atualizar cada uma
- [ ] Aplicar tema dinamicamente

**Estimativa:** 30 minutos  
**Dependências:** Task 6.6 (PreferencesRepository)  
**Critério de Aceitação:**
- [ ] ViewModel criado
- [ ] Preferências aplicadas dinamicamente

**Commit Sugerido:** `feat(phase6): create PreferencesViewModel`

---

### Task 6.8: Criar Tela de Preferências Avançadas

**Descrição:** Implementar tela para ajustar preferências

**Subtarefas:**
- [ ] Criar `PreferencesScreen.kt` em `ui/screens/preferencias/`
- [ ] Switch para tema claro/escuro/sistema
- [ ] Dropdown para formato de moeda
- [ ] Switch para notificações
- [ ] Salvar automaticamente ao mudar

**Estimativa:** 40 minutos  
**Dependências:** Task 6.7 (ViewModel)  
**Critério de Aceitação:**
- [ ] Tela criada e compilando
- [ ] Preferências funcionam

**Commit Sugerido:** `feat(phase6): create PreferencesScreen`

---

### Task 6.9: Implementar Suporte a Modo Escuro

**Descrição:** Aplicar tema escuro dinamicamente

**Subtarefas:**
- [ ] Atualizar `Theme.kt` para usar preferência do DataStore
- [ ] Testar modo claro e escuro
- [ ] Verificar cores em ambos os modos

**Estimativa:** 25 minutos  
**Dependências:** Tasks 1.5, 6.7  
**Critério de Aceitação:**
- [ ] Tema escuro funcional
- [ ] Cores legíveis em ambos modos

**Commit Sugerido:** `feat(phase6): implement dark mode support`

---

### Task 6.10: Integrar WorkManager para Notificações

**Descrição:** Adicionar e configurar WorkManager para agendamento de notificações

**Subtarefas:**
- [ ] Adicionar dependência WorkManager em `libs.versions.toml`
- [ ] Criar `VencimentoContaWorker.kt` para trabalho agendado
- [ ] Criar `NotificationRepository.kt` para gerenciar notificações
- [ ] Agendar notificação de vencimento de contas (3 dias antes)

**Estimativa:** 1 hora  
**Dependências:** Fase 2 completa  
**Critério de Aceitação:**
- [ ] WorkManager configurado
- [ ] Notificações agendadas

**Commit Sugerido:** `feat(phase6): setup WorkManager for notifications`

---

### Task 6.11: Criar Notificações Locais

**Descrição:** Implementar sistema de notificações com NotificationManager

**Subtarefas:**
- [ ] Criar `NotificationHelper.kt` em `util/`
- [ ] Configurar NotificationChannel
- [ ] Implementar método para mostrar notificação
- [ ] Notificação de vencimento de contas
- [ ] Notificação de orçamento excedido (opcional)

**Estimativa:** 45 minutos  
**Dependências:** Task 6.10 (WorkManager)  
**Critério de Aceitação:**
- [ ] Notificações aparecem
- [ ] Sem crashes

**Commit Sugerido:** `feat(phase6): create notification system`

---

### Task 6.12: Testar Notificações

**Descrição:** Testar sistema de notificações

**Subtarefas:**
- [ ] Criar conta fixa vencendo em 3 dias
- [ ] Aguardar ou simular trigger da notificação
- [ ] Verificar se notificação aparece

**Estimativa:** 20 minutos  
**Dependências:** Tasks 6.10, 6.11  
**Critério de Aceitação:**
- [ ] Notificações funcionando

**Commit Sugerido:** `test(phase6): verify notification system`

---

### Task 6.13: Implementar Exportação de Dados (CSV)

**Descrição:** Criar funcionalidade para exportar dados em CSV

**Subtarefas:**
- [ ] Criar `ExportRepository.kt` em `domain/repository/`
- [ ] Implementar método `exportToCsv(): String`
- [ ] Incluir: salário, contas fixas, gastos
- [ ] Format: CSV padrão (separado por vírgula)
- [ ] Salvar em cache ou Documents

**Estimativa:** 45 minutos  
**Dependências:** Fase 2, 3 completas  
**Critério de Aceitação:**
- [ ] Arquivo CSV gerado
- [ ] Dados corretos

**Commit Sugerido:** `feat(phase6): implement CSV export`

---

### Task 6.14: Implementar Compartilhamento de Dados

**Descrição:** Permitir compartilhar arquivo CSV exportado

**Subtarefas:**
- [ ] Criar função de share via Intent
- [ ] Permitir selecionar app de destino (email, whatsapp, etc)
- [ ] Adicionar botão em PreferencesScreen

**Estimativa:** 25 minutos  
**Dependências:** Task 6.13 (Export)  
**Critério de Aceitação:**
- [ ] Compartilhamento funciona

**Commit Sugerido:** `feat(phase6): add data sharing functionality`

---

### Task 6.15: Criar Onboarding para Primeiro Uso

**Descrição:** Implementar tela de boas-vindas para novo usuário

**Subtarefas:**
- [ ] Criar `OnboardingScreen.kt` em `ui/screens/onboarding/`
- [ ] Mostrar 3-4 telas explicativas
- [ ] Usar `HorizontalPager` ou similar para swipe
- [ ] Botão "Começar" ao final
- [ ] Salvar flag de "já onboarded" em DataStore

**Estimativa:** 1 hora  
**Dependências:** Task 6.6 (DataStore)  
**Critério de Aceitação:**
- [ ] Onboarding criado
- [ ] Primeira execução mostra onboarding

**Commit Sugerido:** `feat(phase6): create onboarding screen`

---

### Task 6.16: Implementar Mensagens de Sucesso/Erro com Snackbar

**Descrição:** Adicionar feedback visual com Snackbar em operações

**Subtarefas:**
- [ ] Criar `SnackbarHelper.kt` em `util/`
- [ ] Estender ViewModels com método `showMessage()`
- [ ] Adicionar mensagens em: salvar salário, adicionar gasto, deletar
- [ ] Cores de sucesso (verde) e erro (vermelho)

**Estimativa:** 30 minutos  
**Dependências:** Fase 4 completa  
**Critério de Aceitação:**
- [ ] Snackbars aparecem corretamente
- [ ] Mensagens legíveis

**Commit Sugerido:** `feat(phase6): implement Snackbar feedback`

---

### Task 6.17: Polir Animações e Transições

**Descrição:** Melhorar animações de transição e estado

**Subtarefas:**
- [ ] Revisar todas as transições de navegação
- [ ] Adicionar animações ao carregar dados
- [ ] Animar card expansion/collapse
- [ ] Animar mudança de cores (saldo positivo/negativo)

**Estimativa:** 45 minutos  
**Dependências:** Fase 5 completa  
**Critério de Aceitação:**
- [ ] Animações suaves
- [ ] Sem lag ou stuttering

**Commit Sugerido:** `feat(phase6): polish animations and transitions`

---

### Task 6.18: Implementar Testes de Usabilidade

**Descrição:** Realizar testes com usuários reais (ou checklist manual)

**Subtarefas:**
- [ ] Criar checklist de funcionalidades
- [ ] Testar em diferentes tamanhos de tela
- [ ] Testar em diferentes versões do Android
- [ ] Verificar performance (memory, battery)
- [ ] Coletar feedback

**Estimativa:** 1 hora  
**Dependências:** Todas as fases completas  
**Critério de Aceitação:**
- [ ] Checklist completo
- [ ] Sem bugs críticos

**Commit Sugerido:** `test(phase6): perform usability testing`

---

### Task 6.19: Corrigir Bugs e Fazer Ajustes Finais

**Descrição:** Corrigir issues encontradas nos testes

**Subtarefas:**
- [ ] Documentar bugs encontrados
- [ ] Priorizar por severidade
- [ ] Corrigir bugs críticos
- [ ] Testar novamente

**Estimativa:** 1 hora  
**Dependências:** Task 6.18 (Testes de usabilidade)  
**Critério de Aceitação:**
- [ ] Bugs críticos corrigidos
- [ ] App estável

**Commit Sugerido:** `fix(phase6): address usability issues`

---

### Task 6.20: Otimizar Performance

**Descrição:** Otimizar performance da aplicação

**Subtarefas:**
- [ ] Usar `Profiler` do Android Studio
- [ ] Verificar memory leaks
- [ ] Otimizar queries do Room (índices)
- [ ] Lazy load de composables pesados
- [ ] Verificar startup time

**Estimativa:** 1 hora  
**Dependências:** Fase 5 completa  
**Critério de Aceitação:**
- [ ] Startup < 2 segundos
- [ ] Memory < 100MB
- [ ] Sem memory leaks

**Commit Sugerido:** `perf(phase6): optimize app performance`

---

### Task 6.21: Integrar Koin com Novos Componentes

**Descrição:** Registrar repositories e ViewModels finais em Koin

**Subtarefas:**
- [ ] Registrar `PreferencesRepositoryImpl`
- [ ] Registrar `ExportRepository`
- [ ] Registrar `PreferencesViewModel`
- [ ] Registrar `GraficosViewModel`

**Estimativa:** 15 minutos  
**Dependências:** Tasks 6.4, 6.6, 6.7  
**Critério de Aceitação:**
- [ ] Koin atualizado
- [ ] Compilando

**Commit Sugerido:** `feat(phase6): register final dependencies in Koin`

---

### Task 6.22: Preparar Screenshots e Documentação Final

**Descrição:** Criar documentação final e capturas de tela

**Subtarefas:**
- [ ] Tirar screenshots de cada tela principal
- [ ] Criar `FASE_6_Recursos_Avancados.md` em `docs/phases/`
- [ ] Documentar features extras
- [ ] Criar diagrama final de arquitetura
- [ ] Atualizar README.md do projeto

**Estimativa:** 1 hora  
**Dependências:** Todas as fases completas  
**Critério de Aceitação:**
- [ ] Screenshots em `docs/screenshots/`
- [ ] Documentação completa

**Commit Sugerido:** `docs(phase6): add final documentation and screenshots`

---

### Task 6.23: Preparar Build de Release

**Descrição:** Configurar build de release para Google Play (opcional)

**Subtarefas:**
- [ ] Gerar assinatura (keystore)
- [ ] Configurar versão de release em `build.gradle.kts`
- [ ] Executar `./gradlew bundleRelease` ou `./gradlew assembleRelease`
- [ ] Testar APK/Bundle de release
- [ ] Documentar processo

**Estimativa:** 45 minutos  
**Dependências:** Todas as fases completas  
**Critério de Aceitação:**
- [ ] APK/Bundle gerado com sucesso
- [ ] Instalável e funcional

**Commit Sugerido:** `chore(phase6): prepare release build`

---

### Task 6.24: Criar CHANGELOG Final

**Descrição:** Documentar todas as mudanças do projeto

**Subtarefas:**
- [ ] Criar `CHANGELOG.md` na raiz
- [ ] Listar features por fase
- [ ] Listar versão e data
- [ ] Incluir créditos e dependências

**Estimativa:** 30 minutos  
**Dependências:** Todas as fases  
**Critério de Aceitação:**
- [ ] CHANGELOG.md completo

**Commit Sugerido:** `docs(phase6): create CHANGELOG`

---

## ✅ Checklist de Conclusão - Fase 6

- [ ] Task 6.1: Vico Charts integrado
- [ ] Task 6.2: Gráfico de pizza criado
- [ ] Task 6.3: Gráfico de barras criado
- [ ] Task 6.4: GraficosScreen criada
- [ ] Task 6.5: Navegação para gráficos
- [ ] Task 6.6: DataStore implementado
- [ ] Task 6.7: PreferencesViewModel criado
- [ ] Task 6.8: PreferencesScreen criada
- [ ] Task 6.9: Modo escuro suportado
- [ ] Task 6.10: WorkManager integrado
- [ ] Task 6.11: Notificações implementadas
- [ ] Task 6.12: Notificações testadas
- [ ] Task 6.13: Exportação CSV implementada
- [ ] Task 6.14: Compartilhamento funcional
- [ ] Task 6.15: Onboarding criado
- [ ] Task 6.16: Snackbar feedback implementado
- [ ] Task 6.17: Animações polidas
- [ ] Task 6.18: Testes de usabilidade completos
- [ ] Task 6.19: Bugs corrigidos
- [ ] Task 6.20: Performance otimizada
- [ ] Task 6.21: Koin atualizado
- [ ] Task 6.22: Screenshots e documentação final
- [ ] Task 6.23: Build de release preparado
- [ ] Task 6.24: CHANGELOG criado

**Tempo Total Estimado:** 3-4 dias  
**Status:** ⏳ Pronto após Fase 5

---

---

## 📈 Resumo de Estimativas e Status

### Duração por Fase

| Fase | Duração | Tasks | Status |
|------|---------|-------|--------|
| 1 | 2-3 dias | 12 | ⏳ |
| 2 | 3-4 dias | 18 | ⏳ |
| 3 | 2-3 dias | 10 | ⏳ |
| 4 | 3-4 dias | 17 | ⏳ |
| 5 | 2-3 dias | 14 | ⏳ |
| 6 | 3-4 dias | 24 | ⏳ |
| **TOTAL** | **15-21 dias** | **95 tasks** | **⏳** |

### Recomendações de Cronograma

- **Semana 1:** Fases 1 + 2 (2-3 dias de setup + 3-4 dias de DB)
- **Semana 2:** Fase 3 + Fase 4 (2-3 dias de gastos + 3-4 dias de dashboard)
- **Semana 3:** Fase 5 + Fase 6 (2-3 dias de navegação + 3-4 dias polimento)

### Buffer de Tempo

- **+20% contingência** = 18-25 dias recomendados

### Commits Sugeridos por Fase

- **Fase 1:** ~12 commits de setup
- **Fase 2:** ~18 commits de dados e repositórios
- **Fase 3:** ~10 commits de gastos
- **Fase 4:** ~17 commits de dashboard
- **Fase 5:** ~14 commits de navegação
- **Fase 6:** ~24 commits de features e polimento

**Total:** ~95 commits bem organizados

---

## 🎯 Próximos Passos

1. ✅ Revisar todas as tarefas acima
2. ✅ Priorizar ou agrupar conforme necessário
3. ✅ Começar pela **Fase 1** - Setup e Fundação
4. ✅ Manter este arquivo (`TASKS.md`) atualizado conforme progride
5. ✅ Marcar tasks completas com `[x]`
6. ✅ Criar branch Git para cada fase: `feature/phase-1`, `feature/phase-2`, etc.

---

## 📚 Referências

- [Plan de Desenvolvimento Original](./plan-appFinancasDesenvolvedor.prompt.md)
- [Clean Architecture Android](https://developer.android.com/topic/architecture)
- [Jetpack Compose Documentation](https://developer.android.com/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Koin Documentation](https://insert-koin.io/)

---

**Última Atualização:** 2026-02-19  
**Versão:** 1.0  
**Status Geral:** 📋 Estrutura Completa - Pronto para Iniciar Desenvolvimento
