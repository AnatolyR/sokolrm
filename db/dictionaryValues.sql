-- SET search_path TO sokol;
DROP TABLE IF EXISTS dictionaryvalues;
CREATE TABLE dictionaryvalues (
  id uuid CONSTRAINT dictionaryvalues_pkey PRIMARY KEY,
  dictionaryId varchar(255),
  parentId uuid,
  title varchar(255),
  creator varchar(255),
  createDate timestamp,
  modifier varchar(255),
  modifyDate timestamp,
  deletor varchar(255),
  deleteDate timestamp,
  deleted boolean,
  request boolean,
  comment text
);