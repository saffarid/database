package database;

import database.Column.TableColumn;
import database.Column.atributes.ForeignKey;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequestBuilder {

    /**
     * Шаблон для генерации команды создания новой таблицы.
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     *     <li>Наименование колонок и их атрибутов</li>
     * </ul>
     */
    private static final String CREATE_TEMPLATE = "create table if not exists `%1$s` (\n\t%2$s\n\t)";

    /**
     * Шаблон для генерации команды вставки новых строк в таблицу.
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
     * Шаблон для генерации команды обновления строк в таблице
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     *     <li>Строка с обновляемыми данными</li>
     * </ul>
     */
    private static final String UPDATE_TEMPLATE = "update `%1$s` set %2$s";

    /**
     * Шаблон для генерации команды удаления строк из таблицы
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     * </ul>
     */
    private static final String DELETE_TEMPLATE = "delete from `%1$s`";

    /**
     * Шаблон для генерации команды выборки данных из таблицы
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Список столбцов для вывода</li>
     *     <li>Наименование таблицы</li>
     * </ul>
     */
    private static final String SELECT_TEMPLATE = "select %1$s \nfrom `%2$s`";

    /**
     * Шаблон для генерации определённого условия
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Условие</li>
     * </ul>
     */
    private static final String WHERE_TEMPLATE = " where %1$s";

    /**
     * Функция генерирует команду запроса к БД на создание таблицы
     */
    public static String create(Table table) {
        StringBuilder res = new StringBuilder("");

        Map<String, TableColumn> columns = table.getColumns();
        var columnCounter = new Object() {
            int count = 0;
            int amount = columns.size();
        };

        columns.keySet()
               .stream()
               .forEach(columnName -> {
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

    /**
     * Функция генерирует команду запроса к БД на вставку строк в таблицу
     */
    public static String insert(Table table,
                                ContentValues content) {

        StringBuilder columns = new StringBuilder("");
        StringBuilder values  = new StringBuilder("");

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

            if (value == null) {continue;}

            //Проверяем TableColumn на наличие внешнего ключа
            if (column instanceof ForeignKey && ((ForeignKey) column).hasForeignKey()) {
                //Колонка содержит внешний ключ
                values.append(content.getValueSubrequest((ForeignKey) column, value));
            }
            else {
                //Колонка не содержит внешний ключ
                if (value instanceof String) {
                    values.append(String.format(strColValTemplate, value));
                }
                else {
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

    /**
     * Функция генерирует команду запроса к БД на обновление строк в таблице
     */
    public static String update(Table table,
                                ContentValues values,
                                WhereValues where) {

        String        TEMPLATE_COMAND_UPDATE = "`%1$s` = %2$s\n";
        StringBuilder setSegment                    = new StringBuilder("");

        var columnCounter = new Object() {
            int count = 0;
            int amount = values.size();
        };

        for (TableColumn column : values.keySet()) {

            Object value = values.get(column);

            if (value == null) {continue;}
            if (column instanceof ForeignKey && ((ForeignKey) column).hasForeignKey()) {
                setSegment.append(String.format(TEMPLATE_COMAND_UPDATE,
                                         column.getName(),
                                         values.getValueSubrequest((ForeignKey) column, value)));
            }
            else {
                if (value instanceof String) {
                    setSegment.append(String.format(TEMPLATE_COMAND_UPDATE, column.getName(), "\'" + value.toString() + "\'"));
                }
                else {
                    setSegment.append(String.format(TEMPLATE_COMAND_UPDATE, column.getName(), value.toString()));
                }
            }

            if (columnCounter.count != (columnCounter.amount - 1)) {
                setSegment.append(", ");
            }
            columnCounter.count += 1;
        }

        StringBuilder update = new StringBuilder(String.format(UPDATE_TEMPLATE, table.getName(), values.toStringUpdate()));

        if (where != null) {
            update.append(String.format(WHERE_TEMPLATE, where.toString()));
        }

        return update.toString();
    }

    public static String select() {
        StringBuilder select = new StringBuilder();
        return select.toString();
    }

    /**
     * Функция генерирует команду запроса к БД на удаление строк в таблице
     */
    public static String delete(Table table,
                                WhereValues where) {
        StringBuilder delete = new StringBuilder(String.format(DELETE_TEMPLATE, table.getName()));
        if (where != null) {
            delete.append(String.format(WHERE_TEMPLATE, where.toString()));
        }
        return delete.toString();
    }

}
