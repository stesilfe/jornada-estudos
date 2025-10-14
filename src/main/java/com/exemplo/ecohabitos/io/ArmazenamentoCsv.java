package com.exemplo.ecohabitos.io;

import com.exemplo.ecohabitos.dominio.*;
import com.exemplo.ecohabitos.excecoes.HabitoInvalidoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ArmazenamentoCsv {

    public void salvar(Path caminho, List<Habito> habitos) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
            for (Habito h : habitos) {
                String tipo = h.getClass().getSimpleName();
                String linha = "%s;%s;%s;%s;%.2f".formatted(
                        tipo, h.getId(), h.getNome(), h.getData(), h.getQuantidade());
                bw.write(linha);
                bw.newLine();
            }
        }
    }

    public List<Habito> carregar(Path caminho) throws IOException {
        List<Habito> resultado = new ArrayList<>();
        if (!Files.exists(caminho)) return resultado;

        try (BufferedReader br = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length != 5) continue;

                String tipo = partes[0];
                String id = partes[1];
                String nome = partes[2];
                LocalDate data = LocalDate.parse(partes[3]);
                double quantidade = Double.parseDouble(partes[4]);

                Habito h = criarHabitoPorTipo(tipo, id, nome, data, quantidade);
                resultado.add(h);
            }
        } catch (NumberFormatException | HabitoInvalidoException e) {
            throw new IOException("Falha ao carregar CSV: " + e.getMessage(), e);
        } finally {
        }
        return resultado;
    }

    private Habito criarHabitoPorTipo(String tipo, String id, String nome, LocalDate data, double q) {
        return switch (tipo) {
            case "EnergiaHabito" -> new EnergiaHabito(id, nome, data, q);
            case "TransporteHabito" -> new TransporteHabito(id, nome, data, q);
            case "AlimentacaoHabito" -> new AlimentacaoHabito(id, nome, data, q);
            default -> throw new HabitoInvalidoException("Tipo de hábito desconhecido: " + tipo);
        };
    }
}
