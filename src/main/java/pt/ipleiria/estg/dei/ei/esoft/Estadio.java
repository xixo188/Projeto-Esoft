import java.util.ArrayList;
import java.util.List;

public class Estadio {
    public int id;
    public int capacidade;
    public String nome;
    public String localizacao;
    public List<Bancada> bancadas = new ArrayList<>();

    public Estadio(int id, String nome, String localizacao, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.capacidade = capacidade;
    }

    public int totalCapacidadeBancadas() {
        return bancadas.stream().mapToInt(b -> b.capacidade).sum();
    }
}