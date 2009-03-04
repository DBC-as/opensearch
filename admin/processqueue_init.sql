-- The processqueue table

CREATE TABLE processqueue(
       queueID INTEGER PRIMARY KEY,
       fedorahandle VARCHAR(100),
       processing CHAR(1) CHECK (processing IN ( 'Y', 'N' ))
);

CREATE SEQUENCE processqueue_sequence
       MAXVALUE 1000000000
       NO CYCLE;

CREATE OR REPLACE FUNCTION get_all_posts() RETURNS SETOF processqueue AS
$BODY$
DECLARE
    r processqueue%rowtype;
BEGIN
    FOR r IN SELECT * FROM processqueue
    WHERE processing = 'N' LOOP
        UPDATE processqueue
        SET processing = 'Y'
        WHERE queueid = r.queueid;
        RETURN NEXT r;
    END LOOP;
    RETURN;
END
$BODY$
LANGUAGE 'plpgsql';
