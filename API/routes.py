
from fastapi import APIRouter
from v1 import v1


routes = APIRouter()

routes.include_router(v1.router, prefix="/v1")