import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EquipaTestCase {


    private Store store;
    private TorneioApp.Team equipaCriada;

    @BeforeEach
    public void setUp() {
        store = Store.getInstance();
        equipaCriada = null;
    }

    @AfterEach
    public void tearDown() {
        if (equipaCriada != null) {
            store.teams.remove(equipaCriada);
        }
    }

    @Test
    public void testCriarEquipaComSucesso() {
        int numeroEquipasAntes = store.teams.size();

        equipaCriada = new TorneioApp.Team(
                store.nextId(),
                "Equipa Teste",
                "ETU",
                "Treinador Teste",
                "Vermelho",
                "Branco",
                ""
        );

        store.teams.add(equipaCriada);

        assertAll(
                () -> assertEquals(
                        numeroEquipasAntes + 1,
                        store.teams.size(),
                        "A quantidade de equipas deveria aumentar em uma."
                ),

                () -> assertTrue(
                        store.teams.contains(equipaCriada),
                        "A equipa criada deveria estar presente no Store."
                ),

                () -> assertSame(
                        equipaCriada,
                        store.findTeam(equipaCriada.id),
                        "A equipa encontrada deveria ser a mesma que foi criada."
                ),

                () -> assertEquals(
                        "Equipa Teste",
                        equipaCriada.name,
                        "O nome da equipa não foi guardado corretamente."
                ),

                () -> assertEquals(
                        "ETU",
                        equipaCriada.acronym,
                        "A sigla da equipa não foi guardada corretamente."
                ),

                () -> assertEquals(
                        "Treinador Teste",
                        equipaCriada.coach,
                        "O treinador da equipa não foi guardado corretamente."
                )
        );
    }

    @Test
    public void testDetetarNomeDeEquipaDuplicado() {
        equipaCriada = new TorneioApp.Team(
                store.nextId(),
                "Equipa Duplicada",
                "EDP",
                "Treinador",
                "Azul",
                "Branco",
                ""
        );

        store.teams.add(equipaCriada);

        assertTrue(
                store.teamNameExists(
                        "equipa duplicada",
                        null
                ),
                "O Store deveria detetar um nome de equipa duplicado, independentemente das maiúsculas."
        );
    }

    @Test
    public void testDetetarSiglaDeEquipaDuplicada() {
        equipaCriada = new TorneioApp.Team(
                store.nextId(),
                "Equipa da Sigla",
                "EDS",
                "Treinador",
                "Verde",
                "Preto",
                ""
        );

        store.teams.add(equipaCriada);

        assertTrue(
                store.teamAcronymExists(
                        "eds",
                        null
                ),
                "O Store deveria detetar uma sigla duplicada, independentemente das maiúsculas."
        );
    }

    @Test
    public void testIgnorarPropriaEquipaDuranteEdicao() {
        equipaCriada = new TorneioApp.Team(
                store.nextId(),
                "Equipa Editada",
                "EED",
                "Treinador",
                "Amarelo",
                "Azul",
                ""
        );

        store.teams.add(equipaCriada);

        assertFalse(
                store.teamNameExists(
                        equipaCriada.name,
                        equipaCriada
                ),
                "A própria equipa não deveria ser considerada um nome duplicado durante a edição."
        );

        assertFalse(
                store.teamAcronymExists(
                        equipaCriada.acronym,
                        equipaCriada
                ),
                "A própria equipa não deveria ser considerada uma sigla duplicada durante a edição."
        );
    }

    @Test
    public void testRemoverEquipaComSucesso() {
        equipaCriada = new TorneioApp.Team(
                store.nextId(),
                "Equipa para Remover",
                "EPR",
                "Treinador",
                "Preto",
                "Branco",
                ""
        );

        store.teams.add(equipaCriada);

        assertTrue(
                store.teams.contains(equipaCriada),
                "A equipa deveria existir antes de ser removida."
        );

        boolean removida = store.teams.remove(equipaCriada);

        assertAll(
                () -> assertTrue(
                        removida,
                        "A remoção da equipa deveria devolver true."
                ),

                () -> assertFalse(
                        store.teams.contains(equipaCriada),
                        "A equipa não deveria continuar no Store depois da remoção."
                ),

                () -> assertNull(
                        store.findTeam(equipaCriada.id),
                        "A equipa removida não deveria ser encontrada pelo seu ID."
                )
        );

        equipaCriada = null;
    }


}
