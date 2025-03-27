package org.example.financiaifinalfx.model.entities;

import org.example.financiaifinalfx.enums.TipoImovel;

public class Imovel {
    private TipoImovel tipoImovel;
    private Double valorImovel;

    public Imovel() {}

    // Modifique este construtor para aceitar TipoImovel em vez de String
    public Imovel(TipoImovel tipoImovel, Double valorImovel) {
        this.tipoImovel = tipoImovel;
        this.valorImovel = valorImovel;
    }

    // Adicione este novo construtor que aceita String e converte para enum
    public Imovel(String tipoImovelStr, Double valorImovel) {
        this.tipoImovel = TipoImovel.valueOf(tipoImovelStr.toUpperCase());
        this.valorImovel = valorImovel;
    }

    // Restante dos métodos permanecem iguais...
    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public Double getValorImovel() {
        return valorImovel;
    }

    public void setValorImovel(Double valorImovel) {
        this.valorImovel = valorImovel;
    }
}