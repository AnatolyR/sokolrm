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

--
-- Name: sokol; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA sokol;


SET search_path = sokol, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: accessrecords; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE accessrecords (
    id uuid NOT NULL,
    groupid uuid,
    space character varying(255),
    element character varying(255),
    subelement character varying(255),
    level character varying(255)
);


--
-- Name: contragents; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE contragents (
    id uuid NOT NULL,
    title character varying(255),
    address text,
    phone character varying(255),
    fullname character varying(255)
);


--
-- Name: dictionaries; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE dictionaries (
    id uuid NOT NULL,
    dictionaryid character varying(255),
    title character varying(255),
    creator character varying(255),
    createdate timestamp without time zone,
    modifier character varying(255),
    modifydate timestamp without time zone,
    deletor character varying(255),
    deletedate timestamp without time zone,
    deleted boolean,
    comment text
);


--
-- Name: dictionaryvalues; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE dictionaryvalues (
    id uuid NOT NULL,
    dictionaryid character varying(255),
    parentid uuid,
    title character varying(255),
    creator character varying(255),
    createdate timestamp without time zone,
    modifier character varying(255),
    modifydate timestamp without time zone,
    deletor character varying(255),
    deletedate timestamp without time zone,
    deleted boolean,
    request boolean,
    comment text
);


--
-- Name: documents; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE documents (
    id uuid NOT NULL,
    type character varying(255),
    status character varying(255),
    title character varying(255),
    "documentKind" character varying(255),
    "registrationDate" timestamp without time zone,
    correspondent character varying(255),
    "correspondentTitle" character varying(255),
    "externalSigner" character varying(255),
    "externalExecutor" character varying(255),
    "externalNumber" character varying(255),
    "externalDate" timestamp without time zone,
    "documentNumber" character varying(255),
    "pagesQuantity" integer,
    addressee character varying(255)[],
    "addresseeTitle" character varying(255)[],
    author character varying(255),
    space character varying(255),
    "itemQuantity" integer,
    "appQuantity" integer,
    "executionDate" timestamp without time zone,
    comment text,
    signer character varying(255),
    "signerTitle" character varying(255),
    "executorsTitle" character varying(255)[],
    executors character varying(255)[],
    archivecase character varying(255),
    searchtext text,
    history jsonb,
    created timestamp without time zone
);


--
-- Name: favorites; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE favorites (
    documentid character varying(255),
    userid character varying(255)
);


--
-- Name: groups; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE groups (
    id uuid NOT NULL,
    title character varying(255),
    ardata jsonb
);


--
-- Name: linkeddocuments; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE linkeddocuments (
    id uuid,
    docid uuid,
    linkid uuid,
    linktype character varying(255)
);


--
-- Name: registrationlists; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE registrationlists (
    id uuid NOT NULL,
    title character varying(255),
    prefix character varying(255),
    suffix character varying(255),
    count integer
);


--
-- Name: spaces; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE spaces (
    id uuid NOT NULL,
    title character varying(255),
    creator character varying(255),
    createdate timestamp without time zone,
    modifier character varying(255),
    modifydate timestamp without time zone,
    deletor character varying(255),
    deletedate timestamp without time zone,
    registrationlistid uuid
);


--
-- Name: table1; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE table1 (
    id uuid NOT NULL,
    type character varying(255),
    status character varying(255),
    title character varying(255)
);


--
-- Name: table2; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE table2 (
    id uuid NOT NULL,
    type2 character varying(255),
    status2 character varying(255),
    title2 character varying(255)
);


--
-- Name: tasks; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE tasks (
    id uuid NOT NULL,
    documentid uuid,
    listid uuid,
    userid uuid,
    executedbyuser uuid,
    type character varying(255),
    status character varying(255),
    stage character varying(255),
    description character varying(255),
    created timestamp without time zone,
    author uuid,
    duedate timestamp without time zone,
    executeddate timestamp without time zone,
    comment text,
    result character varying(255)
);


--
-- Name: taskslists; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE taskslists (
    id uuid NOT NULL,
    parentid uuid,
    documentid uuid,
    userid uuid,
    status character varying(255),
    comment text,
    created timestamp without time zone,
    stage character varying(255),
    type character varying(255),
    parenttaskid uuid,
    mainexecutor uuid
);


--
-- Name: user_groups; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE user_groups (
    userid uuid,
    groupid uuid
);


--
-- Name: users; Type: TABLE; Schema: sokol; Owner: -
--

CREATE TABLE users (
    id uuid NOT NULL,
    title character varying(255),
    login character varying(255),
    password character varying(255),
    firstname character varying(255),
    middlename character varying(255),
    lastname character varying(255),
    groups character varying(255)[]
);


--
-- Name: accessrecords_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY accessrecords
    ADD CONSTRAINT accessrecords_pkey PRIMARY KEY (id);


--
-- Name: contragents_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY contragents
    ADD CONSTRAINT contragents_pkey PRIMARY KEY (id);


--
-- Name: dictionaries_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY dictionaries
    ADD CONSTRAINT dictionaries_pkey PRIMARY KEY (id);


--
-- Name: dictionaryvalues_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY dictionaryvalues
    ADD CONSTRAINT dictionaryvalues_pkey PRIMARY KEY (id);


--
-- Name: documents_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY documents
    ADD CONSTRAINT documents_pkey PRIMARY KEY (id);


--
-- Name: groups_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (id);


--
-- Name: pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY table1
    ADD CONSTRAINT pkey PRIMARY KEY (id);


--
-- Name: pkey2; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY table2
    ADD CONSTRAINT pkey2 PRIMARY KEY (id);


--
-- Name: registrationlists_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY registrationlists
    ADD CONSTRAINT registrationlists_pkey PRIMARY KEY (id);


--
-- Name: spaces_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY spaces
    ADD CONSTRAINT spaces_pkey PRIMARY KEY (id);


--
-- Name: tasks_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY tasks
    ADD CONSTRAINT tasks_pkey PRIMARY KEY (id);


--
-- Name: taskslists_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY taskslists
    ADD CONSTRAINT taskslists_pkey PRIMARY KEY (id);


--
-- Name: contragents_stidx; Type: INDEX; Schema: sokol; Owner: -
--

CREATE INDEX contragents_stidx ON contragents USING gin (to_tsvector('russian'::regconfig, (title)::text));


--
-- Name: documents_stidx; Type: INDEX; Schema: sokol; Owner: -
--

CREATE INDEX documents_stidx ON documents USING gin (to_tsvector('russian'::regconfig, searchtext));


--
-- PostgreSQL database dump complete
--

