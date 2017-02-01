-- SET search_path TO sokol;
DROP TABLE IF EXISTS accessrecords;
CREATE TABLE accessrecords (
  id uuid CONSTRAINT accessrecords_pkey PRIMARY KEY,
  groupId uuid,
  space varchar(255),
  element varchar(255),
  subelement varchar(255),
  level varchar(255)
);