CREATE TABLE `sleepBoardContent` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`writer` VARCHAR(45) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`writerTitle` VARCHAR(250) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`registerTime` TIMESTAMP NULL DEFAULT NULL,
	`linkUrl` VARCHAR(250) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`linkTitle` VARCHAR(250) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`linkChannel` VARCHAR(250) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`likeCount` INT(11) NOT NULL DEFAULT '0',
	`dislikeCount` INT(11) NOT NULL DEFAULT '0',
	`fireCount` INT(11) NOT NULL DEFAULT '0',
	`thumbnailUrl` VARCHAR(250) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`boardIp` VARCHAR(60) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	`password` VARCHAR(80) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepBoardReply` (
	`rid` INT(11) NOT NULL AUTO_INCREMENT,
	`id` INT(11) NOT NULL DEFAULT '0',
	`writer` VARCHAR(250) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`replyContent` VARCHAR(250) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`password` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`likeCount` INT(11) NULL DEFAULT NULL,
	`fireCount` INT(11) NULL DEFAULT NULL,
	`boardIp` VARCHAR(80) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`registerTime` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`rid`) USING BTREE,
	CONSTRAINT `FK__sleepBoardContent` FOREIGN KEY (`rid`) REFERENCES `sleepwell`.`sleepBoardContent` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepBoardRereply` (
	`rrid` INT(11) NOT NULL,
	`rid` INT(11) NOT NULL,
	`writer` VARCHAR(250) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`rereplyContent` VARCHAR(250) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`password` VARCHAR(100) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`likeCount` INT(11) NOT NULL DEFAULT '0',
	`fireCount` INT(11) NOT NULL DEFAULT '0',
	`boardIp` VARCHAR(45) NOT NULL COLLATE 'utf8_general_ci',
	`registerTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`id` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`rrid`) USING BTREE,
	INDEX `FK__sleepBoardReply` (`rid`) USING BTREE,
	CONSTRAINT `FK__sleepBoardReply` FOREIGN KEY (`rid`) REFERENCES `sleepwell`.`sleepBoardReply` (`rid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepFireReplyCheck` (
	`fireTime` DATETIME NOT NULL,
	`rid` INT(11) NOT NULL DEFAULT '0',
	`boardIp` VARCHAR(45) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	PRIMARY KEY (`fireTime`) USING BTREE,
	INDEX `FK__sleepBoardReply_3` (`rid`) USING BTREE,
	CONSTRAINT `FK__sleepBoardReply_3` FOREIGN KEY (`rid`) REFERENCES `sleepwell`.`sleepBoardReply` (`rid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepFireReReplyCheck` (
	`fireTime` DATETIME NOT NULL,
	`rrid` INT(11) NOT NULL DEFAULT '0',
	`boardIp` VARCHAR(45) NOT NULL DEFAULT '0' COLLATE 'utf8_general_ci',
	PRIMARY KEY (`fireTime`) USING BTREE,
	INDEX `FK__sleepBoardRereply` (`rrid`) USING BTREE,
	CONSTRAINT `FK__sleepBoardRereply` FOREIGN KEY (`rrid`) REFERENCES `sleepwell`.`sleepBoardRereply` (`rrid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepLikeCheck` (
	`likeTime` DATETIME NOT NULL,
	`boardIp` VARCHAR(45) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`id` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`likeTime`) USING BTREE,
	INDEX `FK__sleepBoardReply_1` (`id`) USING BTREE,
	CONSTRAINT `FK__sleepBoardReply_1` FOREIGN KEY (`id`) REFERENCES `sleepwell`.`sleepBoardReply` (`rid`) ON UPDATE RESTRICT ON DELETE RESTRICT
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepLikeReplyCheck` (
	`likeTime` DATETIME NOT NULL,
	`boardIp` VARCHAR(45) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`rid` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`likeTime`) USING BTREE,
	INDEX `FK__sleepBoardReply_2` (`rid`) USING BTREE,
	CONSTRAINT `FK__sleepBoardReply_2` FOREIGN KEY (`rid`) REFERENCES `sleepwell`.`sleepBoardReply` (`rid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `sleepLikeReReplyCheck` (
	`likeTime` DATETIME NOT NULL,
	`boardIp` VARCHAR(45) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`rrid` INT(11) NOT NULL DEFAULT '0',
	PRIMARY KEY (`likeTime`) USING BTREE,
	INDEX `FK__sleepBoardRereply_2` (`rrid`) USING BTREE,
	CONSTRAINT `FK__sleepBoardRereply_2` FOREIGN KEY (`rrid`) REFERENCES `sleepwell`.`sleepBoardRereply` (`rrid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
