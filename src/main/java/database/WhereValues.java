package database;

import database.Column.atributes.AutoincrementColumn;
import database.Column.atributes.ForeignKey;
import database.Column.atributes.PrimaryKeyColumn;
import database.Column.TableColumn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WhereValues
        extends HashMap<TableColumn, Object> {

    @Override
    public String toString() {
        StringBuilder           result           = new StringBuilder("");
        String                  templateFullName = "\"%1$s\".`%2$s`";
        LinkedList<TableColumn> tablesColumns    = new LinkedList<>(this.keySet());

        var columnCounter = new Object() {
            int count = 0;
            int amount = size();
        };

        for (TableColumn tableColumn : this.keySet()) {

            Object value = this.get(tableColumn);
            String whereTemplate = (value instanceof String)
                                   ? ("%1$s=\'%2$s\'")
                                   : ("%1$s=%2$s");
            if (tableColumn instanceof ForeignKey) {
                TableColumn foreignKey = ((ForeignKey) tableColumn).getForeignKey();

                if (foreignKey == null) {continue;}

                String subreqTemplate = "(select `%1$s` from `%2$s` where %3$s)";

                if (foreignKey instanceof AutoincrementColumn) {
                    //Внешний ключ ссылается на автоинкрементируемый первичный ключ внешней таблицы
                    /*Формируем подзапрос следующего вида
                     * select id from fkTable where "все колонки кроме id" = value
                     */
                    //Определяем колонки для вывода информации

                    StringBuilder whereSubrequesResult = new StringBuilder("");

                    Map<String, TableColumn> fkColumns = foreignKey.getTable()
                                                                   .getColumns();

                    var fkColumnCounter = new Object() {
                        int count = 0;
                        int amount = fkColumns.size();
                    };

                    for (String columnName : fkColumns.keySet()) {
                        TableColumn column = fkColumns.get(columnName);
                        if (column instanceof PrimaryKeyColumn) {continue;}

                        whereSubrequesResult.append(String.format(whereTemplate,
                                                                  String.format(templateFullName,
                                                                                column.getTable()
                                                                                      .getName(),
                                                                                column.getName()),
                                                                  value));
                        if (fkColumnCounter.count < fkColumnCounter.amount - 1) {
                            whereSubrequesResult.append(" or \n\t");
                        }
                        fkColumnCounter.count += 1;
                    }

                    String subrequest = String.format(subreqTemplate,
                                                      foreignKey.getName(),
                                                      foreignKey.getTable()
                                                                .getName(),
                                                      whereSubrequesResult.toString());

                    result.append(String.format("`%1$s` = %2$s", tableColumn.getName(), subrequest));
                }
                else {
                    //Внешний ключ ссылается на кастомную колонку внешней таблицы
                    /*Формируем подзапрос следующего вида
                     * select foreignKey from fkTable where fk.id = value*/
                    String subrequest = String.format(subreqTemplate,
                                                      foreignKey.getTable()
                                                                .getPrimaryKeyColumn()
                                                                .getName(),
                                                      foreignKey.getTable()
                                                                .getName(),
                                                      String.format(whereTemplate,
                                                                    String.format(templateFullName,
                                                                                  foreignKey.getTable()
                                                                                            .getName(),
                                                                                  foreignKey.getName()),
                                                                    value));
                    result.append(String.format("`%1$s` = %2$s", tableColumn.getName(), subrequest));
                }
            }
            else {
                //Колонка не содержит внешнего ключа
                if (value instanceof String) {
                    result.append(String.format("`%1$s` = \'%2$s\'", tableColumn.getName(), value.toString()));
                }
                else {
                    result.append(String.format("`%1$s` = %2$s", tableColumn.getName(), value));
                }
            }

            if (columnCounter.count < columnCounter.amount - 1) {
                result.append(" and \n\t");
            }
            columnCounter.count += 1;
        }

        return result.toString();
    }
}
