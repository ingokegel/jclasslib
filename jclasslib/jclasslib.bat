::
:: Startup batch file for the jclasslib bytecode browser
::

@echo off
set DEFAULT_LAF=false

java -Dclasslib.laf.default=%DEFAULT_LAF% -jar jclasslib.jar
