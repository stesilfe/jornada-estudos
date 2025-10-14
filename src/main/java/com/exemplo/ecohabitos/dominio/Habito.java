package com.exemplo.ecohabitos.dominio;

import java.time.LocalDate;
import java.util.Objects;
import com.exemplo.ecohabitos.excecoes.HabitoInvalidoException;

public abstract class Habito {
    private final String id;
    private String nome;
    private LocalDate data;
    private double quantidade;

    protected Habito(String id, String nome, LocalDate data, double quantidade) {
        if (id == null || id.isBlank()) {
            throw new HabitoInvalidoException("ID do hábito não pode ser vazio.");
        }
        this.id = id;
        setNome(nome);
        setData(data);
        setQuantidade(quantidade);
    }

    public abstract double calcularImpacto();

    public String getId() { 
        return id; }

    public String getNome() { 
        return nome; }
    
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new HabitoInvalidoException("Nome do hábito inválido.");
        }
        this.nome = nome;
    }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) {
        if (data == null) {
            throw new HabitoInvalidoException("Data do hábito não pode ser nula.");
        }
        this.data = data;
    }

    public double getQuantidade() {
        return quantidade; }
    
    public void setQuantidade(double quantidade) {
        if (quantidade <= 0) {
            throw new HabitoInvalidoException("Quantidade deve ser positiva.");
        }
        this.quantidade = quantidade;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Habito)) return false;
        Habito habito = (Habito) o;
        return Objects.equals(id, habito.id);
    }

    @Override public int hashCode() { 
        return Objects.hash(id); }

    @Override public String toString() {
        return "%s{id='%s', nome='%s', data=%s, quantidade=%.2f}"
                .formatted(getClass().getSimpleName(), id, nome, data, quantidade);
    }
}
