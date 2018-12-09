DROP TABLE IF EXISTS account;

CREATE TABLE account (
  uid VARCHAR(50) NOT NULL,
  ownerName VARCHAR(400) NOT NULL,
  balance DECIMAL(20, 2) NOT NULL,
  CONSTRAINT pk_t_account PRIMARY KEY (uid)
);

INSERT INTO account VALUES ('qwe123', 'George', 1);
