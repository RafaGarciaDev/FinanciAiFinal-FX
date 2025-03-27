package org.example.financiaifinalfx.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.NumberStringConverter;
import org.example.financiaifinalfx.controller.FinanciamentoController;
import org.example.financiaifinalfx.enums.TipoAmortizacao;
import org.example.financiaifinalfx.model.entities.Cliente;
import org.example.financiaifinalfx.model.entities.Financiamento;
import org.example.financiaifinalfx.model.entities.Imovel;
import org.example.financiaifinalfx.model.entities.Parcelas;
import org.example.financiaifinalfx.util.GeradorPDF;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FinanciamentoView_Controller {

    // Formatters
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final NumberFormat PERCENT = NumberFormat.getPercentInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtRenda;
    @FXML private ComboBox<String> cbTipoImovel;
    @FXML private TextField txtValorImovel;
    @FXML private TextField txtEntrada;
    @FXML private TextField txtJuros;
    @FXML private TextField txtPrazo;
    @FXML private ComboBox<String> cbAmortizacao;

    @FXML private TableView<Parcelas> tabelaParcelas;
    @FXML private TableColumn<Parcelas, Integer> colunaNumero;
    @FXML private TableColumn<Parcelas, Double> colunaValor;
    @FXML private TableColumn<Parcelas, Double> colunaAmortizacao;
    @FXML private TableColumn<Parcelas, Double> colunaJuros;

    @FXML
    public void initialize() {
        // Initialize combo boxes
        cbTipoImovel.getItems().addAll("Casa", "Apartamento");
        cbAmortizacao.getItems().addAll("Price", "SAC");

        // Set up number formatting
        txtRenda.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        txtValorImovel.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        txtEntrada.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        txtJuros.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        txtPrazo.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        // Set default values
        txtEntrada.setText("20");
        txtJuros.setText("10");
        txtPrazo.setText("48");

        // Configure table columns
        configureTableColumns();
    }

    private void configureTableColumns() {
        colunaNumero.setCellValueFactory(new PropertyValueFactory<>("numeroParcela"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorParcela"));
        colunaAmortizacao.setCellValueFactory(new PropertyValueFactory<>("valorAmortizacao"));
        colunaJuros.setCellValueFactory(new PropertyValueFactory<>("valorJuros"));

        // Format numeric columns
        colunaValor.setCellFactory(column -> new TableCell<Parcelas, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("R$ %.2f", item));
            }
        });

        colunaAmortizacao.setCellFactory(column -> new TableCell<Parcelas, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("R$ %.2f", item));
            }
        });

        colunaJuros.setCellFactory(column -> new TableCell<Parcelas, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("R$ %.2f", item));
            }
        });
    }

    @FXML
    private void calcularFinanciamento() {
        try {
            validateInputFields();

            // Get input values
            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            double renda = Double.parseDouble(txtRenda.getText());
            String tipoImovel = cbTipoImovel.getValue();
            double valorImovel = Double.parseDouble(txtValorImovel.getText());
            double entradaPercentual = Double.parseDouble(txtEntrada.getText());
            double jurosAnual = Double.parseDouble(txtJuros.getText());
            int prazo = Integer.parseInt(txtPrazo.getText());
            TipoAmortizacao tipoAmortizacao = cbAmortizacao.getValue().equals("Price")
                    ? TipoAmortizacao.PRICE
                    : TipoAmortizacao.SAC;

            // Calculate values
            double valorEntrada = valorImovel * (entradaPercentual / 100);

            // Calculate installments
            List<Parcelas> parcelas = FinanciamentoController.calcularFinanciamento(
                    renda, valorImovel, valorEntrada, jurosAnual, prazo, tipoAmortizacao);

            // Update UI
            updateUIWithResults(nome, cpf, renda, tipoImovel, valorImovel,
                    valorEntrada, jurosAnual, prazo, tipoAmortizacao, parcelas);

        } catch (NumberFormatException e) {
            showErrorAlert("Erro de formato", "Por favor, insira valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            showErrorAlert("Erro de validação", e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Erro", "Ocorreu um erro ao calcular o financiamento: " + e.getMessage());
        }
    }

    private void validateInputFields() {
        if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty()) {
            throw new IllegalArgumentException("Nome e CPF são obrigatórios.");
        }
        if (cbTipoImovel.getValue() == null || cbAmortizacao.getValue() == null) {
            throw new IllegalArgumentException("Tipo de imóvel e amortização são obrigatórios.");
        }

        double entradaPercentual = Double.parseDouble(txtEntrada.getText());
        if (entradaPercentual < 20 || entradaPercentual > 100) {
            throw new IllegalArgumentException("Entrada deve ser entre 20% e 100%.");
        }

        double jurosAnual = Double.parseDouble(txtJuros.getText());
        if (jurosAnual <= 0 || jurosAnual > 30) {
            throw new IllegalArgumentException("Taxa de juros deve ser entre 0.1% e 30%.");
        }

        int prazo = Integer.parseInt(txtPrazo.getText());
        if (prazo < 12 || prazo > 360) {
            throw new IllegalArgumentException("Prazo deve ser entre 12 e 360 meses.");
        }
    }

    private void updateUIWithResults(String nome, String cpf, double renda, String tipoImovel,
                                     double valorImovel, double valorEntrada, double jurosAnual,
                                     int prazo, TipoAmortizacao tipoAmortizacao, List<Parcelas> parcelas) {
        // Update table
        tabelaParcelas.getItems().setAll(parcelas);

        // Create model objects - agora usando o construtor que aceita String
        Cliente cliente = new Cliente(nome, cpf, renda);
        Imovel imovel = new Imovel(tipoImovel, valorImovel); // Isso vai usar o novo construtor
        Financiamento financiamento = new Financiamento(
                prazo, jurosAnual, tipoAmortizacao, valorEntrada,
                valorImovel - valorEntrada, calcularTotalPagar(parcelas));

        // Restante do método permanece igual...
    }

    private double calcularTotalPagar(List<Parcelas> parcelas) {
        return parcelas.stream().mapToDouble(Parcelas::getValorParcela).sum();
    }

    public void mostrarDetalhesFinanciamento(Cliente cliente, Imovel imovel,
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
        System.out.println("Entrada: " + CURRENCY.format(financiamento.getValorEntrada()));
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
                CURRENCY.format(p.getValorJuros())));

        // Resumo
        double totalJuros = parcelas.stream()
                .mapToDouble(Parcelas::getValorJuros)
                .sum();

        System.out.println("\n[RESUMO]");
        System.out.println("Total Juros: " + CURRENCY.format(totalJuros));
        System.out.println("CET Anual: " + PERCENT.format(calcularCET(financiamento, parcelas)));
    }

    private String formatarCPF(String cpf) {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private double calcularCET(Financiamento financiamento, List<Parcelas> parcelas) {
        double totalPago = financiamento.getTotalPagar();
        double valorFinanciado = financiamento.getValorFinanciado();
        int prazo = financiamento.getPrazo();
        return Math.pow(totalPago/valorFinanciado, 12.0/prazo) - 1;
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Financiamento calculado com sucesso!\nPDF gerado.");
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}