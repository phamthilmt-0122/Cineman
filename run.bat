@echo off
echo [CineMan] Dang khoi chay he thong CineMan...
cd /d %~dp0
java -cp "bin;lib/*" com.cineman.Main 8080
pause
