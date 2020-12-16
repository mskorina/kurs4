# WalletLite
Проект состоит из 3 частей:

Передача данных между всеми компонентами по протоколу HTTPS.
## 1.API

* Генерация токена доступа и хеша пароля
* Управление учетной записи
* Синхронизация кошельков и счетов
* Поддержка веб-части

Работает через uvicorn и FastAPI (python) c использованием СУБД postgress. 

## 2.WEB

* Доступ к кошельку и счетам

Написано на vue.js

## 3.Приложение на Android

* Управление учетной записью
* Управление кошельком и счетами
* Экспорт\Импорт данных
* Экстренный сброс данных
* Защита паролем
* Защита от скриншотов
* Шифрование базы данных
* Статистика по платежам

Написано на Java, с использование sqlchipher для шифрование базы данных.
