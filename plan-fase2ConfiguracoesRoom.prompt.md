## Plan: Fase 2 Configurações com Room

Rascunho de execução para fechar a Fase 2 com fluxo ponta a ponta (persistência, domínio e UI). A sequência prioriza primeiro o contrato de dados (`Entity`/`DAO`), depois regras de negócio (repositórios/use cases), e por fim estado e tela de configurações, reduzindo retrabalho de arquitetura e deixando validações centralizadas no `ConfiguracoesViewModel`.

### Steps
1. Evoluir `SalarioEntity` e `ContaFixaEntity` em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/entity/SalarioEntity.kt` e `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/entity/ContaFixaEntity.kt`, alinhando colunas e `tableName`.
2. Implementar queries reativas nos DAOs `SalarioDao` e `ContaFixaDao` em `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/SalarioDao.kt` e `composeApp/src/androidMain/kotlin/com/example/silvahub/data/local/dao/ContaFixaDao.kt`, com `Flow` e CRUD.
3. Criar contratos e implementações de domínio/dados em `composeApp/src/androidMain/kotlin/com/example/silvahub/domain/repository/` e `composeApp/src/androidMain/kotlin/com/example/silvahub/data/repository/`, incluindo mapeamentos Entity-Domain.
4. Criar use cases em `composeApp/src/androidMain/kotlin/com/example/silvahub/domain/usecase/` e registrar tudo no DI em `composeApp/src/androidMain/kotlin/com/example/silvahub/di/KoinModule.kt` (`appModule`).
5. Implementar `ConfiguracoesViewModel` e `ConfiguracoesScreen` em `composeApp/src/androidMain/kotlin/com/example/silvahub/ui/screens/configuracoes/`, integrar no `AppNavHost` em `composeApp/src/androidMain/kotlin/com/example/silvahub/ui/navigation/AppNavHost.kt`.

### Further Considerations
1. Validação monetária: usar `Double` simples agora ou `BigDecimal` para precisão financeira (A/B)?
2. Estratégia de schema Room: manter `version = 1` com `fallbackToDestructiveMigration()` ou subir versão para 2 já nesta entrega (A/B)?
3. Navegação inicial: abrir direto `ConfiguracoesScreen` temporariamente ou já estruturar rota com Navigation Compose (A/B/C)?
