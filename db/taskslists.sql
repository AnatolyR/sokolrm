-- SET search_path TO sokol;
DROP TABLE IF EXISTS taskslists;
CREATE TABLE taskslists (
  id uuid CONSTRAINT taskslists_pkey PRIMARY KEY,
  parentId uuid,
  documentId uuid,
  userId uuid,
  status varchar(255),
  comment text,
  created timestamp,
  stage varchar(255),
  type varchar(255)
);