package br.ucs.aula.meusmomentos.model;

import java.io.Serializable;

public class Momento implements Serializable {
    private int codigo;
    private String descricao;
    private String data;
    private String localizacao;
    private String caminho;

    public Momento() { }

    public Momento(String descricao, String data, String localizacao, String caminho) {
        super();
        this.descricao = descricao;
        this.data = data;
        this.localizacao = localizacao;
        this.caminho = caminho;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }

    public  String getLocalizacao() { return localizacao;}

    public void setLocalizacao(String local) { this.localizacao = localizacao; }

    public String getCaminho() { return caminho; }

    public void setCaminho(String caminho) { this.caminho = caminho; }
}
