create table entity_with_vector (
    id bigint not null,
    vector_data vector(768),
    primary key (id)
);

create index idx_vector_data
    on entity_with_vector (vector_data);
