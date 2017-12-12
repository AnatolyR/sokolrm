--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = sokol, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: files; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE files (
    id uuid NOT NULL,
    objectid uuid,
    objecttype character varying(255),
    title character varying(255),
    size integer,
    content bytea,
    author uuid,
    authortitle character varying(255),
    created timestamp without time zone,
    searchtext text
);


--
-- Name: files_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- Name: files_stidx; Type: INDEX; Schema: sokol; Owner: -
--

CREATE INDEX files_stidx ON files USING gin (to_tsvector('russian'::regconfig, searchtext));


--
-- PostgreSQL database dump complete
--

