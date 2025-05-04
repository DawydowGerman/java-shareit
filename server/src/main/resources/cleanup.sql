SET CONSTRAINTS ALL DEFERRED;

TRUNCATE TABLE
    bookings,
    comments,
    items,
    requests,
    users
RESTART IDENTITY CASCADE;