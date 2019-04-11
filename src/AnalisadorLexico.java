import token.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class AnalisadorLexico {

    private static ArrayList<String> auxLexemas = new ArrayList<>();
    private static ArrayList<Integer> auxLinhas = new ArrayList<>();
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static int totalLinhas = 0;
    private static String nomeArquivo;

    public static void main(String[] args) {

        try {
            lerArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nIniciando a análise léxica do arquivo de entrada: " + nomeArquivo + ".");
        identificaLexema();
        checaTokens();
        System.out.println("\nFinalizando a análise léxica do arquivo de entrada: " + nomeArquivo + ".");

        try {
            escreveArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void lerArquivo() throws IOException {
        String linha;
        boolean comentarioBloco = false;
        System.out.println("\n -- ANALISADOR LÉXICO -- ");
        Scanner in = new Scanner(System.in);
        File arquivo;

        do {
            System.out.print("Digite o nome do arquivo que deseja analisar (com a extensão): ");
            nomeArquivo = in.nextLine();
            arquivo = new File("teste/" + nomeArquivo);
        } while (!arquivo.exists());
        FileReader leArquivo = new FileReader(arquivo);
        BufferedReader buffer = new BufferedReader(leArquivo);

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
        buffer.close();
    }

    public static void separaLinha(String linha) {
        String partes[] = linha.split("\\s+");

        for (int i = 0; i < partes.length; i++) {

            if (!partes[i].isEmpty()) {
                auxLexemas.add(partes[i]);
                auxLinhas.add(totalLinhas);
            }
        }
    }

    public static void identificaLexema() {
        StringBuilder lexema = new StringBuilder();
        String classe, classeAnterior = "", auxLexema;

        for (int i = 0; i < auxLexemas.size(); i++) {
            auxLexema = auxLexemas.get(i);
            classe = classificaLexema(auxLexema);

            if ((!classe.equals("CLASSE_INVALIDA")) && (!classe.equals("CADEIA_CARACTERES_INCOMPLETA")) && (!classeAnterior.equals("CADEIA_CARACTERES_INCOMPLETA"))) {
                tokens.add(new Token(classe, auxLexema, auxLinhas.get(i)));
            } else {
                auxLexema = auxLexemas.get(i);

                for (int j = 0; j < auxLexema.length(); j++) {
                    lexema.append(auxLexema.charAt(j)); //"O
                    classe = classificaLexema(lexema.toString());

                    if (classe.equals("CLASSE_INVALIDA")) {
                        char c = lexema.charAt(lexema.length() - 1);
                        tokens.add(new Token(classeAnterior, lexema.substring(0, lexema.length() - 1), auxLinhas.get(i)));
                        lexema.delete(0, lexema.length());
                        lexema.append(c);
                        classeAnterior = classificaLexema(lexema.toString());

                        if ((j+1) == auxLexema.length()) {
                            tokens.add(new Token(classeAnterior, lexema.toString(), auxLinhas.get(i)));
                        }
                    } else {
                        classeAnterior = classe;
                    }
                }

                if (!classe.equals("CADEIA_CARACTERES_INCOMPLETA")) {
                    lexema.delete(0, lexema.length());
                    classeAnterior = "";
                } else {
                    lexema.append(" ");
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
        } else if (lexema.matches("(-)?\\s*\\d+\\.")) {
            return "NUMERO INCOMPLETO";
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
        } else if (lexema.matches("\"((\\\\\")|[^\"]|\\n)*")) {
            return "CADEIA_CARACTERES_INCOMPLETA";
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

    public static void checaTokens() {

        for (int i = 0; i < tokens.size(); i++) {

            if (i > 2) {

                if ((tokens.get(i).getClasse().equals("NUMERO")) && (tokens.get(i - 1).getClasse().equals("OPERADOR_ARITMETICO"))) {

                    if ((!tokens.get(i - 2).getClasse().equals("NUMERO")) || (!tokens.get(i - 2).getClasse().equals("IDENTIFICADOR"))) {
                        tokens.get(i).setLexema(tokens.get(i - 1).getLexema() + tokens.get(i).getLexema());
                        tokens.remove(i - 1);
                    }
                }
            } else {

                if ((tokens.get(1).getClasse().equals("NUMERO")) && (tokens.get(0).getClasse().equals("OPERADOR_ARITMETICO"))) {
                    tokens.get(1).setLexema(tokens.get(0).getLexema() + tokens.get(1).getLexema());
                    tokens.remove(0);
                }

                if ((tokens.get(2).getClasse().equals("NUMERO")) && (tokens.get(1).getClasse().equals("OPERADOR_ARITMETICO"))) {
                    tokens.get(2).setLexema(tokens.get(1).getLexema() + tokens.get(2).getLexema());
                    tokens.remove(1);
                }
            }
        }
    }

    public static void escreveArquivo() throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("teste/saida.txt"));

        for (int i = 0; i < tokens.size(); i++) {
            buffWrite.append(tokens.get(i).toString() + "\n");
        }
        buffWrite.close();
        System.out.println("\nResultado da análise léxica do arquivo \"" + nomeArquivo + "\" no arquivo: saida.txt.");
    }
}