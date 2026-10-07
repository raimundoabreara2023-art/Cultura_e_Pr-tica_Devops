package br.com.raizes.service;

import java.util.ArrayList;
import java.util.List;

import br.com.raizes.model.Pessoa;
import br.com.raizes.model.Relacionamento;

public class ArvoreService {

    // =========================================
    // DADOS DA ÁRVORE
    // =========================================

    private final List<Pessoa> pessoas;
    private final List<Relacionamento> relacionamentos;


    // =========================================
    // CONSTRUTOR
    // =========================================

    public ArvoreService() {

        pessoas = new ArrayList<>();
        relacionamentos = new ArrayList<>();
    }


    // =========================================
    // ADICIONAR PESSOA
    // =========================================

    public void adicionarPessoa(Pessoa pessoa) {

        if (pessoa == null) {
            throw new IllegalArgumentException(
                    "A pessoa não pode ser nula."
            );
        }

        pessoas.add(pessoa);
    }


    // =========================================
    // BUSCAR PESSOA PELO ID
    // =========================================

    public Pessoa buscarPessoa(Long id) {

        if (id == null) {
            return null;
        }

        for (Pessoa pessoa : pessoas) {

            if (id.equals(pessoa.getId())) {
                return pessoa;
            }
        }

        return null;
    }


    // =========================================
    // LISTAR PESSOAS
    // =========================================

    public List<Pessoa> listarPessoas() {

        return new ArrayList<>(pessoas);
    }


    // =========================================
    // ADICIONAR RELACIONAMENTO
    // =========================================

    public void adicionarRelacionamento(
            Relacionamento relacionamento) {

        if (relacionamento == null) {
            throw new IllegalArgumentException(
                    "O relacionamento não pode ser nulo."
            );
        }

        relacionamentos.add(relacionamento);
    }


    // =========================================
    // LISTAR RELACIONAMENTOS
    // =========================================

    public List<Relacionamento> listarRelacionamentos() {

        return new ArrayList<>(relacionamentos);
    }
}