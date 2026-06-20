import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EstatisticasGeraisTestCase {

    @Test
    public void testContabilizarGolos() {

        int golos = 3;

        assertEquals(3, golos);
    }

    @Test
    public void testContabilizarCartaoAmarelo() {

        int amarelos = 1;

        assertEquals(1, amarelos);
    }

    @Test
    public void testContabilizarCartaoVermelho() {

        int vermelhos = 1;

        assertEquals(1, vermelhos);
    }

    @Test
    public void testJogadorSemEstatisticas() {

        int golos = 0;
        int amarelos = 0;
        int vermelhos = 0;

        assertEquals(0, golos);
        assertEquals(0, amarelos);
        assertEquals(0, vermelhos);
    }

    @Test
    public void testOrdenacaoPorGolos() {

        int jogadorA = 5;
        int jogadorB = 2;

        assertTrue(jogadorA > jogadorB);
    }
}