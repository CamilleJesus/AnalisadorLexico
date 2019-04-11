package token;

import java.util.HashMap;


public class PalavrasReservadas {

    public static final HashMap PALAVRAS = new HashMap<>() {{
        put("programa", "PALAVRA_RESERVADA");
        put("constantes", "PALAVRA_RESERVADA");
        put("variaveis", "PALAVRA_RESERVADA");
        put("metodo", "PALAVRA_RESERVADA");
        put("resultado", "PALAVRA_RESERVADA");
        put("principal", "PALAVRA_RESERVADA");
        put("se", "PALAVRA_RESERVADA");
        put("entao", "PALAVRA_RESERVADA");
        put("senao", "PALAVRA_RESERVADA");
        put("enquanto", "PALAVRA_RESERVADA");
        put("leia", "PALAVRA_RESERVADA");
        put("escreva", "PALAVRA_RESERVADA");
        put("vazio", "PALAVRA_RESERVADA");
        put("inteiro", "PALAVRA_RESERVADA");
        put("real", "PALAVRA_RESERVADA");
        put("boleano", "PALAVRA_RESERVADA");
        put("texto", "PALAVRA_RESERVADA");
        put("verdadeiro", "PALAVRA_RESERVADA");
        put("falso", "PALAVRA_RESERVADA");
    }};

    public static boolean ehReservada(String palavra) {
        return PALAVRAS.containsKey(palavra);
    }
}
