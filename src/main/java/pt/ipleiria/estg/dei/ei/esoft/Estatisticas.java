import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Estatisticas {

    public static void showEstatisticas(TorneioApp app, Store store) {
        JPanel p = new JPanel(new GridBagLayout());

        JPanel tabelaPanel = new JPanel(new GridBagLayout());
        tabelaPanel.setBackground(new Color(220, 220, 220));
        tabelaPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 80));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 8, 3, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        addCell(tabelaPanel, c, 0, "", 60, false);
        addCell(tabelaPanel, c, 1, "Jogador", 520, false);
        addCell(tabelaPanel, c, 2, "GM", 60, false);
        addCell(tabelaPanel, c, 3, "CA", 60, false);
        addCell(tabelaPanel, c, 4, "CV", 60, false);

        List<LinhaEstatistica> linhas = new ArrayList<>();

        for (Equipa team : store.teams) {
            for (Jogador player : team.players) {
                int gm = calcularGolosMarcados(store, player);
                int ca = calcularCartoesAmarelos(store, player);
                int cv = calcularCartoesVermelhos(store, player);

                linhas.add(new LinhaEstatistica(player.name, gm, ca, cv));
            }
        }

        linhas.sort(
                Comparator.comparingInt((LinhaEstatistica l) -> l.gm).reversed()
                        .thenComparing(Comparator.comparingInt((LinhaEstatistica l) -> l.ca).reversed())
                        .thenComparing(Comparator.comparingInt((LinhaEstatistica l) -> l.cv).reversed())
                        .thenComparing(l -> l.nome)
        );

        int linha = 1;
        int numero = 1;

        for (LinhaEstatistica item : linhas) {
            c.gridy = linha;

            addCell(tabelaPanel, c, 0, String.valueOf(numero), 60, false);
            addCell(tabelaPanel, c, 1, item.nome, 520, false);
            addCell(tabelaPanel, c, 2, item.gm > 0 ? String.valueOf(item.gm) : "", 60, true);
            addCell(tabelaPanel, c, 3, item.ca > 0 ? String.valueOf(item.ca) : "", 60, true);
            addCell(tabelaPanel, c, 4, item.cv > 0 ? String.valueOf(item.cv) : "", 60, true);

            linha++;
            numero++;
        }

        if (linhas.isEmpty()) {
            JLabel empty = new JLabel(
                    "Ainda não existem dados estatísticos registados para este torneio.",
                    SwingConstants.CENTER
            );
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            p.add(empty);
        } else {
            JScrollPane scroll = new JScrollPane(
                    tabelaPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            scroll.setPreferredSize(new Dimension(880, 460));
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            p.add(scroll);
        }

        app.setPage("Estatísticas gerais", p);
    }

    private static int calcularGolosMarcados(Store store, Jogador player) {
        int total = 0;

        for (EventoJogo evento : store.gameEvents) {
            if (evento.player == player && evento.type == TipoEventoJogo.GOLO) {
                total++;
            }
        }

        for (EstatisticaJogadorJogo stat : store.playerGameStats) {
            if (stat.player == player && !existeEventoParaJogo(store, stat.game)) {
                total += stat.goals;
            }
        }

        return total;
    }

    private static int calcularCartoesAmarelos(Store store, Jogador player) {
        int total = 0;

        for (EventoJogo evento : store.gameEvents) {
            if (evento.player == player && evento.type == TipoEventoJogo.CARTAO_AMARELO) {
                total++;
            }
        }

        for (EstatisticaJogadorJogo stat : store.playerGameStats) {
            if (stat.player == player && !existeEventoParaJogo(store, stat.game)) {
                total += stat.yellowCards;
            }
        }

        return total;
    }

    private static int calcularCartoesVermelhos(Store store, Jogador player) {
        int total = 0;

        for (EventoJogo evento : store.gameEvents) {
            if (evento.player == player && evento.type == TipoEventoJogo.CARTAO_VERMELHO) {
                total++;
            }
        }

        for (EstatisticaJogadorJogo stat : store.playerGameStats) {
            if (stat.player == player && !existeEventoParaJogo(store, stat.game)) {
                total += stat.redCards;
            }
        }

        return total;
    }

    private static boolean existeEventoParaJogo(Store store, Jogo game) {
        for (EventoJogo evento : store.gameEvents) {
            if (evento.game == game) {
                return true;
            }
        }

        return false;
    }

    private static void addCell(
            JPanel panel,
            GridBagConstraints c,
            int x,
            String text,
            int width,
            boolean whiteBox
    ) {
        c.gridx = x;

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(width, 24));
        label.setFont(new Font("Arial", Font.PLAIN, 13));

        if (x == 1) {
            label.setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (whiteBox) {
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        panel.add(label, c);
    }

    private static class LinhaEstatistica {
        String nome;
        int gm;
        int ca;
        int cv;

        LinhaEstatistica(String nome, int gm, int ca, int cv) {
            this.nome = nome;
            this.gm = gm;
            this.ca = ca;
            this.cv = cv;
        }
    }
}