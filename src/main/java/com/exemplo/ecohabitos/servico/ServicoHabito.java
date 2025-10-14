package com.exemplo.ecohabitos.servico;

import com.exemplo.ecohabitos.dominio.Habito;
import com.exemplo.ecohabitos.dominio.ImpactoCalculadora;
import com.exemplo.ecohabitos.repositorio.Repositorio;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicoHabito {

    private final Repositorio<Habito, String> repositorio;
    private final ImpactoCalculadora impactoCalculadora;

    public ServicoHabito(Repositorio<Habito, String> repositorio,
                         ImpactoCalculadora impactoCalculadora) {
        this.repositorio = repositorio;
        this.impactoCalculadora = impactoCalculadora;
    }

    public Habito cadastrar(Habito habito) {
        return repositorio.salvar(habito);
    }

    public List<Habito> listarOrdenadoPorDataDecrescente() {
        return repositorio.listarTodos().stream()
                .sorted(Comparator.comparing(Habito::getData).reversed())
                .collect(Collectors.toList());
    }

    public List<Habito> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return repositorio.listarTodos().stream()
                .filter(h -> !h.getData().isBefore(inicio) && !h.getData().isAfter(fim))
                .collect(Collectors.toList());
    }

    public double somarImpactoPeriodo(LocalDate inicio, LocalDate fim) {
        return filtrarPorPeriodo(inicio, fim).stream()
                .mapToDouble(impactoCalculadora::calcular)
                .sum();
    }
}
