import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

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
            try {
                showFaseEliminacao(app, store);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(app, "Erro: Não foi possível carregar as informações do calendário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- TABELAS DE CLASSIFICAÇÃO DOS GRUPOS ---
        int numGrupos = (int) Math.ceil((double) store.teams.size() / 4);
        JPanel tabelasPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        tabelasPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < numGrupos; i++) {
            String nomeGrupo = "Grupo " + (char) ('A' + i);
            DefaultTableModel model = new DefaultTableModel(new String[]{"Equipa", "Pts", "V", "E", "D"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            int startIndex = i * 4;
            int endIndex = Math.min(startIndex + 4, store.teams.size());
            for (int j = startIndex; j < endIndex; j++) {
                model.addRow(new Object[]{store.teams.get(j).name, 0, 0, 0, 0});
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

        // --- LISTA DE JOGOS AGENDADOS ---
        DefaultTableModel jogosModel = new DefaultTableModel(new String[]{"Data ✏️", "Equipa 1", "Equipa 2", "Estádio", "Ação"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<TorneioApp.Game> faseGruposJogos = store.games.stream()
                .filter(g -> g.phase.equals("Fase de Grupos"))
                .collect(Collectors.toList());

        for (TorneioApp.Game g : faseGruposJogos) {
            String textoAcao = "Finalizado";
            if (g.state == TorneioApp.GameState.AGENDADO) textoAcao = "Começar Jogo";
            else if (g.state == TorneioApp.GameState.EM_CURSO) textoAcao = "Gerir Jogo";

            jogosModel.addRow(new Object[]{g.dateTime, g.teamA, g.teamB, g.stadium.nome, textoAcao});
        }

        JTable tabelaJogos = new JTable(jogosModel);
        tabelaJogos.setRowHeight(32);

        // Renderizador azul para destacar que a Data é editável
        tabelaJogos.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel lbl = new JLabel(value != null ? value.toString() : "");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setForeground(new Color(0, 102, 204));
            return lbl;
        });

        // Renderizador dos Botões de Ação
        tabelaJogos.getColumnModel().getColumn(4).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton(value != null ? value.toString() : "");
            btn.setFocusPainted(false);
            if ("Começar Jogo".equals(value)) {
                btn.setBackground(new Color(64, 107, 109));
                btn.setForeground(Color.WHITE);
            } else if ("Gerir Jogo".equals(value)) {
                btn.setBackground(new Color(220, 150, 40));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setForeground(Color.DARK_GRAY);
            }
            return btn;
        });

        // CORREÇÃO BULLETPROOF: Uso de getSelectedRow e getSelectedColumn para precisão total no clique
        tabelaJogos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelaJogos.getSelectedRow();
                int col = tabelaJogos.getSelectedColumn();

                if (row >= 0 && row < faseGruposJogos.size()) {
                    TorneioApp.Game g = faseGruposJogos.get(row);

                    // Clique na Coluna Data (0)
                    if (col == 0) {
                        if (g.state == TorneioApp.GameState.AGENDADO) {
                            String novaData = JOptionPane.showInputDialog(app, "Nova Data/Hora do jogo:", g.dateTime);
                            if (novaData != null && !novaData.trim().isEmpty()) {
                                g.dateTime = novaData.trim();
                                jogosModel.setValueAt(g.dateTime, row, 0); // Altera visualmente o modelo na hora
                                showFaseGrupos(app, store); // Atualiza o ecrã completo
                            }
                        } else {
                            app.error("Não podes alterar a data de um jogo que já começou ou já terminou.");
                        }
                    }

                    // Clique na Coluna Ação (4)
                    if (col == 4) {
                        if (g.state == TorneioApp.GameState.AGENDADO) {
                            int opt = JOptionPane.showConfirmDialog(app, "Dar o apito inicial para o jogo " + g.teamA + " vs " + g.teamB + "?", "Começar Jogo", JOptionPane.YES_NO_OPTION);
                            if (opt == JOptionPane.YES_OPTION) {
                                g.state = TorneioApp.GameState.EM_CURSO;
                                app.info("O jogo começou! Clica em 'Gerir Jogo' para registar golos e cartões.");
                                showFaseGrupos(app, store);
                            }
                        } else if (g.state == TorneioApp.GameState.EM_CURSO) {
                            app.showGameDetails(g); // Só permite abrir se estiver em Curso!
                        } else {
                            app.info("Este jogo já se encontra finalizado.");
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
        if (!store.games.isEmpty()) {
            centroPanel.add(painelJogos, BorderLayout.CENTER);
        }

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton generate = new JButton("Gerar Calendário");
        generate.addActionListener(e -> app.generateCalendar());
        bottom.add(generate);

        p.add(top, BorderLayout.NORTH);
        p.add(centroPanel, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        app.setPage("Calendário", p);
    }

    private static void showFaseEliminacao(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        JButton groupPhase = criarBotaoFase("Fase de Grupos", false);
        JButton eliminationPhase = criarBotaoFase("Fase de eliminação", true);
        top.add(groupPhase);
        top.add(eliminationPhase);

        groupPhase.addActionListener(e -> {
            try {
                showFaseGrupos(app, store);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(app, "Erro: Não foi possível carregar as informações do calendário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel chavesPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        chavesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<TorneioApp.Game> quartos = store.games.stream()
                .filter(g -> g.phase.equals("Quartos de Final"))
                .collect(Collectors.toList());

        List<TorneioApp.Game> semis = store.games.stream()
                .filter(g -> g.phase.equals("Semifinais"))
                .collect(Collectors.toList());

        JPanel colQuartos = new JPanel(new GridLayout(5, 1, 0, 15));
        JLabel lblQuartos = new JLabel("Quartas de final", SwingConstants.CENTER);
        lblQuartos.setOpaque(true);
        lblQuartos.setBackground(Color.GRAY);
        lblQuartos.setForeground(Color.WHITE);
        colQuartos.add(lblQuartos);

        for(int i = 0; i < 4; i++) {
            TorneioApp.Game g = (i < quartos.size()) ? quartos.get(i) : null;
            colQuartos.add(criarCartaoJogo(app, store, g));
        }

        JPanel colSemis = new JPanel(new GridLayout(5, 1, 0, 15));
        JLabel lblSemis = new JLabel("Semifinais", SwingConstants.CENTER);
        lblSemis.setOpaque(true);
        lblSemis.setBackground(Color.GRAY);
        lblSemis.setForeground(Color.WHITE);
        colSemis.add(lblSemis);

        colSemis.add(new JLabel());
        colSemis.add(criarCartaoJogo(app, store, semis.size() > 0 ? semis.get(0) : null));
        colSemis.add(new JLabel());
        colSemis.add(criarCartaoJogo(app, store, semis.size() > 1 ? semis.get(1) : null));

        chavesPanel.add(colQuartos);
        chavesPanel.add(colSemis);

        p.add(top, BorderLayout.NORTH);
        p.add(chavesPanel, BorderLayout.CENTER);
        app.setPage("Calendário", p);
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

    private static JPanel criarCartaoJogo(TorneioApp app, Store store, TorneioApp.Game g) {
        JPanel cartao = new JPanel(new GridLayout(4, 1, 0, 2));
        cartao.setBackground(Color.GRAY);
        cartao.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String emblemaA = "⚽";
        String emblemaB = "⚽";

        JPanel linha1 = new JPanel(new GridLayout(1, 3));
        linha1.setOpaque(false);
        linha1.add(textoBranco(g != null ? g.dateTime.split(" ")[0] : "Data"));
        linha1.add(textoBranco(g != null ? g.state.toString() : "Estado"));
        linha1.add(textoBranco(g != null ? g.resultText() : "Resultado"));

        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linha2.setOpaque(false);
        linha2.add(textoBranco(emblemaA));
        linha2.add(textoBranco(g != null ? g.teamA : "A determinar"));

        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linha3.setOpaque(false);
        linha3.add(textoBranco(emblemaB));
        linha3.add(textoBranco(g != null ? g.teamB : "A determinar"));

        JPanel linha4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        linha4.setOpaque(false);
        linha4.add(textoBranco(g != null && g.stadium != null ? g.stadium.nome : "Estádio"));

        cartao.add(linha1);
        cartao.add(linha2);
        cartao.add(linha3);
        cartao.add(linha4);

        if (g != null && !g.teamA.equals("A determinar")) {
            cartao.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cartao.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (g.state == TorneioApp.GameState.AGENDADO) {
                        int opt = JOptionPane.showConfirmDialog(app, "Deseja dar o apito inicial para este jogo?", "Começar Jogo", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            g.state = TorneioApp.GameState.EM_CURSO;
                            app.info("Jogo iniciado!");
                            showFaseEliminacao(app, store);
                        }
                    } else if (g.state == TorneioApp.GameState.EM_CURSO) {
                        app.showGameDetails(g);
                    } else {
                        app.info("Este jogo já terminou.");
                    }
                }
            });
        }

        return cartao;
    }

    private static JLabel textoBranco(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        return lbl;
    }
}