/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package importXLS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.Player;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.PFXUtil;

/**
 *
 * @author Mark Dechamps
 */
public class Import {

    Player zetNaamEnVoornaamOp(Player player, String stringCellValue) {
        String[] nameArr = stringCellValue.split(" ");

        if (nameArr.length == 1) {
            player.setFirstname(stringCellValue);
        } else {

            String voornaam = nameArr[nameArr.length - 1].trim();
            player.setFirstname(voornaam);

            StringBuilder naam = new StringBuilder();
            for (int i = 0; i < nameArr.length - 1; i++) {
                naam.append(nameArr[i]);
                naam.append(" ");
            }
            player.setLastname(naam.toString().trim());
        }
        return player;
    }

    boolean isValidPlayer(Player player) {
        if (PFXUtil.notNull(player)) {
            boolean voornaamLeeg = PFXUtil.isEmpty(player.getFirstname());
            boolean naamLeeg = PFXUtil.isEmpty(player.getLastname());           
            return !(voornaamLeeg && naamLeeg);
        } else {
            return false;
        }
    }

    private List<Player> parseFile(Iterator<Row> rowIterator) {
        List<Player> result = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //Read Rows from Excel document       
            Player player = processRow(row);
            if (isValidPlayer(player)) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> importFromXLSXFile(File file) throws FileNotFoundException, IOException {
        List<Player> result;
        try (FileInputStream input_document = new FileInputStream(file)) {
            XSSFWorkbook my_xlsx_workbook = new XSSFWorkbook(input_document);
            XSSFSheet my_worksheet = my_xlsx_workbook.getSheetAt(0);
            Iterator<Row> rowIterator = my_worksheet.iterator();
            result = parseFile(rowIterator);
        }
        return result;
    }

    public List<Player> importFromXLSFile(File file) throws FileNotFoundException, IOException {
        List<Player> result;
        try (FileInputStream input_document = new FileInputStream(file)) {
            HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document);
            HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
            Iterator<Row> rowIterator = my_worksheet.iterator();
            result = parseFile(rowIterator);
        }
        return result;
    }

    private Player processRow(Row row) {

        if (row.getRowNum() == 0) {//skip title
            return null;
        }
        Iterator<Cell> cellIterator = row.cellIterator();//Read every column for every row that is READ
        Player player = new Player();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            processCell(cell, player);
        }
        return player;
    }

    private Player processCell(Cell cell, Player player) {

        final int ID_INDEX = 0;
        final int NAAM_VOORNAAM_INDEX = 2;

        int index = cell.getColumnIndex();

        switch (cell.getCellType()) { 
            case Cell.CELL_TYPE_NUMERIC:
                if (index == ID_INDEX) {
                    int id = (int) cell.getNumericCellValue();
                    player.setId(id);
                }
                break;
            case Cell.CELL_TYPE_STRING:
                if (index == NAAM_VOORNAAM_INDEX) {
                    zetNaamEnVoornaamOp(player, cell.getStringCellValue());
                }
                break;
        }
        return player;
    }
    
  
}
