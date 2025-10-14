package com.exemplo.ecohabitos.servico;

import com.exemplo.ecohabitos.dominio.Habito;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServicoRelatorio {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LocalDate inicioDaSemana(LocalDate referencia) {
        DayOfWeek dow = referencia.getDayOfWeek();
        int diff = dow.getValue() - DayOfWeek.MONDAY.getValue();
        return referencia.minusDays(diff);
    }

    public LocalDate fimDaSemana(LocalDate referencia) {
        return inicioDaSemana(referencia).plusDays(6);
    }

    public String formatar(LocalDate data) {
        return data.format(FORMATADOR);
    }

    public Map<LocalDate, Double> impactoDiario(List<Habito> habitos) {
        return habitos.stream().collect(Collectors.groupingBy(
                Habito::getData,
                Collectors.summingDouble(Habito::calcularImpacto)
        ));
    }

    public Period periodoEntre(LocalDate inicio, LocalDate fim) {
        return Period.between(inicio, fim);
    }

    public Duration duracaoEntre(LocalDateTime a, LocalDateTime b) {
        return Duration.between(a, b);
    }
}
