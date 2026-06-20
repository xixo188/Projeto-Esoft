import javax.swing.*;
import java.awt.*;

public class Estatisticas {

    public static void showEstatisticas(TorneioApp app, Store store) {
        JPanel p = new JPanel(new GridBagLayout());

        JPanel tabelaPanel = new JPanel(new GridBagLayout());
        tabelaPanel.setBackground(new Color(220, 220, 220));
        tabelaPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 4, 2, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        addCell(tabelaPanel, c, 0, "", 60, false);
        addCell(tabelaPanel, c, 1, "Jogador", 430, false);
        addCell(tabelaPanel, c, 2, "GM", 60, false);
        addCell(tabelaPanel, c, 3, "CA", 60, false);
        addCell(tabelaPanel, c, 4, "CV", 60, false);

        int linha = 1;
        int numero = 1;

        for (TorneioApp.Team team : store.teams) {
            for (TorneioApp.Player player : team.players) {

                c.gridy = linha;
                addCell(tabelaPanel, c, 0, String.valueOf(numero), 60, false);
                addCell(tabelaPanel, c, 1, player.name, 430, false);
                addCell(tabelaPanel, c, 2, "0", 60, true);
                addCell(tabelaPanel, c, 3, "0", 60, true);
                addCell(tabelaPanel, c, 4, "0", 60, true);

                linha++;
                numero++;
            }
        }

        if (numero == 1) {
            JLabel empty = new JLabel("Ainda não existem dados estatísticos registados para este torneio.");
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            p.add(empty);
        } else {
            JScrollPane scroll = new JScrollPane(
                    tabelaPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            scroll.setPreferredSize(new Dimension(720, 420));
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            p.add(scroll);
        }

        app.setPage("Estatísticas gerais", p);
    }

    private static void addCell(JPanel panel, GridBagConstraints c, int x, String text, int width, boolean whiteBox) {
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
}