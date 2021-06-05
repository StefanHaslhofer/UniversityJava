package com.company.gui;

import com.company.model.InventoryChangeListener;
import com.company.model.VaccinationStationModel;
import com.company.model.Vaccine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

public final class VaccinationStationGUI<VaccineClass extends Vaccine> {

    private static final class DisplayVaccine<VaccineClass extends Vaccine> {

        private final VaccineClass vaccine;

        private DisplayVaccine(VaccineClass vaccine) {
            assert vaccine != null;
            this.vaccine = vaccine;
        }

        public VaccineClass getVaccine() {
            return vaccine;
        }

        /**
         * This method is invoked to generate the string displayed in the list view.
         *
         * @return the description of the vaccine
         */
        @Override
        public String toString() {
            return vaccine.getName();
        }

        @Override
        public int hashCode() {
            return vaccine.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DisplayVaccine && vaccine.equals(((DisplayVaccine) obj).getVaccine());
        }
    }

    private final JFrame frame;
    private final JList<DisplayVaccine<VaccineClass>> stockListView;
    private final JTextArea vaccineName;
    private final JTextArea vaccineDescription;
    private final JTextArea vaccineQuantity;
    private final JButton addStockButton;
    private final JButton removeStockButton;
    private final JButton editDescriptionButton;

    private VaccinationStationGUI(VaccinationStationModel<VaccineClass> model) {
        frame = new JFrame();
        frame.setTitle("VaxMaster 2000");
        frame.setSize(800, 600);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocation(50, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel mainPanel = new JPanel();
        frame.getContentPane().add(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        final DefaultListModel<DisplayVaccine<VaccineClass>> stockListModel = new DefaultListModel<>();
        stockListView = new JList<>(stockListModel);
        splitPane.setLeftComponent(stockListView);

        final JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());
        splitPane.setRightComponent(detailPanel);

        final JPanel detailNamePanel = new JPanel();
        detailNamePanel.setLayout(new BoxLayout(detailNamePanel, BoxLayout.Y_AXIS));
        detailNamePanel.add(createDetailLabel("Vaccine Name:"));
        detailNamePanel.add(wrapTextArea(vaccineName = createTextField()));
        detailPanel.add(detailNamePanel, BorderLayout.NORTH);

        final JPanel detailDescriptionPanel = new JPanel();
        detailDescriptionPanel.setLayout(new BoxLayout(detailDescriptionPanel, BoxLayout.Y_AXIS));
        detailDescriptionPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        detailDescriptionPanel.add(createDetailLabel("Vaccine Description:"));
        detailDescriptionPanel.add(wrapTextArea(vaccineDescription = createTextField()));
        detailDescriptionPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        detailDescriptionPanel.add(createDetailLabel("Vaccine Quantity:"));
        detailDescriptionPanel.add(wrapTextArea(vaccineQuantity = createTextField()));
        detailPanel.add(detailDescriptionPanel, BorderLayout.CENTER);

        final JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        detailPanel.add(editPanel, BorderLayout.SOUTH);

        final JPanel changeVaccinePanel = new JPanel();
        final SpinnerModel quantitySpinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        changeVaccinePanel.add(new JSpinner(quantitySpinnerModel));
        changeVaccinePanel.setLayout(new BoxLayout(changeVaccinePanel, BoxLayout.X_AXIS));
        changeVaccinePanel.add(addStockButton = createQuantityButton("Increase Qty."));
        changeVaccinePanel.add(removeStockButton = createQuantityButton("Decrease Qty."));
        editPanel.add(changeVaccinePanel);
        changeVaccinePanel.add(editDescriptionButton = new JButton("Edit Desc."));

        final JPanel editListPanel = new JPanel();
        editListPanel.setLayout(new BoxLayout(editListPanel, BoxLayout.X_AXIS));
        JButton createVaccineButton;
        editListPanel.add(createVaccineButton = new JButton("Create Vaccine"));
        JButton deleteVaccineButton;
        editListPanel.add(deleteVaccineButton = new JButton("Delete Vaccine"));
        editPanel.add(editListPanel);

        stockListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockListView.addListSelectionListener(listSelectionEvent -> updateDetailsView());

        addStockButton.addActionListener(e -> {
            final DisplayVaccine<VaccineClass> selectedVaccine = stockListView.getSelectedValue();
            if (selectedVaccine != null) {
                try {
                    model.increaseQuantity(selectedVaccine.getVaccine(), (int) quantitySpinnerModel.getValue());
                } catch (IllegalArgumentException error) {
                    JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while changing quantity", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        removeStockButton.addActionListener(e -> {
            final DisplayVaccine<VaccineClass> selectedVaccine = stockListView.getSelectedValue();
            if (selectedVaccine != null) {
                try {
                    model.decreaseQuantity(selectedVaccine.getVaccine(), (int) quantitySpinnerModel.getValue());
                } catch (IllegalArgumentException error) {
                    JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while changing quantity", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        editDescriptionButton.addActionListener(e -> {
            final DisplayVaccine<VaccineClass> selectedVaccine = stockListView.getSelectedValue();
            if (selectedVaccine != null) {
                SwingUtilities.invokeLater(() -> showEditDescriptionDialog(model, selectedVaccine.getVaccine()));
            }
        });

        createVaccineButton.addActionListener(e -> {
            final String newName = JOptionPane.showInputDialog(frame, "Please enter a unique name for the new vaccine:", "Create Vaccine", JOptionPane.PLAIN_MESSAGE);
            if (newName != null) {
                try {
                    model.createVaccine(newName);
                } catch (IllegalArgumentException | RemoteException error) {
                    JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while creating a new vaccine", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteVaccineButton.addActionListener(e -> {
            final DisplayVaccine<VaccineClass> selectedVaccine = stockListView.getSelectedValue();
            try {
                model.removeVaccine(selectedVaccine.getVaccine());
            } catch (IllegalArgumentException error) {
                JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while deleting vaccine", JOptionPane.ERROR_MESSAGE);
            }
        });

        // connect inventory model and display model
        final InventoryChangeListener<VaccineClass> inventoryChangeListener = new InventoryChangeListener<VaccineClass>() {
            @Override
            public void onVaccineAdded(VaccineClass addedVaccine) {
                stockListModel.addElement(new DisplayVaccine<>(addedVaccine));
            }

            @Override
            public void onVaccineChanged(VaccineClass changedVaccine) {
                updateDetailsView();
            }

            @Override
            public void onVaccineRemoved(VaccineClass removedVaccine) {
                stockListModel.removeElement(new DisplayVaccine<>(removedVaccine));
            }
        };

        try {
            model.addListener(inventoryChangeListener);
            model.getVaccines().forEach(element -> stockListModel.addElement(new DisplayVaccine<>(element)));
        } catch (IllegalArgumentException error) {
            throw new AssertionError("Error during initialization", error);
        }
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    model.removeListener(inventoryChangeListener);
                } catch (IllegalArgumentException error) {
                    // the application will now terminate anyways
                    throw new AssertionError("Error during closing", error);
                }
            }
        });

        // initialize the details view
        updateDetailsView();
    }

    public static <VaccineClass extends Vaccine> void startGui(VaccinationStationModel<VaccineClass> model) {
        new VaccinationStationGUI<>(model).show();
    }

    private static JButton createQuantityButton(String label) {
        return new JButton(label);
    }

    private static JLabel createDetailLabel(String labelText) {
        return new JLabel(labelText, JLabel.CENTER);
    }

    private static JScrollPane wrapTextArea(JTextArea textArea) {
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private static JTextArea createTextField() {
        final JTextArea textPane = new JTextArea();
        textPane.setWrapStyleWord(true);
        textPane.setEditable(true);
        textPane.setEditable(false);
        return textPane;
    }

    private void updateDetailsView() {
        SwingUtilities.invokeLater(() -> {
            final DisplayVaccine<VaccineClass> selectedVaccine = stockListView.getSelectedValue();
            if (selectedVaccine != null) {
                setDetails(selectedVaccine.getVaccine());
            } else {
                clearDetails();
            }

            addStockButton.setEnabled(selectedVaccine != null);
            removeStockButton.setEnabled(selectedVaccine != null);
            editDescriptionButton.setEnabled(selectedVaccine != null);
        });
    }

    private void setDetails(Vaccine vaccine) {
        try {
            vaccineName.setText(vaccine.getName());
            vaccineDescription.setText(vaccine.getDescription());
            vaccineQuantity.setText(String.valueOf(vaccine.getQuantity()));
        } catch (IllegalArgumentException error) {
            clearDetails();
            JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while retrieving vaccine details", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDetails() {
        vaccineName.setText("");
        vaccineDescription.setText("");
        vaccineQuantity.setText("");
    }

    private void showEditDescriptionDialog(VaccinationStationModel<VaccineClass> model, VaccineClass editedVaccine) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Edit Description");
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setResizable(true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(mainPanel);

        final JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));
        final JTextArea descriptionPane = new JTextArea(50, 50);
        descriptionPane.setWrapStyleWord(true);
        descriptionPane.setEnabled(true);
        descriptionPane.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(descriptionPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        descriptionPanel.add(scrollPane);
        mainPanel.add(descriptionPanel, BorderLayout.CENTER);

        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        final JButton saveButton = new JButton("Save");
        final JButton reloadButton = new JButton("Reload");
        buttonsPanel.add(saveButton);
        buttonsPanel.add(reloadButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> editDescription(model, editedVaccine, descriptionPane.getText()));
        reloadButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            try {
                descriptionPane.setText(editedVaccine.getDescription());
            } catch (IllegalArgumentException error) {
                descriptionPane.setText("");
                JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while loading description", JOptionPane.ERROR_MESSAGE);
            }
        }));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                editDescription(model, editedVaccine, descriptionPane.getText());
            }
        });

        descriptionPane.setText(editedVaccine.getDescription());

        frame.setVisible(true);
        descriptionPane.requestFocus();
    }

    private void editDescription(VaccinationStationModel<VaccineClass> model, VaccineClass editedVaccine, String newDescription) {
        SwingUtilities.invokeLater(() -> {
            try {
                model.setDescription(editedVaccine, newDescription);
            } catch (IllegalArgumentException error) {
                JOptionPane.showMessageDialog(frame, error.getMessage(), "Error while editing description", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    void show() {
        frame.setVisible(true);
    }
}
