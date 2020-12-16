from typing import TypeVar, Generic, Type, Any
from starlette.requests import Request
from starlette.responses import Response
from simplexml import dumps, loads
from sqlalchemy.orm import Session
from core.dbtables import *
from pydantic import ValidationError
from core.db import *
import jwt
from jwt import PyJWTError
from v1.models import *

from simplexml import dumps, loads
# from bs4 import BeautifulSoup
from lxml import etree
from lxml.builder import ElementMaker


T = TypeVar("T", bound=BaseModel)
SECRET_KEY = "33d69da0804391e89ad7fb0655f76294a200404337a75c51ff0567fcbdf819ef"
SECRET_KEY512 = "93dd1cbd8a4ec5632f4a2728e09ec40525f7c3e41284d9bc484b615e0a2492a5d608b2a62c959b7d54c691fd2849721de076a11f6fb3fec7049a7163ef384428"
ALGORITHM = "HS256"
ALGORITHM512 = "HS512"

def get_db(request: Request):
    return request.state.db

#генерация токена с помощью JWT
def gettoken(userid:String,passwordhash:String):
    payloadsrc={'passhash':passwordhash}
    token=jwt.encode(payloadsrc,SECRET_KEY,algorithm=ALGORITHM)
    tokenstr=str(token)
    #используя сигнатуру от хеша пароля генерируем конечный JWT
    payloadlvl2={"userid":userid,"token":tokenstr.split('.')[2]}
    token=jwt.encode(payloadlvl2,SECRET_KEY512,algorithm=ALGORITHM512).decode('utf-8')
    tokenstr=str(token)
    return tokenstr

def checktoken(db_session: Session,jwtstr:String):
    # payloadsrc={email:passwordhash}
    try:
        payload=jwt.decode(jwtstr,SECRET_KEY512,algorithm=ALGORITHM512)
    except (PyJWTError, ValidationError):
        #Если сигнатура не верна
        return {"account":-3,"id":0}
    #достаем userid
    userid: str = str(payload.get("userid"))
    if userid is None:
        return {"account":-3,"id":0}
    #проверяем пароль
    result =db_session.execute('SELECT passhash,blocked FROM users where id='+userid).fetchone()
    if result:
        #генерируем jwt от хеша пароля чтобы сравнить сигнатуру с той что нам пришла
        payloadsrc={'passhash':result[0]}
        checktoken=jwt.encode(payloadsrc,SECRET_KEY,algorithm=ALGORITHM)
        checktokenstr=str(checktoken)
        if checktokenstr.split('.')[2]==payload.get("token"):
            #аккаунт заблокирован
            if result[1]==True:
                return {"account":-2,"id":0}
            #иначе корректно
            return {"account":1,"id":userid}
        
        else:
            #пользователь имеет другой пароль
            return {"account":0,"id":0}
    #пользователь не существует
    return {"account":-1,"id":"0"}

#-3 token bad, -2 blocked, -1 none,0-password change,1-correct

def sync1(db: Session,iduser:int, listpack: StepUser):
    stepserver=StepServer()

    ########## Wallet

    walletsfromclient={}
    walletsfromserver={}

    #помещаем в массив объекты которые изменились
    for wallet in listpack.wallets:
        walletsfromclient[wallet.id]=wallet.modifieddate

    #помещаем в массив айди объектов которые нужно удалить на клиенте
    walletsfromserverdb=db.execute('select walletid from wallets where userid='+str(iduser)+' and modifieddate=0').fetchall()
    for wallet in walletsfromserverdb:
        stepserver.walletsdelete.append(wallet[0])
    #помещаем в массив айди объектов которые нужно удалить на сервере
    #( в данном случае не удаляем а помечаем как удалённые с помощью установки даты модификации=0)
    for deletew in listpack.walletsdelete:
        db.execute('update wallets set modifieddate=0 where walletid='+str(deletew)+' and userid='+str(iduser))
    db.commit()
    # from server
    #достаем объекты с сервера с целью найти более новые, измененнные и требуемые
    walletsfromserverdb=db.execute('select walletid,name,typemoney,money,modifieddate from wallets where userid='+str(iduser)+' and modifieddate>0').fetchall()
    for wallet in walletsfromserverdb:
        walletsfromserver[wallet[0]]=wallet[4]
        #если у клиента нет до добавляем в список для отправки на клиент
        if not walletsfromclient.get(wallet[0]):
            wal= Wallet()
            wal.walletid=wallet[0]
            wal.name=wallet[1]
            wal.typemoney=wallet[2]
            wal.money=wallet[3]
            wal.modifieddate=wallet[4]
            stepserver.walletsnew.append(wal)
        #если у клиента старый до добавляем в список для отправки на клиент
        elif walletsfromclient.get(wallet[0])<=wallet[4]:
            wal= Wallet()
            wal.walletid=wallet[0]
            wal.name=wallet[1]
            wal.typemoney=wallet[2]
            wal.money=wallet[3]
            wal.modifieddate=wallet[4]
            stepserver.walletsmodife.append(wal)
        #если у клиента новее до добавляем в список для отправки на сервер во втором цикле синхронизации
        elif walletsfromclient.get(wallet[0])>wallet[4]:
            stepserver.walletsneed.append(wallet[0])
    #если у клиента есть а на сервере нет до добавляем в список для отправки на сервер во втором цикле синхронизации
    for walletid in walletsfromclient:
        if walletsfromserver.get(walletid,-1)==-1 and listpack.walletsdelete.count(walletid)==0 and stepserver.walletsdelete.count(walletid)==0:
            stepserver.walletsneed.append(walletid)
    
    ########## Payment
    paymentsfromclient={}
    paymentsfromserver={}

    #помещаем в массив объекты которые изменились
    for payment in listpack.payments:
        paymentsfromclient[payment.id]=payment.modifieddate

    #помещаем в массив айди объектов которые нужно удалить на клиенте
    paymentsfromserverdb=db.execute('select (payments.paymentid)paymentid from payments join wallets on wallets.walletid=payments.walletid where payments.userid='+str(iduser)+' and (payments.modifieddate=0 or wallets.modifieddate=0)').fetchall()
    for payment in paymentsfromserverdb:
        stepserver.paymentsdelete.append(payment[0])
    #помещаем в массив айди объектов которые нужно удалить на сервере
    #( в данном случае не удаляем а помечаем как удалённые с помощью установки даты модификации=0)
    for deletep in listpack.paymentsdelete:
        db.execute('update payments set modifieddate=0 where paymentid='+str(deletep)+' and userid='+str(iduser))
    db.commit()
    #достаем объекты с сервера с целью найти более новые, измененнные и требуемые
    paymentsfromserverdb=db.execute('select (wallets.walletid)walletid,(payments.paymentid)paymentid,type,(payments.money)money,summary,date,(payments.modifieddate)modifieddate from payments join wallets on wallets.walletid=payments.walletid where payments.userid='+str(iduser)+' and (payments.modifieddate>0 and wallets.modifieddate>0)').fetchall()
    for payment in paymentsfromserverdb:
        paymentsfromserver[payment[1]]=payment[6]
        #если у клиента нет до добавляем в список для отправки на клиент
        if not paymentsfromclient.get(payment[1]):
            pay= Payment()
            pay.walletid=payment[0]
            pay.paymentid=payment[1]
            pay.type=payment[2]
            pay.money=payment[3]
            pay.summary=payment[4]
            pay.date=payment[5]
            pay.modifieddate=payment[6]
            stepserver.paymentsnew.append(pay)
        #если у клиента старый до добавляем в список для отправки на клиент
        elif paymentsfromclient.get(payment[1])<=payment[6]:
            pay= Payment()
            pay.walletid=payment[0]
            pay.paymentid=payment[1]
            pay.type=payment[2]
            pay.money=payment[3]
            pay.summary=payment[4]
            pay.date=payment[5]
            pay.modifieddate=payment[6]
            stepserver.paymentsmodife.append(pay)
        #если у клиента новее до добавляем в список для отправки на сервер во втором цикле синхронизации
        elif walletsfromclient.get(payment[1])>payment[6]:
            stepserver.paymentsneed.append(payment[1])
    #если у клиента есть а на сервере нет до добавляем в список для отправки на сервер во втором цикле синхронизации
    for paymentid in paymentsfromclient:
        if paymentsfromserver.get(paymentid,-1)==-1 and listpack.paymentsdelete.count(paymentid)==0 and stepserver.paymentsdelete.count(paymentid)==0:
            stepserver.paymentsneed.append(paymentid)
    stepserver.status="OK"
    return stepserver

def sync2(db: Session,iduser:int, listpack: StepUser2):
    stepserver=StepServer2()
    stepserver.status="OK"
    # получаем данные от клиента и добавляем на сервер
    for wallet in listpack.wallets:
        checkwalletdb=db.execute('select modifieddate from wallets where userid='+str(iduser)+' and (wallets.walletid='+str(wallet.walletid)+')').fetchone()
        # если нет то добавляем
        if not checkwalletdb:
            db.execute('insert into wallets(userid,walletid,name,typemoney,money,modifieddate) values('+str(iduser)+','+str(wallet.walletid)+',\''+wallet.name+'\',\''+wallet.typemoney+'\','+wallet.money+','+str(wallet.modifieddate)+')')
        # если есть то обновляем
        elif checkwalletdb[0]<wallet.modifieddate:
            db.execute('update wallets set name=\''+wallet.name+'\',typemoney=\''+wallet.typemoney+'\',money='+wallet.money+',modifieddate='+str(wallet.modifieddate)+' where walletid='+str(wallet.walletid)+' and userid='+str(iduser))            
        # иначе объект не корректный
        else:
            stepserver.status="wallet FAIL"
            return stepserver
    db.commit()
    # получаем данные от клиента и добавляем на сервер
    for payment in listpack.payments:
        checkpaymentdb=db.execute('select (wallets.walletid)walletid,(payments.modifieddate)modifieddate from payments join wallets on wallets.walletid=payments.walletid where payments.userid='+str(iduser)+' and payments.paymentid='+str(payment.paymentid)+' and (payments.modifieddate>0 and wallets.modifieddate>0)').fetchone()
        # если нет то добавляем
        if not checkpaymentdb:
            db.execute('insert into payments(userid,walletid,paymentid,type,money,summary,date,modifieddate) values('+str(iduser)+','+str(payment.walletid)+','+str(payment.paymentid)+',\''+payment.type+'\','+payment.money+',\''+payment.summary+'\',\''+payment.date+'\','+str(payment.modifieddate)+')')
        # если кошелёк не совпадает
        elif checkpaymentdb[0]!=payment.walletid:
            db.execute('delete from payments where userid='+str(iduser)+' and paymentid='+str(payment.paymentid))
            db.execute('insert into payments(userid,walletid,paymentid,type,money,summary,date,modifieddate) values('+str(iduser)+','+str(payment.walletid)+','+str(payment.paymentid)+',\''+payment.type+'\','+payment.money+',\''+payment.summary+'\',\''+payment.date+'\','+str(payment.modifieddate)+')')
        # если есть то обновляем
        elif checkpaymentdb[1]<payment.modifieddate:
            db.execute('update payments set type=\''+payment.type+'\',money='+str(payment.money)+',summary=\''+payment.summary+'\',date=\''+payment.date+'\',modifieddate='+str(payment.modifieddate)+' where userid='+str(iduser)+' and paymentid='+str(payment.paymentid))
        # иначе объект не корректный
        else:
            stepserver.status="FAIL"
            return stepserver
    db.commit()
    return stepserver


#удаляем все данные пользователя
def syncdrop(db: Session,iduser:int):
    stepserver=StepServer2()
    stepserver.status="OK"
    db.execute('delete from payments where userid='+str(iduser))
    db.execute('delete from wallets where userid='+str(iduser))
    db.commit()
    return stepserver

#класс хранит стартовые данные в базе данных (не используется)
class Work:
    def v1_work(self,db_session: Session):
        Base.metadata.create_all(bind=engine)
        first_admin = db_session.query(Admins).first()
        if not first_admin:
            db_session.execute("insert into admins(name,login,passhash,level) values('admin','g@gmail.com',crypt('wnononowei12', gen_salt('bf')),1)")
            # sus = Admins(name="admin",login="g@gmail.com",passhash="nonormal",level=1)
            # db_session.add(sus)
            db_session.commit()
        first_config = db_session.query(SystemConfig).first()
        if not first_config:
            db_session.execute("INSERT INTO systemconfig(enabled,newregistration,weblogin,mobilesync) VALUES(true,true,true,true);")
            db_session.commit()
        db_session.commit()
        db_session.close()





#########################################################
######################XML################################
#########################################################
# class XmlResponse(Response):
#     media_type = "text/xml"

#     def render(self, content: Any) -> bytes:
#         return dumps({'response': content}).encode("utf-8")
        # return dumps({'response':content}).encode("utf-8")

#поиск элемента в теле xml
class XmlBody(Generic[T]):
    def __init__(self, model_class: Type[T]):
        self.model_class = model_class

    async def __call__(self, request: Request) -> T:

        body = await request.body()
        print(str(body)[1:])
        dict_data = loads(body)
        if (dict_data.get('soap:Envelope')!=None and dict_data.get('soap:Envelope').get('soap:Body')!=None):
            return int(dict_data['soap:Envelope']['soap:Body'])
        return 0

#генерация кода xml
class XmlResponse(Response):
    media_type = "text/xml"

    def render(self, content: int) -> bytes:
        SOAP_ENV = "http://schemas.xmlsoap.org/soap/envelope/"
        SOAP_ENC = "http://schemas.xmlsoap.org/soap/encoding/"
        XSD = "http://www.w3.org/2001/XMLSchema"
        XSI = "http://www.w3.org/2001/XMLSchema-instance"
        CWMP = "urn:dslforum-org:cwmp-1-2"

        soap_enc = ElementMaker(namespace=SOAP_ENC)
        xsd = ElementMaker(namespace=XSD)
        xsi = ElementMaker(namespace=XSI)
        cwmp = ElementMaker(namespace=CWMP)

        soap = ElementMaker(namespace=SOAP_ENV,
                nsmap={
                       'xsi' : XSI,
                       'soap' : SOAP_ENV,
                       'soap-enc' : SOAP_ENC,
                       'xsd' : XSD,
                       'cwmp' : CWMP})

        SOAP_MUST_UNDERSTAND = '{{{0}}}mustUnderstand'.format(SOAP_ENV)
        MUST_UNDERSTAND = '1'

        body={"valuecheck":content}
        xml = soap.Envelope(
                soap.Body(str(content))
                )
        xml_declaration = """<?xml version="1.0" encoding="utf-8"?>\n""" 
        return etree.tostring(xml, encoding='utf-8' , xml_declaration=True, pretty_print=True)
        




############WEB#################

#достаем и базы данных данные пользователя для передачи в web
def webdata(db: Session,iduser:int):
    data=DataWeb()
    data.status="OK"
    #получаем данные из бд о кошельках
    walletsfromserverdb=db.execute('select walletid,name,typemoney,money,modifieddate from wallets where userid='+str(iduser)+' and modifieddate>0').fetchall()
    for wallet in walletsfromserverdb:
        wal= Wallet()
        wal.walletid=wallet[0]
        wal.name=wallet[1]
        wal.typemoney=wallet[2]
        wal.money=wallet[3]
        wal.modifieddate=wallet[4]
        data.wallets.append(wal)
    #получаем данные из бд о счетах
    paymentsfromserverdb=db.execute('select (wallets.walletid)walletid,(payments.paymentid)paymentid,type,(payments.money)money,summary,date,(payments.modifieddate)modifieddate from payments join wallets on wallets.walletid=payments.walletid where payments.userid='+str(iduser)+' and (payments.modifieddate>0 and wallets.modifieddate>0)').fetchall()
    for payment in paymentsfromserverdb:
        pay= Payment()
        pay.walletid=payment[0]
        pay.paymentid=payment[1]
        pay.type=payment[2]
        pay.money=payment[3]
        pay.summary=payment[4]
        pay.date=payment[5]
        pay.modifieddate=payment[6]
        data.payments.append(pay)
    return data

