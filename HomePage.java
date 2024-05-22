package secure.notes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;


public class HomePage extends JFrame{
    //  user info
    String username;
    String hashPassword;
    private boolean newUser;

    //  helper classes
    private MakeTitle titleMaker;
    NewNotesList newNotesList;
    MakeNotesArea makeNotesArea;
     
    public HomePage(String username, String hashPass, Boolean newUser){
        this.setTitle("Secure Note App");
        this.newUser = newUser;
        this.username = username;
        this.hashPassword = hashPass;
        setSettings();
        this.titleMaker = new MakeTitle(this);
        this.makeNotesArea = new MakeNotesArea(this);
        this.newNotesList = new NewNotesList(this, this.makeNotesArea, this.newUser);
        
        

        this.setVisible(true);
    }

    private void setSettings(){
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(500, 500));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.BLACK);
    }

}

class MakeTitle extends JPanel{
    HomePage parent;
    JLabel title = new JLabel("Secure Notes App");

    public MakeTitle(HomePage parent){
        this.parent = parent;
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(500, 50));
        this.setBackground(Color.BLACK);
        this.setBorder((BorderFactory.createLineBorder(Color.WHITE)));

        this.title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        this.title.setForeground(Color.WHITE);

        this.add(title);
        parent.add(this, BorderLayout.NORTH);
    }
}


class NewNotesList extends JPanel{
    HomePage parent;
    MakeNotesArea makeNotesArea;
    boolean newUser;

    JPanel createOrDestory = new JPanel();
    JPanel noteList = new JPanel();

    JLabel notes = new JLabel("Notes");
    JButton createNote = new JButton("Create");
    JButton deleteNote = new JButton("Delete");
    JButton save = new JButton("Save");

    ArrayList<String> allNotes = new ArrayList<>();
    ArrayList<JButton> allNotesButtons = new ArrayList<>();
    private ActionListener buttonActionListener;
    
    public NewNotesList(HomePage parent, MakeNotesArea makeNotesArea, boolean newUser){
        this.parent = parent;
        this.makeNotesArea = makeNotesArea;
        this.newUser = newUser;


        setSettings();

        this.notes.setFont(new Font("Times New Roman", Font.BOLD, 15));
        this.notes.setForeground(Color.WHITE);
        this.notes.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add/remove panel creation
        this.createOrDestory.setAlignmentY(TOP_ALIGNMENT);
        this.createOrDestory.setBorder((BorderFactory.createLineBorder(Color.WHITE)));
        this.createOrDestory.setPreferredSize(new Dimension(125,100));
        this.createOrDestory.setBackground(Color.BLACK);
        this.createOrDestory.setLayout(new FlowLayout());

        this.createOrDestory.add(this.createNote);
        this.createOrDestory.add(this.deleteNote);
        this.createOrDestory.add(this.save);

        // notelist panel creation
        this.noteList.setBorder((BorderFactory.createLineBorder(Color.WHITE)));
        this.noteList.setAlignmentX(BOTTOM_ALIGNMENT);
        this.noteList.setBackground(Color.BLACK);
        this.noteList.setPreferredSize(new Dimension(125,340));
        this.noteList.setLayout(new FlowLayout());
        
        this.add(this.createOrDestory, BorderLayout.NORTH);
        this.add(this.noteList, BorderLayout.CENTER);

        this.parent.add(this,BorderLayout.WEST);

        buttonActionListener = new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String noteName = ((JButton) e.getSource()).getText();
                // Handle button click (e.g., load note content)
                parent.makeNotesArea.setNoteName(noteName);
                parent.makeNotesArea.loadNote(noteName);
                //System.out.println("Button clicked: " + noteName);
            }
        };

        // adding action listeners for buttons ... 
        this.createNote.addActionListener(e -> createNewNote());
        this.save.addActionListener(e -> makeNotesArea.saveNote());
        this.deleteNote.addActionListener(e -> deleteNote());

        if (!this.newUser){
            getAllNoteNames();
            loadAllNotes();
        }
    }

    private void deleteNote(){
        String noteNameDel;
        String sql = "DELETE FROM notes WHERE username = ? AND notename = ?";

        //  means no note is selected, since will load current note when clicked on note button and give the name of the note as well
        if ((noteNameDel = this.parent.makeNotesArea.noteName) == null) return;

        try(Connection con = DBConnect.connect();
        PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1, this.parent.username);
            ps.setString(2, noteNameDel);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Issue with deleting the note, try again.");
        }

        int index = this.allNotes.indexOf(noteNameDel);
        JButton bToRemove = this.allNotesButtons.get(index);
        this.noteList.remove(bToRemove);
        this.noteList.revalidate();
        this.noteList.repaint();
        this.allNotes.remove(index);
        this.allNotesButtons.remove(index);
        this.makeNotesArea.note.setText(""); // Clear the text area
    }

    private void createNewNote() {
        String sql = "INSERT INTO notes (username, notename, note_content) VALUES (?, ?, ?)";
        String notename = JOptionPane.showInputDialog(this, "Enter note name:");
        if (notename != null && !notename.trim().isEmpty()) {
            // Add the new note to the database

            try(Connection con = DBConnect.connect();
            PreparedStatement ps = con.prepareStatement(sql)){

                //  setting the ? in the string with elements
                ps.setString(1, this.parent.username);
                ps.setString(2, notename);
                ps.setString(3, "");
                ps.executeUpdate();

                JButton button = new JButton(notename);
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setPreferredSize(new Dimension(125, 30));
                button.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
                button.addActionListener(buttonActionListener);

                this.allNotes.add(notename);
                this.allNotesButtons.add(button);

                this.noteList.add(button); // Add button to the panel
                this.noteList.revalidate(); // Refresh the layout

                this.makeNotesArea.setNoteName(notename);
                this.makeNotesArea.note.setText("");
                

            } catch (SQLException e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this.parent, "Issue with making note, try again.");
            };
            
        }
    }

    private void getAllNoteNames() {
        String sql = "SELECT notename FROM notes WHERE username = ?";
        try (Connection con = DBConnect.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, this.parent.username);
    
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    this.allNotes.add(rs.getString("notename"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.parent, "Issue with retrieving note names, try again.");
        }
    }
    

    private void loadAllNotes(){
        for (String s: allNotes){
            JButton button = new JButton(s);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(125, 30));
            button.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
            button.addActionListener(buttonActionListener);
            this.noteList.add(button); // Add button to the panel
            this.allNotesButtons.add(button);
            this.noteList.revalidate(); // Refresh the layout
        }
    }


    private void setSettings(){
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(125, 445));
        this.setBackground(Color.BLACK);
        this.setBorder((BorderFactory.createLineBorder(Color.WHITE)));
    }
}

class MakeNotesArea extends JPanel {
    HomePage parent;
    JScrollPane noteScrollPane = new JScrollPane();
    JTextArea note = new JTextArea();
    //JButton save = new JButton("Save");
    String noteName = null;

    public MakeNotesArea(HomePage parent) {
        this.parent = parent;
        this.setSettings();

        this.noteScrollPane.setViewportView(note);

        this.add(noteScrollPane, BorderLayout.CENTER);
        this.parent.add(this, BorderLayout.CENTER);
    }

    private void setSettings() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(375, 445));
        this.setBackground(Color.BLACK);
        this.setBorder((BorderFactory.createLineBorder(Color.WHITE)));
    }

    public void loadNote(String noteName){
        String query = "SELECT note_content FROM notes WHERE username = ? AND notename = ?";

        try(Connection con = DBConnect.connect();
        PreparedStatement ps = con.prepareStatement(query)){

            ps.setString(1,this.parent.username);
            ps.setString(2, noteName);
            ResultSet rs = ps.executeQuery();
            //System.out.println(rs.next());
            if (rs.next()) {
                // Read the note content from the ResultSet
                String content = rs.getString("note_content");
                //System.out.println(content);
                this.note.setText(content);
            }
            rs.close();
        } catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.parent, "Issue with loading note, try again.");
        }
    }

    public void saveNote(){
        //String noteName = getNoteName();
        String textToSave;
        if ((textToSave=this.note.getText()) == null) return;
        else{
            String sql = "UPDATE notes SET note_content = ? WHERE username = ? AND notename = ?";

            try(Connection con = DBConnect.connect();
            PreparedStatement ps = con.prepareStatement(sql)){

                ps.setString(1, textToSave);
                ps.setString(2, this.parent.username);
                ps.setString(3,this.noteName);
                ps.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this.parent, "Issue with updating note, try again.");
            }
            
        }

    }

    public void setNoteName(String name){
        this.noteName = name;
    }

}