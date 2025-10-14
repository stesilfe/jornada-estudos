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
        var calculadora = new ImpactoCalculadoraPadrao();
        var servico = new ServicoHabito(repo, calculadora);
        var relatorio = new ServicoRelatorio();
        var io = new ArmazenamentoCsv();

        var usuario = new UsuarioPerfil("Usuária(o) Eco", 5.0);
        System.out.println("Perfil: " + usuario);

        Path arquivo = Path.of("src/main/resources/dados-exemplo-habitos.csv");

        try {
            List<Habito> lidos = io.carregar(arquivo);
            if (lidos.isEmpty()) {
                Habito h1 = new EnergiaHabito("H1", "Desligar stand-by", LocalDate.now().minusDays(1), 2.0);
                Habito h2 = new TransporteHabito("H2", "Ir de bike ao trabalho", LocalDate.now().minusDays(2), 8.0);
                Habito h3 = new AlimentacaoHabito("H3", "Almoço vegetariano", LocalDate.now(), 1.0);
                servico.cadastrar(h1);
                servico.cadastrar(h2);
                servico.cadastrar(h3);
            } else {
                servico.importar(lidos, true);
            }

            assert repo.listarTodos().size() >= 3;

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

            io.salvar(arquivo, servico.listarOrdenadoPorDataDecrescente());
            System.out.println("Hábitos salvos em: " + arquivo.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Falha no fluxo principal: " + e.getMessage());
        } finally {
            try (Scanner sc = new Scanner(System.in)) {
                System.out.println("\nDeseja abrir o menu? (s/n)");
                String resp = sc.nextLine().trim().toLowerCase();
                if (resp.equals("s")) {
                    menu(sc, servico, relatorio, io, arquivo);
                }
            }
        }

        System.out.println("=== Fim ===");
    }

    private static void menu(Scanner sc, ServicoHabito servico, ServicoRelatorio relatorio,
                             ArmazenamentoCsv io, Path arquivoPrincipal) {
        while (true) {
            System.out.println("\n== Menu ==");
            System.out.println("1) Listar hábitos por data (desc)");
            System.out.println("2) Somar impacto últimos 7 dias");
            System.out.println("3) Importar novos hábitos de um CSV (formato: tipo;id;nome;data;quantidade)");
            System.out.println("4) Exportar CSV atual");
            System.out.println("0) Sair");
            System.out.print("> ");
            String opc = sc.nextLine().trim();

            try {
                switch (opc) {
                    case "1" -> servico.listarOrdenadoPorDataDecrescente().forEach(System.out::println);

                    case "2" -> {
                        var fim = LocalDate.now();
                        var inicio = fim.minusDays(6);
                        double soma = servico.somarImpactoPeriodo(inicio, fim);
                        System.out.printf("Últimos 7 dias (%s a %s): %.2f kg CO2%n",
                                relatorio.formatar(inicio), relatorio.formatar(fim), soma);
                    }

                    case "3" -> {
                        Path csvImport = Path.of("/home/stefany/Documentos/habitossust/novos-habitos.csv");
                        System.out.println("Importando hábitos de: " + csvImport);
                        List<Habito> novos = io.carregar(csvImport);
                        int qtd = servico.importar(novos, true); // ignora IDs duplicados
                        System.out.printf("Importação concluída: %d novos hábito(s) adicionados.%n", qtd);
                        io.salvar(arquivoPrincipal, servico.listarOrdenadoPorDataDecrescente());
                        System.out.println("CSV principal atualizado em: " + arquivoPrincipal.toAbsolutePath());
                    }

                    case "4" -> {
                        io.salvar(arquivoPrincipal, servico.listarOrdenadoPorDataDecrescente());
                        System.out.println("Exportado para: " + arquivoPrincipal.toAbsolutePath());
                    }

                    case "0" -> {
                        return;
                    }

                    default -> System.out.println("Opção inválida.");
                }
            } catch (HabitoInvalidoException e) {
                System.out.println("[Entrada inválida] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[Erro] " + e.getMessage());
            }
        }
    }
}

