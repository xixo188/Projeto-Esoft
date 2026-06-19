
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
        if (store.tournament == null) {
            error(
                    "É necessário criar um torneio antes de gerar o calendário."
            );
            return;
        }

        if (
                store.tournament.startDate.compareTo(
                        store.tournament.endDate
                ) > 0
        ) {
            error(
                    "Não é possível gerar um calendário com as datas fornecidas."
            );
            return;
        }

        if (store.stadiums.isEmpty()) {
            error(
                    "É necessário criar pelo menos um estádio antes de gerar o calendário."
            );
            return;
        }

        boolean existemJogosIniciados =
                store.games.stream().anyMatch(game ->
                        game.state == GameState.CONCLUIDO ||
                                game.state == GameState.EM_CURSO
                );

        if (existemJogosIniciados) {
            error(
                    "Não é possível gerar o calendário depois de já terem ocorrido jogos."
            );
            return;
        }

        store.games.clear();

        Estadio stadium = store.stadiums.get(0);

        Game firstGame = new Game(
                store.nextId(),
                "Fase de Grupos",
                "Equipa A",
                "Equipa B",
                store.tournament.startDate + " 18:00",
                stadium
        );

        Game secondGame = new Game(
                store.nextId(),
                "Fase de Grupos",
                "Equipa C",
                "Equipa D",
                addDays(
                        store.tournament.startDate,
                        store.tournament.restDays
                ) + " 18:00",
                stadium
        );

        Game finalGame = new Game(
                store.nextId(),
                "Final",
                "Por definir",
                "Por definir",
                store.tournament.endDate + " 20:00",
                stadium
        );

        store.games.add(firstGame);
        store.games.add(secondGame);
        store.games.add(finalGame);

        store.calendarGenerated = true;
        store.tournament.state = "em curso";

        info("Calendário gerado com sucesso.");

        showCalendarPage();
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

