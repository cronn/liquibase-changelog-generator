create type Color as enum ('BLUE','GREEN','RED');

create cast (varchar as Color) with inout as implicit;

create cast (Color as varchar) with inout as implicit;

create type Count as enum ('ONE','THREE','TWO');

create cast (varchar as Count) with inout as implicit;

create cast (Count as varchar) with inout as implicit;

create type Size as enum ('L','M','S','XL','XS');

create cast (varchar as Size) with inout as implicit;

create cast (Size as varchar) with inout as implicit;

create table entity_with_enum1 (
    id bigint not null,
    count Count,
    size Size,
    primary key (id)
);

create table entity_with_enum2 (
    id bigint not null,
    color Color,
    count Count,
    primary key (id)
);

create table other_entity (
    id bigint not null,
    primary key (id)
);

create table test_entity (
    id bigint not null,
    other_id bigint not null,
    description varchar(255),
    name varchar(255) not null unique,
    primary key (id)
);

alter table if exists test_entity
    add constraint FKlfw5k4g68kprhjh5lujkkhi5a
    foreign key (other_id)
    references other_entity;
