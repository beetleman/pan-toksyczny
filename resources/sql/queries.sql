-- :name create-user! :i!
-- :doc creates a new user record
INSERT INTO users
(psid)
VALUES (:psid)

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

-- :name set-location! :! :n
-- :doc update user location
UPDATE users
SET lat = :lat,
    long = :long
WHERE id = :id

-- :name get-location :? :1
-- :doc get user location
SELECT lat, long, id FROM users
WHERE id = :id
      AND NOT lat is NULL
      AND NOT long is NULL

-- :name get-all-location :?
-- :doc get user location
SELECT lat, long, url FROM users

-- :name delete-location! :! :n
-- :doc delete user location
UPDATE users
SET lat = NULL,
    long = NULL
WHERE id = :id
