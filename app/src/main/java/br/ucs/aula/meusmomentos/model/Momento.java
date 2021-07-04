package br.ucs.aula.meusmomentos.model;

import java.io.Serializable;

public class Momento implements Serializable {
    private int codigo;
    private String descricao;
    private String data;
    private String local;
    private String caminho;

    public Momento() { }

    public Momento(String descricao, String data, String local, String caminho) {
        super();
        this.descricao = descricao;
        this.data = data;
        this.local = local;
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

    public  String getLocal() { return local;}

    public void setLocal(String local) { this.local = local; }

    public String getCaminho() { return caminho; }

    public void setCaminho(String caminho) { this.caminho = caminho; }
}
