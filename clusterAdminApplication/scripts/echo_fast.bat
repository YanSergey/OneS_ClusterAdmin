# пока что каждый командный файл должен содержать строку chcp 65001

@echo off
chcp 65001
echo server: %v8serverName%:%v8managerPort%
echo password: %v8password%
echo infobase: %v8infobase%
echo username: %v8username%
echo infobase1: %infobase1%
echo end
