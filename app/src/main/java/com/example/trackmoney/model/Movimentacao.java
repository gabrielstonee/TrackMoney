package com.example.trackmoney.model;

import com.example.trackmoney.config.ConfigFirebase;
import com.example.trackmoney.helper.Base64Custom;
import com.example.trackmoney.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {
    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private Double valor;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Movimentacao() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Movimentacao(String data, String categoria, String descricao, String tipo, Double valor) {
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }

    public void salvar(String dataEscolhida) {
        //pra recuperar a autentificação do usuário:
        FirebaseAuth autenticacao = ConfigFirebase.getFirebaseAutenticacao();
        //codificando o email:
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference firebase = ConfigFirebase.getFirebaseDatabase();
        firebase.child("movimentacoes")
                .child(idUsuario)
                .child(DateCustom.dataEscolhida(dataEscolhida))
                .push()
                .setValue(this);
        //o push cria um id único
    }
}
