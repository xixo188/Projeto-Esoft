import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FaturacaoTestCase {

    @Test
    public void testFaturacaoBilhetes() {
        Estadio luz = new Estadio(1, "Luz", "Lisboa", 65000);
        Bancada b = new Bancada(1, "Sagres", 30000);
        TorneioApp.Game jogo = new TorneioApp.Game(1, "Fase de Grupos", "Benfica", "Porto", "10/06 18:00", luz);

        // Criar um lote de 100 bilhetes a 20.00€ cada
        TorneioApp.TicketBatch lote = new TorneioApp.TicketBatch(1, jogo, b, 20.00, 100);

        // Simular a venda de 3 bilhetes
        lote.available -= 3;
        lote.sold += 3;

        double receitaBilhetes = lote.sold * lote.price;

        assertEquals(97, lote.available, "Deviam sobrar 97 bilhetes.");
        assertEquals(3, lote.sold, "Deviam estar vendidos 3 bilhetes.");
        assertEquals(60.00, receitaBilhetes, "O cálculo do valor faturado nos bilhetes falhou.");
    }

    @Test
    public void testFaturacaoPatrocinios() {
        Store store = Store.getInstance();
        store.patrocinios.clear();

        store.patrocinios.add(new Patrocinio(1, "Sagres", "Bebidas", 50000.00));
        store.patrocinios.add(new Patrocinio(2, "Betano", "Apostas", 120000.00));

        // Soma todos os valores da lista
        double totalPatrocinios = store.patrocinios.stream()
                .mapToDouble(p -> p.valor)
                .sum();

        assertEquals(170000.00, totalPatrocinios, "O cálculo da faturação total dos patrocinadores está errado.");
    }
}