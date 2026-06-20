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

    public final List<EstatisticaJogadorJogo> playerGameStats = new ArrayList<>();

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
                .filter(team -> team.name.equalsIgnoreCase(name.trim()))
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

    public boolean teamNameExists(String name, TorneioApp.Team ignoredTeam) {
        return teams.stream()
                .anyMatch(team -> team != ignoredTeam && team.name.equalsIgnoreCase(name.trim()));
    }

    public boolean teamAcronymExists(String acronym, TorneioApp.Team ignoredTeam) {
        return teams.stream()
                .anyMatch(team -> team != ignoredTeam && team.acronym.equalsIgnoreCase(acronym.trim()));
    }

    public boolean playerNumberExists(TorneioApp.Team team, int number, TorneioApp.Player ignoredPlayer) {
        if (team == null) return false;
        return team.players.stream()
                .anyMatch(player -> player != ignoredPlayer && player.number == number);
    }

    // =========================================================
    // ESTATÍSTICAS DOS JOGADORES POR JOGO
    // =========================================================

    public EstatisticaJogadorJogo findOrCreatePlayerGameStats(TorneioApp.Game game, TorneioApp.Team team, TorneioApp.Player player) {
        EstatisticaJogadorJogo existingStat = playerGameStats.stream()
                .filter(stat -> stat.game == game && stat.player == player)
                .findFirst()
                .orElse(null);

        if (existingStat != null) return existingStat;

        EstatisticaJogadorJogo newStat = new EstatisticaJogadorJogo(game, team, player);
        playerGameStats.add(newStat);
        return newStat;
    }

    public List<EstatisticaJogadorJogo> findPlayerStatsByGame(TorneioApp.Game game) {
        List<EstatisticaJogadorJogo> result = new ArrayList<>();
        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.game == game) result.add(stat);
        }
        return result;
    }

    public List<EstatisticaJogadorJogo> findPlayerStats(TorneioApp.Player player) {
        List<EstatisticaJogadorJogo> result = new ArrayList<>();
        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.player == player) result.add(stat);
        }
        return result;
    }

    public void recalculateGameTotals(TorneioApp.Game game) {
        if (game == null) return;

        game.goalsA = 0;
        game.goalsB = 0;
        game.yellowA = 0;
        game.yellowB = 0;
        game.redA = 0;
        game.redB = 0;

        TorneioApp.Team teamA = findTeamByName(game.teamA);
        TorneioApp.Team teamB = findTeamByName(game.teamB);

        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.game != game) continue;

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

    public void removePlayerGameStats(TorneioApp.Player player) {
        playerGameStats.removeIf(stat -> stat.player == player);
    }

    public void removeGameStats(TorneioApp.Game game) {
        playerGameStats.removeIf(stat -> stat.game == game);
    }

    // =========================================================
    // DADOS INICIAIS
    // =========================================================

    private void inicializarDadosMock() {

        this.tournament = new TorneioApp.Tournament("Torneio ESoft 2026", "10/06/2026", "30/06/2026", 2);
        this.tournament.state = "em preparação";
        this.calendarGenerated = false;

        // =====================================================
        // ESTÁDIOS
        // =====================================================

        Estadio luz = new Estadio(nextId(), "Estádio da Luz", "Lisboa", 65000);
        luz.bancadas.add(new Bancada(nextId(), "Bancada Sagres", 30000));
        stadiums.add(luz);

        Estadio dragao = new Estadio(nextId(), "Estádio do Dragão", "Porto", 50000);
        dragao.bancadas.add(new Bancada(nextId(), "Bancada Norte", 25000));
        stadiums.add(dragao);

        Estadio alvalade = new Estadio(nextId(), "Estádio José Alvalade", "Lisboa", 50000);
        alvalade.bancadas.add(new Bancada(nextId(), "Bancada Sul", 25000));
        stadiums.add(alvalade);

        Estadio pedreira = new Estadio(nextId(), "Estádio Municipal", "Braga", 30000);
        pedreira.bancadas.add(new Bancada(nextId(), "Bancada Nascente", 15000));
        stadiums.add(pedreira);

        // =====================================================
        // 16 EQUIPAS
        // =====================================================

        TorneioApp.Team benfica = new TorneioApp.Team(nextId(), "Benfica", "SLB", "Roger Schmidt", "Vermelho", "Preto", "🦅");
        TorneioApp.Team porto = new TorneioApp.Team(nextId(), "Porto", "FCP", "Sérgio Conceição", "Azul", "Amarelo", "🐉");
        TorneioApp.Team sporting = new TorneioApp.Team(nextId(), "Sporting", "SCP", "Rúben Amorim", "Verde", "Preto", "🦁");
        TorneioApp.Team braga = new TorneioApp.Team(nextId(), "Sp. Braga", "SCB", "Artur Jorge", "Vermelho", "Branco", "⚔️");
        TorneioApp.Team vitoria = new TorneioApp.Team(nextId(), "Vitória SC", "VSC", "Álvaro Pacheco", "Branco", "Preto", "🛡️");
        TorneioApp.Team boavista = new TorneioApp.Team(nextId(), "Boavista", "BFC", "Petit", "Xadrez", "Preto", "🐆");
        TorneioApp.Team famalicao = new TorneioApp.Team(nextId(), "Famalicão", "FCF", "João Pedro", "Branco", "Azul", "⚽");
        TorneioApp.Team gilVicente = new TorneioApp.Team(nextId(), "Gil Vicente", "GVC", "Vítor Campelos", "Vermelho", "Azul", "🐓");

        TorneioApp.Team arouca = new TorneioApp.Team(nextId(), "Arouca", "FCA", "Daniel Sousa", "Amarelo", "Azul", "🟡");
        TorneioApp.Team rioAve = new TorneioApp.Team(nextId(), "Rio Ave", "RAFC", "Luís Freire", "Verde", "Branco", "⛵");
        TorneioApp.Team estoril = new TorneioApp.Team(nextId(), "Estoril Praia", "GDEP", "Vasco Seabra", "Amarelo", "Azul", "🏖️");
        TorneioApp.Team farense = new TorneioApp.Team(nextId(), "Farense", "SCF", "José Mota", "Preto", "Branco", "🦁");
        TorneioApp.Team portimonense = new TorneioApp.Team(nextId(), "Portimonense", "PSC", "Paulo Sérgio", "Preto", "Amarelo", "🦅");
        TorneioApp.Team casaPia = new TorneioApp.Team(nextId(), "Casa Pia", "CPAC", "Gonçalo Santos", "Preto", "Branco", "🦆");
        TorneioApp.Team chaves = new TorneioApp.Team(nextId(), "GD Chaves", "GDC", "Moreno", "Azul", "Grená", "⚔️");
        TorneioApp.Team moreirense = new TorneioApp.Team(nextId(), "Moreirense", "MFC", "Rui Borges", "Verde", "Branco", "🟢");

        teams.addAll(List.of(
                benfica, porto, sporting, braga, vitoria, boavista, famalicao, gilVicente,
                arouca, rioAve, estoril, farense, portimonense, casaPia, chaves, moreirense
        ));

        // =====================================================
        // JOGADORES REAIS (4 por equipa)
        // =====================================================

        adicionarJogador(benfica, "Anatoliy Trubin", 1, "Guarda-Redes");
        adicionarJogador(benfica, "António Silva", 4, "Defesa");
        adicionarJogador(benfica, "João Neves", 87, "Médio");
        adicionarJogador(benfica, "Rafa Silva", 27, "Avançado");

        adicionarJogador(porto, "Diogo Costa", 99, "Guarda-Redes");
        adicionarJogador(porto, "Pepe", 3, "Defesa");
        adicionarJogador(porto, "Alan Varela", 22, "Médio");
        adicionarJogador(porto, "Evanilson", 30, "Avançado");

        adicionarJogador(sporting, "Antonio Adán", 1, "Guarda-Redes");
        adicionarJogador(sporting, "Gonçalo Inácio", 25, "Defesa");
        adicionarJogador(sporting, "Pedro Gonçalves", 8, "Médio");
        adicionarJogador(sporting, "Viktor Gyökeres", 9, "Avançado");

        adicionarJogador(braga, "Matheus", 1, "Guarda-Redes");
        adicionarJogador(braga, "José Fonte", 6, "Defesa");
        adicionarJogador(braga, "João Moutinho", 28, "Médio");
        adicionarJogador(braga, "Ricardo Horta", 21, "Avançado");

        adicionarJogador(vitoria, "Bruno Varela", 14, "Guarda-Redes");
        adicionarJogador(vitoria, "Toni Borevkovic", 24, "Defesa");
        adicionarJogador(vitoria, "André André", 21, "Médio");
        adicionarJogador(vitoria, "Jota Silva", 11, "Avançado");

        adicionarJogador(boavista, "João Gonçalves", 99, "Guarda-Redes");
        adicionarJogador(boavista, "Rodrigo Abascal", 26, "Defesa");
        adicionarJogador(boavista, "Sebastián Pérez", 24, "Médio");
        adicionarJogador(boavista, "Róbert Boženík", 9, "Avançado");

        adicionarJogador(famalicao, "Luiz Júnior", 31, "Guarda-Redes");
        adicionarJogador(famalicao, "Riccieli", 15, "Defesa");
        adicionarJogador(famalicao, "Zaydou Youssouf", 28, "Médio");
        adicionarJogador(famalicao, "Jhonder Cádiz", 29, "Avançado");

        adicionarJogador(gilVicente, "Andrew", 42, "Guarda-Redes");
        adicionarJogador(gilVicente, "Gabriel Pereira", 13, "Defesa");
        adicionarJogador(gilVicente, "Kanya Fujimoto", 10, "Médio");
        adicionarJogador(gilVicente, "Depú", 9, "Avançado");

        adicionarJogador(arouca, "Arruabarrena", 12, "Guarda-Redes");
        adicionarJogador(arouca, "Javi Montero", 4, "Defesa");
        adicionarJogador(arouca, "David Simão", 5, "Médio");
        adicionarJogador(arouca, "Rafa Mújica", 19, "Avançado");

        adicionarJogador(rioAve, "Jhonatan", 18, "Guarda-Redes");
        adicionarJogador(rioAve, "Aderllan Santos", 33, "Defesa");
        adicionarJogador(rioAve, "Guga", 10, "Médio");
        adicionarJogador(rioAve, "Emmanuel Boateng", 21, "Avançado");

        adicionarJogador(estoril, "Marcelo Carné", 81, "Guarda-Redes");
        adicionarJogador(estoril, "Bernardo Vital", 3, "Defesa");
        adicionarJogador(estoril, "Mateus Fernandes", 82, "Médio");
        adicionarJogador(estoril, "Rodrigo Gomes", 33, "Avançado");

        adicionarJogador(farense, "Ricardo Velho", 33, "Guarda-Redes");
        adicionarJogador(farense, "Zach Muscat", 3, "Defesa");
        adicionarJogador(farense, "Cláudio Falcão", 29, "Médio");
        adicionarJogador(farense, "Bruno Duarte", 9, "Avançado");

        adicionarJogador(portimonense, "Kosuke Nakamura", 32, "Guarda-Redes");
        adicionarJogador(portimonense, "Pedrão", 44, "Defesa");
        adicionarJogador(portimonense, "Carlinhos", 11, "Médio");
        adicionarJogador(portimonense, "Hélio Varela", 10, "Avançado");

        adicionarJogador(casaPia, "Ricardo Batista", 33, "Guarda-Redes");
        adicionarJogador(casaPia, "Fernando Varela", 15, "Defesa");
        adicionarJogador(casaPia, "Neto", 8, "Médio");
        adicionarJogador(casaPia, "Felippe Cardoso", 9, "Avançado");

        adicionarJogador(chaves, "Hugo Souza", 1, "Guarda-Redes");
        adicionarJogador(chaves, "Steven Vitória", 19, "Defesa");
        adicionarJogador(chaves, "Rúben Ribeiro", 10, "Médio");
        adicionarJogador(chaves, "Héctor Hernández", 23, "Avançado");

        adicionarJogador(moreirense, "Kewin Silva", 40, "Guarda-Redes");
        adicionarJogador(moreirense, "Maracás", 44, "Defesa");
        adicionarJogador(moreirense, "Alan", 11, "Médio");
        adicionarJogador(moreirense, "André Luís", 9, "Avançado");

        // =====================================================
        // ALGORITMO: PREENCHER CADA EQUIPA ATÉ TER 24 JOGADORES
        // =====================================================
        for (TorneioApp.Team t : teams) {
            int numJogadoresFalta = 24 - t.players.size();
            int numeroCamisolaBase = 30; // Garante números únicos

            for (int i = 0; i < numJogadoresFalta; i++) {
                String posicao;
                // Distribui realisticamente as posições pelo plantel
                if (i < 2) posicao = "Guarda-Redes";
                else if (i < 8) posicao = "Defesa";
                else if (i < 15) posicao = "Médio";
                else posicao = "Avançado";

                adicionarJogador(t, "Jogador " + (t.players.size() + 1) + " (" + t.acronym + ")", numeroCamisolaBase++, posicao);
            }
        }

        // =====================================================
        // PATROCÍNIOS
        // =====================================================

        patrocinios.add(new Patrocinio(nextId(), "Sagres", "Sponsor de Bebidas", 50000.00));
        patrocinios.add(new Patrocinio(nextId(), "Betano", "Sponsor Principal", 120000.00));

    }

    private void adicionarJogador(TorneioApp.Team team, String name, int number, String position) {
        team.players.add(new TorneioApp.Player(nextId(), name, number, position, ""));
    }
}