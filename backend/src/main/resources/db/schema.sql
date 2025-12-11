CREATE DATABASE IF NOT EXISTS drug_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE drug_management;

CREATE TABLE IF NOT EXISTS sys_user (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    status       TINYINT      NOT NULL,
    create_time  DATETIME     NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS drug_category (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(50)  NOT NULL,
    description  VARCHAR(200),
    create_time  DATETIME     NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS drug_info (
    id              BIGINT        PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100)  NOT NULL,
    category_id     BIGINT        NOT NULL,
    specification   VARCHAR(100),
    unit            VARCHAR(20),
    price           DECIMAL(10,2),
    stock           INT           NOT NULL DEFAULT 0,
    manufacturer    VARCHAR(100),
    production_date DATE,
    expire_date     DATE,
    status          TINYINT       NOT NULL DEFAULT 1,
    create_time     DATETIME      NOT NULL,
    CONSTRAINT fk_drug_category FOREIGN KEY (category_id) REFERENCES drug_category(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS stock_in (
    id           BIGINT        PRIMARY KEY AUTO_INCREMENT,
    drug_id      BIGINT        NOT NULL,
    quantity     INT           NOT NULL,
    price        DECIMAL(10,2),
    supplier     VARCHAR(100),
    batch_no     VARCHAR(50),
    in_time      DATETIME      NOT NULL,
    operator_id  BIGINT,
    remark       VARCHAR(200),
    CONSTRAINT fk_stockin_drug FOREIGN KEY (drug_id) REFERENCES drug_info(id),
    CONSTRAINT fk_stockin_user FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS stock_out (
    id           BIGINT        PRIMARY KEY AUTO_INCREMENT,
    drug_id      BIGINT        NOT NULL,
    quantity     INT           NOT NULL,
    reason       VARCHAR(100),
    out_time     DATETIME      NOT NULL,
    operator_id  BIGINT,
    remark       VARCHAR(200),
    CONSTRAINT fk_stockout_drug FOREIGN KEY (drug_id) REFERENCES drug_info(id),
    CONSTRAINT fk_stockout_user FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
