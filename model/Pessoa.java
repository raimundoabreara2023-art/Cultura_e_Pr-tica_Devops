package br.com.raizes.model;

import java.time.LocalDate;

public class Pessoa {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String foto;


    // =========================================
    // CONSTRUTOR VAZIO
    // =========================================

    public Pessoa() {
    }


    // =========================================
    // CONSTRUTOR COMPLETO
    // =========================================

    public Pessoa(Long id, String nome, LocalDate dataNascimento,
                  String sexo, String foto) {

        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.foto = foto;
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


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }


    // =========================================
    // TOSTRING
    // =========================================

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}