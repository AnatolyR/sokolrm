-- SET search_path TO sokol;
DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks (
  id uuid CONSTRAINT tasks_pkey PRIMARY KEY,
  documentId uuid,
  listId uuid,
  userId uuid,
  executedByUser uuid,
  type varchar(255),
  status varchar(255),
  stage varchar(255),
  description varchar(255),
  created timestamp,
  author uuid,
  dueDate timestamp,
  executedDate timestamp,
  comment text,
  result varchar(255)
);