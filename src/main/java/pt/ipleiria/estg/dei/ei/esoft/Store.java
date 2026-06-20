
import java.util.ArrayList;
import java.util.List;

public class Store {

    private static Store instance;

    public TorneioApp.Tournament tournament;

    public final List<TorneioApp.Team> teams = new ArrayList<>();
    public final List<Estadio> stadiums = new ArrayList<>();
    public final List<TorneioApp.Game> games = new ArrayList<>();
    public final List<TorneioApp.TicketBatch> tickets = new ArrayList<>();
    public final List<TorneioApp.SoldTicket> soldTickets = new ArrayList<>();
    public final List<Patrocinio> patrocinios = new ArrayList<>();

    /*
     * Guarda os golos e cartões de cada jogador
     * num determinado jogo.
     */
    public final List<EstatisticaJogadorJogo> playerGameStats =
            new ArrayList<>();

    public boolean calendarGenerated = false;

    private int sequence = 1;

    private Store() {
        inicializarDadosMock();
    }

    public static Store getInstance() {
        if (instance == null) {
            instance = new Store();
        }

        return instance;
    }

    public int nextId() {
        return sequence++;
    }

    // =========================================================
    // PESQUISAS
    // =========================================================

    public TorneioApp.Team findTeam(int id) {
        return teams.stream()
                .filter(team -> team.id == id)
                .findFirst()
                .orElse(null);
    }

    public TorneioApp.Team findTeamByName(String name) {
        if (name == null) {
            return null;
        }

        return teams.stream()
                .filter(team ->
                        team.name.equalsIgnoreCase(name.trim())
                )
                .findFirst()
                .orElse(null);
    }

    public Estadio findStadium(int id) {
        return stadiums.stream()
                .filter(stadium -> stadium.id == id)
                .findFirst()
                .orElse(null);
    }

    public TorneioApp.Game findGame(int id) {
        return games.stream()
                .filter(game -> game.id == id)
                .findFirst()
                .orElse(null);
    }

    public TorneioApp.TicketBatch findTicket(int id) {
        return tickets.stream()
                .filter(ticket -> ticket.id == id)
                .findFirst()
                .orElse(null);
    }

    public Patrocinio findSponsor(int id) {
        return patrocinios.stream()
                .filter(sponsor -> sponsor.id == id)
                .findFirst()
                .orElse(null);
    }

    // =========================================================
    // VALIDAÇÕES DE EQUIPAS E JOGADORES
    // =========================================================

    public boolean teamNameExists(
            String name,
            TorneioApp.Team ignoredTeam
    ) {
        return teams.stream()
                .anyMatch(team ->
                        team != ignoredTeam &&
                                team.name.equalsIgnoreCase(name.trim())
                );
    }

    public boolean teamAcronymExists(
            String acronym,
            TorneioApp.Team ignoredTeam
    ) {
        return teams.stream()
                .anyMatch(team ->
                        team != ignoredTeam &&
                                team.acronym.equalsIgnoreCase(acronym.trim())
                );
    }

    public boolean playerNumberExists(
            TorneioApp.Team team,
            int number,
            TorneioApp.Player ignoredPlayer
    ) {
        if (team == null) {
            return false;
        }

        return team.players.stream()
                .anyMatch(player ->
                        player != ignoredPlayer &&
                                player.number == number
                );
    }

    // =========================================================
    // ESTATÍSTICAS DOS JOGADORES POR JOGO
    // =========================================================

    public EstatisticaJogadorJogo findOrCreatePlayerGameStats(
            TorneioApp.Game game,
            TorneioApp.Team team,
            TorneioApp.Player player
    ) {
        EstatisticaJogadorJogo existingStat =
                playerGameStats.stream()
                        .filter(stat ->
                                stat.game == game &&
                                        stat.player == player
                        )
                        .findFirst()
                        .orElse(null);

        if (existingStat != null) {
            return existingStat;
        }

        EstatisticaJogadorJogo newStat =
                new EstatisticaJogadorJogo(
                        game,
                        team,
                        player
                );

        playerGameStats.add(newStat);

        return newStat;
    }

    public List<EstatisticaJogadorJogo> findPlayerStatsByGame(
            TorneioApp.Game game
    ) {
        List<EstatisticaJogadorJogo> result =
                new ArrayList<>();

        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.game == game) {
                result.add(stat);
            }
        }

        return result;
    }

    public List<EstatisticaJogadorJogo> findPlayerStats(
            TorneioApp.Player player
    ) {
        List<EstatisticaJogadorJogo> result =
                new ArrayList<>();

        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.player == player) {
                result.add(stat);
            }
        }

        return result;
    }

    public void recalculateGameTotals(
            TorneioApp.Game game
    ) {
        if (game == null) {
            return;
        }

        game.goalsA = 0;
        game.goalsB = 0;

        game.yellowA = 0;
        game.yellowB = 0;

        game.redA = 0;
        game.redB = 0;

        TorneioApp.Team teamA =
                findTeamByName(game.teamA);

        TorneioApp.Team teamB =
                findTeamByName(game.teamB);

        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.game != game) {
                continue;
            }

            if (stat.team == teamA) {
                game.goalsA += stat.goals;
                game.yellowA += stat.yellowCards;
                game.redA += stat.redCards;
            } else if (stat.team == teamB) {
                game.goalsB += stat.goals;
                game.yellowB += stat.yellowCards;
                game.redB += stat.redCards;
            }
        }
    }

    public void removePlayerGameStats(
            TorneioApp.Player player
    ) {
        playerGameStats.removeIf(stat ->
                stat.player == player
        );
    }

    public void removeGameStats(
            TorneioApp.Game game
    ) {
        playerGameStats.removeIf(stat ->
                stat.game == game
        );
    }

    // =========================================================
    // DADOS INICIAIS
    // =========================================================

    private void inicializarDadosMock() {

        /*
         * O torneio não é criado automaticamente.
         * O utilizador poderá criá-lo através da interface.
         */
        this.tournament = new TorneioApp.Tournament(
                "Torneio ESoft 2026",
                "10/06/2026",
                "30/06/2026",
                2
        );

        this.tournament.state = "em curso";
        this.calendarGenerated = true;

        // =====================================================
        // ESTÁDIOS
        // =====================================================

        Estadio luz = new Estadio(
                nextId(),
                "Estádio da Luz",
                "Lisboa",
                65000
        );

        luz.bancadas.add(
                new Bancada(
                        nextId(),
                        "Bancada Sagres",
                        30000
                )
        );

        stadiums.add(luz);

        Estadio dragao = new Estadio(
                nextId(),
                "Estádio do Dragão",
                "Porto",
                50000
        );

        dragao.bancadas.add(
                new Bancada(
                        nextId(),
                        "Bancada Norte",
                        25000
                )
        );

        stadiums.add(dragao);

        Estadio alvalade = new Estadio(
                nextId(),
                "Estádio José Alvalade",
                "Lisboa",
                50000
        );

        alvalade.bancadas.add(
                new Bancada(
                        nextId(),
                        "Bancada Sul",
                        25000
                )
        );

        stadiums.add(alvalade);

        Estadio pedreira = new Estadio(
                nextId(),
                "Estádio Municipal",
                "Braga",
                30000
        );

        pedreira.bancadas.add(
                new Bancada(
                        nextId(),
                        "Bancada Nascente",
                        15000
                )
        );

        stadiums.add(pedreira);

        // =====================================================
        // EQUIPAS
        // =====================================================

        TorneioApp.Team benfica =
                new TorneioApp.Team(
                        nextId(),
                        "Benfica",
                        "SLB",
                        "Roger Schmidt",
                        "Vermelho",
                        "Preto",
                        "🦅"
                );

        TorneioApp.Team porto =
                new TorneioApp.Team(
                        nextId(),
                        "Porto",
                        "FCP",
                        "Sérgio Conceição",
                        "Azul",
                        "Amarelo",
                        "🐉"
                );

        TorneioApp.Team sporting =
                new TorneioApp.Team(
                        nextId(),
                        "Sporting",
                        "SCP",
                        "Rúben Amorim",
                        "Verde",
                        "Preto",
                        "🦁"
                );

        TorneioApp.Team braga =
                new TorneioApp.Team(
                        nextId(),
                        "Sp. Braga",
                        "SCB",
                        "Artur Jorge",
                        "Vermelho",
                        "Branco",
                        "⚔️"
                );

        TorneioApp.Team vitoria =
                new TorneioApp.Team(
                        nextId(),
                        "Vitória SC",
                        "VSC",
                        "Álvaro Pacheco",
                        "Branco",
                        "Preto",
                        "🛡️"
                );

        TorneioApp.Team boavista =
                new TorneioApp.Team(
                        nextId(),
                        "Boavista",
                        "BFC",
                        "Petit",
                        "Xadrez",
                        "Preto",
                        "🐆"
                );

        TorneioApp.Team famalicao =
                new TorneioApp.Team(
                        nextId(),
                        "Famalicão",
                        "FCF",
                        "João Pedro",
                        "Branco",
                        "Azul",
                        "⚽"
                );

        TorneioApp.Team gilVicente =
                new TorneioApp.Team(
                        nextId(),
                        "Gil Vicente",
                        "GVC",
                        "Vítor Campelos",
                        "Vermelho",
                        "Azul",
                        "🐓"
                );

        teams.addAll(
                List.of(
                        benfica,
                        porto,
                        sporting,
                        braga,
                        vitoria,
                        boavista,
                        famalicao,
                        gilVicente
                )
        );

        // =====================================================
        // JOGADORES DO BENFICA
        // =====================================================

        adicionarJogador(
                benfica,
                "Anatoliy Trubin",
                1,
                "Guarda-Redes"
        );

        adicionarJogador(
                benfica,
                "António Silva",
                4,
                "Defesa"
        );

        adicionarJogador(
                benfica,
                "João Neves",
                87,
                "Médio"
        );

        adicionarJogador(
                benfica,
                "Rafa Silva",
                27,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO PORTO
        // =====================================================

        adicionarJogador(
                porto,
                "Diogo Costa",
                99,
                "Guarda-Redes"
        );

        adicionarJogador(
                porto,
                "Pepe",
                3,
                "Defesa"
        );

        adicionarJogador(
                porto,
                "Alan Varela",
                22,
                "Médio"
        );

        adicionarJogador(
                porto,
                "Evanilson",
                30,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO SPORTING
        // =====================================================

        adicionarJogador(
                sporting,
                "Antonio Adán",
                1,
                "Guarda-Redes"
        );

        adicionarJogador(
                sporting,
                "Gonçalo Inácio",
                25,
                "Defesa"
        );

        adicionarJogador(
                sporting,
                "Pedro Gonçalves",
                8,
                "Médio"
        );

        adicionarJogador(
                sporting,
                "Viktor Gyökeres",
                9,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO BRAGA
        // =====================================================

        adicionarJogador(
                braga,
                "Matheus",
                1,
                "Guarda-Redes"
        );

        adicionarJogador(
                braga,
                "José Fonte",
                6,
                "Defesa"
        );

        adicionarJogador(
                braga,
                "João Moutinho",
                28,
                "Médio"
        );

        adicionarJogador(
                braga,
                "Ricardo Horta",
                21,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO VITÓRIA SC
        // =====================================================

        adicionarJogador(
                vitoria,
                "Bruno Varela",
                14,
                "Guarda-Redes"
        );

        adicionarJogador(
                vitoria,
                "Toni Borevkovic",
                24,
                "Defesa"
        );

        adicionarJogador(
                vitoria,
                "André André",
                21,
                "Médio"
        );

        adicionarJogador(
                vitoria,
                "Jota Silva",
                11,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO BOAVISTA
        // =====================================================

        adicionarJogador(
                boavista,
                "João Gonçalves",
                99,
                "Guarda-Redes"
        );

        adicionarJogador(
                boavista,
                "Rodrigo Abascal",
                26,
                "Defesa"
        );

        adicionarJogador(
                boavista,
                "Sebastián Pérez",
                24,
                "Médio"
        );

        adicionarJogador(
                boavista,
                "Róbert Boženík",
                9,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO FAMALICÃO
        // =====================================================

        adicionarJogador(
                famalicao,
                "Luiz Júnior",
                31,
                "Guarda-Redes"
        );

        adicionarJogador(
                famalicao,
                "Riccieli",
                15,
                "Defesa"
        );

        adicionarJogador(
                famalicao,
                "Zaydou Youssouf",
                28,
                "Médio"
        );

        adicionarJogador(
                famalicao,
                "Jhonder Cádiz",
                29,
                "Avançado"
        );

        // =====================================================
        // JOGADORES DO GIL VICENTE
        // =====================================================

        adicionarJogador(
                gilVicente,
                "Andrew",
                42,
                "Guarda-Redes"
        );

        adicionarJogador(
                gilVicente,
                "Gabriel Pereira",
                13,
                "Defesa"
        );

        adicionarJogador(
                gilVicente,
                "Kanya Fujimoto",
                10,
                "Médio"
        );

        adicionarJogador(
                gilVicente,
                "Depú",
                9,
                "Avançado"
        );

        // =====================================================
        // PATROCÍNIOS
        // =====================================================

        patrocinios.add(
                new Patrocinio(
                        nextId(),
                        "Sagres",
                        "Sponsor de Bebidas",
                        50000.00
                )
        );

        patrocinios.add(
                new Patrocinio(
                        nextId(),
                        "Betano",
                        "Sponsor Principal",
                        120000.00
                )
        );

        TorneioApp.Game q1 = new TorneioApp.Game(
                nextId(),
                "Quartos de Final",
                "Benfica",
                "Sp. Braga",
                "10/06/2026 18:00",
                luz
        );

        TorneioApp.Game q2 = new TorneioApp.Game(
                nextId(),
                "Quartos de Final",
                "Porto",
                "Vitória SC",
                "11/06/2026 20:00",
                dragao
        );

        TorneioApp.Game q3 = new TorneioApp.Game(
                nextId(),
                "Quartos de Final",
                "Sporting",
                "Famalicão",
                "12/06/2026 18:00",
                alvalade
        );

        TorneioApp.Game q4 = new TorneioApp.Game(
                nextId(),
                "Quartos de Final",
                "Boavista",
                "Gil Vicente",
                "13/06/2026 20:00",
                pedreira
        );

        TorneioApp.Game s1 = new TorneioApp.Game(
                nextId(),
                "Semifinais",
                "A determinar",
                "A determinar",
                "20/06/2026 18:00",
                luz
        );

        TorneioApp.Game s2 = new TorneioApp.Game(
                nextId(),
                "Semifinais",
                "A determinar",
                "A determinar",
                "21/06/2026 20:00",
                dragao
        );

        games.add(q1);
        games.add(q2);
        games.add(q3);
        games.add(q4);
        games.add(s1);
        games.add(s2);

        /*
         * Os jogos e bilhetes continuam vazios.
         * Serão criados através da calendarização.
         */
    }



    private void adicionarJogador(
            TorneioApp.Team team,
            String name,
            int number,
            String position
    ) {
        team.players.add(
                new TorneioApp.Player(
                        nextId(),
                        name,
                        number,
                        position,
                        ""
                )
        );
    }
}

