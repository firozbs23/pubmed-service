-- Autogenerated: do not edit this file

CREATE TABLE IF NOT EXISTS BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID
            BIGINT
                         NOT
                             NULL
        PRIMARY
            KEY,
    VERSION
            BIGINT,
    JOB_NAME
            VARCHAR(100) NOT NULL,
    JOB_KEY VARCHAR(32)  NOT NULL,
    constraint JOB_INST_UN unique
        (
         JOB_NAME,
         JOB_KEY
            )
);

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID
                 BIGINT
        NOT
            NULL
        PRIMARY
            KEY,
    VERSION
                 BIGINT,
    JOB_INSTANCE_ID
                 BIGINT
        NOT
            NULL,
    CREATE_TIME
                 TIMESTAMP
        NOT
            NULL,
    START_TIME
                 TIMESTAMP
        DEFAULT
            NULL,
    END_TIME
                 TIMESTAMP
        DEFAULT
            NULL,
    STATUS
                 VARCHAR(10),
    EXIT_CODE    VARCHAR(2500),
    EXIT_MESSAGE VARCHAR(2500),
    LAST_UPDATED TIMESTAMP,
    constraint JOB_INST_EXEC_FK foreign key
        (
         JOB_INSTANCE_ID
            )
        references BATCH_JOB_INSTANCE
            (
             JOB_INSTANCE_ID
                )
);

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID
                    BIGINT
                                 NOT
                                     NULL,
    PARAMETER_NAME
                    VARCHAR(100) NOT NULL,
    PARAMETER_TYPE  VARCHAR(100) NOT NULL,
    PARAMETER_VALUE VARCHAR(2500),
    IDENTIFYING     CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key
        (
         JOB_EXECUTION_ID
            )
        references BATCH_JOB_EXECUTION
            (
             JOB_EXECUTION_ID
                )
);

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID
                       BIGINT
                                    NOT
                                        NULL
        PRIMARY
            KEY,
    VERSION
                       BIGINT
                                    NOT
                                        NULL,
    STEP_NAME
                       VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP,
    constraint JOB_EXEC_STEP_FK foreign key
        (
         JOB_EXECUTION_ID
            )
        references BATCH_JOB_EXECUTION
            (
             JOB_EXECUTION_ID
                )
);

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID
                       BIGINT
                                     NOT
                                         NULL
        PRIMARY
            KEY,
    SHORT_CONTEXT
                       VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key
        (
         STEP_EXECUTION_ID
            )
        references BATCH_STEP_EXECUTION
            (
             STEP_EXECUTION_ID
                )
);

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID
                       BIGINT
                                     NOT
                                         NULL
        PRIMARY
            KEY,
    SHORT_CONTEXT
                       VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key
        (
         JOB_EXECUTION_ID
            )
        references BATCH_JOB_EXECUTION
            (
             JOB_EXECUTION_ID
                )
);

CREATE SEQUENCE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;


-- Add job_status and job_data tables

CREATE TABLE IF NOT EXISTS job_status
(
    job_id
              UUID
        PRIMARY
            KEY,
    job_status
              VARCHAR(255),
    job_title VARCHAR(255),
    timestamp TIMESTAMPTZ
);



CREATE TABLE IF NOT EXISTS job_data
(
    id                   UUID PRIMARY KEY,
    job_id               UUID REFERENCES job_status (job_id) ON DELETE CASCADE,
    hcp_viq_id           VARCHAR(255),
    title                VARCHAR(255),
    matching_external_id VARCHAR(255),
    timestamp            TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS pubmed_data
(
    id                   UUID PRIMARY KEY,
    job_id               UUID,
    job_title            VARCHAR(255),
    transaction_viq_id   VARCHAR(255),
    hcp_viq_id           VARCHAR(255),
    country_iso2         VARCHAR(2),
    specialty_code       VARCHAR(255),
    publication_id       VARCHAR(255),
    title                TEXT,
    journal              TEXT,
    publication_date     VARCHAR(255),
    abstract             TEXT,
    hcp_role             VARCHAR(255),
    publication_type     VARCHAR(255),
    issn                 VARCHAR(255),
    url                  TEXT,
    gds_tag_viq_id       VARCHAR(255),
    hcp_role_viq_id      VARCHAR(255),
    key                  BIGINT,
    created_by_job       VARCHAR(255),
    updated_by_job       VARCHAR(255),
    created_at           VARCHAR(255),
    updated_at           VARCHAR(255),
    matching_external_id VARCHAR(255),
    first_name           VARCHAR(255),
    last_name            VARCHAR(255),
    initials             VARCHAR(255),
    full_name            VARCHAR(255),
    affiliations         TEXT,
    pmcid                VARCHAR(255),
    doi                  VARCHAR(255),
    mesh_terms           TEXT,
    search_name          VARCHAR(255),
    timestamp            TIMESTAMPTZ
);