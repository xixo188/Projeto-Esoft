import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EquipaPainelControlador {

    public static void showTeamsPage(TorneioApp app, Store store) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JButton createButton = createButton("Criar Equipa");
        JButton viewButton = createButton("Ver Equipa");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(createButton);
        topPanel.add(viewButton);

        DefaultTableModel model = createTableModel(
                "ID",
                "Nome",
                "Sigla",
                "Treinador",
                "Jogadores"
        );

        for (TorneioApp.Team team : store.teams) {
            model.addRow(new Object[]{
                    team.id,
                    team.name,
                    team.acronym,
                    team.coach,
                    team.players.size()
            });
        }

        JTable table = new JTable(model);

        createButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error("Não se pode inserir equipas a meio do torneio.");
                return;
            }

            showTeamForm(app, store, null);
        });

        viewButton.addActionListener(e -> {
            TorneioApp.Team team = selectedTeam(app, table, store);

            if (team != null) {
                showTeamDetails(app, store, team);
            }
        });

        table.addMouseListener(doubleClick(() -> {
            TorneioApp.Team team = selectedTeam(app, table, store);

            if (team != null) {
                showTeamDetails(app, store, team);
            }
        }));

        if (store.teams.isEmpty()) {
            panel.add(
                    emptyLabel("Não há equipas criadas."),
                    BorderLayout.CENTER
            );
        } else {
            panel.add(
                    new JScrollPane(table),
                    BorderLayout.CENTER
            );
        }

        panel.add(topPanel, BorderLayout.NORTH);

        app.setPage("Lista de Equipas", panel);
    }

    public static void showTeamDetails(
            TorneioApp app,
            Store store,
            TorneioApp.Team team
    ) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        infoArea.setText(
                "Nome: " + team.name + "\n" +
                        "Sigla: " + team.acronym + "\n" +
                        "Treinador: " + team.coach + "\n" +
                        "Uniforme principal: " + team.homeKit + "\n" +
                        "Uniforme alternativo: " + team.awayKit + "\n" +
                        "Emblema: " +
                        (
                                team.emblem == null || team.emblem.isBlank()
                                        ? "Opcional / não definido"
                                        : team.emblem
                        )
        );

        DefaultTableModel model = createTableModel(
                "ID",
                "Nome",
                "Número",
                "Posição"
        );

        for (TorneioApp.Player player : team.players) {
            model.addRow(new Object[]{
                    player.id,
                    player.name,
                    player.number,
                    player.position
            });
        }

        JTable playerTable = new JTable(model);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = createButton("Voltar");
        JButton editButton = createButton("Editar Equipa");
        JButton deleteButton = createButton("Eliminar Equipa");
        JButton addPlayerButton = createButton("Inserir Jogador");
        JButton viewPlayerButton = createButton("Ver Jogador");

        buttons.add(backButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(addPlayerButton);
        buttons.add(viewPlayerButton);

        backButton.addActionListener(e ->
                showTeamsPage(app, store)
        );

        editButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error("Não se pode editar equipas a meio do torneio.");
                return;
            }

            showTeamForm(app, store, team);
        });

        deleteButton.addActionListener(e ->
                deleteTeam(app, store, team)
        );

        addPlayerButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error(
                        "Não é possível inserir jogadores depois de existir calendarização."
                );
                return;
            }

            JogadorPainelControlador.showPlayerForm(
                    app,
                    store,
                    team,
                    null
            );
        });

        viewPlayerButton.addActionListener(e -> {
            TorneioApp.Player player =
                    JogadorPainelControlador.selectedPlayer(
                            app,
                            playerTable,
                            team
                    );

            if (player != null) {
                JogadorPainelControlador.showPlayerDetails(
                        app,
                        store,
                        team,
                        player
                );
            }
        });

        playerTable.addMouseListener(doubleClick(() -> {
            TorneioApp.Player player =
                    JogadorPainelControlador.selectedPlayer(
                            app,
                            playerTable,
                            team
                    );

            if (player != null) {
                JogadorPainelControlador.showPlayerDetails(
                        app,
                        store,
                        team,
                        player
                );
            }
        }));

        JPanel centerPanel = new JPanel(
                new GridLayout(1, 2, 12, 12)
        );

        centerPanel.add(new JScrollPane(infoArea));

        if (team.players.isEmpty()) {
            centerPanel.add(
                    emptyLabel("Ainda não foram inseridos jogadores.")
            );
        } else {
            centerPanel.add(new JScrollPane(playerTable));
        }

        panel.add(buttons, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        app.setPage("Equipa", panel);
    }

    private static void showTeamForm(
            TorneioApp app,
            Store store,
            TorneioApp.Team editing
    ) {
        boolean isEdit = editing != null;

        JTextField nameField = new JTextField(
                isEdit ? editing.name : ""
        );

        JTextField acronymField = new JTextField(
                isEdit ? editing.acronym : ""
        );

        JTextField coachField = new JTextField(
                isEdit ? editing.coach : ""
        );

        JTextField homeKitField = new JTextField(
                isEdit ? editing.homeKit : ""
        );

        JTextField awayKitField = new JTextField(
                isEdit ? editing.awayKit : ""
        );

        JTextField emblemField = new JTextField(
                isEdit ? editing.emblem : ""
        );

        JPanel form = createFormPanel();

        addRow(form, "Nome da Equipa *", nameField, 0);
        addRow(form, "Sigla *", acronymField, 1);
        addRow(form, "Treinador *", coachField, 2);
        addRow(form, "Uniforme Principal *", homeKitField, 3);
        addRow(form, "Uniforme Alternativo *", awayKitField, 4);
        addRow(form, "Emblema", emblemField, 5);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton cancelButton = createButton("Cancelar");
        JButton confirmButton = createButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        cancelButton.addActionListener(e -> {
            if (isEdit) {
                app.info("Edição de dados da equipa cancelada.");
                showTeamDetails(app, store, editing);
            } else {
                app.info("Criação de equipa cancelada.");
                showTeamsPage(app, store);
            }
        });

        confirmButton.addActionListener(e -> {
            if (store.calendarGenerated) {
                app.error(
                        isEdit
                                ? "Não se pode editar equipas a meio do torneio."
                                : "Não se pode inserir equipas a meio do torneio."
                );
                return;
            }

            if (
                    blank(
                            nameField,
                            acronymField,
                            coachField,
                            homeKitField,
                            awayKitField
                    )
            ) {
                app.error(
                        "Preenche todos os campos obrigatórios."
                );
                return;
            }

            String name = nameField.getText().trim();
            String acronym = acronymField.getText().trim();
            String coach = coachField.getText().trim();
            String homeKit = homeKitField.getText().trim();
            String awayKit = awayKitField.getText().trim();
            String emblem = emblemField.getText().trim();

            if (store.teamNameExists(name, editing)) {
                app.error(
                        "Já existe uma equipa com esse nome."
                );
                return;
            }

            if (store.teamAcronymExists(acronym, editing)) {
                app.error(
                        "Já existe uma equipa com essa sigla."
                );
                return;
            }

            if (
                    isEdit &&
                            editing.sameData(
                                    name,
                                    acronym,
                                    coach,
                                    homeKit,
                                    awayKit,
                                    emblem
                            )
            ) {
                app.info(
                        "Os dados inseridos são iguais aos dados originais."
                );

                showTeamsPage(app, store);
                return;
            }

            if (isEdit) {
                editing.name = name;
                editing.acronym = acronym;
                editing.coach = coach;
                editing.homeKit = homeKit;
                editing.awayKit = awayKit;
                editing.emblem = emblem;

                app.info("Equipa editada com sucesso.");
            } else {
                TorneioApp.Team newTeam =
                        new TorneioApp.Team(
                                store.nextId(),
                                name,
                                acronym,
                                coach,
                                homeKit,
                                awayKit,
                                emblem
                        );

                store.teams.add(newTeam);

                app.info("Equipa criada com sucesso.");
            }

            showTeamsPage(app, store);
        });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        app.setPage(
                isEdit ? "Editar Equipa" : "Criar Equipa",
                panel
        );
    }

    private static void deleteTeam(
            TorneioApp app,
            Store store,
            TorneioApp.Team team
    ) {
        if (store.calendarGenerated) {
            app.error(
                    "Não se pode eliminar equipas a meio do torneio."
            );
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                app,
                "Pretende eliminar a equipa " + team.name + "?",
                "Confirmar eliminação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            app.info("Eliminação da equipa cancelada.");
            return;
        }

        store.teams.remove(team);

        app.info("Equipa eliminada com sucesso.");

        showTeamsPage(app, store);
    }

    private static TorneioApp.Team selectedTeam(
            TorneioApp app,
            JTable table,
            Store store
    ) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            app.error(
                    "Seleciona primeiro uma equipa da lista."
            );
            return null;
        }

        int id = (Integer) table.getValueAt(selectedRow, 0);

        return store.findTeam(id);
    }

    private static DefaultTableModel createTableModel(
            String... columns
    ) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };
    }

    private static JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        return form;
    }

    private static void addRow(
            JPanel form,
            String label,
            JComponent component,
            int row
    ) {
        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(6, 6, 6, 6);

        constraints.fill =
                GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        form.add(
                new JLabel(label),
                constraints
        );

        constraints.gridx = 1;
        constraints.weightx = 1;

        component.setPreferredSize(
                new Dimension(320, 28)
        );

        form.add(component, constraints);
    }

    private static boolean blank(
            JTextField... fields
    ) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private static JLabel emptyLabel(String text) {
        JLabel label = new JLabel(
                text,
                SwingConstants.CENTER
        );

        label.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        18
                )
        );

        return label;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    private static java.awt.event.MouseAdapter doubleClick(
            Runnable action
    ) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(
                    java.awt.event.MouseEvent event
            ) {
                if (event.getClickCount() == 2) {
                    action.run();
                }
            }
        };
    }
}