package token;

public class Token {

    private ClassesToken classe;
    private String lexema;
    private long linha;

    public Token (ClassesToken classe, String lexema, long linha) {
        this.classe = classe;
        this.lexema = lexema;
        this.linha = linha;
    }

    public ClassesToken getClasse() {
        return this.classe;
    }

    public void setClasse(ClassesToken classe) {
        this.classe = classe;
    }

    public String getLexema() {
        return this.lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public long getLinha() {
        return this.linha;
    }

    public void setLinha(long linha) {
        this.linha = linha;
    }

    @Override
    public String toString() {
        return (String.format("<%s, %s, %d>", this.classe, this.lexema, this.linha));
    }
}