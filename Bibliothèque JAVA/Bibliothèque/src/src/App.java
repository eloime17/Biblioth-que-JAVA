import java.sql.*;

public class App {
    public static void main(String[] args) {
        try {
            // Étape 1: Charger la classe du driver
            Class.forName("com.mysql.jdbc.Driver");
            // Étape 2: Créer l'objet de connexion
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliothèque+java", "root", "root");
            // Étape 3: Créer l'objet statement 
            Statement stmt = conn.createStatement();
            
            // Créer la table adhérent
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS adherent (adhnum INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(255), prenom VARCHAR(255), email VARCHAR(255))");
            
            // Créer la table auteur
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS auteur (autnum INT PRIMARY KEY AUTO_INCREMENT, nom VARCHAR(255), prenom VARCHAR(255), date_naissance DATE, description TEXT)");
            
            // Créer la table livre
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS livre (isbn VARCHAR(13) PRIMARY KEY, titre VARCHAR(255), prix DECIMAL(10, 2))");
            
            System.out.println("Tables créées avec succès.");

            // Étape 5: Fermer l'objet de connexion
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}