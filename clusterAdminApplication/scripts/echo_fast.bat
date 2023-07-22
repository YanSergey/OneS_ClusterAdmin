# пока что каждый командный файл должен содержать строку chcp 65001

@echo off
chcp 65001
echo server: %serverName%:%managerPort%
echo password: %password%
echo infobase: %infobase%
echo username: %username%
echo infobase1: %infobase1%
echo end
