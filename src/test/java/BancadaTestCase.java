import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BancadaTestCase {

    @Test
    public void testAdicionarBancadasAoEstadio() {
        Estadio estadio = new Estadio(1, "Estádio José Alvalade", "Lisboa", 50000);

        Bancada b1 = new Bancada(10, "Bancada Sul", 25000);
        Bancada b2 = new Bancada(11, "Bancada Norte", 20000);

        estadio.bancadas.add(b1);
        estadio.bancadas.add(b2);

        assertEquals(2, estadio.bancadas.size(), "O estádio devia ter exatamente 2 bancadas.");
    }

    @Test
    public void testCalcularTotalCapacidadeBancadas() {
        Estadio estadio = new Estadio(1, "Estádio Municipal de Braga", "Braga", 30000);

        estadio.bancadas.add(new Bancada(1, "Bancada Nascente", 15000));
        estadio.bancadas.add(new Bancada(2, "Bancada Poente", 12000));

        // 15000 + 12000 = 27000
        int totalCalculado = estadio.totalCapacidadeBancadas();
        assertEquals(27000, totalCalculado, "A soma da capacidade das bancadas falhou.");
    }
}