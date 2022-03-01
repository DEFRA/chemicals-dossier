create database [reach-dossier] COLLATE Latin1_General_100_CI_AI_SC
go
exec sp_configure 'contained database authentication', 1
go
create database [reach-dossier-unit-test] COLLATE Latin1_General_100_CI_AI_SC
go
reconfigure
go
alter database [reach-dossier] set containment = partial
go
alter database [reach-dossier-unit-test] set containment = partial
go
