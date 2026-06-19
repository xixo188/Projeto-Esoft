import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

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
        page.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel header = new JLabel(title);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        page.add(header, BorderLayout.NORTH);
        page.add(content, BorderLayout.CENTER);

        root.add(page, BorderLayout.CENTER);
        setContentPane(root);
        revalidate();
        repaint();
    }

    private JPanel menu() {
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        menu.setPreferredSize(new Dimension(180, 0));
        menu.setBackground(new Color(238, 238, 238));

        JButton home = btn("Homepage");
        JButton torneios = btn("Torneio");
        JButton equipas = btn("Equipas");
        JButton estadios = btn("Estádios");
        JButton calendario = btn("Calendário");
        JButton bilhetes = btn("Bilhetes");
        JButton patrocinadores = btn("Patrocínios");
        JButton estatisticas = btn("Estatísticas");
        JButton faturacao = btn("Faturação");

        home.addActionListener(e -> showHome());
        torneios.addActionListener(e -> TorneioPainelControlador.showTorneioPage(this, store));
        equipas.addActionListener(e -> showTeamsPage());
        estadios.addActionListener(e -> showStadiumsPage());
        calendario.addActionListener(e -> showCalendarPage());
        bilhetes.addActionListener(e -> BilhetePainelControlador.showTicketsPage(this, store));
        patrocinadores.addActionListener(e -> PatrocinioPainelControlador.showSponsorsPage(this, store));
        estatisticas.addActionListener(e -> showStatsPage());
        faturacao.addActionListener(e -> showBillingPage());

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
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        return b;
    }

    private void showHome() {
        JPanel p = new JPanel(new GridLayout(2, 2, 18, 18));
        p.add(card("Equipas", "Criar, editar, visualizar e remover equipas e jogadores."));
        p.add(card("Estádios", "Criar, editar, visualizar e remover estádios e bancadas."));
        p.add(card("Bilhetes", "Criar bilhetes por jogo/bancada e simular compras."));
        p.add(card("Calendário", "Gerar jogos exemplo e gerir estados dos jogos."));
        setPage("Homepage", p);
    }

    private JPanel card(String title, String desc) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        JTextArea d = new JTextArea(desc);
        d.setWrapStyleWord(true);
        d.setLineWrap(true);
        d.setEditable(false);
        d.setOpaque(false);
        c.add(t, BorderLayout.NORTH);
        c.add(d, BorderLayout.CENTER);
        return c;
    }

    private void showTeamsPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JButton create = btn("Criar Equipa");
        JButton view = btn("Ver Equipa");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(create);
        top.add(view);

        DefaultTableModel model = tableModel("ID", "Nome", "Sigla", "Treinador", "Jogadores");
        for (Team t : store.teams) {
            model.addRow(new Object[]{t.id, t.name, t.acronym, t.coach, t.players.size()});
        }
        JTable table = new JTable(model);

        create.addActionListener(e -> {
            if (store.calendarGenerated) {
                error("Não se pode inserir equipas a meio do torneio.");
            } else showTeamForm(null);
        });
        view.addActionListener(e -> {
            Team team = selectedTeam(table);
            if (team != null) showTeamDetails(team);
        });
        table.addMouseListener(doubleClick(() -> {
            Team team = selectedTeam(table);
            if (team != null) showTeamDetails(team);
        }));

        if (store.teams.isEmpty()) {
            p.add(empty("Não há equipas criadas."), BorderLayout.CENTER);
        } else {
            p.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        p.add(top, BorderLayout.NORTH);
        setPage("Lista de Equipas", p);
    }

    private void showTeamDetails(Team team) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText(
                "Nome: " + team.name + "\n" +
                        "Sigla: " + team.acronym + "\n" +
                        "Treinador: " + team.coach + "\n" +
                        "Uniforme principal: " + team.homeKit + "\n" +
                        "Uniforme alternativo: " + team.awayKit + "\n" +
                        "Emblema: " + (team.emblem.isBlank() ? "Opcional / não definido" : team.emblem) + "\n"
        );

        DefaultTableModel model = tableModel("ID", "Nome", "Número", "Posição");
        for (Player pl : team.players) {
            model.addRow(new Object[]{pl.id, pl.name, pl.number, pl.position});
        }
        JTable table = new JTable(model);

        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar Equipa");
        JButton delete = btn("Eliminar Equipa");
        JButton addPlayer = btn("Inserir Jogador");
        JButton viewPlayer = btn("Ver Jogador");
        topButtons.add(back);
        topButtons.add(edit);
        topButtons.add(delete);
        topButtons.add(addPlayer);
        topButtons.add(viewPlayer);

        back.addActionListener(e -> showTeamsPage());
        edit.addActionListener(e -> {
            if (store.calendarGenerated) error("Não se pode editar equipas a meio do torneio.");
            else showTeamForm(team);
        });
        delete.addActionListener(e -> deleteTeam(team));
        addPlayer.addActionListener(e -> {
            if (store.calendarGenerated) error("Não é possível inserir jogadores depois de existir calendarização.");
            else showPlayerForm(team, null);
        });
        viewPlayer.addActionListener(e -> {
            Player pl = selectedPlayer(table, team);
            if (pl != null) showPlayerDetails(team, pl);
        });
        table.addMouseListener(doubleClick(() -> {
            Player pl = selectedPlayer(table, team);
            if (pl != null) showPlayerDetails(team, pl);
        }));

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.add(new JScrollPane(infoArea));
        center.add(team.players.isEmpty() ? empty("Ainda não foram inseridos jogadores.") : new JScrollPane(table));
        p.add(topButtons, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        setPage("Equipa", p);
    }

    private void showTeamForm(Team editing) {
        boolean isEdit = editing != null;
        JTextField name = new JTextField(isEdit ? editing.name : "");
        JTextField acronym = new JTextField(isEdit ? editing.acronym : "");
        JTextField coach = new JTextField(isEdit ? editing.coach : "");
        JTextField homeKit = new JTextField(isEdit ? editing.homeKit : "");
        JTextField awayKit = new JTextField(isEdit ? editing.awayKit : "");
        JTextField emblem = new JTextField(isEdit ? editing.emblem : "");

        JPanel form = formPanel();
        addRow(form, "Nome da Equipa *", name, 0);
        addRow(form, "Sigla *", acronym, 1);
        addRow(form, "Treinador *", coach, 2);
        addRow(form, "Uniforme Principal *", homeKit, 3);
        addRow(form, "Uniforme Alternativo *", awayKit, 4);
        addRow(form, "Emblema", emblem, 5);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> {
            info(isEdit ? "Edição de dados da equipa cancelada." : "Criação de equipa cancelada.");
            showTeamsPage();
        });
        save.addActionListener(e -> {
            if (blank(name, acronym, coach, homeKit, awayKit)) {
                error("Preenche todos os campos obrigatórios.");
                return;
            }
            if (store.teamNameExists(name.getText().trim(), editing)) {
                error("Já existe uma equipa com esse nome.");
                return;
            }
            if (store.teamAcronymExists(acronym.getText().trim(), editing)) {
                error("Já existe uma equipa com essa sigla.");
                return;
            }
            if (isEdit && editing.sameData(name.getText(), acronym.getText(), coach.getText(), homeKit.getText(), awayKit.getText(), emblem.getText())) {
                info("Os dados inseridos são iguais aos originais.");
                showTeamsPage();
                return;
            }
            if (isEdit) {
                editing.name = name.getText().trim();
                editing.acronym = acronym.getText().trim();
                editing.coach = coach.getText().trim();
                editing.homeKit = homeKit.getText().trim();
                editing.awayKit = awayKit.getText().trim();
                editing.emblem = emblem.getText().trim();
                info("Equipa editada com sucesso.");
            } else {
                store.teams.add(new Team(store.nextId(), name.getText().trim(), acronym.getText().trim(), coach.getText().trim(), homeKit.getText().trim(), awayKit.getText().trim(), emblem.getText().trim()));
                info("Equipa criada com sucesso.");
            }
            showTeamsPage();
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        setPage(isEdit ? "Editar Equipa" : "Criar Equipa", p);
    }

    private void deleteTeam(Team team) {
        if (store.calendarGenerated) {
            error("Não se pode eliminar equipas a meio do torneio.");
            return;
        }
        int option = JOptionPane.showConfirmDialog(this, "Eliminar a equipa " + team.name + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            store.teams.remove(team);
            info("Foi eliminada a equipa com sucesso.");
            showTeamsPage();
        }
    }

    private void showPlayerDetails(Team team, Player player) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText("Nome: " + player.name + "\nNúmero: " + player.number + "\nPosição: " + player.position + "\nFoto: " + (player.photo.isBlank() ? "Opcional / não definida" : player.photo));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar Jogador");
        JButton delete = btn("Eliminar Jogador");
        buttons.add(back);
        buttons.add(edit);
        buttons.add(delete);

        back.addActionListener(e -> showTeamDetails(team));
        edit.addActionListener(e -> {
            if (store.calendarGenerated) error("Não é possível editar jogadores depois de existir calendarização.");
            else showPlayerForm(team, player);
        });
        delete.addActionListener(e -> {
            if (store.calendarGenerated) {
                error("Não é possível eliminar jogadores depois de existir calendarização.");
                return;
            }
            int opt = JOptionPane.showConfirmDialog(this, "Eliminar jogador " + player.name + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                team.players.remove(player);
                info("Jogador eliminado com sucesso.");
                showTeamDetails(team);
            }
        });
        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        setPage("Jogador", p);
    }

    private void showPlayerForm(Team team, Player editing) {
        boolean isEdit = editing != null;
        JTextField name = new JTextField(isEdit ? editing.name : "");
        JTextField number = new JTextField(isEdit ? String.valueOf(editing.number) : "");
        JTextField position = new JTextField(isEdit ? editing.position : "");
        JTextField photo = new JTextField(isEdit ? editing.photo : "");

        JPanel form = formPanel();
        addRow(form, "Nome *", name, 0);
        addRow(form, "Número *", number, 1);
        addRow(form, "Posição *", position, 2);
        addRow(form, "Foto", photo, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showTeamDetails(team));
        save.addActionListener(e -> {
            if (blank(name, number, position)) {
                error("Preenche todos os campos obrigatórios.");
                return;
            }
            Integer num = parseInt(number.getText(), "O número da camisola tem de ser um número inteiro.");
            if (num == null) return;
            if (num <= 0) {
                error("O número da camisola deve ser positivo.");
                return;
            }
            if (store.playerNumberExists(team, num, editing)) {
                error("Já existe um jogador com esse número na equipa.");
                return;
            }
            if (isEdit && editing.sameData(name.getText(), num, position.getText(), photo.getText())) {
                info("Os dados inseridos são iguais aos dados originais.");
                return;
            }
            if (isEdit) {
                editing.name = name.getText().trim();
                editing.number = num;
                editing.position = position.getText().trim();
                editing.photo = photo.getText().trim();
                info("Jogador editado com sucesso.");
            } else {
                team.players.add(new Player(store.nextId(), name.getText().trim(), num, position.getText().trim(), photo.getText().trim()));
                info("Jogador inserido com sucesso.");
            }
            showTeamDetails(team);
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        setPage(isEdit ? "Editar Jogador" : "Inserir Jogador", p);
    }

    public void showStadiumsPage() {
        EstadioPainelControlador.showStadiumsPage(this, store);
    }

    private void showCalendarPage() {
        Calendario.showCalendario(this, store);
    }

    public void generateCalendar() {
        if (store.tournament.startDate.compareTo(store.tournament.endDate) > 0) {
            error("Não é possível gerar um calendário com as datas fornecidas.");
            return;
        }
        if (store.stadiums.isEmpty()) {
            error("É necessário criar pelo menos um estádio antes de gerar o calendário.");
            return;
        }
        if (store.games.stream().anyMatch(g -> g.state == GameState.CONCLUIDO || g.state == GameState.EM_CURSO)) {
            error("Não é possível gerar calendário depois de já terem ocorrido jogos.");
            return;
        }
        store.games.clear();
        Estadio stadium = store.stadiums.get(0);
        store.games.add(new Game(store.nextId(), "Fase de Grupos", "Equipa A", "Equipa B",
                store.tournament.startDate + " 18:00", stadium));

        store.games.add(new Game(store.nextId(), "Fase de Grupos", "Equipa C", "Equipa D",
                addDays(store.tournament.startDate, store.tournament.restDays) + " 18:00", stadium));

        store.games.add(new Game(store.nextId(), "Final", "Por definir", "Por definir",
                store.tournament.endDate + " 20:00", stadium));
        store.calendarGenerated = true;
        store.tournament.state = "em curso";
        info("Calendário gerado com sucesso.");
        showCalendarPage();
    }

    private String addDays(String date, int days) {
        try {
            java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            java.time.LocalDate localDate =
                    java.time.LocalDate.parse(date, formatter);

            return localDate.plusDays(days).format(formatter);
        } catch (Exception e) {
            return date;
        }
    }

    public void showGameDetails(Game g) {

        boolean jogoComEstatisticas = g.state == GameState.EM_CURSO || g.state == GameState.CONCLUIDO;

        int yellowA = jogoComEstatisticas ? g.yellowA : 0;
        int yellowB = jogoComEstatisticas ? g.yellowB : 0;
        int redA = jogoComEstatisticas ? g.redA : 0;
        int redB = jogoComEstatisticas ? g.redB : 0;
        int goalsA = jogoComEstatisticas ? g.goalsA : 0;
        int goalsB = jogoComEstatisticas ? g.goalsB : 0;
        int possessionA = jogoComEstatisticas ? g.possessionA : 0;
        int possessionB = jogoComEstatisticas ? (100 - g.possessionA) : 0;


        JPanel p = new JPanel(new BorderLayout(12, 12));
        JPanel dados = new JPanel(new GridBagLayout());

        dados.setBackground(new Color(220, 220, 220));
        dados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dados.setPreferredSize(new Dimension(800, 430));
        dados.setBackground(new Color(220, 220, 220));
        dados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel equipaA = new JLabel(g.teamA, SwingConstants.CENTER);
        JLabel equipaB = new JLabel(g.teamB, SwingConstants.CENTER);

        equipaA.setFont(new Font("Arial", Font.BOLD, 24));
        equipaB.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField data = new JTextField(g.dateTime);
        JTextField estado = new JTextField(g.state.toString());
        data.setPreferredSize(new Dimension(160, 30));

        JTextField amarelosA = new JTextField(String.valueOf(yellowA));
        JTextField amarelosB = new JTextField(String.valueOf(yellowB));
        JTextField vermelhosA = new JTextField(String.valueOf(redA));
        JTextField vermelhosB = new JTextField(String.valueOf(redB));
        JTextField posseA = new JTextField(String.valueOf(possessionA));
        JTextField posseB = new JTextField(String.valueOf(possessionB));
        JTextField golosA = new JTextField(String.valueOf(goalsA));
        JTextField golosB = new JTextField(String.valueOf(goalsB));

        JTextField[] fields = {
                data, estado, amarelosA, amarelosB, vermelhosA, vermelhosB,
                posseA, posseB, golosA, golosB
        };

        for (JTextField f : fields) {
            f.setEditable(false);
            f.setHorizontalAlignment(JTextField.CENTER);
            f.setPreferredSize(new Dimension(115, 30));
            f.setFont(new Font("Arial", Font.PLAIN, 14));
        }
        data.setPreferredSize(new Dimension(200, 30));

        c.gridx = 0; c.gridy = 0;
        dados.add(new JLabel("img", SwingConstants.CENTER), c);

        c.gridx = 1;
        dados.add(new JLabel("Data", SwingConstants.CENTER), c);

        c.gridx = 2;
        dados.add(new JLabel("Estado", SwingConstants.CENTER), c);

        c.gridx = 3;
        dados.add(new JLabel("img", SwingConstants.CENTER), c);

        c.gridx = 0; c.gridy = 1;
        dados.add(equipaA, c);

        c.gridx = 1;
        dados.add(data, c);

        c.gridx = 2;
        dados.add(estado, c);

        c.gridx = 3;
        dados.add(equipaB, c);

        c.gridx = 0; c.gridy = 2;
        dados.add(new JLabel(g.teamA, SwingConstants.CENTER), c);

        c.gridx = 1;
        dados.add(new JLabel("vs", SwingConstants.CENTER), c);

        c.gridx = 3;
        dados.add(new JLabel(g.teamB, SwingConstants.CENTER), c);

        c.gridx = 0; c.gridy = 3;
        dados.add(amarelosA, c);

        c.gridx = 1; c.gridwidth = 2;
        dados.add(new JLabel("Cartão Amarelo", SwingConstants.CENTER), c);

        c.gridx = 3; c.gridwidth = 1;
        dados.add(amarelosB, c);

        c.gridx = 0; c.gridy = 4;
        dados.add(vermelhosA, c);

        c.gridx = 1; c.gridwidth = 2;
        dados.add(new JLabel("Cartão Vermelho", SwingConstants.CENTER), c);

        c.gridx = 3; c.gridwidth = 1;
        dados.add(vermelhosB, c);

        c.gridx = 0; c.gridy = 5;
        dados.add(posseA, c);

        c.gridx = 1; c.gridwidth = 2;
        dados.add(new JLabel("Posse de bola", SwingConstants.CENTER), c);

        c.gridx = 3; c.gridwidth = 1;
        dados.add(posseB, c);

        c.gridx = 0; c.gridy = 6;
        dados.add(golosA, c);

        c.gridx = 1; c.gridwidth = 2;
        dados.add(new JLabel("Golos", SwingConstants.CENTER), c);

        c.gridx = 3; c.gridwidth = 1;
        dados.add(golosB, c);

        c.gridx = 0; c.gridy = 7; c.gridwidth = 4;
        dados.add(new JLabel("Estádio: " + (g.stadium == null ? "" : g.stadium.nome)), c);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(dados);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar Dados");

        buttons.add(back);
        buttons.add(edit);

        back.addActionListener(e -> Calendario.showCalendario(this, store));
        edit.addActionListener(e -> showGameDataForm(g));

        p.add(buttons, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);

        setPage("Dados Jogo", p);
    }

    private void showGameDataForm(Game g) {
        boolean jogoComEstatisticas = g.state == GameState.EM_CURSO || g.state == GameState.CONCLUIDO;

        int yellowA = jogoComEstatisticas ? g.yellowA : 0;
        int yellowB = jogoComEstatisticas ? g.yellowB : 0;
        int redA = jogoComEstatisticas ? g.redA : 0;
        int redB = jogoComEstatisticas ? g.redB : 0;
        int goalsA = jogoComEstatisticas ? g.goalsA : 0;
        int goalsB = jogoComEstatisticas ? g.goalsB : 0;
        int possessionA = jogoComEstatisticas ? g.possessionA : 0;
        int possessionB = jogoComEstatisticas ? (100 - g.possessionA) : 0;

        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel dados = new JPanel(new GridBagLayout());
        dados.setBackground(new Color(220, 220, 220));
        dados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dados.setPreferredSize(new Dimension(800, 430));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel equipaA = new JLabel(g.teamA, SwingConstants.CENTER);
        JLabel equipaB = new JLabel(g.teamB, SwingConstants.CENTER);

        equipaA.setFont(new Font("Arial", Font.BOLD, 24));
        equipaB.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField data = field(g.dateTime, 190);
        JTextField estado = field(g.state.toString(), 120);

        JTextField amarelosA = field(String.valueOf(yellowA), 70);
        JTextField amarelosB = field(String.valueOf(yellowB), 70);
        JTextField vermelhosA = field(String.valueOf(redA), 70);
        JTextField vermelhosB = field(String.valueOf(redB), 70);
        JTextField posseA = field(String.valueOf(possessionA), 70);
        JTextField posseB = field(String.valueOf(possessionB), 70);
        JTextField golosA = field(String.valueOf(goalsA), 70);
        JTextField golosB = field(String.valueOf(goalsB), 70);

        c.gridx = 0; c.gridy = 0;
        dados.add(new JLabel("img", SwingConstants.CENTER), c);

        c.gridx = 1;
        dados.add(new JLabel("Data", SwingConstants.CENTER), c);

        c.gridx = 2;
        dados.add(new JLabel("Estado", SwingConstants.CENTER), c);

        c.gridx = 3;
        dados.add(new JLabel("img", SwingConstants.CENTER), c);

        c.gridx = 0; c.gridy = 1;
        dados.add(equipaA, c);

        c.gridx = 1;
        dados.add(data, c);

        c.gridx = 2;
        dados.add(estado, c);

        c.gridx = 3;
        dados.add(equipaB, c);

        c.gridx = 0; c.gridy = 2;
        dados.add(new JLabel(g.teamA, SwingConstants.CENTER), c);

        c.gridx = 1;
        dados.add(new JLabel("vs", SwingConstants.CENTER), c);

        c.gridx = 3;
        dados.add(new JLabel(g.teamB, SwingConstants.CENTER), c);

        addLinhaEditavel(dados, c, 3, amarelosA, "Cartão Amarelo", amarelosB);
        addLinhaEditavel(dados, c, 4, vermelhosA, "Cartão Vermelho", vermelhosB);
        addLinhaEditavel(dados, c, 5, posseA, "Posse de bola", posseB);
        addLinhaEditavel(dados, c, 6, golosA, "Golos", golosB);

        c.gridx = 0; c.gridy = 7; c.gridwidth = 4;
        dados.add(new JLabel("Estádio: " + (g.stadium == null ? "" : g.stadium.nome)), c);
        c.gridwidth = 1;

        JPanel center = new JPanel(new GridBagLayout());
        center.add(dados);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showGameDetails(g));

        save.addActionListener(e -> {
            if (g.state != GameState.EM_CURSO) {
                error("Não se pode inserir dados enquanto o jogo não está a decorrer.");
                return;
            }

            Integer ya = parseNonNegative(amarelosA.getText(), "Cartões inválidos.");
            Integer yb = parseNonNegative(amarelosB.getText(), "Cartões inválidos.");
            Integer ra = parseNonNegative(vermelhosA.getText(), "Cartões inválidos.");
            Integer rb = parseNonNegative(vermelhosB.getText(), "Cartões inválidos.");
            Integer pa = parseNonNegative(posseA.getText(), "Percentagem inválida.");
            Integer ga = parseNonNegative(golosA.getText(), "Golos inválidos.");
            Integer gb = parseNonNegative(golosB.getText(), "Golos inválidos.");

            if (ya == null || yb == null || ra == null || rb == null ||
                    pa == null || ga == null || gb == null) return;

            if (pa > 100) {
                error("A posse de bola tem de estar entre 0 e 100.");
                return;
            }

            g.yellowA = ya;
            g.yellowB = yb;
            g.redA = ra;
            g.redB = rb;
            g.possessionA = pa;
            g.goalsA = ga;
            g.goalsB = gb;

            info("Dados editados com sucesso.");
            showGameDetails(g);
        });

        p.add(buttons, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);

        setPage("Editar Dados Jogo", p);
    }

    private JTextField field(String value, int width) {
        JTextField f = new JTextField(value);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setPreferredSize(new Dimension(width, 30));
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        return f;
    }

    private void addLinhaEditavel(
            JPanel dados,
            GridBagConstraints c,
            int row,
            JTextField leftField,
            String label,
            JTextField rightField
    ) {
        c.gridx = 0;
        c.gridy = row;
        dados.add(counterPanel(leftField), c);

        c.gridx = 1;
        c.gridwidth = 2;
        dados.add(new JLabel(label, SwingConstants.CENTER), c);

        c.gridx = 3;
        c.gridwidth = 1;
        dados.add(counterPanel(rightField), c);
    }

    private JPanel counterPanel(JTextField field) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        p.setOpaque(false);

        JButton minus = new JButton("-");
        JButton plus = new JButton("+");

        minus.setMargin(new Insets(1, 5, 1, 5));
        plus.setMargin(new Insets(1, 5, 1, 5));

        minus.addActionListener(e -> {
            int value = parseCounter(field.getText());
            if (value > 0) field.setText(String.valueOf(value - 1));
        });

        plus.addActionListener(e -> {
            int value = parseCounter(field.getText());
            field.setText(String.valueOf(value + 1));
        });

        p.add(minus);
        p.add(field);
        p.add(plus);

        return p;
    }

    private int parseCounter(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void showStatsPage() {
        Estatisticas.showEstatisticas(this, store);
    }

    private void showBillingPage() {
        Faturacao.showFaturacao(this, store);
    }

    private JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return form;
    }

    private void addRow(JPanel form, String label, JComponent comp, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        comp.setPreferredSize(new Dimension(320, 28));
        form.add(comp, c);
    }

    private JLabel empty(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 18));
        return l;
    }

    private DefaultTableModel tableModel(String... cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private java.awt.event.MouseAdapter doubleClick(Runnable action) {
        return new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) action.run();
            }
        };
    }

    private boolean blank(JTextField... fields) {
        for (JTextField f : fields) if (f.getText().trim().isEmpty()) return true;
        return false;
    }

    private Integer parseInt(String value, String message) {
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { error(message); return null; }
    }

    private Integer parseNonNegative(String value, String message) {
        Integer i = parseInt(value, message);
        if (i == null) return null;
        if (i < 0) { error(message); return null; }
        return i;
    }

    private Double parseDouble(String value, String message) {
        try { return Double.parseDouble(value.trim().replace(",", ".")); }
        catch (NumberFormatException e) { error(message); return null; }
    }

    public String money(double value) {
        return String.format("%.2f €", value);
    }
    public void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Informação", JOptionPane.INFORMATION_MESSAGE); }
    public void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE); }

    private int selectedId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { error("Seleciona primeiro um registo da lista."); return -1; }
        return (Integer) table.getValueAt(row, 0);
    }

    private Team selectedTeam(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findTeam(id); }
    private Game selectedGame(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findGame(id); }
    private TicketBatch selectedTicket(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findTicket(id); }
    private Player selectedPlayer(JTable table, Team team) { int id = selectedId(table); return id < 0 ? null : team.players.stream().filter(p -> p.id == id).findFirst().orElse(null); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TorneioApp().setVisible(true));
    }

    static class Tournament {
        String name, startDate, endDate, state = "em preparação";
        int restDays;
        Tournament(String name, String startDate, String endDate, int restDays) {
            this.name = name; this.startDate = startDate; this.endDate = endDate; this.restDays = restDays;
        }
    }

    static class Team {
        int id; String name, acronym, coach, homeKit, awayKit, emblem;
        List<Player> players = new ArrayList<>();
        Team(int id, String name, String acronym, String coach, String homeKit, String awayKit, String emblem) {
            this.id = id; this.name = name; this.acronym = acronym; this.coach = coach; this.homeKit = homeKit; this.awayKit = awayKit; this.emblem = emblem;
        }
        boolean sameData(String name, String acronym, String coach, String homeKit, String awayKit, String emblem) {
            return this.name.equals(name.trim()) && this.acronym.equals(acronym.trim()) && this.coach.equals(coach.trim()) && this.homeKit.equals(homeKit.trim()) && this.awayKit.equals(awayKit.trim()) && this.emblem.equals(emblem.trim());
        }
    }

    static class Player {
        int id, number; String name, position, photo;
        Player(int id, String name, int number, String position, String photo) {
            this.id = id; this.name = name; this.number = number; this.position = position; this.photo = photo;
        }
        boolean sameData(String name, int number, String position, String photo) {
            return this.name.equals(name.trim()) && this.number == number && this.position.equals(position.trim()) && this.photo.equals(photo.trim());
        }
    }

    enum GameState { POR_AGENDAR, AGENDADO, EM_CURSO, CONCLUIDO, CANCELADO }

    static class Game {
        int id, goalsA = 0, goalsB = 0, yellowA = 0, yellowB = 0, redA = 0, redB = 0, possessionA = 0;
        String phase, teamA, teamB, dateTime;
        Estadio stadium;
        GameState state = GameState.AGENDADO;
        Game(int id, String phase, String teamA, String teamB, String dateTime, Estadio stadium) {
            this.id = id; this.phase = phase; this.teamA = teamA; this.teamB = teamB; this.dateTime = dateTime; this.stadium = stadium;
        }
        String resultText() { return goalsA + " - " + goalsB; }
        public String toString() { return phase + " | " + teamA + " vs " + teamB + " | " + dateTime; }
    }

    static class TicketBatch {
        int id, available, sold = 0;
        Game game; Bancada stand; double price;
        TicketBatch(int id, Game game, Bancada stand, double price, int available) {
            this.id = id; this.game = game; this.stand = stand; this.price = price; this.available = available;
        }
    }

    static class SoldTicket {
        String code; TicketBatch batch; double price;
        SoldTicket(String code, TicketBatch batch, double price) { this.code = code; this.batch = batch; this.price = price; }
    }
}