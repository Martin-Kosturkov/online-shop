create table product (
    name varchar primary key,
    quantity integer not null check(quantity >= 0)
)