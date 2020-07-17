package com.example.trackmoney.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trackmoney.R;
import com.example.trackmoney.config.ConfigFirebase;
import com.example.trackmoney.helper.Base64Custom;
import com.example.trackmoney.helper.DateCustom;
import com.example.trackmoney.model.Movimentacao;
import com.example.trackmoney.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasAcitivity extends AppCompatActivity {
    private EditText campoData, campoCategoria, campoDescricao, campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticação = ConfigFirebase.getFirebaseAutenticacao();
    private double despesaTotal;
    private double despesaGerada;
    private double despesaAtualizada; //junta as duas
    //despesa gerada é a despesa momentane

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas_acitivity);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoData.setText(DateCustom.dataAtual());
        recuperarDespesaTotal();
    }
    public void salvarDespesa(View view){
        if(validarDespesas()){
            movimentacao = new Movimentacao();
            movimentacao.setData(campoData.getText().toString());
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setValor(Double.parseDouble(campoValor.getText().toString()));
            movimentacao.setTipo("d");
            //d de despesa

            despesaGerada = Double.parseDouble(campoValor.getText().toString());
            despesaAtualizada = despesaTotal + despesaGerada;
            atualizarDespesa(despesaAtualizada);
            movimentacao.salvar(campoData.getText().toString());
            finish();
        }
    }
    public boolean validarDespesas(){
        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
                if ( !textoCategoria.isEmpty() ){

                    return true;
                }else {
                    Toast.makeText(DespesasAcitivity.this,
                            "Preencha um valor!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(DespesasAcitivity.this,
                        "Preencha uma data válida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(DespesasAcitivity.this,
                    "Preencha um valor válido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //método pra recuperar despesa
    public void recuperarDespesaTotal(){
        String emailUsuario = autenticação.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                //passando uma classe pro getvalue ele converte um valor pro tipo class que foi passado
                despesaTotal = usuario.getDespesaTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void atualizarDespesa(Double despesa){
        String emailUsuario = autenticação.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesa);
    }
}