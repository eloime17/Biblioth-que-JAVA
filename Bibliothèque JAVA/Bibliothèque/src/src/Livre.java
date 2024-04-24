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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Livre extends JFrame {

    private JPanel contentPane;
    private JTextField textFieldTitreAjout;
    private JTextField textFieldPrixAjout;
    private JComboBox<String> comboBoxAuteurs;
    private JComboBox<String> comboBoxLivres;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Livre frame = new Livre();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Livre() {
        setTitle("Gestion des Livres");
        setBounds(325, 200, 900, 400);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Partie Ajout de livres
        JLabel lblAjouterLivre = new JLabel("Ajouter un Livre");
        lblAjouterLivre.setBounds(30, 20, 150, 20);
        contentPane.add(lblAjouterLivre);

        JLabel lblTitreAjout = new JLabel("Titre:");
        lblTitreAjout.setBounds(30, 50, 50, 20);
        contentPane.add(lblTitreAjout);

        textFieldTitreAjout = new JTextField();
        textFieldTitreAjout.setBounds(70, 50, 210, 20);
        contentPane.add(textFieldTitreAjout);
        textFieldTitreAjout.setColumns(10);

        JLabel lblPrixAjout = new JLabel("Prix:");
        lblPrixAjout.setBounds(30, 80, 70, 20);
        contentPane.add(lblPrixAjout);

        textFieldPrixAjout = new JTextField();
        textFieldPrixAjout.setBounds(70, 80, 210, 20);
        contentPane.add(textFieldPrixAjout);
        textFieldPrixAjout.setColumns(10);
        
        JLabel lblAuteurAjout = new JLabel("Auteur:");
        lblAuteurAjout.setBounds(30, 110, 70, 20);
        contentPane.add(lblAuteurAjout);
        
        comboBoxAuteurs = new JComboBox<String>();
        comboBoxAuteurs.setBounds(80, 110, 200, 20);
        fillComboBoxAuteurs(); // Remplir le menu déroulant avec les auteurs de la base de données
        contentPane.add(comboBoxAuteurs);

// Bouton pour ajouter un livre
JButton btnAjouter = new JButton("Ajouter");
btnAjouter.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        String titre = textFieldTitreAjout.getText();
        String prixStr = textFieldPrixAjout.getText();
        double prix = Double.parseDouble(prixStr);
        String selectedAuteur = (String) comboBoxAuteurs.getSelectedItem();
        int id_auteur = Integer.parseInt(selectedAuteur.split(":")[0].trim());
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            // Vérifier si le nombre de livres est inférieur à 3
            String countQuery = "SELECT COUNT(*) AS total FROM livre";
            try (PreparedStatement countStatement = conn.prepareStatement(countQuery)) {
                ResultSet resultSet = countStatement.executeQuery();
                resultSet.next();
                int totalLivres = resultSet.getInt("total");

                if (totalLivres >= 3) {
                    JOptionPane.showMessageDialog(null, "Ajout impossible, nombre max. de livres atteint");
                } else {
                    // Vérifier si le livre existe déjà
                    String checkQuery = "SELECT * FROM livre WHERE titre = ? AND id_auteur = ?";
                    try (PreparedStatement checkStatement = conn.prepareStatement(checkQuery)) {
                        checkStatement.setString(1, titre);
                        checkStatement.setInt(2, id_auteur);
                        ResultSet checkResultSet = checkStatement.executeQuery();

                        if (checkResultSet.next()) {
                            // Afficher un message d'erreur si le livre existe déjà
                            JOptionPane.showMessageDialog(null, "Livre déjà existant");
                        } else {
                            String insertQuery = "INSERT INTO livre (titre, prix, id_auteur) VALUES (?, ?, ?)";
                            try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                                insertStatement.setString(1, titre);
                                insertStatement.setDouble(2, prix);
                                insertStatement.setInt(3, id_auteur);
                                
                                int rowsInserted = insertStatement.executeUpdate();
                                if (rowsInserted > 0) {
                                    JOptionPane.showMessageDialog(null, "Le livre a été ajouté avec succès !");
                                    textFieldTitreAjout.setText("");
                                    textFieldPrixAjout.setText("");
                                    fillComboBoxLivres(); // Actualiser le menu déroulant des livres
                                } else {
                                    JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données: " + ex.getMessage());
        }
    }
});
btnAjouter.setBounds(30, 160, 100, 25);
contentPane.add(btnAjouter);


        // Partie Modification de livres
        JLabel lblModifierLivre = new JLabel("Modifier un Livre");
        lblModifierLivre.setBounds(350, 20, 150, 20);
        contentPane.add(lblModifierLivre);

        JLabel lblLivres = new JLabel("Livre:");
        lblLivres.setBounds(350, 50, 70, 20);
        contentPane.add(lblLivres);

        comboBoxLivres = new JComboBox<>();
        comboBoxLivres.setBounds(400, 50, 250, 20);
        fillComboBoxLivres(); // Remplir le menu déroulant avec les livres de la base de données
        contentPane.add(comboBoxLivres);

        JLabel lblTitreModif = new JLabel("Nouveau Titre:");
        lblTitreModif.setBounds(350, 80, 100, 20);
        contentPane.add(lblTitreModif);

        JTextField textFieldTitreModif = new JTextField();
        textFieldTitreModif.setBounds(450, 80, 210, 20);
        contentPane.add(textFieldTitreModif);
        textFieldTitreModif.setColumns(10);

        JLabel lblPrixModif = new JLabel("Nouveau Prix:");
        lblPrixModif.setBounds(350, 110, 100, 20);
        contentPane.add(lblPrixModif);

        JTextField textFieldPrixModif = new JTextField();
        textFieldPrixModif.setBounds(450, 110, 210, 20);
        contentPane.add(textFieldPrixModif);
        textFieldPrixModif.setColumns(10);

        JButton btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedLivre = (String) comboBoxLivres.getSelectedItem();
                int isbn = Integer.parseInt(selectedLivre.split(":")[0].trim());
                String nouveauTitre = textFieldTitreModif.getText();
                double nouveauPrix = Double.parseDouble(textFieldPrixModif.getText());
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
                    String updateQuery = "UPDATE livre SET titre = ?, prix = ? WHERE isbn = ?";
                    try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, nouveauTitre);
                        updateStatement.setDouble(2, nouveauPrix);
                        updateStatement.setInt(3, isbn);
                        
                        int rowsUpdated = updateStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(null,"Le livre a été modifié avec succès !");
                            textFieldTitreModif.setText(""); // Réinitialiser les champs de texte après la modification
                            textFieldPrixModif.setText("");
                            fillComboBoxLivres(); // Actualiser le menu déroulant des livres
                        } else {
                            JOptionPane.showMessageDialog(null,"Erreur, aucun livre n'a été modifié.");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Erreur de connexion à la base de données: " + ex.getMessage());
                }
            }
        });
        btnModifier.setBounds(350, 160, 100, 25);
        contentPane.add(btnModifier);

        // Partie Suppression de livres
        JLabel lblSupprimerLivre = new JLabel("Supprimer ce Livre");
        lblSupprimerLivre.setBounds(670, 20, 150, 20);
        contentPane.add(lblSupprimerLivre);

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedLivre = (String) comboBoxLivres.getSelectedItem();
                int isbn = Integer.parseInt(selectedLivre.split(":")[0].trim());
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
                    String deleteQuery = "DELETE FROM livre WHERE isbn = ?";
                    try (PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery)) {
                        deleteStatement.setInt(1, isbn);
                        
                        int rowsDeleted = deleteStatement.executeUpdate();
                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(null,"Le livre a été supprimé avec succès !");
                            fillComboBoxLivres(); // Actualiser le menu déroulant des livres
                        } else {
                            JOptionPane.showMessageDialog(null,"Erreur, aucun livre n'a été supprimé.");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Erreur de connexion à la base de données: " + ex.getMessage());
                }
            }
        });
        btnSupprimer.setBounds(670, 50, 100, 25);
        contentPane.add(btnSupprimer);
    }

    // Méthode pour remplir le menu déroulant avec les auteurs de la base de données
    private void fillComboBoxAuteurs() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String selectQuery = "SELECT autnum, nom, prenom FROM auteur";
            try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    int id_auteur = resultSet.getInt("autnum");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    comboBoxAuteurs.addItem(id_auteur + ": " + nom + " " + prenom);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Erreur lors du chargement des auteurs: " + ex.getMessage());
        }
    }

    // Méthode pour remplir le menu déroulant avec les livres de la base de données
    private void fillComboBoxLivres() {
        comboBoxLivres.removeAllItems(); // Supprimer les éléments existants
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String selectQuery = "SELECT isbn, titre, nom, prenom FROM livre INNER JOIN auteur ON livre.id_auteur = auteur.autnum";
            try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    int isbn = resultSet.getInt("isbn");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String titre = resultSet.getString("titre");
                    comboBoxLivres.addItem(isbn+" : "+'"'+ titre + '"'+" par "+prenom+" "+nom);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Erreur lors du chargement des livres: " + ex.getMessage());
        }
    }
}