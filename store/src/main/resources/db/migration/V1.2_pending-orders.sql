create table pending_order (
    id uuid primary key,
    created_at timestamp not null
);

create table ordered_product (
    id bigserial primary key,
    order_id uuid not null references pending_order (id) on delete cascade,
    product_name varchar not null references product (name),
    quantity integer not null check (quantity >= 1)
)