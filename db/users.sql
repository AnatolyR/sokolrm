DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id uuid CONSTRAINT users_pkey PRIMARY KEY,
  title varchar(255),
  login varchar(255),
  password varchar(255),
  firstName varchar(255),
  middleName varchar(255),
  lastName varchar(255)
);