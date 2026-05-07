## Plan: Fase 2.3-2.6 Room e Clean Architecture

Este plano organiza a implementação das Tasks 2.3 a 2.6 com foco em fidelidade ao `TASKS.md`, consistência arquitetural com a base atual e redução de risco técnico. A execução deve cobrir: modelagem de `gastos`, DAOs reativos com `Flow`, uso correto de `suspend`, critérios de performance (índices e agregações), e alinhamento com camadas já existentes (`repository`, `di`) para evitar regressões por mudança de assinatura.

### Steps
1. Mapear gaps entre tarefas e código atual em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/entity/GastoEntity.kt`, `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/SalarioDao.kt`, `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/ContaFixaDao.kt` e `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/GastoDao.kt`.
2. Planejar Task 2.3 em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/entity/GastoEntity.kt`, incluindo `TipoGasto`, `CategoriaGasto`, campos obrigatórios e comentários técnicos sobre `@PrimaryKey(autoGenerate = true)`, defaults e serialização de enum.
3. Planejar Task 2.4 em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/SalarioDao.kt`, detalhando `suspend` para escrita, `Flow` para leitura reativa, estratégia de conflito e impacto de manter/remover `deletarPorId` nas camadas `SalarioRepositoryImpl` e `SalarioRepository`.
4. Planejar Task 2.5 em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/ContaFixaDao.kt`, preservando as queries esperadas e documentando o porquê de `COALESCE(SUM(...), 0.0)`, além do ajuste de nomenclatura entre `getTotalContasFixas()` e `getTotalContasFixasAtivas()`.
5. Planejar Task 2.6 em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/GastoDao.kt`, com filtros por período/categoria, agregação por intervalo, limite parametrizado e orientação para converter `mesAno` em `dataInicio/dataFim` de forma determinística.
6. Consolidar impactos transversais: versão/schema em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/database/AppDatabase.kt`, provisionamento via `composeApp/src/androidMain/kotlin/com/example/silvahub/di/KoinModule.kt`, e roteiro de testes de DAO em `composeApp/src/androidUnitTest/...` com foco em queries, ordenação e edge cases.

### Further Considerations 
1. Compatibilidade de contratos: alinhar assinaturas do `TASKS.md` com `SalarioRepositoryImpl`/`ContaFixaRepositoryImpl` (Opção A: refactor total; Opção B: manter métodos legados temporariamente).
2. Estratégia temporal: padronizar mês/período (`mesReferencia` string vs epoch em `data`) para evitar bugs de fuso e filtros inconsistentes.
3. Performance Room: avaliar índices em colunas de filtro/ordenação (`mes_referencia`, `ativa`, `dia_vencimento`, `data`, `categoria`) antes de crescer volume de dados.

