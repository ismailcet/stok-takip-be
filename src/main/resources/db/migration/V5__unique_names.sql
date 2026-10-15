CREATE UNIQUE INDEX uq_companies_name_ci ON companies (lower(btrim(name)));
CREATE UNIQUE INDEX uq_cabinets_name_ci ON cabinets (lower(btrim(name)));