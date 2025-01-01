DROP TABLE IF EXISTS users; -- don't do this on a production environment

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL
);

INSERT INTO users
    (id, name, email)
VALUES (1,'John Doe','john@example.com');
INSERT INTO users
    (id, name, email)
VALUES (2,'Jane Smith','jane@example.com');
