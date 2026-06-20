import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BancadaTestCase {

    @Test
    public void testEditarEBancada() {
        Estadio estadio = new Estadio(1, "Alvalade", "Lisboa", 50000);
        Bancada bancada = new Bancada(1, "Bancada A", 10000);
        estadio.bancadas.add(bancada);

        // Simular Edição da Bancada
        bancada.nome = "Bancada Premium";
        bancada.capacidade = 12000;

        assertEquals("Bancada Premium", estadio.bancadas.get(0).nome);
        assertEquals(12000, estadio.bancadas.get(0).capacidade);
    }

    @Test
    public void testRemoverBancada() {
        Estadio estadio = new Estadio(1, "Alvalade", "Lisboa", 50000);
        Bancada bancada = new Bancada(1, "Bancada A", 10000);
        estadio.bancadas.add(bancada);

        // Remover
        estadio.bancadas.remove(bancada);
        assertTrue(estadio.bancadas.isEmpty(), "A bancada devia ter sido apagada do estádio.");
    }

    @Test
    public void testValidarLimiteDeLotacaoDoEstadio() {
        // Estádio com capacidade máxima de 30.000
        Estadio estadio = new Estadio(1, "Municipal", "Braga", 30000);

        estadio.bancadas.add(new Bancada(1, "Nascente", 20000));

        // Tentar adicionar uma bancada de 15.000 (Soma daria 35.000 -> Erro!)
        int novaBancadaCapacidade = 15000;

        int capacidadeSomaTotal = estadio.totalCapacidadeBancadas() + novaBancadaCapacidade;
        boolean ultrapassaMaximo = capacidadeSomaTotal > estadio.capacidade;

        assertTrue(ultrapassaMaximo, "O sistema devia acusar que a lotação ultrapassou o limite do estádio.");
    }
}