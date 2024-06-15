create table if not exists client
(
    client_id        uuid primary key,
    last_name        varchar(30),
    first_name       varchar(30),
    middle_name      varchar(30),
    birth_date       date,
    email            varchar(255),
    gender           varchar(255),
    marital_status   varchar(255),
    dependent_amount integer,
    passport_id      jsonb,
    employment_id    jsonb,
    account_number   varchar(255)
);

create table if not exists credit
(
    credit_id         uuid primary key,
    amount            decimal,
    term              integer,
    monthly_payment   decimal,
    rate              decimal,
    psk               decimal,
    payment_schedule  jsonb,
    insurance_enabled boolean,
    salary_client     jsonb,
    credit_status     varchar(255)
);

create table if not exists statement
(
    statement_id   uuid primary key,
    client_id      uuid,
    credit_id      uuid,
    status         varchar(255),
    creation_date  timestamp,
    applied_offer  jsonb,
    sign_date      timestamp,
    ses_code        varchar(255),
    status_history jsonb
);