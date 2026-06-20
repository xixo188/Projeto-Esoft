
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TorneioApp extends JFrame {

    public final Store store = Store.getInstance();

    public TorneioApp() {
        setTitle("Gestor de Torneios - Protótipo ESoft");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showHome();
    }

    public void setPage(String title, JPanel content) {
        JPanel root = new JPanel(new BorderLayout());

        root.add(menu(), BorderLayout.WEST);

        JPanel page = new JPanel(new BorderLayout());

        page.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        JLabel header = new JLabel(title);

        header.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        header.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        18,
                        0
                )
        );

        page.add(header, BorderLayout.NORTH);
        page.add(content, BorderLayout.CENTER);

        root.add(page, BorderLayout.CENTER);

        setContentPane(root);
        revalidate();
        repaint();
    }

    private JPanel menu() {
        JPanel menu = new JPanel(
                new GridLayout(0, 1, 0, 8)
        );

        menu.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        14,
                        18,
                        14
                )
        );

        menu.setPreferredSize(
                new Dimension(180, 0)
        );

        menu.setBackground(
                new Color(238, 238, 238)
        );

        JButton home = btn("Homepage");
        JButton torneios = btn("Torneio");
        JButton equipas = btn("Equipas");
        JButton estadios = btn("Estádios");
        JButton calendario = btn("Calendário");
        JButton bilhetes = btn("Bilhetes");
        JButton patrocinadores = btn("Patrocínios");
        JButton estatisticas = btn("Estatísticas");
        JButton faturacao = btn("Faturação");

        home.addActionListener(e ->
                showHome()
        );

        torneios.addActionListener(e ->
                TorneioPainelControlador.showTorneioPage(
                        this,
                        store
                )
        );

        equipas.addActionListener(e ->
                EquipaPainelControlador.showTeamsPage(
                        this,
                        store
                )
        );

        estadios.addActionListener(e ->
                showStadiumsPage()
        );

        calendario.addActionListener(e ->
                showCalendarPage()
        );

        bilhetes.addActionListener(e ->
                BilhetePainelControlador.showTicketsPage(
                        this,
                        store
                )
        );

        patrocinadores.addActionListener(e ->
                PatrocinioPainelControlador.showSponsorsPage(
                        this,
                        store
                )
        );

        estatisticas.addActionListener(e ->
                showStatsPage()
        );

        faturacao.addActionListener(e ->
                showBillingPage()
        );

        menu.add(home);
        menu.add(torneios);
        menu.add(equipas);
        menu.add(estadios);
        menu.add(calendario);
        menu.add(bilhetes);
        menu.add(patrocinadores);
        menu.add(estatisticas);
        menu.add(faturacao);

        return menu;
    }

    private JButton btn(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);

        return button;
    }

    private void showHome() {
        JPanel panel = new JPanel(
                new GridLayout(2, 2, 18, 18)
        );

        panel.add(
                card(
                        "Equipas",
                        "Criar, editar, visualizar e remover equipas e jogadores."
                )
        );

        panel.add(
                card(
                        "Estádios",
                        "Criar, editar, visualizar e remover estádios e bancadas."
                )
        );

        panel.add(
                card(
                        "Bilhetes",
                        "Criar bilhetes por jogo e bancada e simular compras."
                )
        );

        panel.add(
                card(
                        "Calendário",
                        "Gerar jogos e gerir os estados e dados dos jogos."
                )
        );

        setPage("Homepage", panel);
    }

    private JPanel card(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.LIGHT_GRAY
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        JTextArea descriptionArea =
                new JTextArea(description);

        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionArea, BorderLayout.CENTER);

        return card;
    }

    public void showStadiumsPage() {
        EstadioPainelControlador.showStadiumsPage(
                this,
                store
        );
    }

    private void showCalendarPage() {
        Calendario.showCalendario(
                this,
                store
        );
    }

    public void generateCalendar() {
        for (Game g : store.games) {
            if (g.state == GameState.EM_CURSO || g.state == GameState.CONCLUIDO) {
                error("Erro: Não podes gerar um novo calendário porque o torneio já começou!");
                return;
            }
        }

        if (store.teams.size() < 8 || store.teams.size() % 4 != 0) {
            error("Erro: O torneio tem de ter no mínimo 8 equipas e ser múltiplo de 4.");
            return;
        }
        for (Team t : store.teams) {
            if (t.players.size() < 23) {
                error("Erro: A equipa " + t.name + " precisa de pelo menos 23 jogadores.");
                return;
            }
        }
        if (store.stadiums.isEmpty()) {
            error("Erro: Não existem estádios criados.");
            return;
        }

        store.games.clear();

        int numGrupos = store.teams.size() / 4;
        java.util.Random rand = new java.util.Random();
        int restDays = (store.tournament != null) ? store.tournament.restDays : 2;
        int gapEntreRondas = restDays + 1;

        // Sorteia as equipas e SALVA essa nova ordem na base de dados para os grupos ficarem corretos na tabela
        java.util.List<Team> equipasSorteadas = new java.util.ArrayList<>(store.teams);
        java.util.Collections.shuffle(equipasSorteadas, rand);
        store.teams.clear();
        store.teams.addAll(equipasSorteadas);

        java.util.Map<String, java.util.List<Estadio>> estadiosLivresPorDia = new java.util.HashMap<>();
        int maxDiasAcumulados = 0;

        // --- 1. GERAR FASE DE GRUPOS ---
        for (int i = 0; i < numGrupos; i++) {
            int startIndex = i * 4;
            java.util.List<Team> grupo = store.teams.subList(startIndex, startIndex + 4);

            int[][] confrontos = {{0, 1, 2, 3}, {0, 2, 1, 3}, {0, 3, 1, 2}};
            int grupoOffsetDias = i * 1;

            for (int ronda = 0; ronda < 3; ronda++) {
                int diasAcumulados = grupoOffsetDias + (ronda * gapEntreRondas);
                if (diasAcumulados > maxDiasAcumulados) maxDiasAcumulados = diasAcumulados;

                String dataBase = addDays(store.tournament.startDate, diasAcumulados);
                estadiosLivresPorDia.putIfAbsent(dataBase, new java.util.ArrayList<>(store.stadiums));
                java.util.List<Estadio> livresHoje = estadiosLivresPorDia.get(dataBase);

                if (livresHoje.size() < 2) {
                    error("Erro: Não há estádios suficientes para gerar os jogos da Fase de Grupos.");
                    return;
                }

                Team t1 = grupo.get(confrontos[ronda][0]);
                Team t2 = grupo.get(confrontos[ronda][1]);
                Team t3 = grupo.get(confrontos[ronda][2]);
                Team t4 = grupo.get(confrontos[ronda][3]);

                store.games.add(new Game(store.nextId(), "Fase de Grupos", t1.name, t2.name, dataBase + " 18:00", livresHoje.remove(rand.nextInt(livresHoje.size()))));
                store.games.add(new Game(store.nextId(), "Fase de Grupos", t3.name, t4.name, dataBase + " 20:30", livresHoje.remove(rand.nextInt(livresHoje.size()))));
            }
        }

        // --- 2. GERAR FASE DE ELIMINAÇÃO (QUARTOS E SEMIFINAIS) ---
        int diasQuartos = maxDiasAcumulados + gapEntreRondas;
        String dataQ1 = addDays(store.tournament.startDate, diasQuartos);
        String dataQ2 = addDays(store.tournament.startDate, diasQuartos + 1); // Quartos divididos em 2 dias

        estadiosLivresPorDia.putIfAbsent(dataQ1, new java.util.ArrayList<>(store.stadiums));
        estadiosLivresPorDia.putIfAbsent(dataQ2, new java.util.ArrayList<>(store.stadiums));

        if (estadiosLivresPorDia.get(dataQ1).size() < 2 || estadiosLivresPorDia.get(dataQ2).size() < 2) {
            error("Erro: Precisas de pelo menos 2 estádios para sediar as eliminatórias.");
            return;
        }

        store.games.add(new Game(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ1 + " 18:00", estadiosLivresPorDia.get(dataQ1).remove(0)));
        store.games.add(new Game(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ1 + " 20:30", estadiosLivresPorDia.get(dataQ1).remove(0)));
        store.games.add(new Game(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ2 + " 18:00", estadiosLivresPorDia.get(dataQ2).remove(0)));
        store.games.add(new Game(store.nextId(), "Quartos de Final", "A determinar", "A determinar", dataQ2 + " 20:30", estadiosLivresPorDia.get(dataQ2).remove(0)));

        int diasSemis = diasQuartos + gapEntreRondas + 1;
        String dataSemis = addDays(store.tournament.startDate, diasSemis);
        estadiosLivresPorDia.putIfAbsent(dataSemis, new java.util.ArrayList<>(store.stadiums));

        store.games.add(new Game(store.nextId(), "Semifinais", "A determinar", "A determinar", dataSemis + " 18:00", estadiosLivresPorDia.get(dataSemis).remove(0)));
        store.games.add(new Game(store.nextId(), "Semifinais", "A determinar", "A determinar", dataSemis + " 20:30", estadiosLivresPorDia.get(dataSemis).remove(0)));
        // --- ADICIONA ESTA PARTE PARA A FINAL ---
        int diasFinal = diasSemis + gapEntreRondas;
        String dataFinal = addDays(store.tournament.startDate, diasFinal);
        estadiosLivresPorDia.putIfAbsent(dataFinal, new java.util.ArrayList<>(store.stadiums));

        // Garante que há um estádio para a grande final
        if(estadiosLivresPorDia.get(dataFinal).isEmpty()) estadiosLivresPorDia.get(dataFinal).addAll(store.stadiums);

        store.games.add(new Game(store.nextId(), "Final", "A determinar", "A determinar", dataFinal + " 20:30", estadiosLivresPorDia.get(dataFinal).remove(0)));
        store.calendarGenerated = true;
        info("Novo Calendário sorteado com sucesso! Grupos e Eliminatórias definidas.");
        Calendario.showCalendario(this, store);
    }

    public void apurarFaseEliminacao() {
        boolean todosConcluidos = store.games.stream()
                .filter(g -> g.phase.equals("Fase de Grupos"))
                .allMatch(g -> g.state == GameState.CONCLUIDO);

        if (!todosConcluidos) {
            error("Ainda existem jogos da Fase de Grupos por concluir! As equipas só podem avançar no fim.");
            return;
        }

        // 1. Calcula Pontos
        java.util.Map<String, Integer> pontos = new java.util.HashMap<>();
        for (Team t : store.teams) pontos.put(t.name, 0);

        for (Game g : store.games) {
            if (g.phase.equals("Fase de Grupos") && g.state == GameState.CONCLUIDO) {
                if (g.goalsA > g.goalsB) pontos.put(g.teamA, pontos.get(g.teamA) + 3);
                else if (g.goalsB > g.goalsA) pontos.put(g.teamB, pontos.get(g.teamB) + 3);
                else {
                    pontos.put(g.teamA, pontos.get(g.teamA) + 1);
                    pontos.put(g.teamB, pontos.get(g.teamB) + 1);
                }
            }
        }

        // 2. Organiza 1ºs e 2ºs lugares
        java.util.List<Team> apurados = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) { // Percorre Grupos A, B, C, D
            java.util.List<Team> grupo = new java.util.ArrayList<>(store.teams.subList(i * 4, i * 4 + 4));
            grupo.sort((t1, t2) -> pontos.get(t2.name).compareTo(pontos.get(t1.name))); // Ordena por pontos
            apurados.add(grupo.get(0)); // Adiciona 1º do grupo
            apurados.add(grupo.get(1)); // Adiciona 2º do grupo
        }

        // 3. Preenche os jogos dos Quartos
        java.util.List<Game> quartos = new java.util.ArrayList<>();
        for (Game g : store.games) if (g.phase.equals("Quartos de Final")) quartos.add(g);

        if (quartos.size() == 4) {
            quartos.get(0).teamA = apurados.get(0).name; quartos.get(0).teamB = apurados.get(3).name; // 1ºA vs 2ºB
            quartos.get(1).teamA = apurados.get(2).name; quartos.get(1).teamB = apurados.get(1).name; // 1ºB vs 2ºA
            quartos.get(2).teamA = apurados.get(4).name; quartos.get(2).teamB = apurados.get(7).name; // 1ºC vs 2ºD
            quartos.get(3).teamA = apurados.get(6).name; quartos.get(3).teamB = apurados.get(5).name; // 1ºD vs 2ºC

            info("Fase de Eliminação gerada com sucesso! As 8 melhores equipas avançaram para os Quartos de Final.");
        }
    }

    private String addDays(String date, int days) {
        try {
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy"
                    );

            java.time.LocalDate localDate =
                    java.time.LocalDate.parse(
                            date,
                            formatter
                    );

            return localDate.plusDays(days)
                    .format(formatter);

        } catch (Exception exception) {
            return date;
        }
    }

    /*
     * Este método funciona como ponte entre o Calendario
     * e o controlador dos dados do jogo.
     */
    public void showGameDetails(Game game) {
        DadosJogoPainelControlador.showGameDetails(
                this,
                store,
                game
        );
    }

    private void showStatsPage() {
        Estatisticas.showEstatisticas(
                this,
                store
        );
    }

    private void showBillingPage() {
        Faturacao.showFaturacao(
                this,
                store
        );
    }

    public String money(double value) {
        return String.format(
                "%.2f €",
                value
        );
    }

    public void info(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Informação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void error(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TorneioApp().setVisible(true)
        );
    }

    static class Tournament {
        String name;
        String startDate;
        String endDate;
        String state = "em preparação";

        int restDays;

        Tournament(
                String name,
                String startDate,
                String endDate,
                int restDays
        ) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.restDays = restDays;
        }
    }

    static class Team {
        int id;

        String name;
        String acronym;
        String coach;
        String homeKit;
        String awayKit;
        String emblem;

        List<Player> players = new ArrayList<>();

        Team(
                int id,
                String name,
                String acronym,
                String coach,
                String homeKit,
                String awayKit,
                String emblem
        ) {
            this.id = id;
            this.name = name;
            this.acronym = acronym;
            this.coach = coach;
            this.homeKit = homeKit;
            this.awayKit = awayKit;
            this.emblem = emblem;
        }

        boolean sameData(
                String name,
                String acronym,
                String coach,
                String homeKit,
                String awayKit,
                String emblem
        ) {
            return this.name.equals(name.trim()) &&
                    this.acronym.equals(acronym.trim()) &&
                    this.coach.equals(coach.trim()) &&
                    this.homeKit.equals(homeKit.trim()) &&
                    this.awayKit.equals(awayKit.trim()) &&
                    this.emblem.equals(emblem.trim());
        }
    }

    static class Player {
        int id;
        int number;

        String name;
        String position;
        String photo;

        Player(
                int id,
                String name,
                int number,
                String position,
                String photo
        ) {
            this.id = id;
            this.name = name;
            this.number = number;
            this.position = position;
            this.photo = photo;
        }

        boolean sameData(
                String name,
                int number,
                String position,
                String photo
        ) {
            return this.name.equals(name.trim()) &&
                    this.number == number &&
                    this.position.equals(position.trim()) &&
                    this.photo.equals(photo.trim());
        }
    }

    enum GameState {
        POR_AGENDAR,
        AGENDADO,
        EM_CURSO,
        CONCLUIDO,
        CANCELADO
    }

    static class Game {
        int id;

        int goalsA = 0;
        int goalsB = 0;

        int yellowA = 0;
        int yellowB = 0;

        int redA = 0;
        int redB = 0;

        int possessionA = 0;

        int cornersA = 0;
        int cornersB = 0;

        int foulsA = 0;
        int foulsB = 0;

        int shotsA = 0;
        int shotsB = 0;

        int offsidesA = 0;
        int offsidesB = 0;

        String phase;
        String teamA;
        String teamB;
        String dateTime;

        Estadio stadium;

        GameState state = GameState.AGENDADO;

        Game(
                int id,
                String phase,
                String teamA,
                String teamB,
                String dateTime,
                Estadio stadium
        ) {
            this.id = id;
            this.phase = phase;
            this.teamA = teamA;
            this.teamB = teamB;
            this.dateTime = dateTime;
            this.stadium = stadium;
        }

        String resultText() {
            return goalsA + " - " + goalsB;
        }

        @Override
        public String toString() {
            return phase +
                    " | " +
                    teamA +
                    " vs " +
                    teamB +
                    " | " +
                    dateTime;
        }
    }

    static class TicketBatch {
        int id;
        int available;
        int sold = 0;

        Game game;
        Bancada stand;

        double price;

        TicketBatch(
                int id,
                Game game,
                Bancada stand,
                double price,
                int available
        ) {
            this.id = id;
            this.game = game;
            this.stand = stand;
            this.price = price;
            this.available = available;
        }
    }

    static class SoldTicket {
        String code;

        TicketBatch batch;

        double price;

        SoldTicket(
                String code,
                TicketBatch batch,
                double price
        ) {
            this.code = code;
            this.batch = batch;
            this.price = price;
        }
    }
}

