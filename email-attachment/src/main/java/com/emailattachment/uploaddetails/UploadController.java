package com.emailattachment.uploaddetails;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UploadController {

    @Autowired
    UploadRepository service;

    @PostMapping("/upload")
    public String uploadData(@RequestParam("file") MultipartFile file) throws Exception{
        List<UploadDetails> uploadDetails = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(settings);
        List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
        parseAllRecords.forEach( record -> {
            UploadDetails details = new UploadDetails();
            details.setAccountNumber(Integer.parseInt(record.getString("Account_number")));
            details.setFirstName((record.getString("First_Name")));
            details.setLastName((record.getString("Last_Name")));
            details.setEmail((record.getString("Email")));
            details.setAddress((record.getString("Address")));
            uploadDetails.add(details);
        });
        service.saveAll(uploadDetails);
        return "Upload Successfully !!!";
    }

}
