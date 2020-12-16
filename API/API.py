from fastapi import FastAPI
from pydantic import BaseModel
from sqlalchemy import Boolean, Column, Integer, String, Numeric, create_engine,ForeignKey, Date, or_, and_,func
import sqlalchemy as db
from sqlalchemy.ext.declarative import declarative_base, declared_attr
from sqlalchemy.orm import Session, sessionmaker
from sqlalchemy.sql import select
import sqlalchemy as db
from core.dbtables import *

SQLALCHEMY_DATABASE_URI = "postgresql://API:pass@localhost/walletdb"

engine = create_engine(
    SQLALCHEMY_DATABASE_URI
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base.metadata.create_all(bind=engine)

db_session = SessionLocal()
first_admin = db_session.query(Admins).first()
if not first_admin:
    db_session.execute("insert into admins(name,login,passhash,level) values('admin','g@gmail.com',crypt('wnononowei12', gen_salt('bf')),1)")
    # sus = Admins(name="admin",login="g@gmail.com",passhash="nonormal",level=1)
    # db_session.add(sus)
    db_session.commit()
first_config = db_session.query(Admins).first()
if not first_config:
    db_session.execute("INSERT INTO public.\"systemConfig\" DEFAULT VALUES;")
    db_session.commit()

class Auth(BaseModel):
    username: int
    password: str

app = FastAPI()

@app.get("/")
def read_root():
    return {"Hello": "World"}


@app.get("/items/{item_id}")
def read_item(item_id: int, q: str = None):
    return {"item_id": item_id, "q": q}

@app.post("/Auth")
async def singin(item: Auth):
    return {'username':item.username,"pass":item.password}
