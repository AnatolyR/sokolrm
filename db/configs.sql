DROP TABLE IF EXISTS configs;
CREATE TABLE configs (
    id uuid NOT NULL,
    path character varying(255) UNIQUE,
    size integer,
    content bytea,
    author uuid,
    authortitle character varying(255),
    created timestamp without time zone
);

ALTER TABLE ONLY configs
    ADD CONSTRAINT config_pkey PRIMARY KEY (id);

