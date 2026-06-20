import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JogadorTestCase {


    private Store store;

    private TorneioApp.Team equipaA;
    private TorneioApp.Team equipaB;

    @BeforeEach
    public void setUp() {
        store = Store.getInstance();

        equipaA = new TorneioApp.Team(
                store.nextId(),
                "Equipa Jogadores A",
                "EJA",
                "Treinador A",
                "Azul",
                "Branco",
                ""
        );

        equipaB = new TorneioApp.Team(
                store.nextId(),
                "Equipa Jogadores B",
                "EJB",
                "Treinador B",
                "Verde",
                "Preto",
                ""
        );

        store.teams.add(equipaA);
        store.teams.add(equipaB);
    }

    @AfterEach
    public void tearDown() {
        for (TorneioApp.Player jogador : equipaA.players) {
            store.removePlayerEvents(jogador);
            store.removePlayerGameStats(jogador);
        }

        for (TorneioApp.Player jogador : equipaB.players) {
            store.removePlayerEvents(jogador);
            store.removePlayerGameStats(jogador);
        }

        store.teams.remove(equipaA);
        store.teams.remove(equipaB);
    }

    @Test
    public void testAdicionarJogadorComSucesso() {
        TorneioApp.Player jogador = new TorneioApp.Player(
                store.nextId(),
                "Jogador Teste",
                10,
                "Avançado",
                ""
        );

        equipaA.players.add(jogador);

        assertAll(
                () -> assertEquals(
                        1,
                        equipaA.players.size(),
                        "A equipa deveria ter um jogador."
                ),

                () -> assertTrue(
                        equipaA.players.contains(jogador),
                        "O jogador deveria estar na lista da equipa."
                ),

                () -> assertSame(
                        jogador,
                        equipaA.players.get(0),
                        "O jogador armazenado deveria ser o mesmo objeto que foi criado."
                ),

                () -> assertEquals(
                        "Jogador Teste",
                        jogador.name,
                        "O nome do jogador não foi guardado corretamente."
                ),

                () -> assertEquals(
                        10,
                        jogador.number,
                        "O número do jogador não foi guardado corretamente."
                ),

                () -> assertEquals(
                        "Avançado",
                        jogador.position,
                        "A posição do jogador não foi guardada corretamente."
                )
        );
    }

    @Test
    public void testDetetarNumeroDuplicadoNaMesmaEquipa() {
        TorneioApp.Player primeiroJogador =
                new TorneioApp.Player(
                        store.nextId(),
                        "Primeiro Jogador",
                        7,
                        "Médio",
                        ""
                );

        equipaA.players.add(primeiroJogador);

        assertTrue(
                store.playerNumberExists(
                        equipaA,
                        7,
                        null
                ),
                "O Store deveria detetar que o número 7 já existe nesta equipa."
        );
    }

    @Test
    public void testPermitirMesmoNumeroEmEquipasDiferentes() {
        TorneioApp.Player jogadorEquipaA =
                new TorneioApp.Player(
                        store.nextId(),
                        "Jogador da Equipa A",
                        9,
                        "Avançado",
                        ""
                );

        equipaA.players.add(jogadorEquipaA);

        assertFalse(
                store.playerNumberExists(
                        equipaB,
                        9,
                        null
                ),
                "O número utilizado noutra equipa não deveria ser considerado duplicado."
        );

        TorneioApp.Player jogadorEquipaB =
                new TorneioApp.Player(
                        store.nextId(),
                        "Jogador da Equipa B",
                        9,
                        "Avançado",
                        ""
                );

        equipaB.players.add(jogadorEquipaB);

        assertAll(
                () -> assertEquals(
                        9,
                        equipaA.players.get(0).number
                ),

                () -> assertEquals(
                        9,
                        equipaB.players.get(0).number
                ),

                () -> assertNotSame(
                        jogadorEquipaA,
                        jogadorEquipaB,
                        "Os jogadores devem ser objetos diferentes."
                )
        );
    }

    @Test
    public void testIgnorarProprioJogadorDuranteEdicao() {
        TorneioApp.Player jogador =
                new TorneioApp.Player(
                        store.nextId(),
                        "Jogador em Edição",
                        21,
                        "Defesa",
                        ""
                );

        equipaA.players.add(jogador);

        assertFalse(
                store.playerNumberExists(
                        equipaA,
                        21,
                        jogador
                ),
                "O próprio jogador não deveria ser considerado um número duplicado durante a edição."
        );
    }

    @Test
    public void testDetetarNumeroDeOutroJogadorDuranteEdicao() {
        TorneioApp.Player jogadorExistente =
                new TorneioApp.Player(
                        store.nextId(),
                        "Jogador Existente",
                        15,
                        "Defesa",
                        ""
                );

        TorneioApp.Player jogadorEmEdicao =
                new TorneioApp.Player(
                        store.nextId(),
                        "Jogador em Edição",
                        20,
                        "Médio",
                        ""
                );

        equipaA.players.add(jogadorExistente);
        equipaA.players.add(jogadorEmEdicao);

        assertTrue(
                store.playerNumberExists(
                        equipaA,
                        15,
                        jogadorEmEdicao
                ),
                "O número de outro jogador deveria ser detetado como duplicado."
        );
    }

    @Test
    public void testRemoverJogadorComSucesso() {
        TorneioApp.Player jogador =
                new TorneioApp.Player(
                        store.nextId(),
                        "Jogador para Remover",
                        30,
                        "Guarda-Redes",
                        ""
                );

        equipaA.players.add(jogador);

        boolean removido =
                equipaA.players.remove(jogador);

        assertAll(
                () -> assertTrue(
                        removido,
                        "A remoção deveria devolver true."
                ),

                () -> assertFalse(
                        equipaA.players.contains(jogador),
                        "O jogador não deveria continuar na equipa."
                ),

                () -> assertTrue(
                        equipaA.players.isEmpty(),
                        "A lista de jogadores deveria ficar vazia."
                )
        );
    }


}
