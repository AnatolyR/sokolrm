-- SET search_path TO sokol;
DROP TABLE IF EXISTS dictionaries;
CREATE TABLE dictionaries (
  id uuid CONSTRAINT dictionaries_pkey PRIMARY KEY,
  dictionaryId varchar(255),
  title varchar(255),
  creator varchar(255),
  createDate timestamp,
  modifier varchar(255),
  modifyDate timestamp,
  deletor varchar(255),
  deleteDate timestamp,
  deleted boolean,
  comment text
);