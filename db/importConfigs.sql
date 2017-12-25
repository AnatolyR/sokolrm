SET search_path TO sokol;
DELETE FROM configs;
\set content `cat config/accessRightsElements.json`
INSERT INTO configs(id, path, content) VALUES('4e5069c1-0064-4821-b251-f8691da677c6', '/accessRightsElements.json', :'content');
\set content `cat config/appSettings.json`
INSERT INTO configs(id, path, content) VALUES('b05943f1-b4bf-4a87-acda-49759dc31245', '/appSettings.json', :'content');
\set content `cat config/dictionaries/contragents.json`
INSERT INTO configs(id, path, content) VALUES('9fa89cac-1e51-47d7-b7bb-e676578baaf9', '/dictionaries/contragents.json', :'content');
\set content `cat config/dictionaries/documentKind.json`
INSERT INTO configs(id, path, content) VALUES('dcc34482-8a62-4f1a-9247-e42c7361195d', '/dictionaries/documentKind.json', :'content');
\set content `cat config/dictionaries/documentLinkTypes.json`
INSERT INTO configs(id, path, content) VALUES('24d1f113-f8c4-49fa-ac2d-4a9875099d81', '/dictionaries/documentLinkTypes.json', :'content');
\set content `cat config/dictionaries/documentTitles.json`
INSERT INTO configs(id, path, content) VALUES('944a2db0-5e10-4c38-ad8a-200cc4a400f5', '/dictionaries/documentTitles.json', :'content');
\set content `cat config/dictionaries/executionReportStatus.json`
INSERT INTO configs(id, path, content) VALUES('a118daab-8ccb-4ac6-8784-7fbd78f46d09', '/dictionaries/executionReportStatus.json', :'content');
\set content `cat config/dictionaries/groups.json`
INSERT INTO configs(id, path, content) VALUES('6a26179b-1938-4ea4-aaa1-01eebcf8b60d', '/dictionaries/groups.json', :'content');
\set content `cat config/dictionaries/organizationPersons.json`
INSERT INTO configs(id, path, content) VALUES('811f3bef-aa86-4d13-9d16-597a7e83377a', '/dictionaries/organizationPersons.json', :'content');
\set content `cat config/dictionaries/registrationLists.json`
INSERT INTO configs(id, path, content) VALUES('c8ff8791-567d-4e64-9c0a-d3ab7aa49632', '/dictionaries/registrationLists.json', :'content');
\set content `cat config/dictionaries/users.json`
INSERT INTO configs(id, path, content) VALUES('b0ff1edb-0d9c-4603-a74b-ed6404795357', '/dictionaries/users.json', :'content');
\set content `cat config/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('e142cdfc-a61a-4f1f-997f-b16946ecaa40', '/dictionaries.json', :'content');
\set content `cat config/documentTypes.json`
INSERT INTO configs(id, path, content) VALUES('9adcd1de-1513-4f4b-904d-75a8fda5ed5d', '/documentTypes.json', :'content');
\set content `cat config/flows/incomingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('df677168-322f-4bef-83a8-eb5eebeba094', '/flows/incomingDocumentFlow.json', :'content');
\set content `cat config/flows/outgoingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('eb81c6b1-b905-4fa6-a328-966d55585c82', '/flows/outgoingDocumentFlow.json', :'content');
\set content `cat config/forms/configFile.json`
INSERT INTO configs(id, path, content) VALUES('d30515c4-e808-4219-8750-af57db6010d9', '/forms/configFile.json', :'content');
\set content `cat config/forms/contragentForm.json`
INSERT INTO configs(id, path, content) VALUES('b707409f-ccb9-4241-a69b-875cdc639036', '/forms/contragentForm.json', :'content');
\set content `cat config/forms/groupForm.json`
INSERT INTO configs(id, path, content) VALUES('5e69e6fa-30f6-465c-ab62-9ba6adb9dd0c', '/forms/groupForm.json', :'content');
\set content `cat config/forms/incomingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('c857ae0f-1da0-475b-bf10-7f7f30085e71', '/forms/incomingDocumentForm.json', :'content');
\set content `cat config/forms/internalDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('22f70654-2368-4de2-beee-31fdc60a35d5', '/forms/internalDocumentForm.json', :'content');
\set content `cat config/forms/outgoingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('c0aeddba-970c-41fd-b6c4-7385130e29c4', '/forms/outgoingDocumentForm.json', :'content');
\set content `cat config/forms/registrationListForm.json`
INSERT INTO configs(id, path, content) VALUES('b7002155-c354-46dd-8c2c-01e56ba05a34', '/forms/registrationListForm.json', :'content');
\set content `cat config/forms/taskDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('ae8f5e21-54f1-4324-b4b2-5398f79a2e5b', '/forms/taskDocumentForm.json', :'content');
\set content `cat config/forms/userForm.json`
INSERT INTO configs(id, path, content) VALUES('e73f9302-f372-44c5-b9ca-98c66d438758', '/forms/userForm.json', :'content');
\set content `cat config/forms/userSystemForm.json`
INSERT INTO configs(id, path, content) VALUES('c6bb9ca1-e4db-4e40-91aa-eff8e4bebbc0', '/forms/userSystemForm.json', :'content');
\set content `cat config/lists/archivedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('536cb31f-4d77-4631-8475-3eb9ebfba8bb', '/lists/archivedDocumentsList.json', :'content');
\set content `cat config/lists/archivedIncomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('5458f1e9-e793-44a7-8b3a-b386158f309e', '/lists/archivedIncomingTasksList.json', :'content');
\set content `cat config/lists/archivedOutgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('c4e9a75d-f57b-4f5b-a735-e7476e3c43b7', '/lists/archivedOutgoingTasksList.json', :'content');
\set content `cat config/lists/configFiles.json`
INSERT INTO configs(id, path, content) VALUES('aa3e75fc-d118-4906-b2a2-33c0fc5a2467', '/lists/configFiles.json', :'content');
\set content `cat config/lists/documentsList.json`
INSERT INTO configs(id, path, content) VALUES('2ead5845-1994-466d-9bf3-a6fa0c3fe8c8', '/lists/documentsList.json', :'content');
\set content `cat config/lists/documentTemplatesList.json`
INSERT INTO configs(id, path, content) VALUES('b98a49b6-a245-43ed-80c9-45561076b222', '/lists/documentTemplatesList.json', :'content');
\set content `cat config/lists/favoriteDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('6e3f4f9a-cb67-49f9-8987-861b3b3f543e', '/lists/favoriteDocumentsList.json', :'content');
\set content `cat config/lists/incomingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('a70c9a6b-c764-4f67-a8c7-ea25a655bdda', '/lists/incomingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/incomingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('12eb63cd-b081-46de-8fdc-439174288e3d', '/lists/incomingApprovalTasksList.json', :'content');
\set content `cat config/lists/incomingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('989fbedf-f007-4c3c-ad5e-22077add666a', '/lists/incomingDocumentsList.json', :'content');
\set content `cat config/lists/incomingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('5d8d0886-1ed3-4ddb-8ee4-80bc7350c66d', '/lists/incomingExecutionTasksList.json', :'content');
\set content `cat config/lists/incomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('c10fc136-c8fc-4f97-9921-063d36a6f4e7', '/lists/incomingTasksList.json', :'content');
\set content `cat config/lists/internalDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('7d3cabcd-101f-4145-b530-34266255ecad', '/lists/internalDocumentsList.json', :'content');
\set content `cat config/lists/linkedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('d6b64d34-357c-4287-a29e-d1237414c316', '/lists/linkedDocumentsList.json', :'content');
\set content `cat config/lists/myDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('829cbd35-9702-437c-a4b2-bbd491cd5613', '/lists/myDocumentsList.json', :'content');
\set content `cat config/lists/outgoingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('262deeb0-d585-46f0-8a43-8f55ddc99247', '/lists/outgoingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/outgoingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('6ec1d8c4-1dc8-4ab2-aff3-d270251e638c', '/lists/outgoingApprovalTasksList.json', :'content');
\set content `cat config/lists/outgoingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('1f424f02-8572-49b9-8b04-f72c45a09a8c', '/lists/outgoingDocumentsList.json', :'content');
\set content `cat config/lists/outgoingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('ccc8c78d-dc6f-492d-8566-c2a263e8c47d', '/lists/outgoingExecutionTasksList.json', :'content');
\set content `cat config/lists/outgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('469d0e42-bbfc-4051-8437-bd090c71d135', '/lists/outgoingTasksList.json', :'content');
\set content `cat config/lists/reviewDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('3283580c-6eba-45aa-8c44-22c8c695dcaa', '/lists/reviewDocumentsList.json', :'content');
\set content `cat config/lists/searchContragentsList.json`
INSERT INTO configs(id, path, content) VALUES('ca79ff35-35f7-4bdb-af04-668f59254d8b', '/lists/searchContragentsList.json', :'content');
\set content `cat config/lists/searchDictionariesList.json`
INSERT INTO configs(id, path, content) VALUES('f339dbe6-bf3d-4dfe-bc1a-da1adb41e930', '/lists/searchDictionariesList.json', :'content');
\set content `cat config/lists/searchDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('f315e988-a0f2-448c-b05e-63516ff1f2a0', '/lists/searchDocumentsList.json', :'content');
\set content `cat config/lists/searchFilesList.json`
INSERT INTO configs(id, path, content) VALUES('e9740209-442e-47c1-bfdc-52eb205b073e', '/lists/searchFilesList.json', :'content');
\set content `cat config/lists/searchTasksList.json`
INSERT INTO configs(id, path, content) VALUES('2f4efdff-4f81-4400-8ff1-a13754ae5fb0', '/lists/searchTasksList.json', :'content');
\set content `cat config/lists/searchUsersList.json`
INSERT INTO configs(id, path, content) VALUES('a8febdc8-3ad4-4fb7-8dcf-b894eb18a6a7', '/lists/searchUsersList.json', :'content');
\set content `cat config/lists/signDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('28bea730-0383-48f0-89eb-4180bd6a3290', '/lists/signDocumentsList.json', :'content');
\set content `cat config/lists/spaces.json`
INSERT INTO configs(id, path, content) VALUES('d3e10fa8-34b2-4d99-8873-d2e498ae0e2d', '/lists/spaces.json', :'content');
\set content `cat config/navigation/admin.json`
INSERT INTO configs(id, path, content) VALUES('e87accd9-fa3b-4564-8c39-d278ac502715', '/navigation/admin.json', :'content');
\set content `cat config/navigation/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('2bb33e5b-2bcc-4a07-83b9-8d4a352849ef', '/navigation/dictionaries.json', :'content');
\set content `cat config/navigation/main.json`
INSERT INTO configs(id, path, content) VALUES('388f6e13-4308-4637-9b53-5d0b8eb8ed39', '/navigation/main.json', :'content');
\set content `cat config/navigation/reports.json`
INSERT INTO configs(id, path, content) VALUES('06059ca3-8f6f-466f-bafe-ef6e27df3d35', '/navigation/reports.json', :'content');
\set content `cat config/navigation/search.json`
INSERT INTO configs(id, path, content) VALUES('6eba57c4-eaa1-407a-a336-f4ebcdae6339', '/navigation/search.json', :'content');
\set content `cat config/reports/allDocuments.json`
INSERT INTO configs(id, path, content) VALUES('923f65c1-7df1-49bf-8298-8018fe6e9686', '/reports/allDocuments.json', :'content');
\set content `cat config/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json`
INSERT INTO configs(id, path, content) VALUES('431126be-c800-4f33-baf5-e2af9c386d51', '/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json', :'content');
\set content `cat config/types/incomingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('88655abd-32f5-4961-af05-a171e4c2de7d', '/types/incomingDocumentType.json', :'content');
\set content `cat config/types/internalDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('80585263-0c93-4a67-8b56-fd49d9cf516c', '/types/internalDocumentType.json', :'content');
\set content `cat config/types/outgoingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('44bd04a4-7da3-4055-a3d0-335549dd5e0c', '/types/outgoingDocumentType.json', :'content');
\set content `cat config/types/taskDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('f551ff59-750a-459d-9a44-31e4b08b1d1f', '/types/taskDocumentType.json', :'content');
