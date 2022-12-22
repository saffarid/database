package database;

import database.Column.atributes.AutoincrementColumn;
import database.Column.atributes.ForeignKey;
import database.Column.atributes.PrimaryKeyColumn;
import database.Column.TableColumn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ContentValues extends HashMap<TableColumn, Object> {

    private final String TEMPLATE_COMAND_INSERT = "(\n\t%1$s\n) values (\n\t%2$s)";
    private final String TEMPLATE_COMAND_UPDATE = "`%1$s`=%2$s\n";

    /**
     * Преобразование объекта в строку при создании таблицы в БД
     */
    public String toStringInsert() {
        StringBuilder colName = new StringBuilder("");
        StringBuilder colValue = new StringBuilder("");

        //Шаблон строки если в ячейку таблицы БД добавляется не строка
        String strColNameTemplate = "`%1$s`";
        //Шаблон строки если в ячейку таблицы БД добавляется строка
        String strColValTemplate = "\'%1$s\'";

        var columnCounter = new Object() {
            int count = 0;
            int amount = size();
        };

        for (TableColumn column : keySet()) {
            Object value = get(column);

            if (value == null) continue;

            //Проверяем TableColumn на наличие внешнего ключа
            if (column instanceof ForeignKey && ((ForeignKey) column).hasForeignKey()) {
                //Колонка содержит внешний ключ
                colValue.append(getValueSubrequest((ForeignKey) column, value));
            } else {
                //Колонка не содержит внешний ключ
                if (value instanceof String) {
                    colValue.append(String.format(strColValTemplate, value));
                } else {
                    colValue.append(value);
                }
            }
            colName.append(String.format(strColNameTemplate, column.getName()));

            if (columnCounter.count < (columnCounter.amount - 1)) {
                colName.append(", \n\t");
                colValue.append(", \n\t");
            }
            columnCounter.count += 1;

        }

        return String.format(TEMPLATE_COMAND_INSERT, colName.toString(), colValue.toString());
    }

    /**
     * Функция формирует строки подзапросов для использования в блоке values запроса Insert
     */
    private String getValueSubrequest(ForeignKey column, Object value) {
        TableColumn foreignKey = column.getForeignKey();
        //Если внешний ключ установлен, объект не будет равен null
        if (foreignKey == null) return null;
        /*
         * Необходимо определить ID строки внешней таблицы, в contentValues содержится значения для пользователя
         * */
        String subRequestTemplate = "(select `%1$s` from `%2$s` where %3$s)";
        String whereTempalte = value instanceof String ? "`%1$s` = \'%2$s\'" : "`%1$s` = %2$s";
        StringBuilder whereBuilder = new StringBuilder("");

        if (foreignKey instanceof AutoincrementColumn) {
            //Обработка случая, когда внешний ключ ссылается на первичный автоинкрементируемый ключ внешней таблицы
            /*
             * Формируем подзапрос select ID from `внешняя таблица` where columnName = columnValue.
             * ID - foreignKey.getName, внешняя таблица - foreignKey.getTable, foreignKey.getName - column, columnValue - get(column)
             */

            var fkClumnCounter = new Object() {
                int count = 0;
                int amount = foreignKey.getTable().getColumns().size();
            };

            /*Формируем блок Where.
             * Логика формирования блока colName1 = value or colName2 = value or colName3 = value.*/
            for (String fkColumnName : foreignKey.getTable().getColumns().keySet()) {

                TableColumn fkColumn = foreignKey.getTable().getColumns().get(fkColumnName);

                if (fkColumn instanceof PrimaryKeyColumn) continue; //Игнорируем PrimaryKey

                whereBuilder.append(
                        String.format(whereTempalte, fkColumn.getName().trim(), value.toString().trim())
                );
                if (fkClumnCounter.count != fkClumnCounter.amount - 1) {
                    whereBuilder.append(" or ");
                }
                fkClumnCounter.count += 1;
            }

            //Формируем строку подзапроса
            return String.format(
                    subRequestTemplate,
                    foreignKey.getName(),   //1
                    foreignKey.getTable().getName(),     //2
                    whereBuilder.toString()
            );

//                colValue.append("(" + subRequest + ")");
        } else {
            //Обработка подзапроса если есть информация, какой столбец интересует внешний ключ
            //foreignKey - представляет столбец в котором ищем информацию для определения идентификатора
            Table tableParent = foreignKey.getTable();
            return String.format(
                    subRequestTemplate,
                    tableParent.getPrimaryKeyColumn().getName(),   //1
                    tableParent.getName(),     //2
                    String.format(whereTempalte, foreignKey.getName().trim(), value.toString().trim())
            );
//                colValue.append("(" + subRequest + ")");
        }
    }

    /**
     * Преобразование объекта в строку при обновлении записи в БД
     */
    public String toStringUpdate() {
        StringBuilder res = new StringBuilder("");

        List<TableColumn> columns = new LinkedList<>(keySet());

        var columnCounter = new Object() {
            int count = 0;
            int amount = size();
        };

        for (TableColumn column : keySet()) {

            Object value = get(column);

            if (value == null) continue;
            if (column instanceof ForeignKey && ((ForeignKey) column).hasForeignKey()) {
                res.append(String.format(TEMPLATE_COMAND_UPDATE, column.getName(), getValueSubrequest((ForeignKey) column, value)));
            } else {
                if (value instanceof String) {
                    res.append(String.format(TEMPLATE_COMAND_UPDATE, column.getName(), "\'" + value.toString() + "\'"));
                } else {
                    res.append(String.format(TEMPLATE_COMAND_UPDATE, column.getName(), value.toString()));
                }
            }

            if (columnCounter.count != (columnCounter.amount - 1)) {
                res.append(", ");
            }
            columnCounter.count += 1;
        }

        return res.toString();
    }
}
