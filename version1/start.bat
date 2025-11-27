@echo off

echo 校园二手商品交易系统启动脚本
================================

echo [1] 检查MySQL服务是否运行...
net start | findstr /i "MySQL" > nul
if %errorlevel% neq 0 (
    echo 警告：MySQL服务未运行，请先启动MySQL服务
    pause
    exit /b 1
)

echo [2] 检查数据库是否存在...
mysql -u root -proot -e "use secondhand;" > nul 2>&1
if %errorlevel% neq 0 (
    echo 数据库不存在，正在创建数据库...
    mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS secondhand CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
)

echo [3] 创建必要的目录...
mkdir upload > nul 2>&1

:menu
echo.
echo 请选择要启动的服务：
echo 1. 启动后端服务
2. 启动前端服务
3. 同时启动前后端服务
4. 退出

getChoice:
set /p choice=请输入选择 [1-4]: 
if "%choice%" == "1" goto backend
if "%choice%" == "2" goto frontend
if "%choice%" == "3" goto both
if "%choice%" == "4" exit /b 0
echo 无效的选择，请重新输入
pause
goto getChoice

:backend
echo 启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
echo 后端服务启动中，请等待...
echo 后端服务地址: http://localhost:8080/api
pause
exit /b 0

:frontend
echo 启动前端服务...
cd frontend
call npm install > nul 2>&1
start "前端服务" cmd /k "npm run serve"
echo 前端服务启动中，请等待...
echo 前端服务地址: http://localhost:8081
pause
exit /b 0

:both
echo 同时启动前后端服务...

REM 启动后端服务
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
echo 后端服务启动中...

REM 返回上级目录
cd ..

REM 启动前端服务
cd frontend
call npm install > nul 2>&1
start "前端服务" cmd /k "npm run serve"
echo 前端服务启动中...

echo.
echo 服务启动信息：
echo 后端服务地址: http://localhost:8080/api
echo 前端服务地址: http://localhost:8081
echo 请等待服务完全启动后再访问
pause
exit /b 0