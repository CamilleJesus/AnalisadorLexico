import token.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class AnalisadorLexico {

    private static ArrayList<String> auxLexemas = new ArrayList<>();
    private static ArrayList<Integer> auxLinhas = new ArrayList<>();
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static int totalLinhas = 0;

    public static void main(String[] args) {

        try {
            lerArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        identificaLexema();
    }

    public static void lerArquivo() throws IOException {
        FileReader arquivo = new FileReader("teste/entrada1.txt");
        BufferedReader buffer = new BufferedReader(arquivo);
        String linha;
        boolean comentarioBloco = false;


        //Lê todas as linhas do arquivo até o final:
        while ((linha = buffer.readLine()) != null) {
            totalLinhas++;

            if (linha.contains("/*")) {
                linha = linha.replaceAll("/\\*.*", "");
                comentarioBloco = true;
                separaLinha(linha);
            }

            if (comentarioBloco == false) {

                if (linha.contains("//")) {
                    linha = linha.replaceAll("//.*", "");
                }
            } else {

                if (linha.contains("*/")) {
                    linha = linha.replaceAll(".*\\*/", "");
                    comentarioBloco = false;
                } else {
                    linha = linha.replaceAll(".*", "");
                }
            }
            separaLinha(linha);
        }
        arquivo.close();
        //System.out.println(totalLinhas);
    }

    public static void separaLinha(String linha) {
        String partes[] = linha.split("\\s+");

        for (int i = 0; i < partes.length; i++) {
            //System.out.println(partes[i]);

            if (!partes[i].isEmpty()) {
                auxLexemas.add(partes[i]);
                auxLinhas.add(totalLinhas);
            }
        }
    }

    public static void identificaLexema() {
        StringBuilder lexema = new StringBuilder();
        String classe = "", auxLexema, auxLexema2 = "";

        for (int i = 0; i < auxLexemas.size(); i++) {
            auxLexema = auxLexemas.get(i);
            System.out.println(auxLexemas.get(i));

            if (!(classe = classificaLexema(auxLexema)).equals("CLASSE_INVALIDA")) {
                tokens.add(new Token(classe, auxLexema, auxLinhas.get(i)));
            } else {

                for (int j = 0; j < auxLexemas.get(i).length(); j++) {
                    /*System.out.println(auxLexemas.get(i).charAt(j)); a=2
                    //lexema.append(auxLexema.charAt(j));
                    //VOU IMPLEMENTAR AQUI PARA PERCORRER CARACTERE POR CARACTERE

                    if (!(classe = classificaLexema(auxLexema.toString())).equals("CLASSE_INVALIDA")) {
                        tokens.add(new Token(classe, auxLexema, auxLinhas.get(i)));
                    }

                    */
                }
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