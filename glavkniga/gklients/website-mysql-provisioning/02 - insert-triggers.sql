USE `gk_site_integration`;

--
--                          DROP
--
DROP TRIGGER IF EXISTS `insert_user_trigger`;
DROP TRIGGER IF EXISTS `insert_user_data_trigger`;
DROP TRIGGER IF EXISTS `insert_user_services_trigger`;
DROP TRIGGER IF EXISTS `insert_user_service_1_trigger`;
DROP TRIGGER IF EXISTS `insert_user_service_2_trigger`;
DROP TRIGGER IF EXISTS `insert_user_service_3_trigger`;

DELIMITER ;;

--
--                      INSERT TRIGGERS
--

CREATE TRIGGER `insert_user_trigger` AFTER INSERT ON `gk_sys_site_user` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"id\":\"", NEW.id, "\",");
SET @json = CONCAT(@json, "\"email\":\"", NEW.email, "\",");
SET @json = CONCAT(@json, "\"pass_hash\":\"", NEW.pass_hash, "\",");
SET @json = CONCAT(@json, "\"access_type\":\"", NEW.access_type, "\",");
SET @json = CONCAT(@json, "\"phone_hash\":\"", NEW.phone_hash, "\",");
SET @json = CONCAT(@json, "\"blocked\":\"", (select if(NEW.blocked, "true", "false")) , "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `data`)
VALUES ('gk_sys_site_user', @json);
END;;

CREATE TRIGGER `insert_user_data_trigger` AFTER INSERT ON `gk_sys_site_user_data` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"id\":\"", NEW.id, "\",");
SET @json = CONCAT(@json, "\"name\":\"", NEW.name, "\",");
SET @json = CONCAT(@json, "\"phone\":\"", NEW.phone, "\",");
SET @json = CONCAT(@json, "\"date\":\"", NEW.date, "\",");
SET @json = CONCAT(@json, "\"pass\":\"", NEW.pass, "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `data`)
VALUES ('gk_sys_site_user_data', @json);
END;;

CREATE TRIGGER `insert_user_services_trigger` AFTER INSERT ON `gk_sys_site_user_services` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"user_id\":\"", NEW.user_id, "\",");
SET @json = CONCAT(@json, "\"service\":\"", NEW.service, "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `data`)
VALUES ('gk_sys_site_user_services', @json);
END;;

CREATE TRIGGER `insert_user_service_1_trigger` AFTER INSERT ON `gk_sys_site_user_service_1` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"user_id\":\"", NEW.user_id, "\",");
SET @json = CONCAT(@json, "\"number\":\"", NEW.number, "\",");
SET @json = CONCAT(@json, "\"year\":\"", NEW.year, "\",");
SET @json = CONCAT(@json, "\"date_begin\":\"", NEW.date_begin, "\",");
SET @json = CONCAT(@json, "\"date_end\":\"", NEW.date_end, "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `data`)
VALUES ('gk_sys_site_user_service_1', @json);
END;;

CREATE TRIGGER `insert_user_service_2_trigger` AFTER INSERT ON `gk_sys_site_user_service_2` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"user_id\":\"", NEW.user_id, "\",");
SET @json = CONCAT(@json, "\"number\":\"", NEW.number, "\",");
SET @json = CONCAT(@json, "\"year\":\"", NEW.year, "\",");
SET @json = CONCAT(@json, "\"date_begin\":\"", NEW.date_begin, "\",");
SET @json = CONCAT(@json, "\"date_end\":\"", NEW.date_end, "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `data`)
VALUES ('gk_sys_site_user_service_2', @json);
END;;

CREATE TRIGGER `insert_user_service_3_trigger` AFTER INSERT ON `gk_sys_site_user_service_3` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"user_id\":\"", NEW.user_id, "\",");
SET @json = CONCAT(@json, "\"date_begin\":\"", NEW.date_begin, "\",");
SET @json = CONCAT(@json, "\"date_end\":\"", NEW.date_end, "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `data`)
VALUES ('gk_sys_site_user_service_3', @json);
END;;

DELIMITER ;