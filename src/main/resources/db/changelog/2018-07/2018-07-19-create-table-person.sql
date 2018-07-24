--liquibase formatted sql

--changeset Uchendu.Offurum:2018-07-19-uche-01
CREATE TABLE IF NOT EXISTS person
(
    id              UUID            NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    first_name      VARCHAR(50),
    last_name       VARCHAR(50),
    password        VARCHAR(100)    NOT NULL,
    created_by      VARCHAR(50)     NOT NULL,
    created_date    TIMESTAMP       NOT NULL,
    updated_by      VARCHAR(50),
    updated_date    TIMESTAMP,
    CONSTRAINT person_pk PRIMARY KEY (id),
    UNIQUE (email)
)