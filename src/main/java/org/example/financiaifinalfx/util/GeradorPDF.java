package org.example.financiaifinalfx.util;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.financiaifinalfx.model.entities.Cliente;
import org.example.financiaifinalfx.model.entities.Financiamento;
import org.example.financiaifinalfx.model.entities.Imovel;
import org.example.financiaifinalfx.model.entities.Parcelas;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GeradorPDF {

    public static void gerarPDF(Financiamento financiamento, Cliente cliente, Imovel imovel, List<Parcelas> parcelas) {
        Document document = new Document();

        try {
            // Cria o arquivo PDF
            String fileName = "SimulacaoFinanciamento_" + financiamento.getFinanciamentoId() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Cria uma tabela com 2 colunas: uma para o logo e outra para as informações
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setSpacingAfter(10);

            // Adiciona o logo na primeira coluna
            try {
                Image logo = Image.getInstance(GeradorPDF.class.getResource("/images/logo.jpeg"));
                logo.scaleToFit(200, 200);

                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                headerTable.addCell(logoCell);
            } catch (IOException e) {
                System.err.println("Erro ao carregar o logo: " + e.getMessage());
                // Adiciona célula vazia se o logo não for encontrado
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            // Adiciona as informações na segunda coluna
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);

            // Configuração das fontes
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph infoParagraph = new Paragraph();
            infoParagraph.add(new Paragraph("ID do Financiamento: " + financiamento.getFinanciamentoId(), normalFont));
            infoParagraph.add(new Paragraph("Cliente: " + cliente.getNome(), normalFont));
            infoParagraph.add(new Paragraph("Tipo de Imóvel: " + imovel.getTipoImovel(), normalFont));
            infoParagraph.add(new Paragraph("Valor do Imóvel: R$ " + String.format("%.2f", imovel.getValorImovel()), normalFont));
            infoParagraph.add(new Paragraph("Valor de Entrada: R$ " + String.format("%.2f", financiamento.getValorEntrada()), normalFont));
            infoParagraph.add(new Paragraph("Valor Financiado: R$ " + String.format("%.2f", financiamento.getValorFinanciado()), normalFont));
            infoParagraph.add(new Paragraph("Taxa de Juros: " + financiamento.getTaxaJuros() + "%", normalFont));
            infoParagraph.add(new Paragraph("Prazo: " + financiamento.getPrazo() + " meses", normalFont));
            infoParagraph.add(new Paragraph("Tipo de Amortização: " + financiamento.getTipoAmortizacao(), normalFont));
            infoParagraph.add(new Paragraph("Total a Pagar: R$ " + String.format("%.2f", financiamento.getTotalPagar()), normalFont));

            infoCell.addElement(infoParagraph);
            headerTable.addCell(infoCell);

            // Adiciona a tabela ao documento
            document.add(headerTable);

            // Adiciona título principal
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Simulação de Financiamento", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Adiciona tabela de parcelas
            document.add(new Paragraph("\nTabela de Parcelas:", headerFont));
            PdfPTable table = new PdfPTable(4); // 4 colunas: Parcela, Valor Parcela, Amortização, Juros
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            // Cabeçalho da tabela
            table.addCell(new PdfPCell(new Phrase("Parcela", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Valor Parcela", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Amortização", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Juros", headerFont)));

            // Adiciona as parcelas à tabela
            for (Parcelas parcela : parcelas) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(parcela.getNumeroParcela()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("R$ " + String.format("%.2f", parcela.getValorParcela()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("R$ " + String.format("%.2f", parcela.getValorAmortizacao()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("R$ " + String.format("%.2f", parcela.getValorJuros()), normalFont)));
            }

            document.add(table);

            // Adiciona data de emissão
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("\nData de Emissão: " + dateFormat.format(new Date()), normalFont));

            System.out.println("PDF gerado com sucesso: " + fileName);
        } catch (DocumentException | IOException e) {
            System.err.println("Erro ao gerar o PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}