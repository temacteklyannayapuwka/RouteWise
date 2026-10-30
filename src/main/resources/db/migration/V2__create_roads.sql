CREATE TABLE roads (
    id UUID PRIMARY KEY,
    from_city_id UUID NOT NULL REFERENCES cities(id),
    to_city_id UUID NOT NULL REFERENCES cities(id),
    distance_km BIGINT NOT NULL CHECK (distance_km > 0),
    CONSTRAINT roads_cities_different CHECK (from_city_id <> to_city_id),
    CONSTRAINT roads_direction_unique UNIQUE (from_city_id, to_city_id)
);

CREATE INDEX roads_to_city_id_idx ON roads(to_city_id);
