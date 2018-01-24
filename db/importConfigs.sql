SET search_path TO sokol;
DELETE FROM configs;
\set content `cat config/accessRightsElements.json`
INSERT INTO configs(id, path, content) VALUES('e603db30-8083-4ab4-9cfb-68cfcd75d4a9', '/accessRightsElements.json', :'content');
\set content `cat config/appSettings.json`
INSERT INTO configs(id, path, content) VALUES('d901e4fa-7c68-4c84-b123-3cbcdaad6a10', '/appSettings.json', :'content');
\set content `cat config/appSettingsTest.json`
INSERT INTO configs(id, path, content) VALUES('2b4a8b48-b12e-4a52-bd57-6de60069769f', '/appSettingsTest.json', :'content');
\set content `cat config/dictionaries/contragents.json`
INSERT INTO configs(id, path, content) VALUES('9a687905-00a0-49a7-a620-4b34ccbe66ed', '/dictionaries/contragents.json', :'content');
\set content `cat config/dictionaries/documentKind.json`
INSERT INTO configs(id, path, content) VALUES('959be689-63a5-4a41-b67a-55e601c19af0', '/dictionaries/documentKind.json', :'content');
\set content `cat config/dictionaries/documentLinkTypes.json`
INSERT INTO configs(id, path, content) VALUES('dd397866-d603-409e-a095-69757149b15d', '/dictionaries/documentLinkTypes.json', :'content');
\set content `cat config/dictionaries/documentTitles.json`
INSERT INTO configs(id, path, content) VALUES('f117f4d8-aff8-452b-9074-daf84084a8b9', '/dictionaries/documentTitles.json', :'content');
\set content `cat config/dictionaries/executionReportStatus.json`
INSERT INTO configs(id, path, content) VALUES('db9c5ee0-5455-4aa6-ab9d-c85f140d799b', '/dictionaries/executionReportStatus.json', :'content');
\set content `cat config/dictionaries/groups.json`
INSERT INTO configs(id, path, content) VALUES('fd955ab9-4377-4182-a1f0-b625fc7498cd', '/dictionaries/groups.json', :'content');
\set content `cat config/dictionaries/organizationPersons.json`
INSERT INTO configs(id, path, content) VALUES('45e78a3b-a8c4-49ef-b15f-5349ff3338ac', '/dictionaries/organizationPersons.json', :'content');
\set content `cat config/dictionaries/registrationLists.json`
INSERT INTO configs(id, path, content) VALUES('8ba4c49d-e716-497f-84d9-d61c3f88c15f', '/dictionaries/registrationLists.json', :'content');
\set content `cat config/dictionaries/users.json`
INSERT INTO configs(id, path, content) VALUES('ee9811e8-7c3c-46bd-a521-c58722ef70be', '/dictionaries/users.json', :'content');
\set content `cat config/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('1e9f14ff-e64e-4891-beaf-a91655ab33c8', '/dictionaries.json', :'content');
\set content `cat config/documentTypes.json`
INSERT INTO configs(id, path, content) VALUES('0abf2c13-55c4-4ce5-9804-36bbbcf4b3af', '/documentTypes.json', :'content');
\set content `cat config/flows/incomingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('89e8d60f-5b72-4b59-b3f1-9a7c58573616', '/flows/incomingDocumentFlow.json', :'content');
\set content `cat config/flows/outgoingDocumentFlow.json`
INSERT INTO configs(id, path, content) VALUES('a9f435f3-1f7e-407e-b48d-b08b879a6ce9', '/flows/outgoingDocumentFlow.json', :'content');
\set content `cat config/forms/configFile.json`
INSERT INTO configs(id, path, content) VALUES('160a4d1f-ce54-433f-a67b-4f0ac74e36b6', '/forms/configFile.json', :'content');
\set content `cat config/forms/contragentForm.json`
INSERT INTO configs(id, path, content) VALUES('cd136ae0-67cb-47e7-90ed-58d2a59b214a', '/forms/contragentForm.json', :'content');
\set content `cat config/forms/groupForm.json`
INSERT INTO configs(id, path, content) VALUES('ee8d9d09-d26f-47ce-ad14-fa34e764824f', '/forms/groupForm.json', :'content');
\set content `cat config/forms/incomingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('b5c1d672-abc1-47b7-b93e-732081f99eaf', '/forms/incomingDocumentForm.json', :'content');
\set content `cat config/forms/internalDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('27169a95-d2d8-46a6-9763-2d56285259da', '/forms/internalDocumentForm.json', :'content');
\set content `cat config/forms/outgoingDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('41750ab1-47e8-482d-a1c3-4a08cfc5d832', '/forms/outgoingDocumentForm.json', :'content');
\set content `cat config/forms/registrationListForm.json`
INSERT INTO configs(id, path, content) VALUES('f04df10f-3552-4955-8df3-3288b9316589', '/forms/registrationListForm.json', :'content');
\set content `cat config/forms/taskDocumentForm.json`
INSERT INTO configs(id, path, content) VALUES('e5c52a73-f3ab-4efa-8068-80b02acec9e0', '/forms/taskDocumentForm.json', :'content');
\set content `cat config/forms/userForm.json`
INSERT INTO configs(id, path, content) VALUES('b9c03cac-fe46-4e1b-9c0c-aeb14e73064f', '/forms/userForm.json', :'content');
\set content `cat config/forms/userSystemForm.json`
INSERT INTO configs(id, path, content) VALUES('119a0566-c1c6-4588-b6c5-197b73134606', '/forms/userSystemForm.json', :'content');
\set content `cat config/lists/archivedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('b2ede74f-6134-47e1-8c83-60aea496cbfe', '/lists/archivedDocumentsList.json', :'content');
\set content `cat config/lists/archivedIncomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('af20d1b5-7246-436e-b3da-52a98cd790a2', '/lists/archivedIncomingTasksList.json', :'content');
\set content `cat config/lists/archivedOutgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('bff43fc8-5330-4492-923c-49323c2add85', '/lists/archivedOutgoingTasksList.json', :'content');
\set content `cat config/lists/configFiles.json`
INSERT INTO configs(id, path, content) VALUES('1b2a9602-2c83-49c1-a220-6503a4938e64', '/lists/configFiles.json', :'content');
\set content `cat config/lists/documentsList.json`
INSERT INTO configs(id, path, content) VALUES('1f4c936d-b3bc-4804-a6b0-bd3af7ae76f0', '/lists/documentsList.json', :'content');
\set content `cat config/lists/documentTemplatesList.json`
INSERT INTO configs(id, path, content) VALUES('1e6d720b-d92e-4d68-872e-6ee52533ddb2', '/lists/documentTemplatesList.json', :'content');
\set content `cat config/lists/favoriteDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('3ad669ae-3943-4eeb-85c4-44a50cb5a004', '/lists/favoriteDocumentsList.json', :'content');
\set content `cat config/lists/incomingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('40a3d7de-c1ec-46e9-97e6-43678d7a7f83', '/lists/incomingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/incomingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('6f57c402-2d10-44e1-85d2-97adab6ee11d', '/lists/incomingApprovalTasksList.json', :'content');
\set content `cat config/lists/incomingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('d04dc0ad-a6cf-4025-a75d-f0e07648bedf', '/lists/incomingDocumentsList.json', :'content');
\set content `cat config/lists/incomingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('5ae66ae8-65ed-4142-be68-3630a7d02c6b', '/lists/incomingExecutionTasksList.json', :'content');
\set content `cat config/lists/incomingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('d2a30fc7-490a-4bb9-8b26-1c47df36c494', '/lists/incomingTasksList.json', :'content');
\set content `cat config/lists/internalDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('d78e2dcd-5bfd-46e8-a056-de836957b4b7', '/lists/internalDocumentsList.json', :'content');
\set content `cat config/lists/linkedDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('2d96c67e-eb0d-4b50-8922-6ca15dcbb7a4', '/lists/linkedDocumentsList.json', :'content');
\set content `cat config/lists/myDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('6186bd3d-6327-42cc-9a76-be3043a69a42', '/lists/myDocumentsList.json', :'content');
\set content `cat config/lists/outgoingAcquaintanceTasksList.json`
INSERT INTO configs(id, path, content) VALUES('09041392-82b2-4b9f-b5cb-2d5e7a934219', '/lists/outgoingAcquaintanceTasksList.json', :'content');
\set content `cat config/lists/outgoingApprovalTasksList.json`
INSERT INTO configs(id, path, content) VALUES('06c08a57-e64f-4900-a814-f1c8eb154337', '/lists/outgoingApprovalTasksList.json', :'content');
\set content `cat config/lists/outgoingDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('5806fd79-a5b0-4e78-8a32-d13d3b659230', '/lists/outgoingDocumentsList.json', :'content');
\set content `cat config/lists/outgoingExecutionTasksList.json`
INSERT INTO configs(id, path, content) VALUES('2bb03cd5-0d1a-4a6b-8a02-b6e2a47ea7d1', '/lists/outgoingExecutionTasksList.json', :'content');
\set content `cat config/lists/outgoingTasksList.json`
INSERT INTO configs(id, path, content) VALUES('5bbff693-b8ce-42de-bc8b-fecd401b9297', '/lists/outgoingTasksList.json', :'content');
\set content `cat config/lists/reviewDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('55af20ee-9370-41bf-a693-a1a95fc60e86', '/lists/reviewDocumentsList.json', :'content');
\set content `cat config/lists/searchContragentsList.json`
INSERT INTO configs(id, path, content) VALUES('4ce46634-0e9c-4710-92a1-92c59d543b60', '/lists/searchContragentsList.json', :'content');
\set content `cat config/lists/searchDictionariesList.json`
INSERT INTO configs(id, path, content) VALUES('6add32cd-b18f-45ab-8a4d-46a93722f0a8', '/lists/searchDictionariesList.json', :'content');
\set content `cat config/lists/searchDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('3bcf1e55-2170-4c09-a62d-ab99ee94bd29', '/lists/searchDocumentsList.json', :'content');
\set content `cat config/lists/searchFilesList.json`
INSERT INTO configs(id, path, content) VALUES('cebca96d-54ce-4221-b9ce-bf48e015bc48', '/lists/searchFilesList.json', :'content');
\set content `cat config/lists/searchTasksList.json`
INSERT INTO configs(id, path, content) VALUES('1102a1ad-d024-4cea-a151-27ba1141543c', '/lists/searchTasksList.json', :'content');
\set content `cat config/lists/searchUsersList.json`
INSERT INTO configs(id, path, content) VALUES('e5c9b274-4acc-40f4-ae19-43058dd84420', '/lists/searchUsersList.json', :'content');
\set content `cat config/lists/signDocumentsList.json`
INSERT INTO configs(id, path, content) VALUES('37de95a9-6023-4cc8-aa0f-e58193b8332f', '/lists/signDocumentsList.json', :'content');
\set content `cat config/lists/spaces.json`
INSERT INTO configs(id, path, content) VALUES('adeb23de-ebe5-4431-b8c4-d9c8c58841e9', '/lists/spaces.json', :'content');
\set content `cat config/navigation/admin.json`
INSERT INTO configs(id, path, content) VALUES('155c4e73-c89e-442f-994e-3c4ce0288002', '/navigation/admin.json', :'content');
\set content `cat config/navigation/dictionaries.json`
INSERT INTO configs(id, path, content) VALUES('3c5b6a07-c07f-4308-8243-e7a3eecc4ca3', '/navigation/dictionaries.json', :'content');
\set content `cat config/navigation/main.json`
INSERT INTO configs(id, path, content) VALUES('bd5c00da-e361-4661-aff6-e8ce4d6da1f3', '/navigation/main.json', :'content');
\set content `cat config/navigation/mainTest.json`
INSERT INTO configs(id, path, content) VALUES('006b3b8e-d937-4655-9d15-375c5405df2c', '/navigation/mainTest.json', :'content');
\set content `cat config/navigation/reports.json`
INSERT INTO configs(id, path, content) VALUES('6de173d1-5b54-4b5f-a167-c8595756ef1b', '/navigation/reports.json', :'content');
\set content `cat config/navigation/search.json`
INSERT INTO configs(id, path, content) VALUES('2f5800b2-581c-41dc-89f1-0b40a8af2bc7', '/navigation/search.json', :'content');
\set content `cat config/reports/allDocuments.json`
INSERT INTO configs(id, path, content) VALUES('0e00caf1-9999-4a41-8bca-ef6f7e3aaba4', '/reports/allDocuments.json', :'content');
\set content `cat config/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json`
INSERT INTO configs(id, path, content) VALUES('7270efba-f861-45b1-b010-93e1e4c46a3c', '/reports/e28cb6f5-2a22-4f20-8be7-ed95a4701bc4.json', :'content');
\set content `cat config/types/incomingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('b05c2ce5-49d0-492a-8f31-a4fe056517fa', '/types/incomingDocumentType.json', :'content');
\set content `cat config/types/internalDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('923b75f7-3fb0-4207-93d1-799f43977473', '/types/internalDocumentType.json', :'content');
\set content `cat config/types/outgoingDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('2cf6cac3-c750-408e-9cb5-189cbfebf95d', '/types/outgoingDocumentType.json', :'content');
\set content `cat config/types/taskDocumentType.json`
INSERT INTO configs(id, path, content) VALUES('37cbd506-e457-406f-ac2d-692e78bb1139', '/types/taskDocumentType.json', :'content');
