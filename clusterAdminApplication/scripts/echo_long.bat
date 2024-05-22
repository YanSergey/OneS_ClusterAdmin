# пока что каждый командный файл должен содержать строку chcp 65001

@echo off
chcp 65001
#chcp
ping 127.0.0.1 -n 20
echo server: %v8serverName%:%v8managerPort%
echo infobase: %v8infobase%
echo end
