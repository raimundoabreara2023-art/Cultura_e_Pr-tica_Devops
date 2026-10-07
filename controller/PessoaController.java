package br.com.raizes.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

import br.com.raizes.model.Pessoa;
import br.com.raizes.service.PessoaService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class PessoaController {

    @FXML private TextField txtNome;
    @FXML private DatePicker dtNascimento;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ListView<Pessoa> lstPessoas; // Alterado de String para Pessoa
    
    // Elementos visuais do dashboard moderno
    @FXML private Label lblTotalPessoas;
    @FXML private Label lblIdPessoa;
    @FXML private Label lblStatusCadastro;
    @FXML private ImageView imgFotoPreview;

    private final PessoaService service = new PessoaService();

    private ObservableList<Pessoa> pessoas = FXCollections.observableArrayList();
    private Pessoa pessoaSelecionada;
    private String caminhoFotoSelecionada;


    // =========================================
    // INICIALIZAÇÃO
    // =========================================

    @FXML
    public void initialize() {
        cmbSexo.setItems(FXCollections.observableArrayList("M", "F"));
        
        // Define a fábrica de células personalizada para mostrar a foto na lista
        lstPessoas.setCellFactory(param -> new PessoaCell());
        
        carregarPessoas();
    }


    // =========================================
    // CARREGAR LISTA DE PESSOAS
    // =========================================

    private void carregarPessoas() {
        try {
            List<Pessoa> lista = service.listarTodos();
            pessoas.setAll(lista);

            // Passa diretamente a lista de objetos Pessoa para o ListView
            lstPessoas.setItems(pessoas);

            if (lblTotalPessoas != null) {
                lblTotalPessoas.setText(String.valueOf(lista.size()));
            }

        } catch (SQLException e) {
            mostrarErro("Erro ao carregar pessoas: " + e.getMessage());
        }
    }


    // =========================================
    // SALVAR (INSERIR OU ATUALIZAR)
    // =========================================

    @FXML
    private void salvarPessoa() {
        try {
            boolean isAtualizacao = (pessoaSelecionada != null && pessoaSelecionada.getId() != null);

            Pessoa pessoa = isAtualizacao
                    ? pessoaSelecionada
                    : new Pessoa();

            pessoa.setNome(txtNome.getText());
            pessoa.setDataNascimento(dtNascimento.getValue());
            pessoa.setSexo(cmbSexo.getValue());
            pessoa.setFoto(caminhoFotoSelecionada);

            if (!isAtualizacao) {
                service.salvar(pessoa);
                System.out.println("DEBUG: Salvou com sucesso no banco!");
                mostrarSucesso("Sucesso", "Pessoa cadastrada com êxito!");
            } else {
                service.atualizar(pessoa);
                System.out.println("DEBUG: Atualizou com sucesso no banco!");
                mostrarSucesso("Sucesso", "Dados atualizados com êxito!");
            }

            limparCampos();
            carregarPessoas();

        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("DEBUG: Caiu no erro: " + e.getMessage());
            mostrarErro(e.getMessage());
        }
    }


    // =========================================
    // EXCLUIR
    // =========================================

    @FXML
    private void excluirPessoa() {
        if (pessoaSelecionada == null) {
            mostrarErro("Selecione uma pessoa na lista para excluir.");
            return;
        }

        try {
            service.excluir(pessoaSelecionada.getId());
            mostrarSucesso("Sucesso", "Pessoa excluída com êxito!");

            limparCampos();
            carregarPessoas();

        } catch (IllegalArgumentException | SQLException e) {
            mostrarErro(e.getMessage());
        }
    }


    // =========================================
    // SELECIONAR PESSOA NA LISTA
    // =========================================

    @FXML
    private void selecionarPessoa() {
        Pessoa selecionada = lstPessoas.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            return;
        }

        pessoaSelecionada = selecionada;

        txtNome.setText(pessoaSelecionada.getNome());
        dtNascimento.setValue(pessoaSelecionada.getDataNascimento());
        cmbSexo.setValue(pessoaSelecionada.getSexo());
        
        caminhoFotoSelecionada = pessoaSelecionada.getFoto();

        if (lblIdPessoa != null) {
            lblIdPessoa.setText("ID: " + pessoaSelecionada.getId());
        }
        if (lblStatusCadastro != null) {
            lblStatusCadastro.setText("● Pessoa cadastrada");
        }

        carregarPreviewFoto(caminhoFotoSelecionada);
    }


    // =========================================
    // SELECIONAR FOTO
    // =========================================

    @FXML
    private void selecionarFoto() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Imagens", "*.png", "*.jpg", "*.jpeg"
            )
        );

        File arquivo = fileChooser.showOpenDialog(txtNome.getScene().getWindow());

        if (arquivo != null) {
            caminhoFotoSelecionada = arquivo.getAbsolutePath();
            carregarPreviewFoto(caminhoFotoSelecionada);
        }
    }

    private void carregarPreviewFoto(String caminho) {
        if (imgFotoPreview == null) return;

        if (caminho != null && !caminho.trim().isEmpty()) {
            try {
                File file = new File(caminho);
                if (file.exists()) {
                    Image imagem = new Image(file.toURI().toURL().toString(), 80, 80, true, true);
                    imgFotoPreview.setImage(imagem);
                    return;
                }
            } catch (MalformedURLException e) {
                // Ignora se houver erro na conversão do URL
            }
        }
        imgFotoPreview.setImage(null);
    }


    // =========================================
    // LIMPAR CAMPOS
    // =========================================

    @FXML
    private void limparCampos() {
        pessoaSelecionada = null;
        caminhoFotoSelecionada = null;

        txtNome.clear();
        dtNascimento.setValue(null);
        cmbSexo.setValue(null);

        if (imgFotoPreview != null) {
            imgFotoPreview.setImage(null);
        }
        if (lblIdPessoa != null) {
            lblIdPessoa.setText("ID: --");
        }
        if (lblStatusCadastro != null) {
            lblStatusCadastro.setText("● Novo cadastro");
        }

        lstPessoas.getSelectionModel().clearSelection();
    }


    // =========================================
    // EXIBIR MENSAGENS (SUCESSO E ERRO)
    // =========================================

    private void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}


// =====================================================================
// CLASSE AUXILIAR PARA RENDERIZAR A FOTO REDONDA (SEM ESTICAR) E O NOME
// =====================================================================

class PessoaCell extends ListCell<Pessoa> {
    @Override
    protected void updateItem(Pessoa pessoa, boolean vazio) {
        super.updateItem(pessoa, vazio);

        if (vazio || pessoa == null) {
            setText(null);
            setGraphic(null);
        } else {
            javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            // Container para garantir que a foto não distorce
            javafx.scene.layout.StackPane avatarContainer = new javafx.scene.layout.StackPane();
            avatarContainer.setPrefSize(32, 32);
            avatarContainer.setMinSize(32, 32);
            avatarContainer.setMaxSize(32, 32);

            ImageView imgMiniatura = new ImageView();
            imgMiniatura.setPreserveRatio(true);

            if (pessoa.getFoto() != null && !pessoa.getFoto().trim().isEmpty()) {
                try {
                    File file = new File(pessoa.getFoto());
                    if (file.exists()) {
                        Image imagem = new Image(file.toURI().toURL().toString(), 32, 32, true, true);
                        imgMiniatura.setImage(imagem);
                    }
                } catch (Exception e) {
                    // Ignora erro se falhar ao carregar
                }
            }

            // Aplica o recorte circular perfeito na moldura de 32x32 (raio 16)
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(16, 16, 16);
            avatarContainer.setClip(clip);
            avatarContainer.getChildren().add(imgMiniatura);

            // Estilo opcional para fundo caso a imagem seja transparente
            avatarContainer.setStyle("-fx-background-color: #e2e8f0;");

            Label lblNome = new Label(pessoa.getId() + " - " + pessoa.getNome());
            lblNome.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");

            hbox.getChildren().addAll(avatarContainer, lblNome);
            setGraphic(hbox);
            setText(null);
        }
    }
}