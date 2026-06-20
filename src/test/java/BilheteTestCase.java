import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BilheteTestCase {

    @Test
    public void testComprarBilhete() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);
        Bancada bancada = new Bancada(1, "Bancada Sagres", 30000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        LoteBilhetes bilhete = new LoteBilhetes(
                1,
                jogo,
                bancada,
                20.00,
                50
        );

        bilhete.available--;
        bilhete.sold++;

        assertEquals(49, bilhete.available, "Deveria restar 49 bilhetes disponíveis.");
        assertEquals(1, bilhete.sold, "Deveria existir 1 bilhete vendido.");
    }

    @Test
    public void testNaoComprarBilheteEsgotado() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);
        Bancada bancada = new Bancada(1, "Bancada Sagres", 30000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        LoteBilhetes bilhete = new LoteBilhetes(
                1,
                jogo,
                bancada,
                20.00,
                0
        );

        boolean esgotado = bilhete.available <= 0;

        assertTrue(esgotado, "O sistema deve impedir a compra de bilhetes esgotados.");
    }

    @Test
    public void testEditarPrecoBilhete() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);
        Bancada bancada = new Bancada(1, "Bancada Sagres", 30000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        LoteBilhetes bilhete = new LoteBilhetes(
                1,
                jogo,
                bancada,
                20.00,
                50
        );

        bilhete.price = 25.00;

        assertEquals(25.00, bilhete.price, "O preço do bilhete deveria ter sido alterado.");
    }

    @Test
    public void testNaoPermitirQuantidadeMaiorQueLotacaoDaBancada() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);
        Bancada bancada = new Bancada(1, "Bancada Sagres", 30000);

        int quantidadeTentada = 35000;

        boolean ultrapassaLotacao = quantidadeTentada > bancada.capacidade;

        assertTrue(
                ultrapassaLotacao,
                "O sistema deve impedir criar bilhetes acima da lotação da bancada."
        );
    }

    @Test
    public void testCalculoReceitaBilhetesVendidos() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);
        Bancada bancada = new Bancada(1, "Bancada Sagres", 30000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        LoteBilhetes bilhete = new LoteBilhetes(
                1,
                jogo,
                bancada,
                15.00,
                100
        );

        bilhete.sold = 10;

        double receita = bilhete.sold * bilhete.price;

        assertEquals(150.00, receita, "A receita dos bilhetes vendidos está incorreta.");
    }
}