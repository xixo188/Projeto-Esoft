import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstadioTestCase {

    @Test
    public void testCriarEstadioValido() {
        // Testa a criação com dados normais
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);

        assertEquals(1, estadio.id, "O ID do estádio devia ser 1.");
        assertEquals("Estádio da Luz", estadio.nome, "O nome do estádio não coincide.");
        assertEquals("Lisboa", estadio.localizacao, "A localização não coincide.");
        assertEquals(65000, estadio.capacidade, "A lotação total está incorreta.");
    }

    @Test
    public void testEstadioSemBancadasAoCriar() {
        // Garante que o estádio nasce sem bancadas associadas
        Estadio estadio = new Estadio(2, "Estádio do Dragão", "Porto", 50000);

        assertNotNull(estadio.bancadas, "A lista de bancadas não deve ser nula.");
        assertTrue(estadio.bancadas.isEmpty(), "Um estádio novo deve começar com 0 bancadas.");
    }
}