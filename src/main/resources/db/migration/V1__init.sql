CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(150) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('MASTER', 'APPRENTICE')),
                       pin_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE cabinets (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE companies (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(150) NOT NULL
);

CREATE TABLE parts (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(150) NOT NULL,
                       barcode VARCHAR(100) UNIQUE,
                       product_code VARCHAR(100) UNIQUE,
                       brand VARCHAR(100),
                       category VARCHAR(100),
                       cabinet_id BIGINT NOT NULL REFERENCES cabinets(id),
                       stock_quantity INTEGER NOT NULL DEFAULT 0,
                       CONSTRAINT part_identifier_check CHECK (barcode IS NOT NULL OR product_code IS NOT NULL)
);

CREATE TABLE maintenance_records (
                                     id BIGSERIAL PRIMARY KEY,
                                     company_id BIGINT NOT NULL REFERENCES companies(id),
                                     visit_date DATE NOT NULL,
                                     recorded_by_user_id BIGINT NOT NULL REFERENCES users(id),
                                     work_order_photo BYTEA NOT NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_parts_cabinet ON parts(cabinet_id);
CREATE INDEX idx_maintenance_company ON maintenance_records(company_id);