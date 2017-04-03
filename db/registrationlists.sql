-- SET search_path TO sokol;
DROP TABLE IF EXISTS registrationlists;
CREATE TABLE registrationlists (
  id uuid CONSTRAINT registrationlists_pkey PRIMARY KEY,
  title varchar(255),
  prefix varchar(255),
  suffix varchar(255),
  count integer
);