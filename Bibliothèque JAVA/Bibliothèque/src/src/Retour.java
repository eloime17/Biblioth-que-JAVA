import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Retour extends JFrame {

    private JPanel contentPane;
    private JComboBox<String> comboBoxLivres;
    private JComboBox<String> comboBoxAdherents;
    private JTextField textFieldDateRetour;

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

    public Retour() {
        setTitle("Retrait d'un Livre");
        setBounds(325, 200, 900, 400);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Label et ComboBox pour sélectionner un livre
        JLabel lblLivre = new JLabel("Livre:");
        lblLivre.setBounds(30, 20, 70, 20);
        contentPane.add(lblLivre);

        comboBoxLivres = new JComboBox<>();
        comboBoxLivres.setBounds(110, 20, 300, 20);
        fillComboBoxLivres();
        contentPane.add(comboBoxLivres);

        // Label et ComboBox pour sélectionner un adhérent
        JLabel lblAdherent = new JLabel("Adhérent:");
        lblAdherent.setBounds(30, 60, 70, 20);
        contentPane.add(lblAdherent);

        comboBoxAdherents = new JComboBox<>();
        comboBoxAdherents.setBounds(110, 60, 300, 20);
        fillComboBoxAdherents();
        contentPane.add(comboBoxAdherents);

        // Label et champ de texte pour la date de retour
        JLabel lblDateRetour = new JLabel("Date de retour (jj-mm-aaaa):");
        lblDateRetour.setBounds(30, 100, 200, 20);
        contentPane.add(lblDateRetour);

        textFieldDateRetour = new JTextField();
        textFieldDateRetour.setBounds(230, 100, 150, 20);
        contentPane.add(textFieldDateRetour);
        textFieldDateRetour.setColumns(10);

        // Bouton Valider
        JButton btnValider = new JButton("Valider");
        btnValider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validerEmprunt();
            }
        });
        btnValider.setBounds(30, 150, 100, 25);
        contentPane.add(btnValider);
    }

    private void fillComboBoxLivres() {
        comboBoxLivres.removeAllItems();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String selectQuery = "SELECT livre.isbn, livre.titre, auteur.nom, auteur.prenom, livre.prix, " +
                "CASE WHEN emprunt.isbn IS NULL THEN 'disponible' ELSE 'indisponible' END AS disponibilite " +
                "FROM livre " +
                "INNER JOIN auteur ON livre.id_auteur = auteur.autnum " +
                "LEFT JOIN emprunt ON livre.isbn = emprunt.isbn";
            try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    int isbn = resultSet.getInt("isbn");
                    String titre = resultSet.getString("titre");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    double prix = resultSet.getDouble("prix");
                    String disponibilite = resultSet.getString("disponibilite");
                    comboBoxLivres.addItem(isbn + " : " + titre + " (" + disponibilite + ") , " + prenom + " " + nom + " , " + prix + "€");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Erreur lors du chargement des livres: " + ex.getMessage());
        }
    }

    private void fillComboBoxAdherents() {
        comboBoxAdherents.removeAllItems();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String selectQuery = "SELECT adhnum, nom, prenom FROM adherent";
            try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    int adhnum = resultSet.getInt("adhnum");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    comboBoxAdherents.addItem(adhnum + " : " + nom + " " + prenom);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Erreur lors du chargement des adhérents: " + ex.getMessage());
        }
    }

    private void validerEmprunt() {
        String selectedLivre = (String) comboBoxLivres.getSelectedItem();
        String disponibilite = selectedLivre.substring(selectedLivre.indexOf("(") + 1, selectedLivre.indexOf(")"));
    
        if (disponibilite.equals("indisponible")) {
            JOptionPane.showMessageDialog(null, "Livre déja emprunté");
            return;
        }
    
        String selectedAdherent = (String) comboBoxAdherents.getSelectedItem();
        int adhnum = Integer.parseInt(selectedAdherent.split(":")[0].trim());
        int isbn = Integer.parseInt(selectedLivre.split(":")[0].trim());
    
        String dateRetourText = textFieldDateRetour.getText();
        LocalDate dateRetour = parseDate(dateRetourText);
        if (dateRetour != null) {
            // Enregistrer l'emprunt dans la base de données
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
                String insertQuery = "INSERT INTO emprunt (date_emprunt, date_retour, adhnum, isbn) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                    LocalDate dateEmprunt = LocalDate.now();
                    insertStatement.setDate(1, java.sql.Date.valueOf(dateEmprunt));
                    insertStatement.setDate(2, java.sql.Date.valueOf(dateRetour));
                    insertStatement.setInt(3, adhnum);
                    insertStatement.setInt(4, isbn);
    
                    int rowsInserted = insertStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null,"Emprunt enregistré avec succès !");
                        fillComboBoxLivres(); // Rafraîchir le menu déroulant des livres
                    } else {
                        JOptionPane.showMessageDialog(null,"Erreur lors de l'enregistrement de l'emprunt.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,"Erreur de connexion à la base de données: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null,"Format de date invalide. Utilisez jj-mm-aaaa.");
        }
    }
    

    private LocalDate parseDate(String dateText) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(dateText, formatter);
        } catch (Exception e) {
            return null;
        }
    }
}