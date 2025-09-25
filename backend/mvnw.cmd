@ECHO OFF
SET MVNW_CMD=mvn
WHERE %MVNW_CMD% >NUL 2>&1
IF %ERRORLEVEL% NEQ 0 (
  ECHO Apache Maven is required to run this project. Please install Maven or replace mvnw with the official wrapper.
  EXIT /B 1
)
%MVNW_CMD% %*
