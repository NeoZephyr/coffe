package foundation.lab.io;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExcelApp {
    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("/Users/meilb/Documents/self/coffe/punk/input/abc.xlsx");
        readCells(inputStream);
    }

    private static List<Map<String, Object>> readCells(InputStream inputStream) throws Exception {
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                List<String> headers = Lists.newArrayList(headerRow.cellIterator()).stream().map(Cell::getStringCellValue).collect(Collectors.toList());

                return Lists.newArrayList(sheet.rowIterator()).stream()
                        .filter(row -> row.getRowNum() > 0)
                        .map(row -> {
                            Map<String, Object> cells = new HashMap<>();
                            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                                String header = headers.get(i);
                                if (!Strings.isNullOrEmpty(header)) {
                                    Cell cell = row.getCell(i);

                                    if (cell == null) {
                                        System.out.println("row num: " + row.getRowNum() + ", col num: " + i);
                                    }
                                    cells.put(header, getCellValue(row.getCell(i)));
                                }
                            }
                            return cells;
                        }).collect(Collectors.toList());
            } else {
                return Lists.newArrayList();
            }
        } finally {
            if (Objects.nonNull(workbook)) {
                workbook.close();
            }
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            System.out.println("fuck");
            return null;
        }
        switch (cell.getCellType()) {
            case STRING: // CELL_TYPE_STRING
            case FORMULA: // CELL_TYPE_FORMULA
                return cell.getStringCellValue();
            default:
                return "";
        }
    }
}
