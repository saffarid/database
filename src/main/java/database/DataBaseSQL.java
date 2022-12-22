package database;

import java.util.*;

public abstract class DataBaseSQL {

    /**
     * Список таблиц
     * */
    protected final Map<String, Table> tables = new HashMap<>();

    public DataBaseSQL() {  }

    /**
     * @param name наименование таблицы.
     * @return Объект класса Table по переданному наименованию таблицы, если объект не найдет возвращается null.
     * @see Table
     * */
    public Table getTable(String name){
        return tables.get(name);
    }

    public Map<String, Table> getTables() {
        return tables;
    }
}
