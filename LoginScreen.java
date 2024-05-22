package secure.notes;

import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class LoginScreen extends JFrame {
    private NewLoginHandler newLoginHandler;
    private LoginHandler returnLoginHandler;
    // Panels
    private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel loginAreaPanel = new JPanel(new GridBagLayout());

    private JLabel username = new JLabel("Username:");
    private JLabel password = new JLabel("Password:");
    static JLabel strength = new JLabel("Password Strength:");

    JTextField usernameInput = new JTextField(15);  // Set columns to control width
    JPasswordField passwordInput = new JPasswordField(15);  // Use JPasswordField for passwords

    private JLabel title = new JLabel("Secure Notes Application");
    private JLabel message = new JLabel("For new users, enter info and click new user to make an account!");

    private JButton newUserButton = new JButton("New User!");
    private JButton loginButton = new JButton("Login");

    char[] specialChars = {
        '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', '[', ']', '{', '}', ',', '.', '/', '\\',
        '|', '<', '>', '?', '~', ';', ' '
    };
    char[] passSpecialChars = {
        '=', '[', ']', '{', '}', '/', '\\',
        '|', '<', '>', '~', ' ', '.', ','
    };


    public LoginScreen() {
        makeLoginScreen();
    }

    void makeLoginScreen() {

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(500, 500));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.BLACK);
        makeTitle();
        makeLoginArea();
        this.setVisible(true);
    }

    private void makeLoginArea() {
        // login managers
        final LoginHandler newLoginHandler;
        final LoginHandler returnLoginHandler;
        // Set size and background color for login area panel
        this.loginAreaPanel.setPreferredSize(new Dimension(400, 100));
        this.loginAreaPanel.setBackground(Color.BLACK);

        // Set font and color for labels
        this.username.setFont(new Font("Times New Roman", Font.BOLD, 15));
        this.password.setFont(new Font("Times New Roman", Font.BOLD, 15));
        LoginScreen.strength.setFont(new Font("Times New Roman", Font.BOLD, 15));
        this.username.setForeground(Color.WHITE);
        this.password.setForeground(Color.WHITE);
        LoginScreen.strength.setForeground(Color.WHITE);

        this.passwordInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passwordInput.getPassword());
                PasswordChecker checker = new PasswordChecker(password);
                String strength = checker.passwordStrength();
                LoginScreen.strength.setText("Password Strength: " + strength);
            }
        });

        RealTimePassChecker realTimeChecker = new RealTimePassChecker(this.passwordInput, LoginScreen.strength);
        realTimeChecker.start();

        // Create layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding between components

        // Add username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        this.loginAreaPanel.add(this.username, gbc);

        // Add username input field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.loginAreaPanel.add(this.usernameInput, gbc);

        // Add password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        this.loginAreaPanel.add(this.password, gbc);

        // Add password input field
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.loginAreaPanel.add(this.passwordInput, gbc);

        // Add password Strength Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        this.loginAreaPanel.add(LoginScreen.strength, gbc);


        // Add new user button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        this.loginAreaPanel.add(this.newUserButton, gbc);

        // Add login button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        this.loginAreaPanel.add(this.loginButton, gbc);

        this.newUserButton.addActionListener(e -> handleNewUser());
        this.loginButton.addActionListener(e -> handleReturningUser());

        // Add login area panel to the main frame
        this.add(this.loginAreaPanel, BorderLayout.CENTER);
    }

    private boolean containsSpecialChars(String input, char[] specialChars) {
        Set<Character> specialCharsSet = new HashSet<>();
        for (char c : specialChars) {
            specialCharsSet.add(c);
        }

        for (char c : input.toCharArray()) {
            if (specialCharsSet.contains(c)) {
                return true; // Found a special character
            }
        }
        return false; // No special characters found
    }

    private boolean containsSpecialChars(char[] input, char[] specialChars) {
        Set<Character> specialCharsSet = new HashSet<>();
        for (char c : specialChars) {
            specialCharsSet.add(c);
        }

        for (char c : input) {
            if (specialCharsSet.contains(c)) {
                return true; // Found a special character
            }
        }
        return false; // No special characters found
    }

    private void handleNewUser() {
        String username = this.usernameInput.getText();
        char[] password = this.passwordInput.getPassword();
    
        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Please provide both username and password.");
        }

        else if(containsSpecialChars(username, this.specialChars) || containsSpecialChars(password, this.passSpecialChars)){
            JOptionPane.showMessageDialog(this, "Particular special characters not allowed.");
        }
        else{
        this.newLoginHandler = new NewLoginHandler(username, password, this);
        }
    }

    private void handleReturningUser() {
        String username = this.usernameInput.getText();
        char[] password = this.passwordInput.getPassword();
    
        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Please provide both username and password.");
        }

        else if (containsSpecialChars(username, this.specialChars) || containsSpecialChars(password, this.passSpecialChars)){
            JOptionPane.showMessageDialog(this, "Particular special characters not allowed.");
        } 
        else{
            this.returnLoginHandler = new LoginHandler(username, password, this);
        }

    }

    void makeTitle() {
        // Set size and background color for the title panel
        this.titlePanel.setPreferredSize(new Dimension(500, 50));
        this.titlePanel.setBackground(Color.BLACK);

        // Set font and color for the title
        this.title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        this.title.setForeground(Color.WHITE);

        this.message.setFont(new Font("Times New Roman", Font.BOLD, 12));
        this.message.setForeground(Color.WHITE);

        // Add the title label to the title panel
        this.titlePanel.add(title);
        this.titlePanel.add(message);

        // Add the title panel to the frame
        this.add(titlePanel, BorderLayout.NORTH);
    }
}

class NewLoginHandler{
    private String password;
    private String username;
    private String hashPassword;
    LoginScreen parentComponent;
    Connection con;

    public NewLoginHandler(String username, char[] password, LoginScreen parentComponent){
        this.parentComponent = parentComponent;
        this.username = username;
        this.password = new String(password);
        this.con = DBConnect.connect();
        
        try {
            this.hashPassword = hashPassword(this.password);
        } catch (NoSuchAlgorithmException e) {}

        if (checkValidUsername(this.username)){
            if (addNewUserInfo(this.username, this.hashPassword)){
                this.parentComponent.dispose();
                // Open the home page
                SwingUtilities.invokeLater(() -> {
                    new HomePage(this.username, this.hashPassword, true);
                });
                //new HomePage(this.username, this.hashPassword);
            }
            else{
                JOptionPane.showMessageDialog(this.parentComponent, "Error adding info to database, try again!");
                this.parentComponent.passwordInput.setText("");
                this.parentComponent.usernameInput.setText("");
            }
        }
        else{
            JOptionPane.showMessageDialog(this.parentComponent, "Username already taken!");
            this.parentComponent.passwordInput.setText("");
            this.parentComponent.usernameInput.setText("");
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] pass = digest.digest(password.getBytes());
        return bytesToHex(pass);
    }

    private boolean checkValidUsername(String username) {
        //Connection con = DBConnect.connect();
        PreparedStatement ps = null;
        //ResultSet rs = null;
    
        try {
            String sql = "SELECT username FROM users WHERE username = ?";
            ps = this.con.prepareStatement(sql);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next(); // Return true if the username does not exist
            }
        } catch (SQLException e) {
            //System.out.println("check user ");
            e.printStackTrace(); // Handle the exception appropriately
            return false; // Return false if an error occurs
    }
    }

    private boolean addNewUserInfo(String username, String hashPassword){
        //Connection con = DBConnect.connect();
        PreparedStatement ps  = null;
        
        try{
            String sql = "INSERT INTO users(username, password) VALUES(?,?)";
            ps = this.con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hashPassword);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was inserted
        } catch (SQLException e) {
            //System.out.println("adding info ");
            e.printStackTrace(); // Log the error or handle it as appropriate
            try{
                this.con.close();
                ps.close();
            } catch (SQLException m){};
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

class LoginHandler{
    private String password;
    private String username;
    private String hashPassword;
    LoginScreen parentComponent;
    Connection con;
    
    public LoginHandler(String username, char[] password, LoginScreen parent){
        this.username = username;
        this.password = new String(password);
        this.parentComponent = parent;
        this.con = DBConnect.connect();

        try {
            this.hashPassword = hashPassword(this.password);
        } catch (NoSuchAlgorithmException e) {}

        if (checkValidUsername(this.username)){
            if (checkValidPassword(this.username, this.hashPassword)){
                this.parentComponent.dispose();
                // Open the home page
                SwingUtilities.invokeLater(() -> {
                    new HomePage(this.username, this.hashPassword, false);
                });
                //System.out.println("worked!");
                //new HomePage(this.username, this.hashPassword);
            } 
            else{ 
                JOptionPane.showMessageDialog(this.parentComponent, "Invalid Password for username: " + this.username);
                this.parentComponent.passwordInput.setText("");
                this.parentComponent.usernameInput.setText("");
        }       
    }
        else { 
            JOptionPane.showMessageDialog(this.parentComponent, "Invalid username: " + this.username);
            this.parentComponent.passwordInput.setText("");
            this.parentComponent.usernameInput.setText("");
    }

}

private boolean checkValidPassword(String username, String pass) {
    try {
        PreparedStatement ps = this.con.prepareStatement("SELECT password FROM users WHERE username = ?");
        ps.setString(1, username);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Check if the provided password matches the stored password
                return storedPassword.equals(pass);
            } else {
                // No such username found in the database
                return false;
            }
        }
    } catch (SQLException e) {
        // Log the exception instead of just printing it
        //System.out.println("Error while checking password validity: " + e.getMessage());
        e.printStackTrace(); // This can be replaced with a proper logging framework like SLF4J
        return false; // Return false if an error occurs
    }
}


    private String hashPassword(String password) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] pass = digest.digest(password.getBytes());
        return bytesToHex(pass);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private boolean checkValidUsername(String username) {
        //Connection con = DBConnect.connect();
        PreparedStatement ps = null;
        //ResultSet rs = null;
    
        try {
            String sql = "SELECT username FROM users WHERE username = ?";
            ps = this.con.prepareStatement(sql);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Return true if the username does exist
            }
        } catch (SQLException e) {
            //System.out.println("check user ");
            e.printStackTrace(); // Handle the exception appropriately
            return false; // Return false if an error occurs
    }
    }
}

class PasswordChecker {
    private String password;
    private static final Set<Character> lowercaseLetters = new HashSet<>();
    private static final Set<Character> uppercaseLetters = new HashSet<>();
    private static final Set<Character> numbers = new HashSet<>();
    private static final Set<Character> specialChars = new HashSet<>();

    static {
        for (char c = 'a'; c <= 'z'; c++) {
            lowercaseLetters.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            uppercaseLetters.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            numbers.add(c);
        }
        String specialCharacters = "!@#$%^&*()-+";
        for (char c : specialCharacters.toCharArray()) {
            specialChars.add(c);
        }
    }

    public PasswordChecker(String password) {
        this.password = password;
    }

    public String passwordStrength() {
        int passLen = password.length();
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (lowercaseLetters.contains(c)) hasLower = true;
            if (uppercaseLetters.contains(c)) hasUpper = true;
            if (numbers.contains(c)) hasDigit = true;
            if (specialChars.contains(c)) hasSpecial = true;
        }

        if (hasLower && hasUpper && hasDigit && hasSpecial) {
            if (passLen >= 12) {
                return "Strong";
            } else if (passLen > 8) {
                return "Moderate";
            } else {
                return "Weak - Too Short";
            }
        } else {
            return "Weak";
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


class RealTimePassChecker extends Thread{
    private JPasswordField passwordInput;
    private JLabel strength;
    private LoginHandler loginHandler;
    private NewLoginHandler newLoginHandler;
    private LoginScreen parentComponent;

    public RealTimePassChecker(JPasswordField pass, JLabel strength){
        this.passwordInput = pass;
        this.strength = strength;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500); // Check every 500ms
                SwingUtilities.invokeLater(() -> {
                    String password = new String(passwordInput.getPassword());
                    PasswordChecker checker = new PasswordChecker(password);
                    String strength = checker.passwordStrength();
                    this.strength.setText("Password Strength: " + strength);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

