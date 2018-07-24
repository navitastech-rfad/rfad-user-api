--liquibase formatted sql

--changeset Uchendu.Offurum:2018-07-19-uche-02
CREATE TABLE IF NOT EXISTS role
(
    code           VARCHAR(50)    NOT NULL,
    description    VARCHAR(255),
    CONSTRAINT role_pk PRIMARY KEY (code)
)