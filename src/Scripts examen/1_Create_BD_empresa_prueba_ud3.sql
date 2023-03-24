USE [master]
GO

IF NOT EXISTS (  SELECT * FROM sys.databases  WHERE name = 'empresa_prueba_ud3')
BEGIN

CREATE DATABASE [empresa_prueba_ud3];
END
