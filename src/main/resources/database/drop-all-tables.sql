DECLARE
  v_table_name VARCHAR2(100);
  v_user_name VARCHAR2(100);
BEGIN
  FOR c IN (SELECT owner, table_name FROM all_tables WHERE owner LIKE 'DEN_%') LOOP
    v_table_name := c.table_name;
    v_user_name := c.owner;
    EXECUTE IMMEDIATE 'DROP TABLE "' || v_user_name || '"."' || v_table_name || '" CASCADE CONSTRAINTS';
  END LOOP;
END;
/