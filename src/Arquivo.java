import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Arquivo {
    public static void main(String[] args) {
        try {
            FileReader arquivo = new FileReader("teste/entrada1.txt");
            BufferedReader lerArquivo = new BufferedReader(arquivo);

            String linha = lerArquivo.readLine(); // lê a primeira linha
            // a variável "linha" recebe o valor "null" quando o processo
            // de repetição atingir o final do arquivo texto
            while (linha != null) {
                System.out.printf("%s\n", linha);

                linha = lerArquivo.readLine(); // lê da segunda até a última linha
            }

            arquivo.close();

        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n",
                    e.getMessage());
        }
    }
}
