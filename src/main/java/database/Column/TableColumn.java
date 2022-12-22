package database.Column;

import database.Table;

/**
 * Базовая часть описания колонки таблицы базы данных.
 */
public interface TableColumn {

    static TableColumn create(ColumnTypes type) {
        return TableColumnFactory.createColumn(type);
    }

    String getName();

    TableColumn setName(String name) throws UnsupportedOperationException;

    DataTypes getType();

    TableColumn setType(DataTypes type) throws UnsupportedOperationException;

    Table getTable();

    TableColumn setTable(Table table);

    String comandForCreate();

}
