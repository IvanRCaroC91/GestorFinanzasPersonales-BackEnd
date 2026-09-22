@echo off
echo Deteniendo servicios Docker para Eureka y Gateway...
echo.

docker-compose down eureka gateway

echo.
echo ¡Servicios detenidos correctamente!
pause
