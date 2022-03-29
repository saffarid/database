package database.Column;

import database.Column.Implements.CustomColumn;
import database.Column.Implements.PrimaryKeyAutoncrement;
import database.Column.Implements.PrimaryKeyCustom;
import database.Table;

/**
 * Базовая часть описания колонки таблицы базы данных.
 */
public interface TableColumn {

    String getName();
    TableColumn setName(String name) throws UnsupportedOperationException;

    DataTypes getType();
    TableColumn setType(DataTypes type) throws UnsupportedOperationException;

    Table getTable();
    TableColumn setTable(Table table);

    String comandForCreate();

    /**
     * @param type требуемый тип колонки.
     * @return реализация интерфейса.
     * */
    static TableColumn getInstance(ColumnTypes type) {
        switch (type) {
            case CUSTOM_COLUMN:
            default: {
                return new CustomColumn();
            }
            case PRIMARYKEY_CUSTOM: {
                return new PrimaryKeyCustom();
            }
            case PRIMARYKEY_AUTOINCREMENT: {
                return new PrimaryKeyAutoncrement();
            }
        }
    }
}
