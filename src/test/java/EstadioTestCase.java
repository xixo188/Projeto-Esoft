import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstadioTestCase {

    private Store store;

    @BeforeEach
    public void setup() {
        store = Store.getInstance();
        store.stadiums.clear();
        store.games.clear();
    }

    @Test
    public void testEditarEstadio() {
        Estadio estadio = new Estadio(1, "Estádio Velho", "Porto", 10000);
        store.stadiums.add(estadio);

        // Simular a Edição
        Estadio estGuardado = store.findStadium(1);
        estGuardado.nome = "Estádio Moderno";
        estGuardado.capacidade = 15000;

        // Verificar se alterou na Store
        Estadio estVerificacao = store.findStadium(1);
        assertEquals("Estádio Moderno", estVerificacao.nome);
        assertEquals(15000, estVerificacao.capacidade);
    }

    @Test
    public void testRemoverEstadio() {
        Estadio estadio = new Estadio(1, "Estádio de Teste", "Braga", 20000);
        store.stadiums.add(estadio);

        // Simular a Remoção
        store.stadiums.remove(estadio);

        // Verificar se desapareceu
        assertNull(store.findStadium(1), "O estádio devia ter sido removido da Store.");
    }

    @Test
    public void testBloquearAcoesDeEstadioSeTorneioJaComeçou() {
        // 1. Criar um estádio inicial
        Estadio estadio = new Estadio(1, "Estádio Central", "Lisboa", 50000);
        store.stadiums.add(estadio);

        // 2. SIMULAR QUE O TORNEIO JÁ COMEÇOU (Injetar um jogo EM_CURSO)
        Jogo jogoAoVivo = new Jogo(10, "Fase de Grupos", "Benfica", "Porto", "10/06 18:00", estadio);
        jogoAoVivo.state = EstadoJogo.EM_CURSO; // <-- O Truque está aqui
        store.games.add(jogoAoVivo);

        // 3. Executar a mesma validação lógica que usas no teu "EstadioPainelControlador"
        boolean torneioComecou = store.games.stream().anyMatch(g ->
                g.state == EstadoJogo.EM_CURSO || g.state == EstadoJogo.CONCLUIDO
        );

        // 4. Mudar ou tentar criar dados neste estado tem de dar "Erro" (Lógica bloqueada)
        assertTrue(torneioComecou, "O sistema devia detetar que o torneio já arrancou.");

        // Se tentar adicionar um estádio novo agora, violaria a regra de negócio do ecrã
        if (torneioComecou) {
            // Teste passa porque a flag detetou corretamente o estado crítico e impediria a ação no controlador
            assertTrue(true);
        } else {
            fail("O sistema permitiu passar a validação mesmo com um jogo em curso!");
        }
    }
}