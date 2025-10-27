from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
from datetime import datetime
import asyncpg

app = FastAPI(title="Kallis Sales API")

DB_DSN = "postgresql://kallis:secret@db:5432/kallisdb"

async def get_conn():
    return await asyncpg.connect(DB_DSN)

class Sale(BaseModel):
    id: int | None = None
    customer: str
    total: float
    time: datetime

@app.get("/sales", response_model=List[Sale])
async def get_sales():
    conn = await get_conn()
    rows = await conn.fetch("SELECT id, customer, total, time FROM sales ORDER BY time DESC")
    await conn.close()
    return [Sale(**dict(r)) for r in rows]

@app.post("/sales", response_model=Sale)
async def add_sale(sale: Sale):
    conn = await get_conn()
    row = await conn.fetchrow(
        "INSERT INTO sales(customer,total,time) VALUES($1,$2,$3) RETURNING id",
        sale.customer, sale.total, sale.time
    )
    await conn.close()
    sale.id = row["id"]
    return sale

