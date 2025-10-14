package com.exemplo.ecohabitos.dominio;

public class ImpactoCalculadoraPadrao implements ImpactoCalculadora {
    @Override
    public double calcular(Habito habito) {
        return habito.calcularImpacto();
    }
}
