package br.com.raizes.controller;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.raizes.dto.NucleoFamiliarDTO;
import br.com.raizes.dto.PessoaCheckItem;
import br.com.raizes.model.Pessoa;
import br.com.raizes.model.Relacionamento;
import br.com.raizes.model.TipoRelacionamento;
import br.com.raizes.service.PessoaService;
import br.com.raizes.service.RelacionamentoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

public class RelacionamentoController {

    @FXML private ComboBox<Pessoa> cmbPai;
    @FXML private ComboBox<Pessoa> cmbMae;
    @FXML private MenuButton btnMenuFilhos; 
    @FXML private TextField txtBuscaFamilia;
    @FXML private VBox listaFamiliasContainer;
    @FXML private VBox containerFilhosAdicionados;
    
    @FXML private Label lblTotalFamilias;
    @FXML private Label lblIdFamilia; 
    @FXML private Label lblStatusFamilia;

    // Elementos da Preview Direita
    @FXML private Pane avatarPaiPreview;
    @FXML private Pane avatarMaePreview;
    @FXML private Label lblPreviewNomePai;
    @FXML private Label lblPreviewNomeMae;
    @FXML private Label lblPreviewTotalFilhos;
    @FXML private HBox grelhaFilhosPreview;
    @FXML private Region linhaHorizontalConector;

    private final RelacionamentoService service = new RelacionamentoService();
    private final PessoaService pessoaService = new PessoaService();

    private List<NucleoFamiliarDTO> nucleosFamiliaresCache = new ArrayList<>();
    private ObservableList<PessoaCheckItem> listaFilhosCheck = FXCollections.observableArrayList();

    private List<Pessoa> cacheTodasPessoas = new ArrayList<>();
    private List<Relacionamento> cacheTodosRelacionamentos = new ArrayList<>();

    // Variável para rastrear o ID da família selecionada para edição/exclusão
    private Long familiaSelecionadaId = null;
    
    // Flag para evitar que os filtros limpem os filhos ao carregar uma família da lista
    private boolean carregandoDadosFamilia = false;

    @FXML
    public void initialize() {
        configurarConversorPessoa(cmbPai);
        configurarConversorPessoa(cmbMae);

        cmbPai.valueProperty().addListener((obs, oldVal, newVal) -> {
            atualizarPreview();
        });

        cmbMae.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!carregandoDadosFamilia) {
                filtrarEAtualizarFilhos();
            }
            atualizarPreview();
        });

        txtBuscaFamilia.textProperty().addListener((obs, oldVal, newVal) -> {
            renderizarListaFamilias(newVal);
        });

        carregarDadosIniciais();
        carregarRelacionamentos();
    }

    private void carregarDadosIniciais() {
        new Thread(() -> {
            try {
                cacheTodasPessoas = pessoaService.listarTodos();
                cacheTodosRelacionamentos = service.listarTodos();

                javafx.application.Platform.runLater(() -> {
                    ObservableList<Pessoa> pais = FXCollections.observableArrayList();
                    ObservableList<Pessoa> maes = FXCollections.observableArrayList();

                    for (Pessoa p : cacheTodasPessoas) {
                        String sexo = p.getSexo() != null ? p.getSexo().toUpperCase() : "";
                        if (sexo.startsWith("M") || sexo.equals("MASCULINO")) {
                            pais.add(p);
                        } else if (sexo.startsWith("F") || sexo.equals("FEMININO")) {
                            maes.add(p);
                        }
                    }
                    cmbPai.setItems(pais);
                    cmbMae.setItems(maes);

                    filtrarEAtualizarFilhos();
                });

            } catch (SQLException e) {
                javafx.application.Platform.runLater(() -> 
                    mostrarErro("Erro ao carregar dados: " + e.getMessage())
                );
            }
        }).start();
    }

    private void carregarRelacionamentos() {
        new Thread(() -> {
            try {
                List<NucleoFamiliarDTO> lista = service.listarNucleosFamiliares();
                javafx.application.Platform.runLater(() -> {
                    nucleosFamiliaresCache = lista;
                    lblTotalFamilias.setText(String.valueOf(lista.size()));
                    renderizarListaFamilias(txtBuscaFamilia.getText());
                });
            } catch (SQLException e) {
                javafx.application.Platform.runLater(() -> 
                    mostrarErro("Erro ao carregar núcleos familiares: " + e.getMessage())
                );
            }
        }).start();
    }

    private void renderizarListaFamilias(String filtro) {
        listaFamiliasContainer.getChildren().clear();

        String termo = filtro != null ? filtro.toLowerCase().trim() : "";

        for (NucleoFamiliarDTO dto : nucleosFamiliaresCache) {
            if (!termo.isEmpty() && !dto.getCasal().toLowerCase().contains(termo)) {
                continue;
            }

            HBox card = new HBox(10);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("card-familia-item");

            VBox info = new VBox(2);
            Label lblCasal = new Label(dto.getCasal());
            lblCasal.getStyleClass().add("item-familia-titulo");
            
            int totalFilhos = dto.getTotalFilhos(); 
            Label lblFilhosCount = new Label(totalFilhos + (totalFilhos == 1 ? " filho" : " filhos"));
            lblFilhosCount.getStyleClass().add("item-familia-sub");

            info.getChildren().addAll(lblCasal, lblFilhosCount);
            card.getChildren().add(info);

            // Ao clicar, preenche o formulário utilizando os dados do DTO
            card.setOnMouseClicked(event -> selecionarFamiliaNaLista(dto));

            listaFamiliasContainer.getChildren().add(card);
        }
    }

    private void selecionarFamiliaNaLista(NucleoFamiliarDTO dto) {
        carregandoDadosFamilia = true;
        familiaSelecionadaId = dto.getId();
        
        lblIdFamilia.setText("ID: " + dto.getId());
        lblStatusFamilia.setText("• Família cadastrada");
        lblStatusFamilia.setStyle("-fx-text-fill: #16a34a;");

        // 1. Seleciona o Pai no ComboBox pelo ID (protegido contra null)
        if (dto.getPaiId() != null) {
            cmbPai.getItems().stream()
                .filter(p -> p.getId().equals(dto.getPaiId()))
                .findFirst()
                .ifPresentOrElse(cmbPai::setValue, () -> cmbPai.setValue(null));
        } else {
            cmbPai.setValue(null);
        }

        // 2. Seleciona a Mãe no ComboBox pelo ID (protegido contra null)
        if (dto.getMaeId() != null) {
            cmbMae.getItems().stream()
                .filter(m -> m.getId().equals(dto.getMaeId()))
                .findFirst()
                .ifPresentOrElse(cmbMae::setValue, () -> cmbMae.setValue(null));
        } else {
            cmbMae.setValue(null);
        }

        // 3. Identifica os IDs dos filhos deste núcleo através dos relacionamentos em cache
        Set<Long> idsFilhosDoNucleo = new HashSet<>();
        if (dto.getPaiId() != null) {
            for (Relacionamento r : cacheTodosRelacionamentos) {
                if (r.getPessoaOrigem() != null && r.getPessoaOrigem().getId().equals(dto.getPaiId())
                        && r.getTipo() == TipoRelacionamento.PAI && r.getPessoaDestino() != null) {
                    idsFilhosDoNucleo.add(r.getPessoaDestino().getId());
                }
            }
        }
        if (dto.getMaeId() != null) {
            for (Relacionamento r : cacheTodosRelacionamentos) {
                if (r.getPessoaOrigem() != null && r.getPessoaOrigem().getId().equals(dto.getMaeId())
                        && r.getTipo() == TipoRelacionamento.MAE && r.getPessoaDestino() != null) {
                    idsFilhosDoNucleo.add(r.getPessoaDestino().getId());
                }
            }
        }

        // 4. Reconstrói a lista completa de filhos marcando apenas os que pertencem a este núcleo
        ObservableList<PessoaCheckItem> filhosItems = FXCollections.observableArrayList();
        for (Pessoa p : cacheTodasPessoas) {
            boolean deveMarcar = idsFilhosDoNucleo.contains(p.getId());
            PessoaCheckItem newItem = new PessoaCheckItem(p);
            newItem.setSelecionado(deveMarcar);
            filhosItems.add(newItem);
        }

        listaFilhosCheck = filhosItems;

        reconstruirMenuFilhos();
        atualizarListaFilhosAdicionadosVisual();
        atualizarPreview();
        
        carregandoDadosFamilia = false;
    }

    private void filtrarEAtualizarFilhos() {
        if (carregandoDadosFamilia) return;

        Pessoa maeSelecionada = cmbMae.getValue();

        Set<Long> filhosComMaeIds = new HashSet<>();
        for (Relacionamento r : cacheTodosRelacionamentos) {
            if (r.getTipo() == TipoRelacionamento.MAE && r.getPessoaDestino() != null) {
                filhosComMaeIds.add(r.getPessoaDestino().getId());
            }
        }

        ObservableList<PessoaCheckItem> filhosItems = FXCollections.observableArrayList();

        for (Pessoa p : cacheTodasPessoas) {
            boolean jaSelecionadoNaEdicao = listaFilhosCheck.stream()
                    .anyMatch(item -> item.getPessoa().getId().equals(p.getId()) && item.isSelecionado());

            if (filhosComMaeIds.contains(p.getId()) && !jaSelecionadoNaEdicao) {
                continue;
            }

            if (maeSelecionada != null && maeSelecionada.getDataNascimento() != null && p.getDataNascimento() != null) {
                LocalDate limiteMinimo = maeSelecionada.getDataNascimento().plusYears(10);
                if (p.getDataNascimento().isBefore(limiteMinimo) && !jaSelecionadoNaEdicao) {
                    continue; 
                }
            }

            boolean estadoAnterior = listaFilhosCheck.stream()
                    .anyMatch(item -> item.getPessoa().getId().equals(p.getId()) && item.isSelecionado());

            PessoaCheckItem newItem = new PessoaCheckItem(p);
            newItem.setSelecionado(estadoAnterior);
            filhosItems.add(newItem);
        }

        listaFilhosCheck = filhosItems;
        reconstruirMenuFilhos();
        atualizarListaFilhosAdicionadosVisual();
    }

    private void reconstruirMenuFilhos() {
        btnMenuFilhos.getItems().clear();

        for (PessoaCheckItem item : listaFilhosCheck) {
            CheckBox checkBox = new CheckBox(item.getPessoa().getNome());
            checkBox.setSelected(item.isSelecionado());
            
            // Força a cor do texto para escuro para garantir visibilidade no menu
            checkBox.setStyle("-fx-text-fill: #1e293b; -fx-cursor: hand;");

            checkBox.setOnAction(event -> {
                item.setSelecionado(checkBox.isSelected());
                atualizarTextoMenuFilhos();
                atualizarListaFilhosAdicionadosVisual();
                atualizarPreview();
            });

            CustomMenuItem menuItem = new CustomMenuItem(checkBox, false);
            menuItem.setHideOnClick(false); 
            btnMenuFilhos.getItems().add(menuItem);
        }

        atualizarTextoMenuFilhos();
    }

    private void atualizarTextoMenuFilhos() {
        long count = listaFilhosCheck.stream().filter(PessoaCheckItem::isSelecionado).count();
        if (count == 0) {
            btnMenuFilhos.setText("Pesquisar e adicionar filho...");
        } else {
            btnMenuFilhos.setText(count + " filho(s) selecionado(s)");
        }
    }

    private void atualizarListaFilhosAdicionadosVisual() {
        containerFilhosAdicionados.getChildren().clear();

        for (PessoaCheckItem item : listaFilhosCheck) {
            if (item.isSelecionado()) {
                HBox linha = new HBox(8);
                linha.setAlignment(Pos.CENTER_LEFT);
                linha.getStyleClass().add("filho-tag-item");

                Label lblNome = new Label(item.getPessoa().getNome());
                lblNome.getStyleClass().add("filho-tag-nome");
                HBox.setHgrow(lblNome, Priority.ALWAYS);

                Button btnRemover = new Button("🗑");
                btnRemover.getStyleClass().add("button-remover-tag");
                btnRemover.setOnAction(e -> {
                    item.setSelecionado(false);
                    reconstruirMenuFilhos();
                    atualizarListaFilhosAdicionadosVisual();
                    atualizarPreview();
                });

                linha.getChildren().addAll(lblNome, btnRemover);
                containerFilhosAdicionados.getChildren().add(linha);
            }
        }
    }

    private void atualizarPreview() {
        Pessoa pai = cmbPai.getValue();
        Pessoa mae = cmbMae.getValue();

        lblPreviewNomePai.setText(pai != null ? pai.getNome() : "Pai");
        lblPreviewNomeMae.setText(mae != null ? mae.getNome() : "Mãe");

        aplicarFotoAvatar(avatarPaiPreview, pai != null ? pai.getFoto() : null);
        aplicarFotoAvatar(avatarMaePreview, mae != null ? mae.getFoto() : null);

        long totalFilhos = listaFilhosCheck.stream().filter(PessoaCheckItem::isSelecionado).count();
        lblPreviewTotalFilhos.setText(totalFilhos + (totalFilhos == 1 ? " filho" : " filhos"));

        grelhaFilhosPreview.getChildren().clear();
        int maxExibir = 8;
        int count = 0;
        
        for (PessoaCheckItem item : listaFilhosCheck) {
            if (item.isSelecionado()) {
                if (count < maxExibir) {
                    // 1. Cria a linhazinha vertical que ramifica da barra principal para o filho
                    Region linhaFilho = new Region();
                    linhaFilho.getStyleClass().add("linha-vertical-filho");

                    // 2. Cria a miniatura do avatar
                    Pane miniatura = new Pane();
                    miniatura.getStyleClass().add("avatar-preview-pequeno");
                    aplicarFotoAvatar(miniatura, item.getPessoa().getFoto());

                    // 3. Agrupa a linha e o avatar em um VBox vertical integrado
                    VBox containerFilho = new VBox(linhaFilho, miniatura);
                    containerFilho.getStyleClass().add("item-filho-preview");

                    grelhaFilhosPreview.getChildren().add(containerFilho);
                }
                count++;
            }
        }
        
        if (count > maxExibir) {
            Label mais = new Label("+" + (count - maxExibir));
            mais.getStyleClass().add("badge-mais-filhos");
            
            // Alinha o badge visualmente junto aos itens com ramificação
            VBox containerMais = new VBox(new Region() {{ setPrefHeight(10); }}, mais);
            containerMais.setAlignment(Pos.CENTER);
            grelhaFilhosPreview.getChildren().add(containerMais);
        }

        // ==========================================
        // LÓGICA DINÂMICA DA BARRA HORIZONTAL (CONECTOR)
        // ==========================================
        if (linhaHorizontalConector != null && count > 0) {
            int filhosNoPreview = Math.min(count, maxExibir);
            // Aumente o fator de multiplicação para esticar a barra e ocupar mais espaço lateral
            double larguraDinamica = Math.max(75.0, filhosNoPreview * 85.0); 
            linhaHorizontalConector.setPrefWidth(larguraDinamica);
            linhaHorizontalConector.setMaxWidth(larguraDinamica);
        }
    }

    private void aplicarFotoAvatar(Pane pane, String fotoPath) {
        pane.getChildren().clear();
        pane.setBackground(null);
        pane.setClip(null);

        // Define a máscara circular perfeita baseada no tamanho atual do componente
        Circle clip = new Circle();
        clip.centerXProperty().bind(pane.widthProperty().divide(2));
        clip.centerYProperty().bind(pane.heightProperty().divide(2));
        clip.radiusProperty().bind(javafx.beans.binding.Bindings.min(
            pane.widthProperty().divide(2), 
            pane.heightProperty().divide(2)
        ));
        pane.setClip(clip);

        if (fotoPath != null && !fotoPath.trim().isEmpty()) {
            File file = new File(fotoPath);
            if (file.exists()) {
                try {
                    Image image = new Image(file.toURI().toString(), false);
                    ImageView imageView = new ImageView(image);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);

                    // Vincula o tamanho do ImageView dinamicamente ao Pane para preencher perfeitamente sem distorcer
                    imageView.fitWidthProperty().bind(pane.widthProperty());
                    imageView.fitHeightProperty().bind(pane.heightProperty());

                    // Garante centralização elegante por StackPane interno
                    StackPane containerImg = new StackPane(imageView);
                    containerImg.prefWidthProperty().bind(pane.widthProperty());
                    containerImg.prefHeightProperty().bind(pane.heightProperty());

                    pane.getChildren().add(containerImg);
                    return;
                } catch (Exception ignored) {}
            }
        }
    }

    private void configurarConversorPessoa(ComboBox<Pessoa> combo) {
        combo.setConverter(new StringConverter<Pessoa>() {
            @Override
            public String toString(Pessoa pessoa) {
                return pessoa == null ? "" : pessoa.getNome();
            }

            @Override
            public Pessoa fromString(String texto) {
                return null;
            }
        });
    }

    @FXML
    private void removerPai() {
        cmbPai.setValue(null);
    }

    @FXML
    private void removerMae() {
        cmbMae.setValue(null);
    }

    @FXML
    private void salvarRelacionamento() {
        try {
            Pessoa pai = cmbPai.getValue();
            Pessoa mae = cmbMae.getValue();

            List<Pessoa> filhosSelecionados = new ArrayList<>();
            for (PessoaCheckItem item : listaFilhosCheck) {
                if (item.isSelecionado()) {
                    filhosSelecionados.add(item.getPessoa());
                }
            }

            if (pai == null && mae == null) {
                mostrarErro("Selecione pelo menos o Pai ou a Mãe.");
                return;
            }

            service.salvarNucleoFamiliar(pai, mae, filhosSelecionados);

            limparCampos();
            carregarRelacionamentos();
            carregarDadosIniciais();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Núcleo familiar gravado com sucesso!");
            alert.showAndWait();

        } catch (IllegalArgumentException | SQLException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void excluirRelacionamento() {
        if (familiaSelecionadaId == null) {
            mostrarErro("Nenhum núcleo familiar selecionado para exclusão.");
            return;
        }
    }

    @FXML
    private void limparCampos() {
        familiaSelecionadaId = null;
        lblIdFamilia.setText("ID: --");
        lblStatusFamilia.setText("• Família nova");
        lblStatusFamilia.setStyle("-fx-text-fill: #64748b;");

        cmbPai.setValue(null);
        cmbMae.setValue(null);

        for (PessoaCheckItem item : listaFilhosCheck) {
            item.setSelecionado(false);
        }

        filtrarEAtualizarFilhos();
        atualizarListaFilhosAdicionadosVisual();
        atualizarPreview();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}