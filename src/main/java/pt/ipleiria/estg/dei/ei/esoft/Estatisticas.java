import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Estatisticas {

    public static void showEstatisticas(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Nº", "Jogador", "GM", "CA", "CV"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int numero = 1;

        for (TorneioApp.Team team : store.teams) {
            for (TorneioApp.Player player : team.players) {
                model.addRow(new Object[]{
                        numero++,
                        player.name,
                        0,
                        0,
                        0
                });
            }
        }

        if (numero == 1) {
            p.add(empty("Ainda não existem dados estatísticos registados para este torneio."), BorderLayout.CENTER);
        } else {
            JTable table = new JTable(model);
            table.setRowHeight(28);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            JPanel tabelaPanel = new JPanel(new BorderLayout());
            tabelaPanel.setBackground(new Color(220, 220, 220));
            tabelaPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            tabelaPanel.setPreferredSize(new Dimension(520, 230));

            tabelaPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel center = new JPanel(new GridBagLayout());
            center.add(tabelaPanel);

            p.add(center, BorderLayout.CENTER);
        }

        app.setPage("Estatísticas gerais", p);
    }

    private static JLabel empty(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 18));
        return l;
    }
}