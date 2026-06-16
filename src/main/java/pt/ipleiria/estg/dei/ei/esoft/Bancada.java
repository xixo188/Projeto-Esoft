public class Bancada {
    public int id;
    public int capacidade;
    public String nome;

    public Bancada(int id, String nome, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
    }

    @Override
    public String toString() {
        if (id == -1) return nome;
        return nome + " (" + capacidade + " lugares)";
    }
}