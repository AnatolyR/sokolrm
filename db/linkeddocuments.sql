-- SET search_path TO sokol;
DROP TABLE IF EXISTS linkeddocuments;
CREATE TABLE linkeddocuments (
  id uuid,
  docid uuid,
  linkid uuid,
  linktype varchar(255)
);