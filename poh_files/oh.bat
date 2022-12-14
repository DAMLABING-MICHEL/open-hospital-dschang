@echo off
set OH_PATH=%~dps0

cd /d %OH_PATH%\mysql\bin
xcopy my.ori my.cnf /y
Xchang32.exe my.cnf "OH_PATH_SUBSTITUTE" "%OH_PATH%"
Xchang32.exe my.cnf "^x5c" "^x2f"

start /b /min %OH_PATH%mysql\bin\mysqld --defaults-file=%OH_PATH%mysql\bin\my.cnf --standalone --console

set OH_HOME=%OH_PATH%oh

set OH_BIN=%OH_HOME%\bin
set OH_LIB=%OH_HOME%\lib
set OH_BUNDLE=%OH_HOME%\bundle

set CLASSPATH=%OH_BIN%

SETLOCAL ENABLEDELAYEDEXPANSION


FOR %%A IN (%OH_LIB%\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\h8\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\dicom\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\dicom\dcm4che\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

FOR %%A IN (%OH_LIB%\dicom\jai\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

set CLASSPATH=%CLASSPATH%;%OH_BIN%\OH.jar
set CLASSPATH=%CLASSPATH%;%OH_BUNDLE%

cd /d %OH_PATH%oh\
%OH_PATH%jvm\bin\java.exe -showversion -Djava.library.path=%OH_PATH%oh\lib\native\Windows -cp %CLASSPATH% org.isf.menu.gui.Menu
start /b %OH_PATH%mysql\bin\mysqladmin --user=root --password= --port=3307 shutdown
exit