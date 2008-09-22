CREATE TABLE processqueue(
  queueid INTEGER PRIMARY KEY,
  fedorahandle VARCHAR(100),
  itemID VARCHAR(100),
  processing CHAR(1) CHECK (processing IN ( 'Y', 'N' ))
);

CREATE SEQUENCE processqueue_seq
  MAXVALUE 1000000000
  NOCYCLE;

CREATE OR REPLACE 
PROCEDURE proc_prod(fedorahandle OUT VARCHAR, queueid OUT INTEGER, processing OUT VARCHAR, itemID OUT VARCHAR)
IS
  CURSOR proc_cur 
  IS 
    SELECT fedorahandle,itemID,processing, queueid 
    FROM processqueue 
    WHERE queueid = ( SELECT MIN(queueid)
                      FROM processqueue 
                      WHERE processing = 'N' )
                      FOR UPDATE OF processing;
BEGIN
  OPEN proc_cur;
  IF proc_cur%ISOPEN THEN
    FETCH proc_cur into fedorahandle,processing,queueid,itemID;
    IF proc_cur%FOUND THEN
      UPDATE processqueue
      SET processing = 'Y'
      WHERE CURRENT OF proc_cur;
    END IF;
  END IF;
END;
/
