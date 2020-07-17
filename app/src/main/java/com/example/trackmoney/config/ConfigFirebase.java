package com.example.trackmoney.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {
    private static FirebaseAuth autenticacao;
    //o de baixo retorna a instancia do FirebaseAuth
    private static DatabaseReference firebase;

    public static DatabaseReference getFirebaseDatabase(){
        if(firebase==null){
            //isso ele pergunta se temos ou n uma instancia
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return firebase;
    }
    public static FirebaseAuth getFirebaseAutenticacao(){
        //deixar esse metodo estático é bom porque por mais que eu instancie outras contas, o valor semre vai ser o mesmo
        if(autenticacao==null){
            //isso ele pergunta se temos ou n uma instancia
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
}
