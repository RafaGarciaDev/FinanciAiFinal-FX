package org.example.financiaifinalfx.controller;

import org.example.financiaifinalfx.enums.TipoAmortizacao;
import org.example.financiaifinalfx.model.entities.Parcelas;
import org.example.financiaifinalfx.services.Price;
import org.example.financiaifinalfx.services.SAC;

import java.util.ArrayList;
import java.util.List;

public class FinanciamentoController {

    private static final double LIMITE_PARCELA_RENDA = 0.3;
    private static final double ENTRADA_MINIMA_PERCENTUAL = 0.2;
    private static final int PRAZO_MINIMO = 12;
    private static final int PRAZO_MAXIMO = 360;
    private static final double TAXA_MINIMA = 0.1;
    private static final double TAXA_MAXIMA = 30.0;

    public static List<Parcelas> calcularFinanciamento(double rendaMensal, double valorImovel, double valorEntrada,
                                                       double taxaJurosAnual, int prazo, TipoAmortizacao amortizacao) {
        // Validações dos parâmetros
        validarParametros(rendaMensal, valorImovel, valorEntrada, taxaJurosAnual, prazo);

        double valorFinanciamento = valorImovel - valorEntrada;
        double taxaJurosMensal = taxaJurosAnual / 12 / 100;
        double limiteParcela = rendaMensal * LIMITE_PARCELA_RENDA;

        List<Parcelas> parcelas = new ArrayList<>();

        try {
            switch (amortizacao) {
                case PRICE:
                    parcelas = calcularPrice(valorFinanciamento, taxaJurosMensal, prazo, limiteParcela);
                    break;
                case SAC:
                    parcelas = calcularSAC(valorFinanciamento, taxaJurosMensal, prazo, limiteParcela);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de amortização não suportado: " + amortizacao);
            }

            return parcelas;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular financiamento: " + e.getMessage(), e);
        }
    }

    private static void validarParametros(double rendaMensal, double valorImovel, double valorEntrada,
                                          double taxaJurosAnual, int prazo) {
        if (valorEntrada < valorImovel * ENTRADA_MINIMA_PERCENTUAL) {
            throw new IllegalArgumentException("Entrada mínima deve ser de " + (ENTRADA_MINIMA_PERCENTUAL * 100) + "% do valor do imóvel.");
        }

        if (prazo < PRAZO_MINIMO || prazo > PRAZO_MAXIMO) {
            throw new IllegalArgumentException("Prazo deve estar entre " + PRAZO_MINIMO + " e " + PRAZO_MAXIMO + " meses.");
        }

        if (taxaJurosAnual < TAXA_MINIMA || taxaJurosAnual > TAXA_MAXIMA) {
            throw new IllegalArgumentException("Taxa de juros anual deve estar entre " + TAXA_MINIMA + "% e " + TAXA_MAXIMA + "%.");
        }

        if (rendaMensal <= 0) {
            throw new IllegalArgumentException("Renda mensal deve ser maior que zero.");
        }

        if (valorImovel <= 0) {
            throw new IllegalArgumentException("Valor do imóvel deve ser maior que zero.");
        }
    }

    private static List<Parcelas> calcularPrice(double valorFinanciamento, double taxaJurosMensal,
                                                int prazo, double limiteParcela) {
        Price price = new Price();
        List<Double> valoresParcelas = price.calculaParcela(valorFinanciamento, taxaJurosMensal, prazo);
        List<Double> amortizacoes = price.calculaAmortizacao(valorFinanciamento, taxaJurosMensal, prazo);

        if (valoresParcelas.get(0) > limiteParcela) {
            throw new IllegalArgumentException("Financiamento não aprovado! A primeira parcela excede " +
                    (LIMITE_PARCELA_RENDA * 100) + "% da renda mensal.");
        }

        List<Parcelas> parcelas = new ArrayList<>();
        for (int i = 0; i < prazo; i++) {
            double juros = valoresParcelas.get(i) - amortizacoes.get(i);
            parcelas.add(new Parcelas(
                    i+1,                    // numeroParcela
                    valoresParcelas.get(i),  // valorParcela
                    amortizacoes.get(i),     // valorAmortizacao
                    0                       // financiamentoId (0 temporário)
            ));
        }
        return parcelas;
    }

    private static List<Parcelas> calcularSAC(double valorFinanciamento, double taxaJurosMensal,
                                              int prazo, double limiteParcela) {
        SAC sac = new SAC();
        List<Double> parcelasSac = sac.calculaParcela(valorFinanciamento, taxaJurosMensal, prazo);
        List<Double> amortizacaoSac = sac.calculaAmortizacao(valorFinanciamento, taxaJurosMensal, prazo);

        if (parcelasSac.get(0) > limiteParcela) {
            throw new IllegalArgumentException("Financiamento não aprovado! A primeira parcela excede " +
                    (LIMITE_PARCELA_RENDA * 100) + "% da renda mensal.");
        }

        List<Parcelas> parcelas = new ArrayList<>();
        for (int i = 0; i < prazo; i++) {
            double juros = parcelasSac.get(i) - amortizacaoSac.get(i);
            parcelas.add(new Parcelas(
                    i+1,                   // numeroParcela
                    parcelasSac.get(i),     // valorParcela
                    amortizacaoSac.get(i),  // valorAmortizacao
                    0                      // financiamentoId (0 temporário)
            ));
        }
        return parcelas;
    }
}