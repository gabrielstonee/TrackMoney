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

public class ReceitasAcitivity extends AppCompatActivity {
    private EditText editValor, editData, editCategoria, editDescricao;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticação = ConfigFirebase.getFirebaseAutenticacao();
    private double receitaTotal;
    private double receitaGerada;
    private double receitaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);
        editValor = findViewById(R.id.editValor);
        editData = findViewById(R.id.editData);
        editCategoria = findViewById(R.id.editCategoria);
        editDescricao = findViewById(R.id.editDescricao);
        editData.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();
    }
    public void salvarReceita(View view){
        if(validarDespesas()){
            movimentacao = new Movimentacao();
            movimentacao.setData(editData.getText().toString());
            movimentacao.setCategoria(editCategoria.getText().toString());
            movimentacao.setDescricao(editDescricao.getText().toString());
            movimentacao.setValor(Double.parseDouble(editValor.getText().toString()));
            movimentacao.setTipo("r");
            //d de despesa

            receitaGerada = Double.parseDouble(editValor.getText().toString());
            receitaAtualizada = receitaTotal + receitaGerada;
            atualizarReceita(receitaAtualizada);
            movimentacao.salvar(editData.getText().toString());
            finish();
        }
    }
    public boolean validarDespesas(){
        String textoValor = editValor.getText().toString();
        String textoData = editData.getText().toString();
        String textoCategoria = editCategoria.getText().toString();
        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
                if ( !textoCategoria.isEmpty() ){

                    return true;
                }else {
                    Toast.makeText(ReceitasAcitivity.this,
                            "Preencha um valor!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else {
                Toast.makeText(ReceitasAcitivity.this,
                        "Preencha uma data válida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(ReceitasAcitivity.this,
                    "Preencha um valor válido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void recuperarReceitaTotal(){
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
                receitaTotal = usuario.getReceitaTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void atualizarReceita(Double receita){
        String emailUsuario = autenticação.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.child("receitaTotal").setValue(receita);
    }

}