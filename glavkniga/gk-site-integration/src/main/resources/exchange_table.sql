USE `gk_site_integration`;

DROP TABLE IF EXISTS `gk_sys_site_exchange`;
CREATE TABLE `gk_sys_site_exchange` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) NOT NULL,
  `table_name` varchar(40) NOT NULL,
  `event` enum('CREATED','UPDATED','DELETED') NOT NULL,
  `data` text NOT NULL,
  `status` enum('NEW','IN_PROGRESS','PROCESSED') NOT NULL,
  `last_update` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `uuid_idx` (`uuid`),
  KEY `status_with_timestamp_idx` (`status`,`last_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TRIGGER IF EXISTS `exchange_added_trigger`;
DROP TRIGGER IF EXISTS `exchange_updated_trigger`;
DELIMITER ;;
CREATE TRIGGER `exchange_added_trigger` BEFORE INSERT ON `gk_sys_site_exchange` FOR EACH ROW
BEGIN
    SET NEW.last_update = now();
    SET NEW.uuid = uuid();
END;;
CREATE TRIGGER `exchange_updated_trigger` BEFORE UPDATE ON `gk_sys_site_exchange` FOR EACH ROW
BEGIN
    Set NEW.last_update = now();
END;;;
DELIMITER ;

