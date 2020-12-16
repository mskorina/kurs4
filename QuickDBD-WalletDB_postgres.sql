CREATE TABLE "Users" (
    "userId" serial PRIMARY KEY,
 --   "Name" varchar(50)   NOT NULL,
    -- email
    "login" varchar(50)   NOT NULL,
    -- pgcrypto ->  UPDATE ... SET passhash = crypt('new password', gen_salt('bf'));
    "passhash" varchar(128)   NOT NULL,
    -- Check: crypt('entered password', pswhash) FROM ... ;
    -- 1 - on, 0 - off
    "blocked" boolean   NOT NULL);

CREATE TABLE "Tokens" (
    "tokenId" serial PRIMARY KEY,
    "userId" int4   NOT NULL,
    "token" varchar(80)   NOT NULL);

CREATE TABLE "Admins" (
    "adminId" serial PRIMARY KEY,
    "Name" varchar(50)   NOT NULL,
    -- email
    "login" varchar(50)   NOT NULL,
    -- pgcrypto
    "passhash" varchar(128)   NOT NULL,
    -- 0-off account,1-manage users,2-manage all
    "level" int2   NOT NULL);

CREATE TABLE "SystemConfig" (
    "Id" smallserial PRIMARY KEY,
    -- 0-off(no logins,no sync),1-normal mode, 2-developer mode(client not login)
    "Enabled" boolean   NOT NULL,
    -- 1-on,0-off
    "NewRegistration" boolean   NOT NULL,
    -- 1-on,0-off
    "WebLogin" boolean   NOT NULL,
    -- 1-on ,0- off sync
    "MobileSync" boolean   NOT NULL);
CREATE TABLE "Wallets" (
    "Id" serial PRIMARY KEY,
    "userId" int4   NOT NULL,
    -- seconds epoch time (sqlite strftime('%s',DATETIME('now')) )
    "walletId" int4 UNIQUE NOT NULL,
    "name" varchar(50)   NOT NULL,
    "typeMoney" varchar(30)   NOT NULL,
    "Money" money   NOT NULL,
    "modifiedDate" int8   NOT NULL);

CREATE TABLE "Payments" (
    "localId" serial PRIMARY KEY,
    "walletId" int8   NOT NULL,
    -- seconds epoch time (sqlite strftime('%s',DATETIME('now')) )
    "paymentId" int8   NOT NULL,
    "type" varchar(30)   NOT NULL,
    "money" NUMERIC   NOT NULL,
    "summary" varchar(100)   NOT NULL,
    "date" varchar(20)   NOT NULL,
    "modifiedDate" int8   NOT NULL);

ALTER TABLE "Tokens" ADD CONSTRAINT "fk_Tokens_userId" FOREIGN KEY("userId")
REFERENCES "Users" ("userId");

ALTER TABLE "Wallets" ADD CONSTRAINT "fk_Wallets_userId" FOREIGN KEY("userId")
REFERENCES "Users" ("userId");

ALTER TABLE "Payments" ADD CONSTRAINT "fk_Payments_walletId" FOREIGN KEY("walletId")
REFERENCES "Wallets" ("walletId") ON DELETE CASCADE ON UPDATE CASCADE;

CREATE INDEX "idx_Wallets_userId"
ON "Wallets" ("userId");
