
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
        if (store.teams.size() < 8) {
            error("Erro: O torneio tem de ter no mínimo 8 equipas.");
            return;
        }
        if (store.teams.size() % 4 != 0) {
            error("Erro: O número de equipas (" + store.teams.size() + ") tem de ser múltiplo de 4.");
            return;
        }
        for (Team t : store.teams) {
            if (t.players.size() < 23) {
                error("Erro: A equipa " + t.name + " tem apenas " + t.players.size() + " jogadores. São necessários pelo menos 23.");
                return;
            }
        }
        if (store.stadiums.isEmpty()) {
            error("Erro: Não existem estádios criados para agendar os jogos.");
            return;
        }

        // Limpa completamente os jogos anteriores para gerar um calendário do zero
        store.games.clear();

        int numGrupos = store.teams.size() / 4;
        java.util.Random rand = new java.util.Random();

        int restDays = (store.tournament != null) ? store.tournament.restDays : 2;
        int gapEntreRondas = restDays + 1;

        // 1. SORTEIO DAS EQUIPAS: Baralha uma cópia das equipas para criar grupos e jogos totalmente novos a cada clique!
        java.util.List<Team> equipasSorteadas = new java.util.ArrayList<>(store.teams);
        java.util.Collections.shuffle(equipasSorteadas, rand);

        // Controlo de estádios: garante no máximo 1 jogo por dia no mesmo estádio
        java.util.Map<String, java.util.List<Estadio>> estadiosLivresPorDia = new java.util.HashMap<>();

        for (int i = 0; i < numGrupos; i++) {
            int startIndex = i * 4;
            // Divide as equipas baralhadas pelos grupos dinamicamente
            java.util.List<Team> grupo = equipasSorteadas.subList(startIndex, startIndex + 4);

            int[][] confrontos = {
                    {0, 1, 2, 3}, // Ronda 1
                    {0, 2, 1, 3}, // Ronda 2
                    {0, 3, 1, 2}  // Ronda 3
            };

            // Mantém os grupos ligeiramente desfasados para o fluxo de estádios por dia bater certo
            int grupoOffsetDias = i * 1;

            for (int ronda = 0; ronda < 3; ronda++) {
                int diasAcumulados = grupoOffsetDias + (ronda * gapEntreRondas);
                String dataBase = addDays(store.tournament.startDate, diasAcumulados);

                estadiosLivresPorDia.putIfAbsent(dataBase, new java.util.ArrayList<>(store.stadiums));
                java.util.List<Estadio> livresHoje = estadiosLivresPorDia.get(dataBase);

                if (livresHoje.isEmpty()) {
                    error("Erro de Logística: Demasiados jogos para o dia " + dataBase + " e não há estádios suficientes (máx 1 jogo/dia por estádio).");
                    return;
                }

                Team t1 = grupo.get(confrontos[ronda][0]);
                Team t2 = grupo.get(confrontos[ronda][1]);
                Team t3 = grupo.get(confrontos[ronda][2]);
                Team t4 = grupo.get(confrontos[ronda][3]);

                // 2. HORÁRIOS DINÂMICOS: Define horas variadas de forma aleatória para cada partida
                String hora1 = "18:00";
                String hora2 = "20:30";

                // Exemplo do Clássico: Se for Benfica vs Porto, força a sugestão das 16:00
                if ((t1.name.equals("Benfica") && t2.name.equals("Porto")) || (t1.name.equals("Porto") && t2.name.equals("Benfica"))) {
                    hora1 = "16:00";
                } else if ((t3.name.equals("Benfica") && t4.name.equals("Porto")) || (t3.name.equals("Porto") && t4.name.equals("Benfica"))) {
                    hora2 = "16:00";
                } else {
                    // Caso não seja o clássico, sorteia horários diferentes para os dois jogos do dia
                    String[] horariosPossiveis = {"16:00", "18:00", "20:30"};
                    hora1 = horariosPossiveis[rand.nextInt(3)];
                    hora2 = horariosPossiveis[rand.nextInt(3)];
                    while (hora1.equals(hora2)) {
                        hora2 = horariosPossiveis[rand.nextInt(3)];
                    }
                }

                // --- AGENDAR JOGO 1 ---
                Estadio est1 = livresHoje.remove(rand.nextInt(livresHoje.size()));
                Game jogo1 = new Game(store.nextId(), "Fase de Grupos", t1.name, t2.name, dataBase + " " + hora1, est1);
                store.games.add(jogo1);

                // --- AGENDAR JOGO 2 ---
                if (livresHoje.isEmpty()) {
                    error("Erro de Logística: Demasiados jogos para o dia " + dataBase + " e não há estádios suficientes.");
                    return;
                }
                Estadio est2 = livresHoje.remove(rand.nextInt(livresHoje.size()));
                Game jogo2 = new Game(store.nextId(), "Fase de Grupos", t3.name, t4.name, dataBase + " " + hora2, est2);
                store.games.add(jogo2);
            }
        }

        store.calendarGenerated = true;
        info("Novo Calendário sorteado com sucesso! Grupos reestruturados, novas datas, horários e estádios distribuídos.");

        // Força a atualização do ecrã com a revolução total dos novos dados
        Calendario.showCalendario(this, store);
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

