@echo off
echo Iniciando servicios Docker para Eureka y Gateway...
echo.

echo Construyendo imagenes...
docker-compose build eureka gateway

echo.
echo Iniciando servicios...
docker-compose up -d eureka gateway

echo.
echo Esperando a que los servicios esten listos...
timeout /t 10 /nobreak >nul

echo.
echo Verificando estado de los servicios...
docker-compose ps

echo.
echo URLs de los servicios:
echo - Eureka Dashboard: http://localhost:8761
echo - Gateway Health: http://localhost:8080/actuator/health
echo.

echo ¡Servicios iniciados correctamente!
pause
