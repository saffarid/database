package database.Column;

import database.Column.Implements.CustomColumn;
import database.Column.Implements.PrimaryKeyAutoncrement;
import database.Column.Implements.PrimaryKeyCustom;

import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика по созданию новых экземпляров табличных колонок
 */
public abstract class TableColumnFactory {

    private static Map<ColumnTypes, TableColumnCreator> map = new HashMap<>();

    static {
        map.put(ColumnTypes.CUSTOM_COLUMN, CustomColumn::new);
        map.put(ColumnTypes.PRIMARYKEY_AUTOINCREMENT, PrimaryKeyAutoncrement::new);
        map.put(ColumnTypes.PRIMARYKEY_CUSTOM, PrimaryKeyCustom::new);
    }

    /**
     * @param type тип требуемой колонки
     * @return Объект реализующий TableColumn
     * @see ColumnTypes
     * @see TableColumn
     */
    public static TableColumn createColumn(ColumnTypes type) {
        return map.get(type).create();
    }

    @FunctionalInterface
    interface TableColumnCreator {
        TableColumn create();
    }

}
