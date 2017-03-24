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

ALTER TABLE ONLY sokol.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY sokol.taskslists DROP CONSTRAINT taskslists_pkey;
ALTER TABLE ONLY sokol.tasks DROP CONSTRAINT tasks_pkey;
ALTER TABLE ONLY sokol.spaces DROP CONSTRAINT spaces_pkey;
ALTER TABLE ONLY sokol.table2 DROP CONSTRAINT pkey2;
ALTER TABLE ONLY sokol.table1 DROP CONSTRAINT pkey;
ALTER TABLE ONLY sokol.groups DROP CONSTRAINT groups_pkey;
ALTER TABLE ONLY sokol.documents DROP CONSTRAINT documents_pkey;
ALTER TABLE ONLY sokol.dictionaryvalues DROP CONSTRAINT dictionaryvalues_pkey;
ALTER TABLE ONLY sokol.dictionaries DROP CONSTRAINT dictionaries_pkey;
ALTER TABLE ONLY sokol.contragents DROP CONSTRAINT contragents_pkey;
ALTER TABLE ONLY sokol.accessrecords DROP CONSTRAINT accessrecords_pkey;
DROP TABLE sokol.users;
DROP TABLE sokol.user_groups;
DROP TABLE sokol.taskslists;
DROP TABLE sokol.tasks;
DROP TABLE sokol.table2;
DROP TABLE sokol.table1;
DROP TABLE sokol.spaces;
DROP TABLE sokol.groups;
DROP TABLE sokol.favorites;
DROP TABLE sokol.documents;
DROP TABLE sokol.dictionaryvalues;
DROP TABLE sokol.dictionaries;
DROP TABLE sokol.contragents;
DROP TABLE sokol.accessrecords;
DROP SCHEMA sokol;
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
    executors character varying(255)[]
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
    deletedate timestamp without time zone
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
-- Data for Name: accessrecords; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY accessrecords (id, groupid, space, element, subelement, level) FROM stdin;
e56425d0-5b30-4aa2-bb21-f2fc51a10507	b0243b1d-5268-4b34-9bae-850c3bf11da4	_system	users	\N	ADD
22461f15-f339-41c6-a990-c1a3692286da	b0243b1d-5268-4b34-9bae-850c3bf11da4	20a4ffbc-c0e0-437c-addc-eca2cc8372eb	incomingDocument	correspondent	NONE
19cf701d-cde8-4cc5-961b-2d5dd63f2bd0	b0243b1d-5268-4b34-9bae-850c3bf11da4	39649da6-9fd9-4a3b-a356-2c1c99d9619a	_document		WRITE
c47a9327-09ff-4351-8ed2-f55ec1c67790	b0243b1d-5268-4b34-9bae-850c3bf11da4	39649da6-9fd9-4a3b-a356-2c1c99d9619a	incomingDocument	status	NONE
0bff133c-440f-46a0-9b15-0c09cbf7128e	b0243b1d-5268-4b34-9bae-850c3bf11da4	39649da6-9fd9-4a3b-a356-2c1c99d9619a	incomingDocument	status	READ
94aa8236-5c5b-4006-96a7-026dd773aece	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_system	groups		WRITE
9451efb7-b4a7-40aa-8f83-6af97e349a42	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	39649da6-9fd9-4a3b-a356-2c1c99d9619a	_document		CREATE
2f75099a-4ee5-458f-b4c0-4adc8a9d1133	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	39649da6-9fd9-4a3b-a356-2c1c99d9619a	_document		WRITE
88dcb2d8-2c82-4872-ba25-4b0f89bfe05c	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	39649da6-9fd9-4a3b-a356-2c1c99d9619a	_document		DELETE
ce3a6aa8-88d6-4095-be3f-5fa5d928bc84	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_system	groups		CREATE
956a9816-8cdc-4d4a-93dc-10e7093da5cb	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_system	groups		LIST
25dc15e4-25cb-4aca-a179-2e722eaa9bbc	7db50d24-7de5-46e8-87ae-aa9cfa08b144	_system	groups		LIST
3071842a-b3d3-41c7-b57a-287ef279c98e	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_system	groups		READ
168b42e0-e125-4730-85ed-1f98d747a559	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_system	users		LIST
7e3791c3-b960-4bdf-b474-e69e9fd85f32	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	toproject	CREATE
cde0d507-14ad-4c42-8ed3-6d745d49cf4b	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*doregistration	ALLOW
60386923-3b04-47e8-b041-9864381c1e6d	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*toreview	ALLOW
cabb4619-38b1-4f57-bf49-00be5f104870	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*doresolution	ALLOW
1a43f7a1-26c2-4247-907e-1a7c05a9110c	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*tocase	ALLOW
3322c0ce-c636-4661-91b8-7d60e84d6737	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*toarchive	ALLOW
82c238af-e28e-42d7-a79d-8c21568965c2	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*toproject	ALLOW
c0925422-1f94-4540-9911-d12f228fc468	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*tosign	ALLOW
8e182915-8a2f-4ef5-bde6-31367d415a3d	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*sign	ALLOW
7230b6b4-c110-4959-802d-19050fde5364	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*reject	ALLOW
4475c05a-1274-4cef-a268-0c01d6007899	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*send	ALLOW
df59c89c-01a4-4151-b88a-796b0e28e311	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document		READ
a40c51c6-bbba-4a0f-a9ef-8b9dbfac1ef4	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	_space	_document	*toapproval	ALLOW
21375c9b-524b-45bb-9cde-204ae29e8677	7db50d24-7de5-46e8-87ae-aa9cfa08b144	_space	_document		READ
\.


--
-- Data for Name: contragents; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY contragents (id, title, address, phone, fullname) FROM stdin;
970cdb4c-cd9a-4944-9bf0-f78585220893	РОСТРА, ОАО, страховая компания	\N	\N	\N
4a2bec91-3bff-48bf-a052-19a150dde393	УралСиб, ЗАО Страховая группа	\N	\N	\N
afc55206-1820-4da1-8bed-69705d202332	ФЕНИКС, страховое агентство	\N	\N	\N
79876265-1e71-47c5-8fec-1f203e862b7c	ЭНЕРГОГАРАНТ, страхование	\N	\N	\N
60d3fde7-c523-4afb-8a56-e713775a3be1	КАПИТАЛ, деловой центр	\N	\N	\N
f1eddfae-b6e1-43f6-b73a-31f3f14f517c	ПРОФИНТЕР ООО, реклам	\N	\N	\N
05a45146-6d08-4021-9248-014a0f373a51	РОСТЕЛЕКОМ, услуги связи, интернет-провайдер	\N	\N	\N
55c3554b-99cc-4857-a964-7a2731a6ddfe	ИСКУССТВО СТИЛЯ, имидж-агентство	\N	\N	\N
ccfde8f8-1563-4dea-81f9-4b15dd3633a0	КАРАВЕЛЛА, бюро переводов	\N	\N	\N
3704a2c0-015f-4c3f-b5ec-01556fc4a204	АРХИТЕКТУРА, ООО, проектная организация	\N	\N	\N
648f37bd-f6b3-4795-8b5c-e7d6f70b8b40	ГРАЖДАНПРОЕКТ, ООО, проектно-конструкторская фирма	\N	\N	\N
65208064-e1ef-4bf5-be7b-39a3839668d3	КВАЛИТЕТ, строительная экспертиза	\N	\N	\N
9f470a59-4216-4958-b01c-a6093f6de5fb	ПЛАНЕТА, кадастровый центр	\N	\N	\N
57a1ecbb-e1cc-4ab1-b782-88e723b416dc	КРОВПРОЕКТ, ООО, строительная организация	\N	\N	\N
e4a238ef-2824-4374-88c1-92388a9b05eb	АЛАТОО ИТЦ, электромонтажные работы	\N	\N	\N
63e5b8b9-d2e0-499b-9b2e-75fb02191ae7	СТРИМ, автострахование			111
8ce9ecce-c2d1-4ab4-b72a-48c6b711a241	ВТБ 24			ВТБ 24
\.


--
-- Data for Name: dictionaries; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY dictionaries (id, dictionaryid, title, creator, createdate, modifier, modifydate, deletor, deletedate, deleted, comment) FROM stdin;
\.


--
-- Data for Name: dictionaryvalues; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY dictionaryvalues (id, dictionaryid, parentid, title, creator, createdate, modifier, modifydate, deletor, deletedate, deleted, request, comment) FROM stdin;
6fbc32d2-0485-4b6c-a021-1844c845c2bd	documentKind	\N	Апелляционная жалоба	\N	\N	\N	\N	\N	\N	\N	\N	\N
333f7dda-9c9b-4a06-9346-d00ca7c698b2	documentKind	\N	Запрос	\N	\N	\N	\N	\N	\N	\N	\N	\N
3da3b499-91c9-4637-80d7-e445391b8a37	documentKind	\N	Заявка	\N	\N	\N	\N	\N	\N	\N	\N	\N
111beda3-3f83-4b94-a771-658e5b55dbc0	documentKind	\N	Извещение	\N	\N	\N	\N	\N	\N	\N	\N	\N
7b3c0298-9411-44d5-8f8e-1e7bfd52b708	documentKind	\N	Исполнительный лист	\N	\N	\N	\N	\N	\N	\N	\N	\N
95de1418-4b97-4d39-b60b-f3ba1935006d	documentKind	\N	Определение	\N	\N	\N	\N	\N	\N	\N	\N	\N
99efc746-abc6-46ea-806a-b4541906536a	documentKind	\N	Ответ на запрос	\N	\N	\N	\N	\N	\N	\N	\N	\N
251e563b-c350-479f-896b-383312a374ca	documentKind	\N	Повестка	\N	\N	\N	\N	\N	\N	\N	\N	\N
808aecf7-b03a-4673-8114-4c74c985a05d	documentKind	\N	Представление	\N	\N	\N	\N	\N	\N	\N	\N	\N
395fb6b3-ed0d-4a72-8380-947211112ac2	documentKind	\N	Предупреждение	\N	\N	\N	\N	\N	\N	\N	\N	\N
a95815a0-d33c-4bbd-8d02-db76c36f64c7	documentKind	\N	Приглашение	\N	\N	\N	\N	\N	\N	\N	\N	\N
b38de36d-50b0-4411-8c35-f3f05087afe3	documentKind	\N	Приговор суда	\N	\N	\N	\N	\N	\N	\N	\N	\N
75f5e60d-5005-4841-ac8c-818a18d1e29b	documentKind	\N	Распоряжение	\N	\N	\N	\N	\N	\N	\N	\N	\N
2c66768d-3975-498b-8afc-d178dd290567	documentKind	\N	Телефонограмма	\N	\N	\N	\N	\N	\N	\N	\N	\N
f4bb5ef6-995c-46fe-80c3-216c2fa37cb4	documentKind	\N	Требование	\N	\N	\N	\N	\N	\N	\N	\N	\N
1e3e3392-8c78-49c0-a560-d68199942f7a	documentKind	\N	Уведомление	\N	\N	\N	\N	\N	\N	\N	\N	\N
02054adb-7be4-47c1-baee-8515f41d5bb4	documentKind	\N	Жалоба	\N	\N	\N	\N	\N	\N	\N	\N	\N
921712b3-7340-4413-9948-aac05932bdc4	documentKind	\N	Исковое заявление	\N	\N	\N	\N	\N	\N	\N	\N	\N
e435c2c2-2eee-41c4-b322-4fb6324eaa50	documentKind	\N	Кассационная жалоба	\N	\N	\N	\N	\N	\N	\N	\N	\N
981e0fe3-1538-403c-b117-db2f93cc52b7	documentKind	\N	Письмо	\N	\N	\N	\N	\N	\N	\N	\N	\N
e2d3be91-bd48-4631-bede-a6e2ac86d4f9	documentKind	\N	Поручение	\N	\N	\N	\N	\N	\N	\N	\N	\N
025cca21-92e8-4941-9569-8afd13e37524	documentKind	\N	Предписание	\N	\N	\N	\N	\N	\N	\N	\N	\N
b9a8d2d0-5c73-4b4a-ab8c-cb744342a21f	documentKind	\N	Претензия	\N	\N	\N	\N	\N	\N	\N	\N	\N
2d74d432-a430-4917-858b-366a5b603622	documentKind	\N	Приказ	\N	\N	\N	\N	\N	\N	\N	\N	\N
098aa80a-ba54-4c0f-8bfd-10c44bbe7bb1	documentKind	\N	Протокол	\N	\N	\N	\N	\N	\N	\N	\N	\N
da13ae00-1e25-4f93-b170-f41132f8353d	documentKind	\N	Решение	\N	\N	\N	\N	\N	\N	\N	\N	\N
59f6524e-52af-4697-8385-ba056a1e1e80	documentKind	\N	Справка	\N	\N	\N	\N	\N	\N	\N	\N	\N
f37d57eb-5c3e-490f-b5f7-462607b9a982	documentKind	\N	Судебная повестка	\N	\N	\N	\N	\N	\N	\N	\N	\N
8b9e5e98-02d0-4c8b-93ab-d42399e94cc1	documentKind	\N	Телеграмма	\N	\N	\N	\N	\N	\N	\N	\N	\N
14d15543-7cac-4927-a63a-9653e10f6bfe	documentKind	\N	Указ	\N	\N	\N	\N	\N	\N	\N	\N	\N
c926f49a-fb4c-4527-98bf-32836215cbd5	documentKind	\N	Указание Минтранса	\N	\N	\N	\N	\N	\N	\N	\N	\N
5bd27ea3-80a5-4cb8-a289-14c2c0f9a4f9	documentKind	\N	Акт	\N	\N	\N	\N	\N	\N	\N	\N	\N
8b54a200-0684-49d0-a07c-7883d553f7c8	\N	\N	Акт	\N	\N	\N	\N	\N	\N	\N	\N	\N
ad29f1a1-dd46-4c8a-93ab-f567e41dde5d	documentTitles	\N	1111	\N	\N	\N	\N	\N	\N	\N	\N	\N
c8c55c67-89c8-46a8-9212-2d90ffb1d0ed	executionReportStatus	\N	Исполнено	\N	\N	\N	\N	\N	\N	\N	\N	\N
ebabac3d-b042-4721-883e-268745c1595f	executionReportStatus	\N	Не исполнено	\N	\N	\N	\N	\N	\N	\N	\N	\N
f1a642c5-653e-4a49-87bd-bd5391b207d7	documentTitles	\N	О платежах	\N	\N	\N	\N	\N	\N	\N	\N	\N
c97dd759-17bc-4f7d-b888-1f59726f7ac1	executionReportStatus	\N	укукуотуе	\N	\N	\N	\N	\N	\N	\N	\N	\N
\.


--
-- Data for Name: documents; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY documents (id, type, status, title, "documentKind", "registrationDate", correspondent, "correspondentTitle", "externalSigner", "externalExecutor", "externalNumber", "externalDate", "documentNumber", "pagesQuantity", addressee, "addresseeTitle", author, space, "itemQuantity", "appQuantity", "executionDate", comment, signer, "signerTitle", "executorsTitle", executors) FROM stdin;
10984d8e-8e18-4384-95bd-8743d1f98676	incomingDocument	\N			2016-04-13 22:03:25.815		ООО 3	\N	\N	\N	\N	\N	\N	{}	{"Кузьмин С. В."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
04b77f61-f92c-4e44-9559-c76261ce3d1e	incomingDocument	\N	jksdf lsdhjf jksd gfjg		2016-04-13 22:44:37.929		ООО 5	\N	\N	\N	\N	\N	\N	{}	{"Быков П. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
b0cf186d-ae9c-44e0-93db-d4d2a72fc88b	test	\N	Test document 2	Тестовый	2016-04-09 15:43:34.545	correspondent 1	ООО 1	\N	\N	\N	\N	\N	\N	{}	{"Леонов С. Р."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
d0e38e2e-18bd-48fd-91ec-5e102519cd06	incomingDocument	\N	Test document 1rtyr2355777	Письмо	2016-04-23 23:32:49.359	65208064-e1ef-4bf5-be7b-39a3839668d3	КВАЛИТЕТ, строительная экспертиза	\N	\N	\N	\N	\N	\N	{bbb3bfbf-66af-41b8-8a6d-24f6e527386a,be547579-8664-4221-8dde-029ef242b517,137e29ed-acb2-4f06-b6f6-74ed1b332caa}	{"Середин Т. И.","Власов Л. И.","Козлов Я. В."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
ed15fe1e-870a-4c01-92e1-39738e678edd	incomingDocument	draft	Проверка новых полей	Акт	\N	\N	\N				\N		1	{3379db0f-8221-43fd-8d46-e05edcef9686}	{"Ивашов Н. В."}	722b151c-f9d7-4222-b541-cfc554695510	\N	2	3	2017-04-01 16:10:00		\N	\N	\N	\N
20a8fac1-cebd-455a-8eb6-ce34e59c3631	incomingDocument	\N	1235678+	Приглашение	2016-11-12 21:51:00	3704a2c0-015f-4c3f-b5ec-01556fc4a204	АРХИТЕКТУРА, ООО, проектная организация	pppppp	eeeee	212143234234	2016-12-30 21:55:00	sd23235	3	{4f4181fc-6953-4fd0-8684-37e435e3cce1,e879c49c-4fdf-43a1-8507-7091f2dea03d}	{"Волкова А. Г.","Волков Б. П."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
698a2c4b-ac01-4b68-a5a4-cb4cd20ed751	incomingDocument	review	2222222		\N	\N	\N				\N		\N	{9bb42bab-8965-49d2-b134-cec0d1505cc3}	{"Ивашова А. Е."}	722b151c-f9d7-4222-b541-cfc554695510	\N	\N	\N	\N	\N	\N	\N	\N	\N
13e615aa-3ac7-4fc5-8484-a7bf72bf222f	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
60c520e0-2005-454b-9a11-16647c818124	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
d45bf780-1983-4e2e-b352-9b21a687bb1a	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
79c868d6-27f5-4859-a16c-0db368576964	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
19435abb-7e0b-478f-b265-bc7fd78a689f	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
6863ac65-d40d-4dc1-98c4-61a087556e43	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
165df595-d6c4-4914-bcf2-ee786e9884a6	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
1cb5f042-7381-4846-b93e-40f0b05d254e	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
b1bfc6fa-1bc9-496c-a2fa-2515db27d284	incomingDocument	Черновик	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
bed13521-d500-4e92-82f9-63dfafccb968	incomingDocument	Черновик	11111111	Указ	\N	\N	\N				\N		\N	{3379db0f-8221-43fd-8d46-e05edcef9686}	{"Ивашов Н. В."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
72c8d4e3-3a74-4d56-8c74-4ae9dccb4590	incomingDocument	Черновик	111222333	Указ	\N	\N	\N				\N		\N	{722b151c-f9d7-4222-b541-cfc554695510}	{"Ивашов В. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
1cf6a554-0c32-4450-8c10-822a7d8fadb0	incomingDocument	Черновик	sdfdsfdf		\N	\N	\N				\N		\N	{722b151c-f9d7-4222-b541-cfc554695510}	{"Ивашов В. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
0ea7d868-d99c-4812-939f-314563f42fc2	incomingDocument	Черновик	565667777		\N	\N	\N				\N		\N	{722b151c-f9d7-4222-b541-cfc554695510}	{"Ивашов В. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
534c9631-a7c2-4145-bde5-13389f42d106	incomingDocument	Черновик	График платежей	Справка	2017-02-25 14:55:00	8ce9ecce-c2d1-4ab4-b72a-48c6b711a241	ВТБ 24				\N		5	{722b151c-f9d7-4222-b541-cfc554695510}	{"Ивашов В. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
001afe34-7912-4571-83a4-9ae0e7e9b8dc	incomingDocument	registered	Проверка Зарегистрирован		\N	\N	\N				\N		\N	{722b151c-f9d7-4222-b541-cfc554695510}	{"Ивашов В. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
de32b8fe-7c34-4417-87f7-844be0c4663f	incomingDocument	review	Проверка Рассмотрение		\N	\N	\N				\N		\N	{722b151c-f9d7-4222-b541-cfc554695510}	{"Ивашов В. Н."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
f9e65b52-8594-4865-b5e3-f234d62c1a48	incomingDocument	tocase	Супер тест 1		\N	\N	\N				\N		\N	{3379db0f-8221-43fd-8d46-e05edcef9686}	{"Ивашов Н. В."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
57b5cfa7-9dfb-40a4-8044-8f3fb6a71a83	incomingDocument	execution	6tu7	567	2016-04-13 22:45:00	\N	\N				\N	1122	\N	{3379db0f-8221-43fd-8d46-e05edcef9686}	{"Ивашов Н. В."}	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
49bb9b47-2ea1-4f63-9ceb-e89c0a42d19d	outgoingDocument	approval	Исходящий 2		\N	4a2bec91-3bff-48bf-a052-19a150dde393	УралСиб, ЗАО Страховая группа	\N	\N	\N	\N		\N	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	\N	\N	\N	\N	\N	\N	\N	\N
04743fe6-f08f-4963-96db-07c32e57b5c1	incomingDocument	draft	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	\N	\N	\N	\N	\N	\N	\N	\N
d0004a5c-b3d9-4934-bed8-05ad15cc037c	outgoingDocument	approval	Исходящий 1	Указ	2017-03-22 17:25:00	55c3554b-99cc-4857-a964-7a2731a6ddfe	ИСКУССТВО СТИЛЯ, имидж-агентство	\N	\N	\N	\N	ИСХ-100500	1	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	2	3	2017-03-23 17:25:00	Olololo THIS IS COMMMENT!!!!!	580f62b3-7b96-4109-a321-dc7d24109a1a	Поляков И. В.	{"Луков Б. П.","Захаров Н. В."}	{5ca6d548-afa3-4c26-a72e-f0f19100e701,a2cfff23-4070-4063-9e94-c3956e824122}
c5e712ba-3a0a-459d-8377-35b840e0360a	outgoingDocument	draft	Все поля	Указ	2017-03-21 17:33:00	\N	\N	\N	\N	\N	\N	ИСХ-012	1	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	2	3	2017-04-02 17:32:00	comment	\N	\N	\N	\N
1eb9b85a-ddcb-4e79-b669-7711e0523752	internalDocument	draft	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	\N	\N	\N	\N	\N	\N	\N	\N
49479cff-f719-4a7f-a7e5-0718746631b4	outgoingDocument	draft	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	\N	\N	\N	\N	\N	\N	\N	\N
6ca8ee0e-a78e-4162-b4ca-33d980e8dd32	internalDocument	draft	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	\N	\N	\N	\N	\N	\N	\N	\N
f3267d38-b674-4964-9003-1109edeaf1d7	internalDocument	draft	Все поля		2017-03-21 17:45:00	\N	\N	\N	\N	\N	\N	ВН-110	1	\N	\N	722b151c-f9d7-4222-b541-cfc554695510	\N	2	3	2017-04-01 17:45:00	Коммент	\N	\N	\N	\N
\.


--
-- Data for Name: favorites; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY favorites (documentid, userid) FROM stdin;
20a8fac1-cebd-455a-8eb6-ce34e59c3631	user1
\.


--
-- Data for Name: groups; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY groups (id, title, ardata) FROM stdin;
b0243b1d-5268-4b34-9bae-850c3bf11da4	Test Group M	{"testField": "testValue"}
e7bfb185-f00e-4bd3-8aaa-8ff2499fb987	Test Group 1	\N
6283b250-dab4-4229-9f6f-d11f488d375e	Test Group 3	\N
a838c335-0fdb-4b3e-9e30-da7fb51e676b	Test Group 4	\N
30ff2649-59b3-473d-a176-29c99ce9bb2e	Test Group 2	\N
7db50d24-7de5-46e8-87ae-aa9cfa08b144	Пользователи	\N
bdc4e199-61e2-4cc6-81c8-0585d5e95b55	Делопроизводители	\N
6f4c9bdb-2cb2-49ee-aa29-0543edbc5103	Администраторы	\N
\.


--
-- Data for Name: spaces; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY spaces (id, title, creator, createdate, modifier, modifydate, deletor, deletedate) FROM stdin;
39649da6-9fd9-4a3b-a356-2c1c99d9619a	Test 1	\N	\N	\N	\N	\N	\N
20a4ffbc-c0e0-437c-addc-eca2cc8372eb	Test Space 3	\N	\N	\N	\N	\N	\N
\.


--
-- Data for Name: table1; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY table1 (id, type, status, title) FROM stdin;
\.


--
-- Data for Name: table2; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY table2 (id, type2, status2, title2) FROM stdin;
\.


--
-- Data for Name: tasks; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY tasks (id, documentid, listid, userid, executedbyuser, type, status, stage, description, created, author, duedate, executeddate, comment, result) FROM stdin;
0224ba9f-c5c9-4511-8863-585a1c9abd3f	d0004a5c-b3d9-4934-bed8-05ad15cc037c	b582ef16-63c9-4173-be2c-25501dd64d67	722b151c-f9d7-4222-b541-cfc554695510	\N	approval	run	\N	111	2017-03-14 13:30:35.176	722b151c-f9d7-4222-b541-cfc554695510	2017-03-15 00:00:00	\N	\N	\N
452cc7aa-a57f-4c79-8800-beef99c13877	d0004a5c-b3d9-4934-bed8-05ad15cc037c	b582ef16-63c9-4173-be2c-25501dd64d67	e879c49c-4fdf-43a1-8507-7091f2dea03d	\N	approval	complete	\N	111	2017-03-07 11:23:10.906	722b151c-f9d7-4222-b541-cfc554695510	2017-03-08 00:00:00	2017-03-07 15:08:56.83	111	agreed
9e981b55-2d9e-49ed-93e1-bb5444883ba0	49bb9b47-2ea1-4f63-9ceb-e89c0a42d19d	810e9596-4cc7-469c-a0bf-3aa2d479d8fe	5d729b02-4d8c-4abd-80cc-c7497d1945cf	5d729b02-4d8c-4abd-80cc-c7497d1945cf	approval	complete	\N	internal 3	2017-03-14 14:22:56.89	722b151c-f9d7-4222-b541-cfc554695510	2017-03-15 00:00:00	2017-03-14 14:23:28.489		agreed
1b3d6646-a133-4231-9ace-750f39df0ebe	20a8fac1-cebd-455a-8eb6-ce34e59c3631	ee8d5dd2-f276-433f-a7c1-29df1c539a34	722b151c-f9d7-4222-b541-cfc554695510	722b151c-f9d7-4222-b541-cfc554695510	execution	complete	\N	Виполнить бистро	2017-02-16 19:17:31.819	722b151c-f9d7-4222-b541-cfc554695510	2017-02-17 00:00:00	2017-02-22 09:42:26.268	Yes, sir	Исполнено
44b10ab2-24fd-4293-aada-6612c0d594c0	57b5cfa7-9dfb-40a4-8044-8f3fb6a71a83	09f01593-0ed4-4958-a9ea-68e8675d9679	722b151c-f9d7-4222-b541-cfc554695510	722b151c-f9d7-4222-b541-cfc554695510	execution	complete	\N	555777 ololo 22233	2017-02-16 16:36:30.674	06fad082-6745-4265-bec1-fd1aa741844b	2017-03-01 00:00:00	2017-03-10 14:09:32.941	Я сделаль	not_done
1c6bf35e-ca5d-4210-99a4-46caf7b8e8e4	57b5cfa7-9dfb-40a4-8044-8f3fb6a71a83	09f01593-0ed4-4958-a9ea-68e8675d9679	a1109397-b265-4cf4-8d6e-986c0fa0ce54	\N	execution	run	\N	555777 ololo 22233	2017-02-16 19:13:16.19	722b151c-f9d7-4222-b541-cfc554695510	2017-02-18 00:00:00	\N	\N	\N
0fa1075d-dc06-4a14-bd63-ec8811ff1351	20a8fac1-cebd-455a-8eb6-ce34e59c3631	ee8d5dd2-f276-433f-a7c1-29df1c539a34	1b025ff2-948d-4f7f-8261-6456f8f8c19e	\N	execution	complete	\N	Виполнить бистро	2017-02-16 19:17:31.809	722b151c-f9d7-4222-b541-cfc554695510	2017-02-19 00:00:00	\N	Тут возникли проблемы кое какие	Не исполнено
86752fe1-ecc8-47bb-9365-2e8675871caf	57b5cfa7-9dfb-40a4-8044-8f3fb6a71a83	09f01593-0ed4-4958-a9ea-68e8675d9679	e879c49c-4fdf-43a1-8507-7091f2dea03d	\N	execution	run	\N	555777 ololo 22233	2017-02-16 19:13:16.197	722b151c-f9d7-4222-b541-cfc554695510	2017-02-26 00:00:00	\N	\N	\N
b439b725-94a3-425d-82e2-7c796c4677f5	534c9631-a7c2-4145-bde5-13389f42d106	f3af9cde-e926-4ad6-a25d-ba6e499a996f	580f62b3-7b96-4109-a321-dc7d24109a1a	\N	execution	run	\N	Отнести в налоговую	2017-02-25 15:02:19.086	722b151c-f9d7-4222-b541-cfc554695510	2017-02-27 00:00:00	\N	\N	\N
43fa2099-2483-4c37-af68-4fd778dfe0f4	534c9631-a7c2-4145-bde5-13389f42d106	f3af9cde-e926-4ad6-a25d-ba6e499a996f	722b151c-f9d7-4222-b541-cfc554695510	722b151c-f9d7-4222-b541-cfc554695510	execution	complete	\N	Отнести в налоговую	2017-02-25 15:02:19.093	722b151c-f9d7-4222-b541-cfc554695510	2017-02-27 00:00:00	2017-02-25 15:03:15.288	Олоолоооооло	Исполнено
875251fc-b4ba-4f4f-8e68-d18c373ad47e	f9e65b52-8594-4865-b5e3-f234d62c1a48	2b95d8ba-1749-4c64-8e9c-b477449aaaa4	580f62b3-7b96-4109-a321-dc7d24109a1a	\N	execution	complete	\N	123	2017-02-28 17:06:52.249	722b151c-f9d7-4222-b541-cfc554695510	2017-03-01 00:00:00	\N	\N	\N
1eff5dd2-f38b-4f66-905c-6bb40477409a	f9e65b52-8594-4865-b5e3-f234d62c1a48	2b95d8ba-1749-4c64-8e9c-b477449aaaa4	722b151c-f9d7-4222-b541-cfc554695510	722b151c-f9d7-4222-b541-cfc554695510	execution	complete	\N	123	2017-02-28 17:06:52.265	722b151c-f9d7-4222-b541-cfc554695510	2017-03-01 00:00:00	2017-02-28 17:41:25.799	11	Исполнено
32319d94-873f-4ae7-a495-3864e6316f49	d0004a5c-b3d9-4934-bed8-05ad15cc037c	b582ef16-63c9-4173-be2c-25501dd64d67	a1109397-b265-4cf4-8d6e-986c0fa0ce54	\N	approval	run	\N	111	2017-03-12 12:59:43.718	722b151c-f9d7-4222-b541-cfc554695510	2017-03-18 00:00:00	\N	\N	\N
71ce57ad-ddfd-43dc-84ea-3dec48a8a57a	d0004a5c-b3d9-4934-bed8-05ad15cc037c	b582ef16-63c9-4173-be2c-25501dd64d67	fb2f4ebc-34c6-4fb5-b73a-7ed1b9249a68	\N	approval	run	\N	111	2017-03-12 13:01:03.413	722b151c-f9d7-4222-b541-cfc554695510	2017-03-17 00:00:00	\N	\N	\N
088a8b15-0750-422a-ab3c-0784f59284c6	49bb9b47-2ea1-4f63-9ceb-e89c0a42d19d	478f6ee4-3ff1-4172-bf84-66a43bfdfd09	722b151c-f9d7-4222-b541-cfc554695510	\N	approval	run	\N	111	2017-03-14 09:46:03.799	722b151c-f9d7-4222-b541-cfc554695510	2017-03-15 00:00:00	\N	\N	\N
98536137-a2d2-4c5e-810c-7dfdc4c9b572	49bb9b47-2ea1-4f63-9ceb-e89c0a42d19d	478f6ee4-3ff1-4172-bf84-66a43bfdfd09	b1fea135-9e3e-4e41-ad6d-492841868fd5	\N	approval	run	\N	111	2017-03-14 09:37:54.917	722b151c-f9d7-4222-b541-cfc554695510	2017-03-15 00:00:00	\N	\N	\N
\.


--
-- Data for Name: taskslists; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY taskslists (id, parentid, documentid, userid, status, comment, created, stage, type, parenttaskid, mainexecutor) FROM stdin;
ee8d5dd2-f276-433f-a7c1-29df1c539a34	\N	20a8fac1-cebd-455a-8eb6-ce34e59c3631	722b151c-f9d7-4222-b541-cfc554695510	\N	Виполнить бистро	2017-02-16 19:17:31.73	\N	execution	\N	\N
f3af9cde-e926-4ad6-a25d-ba6e499a996f	\N	534c9631-a7c2-4145-bde5-13389f42d106	722b151c-f9d7-4222-b541-cfc554695510	\N	Отнести в налоговую	2017-02-25 15:01:35.174	\N	execution	\N	\N
2b95d8ba-1749-4c64-8e9c-b477449aaaa4	\N	f9e65b52-8594-4865-b5e3-f234d62c1a48	722b151c-f9d7-4222-b541-cfc554695510	\N	123	2017-02-28 17:06:52.151	\N	execution	\N	\N
b582ef16-63c9-4173-be2c-25501dd64d67	\N	d0004a5c-b3d9-4934-bed8-05ad15cc037c	722b151c-f9d7-4222-b541-cfc554695510	\N	111	2017-03-07 11:23:10.806	\N	approval	\N	\N
478f6ee4-3ff1-4172-bf84-66a43bfdfd09	\N	49bb9b47-2ea1-4f63-9ceb-e89c0a42d19d	722b151c-f9d7-4222-b541-cfc554695510	\N	111	2017-03-14 08:36:43.181	\N	approval	\N	\N
810e9596-4cc7-469c-a0bf-3aa2d479d8fe	478f6ee4-3ff1-4172-bf84-66a43bfdfd09	49bb9b47-2ea1-4f63-9ceb-e89c0a42d19d	722b151c-f9d7-4222-b541-cfc554695510	\N	internal 3	2017-03-14 13:25:10.107	\N	approval	088a8b15-0750-422a-ab3c-0784f59284c6	\N
09f01593-0ed4-4958-a9ea-68e8675d9679	\N	57b5cfa7-9dfb-40a4-8044-8f3fb6a71a83	722b151c-f9d7-4222-b541-cfc554695510	\N	555777 ololo 22233	2017-02-16 10:53:06.866	\N	execution	\N	a1109397-b265-4cf4-8d6e-986c0fa0ce54
\.


--
-- Data for Name: user_groups; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY user_groups (userid, groupid) FROM stdin;
25353214-53fd-486d-9b6c-0f7f5f3e7550	4b3e3482-3068-4b2b-8922-cb6e7ae6c64c
25353214-53fd-486d-9b6c-0f7f5f3e7550	ea7ad6ca-42f7-4bf7-91a8-86a184416980
5d729b02-4d8c-4abd-80cc-c7497d1945cf	7db50d24-7de5-46e8-87ae-aa9cfa08b144
722b151c-f9d7-4222-b541-cfc554695510	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103
335938f6-877b-4754-b20e-ea5dc2d4f1b4	6f4c9bdb-2cb2-49ee-aa29-0543edbc5103
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: sokol; Owner: -
--

COPY users (id, title, login, password, firstname, middlename, lastname, groups) FROM stdin;
580f62b3-7b96-4109-a321-dc7d24109a1a	Поляков И. В.	\N	\N	Иван	Вячеславович	Поляков	\N
dc175f6e-b18d-495f-aca9-58c956e48a42	Беломестов Г. В.	\N	\N	Густав	Валерьевич	Беломестов	\N
e879c49c-4fdf-43a1-8507-7091f2dea03d	Волков Б. П.	\N	\N	Бронислав	Петрович	Волков	\N
3379db0f-8221-43fd-8d46-e05edcef9686	Ивашов Н. В.	\N	\N	Никита	Валерьевич	Ивашов	\N
a1109397-b265-4cf4-8d6e-986c0fa0ce54	Былинкин Б. Н.	\N	\N	Борис	Николаевич	Былинкин	\N
a2cfff23-4070-4063-9e94-c3956e824122	Захаров Н. В.	\N	\N	Никита	Вячеславович	Захаров	\N
fb2f4ebc-34c6-4fb5-b73a-7ed1b9249a68	Агапов Г. В.	\N	\N	Густав	Валерьевич	Агапов	\N
0cde912f-55e6-48c9-97f8-464fbe74cbb5	Добрынин Л. В.	\N	\N	Леонид	Валерьевич	Добрынин	\N
b1fea135-9e3e-4e41-ad6d-492841868fd5	Агапов Н. В.	\N	\N	Никита	Вячеславович	Агапов	\N
06fad082-6745-4265-bec1-fd1aa741844b	Полякова В. Е.	\N	\N	Валентина	Егоровна	Полякова	\N
335938f6-877b-4754-b20e-ea5dc2d4f1b4	Виноградова А. А.	\N	\N	Анна	Аркадьевна	Виноградова	\N
4f4181fc-6953-4fd0-8684-37e435e3cce1	Волкова А. Г.	\N	\N	Антонина	Георгиевна	Волкова	\N
18af0599-b299-415f-8642-f1b1d1eef37f	Гарина К. М.	\N	\N	Катерина	Михайловна	Гарина	\N
b2d8bff1-f48f-482d-bf2d-9606fe0e1bae	Беломестова В. М.	\N	\N	Валентина	Михайловна	Беломестова	\N
c7e81475-f453-49bc-bdfd-5f8614f063fc	Алексеева А. Е.	\N	\N	Антонина	Егоровна	Алексеева	\N
551697fd-8dac-4132-a557-e74e43c44390	Полякова К. А.	\N	\N	Катерина	Аркадьевна	Полякова	\N
995b431c-c776-4084-81fc-1ef9d8acd087	Зверева А. Г.	\N	\N	Анна	Георгиевна	Зверева	\N
5809dcc7-2903-4dfa-bea9-eb3d30035d51	Балашова Д. М.	\N	\N	Дарья	Михайловна	Балашова	\N
f179d503-c3fe-4b31-b596-fb36aa58f364	Гарина В. М.	\N	\N	Валентина	Михайловна	Гарина	\N
5be7fed3-fde0-4aa9-8a3a-498d5026a3a2	Беломестов И. М.	\N	\N	Иван	Максимович	Беломестов	\N
5ca6d548-afa3-4c26-a72e-f0f19100e701	Луков Б. П.	\N	\N	Бронислав	Петрович	Луков	\N
a36d1e1d-a00b-41f7-b0a4-12c63646d384	Карандашов Б. С.	\N	\N	Бронислав	Сергеевич	Карандашов	\N
00fb33e6-061c-46d5-a5c9-22053eef30c4	Грибов Б. В.	\N	\N	Борис	Валерьевич	Грибов	\N
1b025ff2-948d-4f7f-8261-6456f8f8c19e	Зверев Б. А.	\N	\N	Бронислав	Алексеевич	Зверев	\N
c58f8de5-4431-4c1b-a62e-55c804ba914f	Грибов К. С.	\N	\N	Карл	Сергеевич	Грибов	\N
fc947860-1a91-4a23-a275-82dec5bb5c96	Болотников К. Н.	\N	\N	Карл	Николаевич	Болотников	\N
d259e840-0b34-4512-bc2e-5b5498dc4171	Карандашов К. Н.	\N	\N	Карл	Николаевич	Карандашов	\N
7fa705ee-ac8e-40df-8c83-4024aedc0421	Виноградов К. В.	\N	\N	Карл	Валерьевич	Виноградов	\N
721f2b0b-91d1-4703-bff0-05a130a3b114	Зверева А. А.	\N	\N	Антонина	Аркадьевна	Зверева	\N
9bb42bab-8965-49d2-b134-cec0d1505cc3	Ивашова А. Е.	\N	\N	Анна	Егоровна	Ивашова	\N
c90b9c9f-ca1a-4b7c-bc77-3557c908f8d7	Енотина А. В.	\N	\N	Анна	Вячеславовна	Енотина	\N
39d3f6ae-ca8b-4814-a25e-57b6f253bde3	Карандашова А. А.	\N	\N	Антонина	Аркадьевна	Карандашова	\N
c5e87618-76c8-491f-bb25-aee010be5ab7	Грибова В. М.	\N	\N	Валентина	Михайловна	Грибова	\N
c0d2e91b-81f6-4560-b2c3-11654c6835ef	Гарина А. А.	\N	\N	Анна	Аркадьевна	Гарина	\N
cc1b2ba7-c245-4680-bc15-97b04be8b50e	Грибова А. С.	\N	\N	Антонина	Сергеевна	Грибова	\N
697660a9-8bed-4548-a15e-757282776ebb	Карандашова А. В.	\N	\N	Анна	Вячеславовна	Карандашова	\N
a4cff11c-936a-45e1-889f-f478f27fcc20	Зверева А. М.	\N	\N	Антонина	Михайловна	Зверева	\N
2df09f5d-7d58-42a7-81b7-e0a4dfb44bc6	222222		\N	222	222	222	{6283b250-dab4-4229-9f6f-d11f488d375e}
5d729b02-4d8c-4abd-80cc-c7497d1945cf	Луков В. А.	user	f3762f2447af0c993931a85a4ef52ca9	Виктор	Алексеевич	Луков	\N
a4fa069b-64ac-4a7e-ba5f-a3dc3e84c66e	Карандашова Д. Г.	\N	\N	Дарья	Георгиевна	Карандашова	\N
be3b5036-41df-48df-98b8-357fcb4f4720	3333333	\N	\N	333	333	333	\N
17cbb3ee-5547-47a5-a9cd-d286ad1df39b	1111111	111	bbcbc7ccb072d1310c51ae11afe3b8ad	111	111	111	{123,456}
722b151c-f9d7-4222-b541-cfc554695510	Ивашов В. Н.	test	749bc613e059779cc5e4c22e8fa7bba9	Виктор	Николаевич	Ивашов112	{a838c335-0fdb-4b3e-9e30-da7fb51e676b,b0243b1d-5268-4b34-9bae-850c3bf11da4}
5b4589fc-2061-431f-a765-7bd3907388f6	Test User Title	login1	\N	First Name22	Middle Name	Last Name	\N
a508313f-3fea-4078-8665-6538c97f48f6	Test User Title 55677	login1	\N	First Name22	Middle Name	Last Name	\N
25353214-53fd-486d-9b6c-0f7f5f3e7550	Test User Title 55688	login1	\N	First Name22	Middle Name	Last Name	\N
\.


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
-- Name: users_pkey; Type: CONSTRAINT; Schema: sokol; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

