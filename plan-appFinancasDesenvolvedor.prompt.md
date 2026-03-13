************# Plan: App de Finanças para Desenvolvedores - Arquitetura e Roadmap Completo

Desenvolvimento de aplicativo Android de gestão financeira usando Kotlin, Jetpack Compose, arquitetura Clean Architecture (MVVM), Room Database, e Navigation Compose. O projeto será dividido em 6 fases incrementais, cada uma entregando funcionalidade testável.

## Arquitetura do Projeto

### Estrutura de Pacotes

Em `com.example.silvahub`:

- `data/` - Camada de dados
  - `local/entity/` - Entidades Room (`SalarioEntity`, `ContaFixaEntity`, `GastoEntity`)
  - `local/dao/` - DAOs para acesso ao banco (`SalarioDao`, `ContaFixaDao`, `GastoDao`)
  - `local/database/` - Configuração do Room (`AppDatabase`)
  - `repository/` - Implementação de repositórios
- `domain/` - Regras de negócio
  - `model/` - Modelos de domínio (`Salario`, `ContaFixa`, `Gasto`, `ResumoFinanceiro`)
  - `repository/` - Interfaces de repositórios
  - `usecase/` - Casos de uso (`CalcularSaldoUseCase`, `ObterResumoFinanceiroUseCase`)
- `ui/` - Interface do usuário
  - `theme/` - Material3 Theme customizado
  - `navigation/` - NavHost e rotas (`Screen` sealed class)
  - `screens/home/` - HomeScreen e HomeViewModel
  - `screens/configuracoes/` - ConfiguracoesScreen e ConfiguracoesViewModel
  - `screens/gastos/` - GastosScreen e GastosViewModel
  - `components/` - Componentes reutilizáveis (`MoneyTextField`, `CardResumo`, `ItemGasto`)
- `di/` - Injeção de dependências manual ou Koin
- `util/` - Extensões e utilitários (`formatarMoeda`, `validarValor`)

### Padrão Arquitetural

Clean Architecture com MVVM:
```
UI → ViewModel → UseCase → Repository → DataSource
```

## Fases de Desenvolvimento

### FASE 1: Setup e Fundação (2-3 dias)

**Objetivos:**
- Adicionar dependências Room, Navigation Compose, e Koin/Dagger ao build.gradle.kts
- Criar estrutura completa de pacotes em `androidMain/kotlin/com/example/silvahub/`
- Configurar `AppDatabase` com Room e migrations strategy
- Implementar Theme customizado com cores para finanças (verde/vermelho para saldo positivo/negativo)

**Entregável:** Projeto compilando com estrutura base e tema funcional

**Tarefas Detalhadas:**
1. Atualizar `libs.versions.toml` com dependências:
   - Room (runtime, compiler, ktx)
   - Navigation Compose
   - Koin (injeção de dependências)
   - Kotlinx Serialization (para navegação type-safe)
2. Criar todos os pacotes da arquitetura
3. Configurar `AppDatabase.kt` com versão 1
4. Criar `Color.kt`, `Theme.kt`, `Type.kt` personalizados
5. Documentar decisões arquiteturais

---

### FASE 2: Gestão de Salário e Contas Fixas (3-4 dias)

**Objetivos:**
- Criar entidades `SalarioEntity` (id, valor, mesReferencia, dataCriacao) e `ContaFixaEntity` (id, nome, valor, diaVencimento, ativa)
- Implementar DAOs com queries (`getSalarioDoMes`, `getContasFixasAtivas`)
- Desenvolver repositórios e use cases para salário e contas fixas
- Criar `ConfiguracoesScreen` com formulários para input de salário mensal e cadastro de contas fixas (lista editável)
- Implementar `ConfiguracoesViewModel` com validação de inputs

**Entregável:** Tela de configurações salvando dados no Room com validação

**Tarefas Detalhadas:**
1. Criar `SalarioEntity.kt` com anotações Room
2. Criar `ContaFixaEntity.kt` com anotações Room
3. Implementar `SalarioDao.kt` com queries:
   - `@Insert`, `@Update`, `@Delete`
   - `getSalarioDoMes(mesAno: String): Flow<SalarioEntity?>`
   - `getTodosSalarios(): Flow<List<SalarioEntity>>`
4. Implementar `ContaFixaDao.kt` com queries:
   - CRUD completo
   - `getContasFixasAtivas(): Flow<List<ContaFixaEntity>>`
   - `getTotalContasFixas(): Flow<Double>`
5. Criar interfaces de repositório em `domain/repository/`
6. Implementar repositórios em `data/repository/`
7. Criar use cases:
   - `SalvarSalarioUseCase`
   - `ObterSalarioDoMesUseCase`
   - `GerenciarContasFixasUseCase`
8. Desenvolver `ConfiguracoesViewModel` com StateFlow
9. Criar `ConfiguracoesScreen` com:
   - TextField para salário mensal
   - LazyColumn com lista de contas fixas
   - Dialog para adicionar/editar conta fixa
   - Validação de valores monetários
10. Adicionar testes unitários para ViewModel e UseCases

---

### FASE 3: Gastos Rápidos (2-3 dias)

**Objetivos:**
- Criar `GastoEntity` (id, descricao, valor, categoria, data, tipo = RAPIDO/FIXO)
- Implementar `GastoDao` com queries por período e categoria
- Desenvolver FloatingActionButton na HomeScreen abrindo dialog/bottom sheet para input rápido
- Criar formulário simplificado com campos: valor, descrição opcional, categoria (dropdown)
- Implementar `GastosViewModel` para gerenciar lançamentos

**Entregável:** Funcionalidade de adicionar gastos rápidos operacional

**Tarefas Detalhadas:**
1. Criar `GastoEntity.kt` com enum `TipoGasto` e `CategoriaGasto`
2. Implementar `GastoDao.kt` com queries:
   - CRUD completo
   - `getGastosPorPeriodo(dataInicio: Long, dataFim: Long): Flow<List<GastoEntity>>`
   - `getGastosPorCategoria(categoria: String): Flow<List<GastoEntity>>`
   - `getTotalGastosDoMes(mesAno: String): Flow<Double>`
   - `getUltimosGastos(limit: Int): Flow<List<GastoEntity>>`
3. Criar repositório e use cases para gastos
4. Desenvolver `GastosViewModel`
5. Criar componente `MoneyTextField` reutilizável
6. Criar componente `CategoriaDropdown` com ícones
7. Implementar `GastoRapidoDialog` ou `GastoRapidoBottomSheet`
8. Adicionar FAB na HomeScreen
9. Implementar lógica de salvamento com feedback visual
10. Adicionar validações e tratamento de erros

---

### FASE 4: Dashboard e Cálculos Financeiros (3-4 dias)

**Objetivos:**
- Criar `ResumoFinanceiro` data class (salario, totalContasFixas, totalGastos, saldoDisponivel, percentualGasto)
- Implementar `CalcularSaldoUseCase` e `ObterResumoFinanceiroUseCase` com lógica de negócio
- Desenvolver `HomeScreen` com cards mostrando: salário do mês, contas fixas totais, gastos do mês, saldo disponível
- Adicionar indicadores visuais (ProgressBar para % do salário gasto, cores dinâmicas)
- Criar lista de gastos recentes (últimos 10) com swipe-to-delete

**Entregável:** Dashboard completo mostrando resumo financeiro atualizado em tempo real

**Tarefas Detalhadas:**
1. Criar `ResumoFinanceiro.kt` data class com campos calculados
2. Implementar `CalcularSaldoUseCase`:
   - Saldo = Salário - Total Contas Fixas - Total Gastos
   - Percentual gasto = (Total Gastos / Salário) * 100
3. Implementar `ObterResumoFinanceiroUseCase` combinando flows
4. Criar `HomeViewModel` consumindo resumo financeiro
5. Desenvolver componentes UI:
   - `CardResumoSalario` - mostra salário do mês
   - `CardContasFixas` - total de contas fixas
   - `CardGastos` - total de gastos do mês
   - `CardSaldoDisponivel` - saldo com cor verde/vermelho
   - `IndicadorPercentual` - barra de progresso animada
6. Criar `ItemGasto` composable para lista
7. Implementar swipe-to-delete com confirmação
8. Adicionar estados de loading e erro
9. Implementar refresh manual (pull-to-refresh)
10. Adicionar animações de transição entre estados

---

### FASE 5: Navegação e Histórico (2-3 dias)

**Objetivos:**
- Implementar Navigation Compose com rotas: Home, Configuracoes, Historico, DetalhesGasto
- Criar `HistoricoScreen` com lista completa de gastos filtráveis por período e categoria
- Adicionar navegação bottom bar ou navigation drawer
- Implementar tela de detalhes/edição de gastos e contas fixas
- Adicionar filtros e busca no histórico

**Entregável:** Navegação fluida entre todas as telas com histórico completo

**Tarefas Detalhadas:**
1. Criar `Screen` sealed class com rotas:
   - `Home`, `Configuracoes`, `Historico`, `DetalhesGasto(id)`
2. Implementar `NavHost` em `App.kt` ou `MainScreen.kt`
3. Criar `BottomNavigationBar` com ícones Material
4. Desenvolver `HistoricoScreen`:
   - LazyColumn com todos os gastos
   - Agrupamento por data
   - Filtro por período (mês atual, mês anterior, personalizado)
   - Filtro por categoria
   - Busca por descrição
5. Criar `DetalhesGastoScreen` para edição
6. Implementar `EditarContaFixaScreen`
7. Adicionar navegação de voltar consistente
8. Implementar deep links (opcional)
9. Adicionar transições de navegação animadas
10. Testar fluxos de navegação completos

---

### FASE 6: Recursos Avançados e Polimento (3-4 dias)

**Objetivos:**
- Implementar gráfico de gastos por categoria (biblioteca Vico Charts)
- Adicionar DataStore para preferências (tema, moeda, notificações)
- Criar sistema de notificações locais (alertas de vencimento de contas)
- Implementar exportação de dados (CSV ou PDF)
- Adicionar validações avançadas e mensagens de erro amigáveis
- Polir UI/UX, adicionar animações e transições

**Entregável:** App completo com features extras e UX refinada

**Tarefas Detalhadas:**
1. Integrar Vico Charts:
   - Gráfico de pizza para categorias
   - Gráfico de barras para evolução mensal
2. Implementar DataStore Preferences:
   - Tema (claro/escuro/sistema)
   - Formato de moeda
   - Notificações ativadas
3. Criar `PreferencesRepository` e ViewModel
4. Adicionar tela de Configurações avançadas
5. Implementar notificações locais:
   - WorkManager para agendamento
   - Notificação de vencimento de contas (3 dias antes)
   - Notificação de orçamento excedido
6. Criar funcionalidade de exportação:
   - Gerar CSV com todos os dados
   - Compartilhar arquivo via Intent
7. Adicionar onboarding para primeiro uso
8. Implementar mensagens de sucesso/erro com Snackbar
9. Polir animações e transições
10. Realizar testes de usabilidade e ajustes finais
11. Preparar documentação final e screenshots

---

## Documentação Sugerida

### Para cada fase, documentar:

1. **README_FASE_X.md** - Objetivos, funcionalidades implementadas, decisões técnicas
2. **Diagramas** - Diagrama de classes (entities/models), fluxo de navegação, arquitetura de camadas
3. **Testes** - Unit tests para ViewModels e UseCases, testes de integração para DAOs
4. **Screenshots** - Capturas de tela das funcionalidades implementadas
5. **Changelog** - Commits organizados por feature com mensagens descritivas

### Estrutura de Documentação Completa:

```
docs/
├── architecture/
│   ├── diagrama-arquitetura.png
│   ├── diagrama-classes.png
│   └── fluxo-dados.md
├── phases/
│   ├── FASE_1_Setup.md
│   ├── FASE_2_Salario_Contas.md
│   ├── FASE_3_Gastos_Rapidos.md
│   ├── FASE_4_Dashboard.md
│   ├── FASE_5_Navegacao.md
│   └── FASE_6_Recursos_Avancados.md
├── screenshots/
│   ├── home-screen.png
│   ├── configuracoes-screen.png
│   ├── historico-screen.png
│   └── graficos-screen.png
└── api/
    ├── database-schema.md
    └── use-cases.md
```

---

## Further Considerations

### 1. Biblioteca de injeção de dependências
- **Koin** (mais simples, recomendado para projeto pequeno) - Setup rápido, menos boilerplate
- **Hilt/Dagger** (mais robusto) - Melhor para projetos grandes, compile-time safety

**Recomendação:** Koin para este projeto

### 2. Estratégia de testes
- Implementar testes desde Fase 2 ou criar fase dedicada ao final?
- **Recomendação:** TDD incremental - escrever testes unitários para cada ViewModel e UseCase durante desenvolvimento

### 3. Versionamento de banco de dados
- Planejar migrations desde o início ou usar fallbackToDestructiveMigration em desenvolvimento?
- **Recomendação:** 
  - Desenvolvimento: `fallbackToDestructiveMigration()`
  - Produção: Migrations planejadas desde v1.0

### 4. Categorias de gastos
- Lista fixa inicial: Alimentação, Transporte, Lazer, Saúde, Educação, Outros (customizável na Fase 6)?
- **Recomendação:** Começar com categorias fixas, adicionar customização na Fase 6

### 5. Multi-moeda
- Suporte apenas BRL na v1.0 ou já preparar internacionalização?
- **Recomendação:** BRL apenas na v1.0, preparar arquitetura para i18n futura

### 6. Backup e sincronização
- Implementar backup local (export/import) ou adicionar sincronização cloud?
- **Recomendação:** Começar com export/import CSV (Fase 6), planejar cloud sync para v2.0

### 7. Autenticação
- Adicionar login/senha local ou manter sem autenticação na primeira versão?
- **Recomendação:** Sem autenticação na v1.0, adicionar PIN/Biometria na v1.1 se necessário

---

## Tecnologias e Bibliotecas

### Essenciais:
- **Kotlin** 2.3.0
- **Jetpack Compose** 1.10.0
- **Material3** 1.10.0-alpha05
- **Room** 2.6.x (a definir)
- **Navigation Compose** (a definir)
- **Lifecycle ViewModel Compose** 2.9.6
- **Koin** 3.x (a definir)

### Opcionais/Fase 6:
- **Vico Charts** - Gráficos nativos Compose
- **DataStore** - Preferências
- **WorkManager** - Notificações agendadas
- **Kotlinx Serialization** - Navegação type-safe
- **Coil** - Carregamento de imagens (se necessário)

---

## Estimativa Total

**Tempo estimado:** 15-21 dias de desenvolvimento
**Fases críticas:** Fase 2 e Fase 4 (maior complexidade)
**Buffer:** +20% para imprevistos e refatorações

## Próximos Passos

1. Revisar e aprovar o plano
2. Configurar repositório Git com estrutura de branches (main, develop, feature/*)
3. Iniciar Fase 1: Setup e Fundação
4. Estabelecer rotina de commits e documentação contínua

