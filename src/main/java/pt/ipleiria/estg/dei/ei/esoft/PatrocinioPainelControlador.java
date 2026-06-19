import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;

public class PatrocinioPainelControlador {

    public static void showSponsorsPage(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton create = new JButton("Criar Patrocínio");
        JButton edit = new JButton("Editar");
        JButton delete = new JButton("Remover Patrocinador");

        top.add(create);
        top.add(edit);
        top.add(delete);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nome", "Descrição", "Valor"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Patrocinio s : store.patrocinios) {
            model.addRow(new Object[]{s.id, s.nome, s.descricao, app.money(s.valor)});
        }

        JTable table = new JTable(model);

        create.addActionListener(e -> showSponsorForm(app, store, null));

        edit.addActionListener(e -> {
            Patrocinio s = selectedSponsor(app, table, store);
            if (s != null) showSponsorForm(app, store, s);
        });

        delete.addActionListener(e -> {
            Patrocinio s = selectedSponsor(app, table, store);
            if (s != null) deleteSponsor(app, store, s);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Patrocinio s = selectedSponsor(app, table, store);
                    if (s != null) showSponsorDetails(app, store, s);
                }
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(store.patrocinios.isEmpty()
                ? empty("Não há patrocinadores registados.")
                : new JScrollPane(table), BorderLayout.CENTER);

        app.setPage("Patrocínios", p);
    }

    private static void showSponsorDetails(TorneioApp app, Store store, Patrocinio sponsor) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText(
                "Nome: " + sponsor.nome + "\n" +
                        "Descrição: " + sponsor.descricao + "\n" +
                        "Valor: " + app.money(sponsor.valor) + "\n"
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Voltar");
        JButton edit = new JButton("Editar");
        JButton delete = new JButton("Remover Patrocinador");

        buttons.add(back);
        buttons.add(edit);
        buttons.add(delete);

        back.addActionListener(e -> showSponsorsPage(app, store));
        edit.addActionListener(e -> showSponsorForm(app, store, sponsor));
        delete.addActionListener(e -> deleteSponsor(app, store, sponsor));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        app.setPage("Patrocínios do Torneio", p);
    }

    private static void showSponsorForm(TorneioApp app, Store store, Patrocinio editing) {
        boolean isEdit = editing != null;

        if (isTournamentStarted(store)) {
            app.error(isEdit
                    ? "Erro: Não é possível editar um patrocínio de um torneio já iniciado."
                    : "Erro: Os patrocínios devem ser registados antes do início do jogo.");
            return;
        }

        JTextField name = new JTextField(isEdit ? editing.nome : "");
        JTextField description = new JTextField(isEdit ? editing.descricao : "");

        NumberFormat euroFormat = NumberFormat.getNumberInstance();
        euroFormat.setMinimumFractionDigits(2);
        euroFormat.setMaximumFractionDigits(2);

        JFormattedTextField value = new JFormattedTextField(euroFormat);
        value.setValue(isEdit ? editing.valor : 0.00);
        value.setHorizontalAlignment(JTextField.RIGHT);
        value.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        JPanel form = formPanel();
        addRow(form, "Nome *", name, 0);
        addRow(form, "Descrição *", description, 1);

        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.add(value, BorderLayout.CENTER);
        valuePanel.add(new JLabel(" €"), BorderLayout.EAST);

        addRow(form, "Valor *", valuePanel, 2);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancelar");
        JButton save = new JButton("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showSponsorsPage(app, store));

        save.addActionListener(e -> {
            if (blank(name, description, value)) {
                app.error("Erro: Campos obrigatórios em falta.");
                return;
            }

            try {
                value.commitEdit();
            } catch (java.text.ParseException ex) {
                app.error("O valor do patrocínio tem de ser numérico.");
                return;
            }

            double valueVal = ((Number) value.getValue()).doubleValue();

            if (valueVal <= 0) {
                app.error("O valor do patrocínio deve ser positivo.");
                return;
            }

            if (isEdit) {
                editing.nome = name.getText().trim();
                editing.descricao = description.getText().trim();
                editing.valor = valueVal;
                app.info("Dados salvos com sucesso.");
            } else {
                store.patrocinios.add(new Patrocinio(
                        store.nextId(),
                        name.getText().trim(),
                        description.getText().trim(),
                        valueVal
                ));
                app.info("Patrocínio criado com sucesso.");
            }

            showSponsorsPage(app, store);
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        app.setPage(isEdit ? "Editar Patrocínios" : "Criar Patrocínios", p);
    }

    private static void deleteSponsor(TorneioApp app, Store store, Patrocinio sponsor) {
        if (isTournamentStarted(store)) {
            app.error("Erro: Não é possível eliminar um patrocínio associado a um torneio já iniciado.");
            return;
        }

        int opt = JOptionPane.showConfirmDialog(
                app,
                "Eliminar o patrocínio " + sponsor.nome + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (opt == JOptionPane.YES_OPTION) {
            store.patrocinios.remove(sponsor);
            app.info("Patrocínio eliminado com sucesso.");
            showSponsorsPage(app, store);
        }
    }

    private static Patrocinio selectedSponsor(TorneioApp app, JTable table, Store store) {
        int row = table.getSelectedRow();

        if (row < 0) {
            app.error("Seleciona primeiro um registo da lista.");
            return null;
        }

        int id = (Integer) table.getValueAt(row, 0);
        return store.findSponsor(id);
    }

    private static boolean isTournamentStarted(Store store) {
        return store.games.stream().anyMatch(g ->
                g.state == TorneioApp.GameState.EM_CURSO ||
                        g.state == TorneioApp.GameState.CONCLUIDO
        );
    }

    private static JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return form;
    }

    private static void addRow(JPanel form, String label, Component comp, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;

        if (comp instanceof JComponent) {
            ((JComponent) comp).setPreferredSize(new Dimension(320, 28));
        }

        form.add(comp, c);
    }

    private static boolean blank(JTextField... fields) {
        for (JTextField f : fields) {
            if (f.getText().trim().isEmpty()) return true;
        }
        return false;
    }

    private static JLabel empty(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 18));
        return l;
    }
}