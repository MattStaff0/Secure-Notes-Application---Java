# Secure-Notes-Application---Java
Hi, this is one of my first major projects using Java and SQLite. This notes app is pretty basic in terms of UI design but functional. I plan to update this project with more features, such as UI improvements, better error handling, and password strength validation.

Overview of Secure Notes App:
Project Structure
The project consists of multiple Java classes organized into packages: secure.notes.
It follows a Model-View-Controller (MVC) architecture, where different classes handle UI (View), database operations (Model), and user interactions (Controller).
Main Components

HomePage (View):
Represents the application's main window where users can view and interact with their notes.
Displays the title, notes area, and a list of notes.
Uses helper classes like MakeTitle, NewNotesList, and MakeNotesArea to structure and populate the UI components.

LoginScreen (View):
Provides a login interface for users to authenticate themselves.
Users can either log in with existing credentials or create a new account.
Implements a basic user interface with text fields for username and password and buttons for login and new user registration.
Contains methods to handle user authentication and account creation.

DBConnect (Model):
Handles database connections using JDBC (Java Database Connectivity).
Provides methods to establish connections with the underlying database and execute SQL queries.

NewLoginHandler and LoginHandler (Controller):
Responsible for handling user authentication and account creation logic.
NewLoginHandler processes new user registrations, including hashing passwords and storing user information in the database.
LoginHandler verifies existing user credentials during login attempts.

Functionality and Features
User Authentication: Users can log in with existing usernames and passwords.
Account Creation: New users can register by providing a username and password.
Password Hashing: Passwords are hashed using the SHA-256 algorithm before being stored in the database for security.
Database Interaction: The application uses an underlying SQL database to store user information and notes.
User Interface: The UI components are designed using Swing, providing a basic yet functional interface for users to interact with.

Security Measures
Password Hashing: Passwords are hashed before being stored in the database, enhancing security by protecting users' passwords from unauthorized access.
No Special Characters in Passwords: Special characters are disallowed in usernames and passwords to prevent potential security vulnerabilities, such as SQL injection attacks.
