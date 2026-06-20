import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DadosJogoPainelControlador {

    public static void showGameDetails(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        store.recalculateGameTotals(game);

        JPanel page = new JPanel(
                new BorderLayout(12, 12)
        );

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton backButton = createButton("Voltar");
        buttons.add(backButton);

        backButton.addActionListener(e ->
                voltarAoCalendario(
                        app,
                        store,
                        game
                )
        );

        if (
                game.state == TorneioApp.GameState.AGENDADO ||
                        game.state == TorneioApp.GameState.POR_AGENDAR
        ) {
            JButton startButton =
                    createButton("Começar Jogo");

            buttons.add(startButton);

            boolean participantsDefined =
                    participantsDefined(game);

            startButton.setEnabled(
                    participantsDefined
            );

            if (!participantsDefined) {
                startButton.setToolTipText(
                        "Os participantes deste jogo ainda não estão definidos."
                );
            }

            startButton.addActionListener(e ->
                    startGame(app, store, game)
            );
        }

        if (
                game.state ==
                        TorneioApp.GameState.EM_CURSO
        ) {
            JButton goalButton =
                    createButton("Registar Golo");

            JButton cardButton =
                    createButton("Registar Cartão");

            JButton eventButton =
                    createButton("Registar Evento");

            JButton possessionButton =
                    createButton("Editar Posse");

            JButton finishButton =
                    createButton("Concluir Jogo");

            buttons.add(goalButton);
            buttons.add(cardButton);
            buttons.add(eventButton);
            buttons.add(possessionButton);
            buttons.add(finishButton);

            goalButton.addActionListener(e ->
                    showGoalForm(app, store, game)
            );

            cardButton.addActionListener(e ->
                    showCardForm(app, store, game)
            );

            eventButton.addActionListener(e ->
                    showGeneralEventForm(
                            app,
                            store,
                            game
                    )
            );

            possessionButton.addActionListener(e ->
                    showPossessionForm(
                            app,
                            store,
                            game
                    )
            );

            finishButton.addActionListener(e ->
                    finishGame(app, store, game)
            );
        }

        JPanel dashboard =
                createDashboard(game);

        DefaultTableModel eventModel =
                new DefaultTableModel(
                        new String[]{
                                "Minuto",
                                "Acontecimento",
                                "Equipa",
                                "Jogador"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        List<EventoJogo> events =
                store.findEventsByGame(game);

        for (EventoJogo event : events) {
            eventModel.addRow(new Object[]{
                    event.minute + "'",
                    event.type.toString(),
                    event.team.name,
                    event.playerName()
            });
        }

        JTable eventsTable =
                new JTable(eventModel);

        eventsTable.setRowHeight(26);

        JPanel center = new JPanel(
                new BorderLayout(12, 12)
        );

        center.add(
                dashboard,
                BorderLayout.NORTH
        );

        if (events.isEmpty()) {
            JLabel emptyLabel = new JLabel(
                    "Ainda não existem acontecimentos registados.",
                    SwingConstants.CENTER
            );

            emptyLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            16
                    )
            );

            center.add(
                    emptyLabel,
                    BorderLayout.CENTER
            );
        } else {
            JScrollPane scrollPane =
                    new JScrollPane(eventsTable);

            scrollPane.setBorder(
                    BorderFactory.createTitledBorder(
                            "Histórico do jogo"
                    )
            );

            center.add(
                    scrollPane,
                    BorderLayout.CENTER
            );
        }

        page.add(
                buttons,
                BorderLayout.NORTH
        );

        page.add(
                center,
                BorderLayout.CENTER
        );

        app.setPage(
                "Dados do jogo",
                page
        );
    }

    private static JPanel createDashboard(
            TorneioApp.Game game
    ) {
        JPanel outer = new JPanel(
                new BorderLayout(8, 8)
        );

        outer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                game.phase
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );

        String stateText =
                game.state.toString()
                        .replace('_', ' ');

        JLabel gameInformation = new JLabel(
                game.dateTime +
                        "   |   " +
                        stateText +
                        "   |   Estádio: " +
                        (
                                game.stadium == null
                                        ? "Não definido"
                                        : game.stadium.nome
                        ),
                SwingConstants.CENTER
        );

        gameInformation.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        JPanel statistics = new JPanel(
                new GridLayout(0, 3, 8, 6)
        );

        statistics.add(new JLabel(""));

        statistics.add(
                centeredBoldLabel(game.teamA)
        );

        statistics.add(
                centeredBoldLabel(game.teamB)
        );

        addStatisticRow(
                statistics,
                "Resultado",
                String.valueOf(game.goalsA),
                String.valueOf(game.goalsB)
        );

        addStatisticRow(
                statistics,
                "Cartões amarelos",
                String.valueOf(game.yellowA),
                String.valueOf(game.yellowB)
        );

        addStatisticRow(
                statistics,
                "Cartões vermelhos",
                String.valueOf(game.redA),
                String.valueOf(game.redB)
        );

        addStatisticRow(
                statistics,
                "Faltas",
                String.valueOf(game.foulsA),
                String.valueOf(game.foulsB)
        );

        addStatisticRow(
                statistics,
                "Cantos",
                String.valueOf(game.cornersA),
                String.valueOf(game.cornersB)
        );

        addStatisticRow(
                statistics,
                "Remates",
                String.valueOf(game.shotsA),
                String.valueOf(game.shotsB)
        );

        addStatisticRow(
                statistics,
                "Foras de jogo",
                String.valueOf(game.offsidesA),
                String.valueOf(game.offsidesB)
        );

        addStatisticRow(
                statistics,
                "Posse de bola",
                game.possessionA + "%",
                (100 - game.possessionA) + "%"
        );

        outer.add(
                gameInformation,
                BorderLayout.NORTH
        );

        outer.add(
                statistics,
                BorderLayout.CENTER
        );

        return outer;
    }

    private static void startGame(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        if (!participantsDefined(game)) {
            app.error(
                    "Ainda não estão definidos os participantes deste jogo."
            );
            return;
        }

        if (
                game.state !=
                        TorneioApp.GameState.AGENDADO &&
                game.state !=
                        TorneioApp.GameState.POR_AGENDAR
        ) {
            app.error(
                    "Este jogo não pode ser iniciado."
            );
            return;
        }

        game.state =
                TorneioApp.GameState.EM_CURSO;

        app.info(
                "Jogo iniciado com sucesso."
        );

        showGameDetails(
                app,
                store,
                game
        );
    }

    private static void finishGame(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        if (
                game.state !=
                        TorneioApp.GameState.EM_CURSO
        ) {
            app.error(
                    "Apenas um jogo em curso pode ser concluído."
            );
            return;
        }

        store.recalculateGameTotals(game);

        boolean knockoutGame =
                isKnockoutGame(game);

        if (
                knockoutGame &&
                game.goalsA == game.goalsB
        ) {
            app.error(
                    "Um jogo da fase de eliminação não pode terminar empatado.\n" +
                    "Regista o golo de desempate antes de concluir o jogo."
            );
            return;
        }

        int option =
                JOptionPane.showConfirmDialog(
                        app,
                        "Pretende concluir este jogo?\n" +
                                "Depois de concluído, já não será possível alterar os dados.",
                        "Concluir Jogo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        game.state =
                TorneioApp.GameState.CONCLUIDO;

        Calendario.atualizarEliminatorias(
                app,
                store
        );

        app.info(
                "Jogo concluído com sucesso."
        );

        if (knockoutGame) {
            Calendario.showFaseEliminacao(
                    app,
                    store
            );
        } else {
            showGameDetails(
                    app,
                    store,
                    game
            );
        }
    }

    private static void showGoalForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        TeamPlayerSelection selection =
                createTeamPlayerSelection(
                        app,
                        store,
                        game,
                        false
                );

        if (selection == null) {
            return;
        }

        JTextField minuteField =
                new JTextField();

        JPanel form =
                createFormPanel();

        addRow(
                form,
                "Equipa *",
                selection.teamCombo,
                0
        );

        addRow(
                form,
                "Jogador *",
                selection.playerCombo,
                1
        );

        addRow(
                form,
                "Minuto *",
                minuteField,
                2
        );

        showEventFormPage(
                app,
                store,
                game,
                "Registar Golo",
                form,
                () -> {
                    TorneioApp.Team team =
                            selection.selectedTeam();

                    TorneioApp.Player player =
                            selection.selectedPlayer();

                    Integer minute =
                            parseMinute(
                                    app,
                                    minuteField.getText()
                            );

                    if (
                            team == null ||
                                    player == null ||
                                    minute == null
                    ) {
                        if (
                                team == null ||
                                        player == null
                        ) {
                            app.error(
                                    "Seleciona uma equipa e um jogador."
                            );
                        }

                        return;
                    }

                    registerEvent(
                            app,
                            store,
                            game,
                            team,
                            player,
                            TipoEventoJogo.GOLO,
                            minute
                    );
                }
        );
    }

    private static void showCardForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        TeamPlayerSelection selection =
                createTeamPlayerSelection(
                        app,
                        store,
                        game,
                        false
                );

        if (selection == null) {
            return;
        }

        JComboBox<TipoEventoJogo> cardTypeCombo =
                new JComboBox<>(
                        new TipoEventoJogo[]{
                                TipoEventoJogo.CARTAO_AMARELO,
                                TipoEventoJogo.CARTAO_VERMELHO
                        }
                );

        JTextField minuteField =
                new JTextField();

        JPanel form =
                createFormPanel();

        addRow(
                form,
                "Equipa *",
                selection.teamCombo,
                0
        );

        addRow(
                form,
                "Jogador *",
                selection.playerCombo,
                1
        );

        addRow(
                form,
                "Tipo de cartão *",
                cardTypeCombo,
                2
        );

        addRow(
                form,
                "Minuto *",
                minuteField,
                3
        );

        showEventFormPage(
                app,
                store,
                game,
                "Registar Cartão",
                form,
                () -> {
                    TorneioApp.Team team =
                            selection.selectedTeam();

                    TorneioApp.Player player =
                            selection.selectedPlayer();

                    TipoEventoJogo cardType =
                            (TipoEventoJogo)
                                    cardTypeCombo.getSelectedItem();

                    Integer minute =
                            parseMinute(
                                    app,
                                    minuteField.getText()
                            );

                    if (
                            team == null ||
                                    player == null ||
                                    cardType == null ||
                                    minute == null
                    ) {
                        if (
                                team == null ||
                                        player == null
                        ) {
                            app.error(
                                    "Seleciona uma equipa e um jogador."
                            );
                        }

                        return;
                    }

                    registerEvent(
                            app,
                            store,
                            game,
                            team,
                            player,
                            cardType,
                            minute
                    );
                }
        );
    }

    private static void showGeneralEventForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        TeamPlayerSelection selection =
                createTeamPlayerSelection(
                        app,
                        store,
                        game,
                        true
                );

        if (selection == null) {
            return;
        }

        JComboBox<TipoEventoJogo> eventTypeCombo =
                new JComboBox<>(
                        new TipoEventoJogo[]{
                                TipoEventoJogo.FALTA,
                                TipoEventoJogo.CANTO,
                                TipoEventoJogo.REMATE,
                                TipoEventoJogo.FORA_DE_JOGO
                        }
                );

        JTextField minuteField =
                new JTextField();

        JPanel form =
                createFormPanel();

        addRow(
                form,
                "Tipo de acontecimento *",
                eventTypeCombo,
                0
        );

        addRow(
                form,
                "Equipa *",
                selection.teamCombo,
                1
        );

        addRow(
                form,
                "Jogador",
                selection.playerCombo,
                2
        );

        addRow(
                form,
                "Minuto *",
                minuteField,
                3
        );

        showEventFormPage(
                app,
                store,
                game,
                "Registar Evento",
                form,
                () -> {
                    TipoEventoJogo eventType =
                            (TipoEventoJogo)
                                    eventTypeCombo.getSelectedItem();

                    TorneioApp.Team team =
                            selection.selectedTeam();

                    TorneioApp.Player player =
                            selection.selectedPlayer();

                    Integer minute =
                            parseMinute(
                                    app,
                                    minuteField.getText()
                            );

                    if (
                            eventType == null ||
                                    team == null ||
                                    minute == null
                    ) {
                        if (team == null) {
                            app.error(
                                    "Seleciona uma equipa."
                            );
                        }

                        return;
                    }

                    registerEvent(
                            app,
                            store,
                            game,
                            team,
                            player,
                            eventType,
                            minute
                    );
                }
        );
    }

    private static void showPossessionForm(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        JSpinner possessionASpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                game.possessionA,
                                0,
                                100,
                                1
                        )
                );

        JLabel possessionBLabel =
                new JLabel(
                        (100 - game.possessionA) + "%"
                );

        possessionASpinner.addChangeListener(e -> {
            int possessionA =
                    (Integer)
                            possessionASpinner.getValue();

            possessionBLabel.setText(
                    (100 - possessionA) + "%"
            );
        });

        JPanel form =
                createFormPanel();

        addRow(
                form,
                game.teamA + " (%)",
                possessionASpinner,
                0
        );

        addRow(
                form,
                game.teamB + " (%)",
                possessionBLabel,
                1
        );

        showEventFormPage(
                app,
                store,
                game,
                "Editar Posse de Bola",
                form,
                () -> {
                    game.possessionA =
                            (Integer)
                                    possessionASpinner.getValue();

                    app.info(
                            "Posse de bola atualizada com sucesso."
                    );

                    showGameDetails(
                            app,
                            store,
                            game
                    );
                }
        );
    }

    private static void registerEvent(
            TorneioApp app,
            Store store,
            TorneioApp.Game game,
            TorneioApp.Team team,
            TorneioApp.Player player,
            TipoEventoJogo type,
            int minute
    ) {
        if (
                !validateGameInProgress(
                        app,
                        game
                )
        ) {
            return;
        }

        EventoJogo event =
                new EventoJogo(
                        store.nextId(),
                        game,
                        team,
                        player,
                        type,
                        minute
                );

        store.addGameEvent(event);

        app.info(
                type +
                        " registado com sucesso."
        );

        showGameDetails(
                app,
                store,
                game
        );
    }

    private static void showEventFormPage(
            TorneioApp app,
            Store store,
            TorneioApp.Game game,
            String title,
            JPanel form,
            Runnable confirmAction
    ) {
        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT
                )
        );

        JButton cancelButton =
                createButton("Cancelar");

        JButton confirmButton =
                createButton("Confirmar");

        buttons.add(cancelButton);
        buttons.add(confirmButton);

        cancelButton.addActionListener(e ->
                showGameDetails(
                        app,
                        store,
                        game
                )
        );

        confirmButton.addActionListener(e -> {
            if (
                    !validateGameInProgress(
                            app,
                            game
                    )
            ) {
                return;
            }

            confirmAction.run();
        });

        JPanel page = new JPanel(
                new BorderLayout()
        );

        page.add(
                form,
                BorderLayout.NORTH
        );

        page.add(
                buttons,
                BorderLayout.SOUTH
        );

        app.setPage(
                title,
                page
        );
    }

    private static TeamPlayerSelection
    createTeamPlayerSelection(
            TorneioApp app,
            Store store,
            TorneioApp.Game game,
            boolean allowNoPlayer
    ) {
        TorneioApp.Team teamA =
                store.findTeamByName(
                        game.teamA
                );

        TorneioApp.Team teamB =
                store.findTeamByName(
                        game.teamB
                );

        if (
                teamA == null ||
                        teamB == null
        ) {
            app.error(
                    "Não foi possível encontrar as equipas deste jogo."
            );
            return null;
        }

        JComboBox<TorneioApp.Team> teamCombo =
                new JComboBox<>(
                        new TorneioApp.Team[]{
                                teamA,
                                teamB
                        }
                );

        teamCombo.setRenderer(
                createTeamRenderer()
        );

        JComboBox<TorneioApp.Player> playerCombo =
                new JComboBox<>();

        playerCombo.setRenderer(
                createPlayerRenderer()
        );

        refreshPlayers(
                teamCombo,
                playerCombo,
                allowNoPlayer
        );

        teamCombo.addActionListener(e ->
                refreshPlayers(
                        teamCombo,
                        playerCombo,
                        allowNoPlayer
                )
        );

        return new TeamPlayerSelection(
                teamCombo,
                playerCombo
        );
    }

    private static void refreshPlayers(
            JComboBox<TorneioApp.Team> teamCombo,
            JComboBox<TorneioApp.Player> playerCombo,
            boolean allowNoPlayer
    ) {
        playerCombo.removeAllItems();

        if (allowNoPlayer) {
            playerCombo.addItem(null);
        }

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

    private static ListCellRenderer
            <? super TorneioApp.Team>
    createTeamRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component
            getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label =
                        (JLabel)
                                super
                                        .getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                if (
                        value instanceof
                                TorneioApp.Team
                ) {
                    TorneioApp.Team team =
                            (TorneioApp.Team)
                                    value;

                    label.setText(team.name);
                }

                return label;
            }
        };
    }

    private static ListCellRenderer
            <? super TorneioApp.Player>
    createPlayerRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component
            getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label =
                        (JLabel)
                                super
                                        .getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                if (value == null) {
                    label.setText(
                            "Não indicar jogador"
                    );
                } else if (
                        value instanceof
                                TorneioApp.Player
                ) {
                    TorneioApp.Player player =
                            (TorneioApp.Player)
                                    value;

                    label.setText(
                            player.number +
                                    " - " +
                                    player.name
                    );
                }

                return label;
            }
        };
    }

    private static Integer parseMinute(
            TorneioApp app,
            String value
    ) {
        try {
            int minute =
                    Integer.parseInt(
                            value.trim()
                    );

            if (
                    minute < 1 ||
                            minute > 120
            ) {
                app.error(
                        "O minuto deve estar entre 1 e 120."
                );
                return null;
            }

            return minute;

        } catch (
                NumberFormatException exception
        ) {
            app.error(
                    "O minuto tem de ser um número inteiro."
            );

            return null;
        }
    }

    private static boolean participantsDefined(
            TorneioApp.Game game
    ) {
        return game != null &&
                game.teamA != null &&
                game.teamB != null &&
                !"A determinar".equals(game.teamA) &&
                !"A determinar".equals(game.teamB);
    }

    private static boolean isKnockoutGame(
            TorneioApp.Game game
    ) {
        if (game == null) {
            return false;
        }

        return "Quartos de Final".equals(game.phase) ||
                "Semifinais".equals(game.phase) ||
                "Final".equals(game.phase);
    }

    private static void voltarAoCalendario(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        if (isKnockoutGame(game)) {
            Calendario.showFaseEliminacao(
                    app,
                    store
            );
        } else {
            Calendario.showCalendario(
                    app,
                    store
            );
        }
    }

    private static boolean
    validateGameInProgress(
            TorneioApp app,
            TorneioApp.Game game
    ) {
        if (
                game.state !=
                        TorneioApp.GameState.EM_CURSO
        ) {
            app.error(
                    "Só é possível alterar dados enquanto o jogo está em curso."
            );

            return false;
        }

        return true;
    }

    private static JPanel
    createFormPanel() {
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
                new Dimension(
                        320,
                        28
                )
        );

        form.add(
                component,
                constraints
        );
    }

    private static void addStatisticRow(
            JPanel panel,
            String name,
            String valueA,
            String valueB
    ) {
        panel.add(
                new JLabel(
                        name,
                        SwingConstants.CENTER
                )
        );

        panel.add(
                new JLabel(
                        valueA,
                        SwingConstants.CENTER
                )
        );

        panel.add(
                new JLabel(
                        valueB,
                        SwingConstants.CENTER
                )
        );
    }

    private static JLabel
    centeredBoldLabel(
            String text
    ) {
        JLabel label = new JLabel(
                text,
                SwingConstants.CENTER
        );

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        return label;
    }

    private static JButton createButton(
            String text
    ) {
        JButton button =
                new JButton(text);

        button.setFocusPainted(false);

        return button;
    }

    private static class
    TeamPlayerSelection {

        private final
        JComboBox<TorneioApp.Team>
                teamCombo;

        private final
        JComboBox<TorneioApp.Player>
                playerCombo;

        TeamPlayerSelection(
                JComboBox<TorneioApp.Team>
                        teamCombo,
                JComboBox<TorneioApp.Player>
                        playerCombo
        ) {
            this.teamCombo =
                    teamCombo;

            this.playerCombo =
                    playerCombo;
        }

        TorneioApp.Team selectedTeam() {
            return (TorneioApp.Team)
                    teamCombo
                            .getSelectedItem();
        }

        TorneioApp.Player
        selectedPlayer() {
            return (TorneioApp.Player)
                    playerCombo
                            .getSelectedItem();
        }
    }
}