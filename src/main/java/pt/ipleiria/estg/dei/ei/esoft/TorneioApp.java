

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class TorneioApp extends JFrame {
    private final Store store = Store.getInstance();

    public TorneioApp() {
        setTitle("Gestor de Torneios - Protótipo ESoft");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showHome();
    }

    private void setPage(String title, JPanel content) {
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
        torneios.addActionListener(e -> showTorneioPage());
        equipas.addActionListener(e -> showTeamsPage());
        estadios.addActionListener(e -> showStadiumsPage());
        calendario.addActionListener(e -> showCalendarPage());
        bilhetes.addActionListener(e -> showTicketsPage());
        patrocinadores.addActionListener(e -> showSponsorsPage());
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

    private JFormattedTextField dateField(String value) {
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('0');

            JFormattedTextField field = new JFormattedTextField(mask);
            field.setValue(value == null ? "" : value);
            return field;
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }

    private void showTorneioPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton create = btn("Criar Torneio");

        top.add(create);

        DefaultTableModel model = tableModel("ID", "Torneio", "Estado");

        Tournament t = store.tournament;
        model.addRow(new Object[]{1, t.name, t.state});

        JTable table = new JTable(model);

        create.addActionListener(e -> showTournamentCreateForm());

        table.addMouseListener(doubleClick(() -> showTournamentDetails()));

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        setPage("Lista de Torneios", p);
    }

    private void showTournamentDetails() {
        Tournament t = store.tournament;

        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = tableModel(
                "Início",
                "Fim",
                "Estado",
                "Número Equipas"
        );

        model.addRow(new Object[]{
                t.startDate,
                t.endDate,
                t.state,
                store.teams.size()
        });

        JTable table = new JTable(model);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar Torneio");

        buttons.add(back);
        buttons.add(edit);

        back.addActionListener(e -> showTorneioPage());
        edit.addActionListener(e -> showTournamentForm());

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);

        setPage(t.name, p);
    }

    private void showTournamentForm() {
        Tournament t = store.tournament;
        JTextField name = new JTextField(t.name);
        JTextField start = new JTextField(t.startDate);
        JTextField end = new JTextField(t.endDate);
        JTextField rest = new JTextField(String.valueOf(t.restDays));

        JPanel form = formPanel();
        addRow(form, "Nome *", name, 0);
        addRow(form, "Data início *", start, 1);
        addRow(form, "Data fim *", end, 2);
        addRow(form, "Descanso mínimo *", rest, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> showTorneioPage());
        save.addActionListener(e -> {
            if (blank(name, start, end, rest)) {
                error("Preenche todos os campos obrigatórios.");
                return;
            }

            Integer restVal = parseInt(
                    rest.getText(),
                    "O descanso mínimo tem de ser um número inteiro."
            );

            if (restVal == null) return;

            if (restVal < 2) {
                error("O tempo de descanso entre jogos não pode ser inferior a 2 dias.");
                return;
            }

            t.name = name.getText().trim();
            t.startDate = start.getText().trim();
            t.endDate = end.getText().trim();
            t.restDays = restVal;
            info("Dados do torneio guardados com sucesso.");
            showTorneioPage();
        });
        setPage("Editar Torneio", p);
    }

    private void showTournamentCreateForm() {
        JTextField name = new JTextField();
        JFormattedTextField start = dateField("");
        JFormattedTextField end = dateField("");
        JTextField rest = new JTextField("2");

        JPanel form = formPanel();
        addRow(form, "Nome do Torneio *", name, 0);
        addRow(form, "Data de início *", start, 1);
        addRow(form, "Data de fim *", end, 2);
        addRow(form, "Descanso entre jogos *", rest, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> showTorneioPage());

        save.addActionListener(e -> {
            if (blank(name, start, end, rest)) {
                error("Erro: Campos obrigatórios em falta.");
                return;
            }

            Integer restVal = parseInt(
                    rest.getText(),
                    "O descanso entre jogos tem de ser um número inteiro."
            );

            if (restVal == null) return;

            if (restVal < 2) {
                error("O tempo de descanso entre jogos não pode ser inferior a 2 dias.");
                return;
            }

            store.tournament.name = name.getText().trim();
            store.tournament.startDate = start.getText().trim();
            store.tournament.endDate = end.getText().trim();
            store.tournament.restDays = restVal;
            store.tournament.state = "em preparação";

            info("Torneio criado com sucesso.");
            showTorneioPage();
        });

        setPage("Criar Torneio", p);
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
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("Monospaced", Font.PLAIN, 14));
        info.setText(
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
        center.add(new JScrollPane(info));
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

    private void showStadiumsPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JButton create = btn("Criar Estádio");
        JButton view = btn("Ver Estádio");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(create);
        top.add(view);

        DefaultTableModel model = tableModel("ID", "Nome", "Localização", "Lotação", "Bancadas");
        for (Stadium s : store.stadiums) {
            model.addRow(new Object[]{s.id, s.name, s.location, s.capacity, s.stands.size()});
        }
        JTable table = new JTable(model);
        create.addActionListener(e -> {
            if (store.calendarGenerated) error("Um estádio só pode ser criado antes de começar o torneio.");
            else showStadiumForm(null);
        });
        view.addActionListener(e -> {
            Stadium s = selectedStadium(table);
            if (s != null) showStadiumDetails(s);
        });
        table.addMouseListener(doubleClick(() -> {
            Stadium s = selectedStadium(table);
            if (s != null) showStadiumDetails(s);
        }));

        p.add(top, BorderLayout.NORTH);
        p.add(store.stadiums.isEmpty() ? empty("Não existe nenhum estádio criado.") : new JScrollPane(table), BorderLayout.CENTER);
        setPage("Lista de Estádios", p);
    }

    private void showStadiumDetails(Stadium stadium) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText("Nome: " + stadium.name + "\nLocalização: " + stadium.location + "\nLotação total: " + stadium.capacity + "\nLotação das bancadas: " + stadium.totalStandCapacity());

        JComboBox<Stand> standCombo = new JComboBox<>();
        for (Stand st : stadium.stands) standCombo.addItem(st);
        JButton viewStand = btn("Ver Bancada");
        JButton addStand = btn("Adicionar Bancada");
        JButton edit = btn("Editar Estádio");
        JButton delete = btn("Eliminar Estádio");
        JButton back = btn("Voltar");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(new JLabel("Bancadas:"));
        buttons.add(standCombo);
        buttons.add(viewStand);
        buttons.add(addStand);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(back);

        back.addActionListener(e -> showStadiumsPage());
        viewStand.addActionListener(e -> {
            if (stadium.stands.isEmpty()) error("Não existe nenhuma bancada para este estádio.");
            else showStandDetails(stadium, (Stand) standCombo.getSelectedItem());
        });
        addStand.addActionListener(e -> {
            if (store.calendarGenerated) error("Uma bancada só pode ser inserida antes de começar o torneio.");
            else showStandForm(stadium, null);
        });
        edit.addActionListener(e -> {
            if (store.calendarGenerated) error("Os estádios só podem ser editados antes de começar o torneio.");
            else showStadiumForm(stadium);
        });
        delete.addActionListener(e -> deleteStadium(stadium));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        setPage("Estádio", p);
    }

    private void showStadiumForm(Stadium editing) {
        boolean isEdit = editing != null;
        JTextField name = new JTextField(isEdit ? editing.name : "");
        JTextField location = new JTextField(isEdit ? editing.location : "");
        JTextField capacity = new JTextField(isEdit ? String.valueOf(editing.capacity) : "");
        JPanel form = formPanel();
        addRow(form, "Nome do Estádio *", name, 0);
        addRow(form, "Localização *", location, 1);
        addRow(form, "Lotação Total *", capacity, 2);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);
        cancel.addActionListener(e -> showStadiumsPage());
        save.addActionListener(e -> {
            if (blank(name, location, capacity)) {
                error("É necessário preencher todos os campos obrigatórios (*).");
                return;
            }
            Integer cap = parseInt(capacity.getText(), "O campo Lotação Total tem de ser um número inteiro.");
            if (cap == null) return;
            if (cap <= 0) {
                error("A lotação total deve ser positiva.");
                return;
            }
            if (isEdit && editing.totalStandCapacity() > cap) {
                error("A lotação total não pode ser inferior à soma das bancadas existentes.");
                return;
            }
            if (isEdit) {
                editing.name = name.getText().trim();
                editing.location = location.getText().trim();
                editing.capacity = cap;
                info("Estádio editado com sucesso.");
            } else {
                store.stadiums.add(new Stadium(store.nextId(), name.getText().trim(), location.getText().trim(), cap));
                info("Estádio criado com sucesso.");
            }
            showStadiumsPage();
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        setPage(isEdit ? "Editar Estádio" : "Criar Estádio", p);
    }

    private void deleteStadium(Stadium stadium) {
        if (store.calendarGenerated) {
            error("Um estádio só pode ser eliminado antes de começar o torneio.");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "Eliminar estádio " + stadium.name + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            store.stadiums.remove(stadium);
            info("Estádio eliminado com sucesso.");
            showStadiumsPage();
        }
    }

    private void showStandDetails(Stadium stadium, Stand stand) {
        if (stand == null) {
            error("Não existe nenhuma bancada para este estádio.");
            return;
        }
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea("Nome: " + stand.name + "\nLotação: " + stand.capacity);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar");
        JButton delete = btn("Eliminar");
        buttons.add(back);
        buttons.add(edit);
        buttons.add(delete);
        back.addActionListener(e -> showStadiumDetails(stadium));
        edit.addActionListener(e -> {
            if (store.calendarGenerated) error("Uma bancada só pode ser editada antes de começar o torneio.");
            else showStandForm(stadium, stand);
        });
        delete.addActionListener(e -> {
            if (store.calendarGenerated) {
                error("Uma bancada só pode ser eliminada antes de começar o torneio.");
                return;
            }
            stadium.stands.remove(stand);
            info("Bancada eliminada com sucesso.");
            showStadiumDetails(stadium);
        });
        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        setPage("Bancada", p);
    }

    private void showStandForm(Stadium stadium, Stand editing) {
        boolean isEdit = editing != null;
        JTextField name = new JTextField(isEdit ? editing.name : "");
        JTextField capacity = new JTextField(isEdit ? String.valueOf(editing.capacity) : "");
        JPanel form = formPanel();
        addRow(form, "Nome *", name, 0);
        addRow(form, "Lotação *", capacity, 1);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);
        cancel.addActionListener(e -> showStadiumDetails(stadium));
        save.addActionListener(e -> {
            if (blank(name, capacity)) {
                error("É necessário preencher todos os campos obrigatórios (*).");
                return;
            }
            Integer cap = parseInt(capacity.getText(), "A lotação da bancada tem de ser um número inteiro.");
            if (cap == null) return;
            int old = isEdit ? editing.capacity : 0;
            if (stadium.totalStandCapacity() - old + cap > stadium.capacity) {
                error("A soma das bancadas não pode ultrapassar a lotação total do estádio.");
                return;
            }
            if (isEdit) {
                editing.name = name.getText().trim();
                editing.capacity = cap;
                info("Bancada editada com sucesso.");
            } else {
                stadium.stands.add(new Stand(store.nextId(), name.getText().trim(), cap));
                info("Bancada adicionada com sucesso.");
            }
            showStadiumDetails(stadium);
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        setPage(isEdit ? "Editar Bancada" : "Adicionar Bancada", p);
    }

    private void showCalendarPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton groupPhase = btn("Fase de Grupos");
        JButton eliminationPhase = btn("Fase de Eliminação");

        top.add(groupPhase);
        top.add(eliminationPhase);

        JPanel center = new JPanel(new BorderLayout(12, 12));

        if (store.games.isEmpty()) {
            center.add(empty("Ainda não há calendário definido."), BorderLayout.CENTER);
        } else {
            DefaultTableModel model = tableModel(
                    "ID", "Fase", "Jogo", "Data/Hora", "Estádio", "Estado", "Resultado"
            );

            for (Game g : store.games) {
                model.addRow(new Object[]{
                        g.id,
                        g.phase,
                        g.teamA + " vs " + g.teamB,
                        g.dateTime,
                        g.stadium == null ? "" : g.stadium.name,
                        g.state,
                        g.resultText()
                });
            }

            JTable table = new JTable(model);

            table.addMouseListener(doubleClick(() -> {
                Game g = selectedGame(table);
                if (g != null) showGameDetails(g);
            }));

            center.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton generate = btn("Gerar Calendário");

        bottom.add(generate);

        generate.addActionListener(e -> generateCalendar());

        p.add(top, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        setPage("Calendário", p);
    }

    private JTable getTableFromPanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane scrollPane) {
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();

                if (view instanceof JTable table) {
                    return table;
                }
            }
        }

        return null;
    }

    private void generateCalendar() {
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
        Stadium stadium = store.stadiums.get(0);
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

    private void showGameDetails(Game g) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText(
                "Fase: " + g.phase + "\n" +
                "Jogo: " + g.teamA + " vs " + g.teamB + "\n" +
                "Data/Hora: " + g.dateTime + "\n" +
                "Estádio: " + (g.stadium == null ? "" : g.stadium.name) + "\n" +
                "Estado: " + g.state + "\n" +
                "Resultado: " + g.resultText() + "\n" +
                "Cartões amarelos: " + g.yellowA + " - " + g.yellowB + "\n" +
                "Cartões vermelhos: " + g.redA + " - " + g.redB + "\n" +
                "Posse de bola: " + g.possessionA + "% - " + (100 - g.possessionA) + "%\n"
        );
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar Dados");
        buttons.add(back);
        buttons.add(edit);
        back.addActionListener(e -> showCalendarPage());
        edit.addActionListener(e -> showGameDataForm(g));
        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        setPage("Dados Jogo", p);
    }

    private void showGameDataForm(Game g) {
        JTextField goalsA = new JTextField(String.valueOf(g.goalsA));
        JTextField goalsB = new JTextField(String.valueOf(g.goalsB));
        JTextField yellowA = new JTextField(String.valueOf(g.yellowA));
        JTextField yellowB = new JTextField(String.valueOf(g.yellowB));
        JTextField redA = new JTextField(String.valueOf(g.redA));
        JTextField redB = new JTextField(String.valueOf(g.redB));
        JTextField possessionA = new JTextField(String.valueOf(g.possessionA));
        JPanel form = formPanel();
        addRow(form, "Golos " + g.teamA, goalsA, 0);
        addRow(form, "Golos " + g.teamB, goalsB, 1);
        addRow(form, "Amarelos " + g.teamA, yellowA, 2);
        addRow(form, "Amarelos " + g.teamB, yellowB, 3);
        addRow(form, "Vermelhos " + g.teamA, redA, 4);
        addRow(form, "Vermelhos " + g.teamB, redB, 5);
        addRow(form, "Posse bola equipa A (%)", possessionA, 6);
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
            Integer ga = parseNonNegative(goalsA.getText(), "Golos inválidos.");
            Integer gb = parseNonNegative(goalsB.getText(), "Golos inválidos.");
            Integer ya = parseNonNegative(yellowA.getText(), "Cartões inválidos.");
            Integer yb = parseNonNegative(yellowB.getText(), "Cartões inválidos.");
            Integer ra = parseNonNegative(redA.getText(), "Cartões inválidos.");
            Integer rb = parseNonNegative(redB.getText(), "Cartões inválidos.");
            Integer pa = parseNonNegative(possessionA.getText(), "Percentagem inválida.");
            if (ga == null || gb == null || ya == null || yb == null || ra == null || rb == null || pa == null) return;
            if (pa > 100) {
                error("A posse de bola tem de estar entre 0 e 100.");
                return;
            }
            g.goalsA = ga; g.goalsB = gb; g.yellowA = ya; g.yellowB = yb; g.redA = ra; g.redB = rb; g.possessionA = pa;
            info("Dados editados com sucesso.");
            showGameDetails(g);
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        setPage("Editar Dados Jogo", p);
    }

    private void showTicketDetails(TicketBatch tb) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText(
                "Jogo: " + tb.game.teamA + " vs " + tb.game.teamB + "\n" +
                        "Data/Hora: " + tb.game.dateTime + "\n" +
                        "Estádio: " + tb.game.stadium.name + "\n" +
                        "Bancada: " + tb.stand.name + "\n" +
                        "Preço: " + money(tb.price) + "\n" +
                        "Bilhetes disponíveis: " + tb.available + "\n" +
                        "Bilhetes vendidos: " + tb.sold + "\n" +
                        "Estado do jogo: " + tb.game.state + "\n"
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton buy = btn("Comprar Bilhete");
        JButton edit = btn("Editar Bilhete");
        JButton delete = btn("Eliminar Bilhete");

        buttons.add(back);
        buttons.add(buy);
        buttons.add(edit);
        buttons.add(delete);

        back.addActionListener(e -> showTicketsPage());
        buy.addActionListener(e -> buyTicket(tb));
        edit.addActionListener(e -> showTicketForm(tb));
        delete.addActionListener(e -> deleteTicket(tb));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        setPage("Bilhete", p);
    }

    private void showTicketsPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton create = btn("Criar Bilhetes");
        JButton buy = btn("Comprar Bilhete");
        JButton edit = btn("Editar Bilhete");
        JButton delete = btn("Eliminar Bilhete");
        top.add(create); top.add(buy); top.add(edit); top.add(delete);
        DefaultTableModel model = tableModel("ID", "Jogo", "Data", "Estádio", "Bancada", "Preço", "Disponíveis", "Vendidos", "Fase");
        for (TicketBatch t : store.tickets) {
            model.addRow(new Object[]{t.id, t.game.teamA + " vs " + t.game.teamB, t.game.dateTime, t.game.stadium.name, t.stand.name, money(t.price), t.available, t.sold, t.game.phase});
        }
        JTable table = new JTable(model);
        create.addActionListener(e -> showTicketForm(null));
        buy.addActionListener(e -> {
            TicketBatch tb = selectedTicket(table);
            if (tb != null) showTicketDetails(tb);
        });

        edit.addActionListener(e -> {
            TicketBatch tb = selectedTicket(table);
            if (tb != null) showTicketDetails(tb);
        });

        delete.addActionListener(e -> {
            TicketBatch tb = selectedTicket(table);
            if (tb != null) showTicketDetails(tb);
        });
        table.addMouseListener(doubleClick(() -> {
            TicketBatch tb = selectedTicket(table);
            if (tb != null) showTicketDetails(tb);
        }));
        p.add(top, BorderLayout.NORTH);
        p.add(store.tickets.isEmpty() ? empty("Não existem bilhetes criados.") : new JScrollPane(table), BorderLayout.CENTER);
        setPage("Lista de Bilhetes", p);
    }

    private void showTicketForm(TicketBatch editing) {
        boolean isEdit = editing != null;
        JComboBox<Game> gameBox = new JComboBox<>();
        for (Game g : store.games) gameBox.addItem(g);
        JComboBox<Stand> standBox = new JComboBox<>();
        NumberFormat euroFormat = NumberFormat.getNumberInstance();
        euroFormat.setMinimumFractionDigits(2);
        euroFormat.setMaximumFractionDigits(2);

        JFormattedTextField price = new JFormattedTextField(euroFormat);
        price.setValue(isEdit ? editing.price : 0.00);
        price.setHorizontalAlignment(JTextField.RIGHT);
        price.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        JTextField qty = new JTextField(isEdit ? String.valueOf(editing.available + editing.sold) : "");
        qty.setEnabled(!isEdit);
        if (isEdit) {
            gameBox.addItem(editing.game);
            gameBox.setSelectedItem(editing.game);
            gameBox.setEnabled(false);
            standBox.addItem(editing.stand);
            standBox.setSelectedItem(editing.stand);
            standBox.setEnabled(false);
        } else {
            refreshStandsCombo(gameBox, standBox);
            gameBox.addActionListener(e -> refreshStandsCombo(gameBox, standBox));
        }
        JPanel form = formPanel();
        addRow(form, "Jogo *", gameBox, 0);
        addRow(form, "Bancada *", standBox, 1);
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.add(price, BorderLayout.CENTER);
        pricePanel.add(new JLabel(" €"), BorderLayout.EAST);

        addRow(form, "Preço *", pricePanel, 2);
        addRow(form, "Quantidade *", qty, 3);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel); buttons.add(save);
        cancel.addActionListener(e -> showTicketsPage());
        save.addActionListener(e -> {
            if (blank(price) || (!isEdit && blank(qty))) {
                error("Erro: campos obrigatórios em falta.");
                return;
            }
            try {
                price.commitEdit();
            } catch (java.text.ParseException ex) {
                error("O preço tem de ser numérico.");
                return;
            }

            Double priceVal = ((Number) price.getValue()).doubleValue();
            if (priceVal < 0) { error("O preço não pode ser negativo."); return; }
            if (isEdit) {
                if (editing.game.state == GameState.EM_CURSO || editing.game.state == GameState.CONCLUIDO) {
                    error("O preço do bilhete só pode ser editado até ao início do jogo.");
                    return;
                }
                editing.price = priceVal;
                info("Preço editado com sucesso.");
            } else {
                Game game = (Game) gameBox.getSelectedItem();
                Stand stand = (Stand) standBox.getSelectedItem();
                if (game == null || stand == null) {
                    error("Seleciona um jogo e uma bancada.");
                    return;
                }
                Integer q = parseInt(qty.getText(), "A quantidade tem de ser um número inteiro.");
                if (q == null) return;
                if (q <= 0) { error("A quantidade deve ser positiva."); return; }
                if (q > stand.capacity) { error("A quantidade de bilhetes não pode ultrapassar a lotação máxima da bancada."); return; }
                store.tickets.add(new TicketBatch(store.nextId(), game, stand, priceVal, q));
                info("Bilhete criado com sucesso.");
            }
            showTicketsPage();
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        setPage(isEdit ? "Editar Preço do Bilhete" : "Definir Bilhetes", p);
    }

    private void refreshStandsCombo(JComboBox<Game> gameBox, JComboBox<Stand> standBox) {
        standBox.removeAllItems();
        Game g = (Game) gameBox.getSelectedItem();
        if (g != null && g.stadium != null) {
            for (Stand st : g.stadium.stands) standBox.addItem(st);
        }
    }

    private void buyTicket(TicketBatch tb) {
        if (tb.available <= 0) {
            error("Erro: bilhetes esgotados.");
            return;
        }
        tb.available--;
        tb.sold++;
        store.soldTickets.add(new SoldTicket("BIL-" + store.nextId(), tb, tb.price));
        info("Compra de bilhete realizada com sucesso.");
        showTicketsPage();
    }

    private void deleteTicket(TicketBatch tb) {
        if (tb.game.state == GameState.EM_CURSO || tb.game.state == GameState.CONCLUIDO) {
            error("Não é permitido eliminar bilhetes após o início do jogo.");
            return;
        }
        if (tb.sold > 0) {
            error("Não é possível eliminar um bilhete com vendas associadas.");
            return;
        }
        store.tickets.remove(tb);
        info("Bilhete eliminado com sucesso.");
        showTicketsPage();
    }

    private void showSponsorsPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton create = btn("Criar Patrocínio");
        JButton edit = btn("Editar");
        JButton delete = btn("Remover Patrocinador");

        top.add(create);
        top.add(edit);
        top.add(delete);

        DefaultTableModel model = tableModel("ID", "Nome", "Descrição", "Valor");
        for (Sponsor s : store.sponsors) {
            model.addRow(new Object[]{s.id, s.name, s.description, money(s.value)});
        }

        JTable table = new JTable(model);

        create.addActionListener(e -> showSponsorForm(null));

        edit.addActionListener(e -> {
            Sponsor s = selectedSponsor(table);
            if (s != null) showSponsorForm(s);
        });

        delete.addActionListener(e -> {
            Sponsor s = selectedSponsor(table);
            if (s != null) deleteSponsor(s);
        });

        table.addMouseListener(doubleClick(() -> {
            Sponsor s = selectedSponsor(table);
            if (s != null) showSponsorDetails(s);
        }));

        p.add(top, BorderLayout.NORTH);
        p.add(store.sponsors.isEmpty()
                ? empty("Não há patrocinadores registados.")
                : new JScrollPane(table), BorderLayout.CENTER);

        setPage("Patrocínios", p);
    }

    private void showSponsorDetails(Sponsor sponsor) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText(
                "Nome: " + sponsor.name + "\n" +
                        "Descrição: " + sponsor.description + "\n" +
                        "Valor: " + money(sponsor.value) + "\n"
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar");
        JButton delete = btn("Remover Patrocinador");

        buttons.add(back);
        buttons.add(edit);
        buttons.add(delete);

        back.addActionListener(e -> showSponsorsPage());
        edit.addActionListener(e -> showSponsorForm(sponsor));
        delete.addActionListener(e -> deleteSponsor(sponsor));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        setPage("Patrocínios do Torneio", p);
    }

    private void showSponsorForm(Sponsor editing) {
        boolean isEdit = editing != null;

        if (isTournamentStarted()) {
            error(isEdit
                    ? "Erro: Não é possível editar um patrocínio de um torneio já iniciado."
                    : "Erro: Os patrocínios devem ser registados antes do início do jogo.");
            return;
        }

        JTextField name = new JTextField(isEdit ? editing.name : "");
        JTextField description = new JTextField(isEdit ? editing.description : "");
        NumberFormat euroFormat = NumberFormat.getNumberInstance();
        euroFormat.setMinimumFractionDigits(2);
        euroFormat.setMaximumFractionDigits(2);

        JFormattedTextField value = new JFormattedTextField(euroFormat);
        value.setValue(isEdit ? editing.value : 0.00);
        value.setHorizontalAlignment(JTextField.RIGHT);
        value.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        JPanel form = formPanel();
        addRow(form, "Nome *", name, 0);
        addRow(form, "Descrição *", description, 1);
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.add(value, BorderLayout.CENTER);
        valuePanel.add(new JLabel(" €"), BorderLayout.EAST);

        addRow(form, "Valor *", valuePanel, 2);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showSponsorsPage());

        save.addActionListener(e -> {
            if (blank(name, description, value)) {
                error("Erro: Campos obrigatórios em falta.");
                return;
            }

            try {
                value.commitEdit();
            } catch (java.text.ParseException ex) {
                error("O valor do patrocínio tem de ser numérico.");
                return;
            }

            Double valueVal = ((Number) value.getValue()).doubleValue();
            if (valueVal == null) return;

            if (valueVal <= 0) {
                error("O valor do patrocínio deve ser positivo.");
                return;
            }

            if (isEdit) {
                editing.name = name.getText().trim();
                editing.description = description.getText().trim();
                editing.value = valueVal;
                info("Dados salvos com sucesso.");
            } else {
                store.sponsors.add(new Sponsor(
                        store.nextId(),
                        name.getText().trim(),
                        description.getText().trim(),
                        valueVal
                ));
                info("Patrocínio criado com sucesso.");
            }

            showSponsorsPage();
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        setPage(isEdit ? "Editar Patrocínios" : "Criar Patrocínios", p);
    }

    private void addRow(JPanel form, String label, Component comp, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(comp, c);
    }

    private void deleteSponsor(Sponsor sponsor) {
        if (isTournamentStarted()) {
            error("Erro: Não é possível eliminar um patrocínio associado a um torneio já iniciado.");
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Eliminar o patrocínio " + sponsor.name + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (opt == JOptionPane.YES_OPTION) {
            store.sponsors.remove(sponsor);
            info("Patrocínio eliminado com sucesso.");
            showSponsorsPage();
        }
    }

    private Sponsor selectedSponsor(JTable table) {
        int id = selectedId(table);
        return id < 0 ? null : store.findSponsor(id);
    }

    private boolean isTournamentStarted() {
        return store.games.stream().anyMatch(g ->
                g.state == GameState.EM_CURSO || g.state == GameState.CONCLUIDO
        );
    }

    private void showStatsPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = tableModel(
                "Nº",
                "Jogador",
                "GM",
                "CA",
                "CV"
        );

        int index = 1;

        for (Team team : store.teams) {
            for (Player player : team.players) {
                model.addRow(new Object[]{
                        index++,
                        player.name,
                        0,
                        0,
                        0
                });
            }
        }

        JTable table = new JTable(model);

        if (index == 1) {
            p.add(empty("Ainda não existem dados estatísticos registados para este torneio."), BorderLayout.CENTER);
        } else {
            p.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        setPage("Estatísticas Gerais", p);
    }

    private void showBillingPage() {
        double ticketRevenue = store.soldTickets.stream().mapToDouble(s -> s.price).sum();
        double sponsorRevenue = store.sponsors.stream().mapToDouble(s -> s.value).sum();
        double totalRevenue = ticketRevenue + sponsorRevenue;

        JPanel p = new JPanel(new BorderLayout(12, 12));

        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setFont(new Font("Arial", Font.PLAIN, 16));
        summary.setText(
                "Receita de Patrocínios: " + money(sponsorRevenue) + "\n\n" +
                        "Receita de Jogos: " + money(ticketRevenue) + "\n\n" +
                        "Faturação Total: " + money(totalRevenue)
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 10));
        JButton byGames = btn("Ver faturação por jogos");
        JButton bySponsors = btn("Ver faturação por patrocínios");

        buttons.add(byGames);
        buttons.add(bySponsors);

        byGames.addActionListener(e -> showBillingByGamesPage());
        bySponsors.addActionListener(e -> showBillingBySponsorsPage());

        p.add(summary, BorderLayout.CENTER);
        p.add(buttons, BorderLayout.SOUTH);

        setPage("Faturação " + store.tournament.name, p);
    }

    private void showBillingByGamesPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = tableModel(
                "Jogo",
                "Data",
                "Estádio",
                "Bilhetes vendidos",
                "Receita"
        );

        for (TicketBatch tb : store.tickets) {
            model.addRow(new Object[]{
                    tb.game.teamA + " x " + tb.game.teamB,
                    tb.game.dateTime,
                    tb.game.stadium == null ? "" : tb.game.stadium.name,
                    tb.sold,
                    money(tb.sold * tb.price)
            });
        }

        JTable table = new JTable(model);

        JButton back = btn("Voltar");
        back.addActionListener(e -> showBillingPage());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(back);

        p.add(store.tickets.isEmpty()
                ? empty("Não existem dados financeiros registados para jogos.")
                : new JScrollPane(table), BorderLayout.CENTER);

        p.add(bottom, BorderLayout.SOUTH);

        setPage("Faturação por Jogos", p);
    }

    private void showBillingBySponsorsPage() {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = tableModel(
                "Patrocínios",
                "Valor"
        );

        for (Sponsor s : store.sponsors) {
            model.addRow(new Object[]{
                    s.name,
                    money(s.value)
            });
        }

        JTable table = new JTable(model);

        JButton back = btn("Voltar");
        back.addActionListener(e -> showBillingPage());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(back);

        p.add(store.sponsors.isEmpty()
                ? empty("Não existem dados financeiros registados para patrocínios.")
                : new JScrollPane(table), BorderLayout.CENTER);

        p.add(bottom, BorderLayout.SOUTH);

        setPage("Faturação dos Patrocínios", p);
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

    private String money(double value) {
        return String.format("%.2f €", value);
    }

    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Informação", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE); }

    private int selectedId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { error("Seleciona primeiro um registo da lista."); return -1; }
        return (Integer) table.getValueAt(row, 0);
    }

    private Team selectedTeam(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findTeam(id); }
    private Stadium selectedStadium(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findStadium(id); }
    private Game selectedGame(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findGame(id); }
    private TicketBatch selectedTicket(JTable table) { int id = selectedId(table); return id < 0 ? null : store.findTicket(id); }
    private Player selectedPlayer(JTable table, Team team) { int id = selectedId(table); return id < 0 ? null : team.players.stream().filter(p -> p.id == id).findFirst().orElse(null); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TorneioApp().setVisible(true));
    }

    static class Store {
        private static Store instance;
        Tournament tournament = new Tournament("Torneio A", "2026-06-01", "2026-06-30", 3);
        List<Team> teams = new ArrayList<>();
        List<Stadium> stadiums = new ArrayList<>();
        List<Game> games = new ArrayList<>();
        List<TicketBatch> tickets = new ArrayList<>();
        List<SoldTicket> soldTickets = new ArrayList<>();
        List<Sponsor> sponsors = new ArrayList<>();

        boolean calendarGenerated = false;
        private int sequence = 1;

        private Store() {}

        static Store getInstance() {
            if (instance == null) instance = new Store();
            return instance;
        }

        int nextId() { return sequence++; }

        Team findTeam(int id) { return teams.stream().filter(t -> t.id == id).findFirst().orElse(null); }
        Stadium findStadium(int id) { return stadiums.stream().filter(s -> s.id == id).findFirst().orElse(null); }
        Game findGame(int id) { return games.stream().filter(g -> g.id == id).findFirst().orElse(null); }
        TicketBatch findTicket(int id) { return tickets.stream().filter(t -> t.id == id).findFirst().orElse(null); }
        Sponsor findSponsor(int id) { return sponsors.stream().filter(s -> s.id == id).findFirst().orElse(null); }

        boolean teamNameExists(String name, Team ignore) { return teams.stream().anyMatch(t -> t != ignore && t.name.equalsIgnoreCase(name)); }
        boolean teamAcronymExists(String acronym, Team ignore) { return teams.stream().anyMatch(t -> t != ignore && t.acronym.equalsIgnoreCase(acronym)); }
        boolean playerNumberExists(Team team, int number, Player ignore) { return team.players.stream().anyMatch(p -> p != ignore && p.number == number); }
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

    static class Stadium {
        int id, capacity; String name, location;
        List<Stand> stands = new ArrayList<>();
        Stadium(int id, String name, String location, int capacity) {
            this.id = id; this.name = name; this.location = location; this.capacity = capacity;
        }
        int totalStandCapacity() { return stands.stream().mapToInt(s -> s.capacity).sum(); }
    }

    static class Stand {
        int id, capacity; String name;
        Stand(int id, String name, int capacity) { this.id = id; this.name = name; this.capacity = capacity; }
        public String toString() { return name + " (" + capacity + " lugares)"; }
    }

    enum GameState { POR_AGENDAR, AGENDADO, EM_CURSO, CONCLUIDO, CANCELADO }

    static class Game {
        int id, goalsA = 0, goalsB = 0, yellowA = 0, yellowB = 0, redA = 0, redB = 0, possessionA = 50;
        String phase, teamA, teamB, dateTime;
        Stadium stadium;
        GameState state = GameState.AGENDADO;
        Game(int id, String phase, String teamA, String teamB, String dateTime, Stadium stadium) {
            this.id = id; this.phase = phase; this.teamA = teamA; this.teamB = teamB; this.dateTime = dateTime; this.stadium = stadium;
        }
        String resultText() { return goalsA + " - " + goalsB; }
        public String toString() { return phase + " | " + teamA + " vs " + teamB + " | " + dateTime; }
    }

    static class TicketBatch {
        int id, available, sold = 0;
        Game game; Stand stand; double price;
        TicketBatch(int id, Game game, Stand stand, double price, int available) {
            this.id = id; this.game = game; this.stand = stand; this.price = price; this.available = available;
        }
    }

    static class Sponsor {
        int id;
        String name;
        String description;
        double value;

        Sponsor(int id, String name, String description, double value) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.value = value;
        }
    }

    static class SoldTicket {
        String code; TicketBatch batch; double price;
        SoldTicket(String code, TicketBatch batch, double price) { this.code = code; this.batch = batch; this.price = price; }
    }
}
