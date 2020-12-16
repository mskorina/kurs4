from pydantic import BaseModel
from typing import List

#модель регистрации
class Register(BaseModel):
    email: str
    password: str
#модель авторизации
class Auth(Register):
    pass
#модель токена
class TokenJWT(BaseModel):
    token: str

#модель замены пароля
class Changepass(TokenJWT):
    passwordnew: str

#модель кошелька
class Wallet(BaseModel):
    walletid:int=None
    name:str=None
    typemoney:str=None
    money:str=None
    modifieddate:int=None

#модель платежа
class Payment(BaseModel):
    paymentid:int=None
    walletid:int=None
    type:str=None
    money:str=None
    summary:str=None
    date:str=None
    modifieddate:int=None

#модель кошелька для первого этапа синхронизации
class Walletwatch(BaseModel):
    id: int=None
    modifieddate: int=None

#модель платежа для первого этапа синхронизации
class Paymentwatch(Walletwatch):
    walletid:int=None

#модель запроса клиента для первого этапа синхронизации
class StepUser(BaseModel):
    token: str
    wallets:List[Walletwatch]=None
    walletsdelete:List[int]
    payments:List[Paymentwatch]=None
    paymentsdelete:List[int]

#модель запроса сервера для первого этапа синхронизации
class StepServer(BaseModel):
    status:str=None
    walletsnew:List[Wallet]=[]
    walletsmodife:List[Wallet]=[]
    walletsdelete:List[int]=[]
    walletsneed:List[int]=[]
    paymentsnew:List[Payment]=[]
    paymentsmodife:List[Payment]=[]
    paymentsdelete:List[int]=[]
    paymentsneed:List[int]=[]
 
#модель запроса клиента для второго этапа синхронизации
class StepUser2(BaseModel):
    token:str
    wallets:List[Wallet]=None
    payments:List[Payment]=None

#модель запроса сервера для второго этапа синхронизации
class StepServer2(BaseModel):
    status:str=None

#модель части соап
class ItemXML(BaseModel):
    valuecheck: int

#модель ответа для веб с данными пользователя
class DataWeb(BaseModel):
    status:str=None
    wallets:List[Wallet]=[]
    payments:List[Payment]=[]