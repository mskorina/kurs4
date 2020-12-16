import sqlalchemy as db
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Boolean, Column, Integer, String, Numeric, create_engine,ForeignKey, Date, or_, and_,func
Base = declarative_base()

#таблица с пользователями
class Users(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String, unique=True, index=True)
    passhash = Column(String, nullable=False)
    blocked=Column(Boolean, default="False")

#таблица с пользователями администраторами
class Admins(Base):
    __tablename__ = "admins"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    login = Column(String, nullable=False)
    passhash = Column(String, nullable=False)
    level = Column(Integer, nullable=False)

#таблица с конфигурацией
class SystemConfig(Base):
    __tablename__ = "systemconfig"
    id = Column(Integer, primary_key=True, index=True)
    enabled = Column(Boolean, default="True")
    newregistration = Column(Boolean, default="True")
    weblogin = Column(Boolean,default="True")
    mobilesync = Column(Boolean,default="True")

#таблица с кошельками
class Wallets(Base):
    __tablename__ = "wallets"
    id = Column(Integer, primary_key=True, index=True)
    userid = Column(Integer,ForeignKey('users.id'),index=True)
    walletid = Column(Integer,nullable=False,unique=True,index=True)
    name = Column(String,nullable=False)
    typemoney=Column(String, nullable=False)
    money=Column(Numeric, nullable=False)
    modifieddate=Column(Integer,nullable=False)

#таблица с кошельками
class Payments(Base):
    __tablename__ = "payments"
    id = Column(Integer, primary_key=True, index=True)
    userid = Column(Integer,ForeignKey('users.id'),index=True)
    walletid = Column(Integer,ForeignKey('wallets.walletid'),index=True)
    paymentid = Column(Integer,nullable=False,index=True)
    type = Column(String,nullable=False)
    summary=Column(String, default=" ")
    money=Column(Numeric, nullable=False)
    date=Column(String, nullable=False)
    modifieddate=Column(Integer,nullable=False)