@echo off
echo === Mengluo Calculator Build Script ===
pip install pyinstaller -q
pyinstaller --onefile --windowed --name Calculator calculator.py
echo.
echo Build complete! Check dist\Calculator.exe
echo.
pause
