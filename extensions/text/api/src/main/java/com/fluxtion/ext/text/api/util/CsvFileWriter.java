/*
 * Copyright (C) 2021 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.text.api.util;

import com.fluxtion.ext.text.api.csv.RowProcessor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import lombok.Data;

/**
 * Writes a collection of csv records from a {@link RowProcessor} to a file. Closes the file after the collection is
 * written
 * @author V12 Technology Ltd.
 */
@Data
public class CsvFileWriter {
    
    private final String fileName;
    private final RowProcessor rowProcessor;
    
    public void generateReport(Collection records) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                rowProcessor.toCsv(records, writer);
            }
        } catch (IOException iOException) {
            System.out.println("problem writing file:" + iOException.getMessage());
            throw new RuntimeException(iOException);
        }
    }
    

}
