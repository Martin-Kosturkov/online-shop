create table customer_order (
    id uuid primary key,
    status varchar not null,
    last_updated_at timestamp not null
);

create table product (
    id bigserial primary key,
    name varchar not null,
    quantity integer not null check(quantity >= 0),
    order_id uuid not null references customer_order(id) on delete cascade
);