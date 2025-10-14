package com.exemplo.ecohabitos.repositorio;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RepositorioEmMemoria<T, ID> implements Repositorio<T, ID> {

    private final Map<ID, T> mapa = new ConcurrentHashMap<>();
    private final Set<ID> ids = Collections.synchronizedSet(new HashSet<>());
    private final java.util.function.Function<T, ID> extratorId;

    public RepositorioEmMemoria(java.util.function.Function<T, ID> extratorId) {
        this.extratorId = Objects.requireNonNull(extratorId);
    }

    @Override public T salvar(T entidade) {
        ID id = extratorId.apply(entidade);
        ids.add(id);
        mapa.put(id, entidade);
        return entidade;
    }

    @Override public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(mapa.get(id));
    }

    @Override public List<T> listarTodos() {
        return mapa.values().stream().collect(Collectors.toList());
    }

    @Override public void removerPorId(ID id) {
        mapa.remove(id);
        ids.remove(id);
    }

    @Override public boolean existePorId(ID id) {
        return ids.contains(id);
    }
}
