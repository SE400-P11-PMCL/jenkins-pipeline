DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL
);

INSERT INTO users
    (id, name, email)
VALUES (100,'John Doe','john@example.com');
INSERT INTO users
    (id, name, email)
VALUES (200,'Jane Smith','jane@example.com');
