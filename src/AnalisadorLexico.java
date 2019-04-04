import token.PalavrasReservadas;
import token.Token;

import java.util.Scanner;


public class AnalisadorLexico {

    public static void main(String[] args) {
        int linha = 0;
        String lexema, classe, erro;
        Scanner entrada = new Scanner(System.in);

        while (true) {
            System.out.println("Digite o lexema:");
            lexema = entrada.nextLine();
            classe = classificaLexema(lexema);

            if (!classe.equals("CLASSE_INVALIDA")) {
                Token token = new Token(classe, lexema, linha);
                linha++;
                System.out.println(token.toString()+"\n");
            } else {
                erro = classificaErro(lexema);
                System.out.println("Erro: " + erro + "\n");
            }

        }
    }

    public static String classificaLexema(String lexema) {

        if (lexema.matches("[a-zA-Z]+\\w*")) {

            if (PalavrasReservadas.ehReservada(lexema)) {
                return "PALAVRA_RESERVADA";
            }
            return "IDENTIFICADOR";
        } else if (lexema.matches("(-)?\\s*\\d+(\\.(\\d+))?")) {
            return "NUMERO";
        } else if (lexema.matches("(--)|-|(\\+\\+)|\\+|\\*|/")) {
            return "OPERADOR_ARITMETICO";
        } else if (lexema.matches("(<=)|<|(==)|=|(>=)|>|(!=)")) {
            return "OPERADOR_RELACIONAL";
        } else if (lexema.matches("!|(&&)|(\\|\\|)")) {
            return "OPERADOR_LOGICO";
        } else if (lexema.matches(";|,|\\(|\\)|[|]|\\{|}|\\.")) {
            return "DELIMITADOR";
        } else if (lexema.matches("//")) {   //Modificar para reconhecer até a quebra de linha.
            return "COMENTARIO_LINHA";
        } else if (lexema.matches("/\\*(.|\n)*\\*/")) {
            return "COMENTARIO_BLOCO";
        } else if (lexema.matches("\"((\\\\\")|[^\"]|\\n)*\"")) {
            return "CADEIA_CARACTERES";
        }
        return "CLASSE_INVALIDA";
    }

    public static String classificaErro(String lexema) {

        if (lexema.matches("/\\*(.|\n)*")) {
            return "COMENTARIO DE BLOCO_ABERTO";
        } else if (lexema.matches("\"((\\\\\")|[^\"]|\\n)*")) {
            return "CADEIA DE CARACTERES ABERTA";
        }
        return "VALOR INESPERADO";   //Tentar fazer as regex de caractere inválido e construção inválida depois.
    }
}