package com.exemplo.ecohabitos.dominio;

public class UsuarioPerfil {
    private String nome;
    private double metaSemanalKgCO2; // meta de CO2 evitado por semana

    public UsuarioPerfil(String nome, double metaSemanalKgCO2) {
        setNome(nome);
        setMetaSemanalKgCO2(metaSemanalKgCO2);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do usuário inválido.");
        }
        this.nome = nome;
    }

    public double getMetaSemanalKgCO2() {
        return metaSemanalKgCO2; }
    
    public void setMetaSemanalKgCO2(double metaSemanalKgCO2) {
        if (metaSemanalKgCO2 < 0) {
            throw new IllegalArgumentException("Meta semanal não pode ser negativa.");
        }
        this.metaSemanalKgCO2 = metaSemanalKgCO2;
    }

    @Override public String toString() {
        return "UsuarioPerfil{nome='%s', metaSemanalKgCO2=%.2f}".formatted(nome, metaSemanalKgCO2);
    }
}
