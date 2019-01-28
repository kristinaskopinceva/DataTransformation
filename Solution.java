import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.*;


public class Solution {
    private static Connection connection = null;
    private int N = 0;
    String xml1 = "C:\\test\\1.xml";
    String xml2 = "C:\\test\\2.xml";
    String xslPattern = "C:\\test\\xslPattern.xml";


    public void setConnection() {       //setter для переменной connection
        Connection connection1 = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection1 = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=Test;integratedSecurity=true;");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.connection = connection1;
    }

    public void setN() {    //setter для переменной N (инициализируется значением считанным из консоли)
        int N1 = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input dimension for N");
        try {
            String s = reader.readLine();
                     for( int i=0; i< s.length();i++){
                  if( !Character.isDigit(s.charAt(i))){
                      System.out.println("Input data is`t a number! Input again");
                         s=reader.readLine();
            }}  N1 = Integer.parseInt(s);
                     } catch (Exception E){}

        this.N = N1;
    }

    public Connection getConnection() {
        return this.connection;
    } // getter для переменной connection

    public int getN() {
        return this.N;
    }  // getter для N

    public void insertIntoTableBatch() {  // Транкейт и вставка в таблицу данные пакетами с использованияем addBatch и принудительного отключения /включения автокоммита
        try {
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT TOP 1 * FROM TEST");
            if (resultSet.next())
                statement.executeUpdate("TRUNCATE TABLE TEST");

            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO TEST (FIELD) VALUES (?)");
            getConnection().setAutoCommit(false);

            for (int count = 1; count <= getN(); count++) {
                preparedStatement.setInt(1, count);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            getConnection().commit();
            getConnection().setAutoCommit(true);
            preparedStatement.close();

        } catch (SQLException e) {

        }
    }

    public void insertIntoTable() {
        try {
            Statement statement = getConnection().createStatement();
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TEST");
            if (resultSet.next())
                statement.executeUpdate("TRUNCATE TABLE TEST");
            for (int i = 1; i <= getN(); i++) {
                // String s = "INSERT INTO TEST (FIELD) VALUES ("+i+")";
                statement.executeUpdate("INSERT INTO TEST (FIELD) VALUES (" + i + ")");
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        System.out.println("Data added in sql table");
    }

    public void insertIntoTableNew() {
        try {
            // PreparedStatement preparedStatement = getConnection().prepareStatement("EXEC TestProcedure1 ?");
            // preparedStatement.setInt(1,100);
            // ResultSet resultSet = preparedStatement.executeQuery();
            CallableStatement call = getConnection().prepareCall("{ call TestProcedure (?) }");
            call.setInt(1, getN());
            ResultSet resultSet = call.executeQuery();

        } catch (SQLException e) {
            e.getMessage();
        }
        System.out.println("Data added in sql table");
    }

    public ArrayList<Integer> getFromTable() { //чтение данных из столбца FIELD c последующей записью в промежуточный ArrayList
        Statement statement = null;
        ArrayList<Integer> list = new ArrayList<>();
        try {
            statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TEST");

            while (resultSet.next()) {
                list.add(resultSet.getInt("FIELD"));
            }
        } catch (SQLException E) {
        }

        return list;

    }

    public void createXml1() {  // xml первый по заданному условию
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        ArrayList<Integer> list = getFromTable();
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element rootElement = document.createElement("entries");
            document.appendChild(rootElement);
            for (int i = 0; i < list.size(); i++) {
                Element element = document.createElement("entry");
                Element field = document.createElement("field");
                rootElement.appendChild(element);
                element.appendChild(field);
                Text txt = document.createTextNode(list.get(i).toString());
                field.appendChild(txt);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult file = new StreamResult(new File(xml1));
            transformer.transform((new DOMSource(document)), file);

        } catch (Exception e) {
            e.getMessage();
        }
        System.out.println("xml1 Created");
    }

    public void createXml2() { // использование TransformerFactory для форматирования исходного файла xml по шаблону стилей xsl и сохраниение в xml2

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(new StreamSource(xslPattern));
            transformer.transform(new StreamSource(xml1), new StreamResult(xml2));


        } catch (TransformerException e) {
            e.getMessage();
        }
        System.out.println("xml2 Created");

    }

    public long countN() {// подсчет суммы всего содержимого тега field

        long sumN = 0;
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            Document document = db.parse(xml2);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("entry");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                sumN += Integer.parseInt(element.getAttribute("field"));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
        }
        return sumN;
    }

}







