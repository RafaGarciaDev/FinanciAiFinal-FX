package org.example.financiaifinalfx.enums;

public enum TipoAmortizacao {
    SAC("Sistema de Amortização Constante"),
    PRICE("Sistema Francês de Amortização");

    private final String descricao;

    TipoAmortizacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoAmortizacao fromString(String text) {
        for (TipoAmortizacao tipo : TipoAmortizacao.values()) {
            if (tipo.name().equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de amortização encontrado para: " + text);
    }

    public String toShortString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

