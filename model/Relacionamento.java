package br.com.raizes.model;

public class Relacionamento {

    private Long id;
    private Pessoa pessoaOrigem;
    private Pessoa pessoaDestino;
    private TipoRelacionamento tipo;


    // =========================================
    // CONSTRUTOR VAZIO
    // =========================================

    public Relacionamento() {
    }


    // =========================================
    // CONSTRUTOR COMPLETO
    // =========================================

    public Relacionamento(Long id,
                          Pessoa pessoaOrigem,
                          Pessoa pessoaDestino,
                          TipoRelacionamento tipo) {

        this.id = id;
        this.pessoaOrigem = pessoaOrigem;
        this.pessoaDestino = pessoaDestino;
        this.tipo = tipo;
    }


    // =========================================
    // GETTERS E SETTERS
    // =========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Pessoa getPessoaOrigem() {
        return pessoaOrigem;
    }

    public void setPessoaOrigem(Pessoa pessoaOrigem) {
        this.pessoaOrigem = pessoaOrigem;
    }


    public Pessoa getPessoaDestino() {
        return pessoaDestino;
    }

    public void setPessoaDestino(Pessoa pessoaDestino) {
        this.pessoaDestino = pessoaDestino;
    }


    public TipoRelacionamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoRelacionamento tipo) {
        this.tipo = tipo;
    }


    // =========================================
    // TOSTRING
    // =========================================

    @Override
    public String toString() {
        return "Relacionamento{" +
                "id=" + id +
                ", pessoaOrigem=" + pessoaOrigem.getNome() +
                ", pessoaDestino=" + pessoaDestino.getNome() +
                ", tipo=" + tipo +
                '}';
    }
}