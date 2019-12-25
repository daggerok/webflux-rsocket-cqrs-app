DROP TABLE IF EXISTS users;
CREATE TABLE users(
    id       UUID    NOT NULL,
    name     VARCHAR NOT NULL,
    username VARCHAR NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY ( id )
);
