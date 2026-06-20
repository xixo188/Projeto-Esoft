import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Calendario {

    public static void showCalendario(TorneioApp app, Store store) {
        if (store.games.isEmpty()) {
            JPanel p = new JPanel(new BorderLayout());
            JLabel empty = new JLabel("Ainda não há calendário definido", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.PLAIN, 18));

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton generate = new JButton("Gerar Calendário");
            bottom.add(generate);
            generate.addActionListener(e -> app.generateCalendar());

            p.add(empty, BorderLayout.CENTER);
            p.add(bottom, BorderLayout.SOUTH);
            app.setPage("Calendário", p);
            return;
        }

        showFaseGrupos(app, store);
    }

    private static void showFaseGrupos(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        JButton groupPhase = criarBotaoFase("Fase de Grupos", true);
        JButton eliminationPhase = criarBotaoFase("Fase de eliminação", false);
        top.add(groupPhase);
        top.add(eliminationPhase);

        eliminationPhase.addActionListener(e -> {
            try { showFaseEliminacao(app, store); }
            catch (Exception ex) { JOptionPane.showMessageDialog(app, "Erro ao abrir Fase Eliminação.", "Erro", JOptionPane.ERROR_MESSAGE); }
        });

        // --- TABELAS DE CLASSIFICAÇÃO COM PONTOS REAIS ---
        int numGrupos = (int) Math.ceil((double) store.teams.size() / 4);
        JPanel tabelasPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        tabelasPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < numGrupos; i++) {
            String nomeGrupo = "Grupo " + (char) ('A' + i);
            DefaultTableModel model = new DefaultTableModel(new String[]{"Equipa", "Pts", "V", "E", "D"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };

            int startIndex = i * 4;
            int endIndex = Math.min(startIndex + 4, store.teams.size());

            for (int j = startIndex; j < endIndex; j++) {
                TorneioApp.Team t = store.teams.get(j);
                int pts = 0, v = 0, e = 0, d = 0;

                for (TorneioApp.Game g : store.games) {
                    if (g.phase.equals("Fase de Grupos") && g.state == TorneioApp.GameState.CONCLUIDO) {
                        if (g.teamA.equals(t.name)) {
                            if (g.goalsA > g.goalsB) { pts += 3; v++; }
                            else if (g.goalsA == g.goalsB) { pts += 1; e++; }
                            else { d++; }
                        } else if (g.teamB.equals(t.name)) {
                            if (g.goalsB > g.goalsA) { pts += 3; v++; }
                            else if (g.goalsA == g.goalsB) { pts += 1; e++; }
                            else { d++; }
                        }
                    }
                }
                model.addRow(new Object[]{t.name, pts, v, e, d});
            }

            JTable table = new JTable(model);
            table.setPreferredScrollableViewportSize(new Dimension(450, table.getRowHeight() * 4));

            JPanel grupoContainer = new JPanel(new BorderLayout());
            JLabel tituloGrupo = new JLabel(nomeGrupo, SwingConstants.CENTER);
            tituloGrupo.setFont(new Font("Arial", Font.BOLD, 16));
            tituloGrupo.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            grupoContainer.add(tituloGrupo, BorderLayout.NORTH);
            JPanel wrapTable = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapTable.add(new JScrollPane(table));
            grupoContainer.add(wrapTable, BorderLayout.CENTER);
            tabelasPanel.add(grupoContainer);
        }

        // --- LISTA DE JOGOS AGENDADOS COM BOTÕES ---
        DefaultTableModel jogosModel = new DefaultTableModel(new String[]{"Data ✏️", "Equipa 1", "Equipa 2", "Estádio", "Ação"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        List<TorneioApp.Game> faseGruposJogos = store.games.stream().filter(g -> g.phase.equals("Fase de Grupos")).collect(Collectors.toList());

        for (TorneioApp.Game g : faseGruposJogos) {
            String textoAcao = "Finalizado";
            if (g.state == TorneioApp.GameState.AGENDADO) textoAcao = "Começar Jogo";
            else if (g.state == TorneioApp.GameState.EM_CURSO) textoAcao = "Gerir Jogo";
            jogosModel.addRow(new Object[]{g.dateTime, g.teamA, g.teamB, g.stadium.nome, textoAcao});
        }

        JTable tabelaJogos = new JTable(jogosModel);
        tabelaJogos.setRowHeight(32);

        // Estilizar Data
        tabelaJogos.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel lbl = new JLabel(value != null ? value.toString() : "");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setForeground(new Color(0, 102, 204));
            return lbl;
        });

        // Estilizar Botões de Ação
        tabelaJogos.getColumnModel().getColumn(4).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton(value != null ? value.toString() : "");
            btn.setFocusPainted(false);
            if ("Começar Jogo".equals(value)) { btn.setBackground(new Color(64, 107, 109)); btn.setForeground(Color.WHITE); }
            else if ("Gerir Jogo".equals(value)) { btn.setBackground(new Color(220, 150, 40)); btn.setForeground(Color.WHITE); }
            else { btn.setBackground(Color.LIGHT_GRAY); btn.setForeground(Color.DARK_GRAY); }
            return btn;
        });

        // Eventos de Clique
        tabelaJogos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelaJogos.getSelectedRow();
                int col = tabelaJogos.getSelectedColumn();

                if (row >= 0 && row < faseGruposJogos.size()) {
                    TorneioApp.Game g = faseGruposJogos.get(row);

                    // Alterar a data
                    if (col == 0) {
                        if (g.state == TorneioApp.GameState.AGENDADO) {
                            String novaData = JOptionPane.showInputDialog(app, "Nova Data/Hora do jogo:", g.dateTime);
                            if (novaData != null && !novaData.trim().isEmpty()) {
                                g.dateTime = novaData.trim();
                                jogosModel.setValueAt(g.dateTime, row, 0);
                                showFaseGrupos(app, store);
                            }
                        } else app.error("Não podes alterar a data de um jogo que já começou ou já terminou.");
                    }

                    // Ações do Jogo
                    if (col == 4) {
                        if (g.state == TorneioApp.GameState.AGENDADO) {
                            int opt = JOptionPane.showConfirmDialog(app, "Começar o jogo " + g.teamA + " vs " + g.teamB + "?", "Começar Jogo", JOptionPane.YES_NO_OPTION);
                            if (opt == JOptionPane.YES_OPTION) {
                                g.state = TorneioApp.GameState.EM_CURSO;

                                app.info("Jogo iniciado!");

                                app.showGameDetails(g);
                            }
                        } else if (g.state == TorneioApp.GameState.EM_CURSO) {
                            app.showGameDetails(g);

                        } else if (g.state == TorneioApp.GameState.CONCLUIDO) {
                            app.showGameDetails(g);
                        }
                    }
                }
            }
        });

        JPanel painelJogos = new JPanel(new BorderLayout());
        painelJogos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "Jogos Agendados", 0, 0, new Font("Arial", Font.BOLD, 14)));
        painelJogos.add(new JScrollPane(tabelaJogos), BorderLayout.CENTER);
        painelJogos.setPreferredSize(new Dimension(800, 250));

        JPanel centroPanel = new JPanel(new BorderLayout(0, 10));
        centroPanel.add(tabelasPanel, BorderLayout.NORTH);
        if (!store.games.isEmpty()) centroPanel.add(painelJogos, BorderLayout.CENTER);

        // --- BOTÕES INTELIGENTES NO FUNDO ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        boolean torneioIniciado = store.games.stream().anyMatch(g -> g.state == TorneioApp.GameState.EM_CURSO || g.state == TorneioApp.GameState.CONCLUIDO);

        if (!torneioIniciado) {
            JButton generate = new JButton(store.calendarGenerated ? "Sorteio Novo Calendário" : "Gerar Calendário");
            generate.addActionListener(e -> {
                int opt = JOptionPane.YES_OPTION;
                if (store.calendarGenerated) {
                    opt = JOptionPane.showConfirmDialog(app, "Atenção: Gerar um novo calendário apaga o atual. Queres mesmo continuar?", "Confirmação", JOptionPane.YES_NO_OPTION);
                }
                if (opt == JOptionPane.YES_OPTION) {
                    app.generateCalendar();
                }
            });
            bottom.add(generate);
        }

        if (store.calendarGenerated) {
            boolean quartosCriados =
                    existeFase(store, "Quartos de Final");

            JButton apurar = new JButton(
                    quartosCriados
                            ? "Ver Eliminatórias"
                            : "Apurar Eliminatórias"
            );

            apurar.setBackground(new Color(40, 167, 69));
            apurar.setForeground(Color.WHITE);

            apurar.addActionListener(e -> {
                if (
                        !existeFase(
                                store,
                                "Quartos de Final"
                        )
                ) {
                    boolean generated =
                            apurarFaseEliminacao(
                                    app,
                                    store
                            );

                    if (!generated) {
                        return;
                    }
                }

                showFaseEliminacao(app, store);
            });

            bottom.add(apurar);
        }

        p.add(top, BorderLayout.NORTH);
        p.add(centroPanel, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        app.setPage("Calendário", p);
    }

    public static void showFaseEliminacao(TorneioApp app, Store store) {
        atualizarEliminatorias(app, store);

        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        JButton groupPhase = criarBotaoFase("Fase de Grupos", false);
        JButton eliminationPhase = criarBotaoFase("Fase de eliminação", true);
        top.add(groupPhase);
        top.add(eliminationPhase);

        groupPhase.addActionListener(e -> {
            try { showFaseGrupos(app, store); }
            catch (Exception ex) { JOptionPane.showMessageDialog(app, "Erro ao carregar.", "Erro", JOptionPane.ERROR_MESSAGE); }
        });

        JPanel chavesPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        chavesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        List<TorneioApp.Game> quartos = store.games.stream().filter(g -> g.phase.equals("Quartos de Final")).collect(Collectors.toList());
        List<TorneioApp.Game> semis = store.games.stream().filter(g -> g.phase.equals("Semifinais")).collect(Collectors.toList());
        List<TorneioApp.Game> finais = store.games.stream().filter(g -> g.phase.equals("Final")).collect(Collectors.toList());

        chavesPanel.add(criarColunaFase(app, store, "Quartos de final", quartos, 4));
        chavesPanel.add(criarColunaFase(app, store, "Semifinais", semis, 2));
        chavesPanel.add(criarColunaFase(app, store, "Grande Final", finais, 1));

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(chavesPanel), BorderLayout.CENTER);
        app.setPage("Calendário", p);
    }

    private static JPanel criarColunaFase(TorneioApp app, Store store, String titulo, List<TorneioApp.Game> jogos, int totalEsperado) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(80, 80, 80));
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < totalEsperado; i++) {
            if (totalEsperado == 2 && i == 0) panel.add(Box.createVerticalStrut(55));
            if (totalEsperado == 1) panel.add(Box.createVerticalStrut(130));

            TorneioApp.Game g = (i < jogos.size()) ? jogos.get(i) : null;
            JPanel cartao = criarCartaoJogo(app, store, g);

            cartao.setPreferredSize(new Dimension(280, 165));
            cartao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
            panel.add(cartao);

            panel.add(Box.createVerticalStrut(15));
            if (totalEsperado == 2 && i == 0) panel.add(Box.createVerticalStrut(55));
        }
        return panel;
    }

    private static JPanel criarCartaoJogo(
            TorneioApp app,
            Store store,
            TorneioApp.Game game
    ) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(92, 92, 92));
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(70, 70, 70)
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        if (game == null) {
            JLabel pendingLabel = new JLabel(
                    "A determinar",
                    SwingConstants.CENTER
            );

            pendingLabel.setForeground(
                    new Color(225, 225, 225)
            );

            pendingLabel.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            16
                    )
            );

            card.add(
                    pendingLabel,
                    BorderLayout.CENTER
            );

            return card;
        }

        JPanel header = new JPanel(
                new BorderLayout(8, 0)
        );

        header.setOpaque(false);

        JLabel dateLabel = textoBranco(
                game.dateTime
        );

        dateLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        JLabel stateLabel = new JLabel(
                formatGameState(game),
                SwingConstants.CENTER
        );

        stateLabel.setOpaque(true);
        stateLabel.setForeground(Color.WHITE);
        stateLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );

        stateLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        3,
                        8,
                        3,
                        8
                )
        );

        if (
                game.state ==
                        TorneioApp.GameState.CONCLUIDO
        ) {
            if (
                    !"Fase de Grupos".equals(game.phase) &&
                    game.goalsA == game.goalsB
            ) {
                stateLabel.setText(
                        "Empate inválido"
                );

                stateLabel.setBackground(
                        new Color(190, 55, 55)
                );
            } else {
                stateLabel.setBackground(
                        new Color(40, 130, 75)
                );
            }
        } else if (
                game.state ==
                        TorneioApp.GameState.EM_CURSO
        ) {
            stateLabel.setBackground(
                    new Color(210, 135, 35)
            );
        } else {
            stateLabel.setBackground(
                    new Color(65, 105, 135)
            );
        }

        JLabel resultLabel = textoBranco(
                game.resultText()
        );

        resultLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        resultLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        header.add(
                dateLabel,
                BorderLayout.WEST
        );

        header.add(
                stateLabel,
                BorderLayout.CENTER
        );

        header.add(
                resultLabel,
                BorderLayout.EAST
        );

        JPanel teamsPanel = new JPanel();
        teamsPanel.setOpaque(false);
        teamsPanel.setLayout(
                new BoxLayout(
                        teamsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        teamsPanel.add(
                createTeamMatchLine(
                        store,
                        game,
                        game.teamA,
                        game.goalsA,
                        game.goalsA > game.goalsB
                )
        );

        teamsPanel.add(
                Box.createVerticalStrut(6)
        );

        teamsPanel.add(
                new JSeparator()
        );

        teamsPanel.add(
                Box.createVerticalStrut(6)
        );

        teamsPanel.add(
                createTeamMatchLine(
                        store,
                        game,
                        game.teamB,
                        game.goalsB,
                        game.goalsB > game.goalsA
                )
        );

        JPanel footer = new JPanel(
                new BorderLayout()
        );

        footer.setOpaque(false);

        JLabel stadiumLabel = textoBranco(
                game.stadium == null
                        ? "Estádio não definido"
                        : game.stadium.nome
        );

        stadiumLabel.setForeground(
                new Color(210, 210, 210)
        );

        stadiumLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        String actionText;

        if (
                game.state ==
                        TorneioApp.GameState.AGENDADO
        ) {
            actionText =
                    "Clique para começar";
        } else if (
                game.state ==
                        TorneioApp.GameState.EM_CURSO
        ) {
            actionText =
                    "Clique para gerir";
        } else {
            actionText =
                    "Jogo terminado";
        }

        JLabel actionLabel = textoBranco(
                actionText
        );

        actionLabel.setForeground(
                new Color(210, 210, 210)
        );

        actionLabel.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        11
                )
        );

        footer.add(
                stadiumLabel,
                BorderLayout.WEST
        );

        footer.add(
                actionLabel,
                BorderLayout.EAST
        );

        card.add(
                header,
                BorderLayout.NORTH
        );

        card.add(
                teamsPanel,
                BorderLayout.CENTER
        );

        card.add(
                footer,
                BorderLayout.SOUTH
        );

        boolean participantsDefined =
                !"A determinar".equals(game.teamA) &&
                !"A determinar".equals(game.teamB);

        if (participantsDefined) {
            card.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            card.addMouseListener(
                    new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent event
                        ) {
                            if (
                                    game.state ==
                                            TorneioApp.GameState.AGENDADO
                            ) {
                                int option =
                                        JOptionPane.showConfirmDialog(
                                                app,
                                                "Deseja dar o apito inicial?",
                                                "Começar Jogo",
                                                JOptionPane.YES_NO_OPTION
                                        );

                                if (
                                        option ==
                                                JOptionPane.YES_OPTION
                                ) {
                                    game.state =
                                            TorneioApp.GameState.EM_CURSO;

                                    app.info(
                                            "Jogo iniciado!"
                                    );

                                    app.showGameDetails(
                                            game
                                    );
                                }
                            } else if (
                                    game.state ==
                                            TorneioApp.GameState.EM_CURSO
                            ) {
                                app.showGameDetails(
                                        game
                                );
                            } else if (
                                    game.state ==
                                            TorneioApp.GameState.CONCLUIDO
                            ) {
                                app.showGameDetails(
                                        game
                                );
                            }
                        }
                    }
            );
        } else {
            card.setToolTipText(
                    "Os participantes deste jogo ainda não estão definidos."
            );
        }

        return card;
    }

    private static JPanel createTeamMatchLine(
            Store store,
            TorneioApp.Game game,
            String teamName,
            int goals,
            boolean winner
    ) {
        JPanel panel = new JPanel(
                new BorderLayout(8, 2)
        );

        panel.setOpaque(false);

        JLabel teamLabel = textoBranco(
                winner
                        ? "✓ " + teamName
                        : teamName
        );

        teamLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        if (winner) {
            teamLabel.setForeground(
                    new Color(125, 225, 150)
            );
        }

        JLabel scoreLabel = textoBranco(
                String.valueOf(goals)
        );

        scoreLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        JLabel scorersLabel = new JLabel(
                "Marcadores: " +
                        getScorersText(
                                store,
                                game,
                                teamName,
                                goals
                        )
        );

        scorersLabel.setForeground(
                new Color(210, 210, 210)
        );

        scorersLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11
                )
        );

        panel.add(
                teamLabel,
                BorderLayout.WEST
        );

        panel.add(
                scoreLabel,
                BorderLayout.EAST
        );

        panel.add(
                scorersLabel,
                BorderLayout.SOUTH
        );

        return panel;
    }

    private static String getScorersText(
            Store store,
            TorneioApp.Game game,
            String teamName,
            int goals
    ) {
        List<String> scorers =
                store.findEventsByGame(game)
                        .stream()
                        .filter(event ->
                                event.type ==
                                        TipoEventoJogo.GOLO
                        )
                        .filter(event ->
                                event.team != null &&
                                event.team.name.equalsIgnoreCase(
                                        teamName
                                )
                        )
                        .map(event ->
                                event.minute +
                                        "' " +
                                        event.playerName()
                        )
                        .collect(
                                Collectors.toList()
                        );

        if (!scorers.isEmpty()) {
            return String.join(
                    ", ",
                    scorers
            );
        }

        if (goals > 0) {
            return "não registados";
        }

        return "—";
    }

    private static String formatGameState(
            TorneioApp.Game game
    ) {
        if (
                game.state ==
                        TorneioApp.GameState.CONCLUIDO
        ) {
            return "Concluído";
        }

        if (
                game.state ==
                        TorneioApp.GameState.EM_CURSO
        ) {
            return "Em curso";
        }

        if (
                game.state ==
                        TorneioApp.GameState.AGENDADO
        ) {
            return "Agendado";
        }

        if (
                game.state ==
                        TorneioApp.GameState.CANCELADO
        ) {
            return "Cancelado";
        }

        return "Por agendar";
    }


    private static boolean apurarFaseEliminacao(
            TorneioApp app,
            Store store
    ) {
        if (
                !todosJogosFaseConcluidos(
                        store,
                        "Fase de Grupos"
                )
        ) {
            app.error(
                    "Só é possível apurar as eliminatórias depois de todos os jogos da fase de grupos estarem concluídos."
            );
            return false;
        }

        if (existeFase(store, "Quartos de Final")) {
            return true;
        }

        if (store.stadiums.isEmpty()) {
            app.error(
                    "É necessário existir pelo menos um estádio para gerar a fase de eliminação."
            );
            return false;
        }

        List<TorneioApp.Team> groupA =
                obterClassificacaoGrupo(store, 0);

        List<TorneioApp.Team> groupB =
                obterClassificacaoGrupo(store, 1);

        List<TorneioApp.Team> groupC =
                obterClassificacaoGrupo(store, 2);

        List<TorneioApp.Team> groupD =
                obterClassificacaoGrupo(store, 3);

        if (
                groupA.size() < 2 ||
                groupB.size() < 2 ||
                groupC.size() < 2 ||
                groupD.size() < 2
        ) {
            app.error(
                    "Não foi possível obter os dois primeiros classificados de todos os grupos."
            );
            return false;
        }

        TorneioApp.Team firstA = groupA.get(0);
        TorneioApp.Team secondA = groupA.get(1);

        TorneioApp.Team firstB = groupB.get(0);
        TorneioApp.Team secondB = groupB.get(1);

        TorneioApp.Team firstC = groupC.get(0);
        TorneioApp.Team secondC = groupC.get(1);

        TorneioApp.Team firstD = groupD.get(0);
        TorneioApp.Team secondD = groupD.get(1);

        LocalDateTime firstQuarterDate =
                nextDateAfterPhase(
                        store,
                        "Fase de Grupos"
                );

        /*
         * Cruzamento:
         * A1 vs B2
         * B1 vs A2
         * C1 vs D2
         * D1 vs C2
         */
        createKnockoutGame(
                store,
                "Quartos de Final",
                firstA,
                secondB,
                firstQuarterDate,
                0
        );

        createKnockoutGame(
                store,
                "Quartos de Final",
                firstB,
                secondA,
                firstQuarterDate,
                1
        );

        createKnockoutGame(
                store,
                "Quartos de Final",
                firstC,
                secondD,
                firstQuarterDate.plusDays(1),
                2
        );

        createKnockoutGame(
                store,
                "Quartos de Final",
                firstD,
                secondC,
                firstQuarterDate.plusDays(1),
                3
        );

        int restDays =
                store.tournament == null
                        ? 2
                        : Math.max(
                                1,
                                store.tournament.restDays
                        );

        LocalDateTime lastQuarterDate =
                firstQuarterDate
                        .plusDays(1)
                        .withHour(20)
                        .withMinute(30);

        LocalDateTime semifinalDate =
                lastQuarterDate
                        .plusDays(restDays)
                        .withHour(18)
                        .withMinute(0);

        createPlaceholderKnockoutGame(
                store,
                "Semifinais",
                semifinalDate,
                0
        );

        createPlaceholderKnockoutGame(
                store,
                "Semifinais",
                semifinalDate.withHour(20).withMinute(30),
                1
        );

        LocalDateTime finalDate =
                determineFinalDate(
                        store,
                        semifinalDate,
                        restDays
                );

        createPlaceholderKnockoutGame(
                store,
                "Final",
                finalDate,
                0
        );

        app.info(
                "Fase de eliminação criada com sucesso.\n" +
                "As datas e os estádios dos quartos de final, semifinais e final já estão definidos."
        );

        return true;
    }

    public static void atualizarEliminatorias(
            TorneioApp app,
            Store store
    ) {
        List<TorneioApp.Game> quarters =
                getGamesByPhase(
                        store,
                        "Quartos de Final"
                );

        List<TorneioApp.Game> semifinals =
                getGamesByPhase(
                        store,
                        "Semifinais"
                );

        List<TorneioApp.Game> finals =
                getGamesByPhase(
                        store,
                        "Final"
                );

        if (
                quarters.size() == 4 &&
                semifinals.size() == 2 &&
                quarters.stream().allMatch(game ->
                        game.state ==
                                TorneioApp.GameState.CONCLUIDO
                )
        ) {
            TorneioApp.Team winnerQ1 =
                    getWinner(store, quarters.get(0));

            TorneioApp.Team winnerQ2 =
                    getWinner(store, quarters.get(1));

            TorneioApp.Team winnerQ3 =
                    getWinner(store, quarters.get(2));

            TorneioApp.Team winnerQ4 =
                    getWinner(store, quarters.get(3));

            if (
                    winnerQ1 != null &&
                    winnerQ2 != null &&
                    winnerQ3 != null &&
                    winnerQ4 != null
            ) {
                TorneioApp.Game semifinal1 =
                        semifinals.get(0);

                TorneioApp.Game semifinal2 =
                        semifinals.get(1);

                if (
                        "A determinar".equals(semifinal1.teamA) &&
                        "A determinar".equals(semifinal1.teamB)
                ) {
                    semifinal1.teamA =
                            winnerQ1.name;

                    semifinal1.teamB =
                            winnerQ3.name;
                }

                if (
                        "A determinar".equals(semifinal2.teamA) &&
                        "A determinar".equals(semifinal2.teamB)
                ) {
                    semifinal2.teamA =
                            winnerQ2.name;

                    semifinal2.teamB =
                            winnerQ4.name;
                }
            }
        }

        if (
                semifinals.size() == 2 &&
                finals.size() == 1 &&
                semifinals.stream().allMatch(game ->
                        game.state ==
                                TorneioApp.GameState.CONCLUIDO
                )
        ) {
            TorneioApp.Team winnerS1 =
                    getWinner(store, semifinals.get(0));

            TorneioApp.Team winnerS2 =
                    getWinner(store, semifinals.get(1));

            if (winnerS1 != null && winnerS2 != null) {
                TorneioApp.Game finalGame =
                        finals.get(0);

                if (
                        "A determinar".equals(finalGame.teamA) &&
                        "A determinar".equals(finalGame.teamB)
                ) {
                    finalGame.teamA =
                            winnerS1.name;

                    finalGame.teamB =
                            winnerS2.name;
                }
            }
        }

        if (
                finals.size() == 1 &&
                finals.get(0).state ==
                        TorneioApp.GameState.CONCLUIDO
        ) {
            TorneioApp.Team champion =
                    getWinner(store, finals.get(0));

            if (
                    champion != null &&
                    store.tournament != null
            ) {
                store.tournament.state =
                        "concluído";
            }
        }
    }

    private static List<TorneioApp.Team>
    obterClassificacaoGrupo(
            Store store,
            int groupIndex
    ) {
        int startIndex = groupIndex * 4;
        int endIndex = Math.min(
                startIndex + 4,
                store.teams.size()
        );

        List<ClassificationEntry> entries =
                new ArrayList<>();

        for (int i = startIndex; i < endIndex; i++) {
            TorneioApp.Team team =
                    store.teams.get(i);

            ClassificationEntry entry =
                    new ClassificationEntry(team);

            for (TorneioApp.Game game : store.games) {
                if (
                        !"Fase de Grupos".equals(game.phase) ||
                        game.state !=
                                TorneioApp.GameState.CONCLUIDO
                ) {
                    continue;
                }

                if (game.teamA.equals(team.name)) {
                    entry.goalsFor += game.goalsA;
                    entry.goalsAgainst += game.goalsB;

                    if (game.goalsA > game.goalsB) {
                        entry.points += 3;
                    } else if (game.goalsA == game.goalsB) {
                        entry.points += 1;
                    }
                } else if (game.teamB.equals(team.name)) {
                    entry.goalsFor += game.goalsB;
                    entry.goalsAgainst += game.goalsA;

                    if (game.goalsB > game.goalsA) {
                        entry.points += 3;
                    } else if (game.goalsA == game.goalsB) {
                        entry.points += 1;
                    }
                }
            }

            entries.add(entry);
        }

        entries.sort(
                Comparator
                        .comparingInt(
                                (ClassificationEntry entry) ->
                                        entry.points
                        )
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        (ClassificationEntry entry) ->
                                                entry.goalDifference()
                                ).reversed()
                        )
                        .thenComparing(
                                Comparator.comparingInt(
                                        (ClassificationEntry entry) ->
                                                entry.goalsFor
                                ).reversed()
                        )
                        .thenComparing(
                                entry -> entry.team.name
                        )
        );

        return entries.stream()
                .map(entry -> entry.team)
                .collect(Collectors.toList());
    }

    private static boolean todosJogosFaseConcluidos(
            Store store,
            String phase
    ) {
        List<TorneioApp.Game> games =
                getGamesByPhase(store, phase);

        return !games.isEmpty() &&
                games.stream().allMatch(game ->
                        game.state ==
                                TorneioApp.GameState.CONCLUIDO
                );
    }

    private static boolean existeFase(
            Store store,
            String phase
    ) {
        return store.games.stream()
                .anyMatch(game ->
                        phase.equals(game.phase)
                );
    }

    private static List<TorneioApp.Game>
    getGamesByPhase(
            Store store,
            String phase
    ) {
        return store.games.stream()
                .filter(game ->
                        phase.equals(game.phase)
                )
                .collect(Collectors.toList());
    }

    private static TorneioApp.Team getWinner(
            Store store,
            TorneioApp.Game game
    ) {
        if (
                game == null ||
                game.state !=
                        TorneioApp.GameState.CONCLUIDO ||
                game.goalsA == game.goalsB
        ) {
            return null;
        }

        String winnerName =
                game.goalsA > game.goalsB
                        ? game.teamA
                        : game.teamB;

        return store.findTeamByName(winnerName);
    }

    private static void createPlaceholderKnockoutGame(
            Store store,
            String phase,
            LocalDateTime gameDate,
            int stadiumIndex
    ) {
        Estadio stadium =
                store.stadiums.get(
                        stadiumIndex %
                                store.stadiums.size()
                );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        TorneioApp.Game game =
                new TorneioApp.Game(
                        store.nextId(),
                        phase,
                        "A determinar",
                        "A determinar",
                        gameDate.format(formatter),
                        stadium
                );

        game.state =
                TorneioApp.GameState.AGENDADO;

        store.games.add(game);
    }

    private static LocalDateTime determineFinalDate(
            Store store,
            LocalDateTime semifinalDate,
            int restDays
    ) {
        LocalDateTime minimumFinalDate =
                semifinalDate
                        .plusDays(restDays)
                        .withHour(20)
                        .withMinute(0);

        if (
                store.tournament == null ||
                store.tournament.endDate == null ||
                store.tournament.endDate.trim().isEmpty()
        ) {
            return minimumFinalDate;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        try {
            LocalDateTime configuredFinalDate =
                    LocalDateTime.parse(
                            store.tournament.endDate.trim() +
                                    " 20:00",
                            formatter
                    );

            if (
                    configuredFinalDate.isBefore(
                            minimumFinalDate
                    )
            ) {
                return minimumFinalDate;
            }

            return configuredFinalDate;
        } catch (Exception exception) {
            return minimumFinalDate;
        }
    }

    private static void createKnockoutGame(
            Store store,
            String phase,
            TorneioApp.Team teamA,
            TorneioApp.Team teamB,
            LocalDateTime baseDate,
            int gameIndex
    ) {
        LocalDateTime gameDate =
                baseDate.withHour(
                        gameIndex % 2 == 0
                                ? 18
                                : 20
                ).withMinute(
                        gameIndex % 2 == 0
                                ? 0
                                : 30
                );

        Estadio stadium =
                store.stadiums.get(
                        gameIndex %
                                store.stadiums.size()
                );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        TorneioApp.Game game =
                new TorneioApp.Game(
                        store.nextId(),
                        phase,
                        teamA.name,
                        teamB.name,
                        gameDate.format(formatter),
                        stadium
                );

        game.state =
                TorneioApp.GameState.AGENDADO;

        store.games.add(game);
    }

    private static LocalDateTime nextDateAfterPhase(
            Store store,
            String phase
    ) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        LocalDateTime latest = null;

        for (TorneioApp.Game game : store.games) {
            if (!phase.equals(game.phase)) {
                continue;
            }

            try {
                LocalDateTime date =
                        LocalDateTime.parse(
                                game.dateTime,
                                formatter
                        );

                if (
                        latest == null ||
                        date.isAfter(latest)
                ) {
                    latest = date;
                }
            } catch (Exception ignored) {
            }
        }

        if (latest == null) {
            latest = LocalDateTime.now();
        }

        int restDays =
                store.tournament == null
                        ? 2
                        : Math.max(
                                1,
                                store.tournament.restDays
                        );

        return latest
                .plusDays(restDays)
                .withHour(18)
                .withMinute(0);
    }

    private static class ClassificationEntry {

        private final TorneioApp.Team team;

        private int points;
        private int goalsFor;
        private int goalsAgainst;

        private ClassificationEntry(
                TorneioApp.Team team
        ) {
            this.team = team;
        }

        private int goalDifference() {
            return goalsFor - goalsAgainst;
        }
    }

    private static JButton criarBotaoFase(String texto, boolean ativo) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        if (ativo) {
            btn.setBackground(new Color(64, 107, 109));
        } else {
            btn.setBackground(Color.GRAY);
        }
        return btn;
    }

    private static JLabel textoBranco(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        return lbl;
    }
}