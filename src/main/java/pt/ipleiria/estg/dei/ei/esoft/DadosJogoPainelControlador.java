import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DadosJogoPainelControlador {

    public static void showGameDetails(TorneioApp app, Store store, TorneioApp.Game game) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        boolean jogoComEstatisticas =
                game.state == TorneioApp.GameState.EM_CURSO ||
                        game.state == TorneioApp.GameState.CONCLUIDO;

        int yellowA = jogoComEstatisticas ? game.yellowA : 0;
        int yellowB = jogoComEstatisticas ? game.yellowB : 0;
        int redA = jogoComEstatisticas ? game.redA : 0;
        int redB = jogoComEstatisticas ? game.redB : 0;
        int goalsA = jogoComEstatisticas ? game.goalsA : 0;
        int goalsB = jogoComEstatisticas ? game.goalsB : 0;
        int possessionA = jogoComEstatisticas ? game.possessionA : 0;
        int possessionB = jogoComEstatisticas ? 100 - game.possessionA : 0;

        JPanel dados = new JPanel(new GridBagLayout());
        dados.setBackground(new Color(220, 220, 220));
        dados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dados.setPreferredSize(new Dimension(800, 430));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel equipaA = new JLabel(game.teamA, SwingConstants.CENTER);
        JLabel equipaB = new JLabel(game.teamB, SwingConstants.CENTER);

        equipaA.setFont(new Font("Arial", Font.BOLD, 24));
        equipaB.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField data = campo(game.dateTime, 200);
        JTextField estado = campo(game.state.toString(), 120);

        JTextField amarelosA = campo(String.valueOf(yellowA), 115);
        JTextField amarelosB = campo(String.valueOf(yellowB), 115);
        JTextField vermelhosA = campo(String.valueOf(redA), 115);
        JTextField vermelhosB = campo(String.valueOf(redB), 115);
        JTextField posseA = campo(String.valueOf(possessionA), 115);
        JTextField posseB = campo(String.valueOf(possessionB), 115);
        JTextField golosA = campo(String.valueOf(goalsA), 115);
        JTextField golosB = campo(String.valueOf(goalsB), 115);

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
        dados.add(new JLabel(game.teamA, SwingConstants.CENTER), c);

        c.gridx = 1;
        dados.add(new JLabel("vs", SwingConstants.CENTER), c);

        c.gridx = 3;
        dados.add(new JLabel(game.teamB, SwingConstants.CENTER), c);

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
        dados.add(new JLabel("Estádio: " + (game.stadium == null ? "" : game.stadium.nome)), c);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(dados);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = createButton("Voltar");
        JButton cardsButton = createButton("Inserir Cartões");
        JButton goalsButton = createButton("Inserir Golos");
        JButton editDataButton = createButton("Editar Dados");

        buttons.add(backButton);
        buttons.add(cardsButton);
        buttons.add(goalsButton);
        buttons.add(editDataButton);

        backButton.addActionListener(e -> Calendario.showCalendario(app, store));

        cardsButton.addActionListener(e -> {
            if (!validateGameInProgress(app, game)) return;
            showCardsForm(app, store, game);
        });

        goalsButton.addActionListener(e -> {
            if (!validateGameInProgress(app, game)) return;
            showGoalsForm(app, store, game);
        });

        editDataButton.addActionListener(e -> {
            if (!validateGameInProgress(app, game)) return;
            showGameDataForm(app, store, game);
        });

        panel.add(buttons, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        app.setPage("Dados jogo", panel);
    }

    private static JTextField campo(String valor, int largura) {
        JTextField field = new JTextField(valor);

        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.CENTER);

        field.setPreferredSize(new Dimension(largura, 30));

        field.setFont(new Font("Arial", Font.PLAIN, 14));

        return field;
    }

    private static void showCardsForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        PlayerSelection selection =
                createPlayerSelection(app, store, game);

        if (selection == null) {
            return;
        }

        JTextField yellowField = new JTextField("0");
        JTextField redField = new JTextField("0");

        JPanel form = createFormPanel();

        addRow(form, "Equipa *", selection.teamCombo, 0);
        addRow(form, "Jogador *", selection.playerCombo, 1);
        addRow(form, "Cartões amarelos *", yellowField, 2);
        addRow(form, "Cartões vermelhos *", redField, 3);

        JPanel buttons = createButtonsPanel();

        JButton cancelButton = createButton("Cancelar");
        JButton confirmButton = createButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        cancelButton.addActionListener(e ->
                showGameDetails(app, store, game)
        );

        confirmButton.addActionListener(e -> {
            if (!validateGameInProgress(app, game)) {
                return;
            }

            TorneioApp.Team team =
                    (TorneioApp.Team)
                            selection.teamCombo.getSelectedItem();

            TorneioApp.Player player =
                    (TorneioApp.Player)
                            selection.playerCombo.getSelectedItem();

            if (team == null || player == null) {
                app.error(
                        "Seleciona uma equipa e um jogador."
                );
                return;
            }

            Integer yellowCards = parseNonNegative(
                    app,
                    yellowField.getText(),
                    "O número de cartões amarelos é inválido."
            );

            Integer redCards = parseNonNegative(
                    app,
                    redField.getText(),
                    "O número de cartões vermelhos é inválido."
            );

            if (yellowCards == null || redCards == null) {
                return;
            }

            if (yellowCards == 0 && redCards == 0) {
                app.error(
                        "Insere pelo menos um cartão."
                );
                return;
            }

            EstatisticaJogadorJogo stat =
                    store.findOrCreatePlayerGameStats(
                            game,
                            team,
                            player
                    );

            stat.yellowCards += yellowCards;
            stat.redCards += redCards;

            store.recalculateGameTotals(game);

            app.info("Cartões inseridos com sucesso.");

            showGameDetails(app, store, game);
        });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        app.setPage("Inserir Cartões", panel);
    }

    private static void showGoalsForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        PlayerSelection selection =
                createPlayerSelection(app, store, game);

        if (selection == null) {
            return;
        }

        JTextField goalsField = new JTextField("0");

        JPanel form = createFormPanel();

        addRow(form, "Equipa *", selection.teamCombo, 0);
        addRow(form, "Jogador *", selection.playerCombo, 1);
        addRow(form, "Golos *", goalsField, 2);

        JPanel buttons = createButtonsPanel();

        JButton cancelButton = createButton("Cancelar");
        JButton confirmButton = createButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        cancelButton.addActionListener(e ->
                showGameDetails(app, store, game)
        );

        confirmButton.addActionListener(e -> {
            if (!validateGameInProgress(app, game)) {
                return;
            }

            TorneioApp.Team team =
                    (TorneioApp.Team)
                            selection.teamCombo.getSelectedItem();

            TorneioApp.Player player =
                    (TorneioApp.Player)
                            selection.playerCombo.getSelectedItem();

            if (team == null || player == null) {
                app.error(
                        "Seleciona uma equipa e um jogador."
                );
                return;
            }

            Integer goals = parseNonNegative(
                    app,
                    goalsField.getText(),
                    "O número de golos é inválido."
            );

            if (goals == null) {
                return;
            }

            if (goals == 0) {
                app.error(
                        "O número de golos deve ser superior a zero."
                );
                return;
            }

            EstatisticaJogadorJogo stat =
                    store.findOrCreatePlayerGameStats(
                            game,
                            team,
                            player
                    );

            stat.goals += goals;

            store.recalculateGameTotals(game);

            app.info("Golos inseridos com sucesso.");

            showGameDetails(app, store, game);
        });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        app.setPage("Inserir Golos", panel);
    }

    private static void showGameDataForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        JTextField possessionAField =
                new JTextField(
                        String.valueOf(game.possessionA)
                );

        JTextField cornersAField =
                new JTextField(
                        String.valueOf(game.cornersA)
                );

        JTextField cornersBField =
                new JTextField(
                        String.valueOf(game.cornersB)
                );

        JTextField foulsAField =
                new JTextField(
                        String.valueOf(game.foulsA)
                );

        JTextField foulsBField =
                new JTextField(
                        String.valueOf(game.foulsB)
                );

        JTextField shotsAField =
                new JTextField(
                        String.valueOf(game.shotsA)
                );

        JTextField shotsBField =
                new JTextField(
                        String.valueOf(game.shotsB)
                );

        JTextField offsidesAField =
                new JTextField(
                        String.valueOf(game.offsidesA)
                );

        JTextField offsidesBField =
                new JTextField(
                        String.valueOf(game.offsidesB)
                );

        JPanel form = createFormPanel();

        addRow(
                form,
                "Posse de bola de " + game.teamA + " (%) *",
                possessionAField,
                0
        );

        addRow(
                form,
                "Cantos de " + game.teamA + " *",
                cornersAField,
                1
        );

        addRow(
                form,
                "Cantos de " + game.teamB + " *",
                cornersBField,
                2
        );

        addRow(
                form,
                "Faltas de " + game.teamA + " *",
                foulsAField,
                3
        );

        addRow(
                form,
                "Faltas de " + game.teamB + " *",
                foulsBField,
                4
        );

        addRow(
                form,
                "Remates de " + game.teamA + " *",
                shotsAField,
                5
        );

        addRow(
                form,
                "Remates de " + game.teamB + " *",
                shotsBField,
                6
        );

        addRow(
                form,
                "Foras de jogo de " + game.teamA + " *",
                offsidesAField,
                7
        );

        addRow(
                form,
                "Foras de jogo de " + game.teamB + " *",
                offsidesBField,
                8
        );

        JPanel buttons = createButtonsPanel();

        JButton cancelButton = createButton("Cancelar");
        JButton confirmButton = createButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        cancelButton.addActionListener(e ->
                showGameDetails(app, store, game)
        );

        confirmButton.addActionListener(e -> {
            if (!validateGameInProgress(app, game)) {
                return;
            }

            Integer possessionA = parseNonNegative(
                    app,
                    possessionAField.getText(),
                    "A posse de bola é inválida."
            );

            Integer cornersA = parseNonNegative(
                    app,
                    cornersAField.getText(),
                    "Os cantos da equipa A são inválidos."
            );

            Integer cornersB = parseNonNegative(
                    app,
                    cornersBField.getText(),
                    "Os cantos da equipa B são inválidos."
            );

            Integer foulsA = parseNonNegative(
                    app,
                    foulsAField.getText(),
                    "As faltas da equipa A são inválidas."
            );

            Integer foulsB = parseNonNegative(
                    app,
                    foulsBField.getText(),
                    "As faltas da equipa B são inválidas."
            );

            Integer shotsA = parseNonNegative(
                    app,
                    shotsAField.getText(),
                    "Os remates da equipa A são inválidos."
            );

            Integer shotsB = parseNonNegative(
                    app,
                    shotsBField.getText(),
                    "Os remates da equipa B são inválidos."
            );

            Integer offsidesA = parseNonNegative(
                    app,
                    offsidesAField.getText(),
                    "Os foras de jogo da equipa A são inválidos."
            );

            Integer offsidesB = parseNonNegative(
                    app,
                    offsidesBField.getText(),
                    "Os foras de jogo da equipa B são inválidos."
            );

            if (
                    possessionA == null ||
                            cornersA == null ||
                            cornersB == null ||
                            foulsA == null ||
                            foulsB == null ||
                            shotsA == null ||
                            shotsB == null ||
                            offsidesA == null ||
                            offsidesB == null
            ) {
                return;
            }

            if (possessionA > 100) {
                app.error(
                        "A posse de bola deve estar entre 0 e 100."
                );
                return;
            }

            game.possessionA = possessionA;

            game.cornersA = cornersA;
            game.cornersB = cornersB;

            game.foulsA = foulsA;
            game.foulsB = foulsB;

            game.shotsA = shotsA;
            game.shotsB = shotsB;

            game.offsidesA = offsidesA;
            game.offsidesB = offsidesB;

            app.info("Dados do jogo editados com sucesso.");

            showGameDetails(app, store, game);
        });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(
                new JScrollPane(form),
                BorderLayout.CENTER
        );

        panel.add(buttons, BorderLayout.SOUTH);

        app.setPage("Editar Dados do Jogo", panel);
    }

    private static PlayerSelection createPlayerSelection(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        TorneioApp.Team teamA =
                store.findTeamByName(game.teamA);

        TorneioApp.Team teamB =
                store.findTeamByName(game.teamB);

        if (teamA == null || teamB == null) {
            app.error(
                    "Não foi possível encontrar as equipas associadas a este jogo."
            );
            return null;
        }

        JComboBox<TorneioApp.Team> teamCombo =
                new JComboBox<>();

        teamCombo.addItem(teamA);
        teamCombo.addItem(teamB);

        teamCombo.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {
                        JLabel label =
                                (JLabel) super.getListCellRendererComponent(
                                        list,
                                        value,
                                        index,
                                        isSelected,
                                        cellHasFocus
                                );

                        if (value instanceof TorneioApp.Team team) {
                            label.setText(team.name);
                        }

                        return label;
                    }
                }
        );

        JComboBox<TorneioApp.Player> playerCombo =
                new JComboBox<>();

        playerCombo.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {
                        JLabel label =
                                (JLabel) super.getListCellRendererComponent(
                                        list,
                                        value,
                                        index,
                                        isSelected,
                                        cellHasFocus
                                );

                        if (value instanceof TorneioApp.Player player) {
                            label.setText(
                                    player.number +
                                            " - " +
                                            player.name
                            );
                        }

                        return label;
                    }
                }
        );

        refreshPlayers(teamCombo, playerCombo);

        teamCombo.addActionListener(e ->
                refreshPlayers(teamCombo, playerCombo)
        );

        return new PlayerSelection(
                teamCombo,
                playerCombo
        );
    }

    private static void refreshPlayers(
            JComboBox<TorneioApp.Team> teamCombo,
            JComboBox<TorneioApp.Player> playerCombo
    ) {
        playerCombo.removeAllItems();

        TorneioApp.Team selectedTeam =
                (TorneioApp.Team)
                        teamCombo.getSelectedItem();

        if (selectedTeam == null) {
            return;
        }

        for (
                TorneioApp.Player player :
                selectedTeam.players
        ) {
            playerCombo.addItem(player);
        }
    }

    private static boolean validateGameInProgress(
            TorneioApp app,
            TorneioApp.Game game
    ) {
        if (game.state != TorneioApp.GameState.EM_CURSO) {
            app.error(
                    "Não é possível inserir ou editar dados enquanto o jogo não está em curso."
            );
            return false;
        }

        return true;
    }

    private static Integer parseNonNegative(
            TorneioApp app,
            String value,
            String errorMessage
    ) {
        try {
            int number = Integer.parseInt(
                    value.trim()
            );

            if (number < 0) {
                app.error(errorMessage);
                return null;
            }

            return number;

        } catch (NumberFormatException exception) {
            app.error(errorMessage);
            return null;
        }
    }

    private static JPanel createFormPanel() {
        JPanel form = new JPanel(
                new GridBagLayout()
        );

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

    private static JPanel createButtonsPanel() {
        return new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
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

    private static JLabel emptyLabel(String text) {
        JLabel label = new JLabel(
                text,
                SwingConstants.CENTER
        );

        label.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        return label;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);

        return button;
    }

    private static class PlayerSelection {

        JComboBox<TorneioApp.Team> teamCombo;
        JComboBox<TorneioApp.Player> playerCombo;

        PlayerSelection(
                JComboBox<TorneioApp.Team> teamCombo,
                JComboBox<TorneioApp.Player> playerCombo
        ) {
            this.teamCombo = teamCombo;
            this.playerCombo = playerCombo;
        }
    }
}