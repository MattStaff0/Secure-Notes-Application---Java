CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS notes (
    username VARCHAR(255),
    notename VARCHAR(255),
    note_content TEXT,
    PRIMARY KEY (username, notename),
    FOREIGN KEY (username) REFERENCES users(username)
);
DELETE FROM users;