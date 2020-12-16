
from fastapi import APIRouter, Depends,Header
from sqlalchemy.orm import Session
import jwt
from core.utils import *
from starlette.requests import Request
from starlette.responses import Response
from core.dbtables import *
from . models import *



router = APIRouter()


#########################################################################
#                           Account managment
#########################################################################

#метод регистрации
@router.post("/register")
async def singup(item: Register,db: Session=Depends(get_db)):
    parts = item.email.split('@')
    # Проверка валидности почты
    if ((len(parts) != 2) or (len(parts[0]) == 0) or (len(parts[0]) > 64) or (len(parts[1]) == 0) or (len(parts[1]) > 255) or not ('.' in parts[1])):
        return {'account':"email error"}
    #проверка пароля
    if (len(item.password)<6):
        return {"account":"password error"}
    result = db.query(Users).filter(Users.email == item.email).first()
    if result:
        return {'account':'email_busy'}
    #добавление в базу данных с генерацией хеша пароля
    db.execute("INSERT INTO users(email,passhash) VALUES('"+item.email+"',crypt('"+item.password+"', gen_salt('bf')))")
    db.commit()
    return {'account':'OK'}

#метод авторизации
@router.post("/auth")
async def singin(item: Auth,db: Session=Depends(get_db)):
    #поиск пользователя в базе данных 
    usercheck=db.query(Users).filter(Users.email==item.email)
    #если существует то проверяем пароль и после генерируем токен
    if usercheck:
        result =db.execute('SELECT passhash = crypt(\''+item.password+'\', passhash),passhash,id FROM users where email=\''+item.email+'\'').fetchone()
        if (result!=None and result[0]==True):
            return {'token':gettoken(result[2],result[1])}
    return {'token':'error_password_or_user'}

#метод замены пароля
@router.post("/changepassword")
async def changep(item: Changepass,db: Session=Depends(get_db)):
    usercheck=checktoken(db,item.token)
    if usercheck["account"]==-3:
        return {'user':"badtoken"}
    if usercheck["account"]==1:
        result =db.execute("update users set passhash = crypt('"+item.passwordnew+"', gen_salt('bf')) where id="+str(usercheck['id']))
        db.commit()
        return {'user':"OK"}
    if usercheck["account"]==0:
        return {'user':"password"}
    if usercheck["account"]==-1:
        return {'user':"none"}
    return {'user':'blocked'}

#метод проверки токена
@router.post("/checktoken", response_model=StepServer)
def checktoke(item: StepUser,db: Session=Depends(get_db)):
    usercheck=checktoken(db,item.token)
    if usercheck["account"]!=1:
        itemserver=StepServer()
        itemserver.status="FAILURE"
        return itemserver
    # itemserver.status="OK"
    return sync1(db,usercheck["id"],item)

#########################################################################
#                           Sync managment
#########################################################################

#первый этап синхронизации
@router.post("/syncstep1", response_model=StepServer)
def syncsp1(item: StepUser,db: Session=Depends(get_db)):
    usercheck=checktoken(db,item.token)
    if usercheck["account"]!=1:
        itemserver=StepServer()
        itemserver.status="FAILURE"
        return itemserver
    return sync1(db,usercheck["id"],item)

#второй этап синхронизации
@router.post("/syncstep2", response_model=StepServer2)
def syncsp2(item: StepUser2,db: Session=Depends(get_db)):
    usercheck=checktoken(db,item.token)    
    if usercheck["account"]!=1:
        itemserver=StepServer2()
        itemserver.status="FAILURE"
        return itemserver
    return sync2(db,usercheck["id"],item)

#метод сброса данных пользователя
@router.post("/syncdrop",response_model=StepServer2)
def syncdr(item: TokenJWT,db: Session=Depends(get_db)):
    usercheck=checktoken(db,item.token)    
    if usercheck["account"]!=1:
        itemserver=StepServer2()
        itemserver.status="FAILURE"
        return itemserver
    return syncdrop(db,usercheck["id"])

#обрабатывает соап запрос формирует соап ответ
@router.post("/soapcheck")
async def process_item(item: ItemXML = Depends(XmlBody(ItemXML)), header: str = Header(None)):
    return XmlResponse(item)


######################################################################
#                               WEB
######################################################################
#метод возращает счета и платежи пользователя в web
@router.post("/web/data",response_model=DataWeb)
def webgetdata(item: TokenJWT,db: Session=Depends(get_db)):
    #проверка пользователя
    usercheck=checktoken(db,item.token)    
    if usercheck["account"]!=1:
        data=DataWeb()
        data.status="FAILURE"
        return data
    return webdata(db,usercheck["id"])



#####
#test work API#
@router.get("/")
def read_root():
    return {"Hello": "World"}

@router.get("/test")
def read_root1():
    return Response("OK", status_code=200)
