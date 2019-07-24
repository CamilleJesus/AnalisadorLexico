/*
Autores: Camille Jesús e Reinildo Souza
Componente Curricular: EXA869 - MI Processadores de Linguagem de Programação (P03)
Data: 31/03/2019
*/

import token.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class AnalisadorLexico {

    private static ArrayList<String> auxLexemas = new ArrayList<>();
    private static ArrayList<Integer> auxLinhas = new ArrayList<>();
    private static ArrayList<Token> tokens = new ArrayList<>();
    private static ArrayList<String> erros = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("\n -- ANALISADOR LÉXICO -- ");
        lerArquivos();
    }

    public static void lerArquivos() {
        File arquivos[], diretorio = new File("teste/");
        arquivos = diretorio.listFiles();
        int numeroArquivo = 0;

        for (int i = 0; i < arquivos.length; i++){

            if (arquivos[i].toString().contains("entrada")) {

                try {
                    numeroArquivo = i + 1;
                    System.out.println("\nIniciando a análise léxica do " + numeroArquivo + "º arquivo de entrada.");
                    lerArquivo(arquivos[i]);
                    identificaLexema();
                    checaTokens();
                    escreveArquivo((i + 1));
                    limpaEstruturas();
                    System.out.println("\nFinalizando a análise léxica do " + numeroArquivo + "º arquivo de entrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // FAZ A LEITURA DO ARQUIVO .TXT DE ENTRADA E REMOVE OS COMENTÁRIOS
    public static void lerArquivo(File nomeArquivo) throws IOException {
        String linha;
        boolean comentarioBloco = false;
        Scanner in = new Scanner(System.in);
        int numeroLinha = 0, linhaComentarioBloco = 0;
        BufferedReader buffer = new BufferedReader(new FileReader(nomeArquivo));

        //Lê todas as linhas do arquivo até o final:
        while ((linha = buffer.readLine()) != null) {
            numeroLinha++;

            if ((linha.contains("/*")) && (linha.contains("*/"))) {
                linha = linha.replaceAll("/\\*.*\\*/", "");
                comentarioBloco = false;
                separaLinha(linha, numeroLinha);
                linhaComentarioBloco = numeroLinha;

                if (linha.contains("//")) {
                    linha = linha.replaceAll("//.*", "");
                }
                separaLinha(linha, numeroLinha);
            } else {

                if (linha.contains("/*")) {
                    linha = linha.replaceAll("/\\*.*", "");
                    comentarioBloco = true;
                    separaLinha(linha, numeroLinha);
                    linhaComentarioBloco = numeroLinha;
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
                separaLinha(linha, numeroLinha);
            }
        }
        buffer.close();

        // SINALIZA O ERRO DE COMENTÁRIO DE BLOCO NÃO FECHADO
        if (comentarioBloco == true) {
            erros.add(mensagemErro(linhaComentarioBloco, "comentário de bloco aberto."));
        }
    }


    // QUEBRA O ARQUIVO DE ENTRADA EM VÁRIAS LINHAS
    public static void separaLinha(String linha, int numeroLinha) {
        String partes[] = linha.split("\\s+");

        for (int i = 0; i < partes.length; i++) {

            if (!partes[i].isEmpty()) {
                auxLexemas.add(partes[i]);
                auxLinhas.add(numeroLinha);
            }
        }
    }

    // ANALISA CADA LEXEMA (O ATUAL E O ANTERIOR) ANTES DE CLASSIFICÁ-LO
    public static void identificaLexema() {
        StringBuilder lexema = new StringBuilder();
        String auxLexema, classe = "", classeAnterior = "";
        int linhaCadeiaCaracteres = 0, cadeiaCaracteres = 0;

        for (int i = 0; i < auxLexemas.size(); i++) {
            auxLexema = auxLexemas.get(i);
            classe = classificaLexema(auxLexema);

            if (classe.equals("VALOR_INESPERADO")) {
                erros.add(mensagemErro(auxLinhas.get(i), "valor inesperado." ));
            } else if (classe.equals("CARACTERE_INVALIDO")) {
                erros.add(mensagemErro(auxLinhas.get(i), "caractere inválido."));
            } else {

                if ((!classe.equals("CLASSE_INVALIDA")) && (!classe.equals("CADEIA_CARACTERES_INCOMPLETA")) && (!classeAnterior.equals("CADEIA_CARACTERES_INCOMPLETA")) && (!classeAnterior.equals("NUMERO_INCOMPLETO")) && (!classe.equals("VALOR_INESPERADO")) && (!classe.equals("CARACTERE_INVALIDO"))) {
                    tokens.add(new Token(classe, auxLexema, auxLinhas.get(i)));
                } else {
                    auxLexema = auxLexemas.get(i);

                    for (int j = 0; j < auxLexema.length(); j++) {
                        lexema.append(auxLexema.charAt(j));
                        classe = classificaLexema(lexema.toString());

                        if (classe.equals("VALOR_INESPERADO")) {
                            erros.add(mensagemErro(auxLinhas.get(i), "número mal formado")); //valor inesperado.
                        } else if (classe.equals("CARACTERE_INVALIDO")) {
                            erros.add(mensagemErro(auxLinhas.get(i), "caractere inválido")); //caractere não pertence ao alfabeto.
                        } else {

                            if ((classe.equals("CADEIA_CARACTERES_INCOMPLETA")) && (cadeiaCaracteres == 0)) {
                                linhaCadeiaCaracteres = auxLinhas.get(i);
                                cadeiaCaracteres++;
                            }

                            if (classe.equals("CLASSE_INVALIDA")) {
                                char c = lexema.charAt(lexema.length() - 1);
                                tokens.add(new Token(classeAnterior, lexema.substring(0, lexema.length() - 1), auxLinhas.get(i)));
                                lexema.delete(0, lexema.length());
                                lexema.append(c);
                                classeAnterior = classificaLexema(lexema.toString());
                                cadeiaCaracteres = 0;

                                if ((j + 1) == auxLexema.length()) {
                                    tokens.add(new Token(classeAnterior, lexema.toString(), auxLinhas.get(i)));
                                }
                            } else {
                                classeAnterior = classe;
                            }
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

        if (classe.equals("CADEIA_CARACTERES_INCOMPLETA")) {
            erros.add(mensagemErro(linhaCadeiaCaracteres, "cadeia de caracteres aberta."));
        }
    }


    // NOMEIA O TOKEN DE ACORDO COM SUA CATEGORIA
    public static String classificaLexema(String lexema) {

        if (lexema.matches("[a-zA-Z]+\\w*")) {

            if (PalavrasReservadas.ehReservada(lexema)) {
                return "PALAVRA_RESERVADA";
            }
            return "IDENTIFICADOR";
        } else if (lexema.matches("-?\\d+(\\.(\\d+))?")) {
            return "NUMERO";
        } else if (lexema.matches("-?\\d+\\.?")) {
            return "NUMERO_INCOMPLETO";
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
        } else if ((lexema.matches("[a-zA-Z]+[^\\d-+*/<>=!(){}\\[\\]\"\',;]+")) || (lexema.matches("(-)?\\d+\\.*\\d*[^.,;\\-+*/<>=!)\\]]+"))) {
            return "VALOR_INESPERADO";
        } else if (lexema.matches("[^\\n\\w.()|+\\-<>=!/\\\\*\\[\\]{}\"\'\\\\\"]+")) {
            return "CARACTERE_INVALIDO";
        }
        return "CLASSE_INVALIDA";
    }

    // ADICIONA A MENSAGAGEM DE ERRO NO ARQUIVO DE SAÍDA
    public static String mensagemErro (long linhaErro, String erro) {
        return ("Erro léxico na linha " + linhaErro+ ": " + erro);
    }


    public static void checaTokens() {

        for (int i = 0; i < tokens.size(); i++) {

            if (i > 2) {

                if ((tokens.get(i).getClasse().equals("NUMERO")) && (tokens.get(i - 1).getLexema().equals("-"))) {

                    if ((!tokens.get(i - 2).getClasse().equals("NUMERO")) || (!tokens.get(i - 2).getClasse().equals("IDENTIFICADOR"))) {
                        tokens.get(i).setLexema(tokens.get(i - 1).getLexema() + tokens.get(i).getLexema());
                        tokens.remove(i - 1);
                    }
                }
            } else {

                if ((tokens.get(1).getClasse().equals("NUMERO")) && (tokens.get(0).getClasse().equals("-"))) {
                    tokens.get(1).setLexema(tokens.get(0).getLexema() + tokens.get(1).getLexema());
                    tokens.remove(0);
                }

                if ((tokens.get(2).getClasse().equals("NUMERO")) && (tokens.get(1).getClasse().equals("-"))) {
                    tokens.get(2).setLexema(tokens.get(1).getLexema() + tokens.get(2).getLexema());
                    tokens.remove(1);
                }
            }
            Token token = tokens.get(i);

            if (token.getClasse().equals("NUMERO_INCOMPLETO")) {
                erros.add(mensagemErro(token.getLinha(), "número mal formado")); //valor inesperado.
                tokens.remove(i);
            }

            if (token.getClasse().isEmpty()) {
                tokens.remove(i);
            }
        }
    }

    // GERA O ARQUIVO DE SAÍDA COM OS DEVIDOS TOKENS E POSSÍVELS ERROS LÉXICOS
    public static void escreveArquivo(int numeroArquivo) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("teste/saida" + numeroArquivo + ".txt"));

        for (int i = 0; i < tokens.size(); i++) {
            buffWrite.append(tokens.get(i).toString() + "\n");
        }

        if (erros.isEmpty()) {
            buffWrite.append("\nSucesso!");
        } else {

            for (int i = 0; i < erros.size(); i++) {
                buffWrite.append("\n" + erros.get(i));
            }
        }
        buffWrite.close();
        System.out.println("\nResultado da análise léxica no arquivo: saida" + numeroArquivo + ".txt");
    }

    public static void limpaEstruturas() {
        auxLexemas.clear();
        auxLinhas.clear();
        tokens.clear();
        erros.clear();
    }
}