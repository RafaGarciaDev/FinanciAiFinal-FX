package org.example.financiaifinalfx.view;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.financiaifinalfx.util.Conexao;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Thread.setDefaultUncaughtExceptionHandler(MainApplication::handleException);

        try {
            // Testa conexão com banco antes de iniciar
            Conexao.conectar().close();

            Parent root = FXMLLoader.load(getClass().getResource(
                    "/FinanciamentoView.fxml"));

            Scene scene = new Scene(root, 900, 650);
            //scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            primaryStage.setTitle("FinanciAi - Sistema de Financiamento Imobiliário");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (Exception e) {
            showError("Falha ao iniciar aplicação", e);
            Platform.exit();
        }
    }

    private static void handleException(Thread t, Throwable e) {
        Platform.runLater(() -> showError("Erro não tratado", e));
    }

    private static void showError(String title, Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(e.getMessage());
        alert.setContentText(e.getClass().getSimpleName() + ": " + e.getCause());
        alert.showAndWait();
        e.printStackTrace();
    }

    public static void main(String[] args) {
        launch(args);
    }
}