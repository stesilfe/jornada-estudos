package com.exemplo.ecohabitos.excecoes;

public class HabitoInvalidoException extends RuntimeException {

    public HabitoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
