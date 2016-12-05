DROP TABLE IF EXISTS documents;
CREATE TABLE documents (
  id uuid CONSTRAINT documents_pkey PRIMARY KEY,
  type varchar(255),
  status varchar(255),
  title varchar(255),
  "documentKind" varchar(255),
  "registrationDate" timestamp,
  correspondent varchar(255),
  "correspondentTitle" varchar(255),
  "externalSigner" varchar(255),
  "externalExecutor" varchar(255),
  "externalNumber" varchar(255),
  "externalDate" timestamp,
  "documentNumber" varchar(255),
  "pagesQuantity" integer,
  addressee varchar(255)[],
  "addresseeTitle" varchar(255)[],
  author varchar(255)
);