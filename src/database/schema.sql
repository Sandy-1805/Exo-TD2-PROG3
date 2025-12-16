CREATE TYPE ingredient_type AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TABLE ingredient (
    id serial primary key,
    name varchar(100) not null,
    price numeric(10,2) not null,
    category ingredient_type not null,
    id_dish int references dish(id)
);

CREATE TABLE dish (
    id serial primary key,
    name varchar(100) not null,
    type dish_type not null
);