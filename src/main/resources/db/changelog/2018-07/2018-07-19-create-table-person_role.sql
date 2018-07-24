--liquibase formatted sql
--changeset Uchendu.Offurum:2018-07-19-uche-03
CREATE TABLE IF NOT EXISTS person_role
(
    person_id       UUID            NOT NULL,
    role_code       VARCHAR(50)     NOT NULL,
    created_by      VARCHAR(50)     NOT NULL,
    created_date    TIMESTAMP       NOT NULL,
    updated_by      VARCHAR(50),
    updated_date    TIMESTAMP,
    CONSTRAINT person_role_pk PRIMARY KEY (person_id, role_code)
)