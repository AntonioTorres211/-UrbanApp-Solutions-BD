package br.com.cidade_atende_mini.model;

import java.time.LocalDateTime;

public class Solicitacao {
    
    private Integer id;
    private String titulo;
    private String descricao;
    private String endereco;
    private Integer prioridade;
    private String status;
    private String notas;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechadura;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechadura() {
        return dataFechadura;
    }

    public void setDataFechadura(LocalDateTime dataFechadura) {
        this.dataFechadura = dataFechadura;
    }

    @Override
    public String toString() {
        return "Solicitacao{" +
                "titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", endereco='" + endereco + '\'' +
                ", prioridade=" + prioridade +
                ", status='" + status + '\'' +
                ", notas='" + notas + '\'' +
                ", id='" + id + '\'' +
                ", dataAbertura=" + dataAbertura +
                ", dataFechadura=" + dataFechadura +
                '}';
    }
}