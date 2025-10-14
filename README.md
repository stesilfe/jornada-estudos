# EcoHabits Tracker (Java 17 + Maven + JavaFX)
Rastreador de hábitos sustentáveis (energia, transporte, alimentação). Permite **cadastrar hábitos**, **calcular impacto** (CO₂ evitado, kWh economizados, etc.), **gerar relatórios por período**, **ordenar/filtrar com Streams**, e **salvar/carregar CSV** usando **NIO**. Inclui **UI JavaFX** com **BarChart** e **PieChart**.

## Requisitos
- JDK 17+
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

## Diferença prática I/O vs NIO
- **Clássico**: `java.io` (ex.: `File`); compatibilidade antiga.
- **NIO**: `java.nio.file.Files/Paths`; utilitários modernos, melhor suporte a caminhos e streaming.

## Como abrir no IntelliJ IDEA
1. **Extraia o zip** em uma pasta simples (sem espaços), por exemplo: `~/projetos/ecohabitos-tracker`.
2. Abra o IntelliJ IDEA → **File > Open...** → selecione a pasta do projeto (ou o `pom.xml`) → **Open as Project**.
3. Quando solicitado, clique em **Trust Project**.
4. Verifique o SDK: **File > Project Structure... > Project SDK** → selecione **Java 17**.
5. Maven deve importar automaticamente. Se não, abra a janela **Maven** e clique no ícone de **Reload**.
6. Para rodar a **linha de comando** pela IDE:
   - Abra o painel **Maven** → **Plugins** → **exec** → **exec:java** (ou botão play ao lado).
7. Para rodar a **UI JavaFX**:
   - No painel **Maven**, ative o perfil **ui**.
   - Em **Plugins > javafx**, dê duplo clique em **javafx:run**.

## Habilitar asserções (opcional)
O `Main` contém alguns `assert`. Para habilitar via IntelliJ, em **Run/Debug Configurations**, adicione em **VM options**: `-ea`.
