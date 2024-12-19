package com.example.app1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * This class handles all MySQL database interactions, including user, profile, and folder management.
 * It also includes methods for encrypting and decrypting profile passwords using AES.
 */
public class MySQLConnector {

    // AES encryption key (16 bytes for AES-128)
    // ⚠️ Important: In production, store this key securely and do not hardcode it.
    private static final String SECRET_KEY = "MySecretKey12345"; // Example key (16 characters)

    /**
     * Main method to test database connection and initialization.
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to the database!");
            initializeDatabase(conn); // Ensure the tables are created
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the MySQL database.
     *
     * @return A Connection object.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/ildb"; // Replace with your DB URL
        String username = "root"; // Replace with your DB username
        String password = "Iamsql724857#"; // Replace with your DB password
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Initializes the database by creating necessary tables if they don't exist.
     *
     * @param conn The database connection.
     */
    public static void initializeDatabase(Connection conn) {
        String createUsersTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    status ENUM('Pending', 'Approved') DEFAULT 'Pending'
                );
                """;

        String createProfilesTableSQL = """
                CREATE TABLE IF NOT EXISTS profiles (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    service_name VARCHAR(255) NOT NULL,
                    profile_name VARCHAR(255) NOT NULL,
                    profile_username VARCHAR(255) NOT NULL,
                    profile_password VARCHAR(255) NOT NULL,
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
                );
                """;

        String createFoldersTableSQL = """
                CREATE TABLE IF NOT EXISTS folders (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    folder_name VARCHAR(255) NOT NULL,
                    UNIQUE(username, folder_name),
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
                );
                """;

        try (Statement stmt = conn.createStatement()) {
            // Create Users Table
            stmt.execute(createUsersTableSQL);
            System.out.println("Users table created or already exists.");

            // Ensure the 'username' column has a unique index
            try {
                String alterUsersTableSQL = "ALTER TABLE users ADD UNIQUE(username)";
                stmt.execute(alterUsersTableSQL);
                System.out.println("Unique constraint on 'username' added to 'users' table.");
            } catch (SQLException e) {
                if (e.getErrorCode() == 1061) { // 1061 = Duplicate key name
                    System.out.println("Unique constraint on 'username' already exists in 'users' table.");
                } else {
                    throw e; // Re-throw if it's a different error
                }
            }

            // Create Profiles Table
            stmt.execute(createProfilesTableSQL);
            System.out.println("Profiles table created or already exists.");

            // Create Folders Table
            stmt.execute(createFoldersTableSQL);
            System.out.println("Folders table created or already exists.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a plain-text string using AES encryption.
     *
     * @param strToEncrypt The string to encrypt.
     * @return The encrypted string in Base64 format, or null if encryption fails.
     */
    private String encrypt(String strToEncrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Using ECB mode for simplicity
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    /**
     * Decrypts an AES-encrypted string from Base64 format.
     *
     * @param strToDecrypt The encrypted string in Base64 format.
     * @return The decrypted plain-text string, or null if decryption fails.
     */
    private String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING"); // Using ECB mode for simplicity
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    /**
     * Saves a new user with a plain-text username and hashed password.
     *
     * @param username      The user's username.
     * @param email         The user's email.
     * @param plainPassword The user's plain-text password.
     * @return true if the user was saved successfully, false otherwise.
     */
    public boolean saveUser(String username, String email, String plainPassword) {
        String insertQuery = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            String hashedPassword = PasswordUtils.hashPassword(plainPassword);

            pstmt.setString(1, username); // Plain-text username
            pstmt.setString(2, email);    // Email remains unchanged
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, "Pending");
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User saved with hashed password.");
                return true;
            } else {
                System.out.println("Failed to save user.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of User objects.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, email, status FROM users";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Approves a user by setting their status to 'Approved'.
     *
     * @param userId The ID of the user to approve.
     * @return true if the user was approved successfully, false otherwise.
     */
    public boolean approveUser(int userId) {
        String updateQuery = "UPDATE users SET status = 'Approved' WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User approved successfully.");
                return true;
            } else {
                System.out.println("Failed to approve user.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    public boolean deleteUserById(int userId) {
        String deleteQuery = "DELETE FROM users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete user.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all profiles for a given username and service.
     *
     * @param username     The username of the user.
     * @param serviceName  The name of the service (e.g., Facebook).
     * @return A list of Profile objects with decrypted passwords.
     */
    public List<Profile> getProfiles(String username, String serviceName) {
        List<Profile> profiles = new ArrayList<>();
        String query = "SELECT id, profile_name, profile_username, profile_password FROM profiles WHERE username = ? AND service_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, serviceName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String decryptedPassword = decrypt(rs.getString("profile_password"));
                profiles.add(new Profile(
                        rs.getInt("id"),
                        rs.getString("profile_name"),
                        rs.getString("profile_username"),
                        decryptedPassword
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profiles;
    }

    /**
     * Updates a profile's name, username, and password, ensuring it belongs to the specified user.
     *
     * @param profileId        The ID of the profile to update.
     * @param username         The username of the user.
     * @param profileName      The new profile name.
     * @param profileUsername  The new profile username.
     * @param profilePassword  The new profile password (to be encrypted before storing).
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateProfile(int profileId, String username, String profileName, String profileUsername, String profilePassword) {
        String updateQuery = "UPDATE profiles SET profile_name = ?, profile_username = ?, profile_password = ? WHERE id = ? AND username = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            String encryptedPassword = encrypt(profilePassword);

            pstmt.setString(1, profileName);
            pstmt.setString(2, profileUsername);
            pstmt.setString(3, encryptedPassword);
            pstmt.setInt(4, profileId);
            pstmt.setString(5, username);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a new profile for a user under a specified service.
     *
     * @param username          The username of the user.
     * @param serviceName       The name of the service (e.g., Facebook).
     * @param profileName       The name of the profile.
     * @param profileUsername   The username for the profile.
     * @param profilePassword   The password for the profile (to be encrypted before storing).
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean addProfile(String username, String serviceName, String profileName, String profileUsername, String profilePassword) {
        String insertQuery = "INSERT INTO profiles (username, service_name, profile_name, profile_username, profile_password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            String encryptedPassword = encrypt(profilePassword);

            pstmt.setString(1, username);
            pstmt.setString(2, serviceName);
            pstmt.setString(3, profileName);
            pstmt.setString(4, profileUsername);
            pstmt.setString(5, encryptedPassword);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a profile by its ID and associated username.
     *
     * @param profileId The ID of the profile to delete.
     * @param username  The username of the user.
     * @return true if the profile was deleted successfully, false otherwise.
     */
    public boolean deleteProfile(int profileId, String username) {
        String deleteQuery = "DELETE FROM profiles WHERE id = ? AND username = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, profileId);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Profile deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete profile.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a new folder for a user.
     *
     * @param username    The username of the user.
     * @param folderName  The name of the folder to add.
     * @return true if the folder was added successfully, false otherwise.
     */
    public boolean addUserFolder(String username, String folderName) {
        String insertQuery = "INSERT INTO folders (username, folder_name) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, username);
            pstmt.setString(2, folderName);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Folder added successfully.");
                return true;
            } else {
                System.out.println("Failed to add folder.");
                return false;
            }

        } catch (SQLException e) {
            // Handle duplicate folder name gracefully
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.out.println("Folder already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Retrieves all folders for a given user.
     *
     * @param username The username of the user.
     * @return A list of folder names.
     */
    public List<String> getUserFolders(String username) {
        List<String> folders = new ArrayList<>();
        String query = "SELECT folder_name FROM folders WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                folders.add(rs.getString("folder_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return folders;
    }

    /**
     * Checks if a folder name already exists for a user.
     *
     * @param username   The username of the user.
     * @param folderName The folder name to check.
     * @return true if the folder exists, false otherwise.
     */
    public boolean isFolderNameExists(String username, String folderName) {
        String query = "SELECT COUNT(*) AS count FROM folders WHERE username = ? AND folder_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, folderName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String[] getUserDetails(String username) {
        String query = "SELECT username, email FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("username"), rs.getString("email")};
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
        
    }

    public boolean updateUserDetails(String currentUsername, String newUsername, String email) {
        String updateQuery = "UPDATE users SET username = ?, email = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, newUsername);
            pstmt.setString(2, email);
            pstmt.setString(3, currentUsername);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating user details: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
