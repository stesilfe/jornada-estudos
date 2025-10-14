# EcoHabits Tracker (Java 21 + Maven + JavaFX)
Rastreador de hábitos sustentáveis (energia, transporte, alimentação). Permite **cadastrar hábitos**, **calcular impacto** (CO₂ evitado, kWh economizados, etc.), **gerar relatórios por período**, **ordenar/filtrar com Streams**, e **salvar/carregar CSV** usando **NIO**. Inclui **UI JavaFX** com **BarChart** e **PieChart**.

## Requisitos
- JDK 21+
- Maven 3.8+
- (Opcional) IntelliJ IDEA

## Como rodar no terminal
```bash
mvn clean compile
mvn exec:java
# Interface JavaFX (perfil ui)
mvn -Pui javafx:run
```

## Mapa de tópicos → onde foi aplicado
- **POO (classes/objetos/métodos)**: `src/main/java/com/exemplo/ecohabitos/dominio/*`, `servico/*`, `io/*`
- **Abstração**: `dominio/Habito.java` (classe abstrata)
- **Herança & Polimorfismo**: `EnergiaHabito`, `TransporteHabito`, `AlimentacaoHabito` substituem `calcularImpacto()`
- **Encapsulamento**: campos `private` + getters/setters controlados em `UsuarioPerfil` e `Habito`
- **Coleções & Generics**: `repositorio/Repositorio.java` e `RepositorioEmMemoria.java` (Map/Set/List + generics)
- **Exceções**: `excecoes/HabitoInvalidoException.java`; `try-catch-finally` no `Main`; `throws`/`throw` no domínio/IO
- **Streams & Lambdas**: `servico/ServicoHabito.java` e `servico/ServicoRelatorio.java` (filter, map, sum, groupingBy, sorted)
- **Java I/O & NIO**: `io/ArmazenamentoCsv.java` usando `Files`, `Paths`, `BufferedReader/Writer`
- **Date & Time API**: `ServicoRelatorio.java` (LocalDate, Period, Duration, DateTimeFormatter)
- **UI JavaFX**: `ui/DashboardApp.java` + `resources/estilos.css` (gráficos e tabela)

## Exemplos rápidos
- Cadastro de 3 hábitos (energia/transporte/alimentação)
- Relatório semanal com `LocalDate.now()` e `minusDays(...)`
- Gravação e leitura do CSV (`dados-exemplo-habitos.csv`)
- Tratamento de entrada inválida levantando `HabitoInvalidoException`
- Dashboard com filtros por período e gráficos

