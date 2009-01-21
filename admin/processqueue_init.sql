CREATE TABLE processqueue(
       queueID INTEGER PRIMARY KEY,
       fedorahandle VARCHAR(100),
       itemID VARCHAR(100),
       processing CHAR(1) CHECK (processing IN ( 'Y', 'N' ))
);

CREATE SEQUENCE processqueue_sequence
       MAXVALUE 1000000000
       NO CYCLE;

CREATE OR REPLACE FUNCTION processqueue_pop_post () RETURNS processqueue AS $$
       DECLARE
                rowvar processqueue%ROWTYPE;
                cursor_post refcursor;
       BEGIN
                OPEN cursor_post  FOR SELECT *
                                               FROM processqueue
                                               WHERE queueID = ( SELECT MIN(queueid)
                                               FROM processqueue
                                               WHERE processing = 'N' )
                                               FOR UPDATE OF processqueue;

                FETCH cursor_post INTO rowvar;
                
                IF NOT FOUND THEN
                       RETURN rowvar;
                END IF;
                
                UPDATE processqueue
                SET processing = 'Y'
                WHERE CURRENT OF cursor_post;
                RETURN rowvar;
       END;
$$ LANGUAGE plpgsql;
