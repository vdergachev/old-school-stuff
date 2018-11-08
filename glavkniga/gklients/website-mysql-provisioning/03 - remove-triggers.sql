USE `gk_site_integration`;

--
--                          DROP IT
--
DROP TRIGGER IF EXISTS `remove_user_trigger`;
DROP TRIGGER IF EXISTS `remove_user_data_trigger`;
DROP TRIGGER IF EXISTS `remove_user_services_trigger`;
DROP TRIGGER IF EXISTS `remove_user_service_1_trigger`;
DROP TRIGGER IF EXISTS `remove_user_service_2_trigger`;
DROP TRIGGER IF EXISTS `remove_user_service_3_trigger`;

DELIMITER ;;

--
--                      REMOVE TRIGGERS
--

CREATE TRIGGER `remove_user_trigger` AFTER DELETE ON `gk_sys_site_user` FOR EACH ROW
BEGIN
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user', 'DELETED', CONCAT("{\"id\":\"", OLD.id, "\"}"));
END;;

CREATE TRIGGER `remove_user_data_trigger` AFTER DELETE ON `gk_sys_site_user_data` FOR EACH ROW
BEGIN
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_data', 'DELETED', CONCAT("{\"id\":\"", OLD.id, "\"}"));
END;;

CREATE TRIGGER `remove_user_services_trigger` AFTER DELETE ON `gk_sys_site_user_services` FOR EACH ROW
BEGIN
SET @json = CONCAT("{\"user_id\":\"", OLD.user_id, "\",");
SET @json = CONCAT(@json, "\"service\":\"", OLD.service, "\"}");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_services', 'DELETED', @json);
END;;

CREATE TRIGGER `remove_user_service_1_trigger` AFTER DELETE ON `gk_sys_site_user_service_1` FOR EACH ROW
BEGIN
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_service_1', 'DELETED', CONCAT("{\"user_id\":\"", OLD.user_id, "\"}"));
END;;

CREATE TRIGGER `remove_user_service_2_trigger` AFTER DELETE ON `gk_sys_site_user_service_2` FOR EACH ROW
BEGIN
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_service_2', 'DELETED', CONCAT("{\"user_id\":\"", OLD.user_id, "\"}"));
END;;

CREATE TRIGGER `remove_user_service_3_trigger` AFTER DELETE ON `gk_sys_site_user_service_3` FOR EACH ROW
BEGIN
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_service_3', 'DELETED', CONCAT("{\"user_id\":\"", OLD.user_id, "\"}"));
END;;

DELIMITER ;