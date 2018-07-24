--liquibase formatted sql
--changeset Uchendu.Offurum:2018-07-19-uche-04
INSERT INTO role (code, description) VALUES
('admin', 'Administrator'),
('user', 'User');