import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Adherent extends JFrame {

    private JPanel contentPane;
    private JTextField textFieldNomAjout;
    private JTextField textFieldPrenomAjout;
    private JTextField textFieldEmailAjout;
    private JTextField textFieldNomModif;
    private JTextField textFieldPrenomModif;
    private JTextField textFieldEmailModif;
    private JComboBox<String> comboBoxAdherents;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Adherent frame = new Adherent();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Adherent() {
        setTitle("Gestion des Adhérents");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Partie Ajout d'adhérents
        JLabel lblAjouterAdherent = new JLabel("Ajouter Adhérent");
        lblAjouterAdherent.setBounds(30, 20, 150, 20);
        contentPane.add(lblAjouterAdherent);

        JLabel lblNomAjout = new JLabel("Nom:");
        lblNomAjout.setBounds(30, 50, 50, 20);
        contentPane.add(lblNomAjout);

        textFieldNomAjout = new JTextField();
        textFieldNomAjout.setBounds(110, 50, 150, 20);
        contentPane.add(textFieldNomAjout);
        textFieldNomAjout.setColumns(10);

        JLabel lblPrenomAjout = new JLabel("Prénom:");
        lblPrenomAjout.setBounds(30, 80, 70, 20);
        contentPane.add(lblPrenomAjout);

        textFieldPrenomAjout = new JTextField();
        textFieldPrenomAjout.setBounds(110, 80, 150, 20);
        contentPane.add(textFieldPrenomAjout);
        textFieldPrenomAjout.setColumns(10);

        JLabel lblEmailAjout = new JLabel("Email:");
        lblEmailAjout.setBounds(30, 110, 70, 20);
        contentPane.add(lblEmailAjout);

        textFieldEmailAjout = new JTextField();
        textFieldEmailAjout.setBounds(110, 110, 150, 20);
        contentPane.add(textFieldEmailAjout);
        textFieldEmailAjout.setColumns(10);

        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nom = textFieldNomAjout.getText();
                String prenom = textFieldPrenomAjout.getText();
                String email = textFieldEmailAjout.getText();
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
                    // Compter le nombre d'adhérents actuels
                    String countQuery = "SELECT COUNT(*) AS total FROM adherent";
                    try (PreparedStatement countStatement = conn.prepareStatement(countQuery)) {
                        ResultSet resultSet = countStatement.executeQuery();
                        resultSet.next();
                        int totalAdherents = resultSet.getInt("total");

                        // Vérifier si le nombre d'adhérents atteint 4
                        if (totalAdherents >= 4) {
                            System.out.println("Impossible d'ajouter un nouvel adhérent, la limite de 4 est atteinte.");
                            return;
                        }
                    }

                    // Insérer l'adhérent dans la base de données
                    String insertQuery = "INSERT INTO adherent (nom, prenom, email) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, nom);
                        insertStatement.setString(2, prenom);
                        insertStatement.setString(3, email);
                        
                        int rowsInserted = insertStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("L'adhérent a été ajouté avec succès !");
                            // Réinitialiser les champs de texte après l'ajout
                            textFieldNomAjout.setText("");
                            textFieldPrenomAjout.setText("");
                            textFieldEmailAjout.setText("");
                            // Actualiser le menu déroulant
                            comboBoxAdherents.removeAllItems(); // Supprimer les éléments existants
                            fillComboBoxAdherents(); // Recharger les adhérents
                        } else {
                            System.out.println("Erreur lors de l'ajout de l'adhérent.");
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur de connexion à la base de données: " + ex.getMessage());
                }
            }
        });
        btnAjouter.setBounds(110, 140, 150, 25);
        contentPane.add(btnAjouter);

        // Partie Modification d'adhérents
        JLabel lblModifierAdherent = new JLabel("Modifier Adhérent");
        lblModifierAdherent.setBounds(300, 20, 150, 20);
        contentPane.add(lblModifierAdherent);

        comboBoxAdherents = new JComboBox<String>();
        comboBoxAdherents.setBounds(300, 50, 250, 20);
        // Remplir le menu déroulant avec les adhérents de la base de données
        fillComboBoxAdherents();
        contentPane.add(comboBoxAdherents);

        JLabel lblNomModif = new JLabel("Nom:");
        lblNomModif.setBounds(300, 80, 70, 20);
        contentPane.add(lblNomModif);

        textFieldNomModif = new JTextField();
        textFieldNomModif.setBounds(400, 80, 150, 20);
        contentPane.add(textFieldNomModif);
        textFieldNomModif.setColumns(10);

        JLabel lblPrenomModif = new JLabel("Prénom:");
        lblPrenomModif.setBounds(300, 110, 70, 20);
        contentPane.add(lblPrenomModif);

        textFieldPrenomModif = new JTextField();
        textFieldPrenomModif.setBounds(400, 110, 150, 20);
        contentPane.add(textFieldPrenomModif);
        textFieldPrenomModif.setColumns(10);

        JLabel lblEmailModif = new JLabel("Email:");
        lblEmailModif.setBounds(300, 140, 70, 20);
        contentPane.add(lblEmailModif);

        textFieldEmailModif = new JTextField();
        textFieldEmailModif.setBounds(380, 140, 150, 20);
        contentPane.add(textFieldEmailModif);
        textFieldEmailModif.setColumns(10);

        JButton btnModifier = new JButton("Modifier");
        btnModifier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Récupération des informations saisies
                String nom = textFieldNomModif.getText();
                String prenom = textFieldPrenomModif.getText();
                String email = textFieldEmailModif.getText();
                String selectedAdherent = (String) comboBoxAdherents.getSelectedItem();
                int adhnum = Integer.parseInt(selectedAdherent.split(":")[0].trim()); // Récupérer l'adhnum de l'adhérent sélectionné
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
                    String updateQuery = "UPDATE adherent SET nom = ?, prenom = ?, email = ? WHERE adhnum = ?";
                    try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, nom);
                        updateStatement.setString(2, prenom);
                        updateStatement.setString(3, email);
                        updateStatement.setInt(4, adhnum);
                        
                        int rowsUpdated = updateStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("L'adhérent a été modifié avec succès !");
                            // Réinitialiser les champs de texte après la modification
                            textFieldNomModif.setText("");
                            textFieldPrenomModif.setText("");
                            textFieldEmailModif.setText("");
                            // Actualiser le menu déroulant
                            comboBoxAdherents.removeAllItems(); // Supprimer les éléments existants
                            fillComboBoxAdherents(); // Recharger les adhérents
                        } else {
                            System.out.println("Aucun adhérent n'a été modifié.");
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur de connexion à la base de données: " + ex.getMessage());
                }
            }
        });
        btnModifier.setBounds(380, 170, 150, 25);
        contentPane.add(btnModifier);

        // Partie Suppression d'adhérents
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Récupération des informations saisies
                String selectedAdherent = (String) comboBoxAdherents.getSelectedItem();
                int adhnum = Integer.parseInt(selectedAdherent.split(":")[0].trim()); // Récupérer l'adhnum de l'adhérent sélectionné
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
                    String deleteQuery = "DELETE FROM adherent WHERE adhnum = ?";
                    try (PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery)) {
                        deleteStatement.setInt(1, adhnum);
                        
                        int rowsUpdated = deleteStatement.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("L'adhérent a été supprimé avec succès !");
                            // Actualiser le menu déroulant après la suppression
                            comboBoxAdherents.removeItem(selectedAdherent);
                        } else {
                            System.out.println("Aucun adhérent n'a été supprimé.");
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur de connexion à la base de données: " + ex.getMessage());
                }
            }
        });
        btnSupprimer.setBounds(380, 200, 150, 25);
        contentPane.add(btnSupprimer);
    }

    // Méthode pour remplir le menu déroulant avec les adhérents de la base de données
    private void fillComboBoxAdherents() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root")) {
            String selectQuery = "SELECT adhnum, nom, prenom, email FROM adherent";
            try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    int adhnum = resultSet.getInt("adhnum");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String email = resultSet.getString("email");
                    comboBoxAdherents.addItem(adhnum + ": " + nom + " " + prenom + ", " + email);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors du chargement des adhérents: " + ex.getMessage());
        }
    }
}
