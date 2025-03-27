package org.example.financiaifinalfx.services;


/*
 * Calcula as parcelas de um financiamento utilizando o método Price.
 * @param valorFinanciamento Valor total do financiamento.
 * @param taxaJuros Taxa de juros mensal.
 * @param prazo Número de parcelas.
 * @return Lista de parcelas calculadas.
 * @throws IllegalArgumentException Se os valores de entrada forem inválidos.
 * */


import java.util.ArrayList;
import java.util.List;

public class Price implements Amortizacao {


    @Override
    public List<Double> calculaParcela(Double valorFinanciamento, Double taxaJuros, int prazo) {

        // Em ambos os arquivos, adicione no início dos métodos de cálculo:
        if (valorFinanciamento == null || taxaJuros == null) {
            throw new IllegalArgumentException("Valores não podem ser nulos.");
        }

        if (valorFinanciamento <= 0 || taxaJuros <= 0 || prazo <= 0) {
            throw new IllegalArgumentException("Valores devem ser positivos. Recebido: " +
                    "Financiamento=" + valorFinanciamento +
                    ", Juros=" + taxaJuros +
                    ", Prazo=" + prazo);
        }

        List<Double> parcelas = new ArrayList<>();
        double parcelaConstante = valorFinanciamento * (taxaJuros / (1 - Math.pow(1 + taxaJuros, -prazo)));
        for (int i = 0; i < prazo; i++) {
            parcelas.add(parcelaConstante);
        }
        return parcelas;
    }

    @Override
    public List<Double> calculaAmortizacao(Double valorFinanciamento, Double taxaJuros, int prazo) {
        List<Double> amortizacoes = new ArrayList<>();
        double parcelaConstante = valorFinanciamento * (taxaJuros / (1 - Math.pow(1 + taxaJuros, -prazo)));
        double saldoDevedor = valorFinanciamento;
        for (int i = 0; i < prazo; i++) {
            double juros = saldoDevedor * taxaJuros;
            double amortizacao = parcelaConstante - juros;
            amortizacoes.add(amortizacao);
            saldoDevedor -= amortizacao;
        }
        return amortizacoes;
    }

    public double totalPagoPrice(double valorFinanciamento, Double taxaJuros, int prazo) {
        List<Double> parcelas = calculaParcela(valorFinanciamento, taxaJuros, prazo);
        double totalPago = 0;

        for (double parcela : parcelas) {
            totalPago += parcela;
        }
        return totalPago;
    }
    public List<Double> calculaJuros(Double valorFinanciamento, Double taxaJuros, int prazo) {
        List<Double> juros = new ArrayList<>();
        double saldoDevedor = valorFinanciamento;
        double parcelaConstante = valorFinanciamento * (taxaJuros / (1 - Math.pow(1 + taxaJuros, -prazo)));

        for (int i = 0; i < prazo; i++) {
            double jurosParcela = saldoDevedor * taxaJuros;
            juros.add(jurosParcela);
            saldoDevedor -= (parcelaConstante - jurosParcela);
        }
        return juros;
    }
}

