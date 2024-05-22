# OneS_ClusterAdmin

[![Download](https://img.shields.io/github/release/YanSergey/OneS_ClusterAdmin?label=download&style=flat)](https://github.com/YanSergey/OneS_ClusterAdmin/releases/latest)
[![GitHub Releases](https://img.shields.io/github/downloads/YanSergey/OneS_ClusterAdmin/latest/total?style=flat-square)](https://github.com/YanSergey/OneS_ClusterAdmin/releases)
[![GitHub All Releases](https://img.shields.io/github/downloads/YanSergey/OneS_ClusterAdmin/total?style=flat-square)](https://github.com/YanSergey/OneS_ClusterAdmin/releases)

## Утилита для интерактивного администрирования серверов 1С

![Donate](/clusterAdminLibrary/src/main/resources/icons/Rouble.png)
Поддержать проект https://boosty.to/YanSergeyCoder

Статья с описанием возможностей утилиты на Infostart https://infostart.ru/public/1489055/

Разработка ведется в `Eclipse IDE for Java Developers`

Для разработки необходимо:
- установить в локальный репозиторий maven jar-библиотеки от 1С, описаннные в примечании и расположенные в каталоге `clusterAdminLibrary\lib` командой `mvn install:install-file`. Пример команд в файле [clusterAdminLibrary/lib/install-libs.bat](clusterAdminLibrary/lib/install-libs.bat)


Сборка:

- установить в локальный репозиторий библиотеку `clusterAdminLibrary`, выполнив команду `mvn clean install`. Пример в [clusterAdminLibrary/install.bat](clusterAdminLibrary/install.bat)

- выполнить команду `mvn clean package` для сборки приложения для Windows (профиль по-умолчанию), либо с указанием конкретной ОС `mvn clean package -Plinux`, либо для всех трех ОС командой из [clusterAdminApplication/package.bat](clusterAdminApplication/package.bat)

### Примечание:

Для соединения с RAS используется "Программный Java-интерфейс для административного сервера", который опубликован в свободном доступе [на сайте ИТС в разделе "Методическая поддержка для разработчиков и администраторов 1С:Предприятия 8"](https://its.1c.ru/db/metod8dev#content:4985:hdoc).
Есть две версии:
- для 8.3.6 - 8.3.10
- для 8.3.11+

В утилите используется версия для 8.3.11+, которая успешно работает с платформой 8.3.10. Платформ версий ниже 8.3.10 в своем распоряжении не имею, а потому работа с ними не тестировалась.
