
CREATE TABLE IF NOT EXISTS `account`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `address`    varchar(255) DEFAULT NULL,
    `admin`      bit(1)       DEFAULT NULL,
    `deposit`    int(11)      DEFAULT '0',
    `email`      varchar(255) DEFAULT NULL,
    `first_name` varchar(255) DEFAULT NULL,
    `last_name`  varchar(255) DEFAULT NULL,
    `login`      varchar(255) DEFAULT NULL,
    `password`   varchar(255) DEFAULT NULL,
    `patronymic` varchar(255) DEFAULT NULL,
    `phone`      varchar(11) DEFAULT NULL,
    `position`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `k_login` (`login`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `product`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `count`   int(11)      NOT NULL DEFAULT '1',
    `deleted` tinyint(1)   NOT NULL DEFAULT '0',
    `name`    varchar(255) NOT NULL,
    `price`   int(11)      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `basket`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `count`      int(11)    NOT NULL DEFAULT '0',
    `account_id` bigint(20)          DEFAULT NULL,
    `product_id` bigint(20)          DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `k_account` (`account_id`),
    KEY `k_product` (`product_id`),
    CONSTRAINT `fk_basket_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_basket_account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `category`
(
    `id`        bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`      varchar(255) NOT NULL,
    `parent_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY  `k_name` (`name`),
    KEY         `k_parent` (`parent_id`),
    CONSTRAINT `fk_category_id` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `productcategory`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `category_id` bigint(20) DEFAULT NULL,
    `product_id`  bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `k_category` (`category_id`),
    KEY `k_product` (`product_id`),
    CONSTRAINT `fk_productcategory_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_productcategory_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `purchase`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `count`      int(11)    NOT NULL,
    `date`       datetime   NOT NULL,
    `price`      int(11)    NOT NULL,
    `account_id` bigint(20) DEFAULT NULL,
    `product_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `k_account` (`account_id`),
    KEY `k_product` (`product_id`),
    CONSTRAINT `fk_purchase_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_purchase_account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `session`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `uuid`       varchar(36) DEFAULT NULL,
    `account_id` bigint(20)   DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY  `k_uuid` (`uuid`),
    KEY         `k_account` (`account_id`),
    CONSTRAINT `fk_session_account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8;