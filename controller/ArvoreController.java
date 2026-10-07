package br.com.raizes.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.raizes.model.Pessoa;
import br.com.raizes.model.Relacionamento;
import br.com.raizes.model.TipoRelacionamento;
import br.com.raizes.service.PessoaService;
import br.com.raizes.service.RelacionamentoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

public class ArvoreController {

    // ==============================
    // PESSOA CENTRAL
    // ==============================

    @FXML private ComboBox<Pessoa> cmbPessoaCentral;
    @FXML private Label lblPessoaCentral;

    private final PessoaService pessoaService = new PessoaService();
    private final RelacionamentoService relacionamentoService = new RelacionamentoService();

    private ObservableList<Pessoa> pessoas = FXCollections.observableArrayList();


    // ==============================
    // BOTÕES - DESCENDENTES
    // ==============================

    @FXML
    private Button btnFilho;

    @FXML
    private Button btnNeto;

    @FXML
    private Button btnBisneto;


    // ==============================
    // BOTÕES - ASCENDENTES
    // ==============================

    @FXML
    private Button btnPai;

    @FXML
    private Button btnMae;

    @FXML
    private Button btnAvo;

    @FXML
    private Button btnAvoa;

    @FXML
    private Button btnBisavo;

    @FXML
    private Button btnBisavoa;


    // ==============================
    // BOTÕES - COLATERAIS
    // ==============================

    @FXML
    private Button btnIrmao;

    @FXML
    private Button btnIrma;

    @FXML
    private Button btnTio;

    @FXML
    private Button btnTia;

    @FXML
    private Button btnPrimo;

    @FXML
    private Button btnPrima;


    // ==============================
    // BOTÃO - CÔNJUGE
    // ==============================

    @FXML
    private Button btnConjuge;


    // ==============================
    // INICIALIZAÇÃO
    // ==============================

    @FXML
    public void initialize() {

        System.out.println("ArvoreController inicializado.");

        cmbPessoaCentral.setConverter(new StringConverter<Pessoa>() {

            @Override
            public String toString(Pessoa pessoa) {
                return pessoa == null ? "" : pessoa.getNome();
            }

            @Override
            public Pessoa fromString(String texto) {
                return null;
            }
        });

        cmbPessoaCentral.valueProperty().addListener((obs, antiga, nova) -> {
            lblPessoaCentral.setText(nova == null ? "" : nova.getNome().toUpperCase());
        });

        carregarPessoas();
    }


    // ==============================
    // CARREGAR PESSOAS NO COMBO CENTRAL
    // ==============================

    private void carregarPessoas() {

        try {

            List<Pessoa> lista = pessoaService.listarTodos();
            pessoas.setAll(lista);

            cmbPessoaCentral.setItems(pessoas);

        } catch (SQLException e) {
            mostrarErro("Erro ao carregar pessoas: " + e.getMessage());
        }
    }


    // ==============================
    // AÇÕES DOS BOTÕES - RELAÇÕES DIRETAS
    // ==============================

    @FXML
    private void adicionarFilho() {
        // A pessoa escolhida é FILHO(A) da pessoa central.
        criarRelacionamentoComEscolha(TipoRelacionamento.FILHO, false);
    }

    @FXML
    private void adicionarPai() {
        // A pessoa escolhida é PAI da pessoa central.
        criarRelacionamentoComEscolha(TipoRelacionamento.PAI, true);
    }

    @FXML
    private void adicionarMae() {
        // A pessoa escolhida é MÃE da pessoa central.
        criarRelacionamentoComEscolha(TipoRelacionamento.MAE, true);
    }

    @FXML
    private void adicionarAvo() {
        criarRelacionamentoComEscolha(TipoRelacionamento.AVO, true);
    }

    @FXML
    private void adicionarAvoa() {
        criarRelacionamentoComEscolha(TipoRelacionamento.AVA, true);
    }

    @FXML
    private void adicionarBisavo() {
        criarRelacionamentoComEscolha(TipoRelacionamento.BISAVO, true);
    }

    @FXML
    private void adicionarBisavoa() {
        criarRelacionamentoComEscolha(TipoRelacionamento.BISAVA, true);
    }

    @FXML
    private void adicionarIrmao() {
        criarRelacionamentoComEscolha(TipoRelacionamento.IRMAO, true);
    }

    @FXML
    private void adicionarIrma() {
        criarRelacionamentoComEscolha(TipoRelacionamento.IRMA, true);
    }

    @FXML
    private void adicionarConjuge() {
        criarRelacionamentoComEscolha(TipoRelacionamento.CONJUGE, false);
    }


    // ==============================
    // AÇÕES DOS BOTÕES - RELAÇÕES INDIRETAS (AINDA NÃO SUPORTADAS)
    // ==============================
    // Neto, Bisneto, Tio, Tia, Primo e Prima não existem como tipo direto
    // na tabela relacionamento (CHECK ck_relacionamento_tipo). Elas
    // dependem de uma lógica de parentesco calculada a partir da cadeia
    // de relacionamentos diretos (ex: Tio = Irmão do Pai/Mãe) — a
    // implementar futuramente no MotorGenealogico.

    @FXML
    private void adicionarNeto() {
        mostrarAviso("Relação \"Neto\" ainda não implementada (depende de cálculo indireto de parentesco).");
    }

    @FXML
    private void adicionarBisneto() {
        mostrarAviso("Relação \"Bisneto\" ainda não implementada (depende de cálculo indireto de parentesco).");
    }

    @FXML
    private void adicionarTio() {
        mostrarAviso("Relação \"Tio\" ainda não implementada (depende de cálculo indireto de parentesco).");
    }

    @FXML
    private void adicionarTia() {
        mostrarAviso("Relação \"Tia\" ainda não implementada (depende de cálculo indireto de parentesco).");
    }

    @FXML
    private void adicionarPrimo() {
        mostrarAviso("Relação \"Primo\" ainda não implementada (depende de cálculo indireto de parentesco).");
    }

    @FXML
    private void adicionarPrima() {
        mostrarAviso("Relação \"Prima\" ainda não implementada (depende de cálculo indireto de parentesco).");
    }


    // ==============================
    // LÓGICA COMUM DE CRIAÇÃO DE RELACIONAMENTO
    // ==============================

    /**
     * Abre uma seleção de pessoa e cria o relacionamento com a pessoa
     * central conforme o tipo informado.
     *
     * @param tipo             tipo do relacionamento (enum da tabela)
     * @param escolhidaEhOrigem se true, a pessoa escolhida entra como
     *                          pessoa_origem e a central como pessoa_destino
     *                          (ex: "Pai" -> escolhida é PAI da central).
     *                          Se false, a central entra como origem e a
     *                          escolhida como destino
     *                          (ex: "Filho" -> escolhida é FILHO da central).
     */
    private void criarRelacionamentoComEscolha(TipoRelacionamento tipo, boolean escolhidaEhOrigem) {

        Pessoa central = cmbPessoaCentral.getValue();

        if (central == null) {
            mostrarErro("Selecione a pessoa central primeiro.");
            return;
        }

        Optional<Pessoa> escolhida = escolherPessoa(central);

        if (escolhida.isEmpty()) {
            return;
        }

        try {

            Relacionamento relacionamento = new Relacionamento();

            if (escolhidaEhOrigem) {
                relacionamento.setPessoaOrigem(escolhida.get());
                relacionamento.setPessoaDestino(central);
            } else {
                relacionamento.setPessoaOrigem(central);
                relacionamento.setPessoaDestino(escolhida.get());
            }

            relacionamento.setTipo(tipo);

            relacionamentoService.salvar(relacionamento);

            mostrarSucesso("Relacionamento adicionado com sucesso.");

        } catch (IllegalArgumentException | SQLException e) {
            mostrarErro(e.getMessage());
        }
    }


    // ==============================
    // DIÁLOGO DE SELEÇÃO DE PESSOA
    // ==============================

    private Optional<Pessoa> escolherPessoa(Pessoa central) {

        List<String> opcoes = pessoas.stream()
                .filter(p -> !p.getId().equals(central.getId()))
                .map(p -> p.getId() + " - " + p.getNome())
                .toList();

        if (opcoes.isEmpty()) {
            mostrarErro("Cadastre outra pessoa antes de criar o relacionamento.");
            return Optional.empty();
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcoes.get(0), opcoes);
        dialog.setTitle("Selecionar pessoa");
        dialog.setHeaderText(null);
        dialog.setContentText("Pessoa:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Long idEscolhido = Long.valueOf(resultado.get().split(" - ")[0]);

        return pessoas.stream()
                .filter(p -> p.getId().equals(idEscolhido))
                .findFirst();
    }


    // ==============================
    // ALERTAS
    // ==============================

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
