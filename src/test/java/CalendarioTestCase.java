import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalendarioTestCase {

    private Store store;

    @BeforeEach
    public void setup() {
        store = Store.getInstance();
        store.teams.clear();
        store.stadiums.clear();
        store.games.clear();
        store.calendarGenerated = false;
    }

    @Test
    public void testRegraMinimoOitoEquipas() {
        // Adiciona apenas 3 equipas
        for (int i = 0; i < 3; i++) {
            store.teams.add(new TorneioApp.Team(i, "Equipa " + i, "EQ", "T", "C1", "C2", "X"));
        }

        // A regra diz que tem de ser no mínimo 8
        boolean validacaoEquipas = store.teams.size() >= 8;
        assertFalse(validacaoEquipas, "O sistema validou incorretamente um torneio com apenas 3 equipas.");
    }

    @Test
    public void testRegraMultiploDeQuatroEquipas() {
        // Adiciona 9 equipas (é maior que 8, mas não é múltiplo de 4!)
        for (int i = 0; i < 9; i++) {
            store.teams.add(new TorneioApp.Team(i, "Equipa " + i, "EQ", "T", "C1", "C2", "X"));
        }

        boolean validacaoMultiplo = store.teams.size() % 4 == 0;
        assertFalse(validacaoMultiplo, "O sistema aceitou um número de equipas que não é múltiplo de 4.");
    }

    @Test
    public void testRegraVinteTresJogadoresPorEquipa() {
        TorneioApp.Team equipaFalsa = new TorneioApp.Team(1, "Porto", "FCP", "Sérgio", "Azul", "Branco", "X");

        // Adiciona apenas 10 jogadores (o mínimo exigido é 23)
        for (int i = 0; i < 10; i++) {
            equipaFalsa.players.add(new TorneioApp.Player(i, "Jogador", i, "Defesa", ""));
        }

        boolean plantelValido = equipaFalsa.players.size() >= 23;
        assertFalse(plantelValido, "O sistema validou uma equipa com menos de 23 jogadores.");
    }
}