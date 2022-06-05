CREATE OR REPLACE FUNCTION getAccountAgeDifference(v_id1 IN NUMBER, v_id2 IN NUMBER)
    RETURN NUMBER IS v_difference NUMBER;
    v_date1 DATE;
v_date2 DATE;
BEGIN
    SELECT CREATION_DATE INTO v_date1 FROM USERS WHERE ID = v_id1;
    SELECT CREATION_DATE INTO v_date2 FROM USERS WHERE ID = v_id2;
    IF v_date1 > v_date2 THEN
    v_difference := v_date1 - v_date2;
    ELSE
    v_difference := v_date2 - v_date1;
    RETURN v_difference;
end;
