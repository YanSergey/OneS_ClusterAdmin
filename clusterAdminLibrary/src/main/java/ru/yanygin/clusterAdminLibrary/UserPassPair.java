package ru.yanygin.clusterAdminLibrary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import ru.yanygin.clusterAdminLibrary.Server.SaveCredentialsVariant;

/** Пара логин-пароль с доп. информацией. */
public class UserPassPair {

  @SerializedName("Username")
  @Expose
  private String username = "";

  @SerializedName("Password")
  @Expose
  private String password = "";

  @SerializedName("Description")
  @Expose
  private String description = "";

  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  private static final String HIDDEN_PASSWORD = "***"; //$NON-NLS-1$

  /** Создание экземпляра с пустыми логин/паролем. */
  public UserPassPair() {}

  /**
   * Создание экземпляра логин/пароля.
   *
   * @param username - логин
   * @param password - пароль
   */
  public UserPassPair(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Создание экземпляра логин/пароля с доп. информацией.
   *
   * @param username - логин
   * @param password - пароль
   * @param description - описание, доп. информация
   */
  public UserPassPair(String username, String password, String description) {
    this.username = username;
    this.password = password;
    this.description = description;
  }

  /**
   * Получение имени пользователя.
   *
   * @return имя пользователя
   */
  public String getUsername() {
    return username;
  }
  
  /**
   * Получение пароля.
   *
   * @return пароль
   */
  public String getPassword() {
    return password;
  }

  /**
   * Получение пароля.
   *
   * @param showPasswordMode - если истина возвращается пароль, иначе звездочки
   * @return пароль
   */
  public String getPassword(Boolean showPasswordMode) {
    return Boolean.TRUE.equals(showPasswordMode) ? password : HIDDEN_PASSWORD;
  }
  
  /**
   * Получение описания, доп информации.
   *
   * @return описание
   */
  public String getDescription() {
    return description;
  }

  /**
   * Очистка имени пользователя и пароля.
   *
   * @param variant - вариант хранения логин/пароля
   */
  public void clear(SaveCredentialsVariant variant) {

    switch (variant) {
      case DISABLE:
        this.username = EMPTY_STRING;
        this.password = EMPTY_STRING;
        break;
        
      case NAME:
        this.password = EMPTY_STRING;
        break;

      default:
        break;
    }
  }

  /**
   * Установка имени пользователя.
   *
   * @param username - имя пользователя
   */
  public void setUsername(String username) {
    this.username = username;
  }
  
  /**
   * Установка нового паролядля пользователя.
   *
   * @param password - пароль
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Описание, доп информация.
   *
   * @param description - описание
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
