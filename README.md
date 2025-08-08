# FastCMD [RUS] 

FastCMD - это мощный плагин для Minecraft Spigot, который добавляет виртуальные командные блоки на ваш сервер. Создавайте, управляйте и выполняйте команды с расширенными возможностями планирования и связки, используя простые команды в чате.

## Возможности  

- **Виртуальные командные блоки**: Создавайте командные блоки без физического размещения
- **Разные типы блоков**: Поддержка Обычных, Цепных и Циклических командных блоков
- **Планирование**: Настраиваемые задержки и таймеры отключения
- **Цепочки команд**: Связывайте командные блоки для последовательного выполнения
- **Мультиязычность**: Встроенная поддержка английского и русского языков
- **UTF-8 кодировка**: Корректное отображение в Windows CMD и на всех платформах
- **Гибкий синтаксис**: Современные флаги аргументов с обратной совместимостью
- **Автодополнение**: Умное автодополнение для команд и параметров
- **Система прав**: Детальный контроль доступа к функциям
- **Сохранение данных**: Хранение в YAML-файлах с автоматическим сохранением

## Установка  

1. Скачайте последнюю версию FastCMD.jar из релизов
2. Поместите jar-файл в папку `plugins` вашего сервера
3. Перезапустите сервер
4. Настройте плагин через файл `plugins/FastCMD/config.yml`

## Команды  

Все команды поддерживают как основной вариант `/fastcmd`, так и алиас `/fcmd`.

### Создание командного блока
```
/fcmd create `команда` --name:имя --type:Normal --delay:20 --disable:6000 --connected:другой_блок
```

### Запуск командного блока
```
/fcmd run --name:имя
/fcmd run имя (старый синтаксис)
```

### Удаление командного блока
```
/fcmd delete --name:имя
/fcmd delete имя (старый синтаксис)
```

### Изменение командного блока
```
/fcmd change --name:имя `новая команда` --type:Repeating --delay:40
```

### Остановка командного блока
```
/fcmd stop --name:имя
/fcmd stop имя (старый синтаксис)
```

### Команды языка
```
/fcmd lang en
/fcmd lang ru
/fcmd lang (переключение между языками)
```

### Помощь
```
/fcmd help
```

## Типы командных блоков  

- **Обычный**: Выполняется один раз с указанной задержкой
- **Цепной**: Выполняется и сразу активирует связанный блок
- **Циклический**: Повторяет выполнение пока не будет отключен

## Права  

| Право | Описание | По умолчанию |
|------------|-------------|---------|
| `fastcmd.use` | Доступ к командам FastCMD | op |
| `fastcmd.create` | Создание командных блоков | op |
| `fastcmd.run` | Запуск командных блоков | op |
| `fastcmd.delete` | Удаление командных блоков | op |
| `fastcmd.change` | Изменение командных блоков | op |
| `fastcmd.stop` | Остановка командных блоков | op |
| `fastcmd.help` | Просмотр справки | op |
| `fastcmd.lang` | Смена языка | op |

## Конфигурация  

Плагин создает несколько конфигурационных файлов:

- `config.yml`: Основные настройки, включая язык и автодополнение
- `storage.yml`: Хранение данных командных блоков
- `lang/en.yml`: Английские тексты
- `lang/ru.yml`: Русские тексты

### Основные настройки  

```yaml
# Язык (en/ru)
language: ru

# Вкл/выкл автодополнение
tab-completion: true

# Значения по умолчанию
default-delay: 0
default-disable-time: 0
max-delay: 72000
max-disable-time: 72000
```

## Поддерживаемые версии  

- **Основная поддержка**: Minecraft 1.16.5 (Spigot/Paper)
- **Ожидаемая совместимость**: Minecraft 1.16.5 - 1.20.x
- **Java**: Java 8 или выше

## Сборка из исходников  

1. Клонируйте репозиторий
2. Убедитесь что установлены Java 8+ и Maven
3. Выполните `mvn clean compile package`
4. Собранный jar будет в `target/FastCMD-1.0.0.jar`

## Участие в разработке  

1. Форкните репозиторий
2. Создайте ветку для вашей функции
3. Зафиксируйте изменения
4. Запушьте ветку
5. Создайте Pull Request

## Лицензия  

Проект лицензирован под MIT License - подробности в файле LICENSE.

## Поддержка  

Для помощи, багрепортов или запросов функций создавайте issue на GitHub.

# FastCMD [ENG]

FastCMD is a powerful Minecraft Spigot plugin that brings virtual command blocks to your server. Create, manage, and execute commands with advanced scheduling and chaining capabilities, all through simple chat commands.

## Features

- **Virtual Command Blocks**: Create command blocks without placing physical blocks
- **Multiple Block Types**: Support for Normal, Chain, and Repeating command blocks
- **Advanced Scheduling**: Configurable delays and auto-disable timers
- **Command Chaining**: Connect command blocks for sequential execution
- **Multilingual Support**: Built-in English and Russian localization
- **UTF-8 Encoding**: Proper display support for Windows CMD and all platforms
- **Flexible Syntax**: Modern flag-based arguments with backward compatibility
- **Tab Completion**: Smart auto-completion for all commands and parameters
- **Permission System**: Granular permission control for all features
- **Persistent Storage**: YAML-based storage with automatic saving

## Installation

1. Download the latest FastCMD.jar from releases
2. Place the jar file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin by editing `plugins/FastCMD/config.yml`

## Commands

All commands support both the main command `/fastcmd` and the alias `/fcmd`.

### Create Command Block
```
/fcmd create `command` --name:myblock --type:Normal --delay:20 --disable:6000 --connected:otherblock
```

### Run Command Block
```
/fcmd run --name:myblock
/fcmd run myblock (old syntax)
```

### Delete Command Block
```
/fcmd delete --name:myblock
/fcmd delete myblock (old syntax)
```

### Modify Command Block
```
/fcmd change --name:myblock `new command` --type:Repeating --delay:40
```

### Stop Command Block
```
/fcmd stop --name:myblock
/fcmd stop myblock (old syntax)
```

### Language Commands
```
/fcmd lang en
/fcmd lang ru
/fcmd lang (toggle between languages)
```

### Help
```
/fcmd help
```

## Command Block Types

- **Normal**: Executes once with specified delay
- **Chain**: Executes once and immediately triggers connected block
- **Repeating**: Executes repeatedly until disabled or stopped

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `fastcmd.use` | Access to FastCMD commands | op |
| `fastcmd.create` | Create new command blocks | op |
| `fastcmd.run` | Execute command blocks | op |
| `fastcmd.delete` | Delete command blocks | op |
| `fastcmd.change` | Modify command blocks | op |
| `fastcmd.stop` | Stop running command blocks | op |
| `fastcmd.help` | View help information | op |
| `fastcmd.lang` | Change language settings | op |

## Configuration

The plugin creates several configuration files:

- `config.yml`: Main plugin settings including language and tab completion
- `storage.yml`: Persistent storage for command blocks
- `lang/en.yml`: English localization
- `lang/ru.yml`: Russian localization

### Key Configuration Options

```yaml
# Language setting (en/ru)
language: en

# Enable/disable tab completion
tab-completion: true

# Default values for command blocks
default-delay: 0
default-disable-time: 0
max-delay: 72000
max-disable-time: 72000
```

## Supported Versions

- **Primary Support**: Minecraft 1.16.5 (Spigot/Paper)
- **Expected Compatibility**: Minecraft 1.16.5 - 1.20.x
- **Java**: Java 8 or higher

## Building from Source

1. Clone the repository
2. Ensure Java 8+ and Maven are installed
3. Run `mvn clean compile package`
4. Find the compiled jar in `target/FastCMD-1.0.0.jar`

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, bug reports, or feature requests, please open an issue on GitHub.
