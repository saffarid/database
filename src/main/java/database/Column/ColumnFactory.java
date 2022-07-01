package database.Column;

import database.Column.Implements.CustomColumn;
import database.Column.Implements.PrimaryKeyAutoncrement;
import database.Column.Implements.PrimaryKeyCustom;

import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика создания новых табличных колонок
 * */
public class ColumnFactory {
    /**
     * Карта табличных колонок.
     * Key - ColumnTypes, Value - ссылка на конструктор соответствующего типа колонки.
     */
    private final static Map<ColumnTypes, ColumnCreate> columns = new HashMap<>();
    /**
     * Начальное наполнение фабрики ссылками на конструкторы
     * */
    static {
        columns.put(ColumnTypes.PRIMARYKEY_AUTOINCREMENT, PrimaryKeyAutoncrement::new);
        columns.put(ColumnTypes.PRIMARYKEY_CUSTOM, PrimaryKeyCustom::new);
        columns.put(ColumnTypes.CUSTOM_COLUMN, CustomColumn::new);
    }
    /**
     * @param type тип колонки из ColumnTypes
     * @see ColumnTypes
     *
     * @return Объект, реализующий интерфейс TableColumn, в соответсвии с переданным аргументом
     * @see TableColumn
     * */
    public static TableColumn getColumn(ColumnTypes type) {
        return columns.get(type).create();
    }
}

@FunctionalInterface
interface ColumnCreate {
    TableColumn create();
}