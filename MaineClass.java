public class MaineClass{

    public static void main(String[] args) {
        long timeStart = System.currentTimeMillis(); // время старата главного потока
        Solution solution = new Solution();
            solution.setConnection(); // иниц. переменную connection через сеттер
             solution.setN();  //иниц.переменную N через сеттер
               solution.insertIntoTableBatch();  // insert в таблицу
        // solution.insertIntoTableNew();// запускаем хранимаю процедуру mssql через CallableStatement
        solution.createXml1(); // создаем первый xml
        solution.createXml2(); // создаем второй xml

        System.out.println("Sum all N in xml2: " + solution.countN()); // считаем сумму N
        long timeEnd = System.currentTimeMillis(); //время завершения всех основных операций
        System.out.println((Math.ceil(timeEnd - timeStart) / 1000 / 60) + " IN MINUTE");


    }

}