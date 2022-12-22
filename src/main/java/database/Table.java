package database;

import database.Column.*;
import database.Column.atributes.ForeignKey;
import database.Column.atributes.NotNull;
import database.Column.atributes.PrimaryKeyColumn;
import database.Column.atributes.Unique;

import java.util.*;


/**
 * Шаблон описания создаваемой/считываемой таблицы
 */
public class Table {

    /**
     * Шаблон строки для формирования ограничения на уникальность колонок.
     * Ограничение принимает имя <b>`table_name`_`column_name`_uniq</b>.
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     *     <li>Наименование колонки</li>
     * </ul>
     */
    private final String UNIQ_CONSTR = "constraint `%1$s_%2$s_uniq` unique(`%2$s`)";
    /**
     * Шаблон строки для формирования ограничения на внешние ключи.
     * Ограничение принимает имя <b>`table_name`_`column_name`_`fk_table_name`_`fk_column_name`_fk</b>.
     * <br>
     * Принимаемые аргументы:
     * <ul style="list-style-type: decimal">
     *     <li>Наименование таблицы</li>
     *     <li>Наименование колонки</li>
     *     <li>Наименование внешней таблицы</li>
     *     <li>Наименование внешней колонки</li>
     * </ul>
     */
    private final String FK_CONSTR = "constraint `%1$s_%2$s_%3$s_%4$s_fk` foreign key (`%2$s`) references `%3$s` (`%4$s`)";

    /**
     * Наименование таблицы
     */
    protected String name;

    /**
     * Тип таблицы
     */
    protected String type;

    /**
     * Содержимое таблицы
     */
    protected final List<ContentValues> contentValues = new LinkedList<>();

    /**
     * Список столбцов
     */
    protected final Map<String, TableColumn> columns = new HashMap<>();

    /**
     * Список столбцов для ограничения UNIQUE
     */
    protected final Map<String, TableColumn> uniqueColumns = new HashMap<>();

    /**
     * Список столбцов для ограничения NOT NULL
     */
    protected final Map<String, TableColumn> notNullColumns = new HashMap<>();

    /**
     * Список для ограничения FOREIGN KEY.
     * Key = колонка ТЕКУЩЕЙ таблицы; value = Колонка на которую делается ссылка.
     */
    protected final Map<TableColumn, TableColumn> fkColumns = new HashMap<>();

    public Table() {
    }

    /**
     * Функция отвечает за добавление новой колонки в описание таблицы.
     *
     * @param column новая колонка.
     * @return true - если колонка добавлена.
     */
    public boolean addColumn(TableColumn column) {
        boolean res = false;
        if (!columns.containsKey(column.getName())) {
            columns.put(column.getName(), column);
            column.setTable(this);
            res = true;
        }

        if (column instanceof NotNull && ((NotNull) column).isNotNull() && !notNullColumns.containsKey(column.getName()))
            notNullColumns.put(column.getName(), column);

        if (column instanceof Unique && ((Unique) column).isUnique() && !uniqueColumns.containsKey(column.getName()))
            uniqueColumns.put(column.getName(), column);

        if (column instanceof ForeignKey && ((ForeignKey) column).getForeignKey() != null)
            fkColumns.put(column, ((ForeignKey) column).getForeignKey());

        return res;
    }

    /**
     * Функция копирует в текущую таблицу информацию из переданной таблицы.
     *
     * @param copyTable копируемая таблица
     */
    public Table copy(Table copyTable) {
        this.name = copyTable.getName();
        this.type = copyTable.getType();

        for (String columnNames : copyTable.getColumns().keySet()) {
            addColumn(copyTable.getColumns().get(columnNames));
        }

        for (ContentValues row : copyTable.getContentValues()) {
            LinkedList<TableColumn> tableColumns = new LinkedList<TableColumn>(row.keySet());
            ContentValues copiedRow = new ContentValues();

            for (TableColumn tableColumn : tableColumns) {
                copiedRow.put(getColumnByName(tableColumn.getName()), row.get(tableColumn));
            }

            contentValues.add(copiedRow);
        }

        return this;
    }

    public Map<String, TableColumn> getColumns() {
        return columns;
    }

    public String getConstrainsForeignKey() {
        StringBuilder res = new StringBuilder("");
        if (!fkColumns.keySet().isEmpty()) {

            var fkColumnCounter = new Object() {
                int count = 0;
                int amount = fkColumns.size();
            };

            for (TableColumn column : fkColumns.keySet()) {
                Table foreignKeyTable = fkColumns.get(column).getTable();
                // Определяем наименование колонки первичного ключа внешней таблицы
                String foreignKeyPrimaryKey = foreignKeyTable.getPrimaryKeyColumn().getName();

                res.append(String.format(
                                FK_CONSTR,
                                name,                       //1
                                column.getName(),           //2
                                foreignKeyTable.getName(),  //3
                                foreignKeyPrimaryKey        //4
                        )
                );

                if (fkColumnCounter.count < fkColumnCounter.amount - 1) {
                    res.append(", \n\t");
                }
                fkColumnCounter.count += 1;
            }
        }
        return res.toString().trim();
    }

    /**
     * @return строка со всеми именовынными ограничениями по уникальности
     */
    public String getConstrainsUnique() {
        StringBuilder res = new StringBuilder("");
        var uniqueColumnCounter = new Object() {
            int count = 0;
            int amount = uniqueColumns.size();
        };
        for (String columnName : uniqueColumns.keySet()) {
            TableColumn column = uniqueColumns.get(columnName);

            res.append(String.format(UNIQ_CONSTR, this.name, column.getName().trim()));

            if (uniqueColumnCounter.count < uniqueColumnCounter.amount - 1) {
                res.append(", \n\t");
            }
            uniqueColumnCounter.count += 1;
        }
        return res.toString().trim();
    }

    public List<ContentValues> getContentValues() {
        return contentValues;
    }

    public String getName() {
        return name;
    }

    /**
     * @return колонка - первичный ключ.
     */
    public PrimaryKeyColumn getPrimaryKeyColumn() {
        PrimaryKeyColumn res = null;
        for (String columnName : columns.keySet()) {
            TableColumn column = columns.get(columnName);
            if (column instanceof PrimaryKeyColumn) {
                res = (PrimaryKeyColumn) column;
                break;
            }
        }
        return res;
    }

    /**
     * Функция ищет колонку по её наименованию
     */
    public TableColumn getColumnByName(String colName) {
        return getColumnByName(this, colName);
    }

    public TableColumn getColumnByName(Table table, String colName) {
        return table.getColumns().get(colName);
    }

    public String getType() {
        return type;
    }

    public boolean hasForeignKeys() {
        return !fkColumns.isEmpty();
    }

    public boolean hasUniques() {
        return !uniqueColumns.isEmpty();
    }

    public boolean removeColumn(TableColumn column) {
        boolean res = false;

        if (columns.containsKey(column)) {
            columns.remove(column);
            res = true;
        }

        if (column instanceof NotNull && ((NotNull) column).isNotNull() && notNullColumns.containsKey(column))
            notNullColumns.remove(column);

        if (column instanceof Unique && ((Unique) column).isUnique() && uniqueColumns.containsKey(column))
            uniqueColumns.remove(column);

        return res;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
