import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class Retrait extends JFrame {

    private JPanel contentPane;
    private JComboBox<String> comboBoxEmprunts;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Retour frame = new Retour();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Retrait() {
        setTitle("Retour d'un Livre");
        setBounds(325, 200, 900, 400);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Label et ComboBox pour sélectionner un emprunt
        JLabel lblEmprunt = new JLabel("Liste des emprunts:");
        lblEmprunt.setBounds(30, 20, 170, 20);
        contentPane.add(lblEmprunt);

        comboBoxEmprunts = new JComboBox<>();
        comboBoxEmprunts.setBounds(160, 20, 500, 20);
        fillComboBoxEmprunts();
        contentPane.add(comboBoxEmprunts);

        // Bouton Valider
        JButton btnValider = new JButton("Valider");
        btnValider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                supprimerEmprunt();
            }
        });
        btnValider.setBounds(30, 70, 100, 25);
        contentPane.add(btnValider);
    }

    private void fillComboBoxEmprunts() {
        comboBoxEmprunts.removeAllItems();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String selectQuery = "SELECT id_emprunt, titre, nom, prenom FROM emprunt "
                                + "INNER JOIN livre ON emprunt.isbn = livre.isbn "
                                + "INNER JOIN adherent ON emprunt.adhnum = adherent.adhnum";
            try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    int idEmprunt = resultSet.getInt("id_emprunt");
                    String titre = resultSet.getString("titre");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    comboBoxEmprunts.addItem(idEmprunt + " : " + titre + " par " + nom + " " + prenom);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Erreur lors du chargement des emprunts: " + ex.getMessage());
        }
    }

    private void supprimerEmprunt() {
        String selectedEmprunt = (String) comboBoxEmprunts.getSelectedItem();
        int idEmprunt = Integer.parseInt(selectedEmprunt.split(":")[0].trim());

        // Supprimer l'emprunt de la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String deleteQuery = "DELETE FROM emprunt WHERE id_emprunt = ?";
            try (PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, idEmprunt);

                int rowsDeleted = deleteStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null,"Le livre à été rendu avec succès !");
                    fillComboBoxEmprunts(); // Actualiser la liste des emprunts après la suppression
                } else {
                    JOptionPane.showMessageDialog(null,"Erreur, aucun rendu n'a été validé.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Erreur de connexion à la base de données: " + ex.getMessage());
        }
    }
}