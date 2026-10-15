-- users.role üzerindeki CHECK kısıtını (adı ne olursa olsun) kaldır, ADMIN'i de kabul eden yenisini ekle
DO $$
DECLARE
c text;
BEGIN
SELECT conname INTO c
FROM pg_constraint
WHERE conrelid = 'users'::regclass
      AND contype = 'c'
      AND pg_get_constraintdef(oid) LIKE '%MASTER%';
IF c IS NOT NULL THEN
        EXECUTE format('ALTER TABLE users DROP CONSTRAINT %I', c);
END IF;
END $$;

ALTER TABLE users
    ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'MASTER', 'APPRENTICE'));

-- Yönetici hesabı (PIN'i diğer seed kullanıcılarla aynı, ilk girişten sonra değiştir)
INSERT INTO users (full_name, role, pin_hash) VALUES
    ('Yönetici', 'ADMIN', '$2b$12$4DqAEmyk9e903upF7QA7A.Fpvc.H0tQr2DXXQoCJuulILaDAMIRYC');