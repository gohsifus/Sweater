insert into usr (id, user_name, password, active)
    values(1, 'admin', 'admin', true);

insert into user_role (user_id, roles)
    values(1, 'ADMIN');