DROP TABLE IF EXISTS users;
DROP ROLE IF EXISTS dev_user;
DROP ROLE IF EXISTS staging_user;
DROP ROLE IF EXISTS prod_user;

CREATE ROLE dev_user LOGIN PASSWORD 'dev_password';
CREATE ROLE staging_user LOGIN PASSWORD 'staging_password';
CREATE ROLE prod_user LOGIN PASSWORD 'prod_password';

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