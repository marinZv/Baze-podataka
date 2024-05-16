package database;

import database.settings.Settings;
import lombok.Data;
import lombok.Getter;
import resource.DBNode;
import resource.data.Row;
import resource.enums.AttributeType;
import resource.enums.ConstraintType;
import resource.implementations.Attribute;
import resource.implementations.AttributeConstraint;
import resource.implementations.Entity;
import resource.implementations.InformationResource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Getter
public class MYSQLrepository implements Repository{

    private Settings settings;
    private Connection connection;

    public MYSQLrepository(Settings settings){
        this.settings = settings;
    }

    private void initConnection() throws SQLException,ClassNotFoundException{
        String ip = (String) settings.getParameter("mysql_ip");
        String database = (String) settings.getParameter("mysql_database");
        String username = (String) settings.getParameter("mysql_username");
        String password = (String) settings.getParameter("mysql_password");
        //Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+database,username,password);
    }

    private void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }


    @Override
    public DBNode getSchema() {
        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();
            InformationResource informationResource = new InformationResource("bp_tim93");

            String tableType[] = {"TABLE"};
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, tableType);

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (tableName.contains("trace")) continue;
                Entity newTable = new Entity(tableName, informationResource);
                informationResource.addChild(newTable);

                ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, null);

                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");

                    //System.out.println(columnType);

                    int columnSize = Integer.parseInt(columns.getString("COLUMN_SIZE"));

                    Attribute attribute = new Attribute(columnName, newTable,
                            AttributeType.valueOf(Arrays.stream(columnType.toUpperCase().split(" ")).collect(Collectors.joining("_"))),
                            columnSize);
                    newTable.addChild(attribute);


                    ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);

                    while (primaryKeys.next()){

                        String str = primaryKeys.getString("COLUMN_NAME");
                        if(attribute.getName().equalsIgnoreCase(str)){

                            AttributeConstraint attributeConstraint = new AttributeConstraint(attribute.getName(), attribute, ConstraintType.PRIMARY_KEY);
                            attribute.addChild(attributeConstraint);

                        }

                    }


                    ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);

                    while (foreignKeys.next()){

                        String str = foreignKeys.getString("FKCOLUMN_NAME");
                        if(attribute.getName().equalsIgnoreCase(str)){

                            AttributeConstraint attributeConstraint = new AttributeConstraint(attribute.getName(), attribute, ConstraintType.FOREIGN_KEY);
                            attribute.addChild(attributeConstraint);

                        }

                    }




                }
            }
            return informationResource;
        } catch (SQLException exception1){
            exception1.printStackTrace();
        } catch (ClassNotFoundException exception){
            exception.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return null;
    }

    public String getImportedKey(String tableName){

        String foreignKeyString = null;

        try {
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();

            ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);

            foreignKeyString = foreignKeys.getString("COLUMN_NAME");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }


        return foreignKeyString;
    }

    @Override
    public java.util.List<Row> get(String from) {
        boolean valid = true;
        List<Row> rowList = new ArrayList<>();

        try {
            this.initConnection();
            String query = "SELECT * FROM " + from;
            CallableStatement callableStatement = connection.prepareCall(from);
            if(from.length()>7)
                if(from.substring(0,6).equalsIgnoreCase("CREATE")){
                    Statement statement = connection.createStatement();
                    statement.execute(from);
                    return null;
                }
            
            else if (from.contains(" "))
                query = from; 
                ResultSet resultSet;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                if(query.length()<7)
                    valid = false;
                if(query.substring(0,6).equalsIgnoreCase("SELECT") || from.substring(0,4).equalsIgnoreCase("EXEC")){
                    if(query.substring(0,6).equalsIgnoreCase("SELECT")) {
                        resultSet = preparedStatement.executeQuery();
                    } else resultSet = callableStatement.executeQuery();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    Row row = new Row();
                    if (!(query.equalsIgnoreCase(from)))
                        row.setName(from);

                    for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                        row.addField(resultSetMetaData.getColumnName(i), resultSet.getString(i));
                    }
                    rowList.add(row);

                }

            } else {
                    if(valid)
                        preparedStatement.executeUpdate();
                }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            this.closeConnection();
        }
        return rowList;
    }

    @Override
    public void updateOnInsert(ArrayList<String> querys) {
        //TODO dodaj kod
    }
}

