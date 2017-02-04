-- SET search_path TO sokol;
DROP TABLE IF EXISTS user_groups;
CREATE TABLE user_groups (
  userid uuid,
  groupid uuid
);