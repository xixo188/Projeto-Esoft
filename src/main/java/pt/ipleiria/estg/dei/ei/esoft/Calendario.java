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

        DefaultTableModel model =
                new DefaultTableModel(
                        new String[]{"Grupo", "Equipa", "Pontos", "V", "E", "D"}, 0);

        for (TorneioApp.Team t : store.teams) {
            model.addRow(new Object[]{
                    "A",
                    t.name,
                    0,
                    0,
                    0,
                    0
            });
        }

        JTable table = new JTable(model);

        eliminationPhase.addActionListener(e -> {
            try {
                showFaseEliminacao(app, store);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        app,
                        "Erro: Não foi possível carregar as informações do calendário.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton generate = new JButton("Gerar Calendário");

        generate.addActionListener(e -> {
            app.generateCalendar();
        });

        bottom.add(generate);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
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

        // Painel Principal das Chaves
        JPanel chavesPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        chavesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Filtrar jogos por fase
        List<TorneioApp.Game> quartos = store.games.stream()
                .filter(g -> g.phase.equals("Quartos de Final"))
                .collect(Collectors.toList());

        List<TorneioApp.Game> semis = store.games.stream()
                .filter(g -> g.phase.equals("Semifinais"))
                .collect(Collectors.toList());

        // Coluna 1: Quartos de Final
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

        // Coluna 2: Semifinais
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

        if (g != null) {
            cartao.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cartao.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    app.showGameDetails(g);
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