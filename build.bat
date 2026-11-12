@echo off
echo [CineMan] Dang bien dich ma nguon Java...
cd /d %~dp0
if not exist "bin" mkdir "bin"
dir /s /b src\*.java > sources.txt
javac -cp "lib/*" -d bin @sources.txt
if %ERRORLEVEL% EQU 0 (
    echo [CineMan] Bien dich thanh cong!
) else (
    echo [CineMan] Co loi xay ra trong qua trinh bien dich!
)
pause
