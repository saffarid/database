package database;

import database.Column.TableColumn;
import database.Column.atributes.ForeignKey;

import java.util.Map;

public class RequestBuilder {

    /**
     * Шаблон команды для генерации команды создания новой таблицы.
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     *     <li>Наименование колонок и их атрибутов</li>
     * </ul>
     */
    private static final String CREATE_TEMPLATE = "create table if not exists `%1$s` (\n\t%2$s\n\t)";

    /**
     * Шаблон команды для генерации команды вставки новых строк в таблицу.
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     *     <li>Наименования колонок</li>
     *     <li>Вставляемые строки</li>
     * </ul>
     */
    private static final String INSERT_TEMPLATE = "insert into `%1$s` (\n\t%2$s\n) values (\n\t%3$s)";

    /**
     * Функция формирует запрос к БД на создание таблицы
     */
    public static String create(Table table) {
        StringBuilder res = new StringBuilder("");

        Map<String, TableColumn> columns = table.getColumns();
        var columnCounter = new Object() {
            int count = 0;
            int amount = columns.size();
        };

        columns.keySet().stream().forEach(columnName -> {
            TableColumn column = columns.get(columnName);
            res.append(column.comandForCreate());

            if (columnCounter.count != columnCounter.amount - 1) {
                res.append(", \n\t");
            }
            columnCounter.count += 1;
        });
        res.append(" ");
        if (table.hasUniques()) {
            res.append(", \n\t");
            res.append(table.getConstrainsUnique());
        }
        if (table.hasForeignKeys()) {
            res.append(", \n\t");
            res.append(table.getConstrainsForeignKey());
        }
        return String.format(CREATE_TEMPLATE, table.getName(), res.toString());
    }

    public static String insert(Table table, ContentValues content) {

        StringBuilder columns = new StringBuilder("");
        StringBuilder values = new StringBuilder("");

        //Шаблон наименования колонки
        String strColNameTemplate = "`%1$s`";
        //Шаблон значения если в ячейку таблицы БД добавляется строка
        String strColValTemplate = "\'%1$s\'";

        //Объект используется для удобного определения сколько столбцов в таблице уже пройдено
        var columnCounter = new Object() {
            int count = 0;
            int amount = content.size();
        };

        for (TableColumn column : content.keySet()) {
            Object value = content.get(column);

            if (value == null) continue;

            //Проверяем TableColumn на наличие внешнего ключа
            if (column instanceof ForeignKey && ((ForeignKey) column).hasForeignKey()) {
                //Колонка содержит внешний ключ
                values.append(content.getValueSubrequest((ForeignKey) column, value));
            } else {
                //Колонка не содержит внешний ключ
                if (value instanceof String) {
                    values.append(String.format(strColValTemplate, value));
                } else {
                    values.append(value);
                }
            }
            columns.append(String.format(strColNameTemplate, column.getName()));

            if (columnCounter.count < (columnCounter.amount - 1)) {
                columns.append(", \n\t");
                values.append(", \n\t");
            }
            columnCounter.count += 1;

        }


        return String.format(INSERT_TEMPLATE, table.getName(), columns, values);
    }

    public static String update() {
        StringBuilder update = new StringBuilder();
        return update.toString();
    }

    public static String select() {
        StringBuilder select = new StringBuilder();
        return select.toString();
    }

    public static String remove() {
        StringBuilder remove = new StringBuilder();
        return remove.toString();
    }

}
