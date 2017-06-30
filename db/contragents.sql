DROP TABLE IF EXISTS contragents;
CREATE TABLE contragents (
  id uuid CONSTRAINT contragents_pkey PRIMARY KEY,
  title varchar(255),
  fullName varchar(255),
  address text,
  phone varchar(255)
);

CREATE INDEX contragents_stidx ON contragents USING gin (to_tsvector('russian'::regconfig, title));