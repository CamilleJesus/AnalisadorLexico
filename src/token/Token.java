package token;

public class Token {
    private ClassesToken classe;
    private long linha;

    public Token (ClassesToken classe, long linha) {
        this.classe = classe;
        this.linha = linha;
    }

    public ClassesToken getClasse() {
        return classe;
    }

    public void setClasse(ClassesToken classe) {
        this.classe = classe;
    }

    public long getLinha() {
        return linha;
    }

    public void setLinha(long linha) {
        this.linha = linha;
    }

    @Override
    public String toString() {
        return (String.format("<%s, %d>", this.classe, this.linha));
    }
}