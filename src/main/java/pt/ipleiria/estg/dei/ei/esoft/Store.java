import java.util.ArrayList;
import java.util.List;

public class Store {

    private static Store instance;

    public Torneio tournament;

    // Agora usa a nova classe Equipa
    public final List<Equipa> teams = new ArrayList<>();
    public final List<Estadio> stadiums = new ArrayList<>();
    public final List<Jogo> games = new ArrayList<>();
    public final List<LoteBilhetes> tickets = new ArrayList<>();
    public final List<BilheteVendido> soldTickets = new ArrayList<>();
    public final List<Patrocinio> patrocinios = new ArrayList<>();

    /* Mantido para compatibilidade com o código anterior. */
    public final List<EstatisticaJogadorJogo> playerGameStats = new ArrayList<>();

    /* Novo: histórico dos acontecimentos registados durante cada jogo. */
    public final List<EventoJogo> gameEvents = new ArrayList<>();

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

    public Equipa findTeam(int id) {
        return teams.stream()
                .filter(team -> team.id == id)
                .findFirst()
                .orElse(null);
    }

    public Equipa findTeamByName(String name) {
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

    public Jogo findGame(int id) {
        return games.stream()
                .filter(game -> game.id == id)
                .findFirst()
                .orElse(null);
    }

    public LoteBilhetes findTicket(int id) {
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
            Equipa ignoredTeam
    ) {
        return teams.stream()
                .anyMatch(team ->
                        team != ignoredTeam &&
                                team.name.equalsIgnoreCase(name.trim())
                );
    }

    public boolean teamAcronymExists(
            String acronym,
            Equipa ignoredTeam
    ) {
        return teams.stream()
                .anyMatch(team ->
                        team != ignoredTeam &&
                                team.acronym.equalsIgnoreCase(acronym.trim())
                );
    }

    public boolean playerNumberExists(
            Equipa team,
            int number,
            Jogador ignoredPlayer
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
            Jogo game,
            Equipa team,
            Jogador player
    ) {
        EstatisticaJogadorJogo existingStat = playerGameStats.stream()
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
                new EstatisticaJogadorJogo(game, team, player);

        playerGameStats.add(newStat);
        return newStat;
    }

    public List<EstatisticaJogadorJogo> findPlayerStatsByGame(
            Jogo game
    ) {
        List<EstatisticaJogadorJogo> result = new ArrayList<>();

        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.game == game) {
                result.add(stat);
            }
        }

        return result;
    }

    public List<EstatisticaJogadorJogo> findPlayerStats(
            Jogador player
    ) {
        List<EstatisticaJogadorJogo> result = new ArrayList<>();

        for (EstatisticaJogadorJogo stat : playerGameStats) {
            if (stat.player == player) {
                result.add(stat);
            }
        }

        return result;
    }

    public void removePlayerGameStats(Jogador player) {
        playerGameStats.removeIf(stat -> stat.player == player);
    }

    public void removeGameStats(Jogo game) {
        playerGameStats.removeIf(stat -> stat.game == game);
    }

    // =========================================================
    // ACONTECIMENTOS REGISTADOS DURANTE O JOGO
    // =========================================================

    public void addGameEvent(EventoJogo event) {
        if (event == null) {
            return;
        }

        gameEvents.add(event);
        recalculateGameTotals(event.game);
    }

    public List<EventoJogo> findEventsByGame(
            Jogo game
    ) {
        List<EventoJogo> result = new ArrayList<>();

        for (EventoJogo event : gameEvents) {
            if (event.game == game) {
                result.add(event);
            }
        }

        result.sort(
                (first, second) ->
                        Integer.compare(first.minute, second.minute)
        );

        return result;
    }

    public void removeEventsByGame(Jogo game) {
        gameEvents.removeIf(event -> event.game == game);
        recalculateGameTotals(game);
    }

    public void removePlayerEvents(Jogador player) {
        gameEvents.removeIf(event -> event.player == player);
    }

    public void recalculateGameTotals(Jogo game) {
        if (game == null) {
            return;
        }

        game.goalsA = 0;
        game.goalsB = 0;
        game.yellowA = 0;
        game.yellowB = 0;
        game.redA = 0;
        game.redB = 0;
        game.foulsA = 0;
        game.foulsB = 0;
        game.cornersA = 0;
        game.cornersB = 0;
        game.shotsA = 0;
        game.shotsB = 0;
        game.offsidesA = 0;
        game.offsidesB = 0;

        boolean hasGameEvents = gameEvents.stream()
                .anyMatch(event -> event.game == game);

        if (hasGameEvents) {
            for (EventoJogo event : gameEvents) {
                if (
                        event.game != game ||
                                event.team == null ||
                                event.type == null
                ) {
                    continue;
                }

                boolean belongsToTeamA =
                        event.team.name.equalsIgnoreCase(game.teamA);

                boolean belongsToTeamB =
                        event.team.name.equalsIgnoreCase(game.teamB);

                if (!belongsToTeamA && !belongsToTeamB) {
                    continue;
                }

                switch (event.type) {
                    case GOLO:
                        if (belongsToTeamA) {
                            game.goalsA++;
                        } else {
                            game.goalsB++;
                        }
                        break;

                    case CARTAO_AMARELO:
                        if (belongsToTeamA) {
                            game.yellowA++;
                        } else {
                            game.yellowB++;
                        }
                        break;

                    case CARTAO_VERMELHO:
                        if (belongsToTeamA) {
                            game.redA++;
                        } else {
                            game.redB++;
                        }
                        break;

                    case FALTA:
                        if (belongsToTeamA) {
                            game.foulsA++;
                        } else {
                            game.foulsB++;
                        }
                        break;

                    case CANTO:
                        if (belongsToTeamA) {
                            game.cornersA++;
                        } else {
                            game.cornersB++;
                        }
                        break;

                    case REMATE:
                        if (belongsToTeamA) {
                            game.shotsA++;
                        } else {
                            game.shotsB++;
                        }
                        break;

                    case FORA_DE_JOGO:
                        if (belongsToTeamA) {
                            game.offsidesA++;
                        } else {
                            game.offsidesB++;
                        }
                        break;
                }
            }

            return;
        }

        Equipa teamA = findTeamByName(game.teamA);
        Equipa teamB = findTeamByName(game.teamB);

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

    // =========================================================
    // DADOS INICIAIS
    // =========================================================

    private void inicializarDadosMock() {

        this.tournament = new Torneio(
                "Torneio ESoft 2026",
                "10/06/2026",
                "30/06/2026",
                2
        );

        this.tournament.state = "em preparação";
        this.calendarGenerated = false;

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
        // 16 EQUIPAS
        // =====================================================

        Equipa benfica = new Equipa(
                nextId(), "Benfica", "SLB", "Roger Schmidt",
                "Vermelho", "Preto", "🦅"
        );

        Equipa porto = new Equipa(
                nextId(), "Porto", "FCP", "Sérgio Conceição",
                "Azul", "Amarelo", "🐉"
        );

        Equipa sporting = new Equipa(
                nextId(), "Sporting", "SCP", "Rúben Amorim",
                "Verde", "Preto", "🦁"
        );

        Equipa braga = new Equipa(
                nextId(), "Sp. Braga", "SCB", "Artur Jorge",
                "Vermelho", "Branco", "⚔️"
        );

        Equipa vitoria = new Equipa(
                nextId(), "Vitória SC", "VSC", "Álvaro Pacheco",
                "Branco", "Preto", "🛡️"
        );

        Equipa boavista = new Equipa(
                nextId(), "Boavista", "BFC", "Petit",
                "Xadrez", "Preto", "🐆"
        );

        Equipa famalicao = new Equipa(
                nextId(), "Famalicão", "FCF", "João Pedro",
                "Branco", "Azul", "⚽"
        );

        Equipa gilVicente = new Equipa(
                nextId(), "Gil Vicente", "GVC", "Vítor Campelos",
                "Vermelho", "Azul", "🐓"
        );

        Equipa arouca = new Equipa(
                nextId(), "Arouca", "FCA", "Daniel Sousa",
                "Amarelo", "Azul", "🟡"
        );

        Equipa rioAve = new Equipa(
                nextId(), "Rio Ave", "RAFC", "Luís Freire",
                "Verde", "Branco", "⛵"
        );

        Equipa estoril = new Equipa(
                nextId(), "Estoril Praia", "GDEP", "Vasco Seabra",
                "Amarelo", "Azul", "🏖️"
        );

        Equipa farense = new Equipa(
                nextId(), "Farense", "SCF", "José Mota",
                "Preto", "Branco", "🦁"
        );

        Equipa portimonense = new Equipa(
                nextId(), "Portimonense", "PSC", "Paulo Sérgio",
                "Preto", "Amarelo", "🦅"
        );

        Equipa casaPia = new Equipa(
                nextId(), "Casa Pia", "CPAC", "Gonçalo Santos",
                "Preto", "Branco", "🦆"
        );

        Equipa chaves = new Equipa(
                nextId(), "GD Chaves", "GDC", "Moreno",
                "Azul", "Grená", "⚔️"
        );

        Equipa moreirense = new Equipa(
                nextId(), "Moreirense", "MFC", "Rui Borges",
                "Verde", "Branco", "🟢"
        );

        teams.addAll(
                List.of(
                        benfica, porto, sporting, braga, vitoria, boavista, famalicao, gilVicente,
                        arouca, rioAve, estoril, farense, portimonense, casaPia, chaves, moreirense
                )
        );

        // =====================================================
        // JOGADORES REAIS — 4 POR EQUIPA
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
        // PREENCHER CADA EQUIPA ATÉ TER 24 JOGADORES
        // =====================================================

        for (Equipa team : teams) {
            int playersMissing = 24 - team.players.size();
            int shirtNumber = 30;

            for (int i = 0; i < playersMissing; i++) {
                String position;

                if (i < 2) {
                    position = "Guarda-Redes";
                } else if (i < 8) {
                    position = "Defesa";
                } else if (i < 15) {
                    position = "Médio";
                } else {
                    position = "Avançado";
                }

                adicionarJogador(
                        team,
                        "Jogador " +
                                (team.players.size() + 1) +
                                " (" + team.acronym + ")",
                        shirtNumber++,
                        position
                );
            }
        }

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

        // O torneio arranca preparado, mas ainda sem calendário nem jogos.
        games.clear();
        gameEvents.clear();
        playerGameStats.clear();

        calendarGenerated = false;
        tournament.state = "em preparação";
    }

    private void adicionarJogador(
            Equipa team,
            String name,
            int number,
            String position
    ) {
        team.players.add(
                new Jogador(
                        nextId(),
                        name,
                        number,
                        position,
                        ""
                )
        );
    }
}