-- :name create-user! :i!
-- :doc creates a new user record
INSERT INTO users
(psid)
VALUES (:psid)

-- :name get-user :? :1
-- :doc retrieves a user record given the psid
SELECT * FROM users
WHERE psid = :psid

-- :name delete-user! :! :n
-- :doc deletes a user record given the psid
DELETE FROM users
WHERE psid = :psid


-- :name set-location! :! :n
-- :doc update user location
UPDATE users
SET lat = :lat,
    long = :long,
    last_check = NOW()
WHERE psid = :psid

-- :name get-location :? :1
-- :doc get user location
SELECT lat, long, psid FROM users
WHERE psid = :psid
      AND NOT lat is NULL
      AND NOT long is NULL

-- :name get-locations :?
-- :doc get user locations
SELECT lat, long, psid FROM users


-- :name get-locations-to-check :?
-- :doc get :limit elemtns pocation to check again
SELECT lat, long, aqi, psid FROM users
       WHERE NOT (aqi is NULL OR
                  lat is NULL OR
                  long is NULL OR
                  last_check is NULL)
             AND check_enabled = TRUE
       ORDER BY last_check
       LIMIT :limit


-- :name update-check! :! :n
-- :doc get user locations
UPDATE users
SET last_check = NOW()
WHERE psid = :psid

-- :name disable-check! :! :n
-- :doc get user locations
UPDATE users
SET check_enabled = FALSE
WHERE psid = :psid

-- :name enable-check! :! :n
-- :doc get user locations
UPDATE users
SET check_enabled = TRUE
WHERE psid = :psid

-- :name delete-location! :! :n
-- :doc delete user location
UPDATE users
SET lat = NULL,
    long = NULL
WHERE psid = :psid

-- :name set-aqi! :! :n
-- :doc update user aqi
UPDATE users
SET aqi = :aqi,
    iaqi = :iaqi,
    dominentpol = :dominentpol,
    last_check = NOW()
WHERE psid = :psid

-- :name delete-aqi! :! :n
-- :doc update user aqi
UPDATE users
SET aqi = NULL,
    iaqi = NULL,
    dominentpol = NULL
WHERE psid = :psid

-- :name get-aqi :? :1
-- :doc get user aqi
SELECT aqi, iaqi, dominentpol, psid FROM users
WHERE psid = :psid
      AND NOT aqi is NULL
      AND NOT iaqi is NULL
      AND NOT dominentpol is NULL
