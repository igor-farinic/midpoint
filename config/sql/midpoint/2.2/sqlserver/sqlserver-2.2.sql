CREATE TABLE m_abstract_role (
  approvalExpression    NVARCHAR(MAX),
  approvalProcess       NVARCHAR(255),
  approvalSchema        NVARCHAR(MAX),
  automaticallyApproved NVARCHAR(MAX),
  requestable           BIT,
  id                    BIGINT       NOT NULL,
  oid                   NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_account_shadow (
  accountType              NVARCHAR(255),
  allowedIdmAdminGuiAccess BIT,
  passwordXml              NVARCHAR(MAX),
  id                       BIGINT       NOT NULL,
  oid                      NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_any (
  owner_id   BIGINT       NOT NULL,
  owner_oid  NVARCHAR(36) NOT NULL,
  owner_type INT          NOT NULL,
  PRIMARY KEY (owner_id, owner_oid, owner_type)
);

CREATE TABLE m_any_clob (
  checksum                NVARCHAR(32)  NOT NULL,
  name_namespace          NVARCHAR(255) NOT NULL,
  name_localPart          NVARCHAR(100) NOT NULL,
  anyContainer_owner_id   BIGINT        NOT NULL,
  anyContainer_owner_oid  NVARCHAR(36)  NOT NULL,
  anyContainer_owner_type INT           NOT NULL,
  type_namespace          NVARCHAR(255) NOT NULL,
  type_localPart          NVARCHAR(100) NOT NULL,
  dynamicDef              BIT,
  clobValue               NVARCHAR(MAX),
  valueType               INT,
  PRIMARY KEY (checksum, name_namespace, name_localPart, anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type, type_namespace, type_localPart)
);

CREATE TABLE m_any_date (
  name_namespace          NVARCHAR(255) NOT NULL,
  name_localPart          NVARCHAR(100) NOT NULL,
  anyContainer_owner_id   BIGINT        NOT NULL,
  anyContainer_owner_oid  NVARCHAR(36)  NOT NULL,
  anyContainer_owner_type INT           NOT NULL,
  type_namespace          NVARCHAR(255) NOT NULL,
  type_localPart          NVARCHAR(100) NOT NULL,
  dateValue               DATETIME2     NOT NULL,
  dynamicDef              BIT,
  valueType               INT,
  PRIMARY KEY (name_namespace, name_localPart, anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type, type_namespace, type_localPart, dateValue)
);

CREATE TABLE m_any_long (
  name_namespace          NVARCHAR(255) NOT NULL,
  name_localPart          NVARCHAR(100) NOT NULL,
  anyContainer_owner_id   BIGINT        NOT NULL,
  anyContainer_owner_oid  NVARCHAR(36)  NOT NULL,
  anyContainer_owner_type INT           NOT NULL,
  type_namespace          NVARCHAR(255) NOT NULL,
  type_localPart          NVARCHAR(100) NOT NULL,
  longValue               BIGINT        NOT NULL,
  dynamicDef              BIT,
  valueType               INT,
  PRIMARY KEY (name_namespace, name_localPart, anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type, type_namespace, type_localPart, longValue)
);

CREATE TABLE m_any_poly_string (
  name_namespace          NVARCHAR(255) NOT NULL,
  name_localPart          NVARCHAR(100) NOT NULL,
  anyContainer_owner_id   BIGINT        NOT NULL,
  anyContainer_owner_oid  NVARCHAR(36)  NOT NULL,
  anyContainer_owner_type INT           NOT NULL,
  type_namespace          NVARCHAR(255) NOT NULL,
  type_localPart          NVARCHAR(100) NOT NULL,
  orig                    NVARCHAR(255) NOT NULL,
  dynamicDef              BIT,
  norm                    NVARCHAR(255),
  valueType               INT,
  PRIMARY KEY (name_namespace, name_localPart, anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type, type_namespace, type_localPart, orig)
);

CREATE TABLE m_any_reference (
  name_namespace          NVARCHAR(255) NOT NULL,
  name_localPart          NVARCHAR(100) NOT NULL,
  anyContainer_owner_id   BIGINT        NOT NULL,
  anyContainer_owner_oid  NVARCHAR(36)  NOT NULL,
  anyContainer_owner_type INT           NOT NULL,
  type_namespace          NVARCHAR(255) NOT NULL,
  type_localPart          NVARCHAR(100) NOT NULL,
  targetoid               NVARCHAR(36)  NOT NULL,
  description             NVARCHAR(MAX),
  dynamicDef              BIT,
  filter                  NVARCHAR(MAX),
  relation_namespace      NVARCHAR(255),
  relation_localPart      NVARCHAR(100),
  targetType              INT,
  valueType               INT,
  PRIMARY KEY (name_namespace, name_localPart, anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type, type_namespace, type_localPart, targetoid)
);

CREATE TABLE m_any_string (
  name_namespace          NVARCHAR(255) NOT NULL,
  name_localPart          NVARCHAR(100) NOT NULL,
  anyContainer_owner_id   BIGINT        NOT NULL,
  anyContainer_owner_oid  NVARCHAR(36)  NOT NULL,
  anyContainer_owner_type INT           NOT NULL,
  type_namespace          NVARCHAR(255) NOT NULL,
  type_localPart          NVARCHAR(100) NOT NULL,
  stringValue             NVARCHAR(255) NOT NULL,
  dynamicDef              BIT,
  valueType               INT,
  PRIMARY KEY (name_namespace, name_localPart, anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type, type_namespace, type_localPart, stringValue)
);

CREATE TABLE m_assignment (
  accountConstruction         NVARCHAR(MAX),
  administrativeStatus        INT,
  archiveTimestamp            DATETIME2,
  disableTimestamp            DATETIME2,
  effectiveStatus             INT,
  enableTimestamp             DATETIME2,
  validFrom                   DATETIME2,
  validTo                     DATETIME2,
  validityChangeTimestamp     DATETIME2,
  validityStatus              INT,
  assignmentOwner             INT,
  construction                NVARCHAR(MAX),
  description                 NVARCHAR(MAX),
  owner_id                    BIGINT       NOT NULL,
  owner_oid                   NVARCHAR(36) NOT NULL,
  targetRef_description       NVARCHAR(MAX),
  targetRef_filter            NVARCHAR(MAX),
  targetRef_relationLocalPart NVARCHAR(100),
  targetRef_relationNamespace NVARCHAR(255),
  targetRef_targetOid         NVARCHAR(36),
  targetRef_type              INT,
  id                          BIGINT       NOT NULL,
  oid                         NVARCHAR(36) NOT NULL,
  extId                       BIGINT,
  extOid                      NVARCHAR(36),
  extType                     INT,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_audit_delta (
  checksum         NVARCHAR(32) NOT NULL,
  record_id        BIGINT       NOT NULL,
  delta            NVARCHAR(MAX),
  deltaOid         NVARCHAR(36),
  deltaType        INT,
  details          NVARCHAR(MAX),
  localizedMessage NVARCHAR(MAX),
  message          NVARCHAR(MAX),
  messageCode      NVARCHAR(255),
  operation        NVARCHAR(MAX),
  params           NVARCHAR(MAX),
  partialResults   NVARCHAR(MAX),
  status           INT,
  token            BIGINT,
  PRIMARY KEY (checksum, record_id)
);

CREATE TABLE m_audit_event (
  id                BIGINT NOT NULL,
  channel           NVARCHAR(255),
  eventIdentifier   NVARCHAR(255),
  eventStage        INT,
  eventType         INT,
  hostIdentifier    NVARCHAR(255),
  initiatorName     NVARCHAR(255),
  initiatorOid      NVARCHAR(36),
  message           NVARCHAR(1024),
  outcome           INT,
  parameter         NVARCHAR(255),
  result            NVARCHAR(255),
  sessionIdentifier NVARCHAR(255),
  targetName        NVARCHAR(255),
  targetOid         NVARCHAR(36),
  targetOwnerName   NVARCHAR(255),
  targetOwnerOid    NVARCHAR(36),
  targetType        INT,
  taskIdentifier    NVARCHAR(255),
  taskOID           NVARCHAR(255),
  timestampValue    DATETIME2,
  PRIMARY KEY (id)
);

CREATE TABLE m_authorization (
  decision    INT,
  description NVARCHAR(MAX),
  owner_id    BIGINT       NOT NULL,
  owner_oid   NVARCHAR(36) NOT NULL,
  id          BIGINT       NOT NULL,
  oid         NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_authorization_action (
  role_id  BIGINT       NOT NULL,
  role_oid NVARCHAR(36) NOT NULL,
  action   NVARCHAR(255)
);

CREATE TABLE m_connector (
  connectorBundle              NVARCHAR(255),
  connectorHostRef_description NVARCHAR(MAX),
  connectorHostRef_filter      NVARCHAR(MAX),
  c16_relationLocalPart        NVARCHAR(100),
  c16_relationNamespace        NVARCHAR(255),
  connectorHostRef_targetOid   NVARCHAR(36),
  connectorHostRef_type        INT,
  connectorType                NVARCHAR(255),
  connectorVersion             NVARCHAR(255),
  framework                    NVARCHAR(255),
  name_norm                    NVARCHAR(255),
  name_orig                    NVARCHAR(255),
  namespace                    NVARCHAR(255),
  xmlSchema                    NVARCHAR(MAX),
  id                           BIGINT       NOT NULL,
  oid                          NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_connector_host (
  hostname          NVARCHAR(255),
  name_norm         NVARCHAR(255),
  name_orig         NVARCHAR(255),
  port              NVARCHAR(255),
  protectConnection BIT,
  sharedSecret      NVARCHAR(MAX),
  timeout           INT,
  id                BIGINT       NOT NULL,
  oid               NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_connector_target_system (
  connector_id     BIGINT       NOT NULL,
  connector_oid    NVARCHAR(36) NOT NULL,
  targetSystemType NVARCHAR(255)
);

CREATE TABLE m_container (
  id  BIGINT       NOT NULL,
  oid NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_exclusion (
  description                 NVARCHAR(MAX),
  owner_id                    BIGINT       NOT NULL,
  owner_oid                   NVARCHAR(36) NOT NULL,
  policy                      INT,
  targetRef_description       NVARCHAR(MAX),
  targetRef_filter            NVARCHAR(MAX),
  targetRef_relationLocalPart NVARCHAR(100),
  targetRef_relationNamespace NVARCHAR(255),
  targetRef_targetOid         NVARCHAR(36),
  targetRef_type              INT,
  id                          BIGINT       NOT NULL,
  oid                         NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_focus (
  administrativeStatus    INT,
  archiveTimestamp        DATETIME2,
  disableTimestamp        DATETIME2,
  effectiveStatus         INT,
  enableTimestamp         DATETIME2,
  validFrom               DATETIME2,
  validTo                 DATETIME2,
  validityChangeTimestamp DATETIME2,
  validityStatus          INT,
  id                      BIGINT       NOT NULL,
  oid                     NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_generic_object (
  name_norm  NVARCHAR(255),
  name_orig  NVARCHAR(255),
  objectType NVARCHAR(255),
  id         BIGINT       NOT NULL,
  oid        NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_metadata (
  owner_id                      BIGINT       NOT NULL,
  owner_oid                     NVARCHAR(36) NOT NULL,
  createChannel                 NVARCHAR(255),
  createTimestamp               DATETIME2,
  creatorRef_description        NVARCHAR(MAX),
  creatorRef_filter             NVARCHAR(MAX),
  creatorRef_relationLocalPart  NVARCHAR(100),
  creatorRef_relationNamespace  NVARCHAR(255),
  creatorRef_targetOid          NVARCHAR(36),
  creatorRef_type               INT,
  modifierRef_description       NVARCHAR(MAX),
  modifierRef_filter            NVARCHAR(MAX),
  modifierRef_relationLocalPart NVARCHAR(100),
  modifierRef_relationNamespace NVARCHAR(255),
  modifierRef_targetOid         NVARCHAR(36),
  modifierRef_type              INT,
  modifyChannel                 NVARCHAR(255),
  modifyTimestamp               DATETIME2,
  PRIMARY KEY (owner_id, owner_oid)
);

CREATE TABLE m_node (
  clusteredNode          BIT,
  hostname               NVARCHAR(255),
  internalNodeIdentifier NVARCHAR(255),
  jmxPort                INT,
  lastCheckInTime        DATETIME2,
  name_norm              NVARCHAR(255),
  name_orig              NVARCHAR(255),
  nodeIdentifier         NVARCHAR(255),
  running                BIT,
  id                     BIGINT       NOT NULL,
  oid                    NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_object (
  description NVARCHAR(MAX),
  version     BIGINT       NOT NULL,
  id          BIGINT       NOT NULL,
  oid         NVARCHAR(36) NOT NULL,
  extId       BIGINT,
  extOid      NVARCHAR(36),
  extType     INT,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_object_template (
  accountConstruction NVARCHAR(MAX),
  mapping             NVARCHAR(MAX),
  name_norm           NVARCHAR(255),
  name_orig           NVARCHAR(255),
  type                INT,
  id                  BIGINT       NOT NULL,
  oid                 NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_operation_result (
  owner_oid        NVARCHAR(36) NOT NULL,
  owner_id         BIGINT       NOT NULL,
  details          NVARCHAR(MAX),
  localizedMessage NVARCHAR(MAX),
  message          NVARCHAR(MAX),
  messageCode      NVARCHAR(255),
  operation        NVARCHAR(MAX),
  params           NVARCHAR(MAX),
  partialResults   NVARCHAR(MAX),
  status           INT,
  token            BIGINT,
  PRIMARY KEY (owner_oid, owner_id)
);

CREATE TABLE m_org (
  costCenter       NVARCHAR(255),
  displayName_norm NVARCHAR(255),
  displayName_orig NVARCHAR(255),
  identifier       NVARCHAR(255),
  locality_norm    NVARCHAR(255),
  locality_orig    NVARCHAR(255),
  name_norm        NVARCHAR(255),
  name_orig        NVARCHAR(255),
  id               BIGINT       NOT NULL,
  oid              NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_org_closure (
  id             BIGINT NOT NULL,
  ancestor_id    BIGINT,
  ancestor_oid   NVARCHAR(36),
  depthValue     INT,
  descendant_id  BIGINT,
  descendant_oid NVARCHAR(36),
  PRIMARY KEY (id)
);

CREATE TABLE m_org_incorrect (
  descendant_oid NVARCHAR(36) NOT NULL,
  descendant_id  BIGINT       NOT NULL,
  ancestor_oid   NVARCHAR(36) NOT NULL,
  PRIMARY KEY (descendant_oid, descendant_id, ancestor_oid)
);

CREATE TABLE m_org_org_type (
  org_id  BIGINT       NOT NULL,
  org_oid NVARCHAR(36) NOT NULL,
  orgType NVARCHAR(255)
);

CREATE TABLE m_reference (
  reference_type INT           NOT NULL,
  owner_id       BIGINT        NOT NULL,
  owner_oid      NVARCHAR(36)  NOT NULL,
  relLocalPart   NVARCHAR(100) NOT NULL,
  relNamespace   NVARCHAR(255) NOT NULL,
  targetOid      NVARCHAR(36)  NOT NULL,
  description    NVARCHAR(MAX),
  filter         NVARCHAR(MAX),
  containerType  INT,
  PRIMARY KEY (owner_id, owner_oid, relLocalPart, relNamespace, targetOid)
);

CREATE TABLE m_resource (
  administrativeState            INT,
  capabilities_cachingMetadata   NVARCHAR(MAX),
  capabilities_configured        NVARCHAR(MAX),
  capabilities_native            NVARCHAR(MAX),
  configuration                  NVARCHAR(MAX),
  connectorRef_description       NVARCHAR(MAX),
  connectorRef_filter            NVARCHAR(MAX),
  connectorRef_relationLocalPart NVARCHAR(100),
  connectorRef_relationNamespace NVARCHAR(255),
  connectorRef_targetOid         NVARCHAR(36),
  connectorRef_type              INT,
  consistency                    NVARCHAR(MAX),
  name_norm                      NVARCHAR(255),
  name_orig                      NVARCHAR(255),
  namespace                      NVARCHAR(255),
  o16_lastAvailabilityStatus     INT,
  projection                     NVARCHAR(MAX),
  schemaHandling                 NVARCHAR(MAX),
  scripts                        NVARCHAR(MAX),
  synchronization                NVARCHAR(MAX),
  xmlSchema                      NVARCHAR(MAX),
  id                             BIGINT       NOT NULL,
  oid                            NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_role (
  name_norm NVARCHAR(255),
  name_orig NVARCHAR(255),
  roleType  NVARCHAR(255),
  id        BIGINT       NOT NULL,
  oid       NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_shadow (
  administrativeStatus          INT,
  archiveTimestamp              DATETIME2,
  disableTimestamp              DATETIME2,
  effectiveStatus               INT,
  enableTimestamp               DATETIME2,
  validFrom                     DATETIME2,
  validTo                       DATETIME2,
  validityChangeTimestamp       DATETIME2,
  validityStatus                INT,
  assigned                      BIT,
  attemptNumber                 INT,
  dead                          BIT,
  exist                         BIT,
  failedOperationType           INT,
  intent                        NVARCHAR(255),
  iteration                     INT,
  iterationToken                NVARCHAR(255),
  kind                          INT,
  name_norm                     NVARCHAR(255),
  name_orig                     NVARCHAR(255),
  objectChange                  NVARCHAR(MAX),
  class_namespace               NVARCHAR(255),
  class_localPart               NVARCHAR(100),
  resourceRef_description       NVARCHAR(MAX),
  resourceRef_filter            NVARCHAR(MAX),
  resourceRef_relationLocalPart NVARCHAR(100),
  resourceRef_relationNamespace NVARCHAR(255),
  resourceRef_targetOid         NVARCHAR(36),
  resourceRef_type              INT,
  synchronizationSituation      INT,
  synchronizationTimestamp      DATETIME2,
  id                            BIGINT       NOT NULL,
  oid                           NVARCHAR(36) NOT NULL,
  attrId                        BIGINT,
  attrOid                       NVARCHAR(36),
  attrType                      INT,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_sync_situation_description (
  checksum       NVARCHAR(32) NOT NULL,
  shadow_id      BIGINT       NOT NULL,
  shadow_oid     NVARCHAR(36) NOT NULL,
  chanel         NVARCHAR(255),
  situation      INT,
  timestampValue DATETIME2,
  PRIMARY KEY (checksum, shadow_id, shadow_oid)
);

CREATE TABLE m_system_configuration (
  cleanupPolicy                  NVARCHAR(MAX),
  connectorFramework             NVARCHAR(MAX),
  d22_description                NVARCHAR(MAX),
  defaultUserTemplateRef_filter  NVARCHAR(MAX),
  d22_relationLocalPart          NVARCHAR(100),
  d22_relationNamespace          NVARCHAR(255),
  d22_targetOid                  NVARCHAR(36),
  defaultUserTemplateRef_type    INT,
  g36                            NVARCHAR(MAX),
  g23_description                NVARCHAR(MAX),
  globalPasswordPolicyRef_filter NVARCHAR(MAX),
  g23_relationLocalPart          NVARCHAR(100),
  g23_relationNamespace          NVARCHAR(255),
  g23_targetOid                  NVARCHAR(36),
  globalPasswordPolicyRef_type   INT,
  logging                        NVARCHAR(MAX),
  modelHooks                     NVARCHAR(MAX),
  name_norm                      NVARCHAR(255),
  name_orig                      NVARCHAR(255),
  notificationConfiguration      NVARCHAR(MAX),
  profilingConfiguration         NVARCHAR(MAX),
  id                             BIGINT       NOT NULL,
  oid                            NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_task (
  binding                     INT,
  canRunOnNode                NVARCHAR(255),
  category                    NVARCHAR(255),
  completionTimestamp         DATETIME2,
  executionStatus             INT,
  handlerUri                  NVARCHAR(255),
  lastRunFinishTimestamp      DATETIME2,
  lastRunStartTimestamp       DATETIME2,
  name_norm                   NVARCHAR(255),
  name_orig                   NVARCHAR(255),
  node                        NVARCHAR(255),
  objectRef_description       NVARCHAR(MAX),
  objectRef_filter            NVARCHAR(MAX),
  objectRef_relationLocalPart NVARCHAR(100),
  objectRef_relationNamespace NVARCHAR(255),
  objectRef_targetOid         NVARCHAR(36),
  objectRef_type              INT,
  otherHandlersUriStack       NVARCHAR(MAX),
  ownerRef_description        NVARCHAR(MAX),
  ownerRef_filter             NVARCHAR(MAX),
  ownerRef_relationLocalPart  NVARCHAR(100),
  ownerRef_relationNamespace  NVARCHAR(255),
  ownerRef_targetOid          NVARCHAR(36),
  ownerRef_type               INT,
  parent                      NVARCHAR(255),
  progress                    BIGINT,
  recurrence                  INT,
  resultStatus                INT,
  schedule                    NVARCHAR(MAX),
  taskIdentifier              NVARCHAR(255),
  threadStopAction            INT,
  waitingReason               INT,
  id                          BIGINT       NOT NULL,
  oid                         NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_task_dependent (
  task_id   BIGINT       NOT NULL,
  task_oid  NVARCHAR(36) NOT NULL,
  dependent NVARCHAR(255)
);

CREATE TABLE m_trigger (
  handlerUri     NVARCHAR(255),
  owner_id       BIGINT       NOT NULL,
  owner_oid      NVARCHAR(36) NOT NULL,
  timestampValue DATETIME2,
  id             BIGINT       NOT NULL,
  oid            NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid)
);

CREATE TABLE m_user (
  additionalName_norm      NVARCHAR(255),
  additionalName_orig      NVARCHAR(255),
  costCenter               NVARCHAR(255),
  allowedIdmAdminGuiAccess BIT,
  passwordXml              NVARCHAR(MAX),
  emailAddress             NVARCHAR(255),
  employeeNumber           NVARCHAR(255),
  familyName_norm          NVARCHAR(255),
  familyName_orig          NVARCHAR(255),
  fullName_norm            NVARCHAR(255),
  fullName_orig            NVARCHAR(255),
  givenName_norm           NVARCHAR(255),
  givenName_orig           NVARCHAR(255),
  honorificPrefix_norm     NVARCHAR(255),
  honorificPrefix_orig     NVARCHAR(255),
  honorificSuffix_norm     NVARCHAR(255),
  honorificSuffix_orig     NVARCHAR(255),
  locale                   NVARCHAR(255),
  locality_norm            NVARCHAR(255),
  locality_orig            NVARCHAR(255),
  name_norm                NVARCHAR(255),
  name_orig                NVARCHAR(255),
  nickName_norm            NVARCHAR(255),
  nickName_orig            NVARCHAR(255),
  preferredLanguage        NVARCHAR(255),
  telephoneNumber          NVARCHAR(255),
  timezone                 NVARCHAR(255),
  title_norm               NVARCHAR(255),
  title_orig               NVARCHAR(255),
  id                       BIGINT       NOT NULL,
  oid                      NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE TABLE m_user_employee_type (
  user_id      BIGINT       NOT NULL,
  user_oid     NVARCHAR(36) NOT NULL,
  employeeType NVARCHAR(255)
);

CREATE TABLE m_user_organization (
  user_id  BIGINT       NOT NULL,
  user_oid NVARCHAR(36) NOT NULL,
  norm     NVARCHAR(255),
  orig     NVARCHAR(255)
);

CREATE TABLE m_user_organizational_unit (
  user_id  BIGINT       NOT NULL,
  user_oid NVARCHAR(36) NOT NULL,
  norm     NVARCHAR(255),
  orig     NVARCHAR(255)
);

CREATE TABLE m_value_policy (
  lifetime     NVARCHAR(MAX),
  name_norm    NVARCHAR(255),
  name_orig    NVARCHAR(255),
  stringPolicy NVARCHAR(MAX),
  id           BIGINT       NOT NULL,
  oid          NVARCHAR(36) NOT NULL,
  PRIMARY KEY (id, oid),
  UNIQUE (name_norm)
);

CREATE INDEX iRequestable ON m_abstract_role (requestable);

ALTER TABLE m_abstract_role
ADD CONSTRAINT fk_abstract_role
FOREIGN KEY (id, oid)
REFERENCES m_focus;

ALTER TABLE m_account_shadow
ADD CONSTRAINT fk_account_shadow
FOREIGN KEY (id, oid)
REFERENCES m_shadow;

ALTER TABLE m_any_clob
ADD CONSTRAINT fk_any_clob
FOREIGN KEY (anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type)
REFERENCES m_any;

CREATE INDEX iDate ON m_any_date (dateValue);

ALTER TABLE m_any_date
ADD CONSTRAINT fk_any_date
FOREIGN KEY (anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type)
REFERENCES m_any;

CREATE INDEX iLong ON m_any_long (longValue);

ALTER TABLE m_any_long
ADD CONSTRAINT fk_any_long
FOREIGN KEY (anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type)
REFERENCES m_any;

CREATE INDEX iPolyString ON m_any_poly_string (orig);

ALTER TABLE m_any_poly_string
ADD CONSTRAINT fk_any_poly_string
FOREIGN KEY (anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type)
REFERENCES m_any;

CREATE INDEX iTargetOid ON m_any_reference (targetoid);

ALTER TABLE m_any_reference
ADD CONSTRAINT fk_any_reference
FOREIGN KEY (anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type)
REFERENCES m_any;

CREATE INDEX iString ON m_any_string (stringValue);

ALTER TABLE m_any_string
ADD CONSTRAINT fk_any_string
FOREIGN KEY (anyContainer_owner_id, anyContainer_owner_oid, anyContainer_owner_type)
REFERENCES m_any;

CREATE INDEX iAssignmentAdministrative ON m_assignment (administrativeStatus);

CREATE INDEX iAssignmentEffective ON m_assignment (effectiveStatus);

ALTER TABLE m_assignment
ADD CONSTRAINT fk_assignment
FOREIGN KEY (id, oid)
REFERENCES m_container;

ALTER TABLE m_assignment
ADD CONSTRAINT fk_assignment_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_object;

ALTER TABLE m_audit_delta
ADD CONSTRAINT fk_audit_delta
FOREIGN KEY (record_id)
REFERENCES m_audit_event;

ALTER TABLE m_authorization
ADD CONSTRAINT fk_authorization
FOREIGN KEY (id, oid)
REFERENCES m_container;

ALTER TABLE m_authorization
ADD CONSTRAINT fk_authorization_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_object;

ALTER TABLE m_authorization_action
ADD CONSTRAINT fk_authorization_action
FOREIGN KEY (role_id, role_oid)
REFERENCES m_authorization;

CREATE INDEX iConnectorNameNorm ON m_connector (name_norm);

CREATE INDEX iConnectorNameOrig ON m_connector (name_orig);

ALTER TABLE m_connector
ADD CONSTRAINT fk_connector
FOREIGN KEY (id, oid)
REFERENCES m_object;

CREATE INDEX iConnectorHostName ON m_connector_host (name_orig);

ALTER TABLE m_connector_host
ADD CONSTRAINT fk_connector_host
FOREIGN KEY (id, oid)
REFERENCES m_object;

ALTER TABLE m_connector_target_system
ADD CONSTRAINT fk_connector_target_system
FOREIGN KEY (connector_id, connector_oid)
REFERENCES m_connector;

ALTER TABLE m_exclusion
ADD CONSTRAINT fk_exclusion
FOREIGN KEY (id, oid)
REFERENCES m_container;

ALTER TABLE m_exclusion
ADD CONSTRAINT fk_exclusion_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_object;

CREATE INDEX iFocusAdministrative ON m_focus (administrativeStatus);

CREATE INDEX iFocusEffective ON m_focus (effectiveStatus);

ALTER TABLE m_focus
ADD CONSTRAINT fk_focus
FOREIGN KEY (id, oid)
REFERENCES m_object;

CREATE INDEX iGenericObjectName ON m_generic_object (name_orig);

ALTER TABLE m_generic_object
ADD CONSTRAINT fk_generic_object
FOREIGN KEY (id, oid)
REFERENCES m_object;

ALTER TABLE m_metadata
ADD CONSTRAINT fk_metadata_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_container;

CREATE INDEX iNodeName ON m_node (name_orig);

ALTER TABLE m_node
ADD CONSTRAINT fk_node
FOREIGN KEY (id, oid)
REFERENCES m_object;

ALTER TABLE m_object
ADD CONSTRAINT fk_object
FOREIGN KEY (id, oid)
REFERENCES m_container;

CREATE INDEX iObjectTemplate ON m_object_template (name_orig);

ALTER TABLE m_object_template
ADD CONSTRAINT fk_object_template
FOREIGN KEY (id, oid)
REFERENCES m_object;

ALTER TABLE m_operation_result
ADD CONSTRAINT fk_result_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_object;

CREATE INDEX iOrgName ON m_org (name_orig);

ALTER TABLE m_org
ADD CONSTRAINT fk_org
FOREIGN KEY (id, oid)
REFERENCES m_abstract_role;

ALTER TABLE m_org_closure
ADD CONSTRAINT fk_descendant
FOREIGN KEY (descendant_id, descendant_oid)
REFERENCES m_object;

ALTER TABLE m_org_closure
ADD CONSTRAINT fk_ancestor
FOREIGN KEY (ancestor_id, ancestor_oid)
REFERENCES m_object;

ALTER TABLE m_org_org_type
ADD CONSTRAINT fk_org_org_type
FOREIGN KEY (org_id, org_oid)
REFERENCES m_org;

CREATE INDEX iReferenceTargetOid ON m_reference (targetOid);

ALTER TABLE m_reference
ADD CONSTRAINT fk_reference_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_container;

CREATE INDEX iResourceName ON m_resource (name_orig);

ALTER TABLE m_resource
ADD CONSTRAINT fk_resource
FOREIGN KEY (id, oid)
REFERENCES m_object;

CREATE INDEX iRoleName ON m_role (name_orig);

ALTER TABLE m_role
ADD CONSTRAINT fk_role
FOREIGN KEY (id, oid)
REFERENCES m_abstract_role;

CREATE INDEX iShadowNameOrig ON m_shadow (name_orig);

CREATE INDEX iShadowDead ON m_shadow (dead);

CREATE INDEX iShadowNameNorm ON m_shadow (name_norm);

CREATE INDEX iShadowResourceRef ON m_shadow (resourceRef_targetOid);

CREATE INDEX iShadowAdministrative ON m_shadow (administrativeStatus);

CREATE INDEX iShadowEffective ON m_shadow (effectiveStatus);

ALTER TABLE m_shadow
ADD CONSTRAINT fk_shadow
FOREIGN KEY (id, oid)
REFERENCES m_object;

ALTER TABLE m_sync_situation_description
ADD CONSTRAINT fk_shadow_sync_situation
FOREIGN KEY (shadow_id, shadow_oid)
REFERENCES m_shadow;

CREATE INDEX iSystemConfigurationName ON m_system_configuration (name_orig);

ALTER TABLE m_system_configuration
ADD CONSTRAINT fk_system_configuration
FOREIGN KEY (id, oid)
REFERENCES m_object;

CREATE INDEX iTaskNameNameNorm ON m_task (name_norm);

CREATE INDEX iTaskNameOrig ON m_task (name_orig);

ALTER TABLE m_task
ADD CONSTRAINT fk_task
FOREIGN KEY (id, oid)
REFERENCES m_object;

ALTER TABLE m_task_dependent
ADD CONSTRAINT fk_task_dependent
FOREIGN KEY (task_id, task_oid)
REFERENCES m_task;

CREATE INDEX iTriggerTimestamp ON m_trigger (timestampValue);

ALTER TABLE m_trigger
ADD CONSTRAINT fk_trigger
FOREIGN KEY (id, oid)
REFERENCES m_container;

ALTER TABLE m_trigger
ADD CONSTRAINT fk_trigger_owner
FOREIGN KEY (owner_id, owner_oid)
REFERENCES m_object;

CREATE INDEX iFullName ON m_user (fullName_orig);

CREATE INDEX iLocality ON m_user (locality_orig);

CREATE INDEX iHonorificSuffix ON m_user (honorificSuffix_orig);

CREATE INDEX iEmployeeNumber ON m_user (employeeNumber);

CREATE INDEX iGivenName ON m_user (givenName_orig);

CREATE INDEX iFamilyName ON m_user (familyName_orig);

CREATE INDEX iAdditionalName ON m_user (additionalName_orig);

CREATE INDEX iHonorificPrefix ON m_user (honorificPrefix_orig);

CREATE INDEX iUserName ON m_user (name_orig);

ALTER TABLE m_user
ADD CONSTRAINT fk_user
FOREIGN KEY (id, oid)
REFERENCES m_focus;

ALTER TABLE m_user_employee_type
ADD CONSTRAINT fk_user_employee_type
FOREIGN KEY (user_id, user_oid)
REFERENCES m_user;

ALTER TABLE m_user_organization
ADD CONSTRAINT fk_user_organization
FOREIGN KEY (user_id, user_oid)
REFERENCES m_user;

ALTER TABLE m_user_organizational_unit
ADD CONSTRAINT fk_user_org_unit
FOREIGN KEY (user_id, user_oid)
REFERENCES m_user;

CREATE INDEX iValuePolicy ON m_value_policy (name_orig);

ALTER TABLE m_value_policy
ADD CONSTRAINT fk_value_policy
FOREIGN KEY (id, oid)
REFERENCES m_object;

CREATE TABLE hibernate_sequence (
  next_val BIGINT
);

INSERT INTO hibernate_sequence VALUES (1);