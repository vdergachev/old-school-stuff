USE `gk_site_integration`;

--
--                          DROP IT
--
DROP TRIGGER IF EXISTS `update_user_trigger`;
DROP TRIGGER IF EXISTS `update_user_data_trigger`;
DROP TRIGGER IF EXISTS `update_user_services_trigger`;
DROP TRIGGER IF EXISTS `update_user_service_1_trigger`;
DROP TRIGGER IF EXISTS `update_user_service_2_trigger`;
DROP TRIGGER IF EXISTS `update_user_service_3_trigger`;

DELIMITER ;;

--
--                      UPDATE TRIGGERS
--

CREATE TRIGGER `update_user_trigger` AFTER UPDATE ON `gk_sys_site_user` FOR EACH ROW
BEGIN
	SET @empty = true;
	SET @json = CONCAT("{\"id\":\"", OLD.id, "\"");
	IF (NEW.email <> OLD.email) OR (NEW.email IS NOT NULL AND OLD.email IS NULL) OR (NEW.email IS NULL AND OLD.email IS NOT NULL) THEN
		SET @json = CONCAT(@json, " , ");
		SET @json = CONCAT(@json, "\"email\":\"", NEW.email, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.pass_hash <> OLD.pass_hash) OR (NEW.pass_hash IS NOT NULL AND OLD.pass_hash IS NULL) OR (NEW.pass_hash IS NULL AND OLD.pass_hash IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"pass_hash\":\"", NEW.pass_hash, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.access_type <> OLD.access_type) OR (NEW.access_type IS NOT NULL AND OLD.access_type IS NULL) OR (NEW.access_type IS NULL AND OLD.access_type IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"access_type\":\"", NEW.access_type, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.phone_hash <> OLD.phone_hash) OR (NEW.phone_hash IS NOT NULL AND OLD.phone_hash IS NULL) OR (NEW.phone_hash IS NULL AND OLD.phone_hash IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
		SET @json = CONCAT(@json, "\"phone_hash\":\"", NEW.phone_hash, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.blocked <> OLD.blocked) OR (NEW.blocked IS NOT NULL AND OLD.blocked IS NULL) OR (NEW.blocked IS NULL AND OLD.blocked IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"blocked\":\"", (select if(NEW.blocked, "true", "false")) , "\"");
	END IF;
	SET @json = CONCAT(@json, " }");
    
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user', 'UPDATED', @json);
END;;

CREATE TRIGGER `update_user_data_trigger` AFTER UPDATE ON `gk_sys_site_user_data` FOR EACH ROW
BEGIN
    SET @empty = true;
	SET @json = CONCAT("{\"id\":\"", OLD.id, "\"");
	IF (NEW.name <> OLD.name) OR (NEW.name IS NOT NULL AND OLD.name IS NULL) OR (NEW.name IS NULL AND OLD.name IS NOT NULL) THEN
		SET @json = CONCAT(@json, " , ");
		SET @json = CONCAT(@json, "\"name\":\"", NEW.name, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.phone <> OLD.phone) OR (NEW.phone IS NOT NULL AND OLD.phone IS NULL) OR (NEW.phone IS NULL AND OLD.phone IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"phone\":\"", NEW.phone, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.date <> OLD.date) OR (NEW.date IS NOT NULL AND OLD.date IS NULL) OR (NEW.date IS NULL AND OLD.date IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date\":\"", NEW.date, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.pass <> OLD.pass) OR (NEW.pass IS NOT NULL AND OLD.pass IS NULL) OR (NEW.pass IS NULL AND OLD.pass IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
		SET @json = CONCAT(@json, "\"pass\":\"", NEW.pass, "\"");
	END IF;
	SET @json = CONCAT(@json, " }");

INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_data', 'UPDATED', @json);
END;;

CREATE TRIGGER `update_user_services_trigger` AFTER UPDATE ON `gk_sys_site_user_services` FOR EACH ROW
BEGIN
    SET @empty = true;
    SET @json = "{ ";
	IF (NEW.user_id <> OLD.user_id) OR (NEW.user_id IS NOT NULL AND OLD.user_id IS NULL) OR (NEW.user_id IS NULL AND OLD.user_id IS NOT NULL) THEN
		SET @json = CONCAT(@json, "\"user_id\":\"", NEW.user_id, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.service <> OLD.service) OR (NEW.service IS NOT NULL AND OLD.service IS NULL) OR (NEW.service IS NULL AND OLD.service IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"service\":\"", NEW.service, "\"");
	END IF;
	SET @json = CONCAT(@json, " }");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_services', 'UPDATED', @json);
END;;

CREATE TRIGGER `update_user_service_1_trigger` AFTER UPDATE ON `gk_sys_site_user_service_1` FOR EACH ROW
BEGIN
    SET @empty = true;
    SET @json = "{ ";
	IF (NEW.user_id <> OLD.user_id) OR (NEW.user_id IS NOT NULL AND OLD.user_id IS NULL) OR (NEW.user_id IS NULL AND OLD.user_id IS NOT NULL) THEN
		SET @json = CONCAT(@json, "\"user_id\":\"", NEW.user_id, "\"");
        SET @empty = false;
	END IF;
    IF (NEW.number <> OLD.number) OR (NEW.number IS NOT NULL AND OLD.number IS NULL) OR (NEW.number IS NULL AND OLD.number IS NOT NULL) THEN
		IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"number\":\"", NEW.number, "\"");
        SET @empty = false;
	END IF;
	IF (NEW.year <> OLD.year) OR (NEW.year IS NOT NULL AND OLD.year IS NULL) OR (NEW.year IS NULL AND OLD.year IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"year\":\"", NEW.year, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.date_begin <> OLD.date_begin) OR (NEW.date_begin IS NOT NULL AND OLD.date_begin IS NULL) OR (NEW.date_begin IS NULL AND OLD.date_begin IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date_begin\":\"", NEW.date_begin, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.date_end <> OLD.date_end) OR (NEW.date_end IS NOT NULL AND OLD.date_end IS NULL) OR (NEW.date_end IS NULL AND OLD.date_end IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date_end\":\"", NEW.date_end, "\"");
    END IF;
	SET @json = CONCAT(@json, " }");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_service_1', 'UPDATED', @json);
END;;

CREATE TRIGGER `update_user_service_2_trigger` AFTER UPDATE ON `gk_sys_site_user_service_2` FOR EACH ROW
BEGIN
    SET @empty = true;
    SET @json = "{ ";
    IF (NEW.user_id <> OLD.user_id) OR (NEW.user_id IS NOT NULL AND OLD.user_id IS NULL) OR (NEW.user_id IS NULL AND OLD.user_id IS NOT NULL) THEN
        SET @json = CONCAT(@json, "\"user_id\":\"", NEW.user_id, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.number <> OLD.number) OR (NEW.number IS NOT NULL AND OLD.number IS NULL) OR (NEW.number IS NULL AND OLD.number IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"number\":\"", NEW.number, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.year <> OLD.year) OR (NEW.year IS NOT NULL AND OLD.year IS NULL) OR (NEW.year IS NULL AND OLD.year IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"year\":\"", NEW.year, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.date_begin <> OLD.date_begin) OR (NEW.date_begin IS NOT NULL AND OLD.date_begin IS NULL) OR (NEW.date_begin IS NULL AND OLD.date_begin IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date_begin\":\"", NEW.date_begin, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.date_end <> OLD.date_end) OR (NEW.date_end IS NOT NULL AND OLD.date_end IS NULL) OR (NEW.date_end IS NULL AND OLD.date_end IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date_end\":\"", NEW.date_end, "\"");
    END IF;
    SET @json = CONCAT(@json, " }");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_service_2', 'UPDATED', @json);
END;;

CREATE TRIGGER `update_user_service_3_trigger` AFTER UPDATE ON `gk_sys_site_user_service_3` FOR EACH ROW
BEGIN
    SET @empty = true;
    SET @json = "{ ";
    IF (NEW.user_id <> OLD.user_id) OR (NEW.user_id IS NOT NULL AND OLD.user_id IS NULL) OR (NEW.user_id IS NULL AND OLD.user_id IS NOT NULL) THEN
        SET @json = CONCAT(@json, "\"user_id\":\"", NEW.user_id, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.date_begin <> OLD.date_begin) OR (NEW.date_begin IS NOT NULL AND OLD.date_begin IS NULL) OR (NEW.date_begin IS NULL AND OLD.date_begin IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date_begin\":\"", NEW.date_begin, "\"");
        SET @empty = false;
    END IF;
    IF (NEW.date_end <> OLD.date_end) OR (NEW.date_end IS NOT NULL AND OLD.date_end IS NULL) OR (NEW.date_end IS NULL AND OLD.date_end IS NOT NULL) THEN
        IF (@empty = false) THEN SET @json = CONCAT(@json, " , "); END IF;
        SET @json = CONCAT(@json, "\"date_end\":\"", NEW.date_end, "\"");
    END IF;
    SET @json = CONCAT(@json, " }");
INSERT INTO `gk_sys_site_exchange` (`table_name`, `event`, `data`)
VALUES ('gk_sys_site_user_service_3', 'UPDATED', @json);
END;;


DELIMITER ;