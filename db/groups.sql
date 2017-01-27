DROP TABLE IF EXISTS groups;
CREATE TABLE groups (
  id uuid CONSTRAINT groups_pkey PRIMARY KEY,
  title varchar(255),
  ardata jsonb
);