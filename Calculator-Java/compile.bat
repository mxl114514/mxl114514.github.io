@echo off
echo === Mengluo Calculator (Java) Build Script ===
echo.
if not exist Calculator.java (
    echo [ERROR] Calculator.java not found!
    pause
    exit /b 1
)
echo [1/3] Compiling...
javac -encoding UTF-8 Calculator.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo [2/3] Creating manifest...
echo Main-Class: Calculator > manifest.txt
echo [3/3] Packaging JAR...
jar cfm Calculator.jar manifest.txt *.class
del manifest.txt
del *.class
echo.
echo Build complete! Run: java -jar Calculator.jar
echo.
pause
