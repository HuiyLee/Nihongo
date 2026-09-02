-- Promote the account registered as "huyle" (by username, or by the local
-- part of its email as a fallback) to ADMIN so it can use the /admin/*
-- content management pages. Matches case-insensitively since usernames are
-- stored as typed at registration.

UPDATE users
SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE lower(username) = 'huyle'
   OR lower(split_part(email, '@', 1)) = 'huyle';
