@echo off

echo Compiling sorce files
javac -d out -cp "libs/*" bin/*.java

echo Running...
java -cp "libs/*;out" Main bratishkinoff

pause