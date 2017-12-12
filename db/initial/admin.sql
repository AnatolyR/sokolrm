INSERT INTO accessrecords VALUES ('780922ec-7046-47f7-bfc7-2b8f00e6cbed', 'efe48b07-478a-4e24-a52a-8b82d821225d', '_system', 'groups', '', 'CREATE');
INSERT INTO accessrecords VALUES ('885229b6-b8dd-4253-84c9-ced889c3c963', 'efe48b07-478a-4e24-a52a-8b82d821225d', '_system', 'groups', '', 'READ');
INSERT INTO accessrecords VALUES ('234a3592-2477-4b90-932f-e78566604552', 'efe48b07-478a-4e24-a52a-8b82d821225d', '_system', 'groups', '', 'WRITE');
INSERT INTO accessrecords VALUES ('f75a0ecd-c3ec-4229-82e3-0900f0cc7425', 'efe48b07-478a-4e24-a52a-8b82d821225d', '_system', 'groups', '', 'LIST');


--
-- Data for Name: groups; Type: TABLE DATA; Schema: sokol; Owner: -
--

INSERT INTO groups VALUES ('efe48b07-478a-4e24-a52a-8b82d821225d', 'Administrators', NULL);


--
-- Data for Name: user_groups; Type: TABLE DATA; Schema: sokol; Owner: -
--

INSERT INTO user_groups VALUES ('dfe89495-cfa2-45e6-b62c-35bbe7ffc35e', 'efe48b07-478a-4e24-a52a-8b82d821225d');


--
-- Data for Name: users; Type: TABLE DATA; Schema: sokol; Owner: -
--

INSERT INTO users VALUES ('dfe89495-cfa2-45e6-b62c-35bbe7ffc35e', 'Admin', 'admin', 'cdfc14470b8d824a6d13356ecf79adec', 'Admin', 'Admin', 'Admin', NULL);


