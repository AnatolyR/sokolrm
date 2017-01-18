DROP TABLE IF EXISTS contragents;
CREATE TABLE contragents (
  id uuid CONSTRAINT contragents_pkey PRIMARY KEY,
  title varchar(255),
  fullName varchar(255),
  address text,
  phone varchar(255)
);