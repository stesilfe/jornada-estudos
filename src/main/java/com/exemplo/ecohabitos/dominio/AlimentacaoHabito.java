package com.exemplo.ecohabitos.dominio;

import java.time.LocalDate;

public class AlimentacaoHabito extends Habito {

    private static final double KG_CO2_POR_REFEICAO = 1.5;

    public AlimentacaoHabito(String id, String nome, LocalDate data, double refeicoesPlanta) {
        super(id, nome, data, refeicoesPlanta);
    }

    @Override
    public double calcularImpacto() {
        return getQuantidade() * KG_CO2_POR_REFEICAO;
    }
}
