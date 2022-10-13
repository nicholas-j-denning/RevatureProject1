-- One record for type of user account
CREATE TABLE user_role(user_role_type VARCHAR PRIMARY KEY);

INSERT INTO user_role VALUES('Employee');
INSERT INTO user_role VALUES('Finance Manager');

-- One record per user account
CREATE TABLE account(
    account_username VARCHAR PRIMARY KEY,
    account_password_hash VARCHAR NOT NULL,
    account_legal_name VARCHAR NOT NULL,
    account_role VARCHAR NOT NULL REFERENCES user_role(user_role_type)
);

-- One record per type of ticket status
CREATE TABLE ticket_status(ticket_status_type VARCHAR PRIMARY KEY);

INSERT INTO ticket_status VALUES('Pending');
INSERT INTO ticket_status VALUES('Approved');
INSERT INTO ticket_status VALUES('Denied');

-- One record per ticket
CREATE TABLE ticket(
    ticket_id SERIAL PRIMARY KEY,
    ticket_username VARCHAR NOT NULL REFERENCES account(account_username),
    ticket_amount NUMERIC NOT NULL,
    ticket_description VARCHAR NOT NULL,
    ticket_status VARCHAR NOT NULL REFERENCES ticket_status(ticket_status_type)
);