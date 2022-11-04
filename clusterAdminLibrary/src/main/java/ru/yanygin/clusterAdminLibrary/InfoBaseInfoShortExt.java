package ru.yanygin.clusterAdminLibrary;

import com._1c.v8.ibis.admin.IInfoBaseInfoShort;
import com._1c.v8.ibis.admin.InfoBaseInfoShort;
import java.text.Collator;
import java.util.Locale;
import org.eclipse.swt.graphics.Image;

/** Класс расширяет возможности InfoBaseInfoShort. */
public class InfoBaseInfoShortExt extends InfoBaseInfoShort
    implements Comparable<InfoBaseInfoShortExt> {

  private static final String DEFAULT_ICON_FILENAME = "infobase.png"; //$NON-NLS-1$
  private static final String FAVORITE_ICON_FILENAME = "infobase_favorite.png"; //$NON-NLS-1$
  private static Image defaultIcon = Helper.getImage(DEFAULT_ICON_FILENAME);
  private static Image favoriteIcon = Helper.getImage(FAVORITE_ICON_FILENAME);

  static final String INFOBASE_ID = "InfobaseId"; //$NON-NLS-1$
  static final String INFOBASE_INFO_SHORT_EXT = "InfobaseInfoShortExt"; //$NON-NLS-1$

  int index = 0;
  boolean isFavorite = false;
  Image currentIcon; // protected

  /**
   * Возвращает порядковый номер инфобазы из кластера.
   *
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * Устанавливает порядковый номер инфобазы из кластера.
   *
   * @param index - индекс
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * Возвращает находится ли инфобаза в списке избранных.
   *
   * @return the isFavorite
   */
  public boolean isFavorite() {
    return isFavorite;
  }

  /**
   * Возвращает иконку инфобазы.
   *
   * @return Image currentIcon
   */
  public Image getIcon() {
    return currentIcon;
  }

  /**
   * Устанавливает признак избранной инфобазы.
   *
   * @param isFavorite - текущее состояние
   */
  public void setFavoriteState(boolean isFavorite) {
    this.isFavorite = isFavorite;
    this.currentIcon = isFavorite ? favoriteIcon : defaultIcon;
  }

  /**
   * Создает класс краткое (расширенное) описание инфобазыю.
   *
   * @param ib - краткое описание инфобазы
   * @param index - порядковый номер инфобазы из кластера
   * @param isFavorite - инфобаза в избранном
   */
  public InfoBaseInfoShortExt(IInfoBaseInfoShort ib, int index, boolean isFavorite) {
    super(ib.getInfoBaseId());
    this.setDescr(ib.getDescr());
    this.setName(ib.getName());
    this.index = index;
    this.isFavorite = isFavorite;
    this.currentIcon = isFavorite ? favoriteIcon : defaultIcon;
  }

  /** Направление сортировки инфобаз. */
  public enum InfobasesSortDirection {
    /** Выключено. */
    DISABLE,
    /** По имени. */
    BY_NAME,
    /** По избранноми и имени. */
    BY_FAVORITES_AND_NAME
  }

  @Override
  public int compareTo(InfoBaseInfoShortExt o) {

    InfobasesSortDirection sortDirection = Config.currentConfig.getInfobasesSortDirection();
    Collator collator = Collator.getInstance(Locale.getDefault());

    int compareResult = 0;
    switch (sortDirection) {
      case BY_NAME:
        compareResult = collator.compare(getName(), o.getName());
        break;
      case BY_FAVORITES_AND_NAME:
        compareResult = Boolean.compare(o.isFavorite, isFavorite());
        if (compareResult == 0) {
          compareResult = collator.compare(getName(), o.getName());
        }
        break;

      case DISABLE:
        compareResult = Integer.compare(index, o.index);
        break;

      default:
        return 0;
    }

    return compareResult;
  }

  /**
   * Получение названия инфобазы для дерева.
   *
   * @return названия инфобазы для дерева
   */
  public String getInfobaseDescription() {
    String infobaseTitle;
    if (Config.currentConfig.isShowInfobaseDescription() && !getDescr().isBlank()) {
      infobaseTitle = String.format("%s (%s)", getName(), getDescr()); //$NON-NLS-1$
    } else {
      infobaseTitle = String.format("%s", getName()); //$NON-NLS-1$
    }
    return infobaseTitle;
  }

}
