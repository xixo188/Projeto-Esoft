import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Estatisticas {

    public static void showEstatisticas(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Nº", "Jogador", "Golos Marcados", "Cartões Amarelos", "Cartões Vermelhos"}, 0
        );

        int index = 1;
        for (TorneioApp.Team team : store.teams) {
            for (TorneioApp.Player player : team.players) {
                model.addRow(new Object[]{index++, player.name, 0, 0, 0});
            }
        }

        if (index == 1) {
            JLabel empty = new JLabel("Ainda não existem dados estatísticos registados para este torneio.", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            p.add(empty, BorderLayout.CENTER);
        } else {
            p.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        }

        app.setPage("Estatísticas", p);
    }
}