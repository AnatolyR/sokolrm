-- SET search_path TO sokol;
DROP TABLE IF EXISTS spaces;
CREATE TABLE spaces (
  id uuid CONSTRAINT spaces_pkey PRIMARY KEY,
  title varchar(255),
  creator varchar(255),
  createDate timestamp,
  modifier varchar(255),
  modifyDate timestamp,
  deletor varchar(255),
  deleteDate timestamp,
  registrationlistid uuid
);