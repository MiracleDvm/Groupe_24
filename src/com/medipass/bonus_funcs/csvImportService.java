package com.medipass.bonus_funcs;

// import tech.tablesaw.api.Table;
// import tech.tablesaw.joining.JoinType;
// import tech.tablesaw.io.csv.CsvReadOptions;


public class csvImportService {

    private static final String OUTPUT_FILE = "output/donnes_systeme.csv";
    private static final char DELIMITER = ';';



    public Table mergeAllData() throws Exception {
        Table consultations = Table.read().csv(CsvReadOptions.builder("consultations.csv").separator(DELIMITER).build());

    }
    
}
