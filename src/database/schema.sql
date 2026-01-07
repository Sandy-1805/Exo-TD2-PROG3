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

ALTER TABLE ingredient
ADD COLUMN required_quantity NYMERIC(10,2);
UPDATE ingredient 
SET required_quantity = 
    CASE name
        WHEN 'Laitue' THEN 1.00
        WHEN 'Tomate' THEN 2.00
        WHEN 'Poulet' THEN 0.50
        ELSE NULL
    END
WHERE name IN ('Laitue', 'Tomate', 'Poulet', 'Chocolat', 'Beurre');