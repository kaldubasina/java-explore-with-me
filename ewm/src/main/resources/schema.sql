drop table if exists locations, users, categories, events, requests, compilations, events_compilations cascade;

create table if not exists locations (
    id bigint generated by default as identity,
    lat real not null,
    lon real not null,
    constraint PK_LOC primary key (id)
);

create table if not exists users (
    id bigint generated by default as identity,
    name varchar(250) not null,
    email varchar(254) not null unique,
    constraint PK_USERS primary key (id)
);

create table if not exists categories (
    id bigint generated by default as identity,
    name varchar(50) not null unique,
    constraint PK_CAT primary key (id)
);

create table if not exists events (
    id bigint generated by default as identity,
    annotation varchar(2000) not null,
    description varchar(7000) not null,
    event_date timestamp not null,
    paid boolean not null,
    participant_limit bigint not null,
    request_moderation boolean not null,
    title varchar(120) not null,
    state varchar not null,
    created_on timestamp not null,
    published_on timestamp,
    category_id bigint references categories (id),
    location_id bigint references locations (id) on delete cascade,
    initiator_id bigint references users (id) on delete cascade,
    constraint PK_EVENTS primary key (id)
);

create table if not exists requests (
    id bigint generated by default as identity,
    created timestamp not null,
    status varchar not null,
    event_id bigint references events (id) on delete cascade,
    requester_id bigint references users (id) on delete cascade,
    constraint PK_REQUEST primary key (id),
    constraint UQ_REQUEST unique (event_id, requester_id)
);

create table if not exists compilations (
    id bigint generated by default as identity,
    pinned boolean not null,
    title varchar(50) not null,
    constraint PK_COMP primary key (id)
);

create table if not exists events_compilations (
    compilation_id bigint references compilations (id),
    event_id bigint references events (id),
    constraint PK_EVENTS_COMP primary key (compilation_id, event_id)
);