package ru.yanygin.clusterAdminLibrary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ConfigTest {

  @Test
  void testReadConfig() {

    Config simpleConfig = new Config();

    // проверка чтения конфига из текущего каталога
    Config defaultPathConfig = Config.readConfig();
    assertEquals(simpleConfig.getConfigPath(), defaultPathConfig.getConfigPath());
    Config nullConfig = Config.readConfig(null);
    assertEquals(simpleConfig.getConfigPath(), nullConfig.getConfigPath());
    Config blankPathConfig = Config.readConfig("");
    assertEquals(simpleConfig.getConfigPath(), blankPathConfig.getConfigPath());

    // проверка чтения пустого конфига (создание нового)
    // assertEquals(simpleConfig.getLocale(), blankPathConfig.getLocale());
    // assertEquals(simpleConfig.getServers(), blankPathConfig.getServers());

    // проверка чтения несуществующего конфига (создание нового)
    Config notExistConfig = Config.readConfig("src\\test\\resources\\not_exist_config.json");

    assertNotEquals(simpleConfig.getConfigPath(), notExistConfig.getConfigPath());
    assertEquals(simpleConfig.getLocale(), notExistConfig.getLocale());
    assertEquals(simpleConfig.getServers(), notExistConfig.getServers());

    // проверка миграции конфига с версии 0.2.0 на 0.3.0
    Config config020 = Config.readConfig("src\\test\\resources\\config_0.2.0.json");
    Config config030 = Config.readConfig("src\\test\\resources\\config_0.2.0_convert_0.3.0.json");

    config020
        .getServers()
        .forEach(
            (server020Key, server020) -> {
              Server server030 = config030.getServers().get(server020Key);

              // проверка миграции варианта хранения кредов
              assertEquals(
                  server020.getSaveCredentialsVariant(), server030.getSaveCredentialsVariant());

              // проврка миграции кредов агента
              UserPassPair agent020Creds = server020.getAgentCredential();
              UserPassPair agent030Creds = server030.getAgentCredential();
              assertEquals(agent020Creds.getDescription(), agent030Creds.getDescription());
              assertEquals(agent020Creds.getUsername(), agent030Creds.getUsername());
              assertEquals(agent020Creds.getPassword(), agent030Creds.getPassword());

              // проврка миграции кредов кластера
              server020
                  .getAllClustersCredentials()
                  .forEach(
                      (uuid, creds020) -> {
                        UserPassPair creds030 = server030.getClusterCredentials(uuid);
                        assertEquals(creds020.getDescription(), creds030.getDescription());
                        assertEquals(creds020.getUsername(), creds030.getUsername());
                        assertEquals(creds020.getPassword(), creds030.getPassword());
                      });

            });
  }

  @Test
  void testCreateNewServer() {

    Config emptyConfig = new Config();

    Server server = emptyConfig.createNewServer();

    assertEquals("Server", server.getAgentHost());
    assertEquals("1540", server.getAgentPortAsString());
    assertEquals("Server", server.getRasHost());
    assertEquals("1545", server.getRasPortAsString());
  }
}
