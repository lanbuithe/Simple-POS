select * from databasechangelog;
delete from databasechangelog where ID='2';
update databasechangelog set MD5SUM='7:ebf30945ed2b43c977353044ee595de2' where ID='00000000000001';

ALTER TABLE itemcategory 
CHANGE COLUMN created_date created_date TIMESTAMP NOT NULL DEFAULT now(),
CHANGE COLUMN last_modified_date last_modified_date TIMESTAMP NULL;

update itemcategory set last_modified_date=now();
update item set last_modified_date=now();
update tableno set last_modified_date=now();
update orderno set last_modified_date=now();
update orderdetail set last_modified_date=now();

select * from orderno;
select * from orderdetail;

delete from orderdetail;
delete from orderno;

GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY 'root' WITH GRANT OPTION;

select * from orderdetail a join orderno b on a.orderno_id = b.id where b.status='PAYMENT';

select sum(a.amount) from orderdetail a join orderno b on a.orderno_id = b.id where b.status='PAYMENT';