@echo off
echo Testing External API Integration
echo ================================
echo.

echo Sending appointment reminders via external API...
echo.
curl -X POST http://localhost:8080/api/send-reminders
echo.
echo.

echo Test completed.
pause