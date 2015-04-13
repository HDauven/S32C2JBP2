DROP TABLE PERSONEN CASCADE CONSTRAINTS;
--DROP TABLE GEZINNEN CASCADE CONSTRAINTS;

CREATE TABLE PERSONEN (
persoonsNummer      number            primary key,
achternaam          varchar2(50)      not null,
voornamen           varchar2(100)     not null,
tussenvoegsel       varchar2(50)      not null,
geboortedatum       date              not null,
geboorteplaats      varchar2(50)      not null,
geslacht            varchar2(50)      not null,
ouders              number            ,

FOREIGN KEY (ouders) REFERENCES GEZINNEN(gezinsNummer)
);

CREATE TABLE GEZINNEN (
gezinsNummer        number            primary key,
ouder1              number            not null,
ouder2              number            ,
huwelijksdatum      date              ,
scheidingsdatum     date              ,

FOREIGN KEY (ouder1) REFERENCES PERSONEN(persoonsNummer),
FOREIGN KEY (ouder2) REFERENCES PERSONEN(persoonsNummer)
);

