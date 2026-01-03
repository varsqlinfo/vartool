--pw admin1234!
insert into VT_USER (VIEWID,UID,UNAME,UPW,ACCEPT_YN,BLOCK_YN,DESCRIPTION,LANG,UEMAIL,USER_ROLE,REG_ID,REG_DT,UPD_ID,UPD_DT )
values( '000001','admin','admin','$2a$10$IEOLHyRN.Q7myvgY.4n/6u1TmMvDFEgLU8AMsjHBQJC4CVjNX9OSS','Y','N','admin','ko','admin@vtool.com','ADMIN','000001',current_timestamp,'000001',current_timestamp );

--pw user1!
insert into VT_USER (VIEWID,UID,UNAME,UPW,ACCEPT_YN,BLOCK_YN,DESCRIPTION,LANG,UEMAIL,USER_ROLE,REG_ID,REG_DT,UPD_ID,UPD_DT )
values( '000002','user1','user1','$2a$10$b.noyHRx73pkn4suo3EzFuQEXGJbI9gH7Wrd1laScuw0P9JopsluO','Y','N','admin','ko','user@vtool.com','USER','000001',current_timestamp,'000001',current_timestamp );

--pw user2!
insert into VT_USER (VIEWID,UID,UNAME,UPW,ACCEPT_YN,BLOCK_YN,DESCRIPTION,LANG,UEMAIL,USER_ROLE,REG_ID,REG_DT,UPD_ID,UPD_DT )
values( '000003','user2','user2','$2a$10$9WownlR/exsIdqo/j8IIxuRjfRjeOmt5taU0bjUXji8AS4ujzxGcG','Y','N','admin','ko','user@vtool.com','USER','000001',current_timestamp,'000001',current_timestamp );
