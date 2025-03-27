package org.example.financiaifinalfx.view;
import org.example.financiaifinalfx.model.entities.Cliente;
import org.example.financiaifinalfx.model.entities.Financiamento;
import org.example.financiaifinalfx.model.entities.Imovel;
import org.example.financiaifinalfx.model.entities.Parcelas;
import java.util.List;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

    public class FinanciamentoView {
        private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
        private static final NumberFormat PERCENT = NumberFormat.getPercentInstance();
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        public static void mostrarDetalhesFinanciamento(Cliente cliente, Imovel imovel,
                                                        Financiamento financiamento, List<Parcelas> parcelas) {
            PERCENT.setMaximumFractionDigits(2);

            System.out.println("\n=== DETALHES DO FINANCIAMENTO ===");
            System.out.println("Data: " + DATE_FORMAT.format(LocalDateTime.now()));
            System.out.println("\n[CLIENTE]");
            System.out.println("Nome: " + cliente.getNome());
            System.out.println("CPF: " + formatarCPF(cliente.getCpf()));
            System.out.println("Renda Mensal: " + CURRENCY.format(cliente.getRendaMensal()));

            System.out.println("\n[IMÓVEL]");
            System.out.println("Tipo: " + imovel.getTipoImovel());
            System.out.println("Valor: " + CURRENCY.format(imovel.getValorImovel()));

            System.out.println("\n[CONDIÇÕES]");
            System.out.println("Entrada: " + CURRENCY.format(financiamento.getValorEntrada()) +
                    " (" + PERCENT.format(financiamento.getValorEntrada()/imovel.getValorImovel()) + ")");
            System.out.println("Financiado: " + CURRENCY.format(financiamento.getValorFinanciado()));
            System.out.println("Taxa Juros Anual: " + PERCENT.format(financiamento.getTaxaJuros()/100));
            System.out.println("Prazo: " + financiamento.getPrazo() + " meses");
            System.out.println("Sistema: " + financiamento.getTipoAmortizacao());
            System.out.println("Total: " + CURRENCY.format(financiamento.getTotalPagar()));

            System.out.println("\n[PARCELAS]");
            System.out.printf("%6s %15s %15s %15s%n", "Parcela", "Valor", "Amortização", "Juros");
            parcelas.forEach(p -> System.out.printf("%6d %15s %15s %15s%n",
                    p.getNumeroParcela(),
                    CURRENCY.format(p.getValorParcela()),
                    CURRENCY.format(p.getValorAmortizacao()),
                    CURRENCY.format(p.getValorParcela() - p.getValorAmortizacao())));

            // Resumo
            double totalJuros = parcelas.stream()
                    .mapToDouble(p -> p.getValorParcela() - p.getValorAmortizacao())
                    .sum();

            System.out.println("\n[RESUMO]");
            System.out.println("Total Juros: " + CURRENCY.format(totalJuros));
            System.out.println("CET Anual: " + PERCENT.format(calcularCET(financiamento, parcelas)));
        }

        private static String formatarCPF(String cpf) {
            return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }

        private static double calcularCET(Financiamento financiamento, List<Parcelas> parcelas) {
            // Implementação simplificada do Custo Efetivo Total
            double totalPago = financiamento.getTotalPagar();
            double valorFinanciado = financiamento.getValorFinanciado();
            int prazo = financiamento.getPrazo();

            return Math.pow(totalPago/valorFinanciado, 12.0/prazo) - 1;
        }
    }