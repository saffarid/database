package database;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Класс является Java-представлением базы данных.
 * Таблицы представляются связным списком
 * */
public abstract class DataBaseSQL {

    /**
     * Список таблиц
     * */
    protected final List<Table> tables = new LinkedList<>();

    public DataBaseSQL() {  }

    /**
     * @param name наименование таблицы.
     * @return Объект класса Table по переданному наименованию таблицы, если объект не найдет возвращается null.
     * @see Table
     * */
    public Table getTable(String name){
        Optional<Table> first = tables
                .stream()
                .filter(table -> table.getName().equals(name))
                .findFirst();
        return first.orElse(null);
    }

    public List<Table> getTables() {
        return tables;
    }
}
