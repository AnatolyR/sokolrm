SET search_path TO sokol;
DELETE FROM configs;
\set content `cat config/accessRightsElements.json`
INSERT INTO configs(id, path, content) VALUES('ac22de8b-8815-4d80-a81e-517378b39cf9', '/accessRightsElements.json', :'content');
\set content `cat config/appSettings.json`
INSERT INTO configs(id, path, content) VALUES('a9290046-8998-4540-a9ba-61397d5319d5', '/appSettings.json', :'content');
\set content `cat config/dictionaries/contragents.json`
INSERT INTO configs(id, path, content) VALUES('f1f53036-2525-415f-bb7a-7870b94c671a', '/dictionaries/contragents.json', :'content');
\set content `cat config/dictionaries/documentKind.json`
INSERT INTO configs(id, path, content) VALUES('34f79d92-4993-4bba-bc90-38847c621d6e', '/dictionaries/documentKind.json', :'content');
\set content `cat config/dictionaries/documentLinkTypes.json`
INSERT INTO configs(id, path, content) VALUES('2ca7688b-9c52-4ec1-98d5-459ced1cfba6', '/dictionaries/documentLinkTypes.json', :'content');
\set content `cat config/dictionaries/documentTitles.json`
INSERT INTO configs(id, path, content) VALUES('28563bdb-c844-4fd7-97d0-c53da0280a36', '/dictionaries/documentTitles.json', :'content');
\set content `cat config/dictionaries/executionReportStatus.json`
INSERT INTO configs(id, path, content) VALUES('27e036f0-1f11-4a0e-8d2c-e7a3ab4ff3dd', '/dictionaries/executionReportStatus.json', :'content');
\set content `cat config/dictionaries/groups.json`
INSERT INTO configs(id, path, content) VALUES('1c82d0a2-46cf-468b-996e-b64c9caf2e4e', '/dictionaries/groups.json', :'content');
\set content `cat config/dictionaries/organizationPersons.json`
INSERT INTO configs(id, path, content) VALUES('403ec54d-b11f-420b-bb2c-4b321429151a', '/dictionaries/organizationPersons.json', :'content');
\set content `cat config/dictionaries/registrationLists.json`
INSERT INTO configs(id, path, content) VALUES('f199e420-9890-4f1e-80d6-3cd2e733e9bb', '/dictionaries/registrationLists.json', :'content');
\set content `cat config/dictionaries/users.json`
INSERT INTO configs(id, path, content) VALUES('8cec1c17-958d-4bcb-86da-d8173a2c697d', '/dictionaries/users.json', :'content');
\set content `cat config/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('ff3da7de-f024-4869-841a-4937281afc98', '/dictionaries.json', :'content');
\set content `cat config/documentTypes.json`
INSERT INTO configs(id, path, content) VALUES('c8900c22-b5c5-415c-ab93-07fb4c024335', '/documentTypes.json', :'content');
\set content `cat config/flows/incomingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('f49ad0be-9f6a-4e01-aead-af547ce1d9c2', '/flows/incomingDocumentFlow.json', :'content');
\set content `cat config/flows/outgoingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('27361160-c036-4f64-ba70-70116492fc76', '/flows/outgoingDocumentFlow.json', :'content');
\set content `cat config/forms/contragentForm.json`
INSERT INTO configs(id, path, content) VALUES('ce63864e-0eb6-454d-818e-ea25895e92da', '/forms/contragentForm.json', :'content');
\set content `cat config/forms/groupForm.json`
INSERT INTO configs(id, path, content) VALUES('0755dd59-85d1-47c5-9ebb-400564326c10', '/forms/groupForm.json', :'content');
\set content `cat config/forms/incomingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('957d34c2-e8fb-40f6-9cd6-592fcae40d31', '/forms/incomingDocumentForm.json', :'content');
\set content `cat config/forms/internalDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('91dbdb1a-4b1b-4a89-be3b-fd13cf1c2c14', '/forms/internalDocumentForm.json', :'content');
\set content `cat config/forms/outgoingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('4e60a6b0-4be6-4fc4-8a01-dcd8a488e68c', '/forms/outgoingDocumentForm.json', :'content');
\set content `cat config/forms/registrationListForm.json`
INSERT INTO configs(id, path, content) VALUES('807cd3b2-57c1-4c77-94fe-40ec559d3c69', '/forms/registrationListForm.json', :'content');
\set content `cat config/forms/taskDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('a7be92d1-2f89-4e75-8cd8-672e5d462e1f', '/forms/taskDocumentForm.json', :'content');
\set content `cat config/forms/userForm.json`
INSERT INTO configs(id, path, content) VALUES('8452b90a-217d-48f1-8a23-4e06339aac9e', '/forms/userForm.json', :'content');
\set content `cat config/forms/userSystemForm.json`
INSERT INTO configs(id, path, content) VALUES('088b3925-b345-46a4-a2ac-242e99fed852', '/forms/userSystemForm.json', :'content');
\set content `cat config/lists/archivedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('2c5b076f-bbc8-41db-b8e2-133bb5b49c7a', '/lists/archivedDocumentsList.json', :'content');
\set content `cat config/lists/archivedIncomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('a189c510-46ef-44cb-a5d6-0f62a6bd7548', '/lists/archivedIncomingTasksList.json', :'content');
\set content `cat config/lists/archivedOutgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('56955050-c717-47aa-938f-1197e66eba74', '/lists/archivedOutgoingTasksList.json', :'content');
\set content `cat config/lists/documentsList.json`
INSERT INTO configs(id, path, content) VALUES('3a2d35b0-2ff5-402e-9439-9d9c0176bf66', '/lists/documentsList.json', :'content');
\set content `cat config/lists/documentTemplatesList.json`
INSERT INTO configs(id, path, content) VALUES('d66d8efa-d627-4b5a-bd96-0c7fe0fbfb59', '/lists/documentTemplatesList.json', :'content');
\set content `cat config/lists/favoriteDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('a5a46812-5448-4ec9-8e4a-2d9c122f9c90', '/lists/favoriteDocumentsList.json', :'content');
\set content `cat config/lists/incomingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('d53b997b-8873-4bb5-9850-7f9d8e795e8a', '/lists/incomingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/incomingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('2bf640b1-555d-4634-b6a9-f06fdb363e8a', '/lists/incomingApprovalTasksList.json', :'content');
\set content `cat config/lists/incomingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('6f7ef8d9-a492-4188-b18d-bb51c8c3622b', '/lists/incomingDocumentsList.json', :'content');
\set content `cat config/lists/incomingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('df2b15fc-cd74-4c6d-8ef2-630db355fea2', '/lists/incomingExecutionTasksList.json', :'content');
\set content `cat config/lists/incomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('232ae49f-b926-40c9-b3a5-00cb422553e1', '/lists/incomingTasksList.json', :'content');
\set content `cat config/lists/internalDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('3d26bd94-77dd-4014-a803-dc61603824c6', '/lists/internalDocumentsList.json', :'content');
\set content `cat config/lists/linkedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('ea405dd1-fe05-4729-aab2-17d31e7d86ad', '/lists/linkedDocumentsList.json', :'content');
\set content `cat config/lists/myDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('2fbc02dd-170b-4fb3-8742-06be4e8e25df', '/lists/myDocumentsList.json', :'content');
\set content `cat config/lists/outgoingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('76bd1ffe-1c13-4e43-9626-2c8f75feb8a3', '/lists/outgoingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/outgoingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('18ce5d48-2eb3-48b9-a176-0ca7845a4488', '/lists/outgoingApprovalTasksList.json', :'content');
\set content `cat config/lists/outgoingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('6c4db050-7974-4dd7-b938-2f1edd2c62b1', '/lists/outgoingDocumentsList.json', :'content');
\set content `cat config/lists/outgoingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('03968eb0-4eee-4197-a0b1-fe7ddaba1f2a', '/lists/outgoingExecutionTasksList.json', :'content');
\set content `cat config/lists/outgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('f19c63c0-0bf3-488e-87db-6a03e7854e09', '/lists/outgoingTasksList.json', :'content');
\set content `cat config/lists/reviewDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('08a0cea9-7d46-45b0-aeb3-674734fa8fb2', '/lists/reviewDocumentsList.json', :'content');
\set content `cat config/lists/searchContragentsList.json`
INSERT INTO configs(id, path, content) VALUES('d89e7641-2da5-4ad8-9f72-ce1244ed0f17', '/lists/searchContragentsList.json', :'content');
\set content `cat config/lists/searchDictionariesList.json`
INSERT INTO configs(id, path, content) VALUES('5399b072-e769-4edb-846c-1d603a44041b', '/lists/searchDictionariesList.json', :'content');
\set content `cat config/lists/searchDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('42a2d67e-ff3c-4269-8f46-01a8edc04444', '/lists/searchDocumentsList.json', :'content');
\set content `cat config/lists/searchFilesList.json`
INSERT INTO configs(id, path, content) VALUES('fe2c7f4a-ea58-4733-a8f0-66bfc60b44ee', '/lists/searchFilesList.json', :'content');
\set content `cat config/lists/searchTasksList.json`
INSERT INTO configs(id, path, content) VALUES('2216743f-139b-409b-a38a-9a37e90b2e9f', '/lists/searchTasksList.json', :'content');
\set content `cat config/lists/searchUsersList.json`
INSERT INTO configs(id, path, content) VALUES('4cf2e2ce-1d8a-4033-9d86-3130f4f6e765', '/lists/searchUsersList.json', :'content');
\set content `cat config/lists/signDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('6acc41e7-5443-44e1-83a0-a6ff8ab9bb37', '/lists/signDocumentsList.json', :'content');
\set content `cat config/lists/spaces.json`
INSERT INTO configs(id, path, content) VALUES('6b4be637-21e8-4ed3-b226-c73a09c0b542', '/lists/spaces.json', :'content');
\set content `cat config/navigation/admin.json`
INSERT INTO configs(id, path, content) VALUES('218669d6-df91-4f89-be90-d06e22d9e092', '/navigation/admin.json', :'content');
\set content `cat config/navigation/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('2b7f65b6-e838-4819-ab04-40c1a9f7e2fe', '/navigation/dictionaries.json', :'content');
\set content `cat config/navigation/main.json`
INSERT INTO configs(id, path, content) VALUES('a412ff61-4a4f-4cc6-938e-2032b2aff8cc', '/navigation/main.json', :'content');
\set content `cat config/navigation/reports.json`
INSERT INTO configs(id, path, content) VALUES('1537c6ea-03cc-4d4c-b7fd-a47b01fc38e1', '/navigation/reports.json', :'content');
\set content `cat config/navigation/search.json`
INSERT INTO configs(id, path, content) VALUES('b31d3a56-d2f8-4b7c-af8a-aa0e92202809', '/navigation/search.json', :'content');
\set content `cat config/reports/allDocuments.json`
INSERT INTO configs(id, path, content) VALUES('24267d2e-c23f-45f3-8dfc-5d18eec249d5', '/reports/allDocuments.json', :'content');
\set content `cat config/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json`
INSERT INTO configs(id, path, content) VALUES('37541796-d9a5-4c6f-9fa7-3122a409ebbe', '/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json', :'content');
\set content `cat config/types/incomingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('c5517893-2404-4cf5-b026-2ae4ae4ec1f4', '/types/incomingDocumentType.json', :'content');
\set content `cat config/types/internalDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('94117b27-7fa1-430f-bc4b-579a0efe98e1', '/types/internalDocumentType.json', :'content');
\set content `cat config/types/outgoingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('97115788-cb76-4218-9706-c2f905c19de1', '/types/outgoingDocumentType.json', :'content');
\set content `cat config/types/taskDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('c1a85990-7b61-481f-9903-8e7ffc61f628', '/types/taskDocumentType.json', :'content');
