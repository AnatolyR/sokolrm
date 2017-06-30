DROP TABLE IF EXISTS files;
CREATE TABLE files (
  id uuid CONSTRAINT files_pkey PRIMARY KEY,
  objectId uuid,
  objectType varchar(255),
  title varchar(255),
  size integer,
  content bytea,
  author uuid,
  authorTitle varchar(255),
  created timestamp,
  searchtext text
);
CREATE INDEX files_stidx ON files USING gin (to_tsvector('russian'::regconfig, searchtext));