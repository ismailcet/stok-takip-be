ALTER TABLE companies
    ADD COLUMN address     VARCHAR(500),
    ADD COLUMN tax_office  VARCHAR(100),
    ADD COLUMN tax_number  VARCHAR(11),
    ADD COLUMN phone       VARCHAR(20);