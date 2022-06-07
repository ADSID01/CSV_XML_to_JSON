package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        //создаем CSV файл с данными
        String fileNameCSV = "data.csv";
        createCSV(fileNameCSV);

        String fileNameXML = "data.xml";

        String fileNameCsvJson = "data.json";
        String fileNameXmlJson = "data2.json";

        //CSV  записываем в JSON
        List<Employee> listCSV = parseCSV(columnMapping, fileNameCSV);
        if (listCSV != null) listToJson(listCSV, fileNameCsvJson);

        //XML  записываем в JSON
        try {
            List<Employee> listXML = parseXML(fileNameXML);
            listToJson(listXML, fileNameXmlJson);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        System.out.println("Готово.");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    //чтение XML
    public static List<Employee> parseXML(String fileNameXML) throws ParserConfigurationException, IOException, SAXException, ClassCastException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document doc = documentBuilder.parse(new File(fileNameXML));

        Node root = doc.getDocumentElement();

        return read(root);
    }

    private static List<Employee> read(Node node) {
        NodeList nodeList = node.getChildNodes();
        List<Employee> listEmployee = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {

                System.out.println(node_.getNodeName());
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();
                Employee employee = new Employee();

                for (int a = 0; a < map.getLength(); a++) {
                    if (map.item(a).getNodeName().equals("id")) {
                        employee.setId(Long.parseLong(map.item(a).getNodeValue()));
                    } else if (map.item(a).getNodeName().equals("firstName")) {
                        employee.setFirstName(map.item(a).getNodeValue());
                    } else if (map.item(a).getNodeName().equals("lastName")) {
                        employee.setLastName(map.item(a).getNodeValue());
                    } else if (map.item(a).getNodeName().equals("country")) {
                        employee.setCountry(map.item(a).getNodeValue());
                    } else if (map.item(a).getNodeName().equals("age")) {
                        employee.setAge(Integer.parseInt(map.item(a).getNodeValue()));
                    }
                }
                if (employee.id != 0) listEmployee.add(employee);
            }

        }
        return listEmployee;
    }

    //делаем строку JSON
    public static void listToJson(List<Employee> list, String fileName) {

        //создаем файл JSON
        try (FileWriter file = new FileWriter(fileName)) {
            //получаем строку в формате JSON и записываем
            for (Employee employee : list) {
                try {
                    GsonBuilder gb = new GsonBuilder();
                    Gson gson = gb.create();

                    file.write(gson.toJson(employee));
                    file.flush();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void createCSV(String fileName) {

        String[] columnMapping1 = {"1", "John", "Smith", "USA", "25"};
        String[] columnMapping2 = {"2", "Ivan", "Petrov", "RU", "23"};

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(columnMapping1);
            writer.writeNext(columnMapping2);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
