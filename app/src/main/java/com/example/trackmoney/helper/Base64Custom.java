package com.example.trackmoney.helper;

import android.util.Base64;

public class Base64Custom {
    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
        //o replace é pra evitar caracteres inválidos, \\n e \\r são caracteres inválidos
    }
    public static String decodificarBase64(String textoCodificado){
        return Base64.decode(textoCodificado, Base64.DEFAULT).toString();
    }
}
