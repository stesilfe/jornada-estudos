package com.exemplo.ecohabitos.ui;

import com.exemplo.ecohabitos.dominio.*;
import com.exemplo.ecohabitos.io.ArmazenamentoCsv;
import com.exemplo.ecohabitos.repositorio.RepositorioEmMemoria;
import com.exemplo.ecohabitos.servico.ServicoHabito;
import com.exemplo.ecohabitos.servico.ServicoRelatorio;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardApp extends Application {

    private final RepositorioEmMemoria<Habito, String> repositorio =
            new RepositorioEmMemoria<>(Habito::getId); // [GENERICS][METHOD REF]

    private final ImpactoCalculadora calculadora = new ImpactoCalculadoraPadrao(); // [STRATEGY]
    private final ServicoHabito servicoHabito = new ServicoHabito(repositorio, calculadora);
    private final ServicoRelatorio servicoRelatorio = new ServicoRelatorio();
    private final ArmazenamentoCsv io = new ArmazenamentoCsv();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private TableView<Habito> tabela;
    private BarChart<String, Number> graficoBarra;
    private PieChart graficoPizza;
    private DatePicker dpInicio;
    private DatePicker dpFim;
    private Label lblResumo;
    private Path caminhoCsv = Path.of("src/main/resources/dados-exemplo-habitos.csv");

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("EcoHabits Tracker — Dashboard");

        carregarOuPopularExemplos();

        var titulo = new Text("🌿 EcoHabits — Seu impacto sustentável");
        titulo.getStyleClass().add("titulo");

        lblResumo = new Label();
        lblResumo.getStyleClass().add("resumo");

        var headerBox = new VBox(6, titulo, lblResumo);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        dpInicio = new DatePicker(LocalDate.now().minusDays(6));
        dpFim = new DatePicker(LocalDate.now());

        var btnAplicar = new Button("Aplicar Filtro");
        btnAplicar.setOnAction(e -> atualizarTudo());

        var btnSalvar = new Button("Exportar CSV");
        btnSalvar.setOnAction(e -> {
            try {
                io.salvar(caminhoCsv, servicoHabito.listarOrdenadoPorDataDecrescente());
                alerta("Sucesso", "Dados exportados para:\n" + caminhoCsv.toAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                alerta("Erro ao salvar", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        var filtros = new HBox(10,
                new Label("Início:"), dpInicio,
                new Label("Fim:"), dpFim,
                btnAplicar, btnSalvar
        );
        filtros.setAlignment(Pos.CENTER_LEFT);

        tabela = montarTabela();

        graficoBarra = montarGraficoBarra();
        graficoPizza = montarGraficoPizza();

        var chartsBox = new HBox(16, graficoBarra, graficoPizza);
        chartsBox.setAlignment(Pos.CENTER);
        chartsBox.setPrefHeight(360);

        var root = new VBox(14, headerBox, filtros, tabela, chartsBox);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("container");

        var cena = new Scene(root, 1100, 750);
        cena.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/estilos.css")).toExternalForm());

        stage.setScene(cena);
        stage.show();

        atualizarTudo();
    }

    private void carregarOuPopularExemplos() {
        try {
            var lidos = io.carregar(caminhoCsv);
            if (!lidos.isEmpty()) {
                lidos.forEach(servicoHabito::cadastrar);
            }
        } catch (Exception e) {
        }

        if (repositorio.listarTodos().isEmpty()) {
            servicoHabito.cadastrar(new EnergiaHabito("H1", "Desligar stand-by",
                    LocalDate.now().minusDays(1), 2.0));
            servicoHabito.cadastrar(new TransporteHabito("H2", "Ir de bike ao trabalho",
                    LocalDate.now().minusDays(2), 8.0));
            servicoHabito.cadastrar(new AlimentacaoHabito("H3", "Almoço vegetariano",
                    LocalDate.now(), 1.0));
        }
    }

    private TableView<Habito> montarTabela() {
        var tv = new TableView<Habito>();
        tv.setItems(FXCollections.observableArrayList(servicoHabito.listarOrdenadoPorDataDecrescente()));

        var colTipo = new TableColumn<Habito, String>("Tipo");
        colTipo.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));

        var colNome = new TableColumn<Habito, String>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        var colData = new TableColumn<Habito, String>("Data");
        colData.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getData().format(fmt)));

        var colQtd = new TableColumn<Habito, String>("Quantidade");
        colQtd.setCellValueFactory(cd -> new SimpleStringProperty(String.format(java.util.Locale.getDefault(),"%.2f", cd.getValue().getQuantidade())));

        var colImpacto = new TableColumn<Habito, String>("Impacto (kg CO₂)");
        colImpacto.setCellValueFactory(cd -> new SimpleStringProperty(String.format(java.util.Locale.getDefault(),"%.2f", cd.getValue().calcularImpacto())));

        tv.getColumns().addAll(colTipo, colNome, colData, colQtd, colImpacto);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        return tv;
    }

    private BarChart<String, Number> montarGraficoBarra() {
        var x = new CategoryAxis();
        x.setLabel("Dia");
        var y = new NumberAxis();
        y.setLabel("Impacto (kg CO₂)");

        var chart = new BarChart<>(x, y);
        chart.setTitle("Impacto diário (kg CO₂)");
        chart.setLegendVisible(false);
        chart.getStyleClass().add("card");
        chart.setMinWidth(650);
        return chart;
    }

    private PieChart montarGraficoPizza() {
        var chart = new PieChart();
        chart.setTitle("Distribuição por categoria");
        chart.getStyleClass().add("card");
        chart.setMinWidth(380);
        return chart;
    }

    private void atualizarTudo() {
        LocalDate inicio = java.util.Optional.ofNullable(dpInicio.getValue()).orElse(LocalDate.now().minusDays(6));
        LocalDate fim = java.util.Optional.ofNullable(dpFim.getValue()).orElse(LocalDate.now());

        var habitosPeriodo = servicoHabito.filtrarPorPeriodo(inicio, fim);
        tabela.setItems(FXCollections.observableArrayList(
                habitosPeriodo.stream()
                        .sorted(java.util.Comparator.comparing(Habito::getData).reversed())
                        .collect(Collectors.toList())
        ));

        double soma = servicoHabito.somarImpactoPeriodo(inicio, fim);
        lblResumo.setText("Período: %s a %s • Impacto total: %.2f kg CO₂"
                .formatted(inicio.format(fmt), fim.format(fmt), soma));

        var porDia = new ServicoRelatorio().impactoDiario(habitosPeriodo);
        var serie = new XYChart.Series<String, Number>();
        porDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> serie.getData().add(new XYChart.Data<>(e.getKey().format(fmt), e.getValue())));
        graficoBarra.getData().setAll(serie);

        var porTipo = habitosPeriodo.stream()
                .collect(Collectors.groupingBy(h -> h.getClass().getSimpleName(),
                        Collectors.summingDouble(Habito::calcularImpacto)));
        var fatias = porTipo.entrySet().stream()
                .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        graficoPizza.setData(FXCollections.observableArrayList(fatias));
    }

    private void alerta(String titulo, String msg, Alert.AlertType tipo) {
        var a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
