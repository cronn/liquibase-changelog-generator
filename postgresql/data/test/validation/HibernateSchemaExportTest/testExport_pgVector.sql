create table entity_with_vector (
    id bigint not null,
    vector_data vector(768),
    primary key (id)
);
