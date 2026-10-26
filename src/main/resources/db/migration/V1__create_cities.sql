CREATE TABLE cities (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    CONSTRAINT cities_name_not_blank CHECK (btrim(name) <> '')
);
