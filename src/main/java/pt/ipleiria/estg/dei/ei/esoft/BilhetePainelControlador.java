import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;

public class BilhetePainelControlador {

    public static void showTicketsPage(TorneioApp app, Store store) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton create = new JButton("Criar Bilhetes");
        JButton buy = new JButton("Comprar Bilhete");
        JButton edit = new JButton("Editar Bilhete");
        JButton delete = new JButton("Eliminar Bilhete");

        top.add(create);
        top.add(buy);
        top.add(edit);
        top.add(delete);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Jogo", "Data", "Estádio", "Bancada", "Preço", "Disponíveis", "Vendidos", "Fase"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (TorneioApp.TicketBatch t : store.tickets) {
            model.addRow(new Object[]{
                    t.id,
                    t.game.teamA + " vs " + t.game.teamB,
                    t.game.dateTime,
                    t.game.stadium.nome,
                    t.stand.nome,
                    app.money(t.price),
                    t.available,
                    t.sold,
                    t.game.phase
            });
        }

        JTable table = new JTable(model);

        create.addActionListener(e -> showTicketForm(app, store, null));

        buy.addActionListener(e -> {
            TorneioApp.TicketBatch tb = selectedTicket(app, table, store);
            if (tb != null) showTicketDetails(app, store, tb);
        });

        edit.addActionListener(e -> {
            TorneioApp.TicketBatch tb = selectedTicket(app, table, store);
            if (tb != null) showEditTicketPriceForm(app, store, tb);
        });

        delete.addActionListener(e -> {
            TorneioApp.TicketBatch tb = selectedTicket(app, table, store);
            if (tb != null) showTicketDetails(app, store, tb);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TorneioApp.TicketBatch tb = selectedTicket(app, table, store);
                    if (tb != null) showTicketDetails(app, store, tb);
                }
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(store.tickets.isEmpty()
                ? empty("Não existem bilhetes criados.")
                : new JScrollPane(table), BorderLayout.CENTER);

        app.setPage("Lista de Bilhetes", p);
    }

    private static void showTicketDetails(TorneioApp app, Store store, TorneioApp.TicketBatch tb) {
        JPanel p = new JPanel(new BorderLayout(12, 12));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        infoArea.setText(
                "Jogo: " + tb.game.teamA + " vs " + tb.game.teamB + "\n" +
                        "Data/Hora: " + tb.game.dateTime + "\n" +
                        "Estádio: " + tb.game.stadium.nome + "\n" +
                        "Bancada: " + tb.stand.nome + "\n" +
                        "Preço: " + app.money(tb.price) + "\n" +
                        "Bilhetes disponíveis: " + tb.available + "\n" +
                        "Bilhetes vendidos: " + tb.sold + "\n" +
                        "Estado do jogo: " + tb.game.state + "\n"
        );

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Voltar");
        JButton buy = new JButton("Comprar Bilhete");
        JButton edit = new JButton("Editar Bilhete");
        JButton delete = new JButton("Eliminar Bilhete");

        buttons.add(back);
        buttons.add(buy);
        buttons.add(edit);
        buttons.add(delete);

        back.addActionListener(e -> showTicketsPage(app, store));
        buy.addActionListener(e -> buyTicket(app, store, tb));
        edit.addActionListener(e -> showEditTicketPriceForm(app, store, tb));
        delete.addActionListener(e -> deleteTicket(app, store, tb));

        p.add(buttons, BorderLayout.NORTH);
        p.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        app.setPage("Bilhete", p);
    }

    private static void showTicketForm(TorneioApp app, Store store, TorneioApp.TicketBatch editing) {
        boolean isEdit = editing != null;

        JComboBox<TorneioApp.Game> gameBox = new JComboBox<>();
        for (TorneioApp.Game g : store.games) {
            gameBox.addItem(g);
        }

        JComboBox<Bancada> standBox = new JComboBox<>();

        NumberFormat euroFormat = NumberFormat.getNumberInstance();
        euroFormat.setMinimumFractionDigits(2);
        euroFormat.setMaximumFractionDigits(2);

        JFormattedTextField price = new JFormattedTextField(euroFormat);
        price.setValue(isEdit ? editing.price : 0.00);
        price.setHorizontalAlignment(JTextField.RIGHT);
        price.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        JTextField qty = new JTextField(isEdit ? String.valueOf(editing.available + editing.sold) : "");
        qty.setEnabled(!isEdit);

        if (isEdit) {
            gameBox.addItem(editing.game);
            gameBox.setSelectedItem(editing.game);
            gameBox.setEnabled(false);

            standBox.addItem(editing.stand);
            standBox.setSelectedItem(editing.stand);
            standBox.setEnabled(false);
        } else {
            refreshStandsCombo(gameBox, standBox);
            gameBox.addActionListener(e -> refreshStandsCombo(gameBox, standBox));
        }

        JPanel form = formPanel();
        addRow(form, "Jogo *", gameBox, 0);
        addRow(form, "Bancada *", standBox, 1);

        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.add(price, BorderLayout.CENTER);
        pricePanel.add(new JLabel(" €"), BorderLayout.EAST);

        addRow(form, "Preço *", pricePanel, 2);
        addRow(form, "Quantidade *", qty, 3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancelar");
        JButton save = new JButton("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showTicketsPage(app, store));

        save.addActionListener(e -> {
            if (blank(price) || (!isEdit && blank(qty))) {
                app.error("Erro: campos obrigatórios em falta.");
                return;
            }

            try {
                price.commitEdit();
            } catch (java.text.ParseException ex) {
                app.error("O preço tem de ser numérico.");
                return;
            }

            double priceVal = ((Number) price.getValue()).doubleValue();

            if (priceVal < 0) {
                app.error("O preço não pode ser negativo.");
                return;
            }

            if (isEdit) {
                if (editing.game.state == TorneioApp.GameState.EM_CURSO ||
                        editing.game.state == TorneioApp.GameState.CONCLUIDO) {
                    app.error("O preço do bilhete só pode ser editado até ao início do jogo.");
                    return;
                }

                editing.price = priceVal;
                app.info("Preço editado com sucesso.");
            } else {
                TorneioApp.Game game = (TorneioApp.Game) gameBox.getSelectedItem();
                Bancada stand = (Bancada) standBox.getSelectedItem();

                if (game == null || stand == null) {
                    app.error("Seleciona um jogo e uma bancada.");
                    return;
                }

                Integer q;

                try {
                    q = Integer.parseInt(qty.getText().trim());
                } catch (NumberFormatException ex) {
                    app.error("A quantidade tem de ser um número inteiro.");
                    return;
                }

                if (q <= 0) {
                    app.error("A quantidade deve ser positiva.");
                    return;
                }

                if (q > stand.capacidade) {
                    app.error("A quantidade de bilhetes não pode ultrapassar a lotação máxima da bancada.");
                    return;
                }

                store.tickets.add(new TorneioApp.TicketBatch(
                        store.nextId(),
                        game,
                        stand,
                        priceVal,
                        q
                ));

                app.info("Bilhete criado com sucesso.");
            }

            showTicketsPage(app, store);
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        app.setPage(isEdit ? "Editar Preço do Bilhete" : "Definir Bilhetes", p);
    }

    private static void refreshStandsCombo(JComboBox<TorneioApp.Game> gameBox, JComboBox<Bancada> standBox) {
        standBox.removeAllItems();

        TorneioApp.Game g = (TorneioApp.Game) gameBox.getSelectedItem();

        if (g != null && g.stadium != null) {
            for (Bancada st : g.stadium.bancadas) {
                standBox.addItem(st);
            }
        }
    }

    private static void showEditTicketPriceForm(TorneioApp app, Store store, TorneioApp.TicketBatch ticket) {
        if (ticket.game.state == TorneioApp.GameState.EM_CURSO ||
                ticket.game.state == TorneioApp.GameState.CONCLUIDO) {
            app.error("O preço do bilhete só pode ser editado até ao início do jogo.");
            return;
        }

        NumberFormat euroFormat = NumberFormat.getNumberInstance();
        euroFormat.setMinimumFractionDigits(2);
        euroFormat.setMaximumFractionDigits(2);

        JFormattedTextField price = new JFormattedTextField(euroFormat);
        price.setValue(ticket.price);
        price.setHorizontalAlignment(JTextField.RIGHT);
        price.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        JPanel form = formPanel();

        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.add(price, BorderLayout.CENTER);
        pricePanel.add(new JLabel(" €"), BorderLayout.EAST);

        addRow(form, "Preço *", pricePanel, 0);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancelar");
        JButton save = new JButton("Confirmar");

        buttons.add(cancel);
        buttons.add(save);

        cancel.addActionListener(e -> showTicketDetails(app, store, ticket));

        save.addActionListener(e -> {
            try {
                price.commitEdit();
            } catch (java.text.ParseException ex) {
                app.error("O preço tem de ser numérico.");
                return;
            }

            double priceVal = ((Number) price.getValue()).doubleValue();

            if (priceVal < 0) {
                app.error("O preço não pode ser negativo.");
                return;
            }

            ticket.price = priceVal;
            app.info("Preço editado com sucesso.");
            showTicketsPage(app, store);
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(buttons, BorderLayout.SOUTH);

        app.setPage("Editar Preço do Bilhete", p);
    }

    private static void buyTicket(TorneioApp app, Store store, TorneioApp.TicketBatch tb) {
        if (tb.available <= 0) {
            app.error("Erro: bilhetes esgotados.");
            return;
        }

        tb.available--;
        tb.sold++;

        store.soldTickets.add(new TorneioApp.SoldTicket(
                "BIL-" + store.nextId(),
                tb,
                tb.price
        ));

        app.info("Compra de bilhete realizada com sucesso.");
        showTicketsPage(app, store);
    }

    private static void deleteTicket(TorneioApp app, Store store, TorneioApp.TicketBatch tb) {
        if (tb.game.state == TorneioApp.GameState.EM_CURSO ||
                tb.game.state == TorneioApp.GameState.CONCLUIDO) {
            app.error("Não é permitido eliminar bilhetes após o início do jogo.");
            return;
        }

        if (tb.sold > 0) {
            app.error("Não é possível eliminar um bilhete com vendas associadas.");
            return;
        }

        store.tickets.remove(tb);
        app.info("Bilhete eliminado com sucesso.");
        showTicketsPage(app, store);
    }

    private static TorneioApp.TicketBatch selectedTicket(TorneioApp app, JTable table, Store store) {
        int row = table.getSelectedRow();

        if (row < 0) {
            app.error("Seleciona primeiro um registo da lista.");
            return null;
        }

        int id = (Integer) table.getValueAt(row, 0);
        return store.findTicket(id);
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