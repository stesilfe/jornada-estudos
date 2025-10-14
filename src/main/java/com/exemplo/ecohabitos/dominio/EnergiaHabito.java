package com.exemplo.ecohabitos.dominio;

import java.time.LocalDate;

public class EnergiaHabito extends Habito {

    private static final double KG_CO2_POR_KWH = 0.055;

    public EnergiaHabito(String id, String nome, LocalDate data, double kwhEconomizados) {
        super(id, nome, data, kwhEconomizados);
    }

    @Override
    public double calcularImpacto() {
        return getQuantidade() * KG_CO2_POR_KWH;
    }
}

