SET search_path = money_schema;

-- function for triggers (in transactional etc. tables)
CREATE
OR REPLACE FUNCTION prevent_update_and_delete_fn()
RETURNS trigger AS $$
BEGIN
  RAISE
EXCEPTION 'Modifying or deleting from this table is forbidden';
END;
$$
LANGUAGE plpgsql;

-- function for trigger
CREATE
OR REPLACE FUNCTION updated_at_prevent_update_fn()
RETURNS trigger AS $$
BEGIN
  IF
NEW.updated_at IS DISTINCT FROM OLD.updated_at THEN
    RAISE EXCEPTION 'Cannot modify column ''updated_at'' on this table';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- function for trigger
CREATE
OR REPLACE FUNCTION user_general_balances_created_at_no_update_fn()
RETURNS trigger AS $$
BEGIN
  IF
NEW.created_at IS DISTINCT FROM OLD.created_at THEN
    RAISE EXCEPTION 'Cannot modify column ''created_at'' on ''user_general_balances'' table';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- function for trigger
CREATE
OR REPLACE FUNCTION user_general_balances_update_at_update_fn()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at
:= now();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- function for trigger
CREATE
OR REPLACE FUNCTION user_game_balances_created_at_no_update_fn()
RETURNS trigger AS $$
BEGIN
  IF
NEW.created_at IS DISTINCT FROM OLD.created_at THEN
    RAISE EXCEPTION 'Cannot modify column ''created_at'' on ''user_game_balances'' table';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- function for trigger
CREATE
OR REPLACE FUNCTION user_game_balances_update_at_update_fn()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at
:= now();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;


-- tables:
CREATE TABLE user_general_balances
(
    user_id    UUID PRIMARY KEY,
    balance    NUMERIC(17, 2) NOT NULL DEFAULT 1000
        CONSTRAINT ch_positive_balance CHECK (balance >= 0),
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE TABLE user_game_balances
(
    user_id      UUID PRIMARY KEY,
    game_balance NUMERIC(17, 2) NOT NULL DEFAULT 0
        CONSTRAINT ch_positive_balance CHECK (game_balance >= 0),
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE TABLE general_balance_transactions
(
    transaction_id UUID PRIMARY KEY,
    user_id        UUID           NOT NULL
        CONSTRAINT fk_user_id REFERENCES user_general_balances (user_id)
            ON DELETE CASCADE,
    amount         NUMERIC(17, 2) NOT NULL
        CONSTRAINT ch_amount_different_from_zero CHECK (amount IS DISTINCT FROM 0
) ,
transaction_type VARCHAR(16) NOT NULL
	CONSTRAINT ch_transaction_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
context VARCHAR(255),
created_at TIMESTAMPTZ NOT NULL
	DEFAULT now()
);

CREATE TABLE game_balance_transactions
(
    transaction_id UUID PRIMARY KEY,
    user_id        UUID           NOT NULL
        CONSTRAINT fk_user_id REFERENCES user_general_balances (user_id)
            ON DELETE CASCADE,
    amount         NUMERIC(17, 2) NOT NULL
        CONSTRAINT ch_amount_different_from_zero CHECK (amount IS DISTINCT FROM 0
) ,
transaction_type VARCHAR(16) NOT NULL
	CONSTRAINT ch_transaction_type CHECK (transaction_type IN ('TRANSFER', 'BET', 'DOUBLE_BET', 'BET_CANCEL', 'WIN', 'REFUND')),
context VARCHAR(255),
created_at TIMESTAMPTZ NOT NULL
	DEFAULT now()
);


-- triggers:
CREATE TRIGGER user_general_balances_created_at_no_update_trg
    BEFORE UPDATE
    ON user_general_balances
    FOR EACH ROW EXECUTE FUNCTION user_general_balances_created_at_no_update_fn();

CREATE TRIGGER user_general_balances_update_at_update_trg
    BEFORE UPDATE
    ON user_general_balances
    FOR EACH ROW EXECUTE FUNCTION user_general_balances_update_at_update_fn();

CREATE TRIGGER user_game_balances_created_at_no_update_trg
    BEFORE UPDATE
    ON user_game_balances
    FOR EACH ROW EXECUTE FUNCTION user_game_balances_created_at_no_update_fn();

CREATE TRIGGER user_game_balances_update_at_update_trg
    BEFORE UPDATE
    ON user_game_balances
    FOR EACH ROW EXECUTE FUNCTION user_game_balances_update_at_update_fn();

CREATE TRIGGER general_balance_transactions_protect_from_update_and_delete_trg
    BEFORE UPDATE OR
DELETE
ON general_balance_transactions
FOR EACH ROW
EXECUTE FUNCTION prevent_update_and_delete_fn();

CREATE TRIGGER game_balance_transactions_protect_from_update_and_delete_trg
    BEFORE UPDATE OR
DELETE
ON game_balance_transactions
FOR EACH ROW
EXECUTE FUNCTION prevent_update_and_delete_fn();

