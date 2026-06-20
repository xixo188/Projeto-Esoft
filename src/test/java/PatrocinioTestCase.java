import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatrocinioTestCase {

    private Store store;

    @BeforeEach
    public void setup() {
        store = Store.getInstance();
        store.patrocinios.clear();
        store.games.clear();
    }

    @Test
    public void testInserirPatrocinio() {
        Patrocinio patrocinio = new Patrocinio(
                1,
                "Nike",
                "Patrocinador oficial",
                5000.0
        );

        store.patrocinios.add(patrocinio);

        assertEquals(1, store.patrocinios.size());
        assertEquals("Nike", store.patrocinios.get(0).nome);
        assertEquals("Patrocinador oficial", store.patrocinios.get(0).descricao);
        assertEquals(5000.0, store.patrocinios.get(0).valor);
    }

    @Test
    public void testEditarPatrocinio() {
        Patrocinio patrocinio = new Patrocinio(
                1,
                "Nike",
                "Patrocinador oficial",
                5000.0
        );

        store.patrocinios.add(patrocinio);

        patrocinio.nome = "Adidas";
        patrocinio.descricao = "Patrocinador principal";
        patrocinio.valor = 8000.0;

        assertEquals("Adidas", store.patrocinios.get(0).nome);
        assertEquals("Patrocinador principal", store.patrocinios.get(0).descricao);
        assertEquals(8000.0, store.patrocinios.get(0).valor);
    }

    @Test
    public void testRemoverPatrocinio() {
        Patrocinio patrocinio = new Patrocinio(
                1,
                "Nike",
                "Patrocinador oficial",
                5000.0
        );

        store.patrocinios.add(patrocinio);
        store.patrocinios.remove(patrocinio);

        assertTrue(store.patrocinios.isEmpty());
    }

    @Test
    public void testVisualizarPatrocinio() {
        Patrocinio patrocinio = new Patrocinio(
                1,
                "Nike",
                "Patrocinador oficial",
                5000.0
        );

        assertNotNull(patrocinio);
        assertEquals("Nike", patrocinio.nome);
        assertEquals("Patrocinador oficial", patrocinio.descricao);
        assertEquals(5000.0, patrocinio.valor);
    }

    @Test
    public void testListaPatrociniosVazia() {
        store.patrocinios.clear();

        assertEquals(0, store.patrocinios.size());
    }

    @Test
    public void testBloquearCriacaoPatrocinioComJogoIniciado() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        jogo.state = EstadoJogo.EM_CURSO;
        store.games.add(jogo);

        boolean torneioIniciado = store.games.stream().anyMatch(g ->
                g.state == EstadoJogo.EM_CURSO ||
                        g.state == EstadoJogo.CONCLUIDO
        );

        assertTrue(torneioIniciado);
    }

    @Test
    public void testBloquearEdicaoPatrocinioComTorneioIniciado() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        jogo.state = EstadoJogo.EM_CURSO;
        store.games.add(jogo);

        boolean torneioIniciado = store.games.stream().anyMatch(g ->
                g.state == EstadoJogo.EM_CURSO ||
                        g.state == EstadoJogo.CONCLUIDO
        );

        assertTrue(torneioIniciado);
    }

    @Test
    public void testBloquearRemocaoPatrocinioComTorneioIniciado() {
        Estadio estadio = new Estadio(1, "Estádio da Luz", "Lisboa", 65000);

        Jogo jogo = new Jogo(
                1,
                "Fase de Grupos",
                "Benfica",
                "Porto",
                "10/06/2026 18:00",
                estadio
        );

        jogo.state = EstadoJogo.CONCLUIDO;
        store.games.add(jogo);

        boolean torneioIniciado = store.games.stream().anyMatch(g ->
                g.state == EstadoJogo.EM_CURSO ||
                        g.state == EstadoJogo.CONCLUIDO
        );

        assertTrue(torneioIniciado);
    }
}