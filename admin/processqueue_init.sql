-- The processqueue table

CREATE TABLE processqueue(
       queueID INTEGER PRIMARY KEY,
       fedorahandle VARCHAR(100),
       processing CHAR(1) CHECK (processing IN ( 'Y', 'N' ))
);

CREATE SEQUENCE processqueue_sequence
       MAXVALUE 1000000000
       NO CYCLE;

-- Returns a resultset of non processing posts, which where updated to
-- processing = 'Y' during retrieval.

CREATE OR REPLACE FUNCTION get_all_posts() RETURNS SETOF processqueue AS
$BODY$
DECLARE
    r processqueue%rowtype;
BEGIN
    FOR r IN SELECT * FROM processqueue
    WHERE processing = 'N' ORDER BY queueid LOOP
        UPDATE processqueue
        SET processing = 'Y'
        WHERE queueid = r.queueid;
        RETURN NEXT r;
    END LOOP;
    RETURN;
END
$BODY$
LANGUAGE 'plpgsql';


-- Returns a resultset of non processing posts, which where updated to
-- processing = 'Y' during retrieval. the integer argument is the max
-- resultsize 
-- 
-- @param max  the maximum resultsetsize

CREATE OR REPLACE FUNCTION get_posts( integer ) RETURNS SETOF processqueue AS
$BODY$
DECLARE
    max ALIAS FOR $1;
    r processqueue%rowtype;
BEGIN
    FOR r IN SELECT * FROM processqueue
    WHERE processing = 'N' ORDER BY queueid LIMIT max LOOP
        UPDATE processqueue
        SET processing = 'Y'
        WHERE queueid = r.queueid;
        RETURN NEXT r;
    END LOOP;
    RETURN;
END
$BODY$
LANGUAGE 'plpgsql';


