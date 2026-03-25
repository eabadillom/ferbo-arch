package com.ferbo.arch.persistence;

import java.util.Collections;
import java.util.List;

/**
 * Resultado paginado de una consulta.
 * 
 * @param <T> Tipo de entidad
 */
public class ResultadoPaginado<T> {

    private final List<T> resultados;
    private final long totalRegistros;
    private final int pagina;
    private final int tamanioPagina;

    public ResultadoPaginado(List<T> resultados, long totalRegistros, int pagina, int tamanioPagina) {
        this.resultados = (resultados != null) ? resultados : Collections.emptyList();
        this.totalRegistros = Math.max(totalRegistros, 0L);
        this.pagina = Math.max(pagina, 0);
        this.tamanioPagina = Math.max(tamanioPagina, 1);
    }

    public List<T> getResultados() {
        return resultados;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamanioPagina() {
        return tamanioPagina;
    }

    /** Total de páginas calculadas */
    public int getTotalPaginas() {
        return (int) Math.ceil((double) totalRegistros / tamanioPagina);
    }

    /** Devuelve true si hay resultados */
    public boolean tieneResultados() {
        return !resultados.isEmpty();
    }
}