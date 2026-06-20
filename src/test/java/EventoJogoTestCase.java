import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventoJogoTestCase {


    private Store store;

    private Equipa equipaA;
    private Equipa equipaB;

    private Jogador jogadorA;
    private Jogador jogadorB;

    private Jogo jogo;

    @BeforeEach
    public void setUp() {
        store = Store.getInstance();

        equipaA = new Equipa(
                store.nextId(),
                "Equipa Evento A",
                "EEA",
                "Treinador A",
                "Azul",
                "Branco",
                ""
        );

        equipaB = new Equipa(
                store.nextId(),
                "Equipa Evento B",
                "EEB",
                "Treinador B",
                "Verde",
                "Preto",
                ""
        );

        jogadorA = new Jogador(
                store.nextId(),
                "Jogador A",
                10,
                "Avançado",
                ""
        );

        jogadorB = new Jogador(
                store.nextId(),
                "Jogador B",
                9,
                "Avançado",
                ""
        );

        equipaA.players.add(jogadorA);
        equipaB.players.add(jogadorB);

        store.teams.add(equipaA);
        store.teams.add(equipaB);

        Estadio estadio = new Estadio(
                store.nextId(),
                "Estádio de Testes",
                "Leiria",
                10000
        );

        jogo = new Jogo(
                store.nextId(),
                "Fase de Grupos",
                equipaA.name,
                equipaB.name,
                "20/06/2026 18:00",
                estadio
        );

        jogo.state =
                EstadoJogo.EM_CURSO;

        store.games.add(jogo);
    }

    @AfterEach
    public void tearDown() {
        store.removeEventsByGame(jogo);
        store.removeGameStats(jogo);

        store.games.remove(jogo);

        store.teams.remove(equipaA);
        store.teams.remove(equipaB);
    }

    @Test
    public void testRegistarGoloDaEquipaA() {
        EventoJogo evento = new EventoJogo(
                store.nextId(),
                jogo,
                equipaA,
                jogadorA,
                TipoEventoJogo.GOLO,
                25
        );

        store.addGameEvent(evento);

        List<EventoJogo> eventos =
                store.findEventsByGame(jogo);

        assertAll(
                () -> assertEquals(
                        1,
                        jogo.goalsA,
                        "A equipa A deveria ter um golo."
                ),

                () -> assertEquals(
                        0,
                        jogo.goalsB,
                        "A equipa B não deveria ter golos."
                ),

                () -> assertEquals(
                        1,
                        eventos.size(),
                        "O jogo deveria ter um acontecimento registado."
                ),

                () -> assertSame(
                        evento,
                        eventos.get(0),
                        "O acontecimento guardado deveria ser o mesmo que foi criado."
                ),

                () -> assertSame(
                        jogadorA,
                        eventos.get(0).player,
                        "O golo deveria estar associado ao jogador correto."
                ),

                () -> assertEquals(
                        25,
                        eventos.get(0).minute,
                        "O minuto do golo não foi guardado corretamente."
                )
        );
    }

    @Test
    public void testRegistarGoloDaEquipaB() {
        EventoJogo evento = new EventoJogo(
                store.nextId(),
                jogo,
                equipaB,
                jogadorB,
                TipoEventoJogo.GOLO,
                40
        );

        store.addGameEvent(evento);

        assertAll(
                () -> assertEquals(
                        0,
                        jogo.goalsA,
                        "A equipa A não deveria ter golos."
                ),

                () -> assertEquals(
                        1,
                        jogo.goalsB,
                        "A equipa B deveria ter um golo."
                )
        );
    }

    @Test
    public void testRegistarVariosGolos() {
        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaA,
                        jogadorA,
                        TipoEventoJogo.GOLO,
                        10
                )
        );

        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaA,
                        jogadorA,
                        TipoEventoJogo.GOLO,
                        35
                )
        );

        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaB,
                        jogadorB,
                        TipoEventoJogo.GOLO,
                        70
                )
        );

        assertAll(
                () -> assertEquals(
                        2,
                        jogo.goalsA,
                        "A equipa A deveria ter dois golos."
                ),

                () -> assertEquals(
                        1,
                        jogo.goalsB,
                        "A equipa B deveria ter um golo."
                ),

                () -> assertEquals(
                        "2 - 1",
                        jogo.resultText(),
                        "O resultado textual do jogo está incorreto."
                )
        );
    }

    @Test
    public void testCartaoAmareloNaoAlteraResultado() {
        EventoJogo evento = new EventoJogo(
                store.nextId(),
                jogo,
                equipaA,
                jogadorA,
                TipoEventoJogo.CARTAO_AMARELO,
                50
        );

        store.addGameEvent(evento);

        assertAll(
                () -> assertEquals(
                        1,
                        jogo.yellowA,
                        "A equipa A deveria ter um cartão amarelo."
                ),

                () -> assertEquals(
                        0,
                        jogo.goalsA,
                        "Um cartão não deveria alterar os golos da equipa A."
                ),

                () -> assertEquals(
                        0,
                        jogo.goalsB,
                        "Um cartão não deveria alterar os golos da equipa B."
                )
        );
    }

    @Test
    public void testRegistarFalta() {
        EventoJogo evento = new EventoJogo(
                store.nextId(),
                jogo,
                equipaB,
                jogadorB,
                TipoEventoJogo.FALTA,
                60
        );

        store.addGameEvent(evento);

        assertAll(
                () -> assertEquals(
                        0,
                        jogo.foulsA,
                        "A equipa A não deveria ter faltas."
                ),

                () -> assertEquals(
                        1,
                        jogo.foulsB,
                        "A equipa B deveria ter uma falta."
                ),

                () -> assertEquals(
                        0,
                        jogo.yellowB,
                        "Uma falta não deveria criar automaticamente um cartão."
                )
        );
    }

    @Test
    public void testEventosOrdenadosPorMinuto() {
        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaA,
                        jogadorA,
                        TipoEventoJogo.REMATE,
                        70
                )
        );

        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaB,
                        jogadorB,
                        TipoEventoJogo.FALTA,
                        15
                )
        );

        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaA,
                        jogadorA,
                        TipoEventoJogo.GOLO,
                        42
                )
        );

        List<EventoJogo> eventos =
                store.findEventsByGame(jogo);

        assertAll(
                () -> assertEquals(
                        3,
                        eventos.size()
                ),

                () -> assertEquals(
                        15,
                        eventos.get(0).minute,
                        "O primeiro acontecimento deveria ser o do minuto 15."
                ),

                () -> assertEquals(
                        42,
                        eventos.get(1).minute,
                        "O segundo acontecimento deveria ser o do minuto 42."
                ),

                () -> assertEquals(
                        70,
                        eventos.get(2).minute,
                        "O terceiro acontecimento deveria ser o do minuto 70."
                )
        );
    }

    @Test
    public void testRemoverEventosDoJogo() {
        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaA,
                        jogadorA,
                        TipoEventoJogo.GOLO,
                        20
                )
        );

        store.addGameEvent(
                new EventoJogo(
                        store.nextId(),
                        jogo,
                        equipaB,
                        jogadorB,
                        TipoEventoJogo.CARTAO_VERMELHO,
                        80
                )
        );

        assertEquals(
                2,
                store.findEventsByGame(jogo).size(),
                "Deveriam existir dois acontecimentos antes da remoção."
        );

        store.removeEventsByGame(jogo);

        assertAll(
                () -> assertTrue(
                        store.findEventsByGame(jogo).isEmpty(),
                        "O histórico do jogo deveria ficar vazio."
                ),

                () -> assertEquals(
                        0,
                        jogo.goalsA,
                        "Os golos deveriam voltar a zero."
                ),

                () -> assertEquals(
                        0,
                        jogo.redB,
                        "Os cartões vermelhos deveriam voltar a zero."
                )
        );
    }


}
