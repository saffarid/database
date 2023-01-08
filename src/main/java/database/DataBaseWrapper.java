package database;

import database.Column.atributes.AutoincrementColumn;
import database.Column.atributes.ForeignKey;
import database.Column.atributes.PrimaryKeyColumn;
import database.Column.TableColumn;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Класс представляет обертку команд для работы с базой данных
 *
 * @version 1.1
 */

public class DataBaseWrapper {

    private final static String PRE_URL_SQLITE = "jdbc:sqlite:";
    private final static String CLASS_NAME_SQLITE = "org.sqlite.JDBC";
    private final static boolean isExecute = true;


    /**
     * Функция добавляет новый столбец в таблицу
     *
     * @param tableName   имя таблицы
     * @param tableColumn Объект описание столбца
     * @param conn        Объект подключения
     */
    public static void addColumn(String tableName,
                                 TableColumn tableColumn,
                                 Connection conn) throws SQLException {

//        String template = "alter table `%1s` add %2s";
//        execute(conn, String.format(template, tableName, tableColumn.toAdd()));
    }

    /**
     * Функция начинает транзакцию
     */
    public static void beginTransaction(Connection conn,
                                        String transactionName) throws SQLException {
        String comand = String.format("begin transaction%1$s", ((transactionName != null) ? (" " + transactionName) : ("")));
        execute(conn, comand);
    }

    /**
     * Функция удаляет все записи из таблицы.
     *
     * @param tableName имя таблицы
     * @param conn      Соединение с БД
     */
    public static void clearTable(Connection conn,
                                  String tableName) throws SQLException {
        String comand = "delete from `%1$s`";
        execute(conn, String.format(comand, tableName));
    }

    /**
     * Функция закрывает подключение к БД
     */
    public static void closeConnection(Connection conn,
                                       String dbName) {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Функция закрепляет транзакцию
     */
    public static void commitTransaction(Connection conn,
                                         String transactionName) throws SQLException {
        String comand = String.format("commit%1$s", ((transactionName != null) ? (" transaction " + transactionName) : ("")));
        execute(conn, comand);
    }

    /**
     * Функция создаёт таблицу в базе данных
     *
     * @param table имя таблицы
     * @param conn      Название базы данных
     */
    public static void createTable(Table table,
                                   Connection conn) throws SQLException {
//        String com = "create table if not exists `%1$s` (\n\t%2$s\n\t)";
//        StringBuilder res = new StringBuilder("");
//        Map<String, TableColumn> columns = table.getColumns();
//
//        var columnCounter = new Object() {
//            int count = 0;
//            int amount = columns.size();
//        };
//
//        columns.keySet().stream().forEach(columnName -> {
//            TableColumn column = columns.get(columnName);
//            res.append(column.comandForCreate());
//
//            if (columnCounter.count != columnCounter.amount - 1) {
//                res.append(", \n\t");
//            }
//            columnCounter.count += 1;
//        });
//        res.append(" ");
//        if (table.hasUniques()) {
//            res.append(", \n\t");
//            res.append(table.getConstrainsUnique());
//        }
//        if (table.hasForeignKeys()) {
//            res.append(", \n\t");
//            res.append(table.getConstrainsForeignKey());
//        }
//
        execute(conn, RequestBuilder.create(table));
    }

    /**
     * Функция удаляет записи из таблицы.
     *
     * @param table таблица.
     * @param where массив, хранящий значение по которому ищем строки для удаления.
     */
    public static void delete(Connection conn,
                              Table table,
                              WhereValues where) throws SQLException {
        String templateComand = "delete from `%1$s`";
        String comand = String.format(templateComand, table.getName());
        if (where != null) {
            String templateWhere = "%1$s where %2$s";
            comand = String.format(templateWhere, comand, where.toString());
        }
        execute(conn, comand);
    }

    /**
     * Функция удаляет столбец из таблицы.
     *
     * @param tableName   имя таблицы
     * @param tableColumn Объект описание колонки
     * @param conn        Соединение с БД
     */
    public static void deleteColumn(String tableName,
                                    TableColumn tableColumn,
                                    Connection conn) throws SQLException {
        String template = "alter table `%1$s` drop `%2$s`";
        execute(conn, String.format(template, tableName, tableColumn.getName()));
    }

    /**
     * Функция удаляет базу данных.
     *
     * @param dbName имя базы данных.
     */
    public static void deleteDataBase(Connection conn,
                                      String dbName) throws SQLException {
        String comand = "drop database " + dbName;
        execute(conn, comand);
    }

    /**
     * Функция удаляет таблицу из базы данных
     *
     * @param table Объект таблицы
     * @param conn  Соединение с БД
     */
    public static void dropTable(Table table, Connection conn) throws SQLException {
        String comand = "drop table `%1$s`";
        execute(conn, String.format(comand, table.getName()));
    }

    /**
     * Функция выполняет команду
     */
    private static void execute(Connection conn,
                                String comand) throws SQLException {
        if (isExecute) {
            Statement statement = conn.createStatement();
            statement.execute(comand);
        }
    }

    /**
     * Функция открывает соединение с базой данных.
     *
     * @param db имя базы данных.
     */
    public static Connection openConnetion(File db) throws ClassNotFoundException, SQLException {

        StringBuilder url = new StringBuilder(PRE_URL_SQLITE);

        if (!db.getParentFile().exists()) db.getParentFile().mkdir();

        url.append(db.getAbsolutePath());
        Connection connection = null;

        Class.forName(CLASS_NAME_SQLITE);
        connection = DriverManager.getConnection(url.toString());
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        return connection;
    }

    /**
     * Функция осуществляет вставку данных в таблицу.
     *
     * @param table имя таблицы в которую добавляют данные
     * @param content добавляемая строка
     * @param conn объект соединения с БД
     */
    public static void insert(Table table,
                              ContentValues content,
                              Connection conn) throws SQLException {
//        String template = "insert into `%1$s` %2$s";
//        execute(conn, String.format(template, table.getName(), content.toStringInsert()));
        execute(conn, RequestBuilder.insert(table, content));
    }

    /**
     * Функция переименовывания таблицы в баще данных
     *
     * @param oldName старое наименование таблицы
     * @param newName новое название таблицы
     * @param conn    Соединение с БД
     */
    public static void renameTable(String oldName,
                                   String newName,
                                   Connection conn) throws SQLException {
        String comand = "alter table `%1$s` rename to `%2$s`";
        execute(conn, String.format(comand, oldName, newName));
    }

    /**
     * Функция переименовывает колонку таблицы
     */
    public static void renameColumn(String tableName,
                                    TableColumn oldTableColumn,
                                    TableColumn newTableColumn,
                                    Connection conn) throws SQLException {
        String template = "alter table `%1$s` rename column '%2$s' to `%3$s`";
        execute(conn, String.format(template, tableName, oldTableColumn.getName().trim(), newTableColumn.getName().trim()));
    }

    /**
     * Функция откатывает транзакцию к предыдущей точке сохранения
     */
    public static void rollbackSavepoint(Connection conn,
                                         String savepointName) throws SQLException {
        String comand = String.format("rollback%1$s", ((savepointName != null) ? (" to savepoint " + savepointName) : ("")));
        execute(conn, comand);
    }

    /**
     * Функция откатывает транзакцию к предыдущей транзакции
     */
    public static void rollbackTransaction(Connection conn,
                                           String transactionName) throws SQLException {
        String comand = String.format("rollback%1$s", ((transactionName != null) ? (" transaction " + transactionName) : ("")));
        execute(conn, comand);
    }

    /**
     * Функция создает точку сохранения транзакции
     */
    public static void savepointTransaction(Connection conn,
                                            String savepointName) throws SQLException {
        String comand = String.format("savepoint%1$s", ((savepointName == null) ? ("") : (" " + savepointName)));
        execute(conn, comand);
    }

    /**
     * Функция осуществляет чтение из базы данных
     *
     * @param table объект-описание таблицы.
     * @param where строка, хранящая имя столбца в по которому определяем какую строку удаляем.
     * @return объект ResultSet
     */
    public static ResultSet select(Table table,
                                   WhereValues where,
                                   Connection conn,
                                   boolean useSubRequest) throws SQLException {
        if (useSubRequest) {
            return selectWithSubrequest(table, where, conn);
        } else {
            return selectWithoutSubrequest(table, where, conn);
        }
    }

    private static ResultSet selectWithoutSubrequest(Table table,
                                                     WhereValues where,
                                                     Connection conn) throws SQLException {

        ResultSet result = null;

        String templateComand = "select %1s \nfrom `%2s`";

        //Определяем столбцы для запроса в БД
        Map<String, TableColumn> columns = table.getColumns();
        StringBuilder column = new StringBuilder("");
        Set<String> columnNames = columns.keySet();

        var columnCounter = new Object(){
            int count = 0;
            int amount = columns.size();
        };

        for (String columnName : columnNames) {
            TableColumn tableColumn = columns.get(columnName);
            column.append(tableColumn.getName());

            if (columnCounter.count < columnCounter.amount - 1) {
                column.append(", ");
            }
            columnCounter.count += 1;
        }

        String comand = String.format(templateComand, column.toString(), table.getName());

        if (where != null) {
            String templateWhere = "%1s where %2s";
            comand = String.format(templateWhere, comand, where.toString());
        }

        Statement statement = conn.createStatement();
        result = statement.executeQuery(comand);

        return result;

    }

    private static ResultSet selectWithSubrequest(Table table,
                                                  WhereValues where,
                                                  Connection conn) throws SQLException {
        ResultSet result = null;

        String templateComand = "select %1$s \nfrom `%2$s`";
        String templateFullName = "\"%1s\".`%2s`";
        String templateAs = "`%1s` as `%2s`";
        String templateSqlName = "`%1s`";
        //Определяем столбцы для запроса в БД
        //Если требуемые колонки не переданы, запрашиваем все колонки из таблицы
        Map<String, TableColumn> columns = table.getColumns();

        StringBuilder column = new StringBuilder("");
        //Проходим по всем колонкам для проверки

        Set<String> columnNames = columns.keySet();

        var columnCounter = new Object() {
            int count = 0;
            int amount = columns.size();
        };

        for (String columnName : columnNames) {
            TableColumn col = columns.get(columnName);
            if (col instanceof ForeignKey) {
                TableColumn foreignKeyColumn = ((ForeignKey)col).getForeignKey();
                StringBuilder subRequest = new StringBuilder("");
                /*
                 * Наименование таблицы формируется следующим образом.
                 * Если наименование текущей таблицы совпадает добавляем к наименованию "sub_"*/
                String selectTemplate = "select %1$s from %2$s where %3$s";
                Table fKTable = new Table().copy(foreignKeyColumn.getTable());
                String pseudoName = fKTable.getName();;
                //Переменная для определения внешнего ключа на саму себя
                boolean foreignKeySelf = fKTable.getName().equals(table.getName());
                if (foreignKeySelf) {
                    pseudoName = "sub_".concat(fKTable.getName());
                }
                TableColumn fkTableColId = fKTable.getPrimaryKeyColumn();
                //Ветка выполняется при наличии ссылки на внешнюю таблицу
                if (foreignKeyColumn instanceof AutoincrementColumn) {
                    //Внешний ключ ссылается на автоинкрементируемый первичный ключ внешней таблицы
                    /*Формируем подзапрос следующего вида
                     * select "выбираем все колонки внешней таблицы кроме ID" from fkTable where fk.id = fk_id
                     * Подзапросы должны формироваться для каждой колонки ОТДЕЛЬНО!!!*/
                    //Определяем колонки для вывода информации

                    var fkColumnCounter = new Object() {
                      int count = 0;
                      int amount = fKTable.getColumns().size();
                    };

                    //Формируем подзапрос для каждого столбца
                    for (String fkColumnName : fKTable.getColumns().keySet()) {
                        TableColumn columnFkTable = fKTable.getColumns().get(fkColumnName);

                        if (columnFkTable instanceof PrimaryKeyColumn) continue;

                        subRequest.append("(");
                        subRequest.append(
                                String.format(
                                        selectTemplate,
                                        String.format(templateSqlName), columnFkTable.getName(),    //Наименование колонки внешней таблицы
                                        (!foreignKeySelf) ? (String.format(templateSqlName, pseudoName)) : (String.format(templateAs, fKTable.getName(), pseudoName)),  //Наименование внешней таблицы
                                        String.format(templateFullName, pseudoName, foreignKeyColumn.getName()) + " = " + String.format(templateFullName, col.getTable().getName(), col.getName()) //Блок WHERE
                                )
                        );
                        subRequest.append(") as ");
                        subRequest.append(columnFkTable.getName());
                        if (fkColumnCounter.count < fkColumnCounter.amount - 1) {
                            subRequest.append(", \n");
                        }
                        fkColumnCounter.count += 1;
                    }
                } else {
                    //Внешний ключ ссылается на кастомную колонку внешней таблицы
                    /*Формируем подзапрос следующего вида
                     * select foreignKeyColumn from fkTable where fk.id = fk_id*/
                    subRequest.append("(");
                    subRequest.append(String.format(
                            selectTemplate,
                            String.format(templateSqlName, foreignKeyColumn.getName()),
                            (!foreignKeySelf) ? (String.format(templateSqlName, pseudoName)) : (String.format(templateAs, fKTable.getName(), pseudoName)),
                            String.format(templateFullName, pseudoName, fkTableColId.getName()) + " = " + String.format(templateFullName, col.getTable().getName(), col.getName())
                    ));
                    if (col.getName().startsWith("fk_")) {
                        subRequest.append(
                                ") as " + "fk_".concat(foreignKeyColumn.getName())
                        );
                    } else {
                        subRequest.append(
                                ") as " + foreignKeyColumn.getName()
                        );
                    }

                }
                column.append(subRequest);
            } else {
                //Колонка не содержит внешнего ключа
                column.append(String.format(templateSqlName, col.getName()));
            }

            if (columnCounter.count < columnCounter.amount - 1) {
                column.append(", \n");
            }

            columnCounter.count += 1;
        }

        String comand = String.format(templateComand, column.toString(), table.getName());

        if (where != null) {
            String templateWhere = "%1$s where %2$s";
            comand = String.format(templateWhere, comand, where.toString());
        }

        Statement statement = conn.createStatement();
        result = statement.executeQuery(comand);

        return result;
    }

    /**
     * Функция осуществляет обновление строки в таблице.
     *
     * @param table         имя таблицы в которую добавляют данные
     * @param contentValues класс-обёртка данных, для сохранения в базе данных. Ключ представляет наименование столбца, значение - значение столбца.
     * @param where         строка, хранящая имя столбца в по которому определяем в какой строке обновляем информацию
     */
    public static void update(Table table,
                              ContentValues contentValues,
                              WhereValues where,
                              Connection conn) throws SQLException {
        //Заготовка SQL-команды
        String templateComand = "update `%1$s` set %2$s";
        String comand = String.format(templateComand, table.getName(), contentValues.toStringUpdate());
        if (where != null) {
            String templateWhere = "%1$s where %2$s";
            comand = String.format(templateWhere, comand, where.toString());
        }
        execute(conn, comand);
    }
}
