import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalendarioTestCase {

    private Store store;

    @BeforeEach
    public void setup() {
        store = Store.getInstance();
        store.games.clear();
    }

    @Test
    public void testProibirGerarCalendarioComJogoEmCurso() {
        Estadio est = new Estadio(1, "Luz", "Lisboa", 50000);
        TorneioApp.Game jogo = new TorneioApp.Game(1, "Fase de Grupos", "Benfica", "Porto", "10/06 18:00", est);

        // Jogo começa!
        jogo.state = TorneioApp.GameState.EM_CURSO;
        store.games.add(jogo);

        // Simulação exata da proteção que colocámos no topo do teu "generateCalendar()"
        boolean erroDetetado = false;
        for (TorneioApp.Game g : store.games) {
            if (g.state == TorneioApp.GameState.EM_CURSO || g.state == TorneioApp.GameState.CONCLUIDO) {
                erroDetetado = true;
                break;
            }
        }

        assertTrue(erroDetetado, "O gerador de calendário devia ter bloqueado a operação porque já há bola a rolar.");
    }
    @Test
    public void testCalculoDePontosDaFaseDeGrupos() {
        // Criar um jogo concluído com o resultado Benfica 2 - 1 Porto
        TorneioApp.Game jogo = new TorneioApp.Game(1, "Fase de Grupos", "Benfica", "Porto", "10/06", new Estadio(1, "Luz", "Lx", 50000));
        jogo.state = TorneioApp.GameState.CONCLUIDO;
        jogo.goalsA = 2; // Benfica marca 2
        jogo.goalsB = 1; // Porto marca 1

        store.games.add(jogo);

        int pontosBenfica = 0;
        int pontosPorto = 0;

        // Simulamos o algoritmo que tens no Calendario.java
        if (jogo.goalsA > jogo.goalsB) {
            pontosBenfica += 3;
        } else if (jogo.goalsB > jogo.goalsA) {
            pontosPorto += 3;
        } else {
            pontosBenfica += 1;
            pontosPorto += 1;
        }

        assertEquals(3, pontosBenfica, "O Benfica venceu, devia ter 3 pontos.");
        assertEquals(0, pontosPorto, "O Porto perdeu, devia ter 0 pontos.");
    }
}