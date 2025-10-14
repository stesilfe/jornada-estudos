package com.exemplo.ecohabitos.app;

import com.exemplo.ecohabitos.dominio.*;
import com.exemplo.ecohabitos.excecoes.HabitoInvalidoException;
import com.exemplo.ecohabitos.io.ArmazenamentoCsv;
import com.exemplo.ecohabitos.repositorio.RepositorioEmMemoria;
import com.exemplo.ecohabitos.servico.ServicoHabito;
import com.exemplo.ecohabitos.servico.ServicoRelatorio;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== EcoHabits Tracker ===");

        var repo = new RepositorioEmMemoria<Habito, String>(Habito::getId);
        var calc = new ImpactoCalculadoraPadrao();
        var servico = new ServicoHabito(repo, calc);
        var relatorio = new ServicoRelatorio();
        var io = new ArmazenamentoCsv();

        var usuario = new UsuarioPerfil("Usuária(o) Eco", 5.0); // 5 kg CO2 semanais
        System.out.println("Perfil: " + usuario);

        try {
            Habito h1 = new EnergiaHabito("H1", "Desligar stand-by", LocalDate.now().minusDays(1), 2.0);
            Habito h2 = new TransporteHabito("H2", "Ir de bike ao trabalho", LocalDate.now().minusDays(2), 8.0);
            Habito h3 = new AlimentacaoHabito("H3", "Almoço vegetariano", LocalDate.now(), 1.0);

            servico.cadastrar(h1);
            servico.cadastrar(h2);
            servico.cadastrar(h3);

            assert repo.existePorId("H1");
            assert repo.listarTodos().size() == 3;

            LocalDate hoje = LocalDate.now();
            LocalDate inicio = relatorio.inicioDaSemana(hoje);
            LocalDate fim = relatorio.fimDaSemana(hoje);

            double impactoSemana = servico.somarImpactoPeriodo(inicio, fim);
            System.out.printf("Impacto da semana (%s a %s): %.2f kg CO2 evitados%n",
                    relatorio.formatar(inicio), relatorio.formatar(fim), impactoSemana);

            if (impactoSemana >= usuario.getMetaSemanalKgCO2()) {
                System.out.println("Meta semanal atingida/parcialmente atingida! 🎉");
            } else {
                System.out.printf("Faltam %.2f kg CO2 para a meta semanal.%n",
                        (usuario.getMetaSemanalKgCO2() - impactoSemana));
            }

            Path arquivo = Path.of("src/main/resources/dados-exemplo-habitos.csv");
            io.salvar(arquivo, servico.listarOrdenadoPorDataDecrescente());
            System.out.println("Hábitos salvos em: " + arquivo.toAbsolutePath());

            List<Habito> lidos = io.carregar(arquivo);
            System.out.println("Recarregado do CSV (" + lidos.size() + " registros):");
            lidos.forEach(System.out::println);

            try {
                Habito invalido = new EnergiaHabito("H4", "Nome ok", LocalDate.now(), -10);
                servico.cadastrar(invalido);
            } catch (HabitoInvalidoException e) {
                System.out.println("[ERRO ESPERADO] " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Falha no fluxo principal: " + e.getMessage());
        } finally {
            try (Scanner sc = new Scanner(System.in)) {
                System.out.println("\nDeseja testar o menu rápido? (s/n)");
                String resp = sc.nextLine().trim().toLowerCase();
                if (resp.equals("s")) {
                    menuRapido(sc, servico);
                }
            }
        }

        System.out.println("=== Fim ===");
    }

    private static void menuRapido(Scanner sc, ServicoHabito servico) {
        System.out.println("1) Listar hábitos por data (desc)");
        System.out.println("2) Somar impacto últimos 7 dias");
        System.out.print("> ");
        String opc = sc.nextLine();

        switch (opc) {
            case "1" -> servico.listarOrdenadoPorDataDecrescente().forEach(System.out::println);
            case "2" -> {
                var rel = new ServicoRelatorio();
                var fim = LocalDate.now();
                var inicio = fim.minusDays(6);
                double soma = servico.somarImpactoPeriodo(inicio, fim);
                System.out.printf("Últimos 7 dias (%s a %s): %.2f kg CO2%n",
                        rel.formatar(inicio), rel.formatar(fim), soma);
            }
            default -> System.out.println("Opção inválida.");
        }
    }
}
