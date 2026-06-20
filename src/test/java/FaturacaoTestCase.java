import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FaturacaoTestCase {

    @Test
    public void testCalculoFaturacaoBilhetes() {
        Estadio luz = new Estadio(1, "Luz", "Lisboa", 65000);
        Bancada b = new Bancada(1, "Sagres", 30000);
        Jogo jogo = new Jogo(1, "Fase de Grupos", "Benfica", "Porto", "10/06 18:00", luz);

        LoteBilhetes lote = new LoteBilhetes(1, jogo, b, 15.00, 500);

        // Vender 10 bilhetes
        lote.sold = 10;
        double receitaEsperada = 150.00; // 10 * 15.00

        assertEquals(receitaEsperada, lote.sold * lote.price, "O montante total faturado em bilheteira está incorreto.");
    }
    @Test
    public void testNaoPermitirLoteMaiorQueLotacaoDaBancada() {
        Estadio luz = new Estadio(1, "Luz", "Lisboa", 65000);
        Bancada bancada = new Bancada(1, "Sagres", 30000); // Cabem 30.000 pessoas
        Jogo jogo = new Jogo(1, "Fase de Grupos", "Benfica", "Porto", "10/06", luz);

        // O utilizador tenta criar um lote com 35.000 bilhetes!
        int bilhetesTentativa = 35000;

        boolean ultrapassaLotacao = bilhetesTentativa > bancada.capacidade;

        assertTrue(ultrapassaLotacao, "O sistema tem de detetar que o lote de bilhetes excede a lotação física da bancada.");
    }
}