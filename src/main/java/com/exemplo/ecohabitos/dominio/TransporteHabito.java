package com.exemplo.ecohabitos.dominio;

import java.time.LocalDate;

public class TransporteHabito extends Habito {

    private static final double KG_CO2_POR_KM = 0.12;

    public TransporteHabito(String id, String nome, LocalDate data, double kmNaoDirigidos) {
        super(id, nome, data, kmNaoDirigidos);
    }

    @Override
    public double calcularImpacto() {
        return getQuantidade() * KG_CO2_POR_KM;
    }
}
