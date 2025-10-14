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
import java.util.Locale;

public class ArmazenamentoCsv {

    public void salvar(Path caminho, List<Habito> habitos) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
            for (Habito h : habitos) {
                String tipo = h.getClass().getSimpleName();
                String quantidadeStr = String.format(Locale.US, "%.2f", h.getQuantidade());
                String linha = "%s;%s;%s;%s;%s".formatted(
                        tipo, h.getId(), h.getNome(), h.getData(), quantidadeStr);
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
            boolean primeira = true;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;

                if (primeira) {
                    String lcase = linha.toLowerCase();
                    if (lcase.startsWith("tipo;") && lcase.contains(";data;")) {
                        primeira = false;
                        continue;
                    }
                }
                primeira = false;

                String[] partes = linha.split(";");
                if (partes.length != 5) continue;

                String tipo = partes[0].trim();
                String id = partes[1].trim();
                String nome = partes[2].trim();
                String dataStr = partes[3].trim();
                String qtdStr = partes[4].trim().replace(",", ".");

                try {
                    LocalDate data = LocalDate.parse(dataStr); // yyyy-MM-dd
                    double quantidade = Double.parseDouble(qtdStr);

                    Habito h = criarHabitoPorTipo(tipo, id, nome, data, quantidade);
                    resultado.add(h);
                } catch (Exception linhaRuim) {}
            }
        } catch (IOException e) {
            throw e; // repropaga I/O
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

