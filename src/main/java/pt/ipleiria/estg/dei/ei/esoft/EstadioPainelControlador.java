import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EstadioPainelControlador {

    public static void showStadiumsPage(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JButton create = btn("Criar Estádio");
        JButton view = btn("Ver Estádio");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(create);
        top.add(view);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nome", "Localização", "Lotação", "Bancadas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Estadio e : store.stadiums) {
            model.addRow(new Object[]{e.id, e.nome, e.localizacao, e.capacidade, e.bancadas.size()});
        }

        JTable table = new JTable(model);

        // BLOQUEIO: Impedir a criação de estádios se o torneio já começou
        create.addActionListener(e -> {
            if (isTournamentStarted(store)) {
                app.error("Não podes criar um estádio depois do torneio ter começado!");
            } else {
                showStadiumForm(app, store, null);
            }
        });

        view.addActionListener(ev -> {
            Estadio est = selectedStadium(app, table, store);
            if (est != null) showStadiumDetails(app, store, est);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Estadio est = selectedStadium(app, table, store);
                    if (est != null) showStadiumDetails(app, store, est);
                }
            }
        });

        if (store.stadiums.isEmpty()) {
            p.add(emptyLabel("Não existe nenhum estádio criado."), BorderLayout.CENTER);
        } else {
            p.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        p.add(top, BorderLayout.NORTH);
        app.setPage("Lista de Estádios", p);
    }

    private static void showStadiumDetails(TorneioApp app, Store store, Estadio estadio) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText("Nome: " + estadio.nome + "\nLocalização: " + estadio.localizacao + "\nLotação total: " + estadio.capacidade + "\nLotação das bancadas: " + estadio.totalCapacidadeBancadas());

        JComboBox<Bancada> standCombo = new JComboBox<>();

        if (estadio.bancadas.isEmpty()) {
            standCombo.addItem(new Bancada(-1, "Não existe nenhuma bancada para este estádio!", 0));
        } else {
            for (Bancada b : estadio.bancadas) standCombo.addItem(b);
        }

        JButton viewStand = btn("Ver Bancada");
        JButton addStand = btn("Adicionar Bancada");
        JButton edit = btn("Editar Estádio");
        JButton delete = btn("Eliminar Estádio");
        JButton back = btn("Voltar");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(new JLabel("Bancadas:"));
        buttons.add(standCombo);
        buttons.add(viewStand);
        buttons.add(addStand);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(back);

        back.addActionListener(e -> showStadiumsPage(app, store));

        viewStand.addActionListener(e -> {
            Bancada selected = (Bancada) standCombo.getSelectedItem();
            if (selected == null || selected.id == -1) {
                app.error("Não existe nenhuma bancada para este estádio!");
            } else {
                showStandDetails(app, store, estadio, selected);
            }
        });

        // BLOQUEIO: Impedir a adição de bancadas
        addStand.addActionListener(e -> {
            if (isTournamentStarted(store)) {
                app.error("Não podes adicionar bancadas depois do torneio ter começado!");
            } else {
                showStandForm(app, store, estadio, null);
            }
        });

        // BLOQUEIO: Impedir a edição do estádio
        edit.addActionListener(e -> {
            if (isTournamentStarted(store)) {
                app.error("Não podes editar o estádio depois do torneio ter começado!");
            } else {
                showStadiumForm(app, store, estadio);
            }
        });

        delete.addActionListener(e -> deleteStadium(app, store, estadio));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        app.setPage("Estádio", p);
    }

    private static void showStadiumForm(TorneioApp app, Store store, Estadio editing) {
        boolean isEdit = editing != null;
        JTextField name = new JTextField(isEdit ? editing.nome : "");
        JTextField location = new JTextField(isEdit ? editing.localizacao : "");
        JTextField capacity = new JTextField(isEdit ? String.valueOf(editing.capacidade) : "");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addRowToForm(form, "Nome do Estádio *", name, 0);
        addRowToForm(form, "Localização *", location, 1);
        addRowToForm(form, "Lotação Total *", capacity, 2);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showStadiumsPage(app, store));

        save.addActionListener(e -> {
            if (name.getText().trim().isEmpty() || location.getText().trim().isEmpty() || capacity.getText().trim().isEmpty()) {
                app.error("É necessário preencher todos os campos obrigatórios (*)");
                return;
            }

            Integer cap;
            try {
                cap = Integer.parseInt(capacity.getText().trim());
            } catch (NumberFormatException ex) {
                app.error("O campo Lotação Total tem de ser um número inteiro!");
                return;
            }

            if (cap <= 0) {
                app.error("A lotação total deve ser positiva.");
                return;
            }

            if (isEdit && editing.totalCapacidadeBancadas() > cap) {
                app.error("A lotação total não pode ser inferior à soma das bancadas existentes.");
                return;
            }

            if (isEdit) {
                editing.nome = name.getText().trim();
                editing.localizacao = location.getText().trim();
                editing.capacidade = cap;
                app.info("Estádio editado com sucesso!");
            } else {
                store.stadiums.add(new Estadio(store.nextId(), name.getText().trim(), location.getText().trim(), cap));
                app.info("Estádio criado com sucesso!");
            }
            showStadiumsPage(app, store);
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        app.setPage(isEdit ? "Editar Estádio" : "Criar Estádio", p);
    }

    private static void deleteStadium(TorneioApp app, Store store, Estadio estadio) {
        // BLOQUEIO: Impedir a eliminação do estádio
        if (isTournamentStarted(store)) {
            app.error("Não podes eliminar um estádio depois do torneio ter começado!");
            return;
        }

        int opt = JOptionPane.showConfirmDialog(app, "Eliminar estádio " + estadio.nome + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            store.stadiums.remove(estadio);
            app.info("Estádio eliminado com sucesso!");
            showStadiumsPage(app, store);
        }
    }

    private static void showStandDetails(TorneioApp app, Store store, Estadio estadio, Bancada bancada) {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        JTextArea infoArea = new JTextArea("Nome: " + bancada.nome + "\nLotação: " + bancada.capacidade);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = btn("Voltar");
        JButton edit = btn("Editar");
        JButton delete = btn("Eliminar");
        buttons.add(back);
        buttons.add(edit);
        buttons.add(delete);

        back.addActionListener(e -> showStadiumDetails(app, store, estadio));

        // BLOQUEIO: Impedir a edição da bancada
        edit.addActionListener(e -> {
            if (isTournamentStarted(store)) {
                app.error("Não podes editar uma bancada depois do torneio ter começado!");
            } else {
                showStandForm(app, store, estadio, bancada);
            }
        });

        // BLOQUEIO: Impedir a eliminação da bancada
        delete.addActionListener(e -> {
            if (isTournamentStarted(store)) {
                app.error("Não podes eliminar uma bancada depois do torneio ter começado!");
                return;
            }
            estadio.bancadas.remove(bancada);
            app.info("Bancada eliminada com sucesso!");
            showStadiumDetails(app, store, estadio);
        });

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        app.setPage("Bancada", p);
    }

    private static void showStandForm(TorneioApp app, Store store, Estadio estadio, Bancada editing) {
        boolean isEdit = editing != null;
        JTextField name = new JTextField(isEdit ? editing.nome : "");
        JTextField capacity = new JTextField(isEdit ? String.valueOf(editing.capacidade) : "");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addRowToForm(form, "Nome *", name, 0);
        addRowToForm(form, "Lotação *", capacity, 1);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = btn("Cancelar");
        JButton save = btn("Confirmar");
        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showStadiumDetails(app, store, estadio));

        save.addActionListener(e -> {
            if (name.getText().trim().isEmpty() || capacity.getText().trim().isEmpty()) {
                app.error("É necessário preencher todos os campos obrigatórios (*)");
                return;
            }

            Integer cap;
            try {
                cap = Integer.parseInt(capacity.getText().trim());
            } catch (NumberFormatException ex) {
                app.error("A lotação da bancada tem de ser um número inteiro.");
                return;
            }

            int oldCapacity = isEdit ? editing.capacidade : 0;
            if (estadio.totalCapacidadeBancadas() - oldCapacity + cap > estadio.capacidade) {
                app.error("A soma das bancadas não pode ultrapassar a lotação total do estádio.");
                return;
            }

            if (isEdit) {
                editing.nome = name.getText().trim();
                editing.capacidade = cap;
                app.info("Bancada editada com sucesso!");
            } else {
                estadio.bancadas.add(new Bancada(store.nextId(), name.getText().trim(), cap));
                app.info("Bancada adicionada com sucesso!");
            }

            showStadiumsPage(app, store);
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);
        app.setPage(isEdit ? "Editar Bancada" : "Adicionar Bancada", p);
    }

    private static boolean isTournamentStarted(Store store) {
        return store.games.stream().anyMatch(g ->
                g.state == TorneioApp.GameState.EM_CURSO || g.state == TorneioApp.GameState.CONCLUIDO
        );
    }

    private static JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        return b;
    }

    private static JLabel emptyLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 18));
        return l;
    }

    private static void addRowToForm(JPanel form, String label, JComponent comp, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        comp.setPreferredSize(new Dimension(320, 28));
        form.add(comp, c);
    }

    private static Estadio selectedStadium(TorneioApp app, JTable table, Store store) {
        int row = table.getSelectedRow();
        if (row < 0) {
            app.error("Seleciona primeiro um registo da lista.");
            return null;
        }
        int id = (Integer) table.getValueAt(row, 0);
        return store.findStadium(id);
    }
}