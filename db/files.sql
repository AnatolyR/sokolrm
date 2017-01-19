DROP TABLE IF EXISTS files;
CREATE TABLE files (
  id uuid CONSTRAINT files_pkey PRIMARY KEY,
  objectId uuid,
  title varchar(255),
  size integer,
  content bytea
);