package br.com.raizes.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.raizes.model.Pessoa;
import br.com.raizes.repository.PessoaRepository;

public class PessoaService {

    private final PessoaRepository repository;

    // =========================================
    // CONSTRUTOR
    // =========================================

    public PessoaService() {
        repository = new PessoaRepository();
    }


    // =========================================
    // SALVAR
    // =========================================

    public void salvar(Pessoa pessoa) throws SQLException {

        validarPessoa(pessoa);

        repository.salvar(pessoa);
    }


    // =========================================
    // BUSCAR POR ID
    // =========================================

    public Pessoa buscarPorId(Long id) throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException(
                "O ID da pessoa não pode ser nulo."
            );
        }

        return repository.buscarPorId(id);
    }


    // =========================================
    // LISTAR TODOS
    // =========================================

    public List<Pessoa> listarTodos() throws SQLException {

        return repository.listarTodos();
    }


    // =========================================
    // ATUALIZAR
    // =========================================

    public void atualizar(Pessoa pessoa) throws SQLException {

        if (pessoa == null) {
            throw new IllegalArgumentException(
                "A pessoa não pode ser nula."
            );
        }

        if (pessoa.getId() == null) {
            throw new IllegalArgumentException(
                "O ID da pessoa não pode ser nulo."
            );
        }

        Pessoa existente = repository.buscarPorId(pessoa.getId());

        if (existente == null) {
            throw new IllegalArgumentException(
                "A pessoa informada não existe."
            );
        }

        validarPessoa(pessoa);

        repository.atualizar(pessoa);
    }


    // =========================================
    // EXCLUIR
    // =========================================

    public void excluir(Long id) throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException(
                "O ID da pessoa não pode ser nulo."
            );
        }

        Pessoa existente = repository.buscarPorId(id);

        if (existente == null) {
            throw new IllegalArgumentException(
                "A pessoa informada não existe."
            );
        }

        repository.excluir(id);
    }


    // =========================================
    // VALIDAR PESSOA
    // =========================================

    private void validarPessoa(Pessoa pessoa) {

        if (pessoa == null) {
            throw new IllegalArgumentException(
                "A pessoa não pode ser nula."
            );
        }

        if (pessoa.getNome() == null || pessoa.getNome().isBlank()) {
            throw new IllegalArgumentException(
                "O nome da pessoa é obrigatório."
            );
        }

        if (pessoa.getNome().length() > 150) {
            throw new IllegalArgumentException(
                "O nome não pode ultrapassar 150 caracteres."
            );
        }

        if (pessoa.getDataNascimento() != null
                && pessoa.getDataNascimento().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                "A data de nascimento não pode ser no futuro."
            );
        }

        if (pessoa.getSexo() != null
                && !pessoa.getSexo().equals("M")
                && !pessoa.getSexo().equals("F")) {

            throw new IllegalArgumentException(
                "O sexo deve ser 'M' ou 'F'."
            );
        }

        if (pessoa.getFoto() != null && pessoa.getFoto().length() > 255) {
            throw new IllegalArgumentException(
                "O caminho da foto não pode ultrapassar 255 caracteres."
            );
        }
    }
}