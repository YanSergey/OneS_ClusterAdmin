# пока что каждый командный файл должен содержать строку chcp 65001

@echo off
chcp 65001
#chcp
ping 127.0.0.1 -n 20
echo server: %serverName%:%managerPort%
echo infobase: %infobase%
echo end
