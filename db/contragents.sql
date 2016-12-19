DROP TABLE IF EXISTS contragents;
CREATE TABLE contragents (
  id uuid CONSTRAINT contragents_pkey PRIMARY KEY,
  title varchar(255)
);