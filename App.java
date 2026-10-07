package br.com.raizes;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class App extends Application {

    private BorderPane raiz;

    @Override
    public void start(Stage stage) throws Exception {

        raiz = new BorderPane();
        raiz.setTop(criarMenu());

        carregarTela("/br/com/raizes/view/arvore.fxml");

        Scene scene = new Scene(raiz, 1000, 650);

        stage.setTitle("Raízes e Memórias");
        stage.setScene(scene);
        stage.show();
    }


    // =========================================
    // MENU DE NAVEGAÇÃO
    // =========================================

    private HBox criarMenu() {

        Button btnArvore = new Button("Árvore");
        btnArvore.setOnAction(e -> carregarTela("/br/com/raizes/view/arvore.fxml"));

        Button btnPessoa = new Button("Pessoa");
        btnPessoa.setOnAction(e -> carregarTela("/br/com/raizes/view/pessoa.fxml"));

        Button btnRelacionamento = new Button("Relacionamento");
        btnRelacionamento.setOnAction(e -> carregarTela("/br/com/raizes/view/relacionamento.fxml"));

        HBox menu = new HBox(10, btnArvore, btnPessoa, btnRelacionamento);
        menu.setPadding(new Insets(10));

        return menu;
    }


    // =========================================
    // CARREGAR TELA NO CENTRO
    // =========================================

    private void carregarTela(String caminhoFxml) {

        try {

            FXMLLoader loader = new FXMLLoader(App.class.getResource(caminhoFxml));
            Parent tela = loader.load();

            raiz.setCenter(tela);

        } catch (IOException e) {

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao carregar tela: " + e.getMessage());
            alert.showAndWait();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}