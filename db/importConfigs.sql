SET search_path TO sokol;
DELETE FROM configs;
\set content `cat config/accessRightsElements.json`
INSERT INTO configs(id, path, content) VALUES('b9af20d4-d3be-42a2-b7a0-382c21c7f435', '/accessRightsElements.json', :'content');
\set content `cat config/appSettings.json`
INSERT INTO configs(id, path, content) VALUES('40a0bb9d-ef51-40f1-a035-e802cab3a694', '/appSettings.json', :'content');
\set content `cat config/dictionaries/contragents.json`
INSERT INTO configs(id, path, content) VALUES('05999e9b-fa71-4a8d-b1ea-8c92d9a1e065', '/dictionaries/contragents.json', :'content');
\set content `cat config/dictionaries/documentKind.json`
INSERT INTO configs(id, path, content) VALUES('c32cb2ca-ed30-4040-a599-13827e3ad7c1', '/dictionaries/documentKind.json', :'content');
\set content `cat config/dictionaries/documentLinkTypes.json`
INSERT INTO configs(id, path, content) VALUES('e5b39669-acfe-4ae9-9f09-14e876e0bae5', '/dictionaries/documentLinkTypes.json', :'content');
\set content `cat config/dictionaries/documentTitles.json`
INSERT INTO configs(id, path, content) VALUES('8e13e9d0-594f-439a-98e7-b7d22ee4d904', '/dictionaries/documentTitles.json', :'content');
\set content `cat config/dictionaries/executionReportStatus.json`
INSERT INTO configs(id, path, content) VALUES('eb147b6c-ccab-4f6d-9fac-fd817a7b0e7c', '/dictionaries/executionReportStatus.json', :'content');
\set content `cat config/dictionaries/groups.json`
INSERT INTO configs(id, path, content) VALUES('2e3c1f5c-19f9-428d-8d0f-dde6c24f9a39', '/dictionaries/groups.json', :'content');
\set content `cat config/dictionaries/organizationPersons.json`
INSERT INTO configs(id, path, content) VALUES('13e455d9-7326-4a1f-b524-62a5b88175ff', '/dictionaries/organizationPersons.json', :'content');
\set content `cat config/dictionaries/registrationLists.json`
INSERT INTO configs(id, path, content) VALUES('2d373733-35b6-4e67-9c97-5c022acf0844', '/dictionaries/registrationLists.json', :'content');
\set content `cat config/dictionaries/users.json`
INSERT INTO configs(id, path, content) VALUES('c25072db-b37a-47b5-b2a4-8871fad62ca8', '/dictionaries/users.json', :'content');
\set content `cat config/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('6cc6b927-a267-42b2-a1d0-3e479aa12fa9', '/dictionaries.json', :'content');
\set content `cat config/documentTypes.json`
INSERT INTO configs(id, path, content) VALUES('a7127e2c-1f12-4154-a81a-8b534205494a', '/documentTypes.json', :'content');
\set content `cat config/flows/incomingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('0cf55d9d-9805-4549-a38e-b29fe62bb537', '/flows/incomingDocumentFlow.json', :'content');
\set content `cat config/flows/outgoingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('c40f7d00-a82f-4ebd-95c5-2f019aaf921e', '/flows/outgoingDocumentFlow.json', :'content');
\set content `cat config/forms/configFile.json`
INSERT INTO configs(id, path, content) VALUES('f2109e67-f55a-4757-8c73-26709d9db8b4', '/forms/configFile.json', :'content');
\set content `cat config/forms/contragentForm.json`
INSERT INTO configs(id, path, content) VALUES('6b5dc2bd-190c-4b47-8d7a-ccd736f18297', '/forms/contragentForm.json', :'content');
\set content `cat config/forms/groupForm.json`
INSERT INTO configs(id, path, content) VALUES('b476576c-c14b-469d-86b0-90afaff6e70a', '/forms/groupForm.json', :'content');
\set content `cat config/forms/incomingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('d8648192-7a84-45da-a1b2-d505563df8f0', '/forms/incomingDocumentForm.json', :'content');
\set content `cat config/forms/internalDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('b57eb4a5-3c2a-4436-8158-769bb7797a96', '/forms/internalDocumentForm.json', :'content');
\set content `cat config/forms/outgoingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('320ff707-b984-409e-bfef-c9d657a14249', '/forms/outgoingDocumentForm.json', :'content');
\set content `cat config/forms/registrationListForm.json`
INSERT INTO configs(id, path, content) VALUES('02ece5f6-b608-491c-b9cd-4f43e030b3b7', '/forms/registrationListForm.json', :'content');
\set content `cat config/forms/taskDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('eae02e86-6e06-4b06-a897-fe5779ee0ac8', '/forms/taskDocumentForm.json', :'content');
\set content `cat config/forms/userForm.json`
INSERT INTO configs(id, path, content) VALUES('c12d6e9e-40c0-43c8-bf70-3068b52a349d', '/forms/userForm.json', :'content');
\set content `cat config/forms/userSystemForm.json`
INSERT INTO configs(id, path, content) VALUES('507fc97d-4565-43d9-bc87-6004fc2c829d', '/forms/userSystemForm.json', :'content');
\set content `cat config/lists/archivedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('33b12d0f-e095-4c64-bf7e-5642596bea90', '/lists/archivedDocumentsList.json', :'content');
\set content `cat config/lists/archivedIncomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('76a53716-216e-4a32-b45f-523c1560fa42', '/lists/archivedIncomingTasksList.json', :'content');
\set content `cat config/lists/archivedOutgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('9c854b5f-59f8-4c73-ac34-4ac90349dacd', '/lists/archivedOutgoingTasksList.json', :'content');
\set content `cat config/lists/configFiles.json`
INSERT INTO configs(id, path, content) VALUES('441710bb-424e-4ca0-8c6b-b75132a410a7', '/lists/configFiles.json', :'content');
\set content `cat config/lists/documentsList.json`
INSERT INTO configs(id, path, content) VALUES('efab67bf-79d4-4a19-9e56-8e898f59a510', '/lists/documentsList.json', :'content');
\set content `cat config/lists/documentTemplatesList.json`
INSERT INTO configs(id, path, content) VALUES('d8a38b31-b345-4d82-8384-c5da2ba84b27', '/lists/documentTemplatesList.json', :'content');
\set content `cat config/lists/favoriteDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('0ccdc9cc-c0fa-48f3-9184-018494b27752', '/lists/favoriteDocumentsList.json', :'content');
\set content `cat config/lists/incomingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('78191ae6-0021-4427-94e0-61d75e2d6f3f', '/lists/incomingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/incomingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('2573b255-8c3f-4d08-8453-d8bd14bfe667', '/lists/incomingApprovalTasksList.json', :'content');
\set content `cat config/lists/incomingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('d6f42809-6fc9-4691-b5d9-5e16db7efe7e', '/lists/incomingDocumentsList.json', :'content');
\set content `cat config/lists/incomingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('4cf1daba-0f82-45f4-83aa-ba59775f8c30', '/lists/incomingExecutionTasksList.json', :'content');
\set content `cat config/lists/incomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('8d32d2cf-7f01-44b3-8b07-6530ec8eecf1', '/lists/incomingTasksList.json', :'content');
\set content `cat config/lists/internalDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('68a6f60f-b5d7-44cf-b5ca-bc4692f4a259', '/lists/internalDocumentsList.json', :'content');
\set content `cat config/lists/linkedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('f4bda7d2-d5ab-4e88-b71b-1cc657256ba7', '/lists/linkedDocumentsList.json', :'content');
\set content `cat config/lists/myDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('dae8ed0d-20e1-40a9-8f91-bd4c643e8e06', '/lists/myDocumentsList.json', :'content');
\set content `cat config/lists/outgoingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('70f37837-bf0b-4fc2-97f2-a4b3b6b27473', '/lists/outgoingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/outgoingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('68547a05-44cc-4b5a-92d6-7ef586a79a6a', '/lists/outgoingApprovalTasksList.json', :'content');
\set content `cat config/lists/outgoingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('9b41e052-921a-40a5-a44f-11e6281ec5a8', '/lists/outgoingDocumentsList.json', :'content');
\set content `cat config/lists/outgoingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('1fd2846c-df67-47e7-b748-450e1d955955', '/lists/outgoingExecutionTasksList.json', :'content');
\set content `cat config/lists/outgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('d51ce350-f9fe-4a14-8ed1-4b92b9b64755', '/lists/outgoingTasksList.json', :'content');
\set content `cat config/lists/reviewDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('54da42fe-0f02-4054-a8c3-a081647d994c', '/lists/reviewDocumentsList.json', :'content');
\set content `cat config/lists/searchContragentsList.json`
INSERT INTO configs(id, path, content) VALUES('1630fafc-4501-4bb6-a2da-b4398181ae51', '/lists/searchContragentsList.json', :'content');
\set content `cat config/lists/searchDictionariesList.json`
INSERT INTO configs(id, path, content) VALUES('8f2f6c7d-620f-494c-9f75-175be2fcdf16', '/lists/searchDictionariesList.json', :'content');
\set content `cat config/lists/searchDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('64497561-9ac7-4d36-bc7e-fc1561c2edba', '/lists/searchDocumentsList.json', :'content');
\set content `cat config/lists/searchFilesList.json`
INSERT INTO configs(id, path, content) VALUES('2e522b5e-1531-4db5-9935-5a6a32d9dfbb', '/lists/searchFilesList.json', :'content');
\set content `cat config/lists/searchTasksList.json`
INSERT INTO configs(id, path, content) VALUES('d9f2ef99-8b3b-4666-9880-6d9b5e1dcc60', '/lists/searchTasksList.json', :'content');
\set content `cat config/lists/searchUsersList.json`
INSERT INTO configs(id, path, content) VALUES('6fb03882-0e12-4876-9873-e08c07b1da84', '/lists/searchUsersList.json', :'content');
\set content `cat config/lists/signDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('8979826d-fe73-4e56-985d-6bd09ab40c07', '/lists/signDocumentsList.json', :'content');
\set content `cat config/lists/spaces.json`
INSERT INTO configs(id, path, content) VALUES('967dd42f-bc27-46cd-91dd-fc8aa5f375ce', '/lists/spaces.json', :'content');
\set content `cat config/navigation/admin.json`
INSERT INTO configs(id, path, content) VALUES('2055c12a-4458-4d9f-a015-5ea261282e62', '/navigation/admin.json', :'content');
\set content `cat config/navigation/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('71adf079-97b6-4f64-bc44-e9ac1c9890c0', '/navigation/dictionaries.json', :'content');
\set content `cat config/navigation/main.json`
INSERT INTO configs(id, path, content) VALUES('0509c71e-5de6-44e5-aa7e-32249dbf9432', '/navigation/main.json', :'content');
\set content `cat config/navigation/reports.json`
INSERT INTO configs(id, path, content) VALUES('58492072-ba05-47fe-b7e0-ad1db7f42021', '/navigation/reports.json', :'content');
\set content `cat config/navigation/search.json`
INSERT INTO configs(id, path, content) VALUES('7ee2128a-126c-4da4-b892-e1b26802d821', '/navigation/search.json', :'content');
\set content `cat config/reports/allDocuments.json`
INSERT INTO configs(id, path, content) VALUES('4f03e1c1-e644-40ba-8802-e44d4208f8a9', '/reports/allDocuments.json', :'content');
\set content `cat config/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json`
INSERT INTO configs(id, path, content) VALUES('071d3b01-18bf-4b81-91ad-bb9820188617', '/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json', :'content');
\set content `cat config/types/incomingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('66e16d0b-3e78-4a70-885c-86391e474a83', '/types/incomingDocumentType.json', :'content');
\set content `cat config/types/internalDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('7c649c82-5886-453a-9507-8c53d6aa78b7', '/types/internalDocumentType.json', :'content');
\set content `cat config/types/outgoingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('38bd6625-5014-4d44-a972-b8625b789cd8', '/types/outgoingDocumentType.json', :'content');
\set content `cat config/types/taskDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('2cb8bb4d-2aea-48ee-9842-01c23eb446dc', '/types/taskDocumentType.json', :'content');
