package com.emailattachment.service.impl;

import com.emailattachment.email.*;
import com.emailattachment.entity.UploadDetails;
import com.emailattachment.repository.UploadRepository;
import com.emailattachment.service.UploadService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
//import org.apache.tomcat.util.http.fileupload.*;

//import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@EnableScheduling
public class UploadServiceImpl implements UploadService {
    private UploadRepository repository;

    public UploadServiceImpl(UploadRepository repository) throws IOException {
        this.repository = repository;
    }

    @Override
    public List<UploadDetails> getByKeyword(String keyword) {
        List<UploadDetails> uploads = repository.findByAccountNumber(keyword);
            return uploads;
    }

    @Override
    public void saveUploadsToDatabase(MultipartFile file) {
        if(isValidExcelFile(file)){
            try {
                List<UploadDetails> dataFromExcel = getUploadDataFromExcel(file.getInputStream());
                this.repository.saveAll(dataFromExcel);
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        }
    }
    @Override
    public void saveAttachToDatabase(InputStream file) {
            List<UploadDetails> dataFromExcel = getUploadDataFromExcel(file);
            this.repository.saveAll(dataFromExcel);
    }

    IncomingMail incomingMail = new IncomingMail();

    public Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    final String dir = System.getProperty("user.dir") + File.separator + "uploads";
    Set<String> myFiles = listFilesUsingFilesList(dir);

    @Scheduled(fixedRate = 10000)
    void checkMails() throws IOException {
        if(!myFiles.isEmpty() || myFiles != null) {
           // incomingMail.downloadPop3("","","");
            for( String uploadFiles : myFiles ) {
                File file = new File(dir + uploadFiles);
                FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
                fileItem.getOutputStream();

                try {
                    InputStream input = new FileInputStream(file);
                    OutputStream os = fileItem.getOutputStream();
                    IOUtils.copy(input, os);
                    // Or faster..
                    // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
                } catch (IOException ex) {
                    // do something.
                }

                MultipartFile multipartFile = new CommonsMultipartFile((org.apache.commons.fileupload.FileItem) fileItem);
               saveUploadsToDatabase(multipartFile);

                File destDir = new File("/dest/2014");
                //file.renameTo(new File(dir + uploadFiles+ 1));
                System.out.println("current dir = " + dir + File.separator + "uploads");
                // saveAttachToDatabase(new ByteArrayInputStream(uploadFiles.getBytes()) );
                System.out.println("done: " + multipartFile);
            }

        }

    }

    public static boolean isValidExcelFile(MultipartFile file){
        String contentType = file.getContentType();

        if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" )) {
            return true;
        }else {
            return false;
        }
    }
    public static List<UploadDetails> getUploadDataFromExcel(InputStream inputStream){
        List<UploadDetails> details = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            DataFormatter fmt = new DataFormatter();

            XSSFSheet sheet = workbook.getSheet("Sheet1");

            int rowIndex = 0;
            for (Row row : sheet){
                if (rowIndex ==0){
                    rowIndex++;
                    continue;
                }
                System.out.println(row);
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                UploadDetails uploadDetails = new UploadDetails();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    switch (cellIndex){

                        case 0 : uploadDetails.setTwr_pref(fmt.formatCellValue(cell));
                        break;
                        case 1 : uploadDetails.setTwr_no(fmt.formatCellValue(cell));
                            break;
                        case 2 : uploadDetails.setPl_no(fmt.formatCellValue(cell));
                            break;
                        case 3 : uploadDetails.setIsabend(fmt.formatCellValue(cell));
                            break;
                        case 4 : uploadDetails.setTwr_type(fmt.formatCellValue(cell));
                            break;
                        case 5 : uploadDetails.setSub_type(fmt.formatCellValue(cell));
                            break;
                        case 6 :
                            uploadDetails.setCond_att((cell.getNumericCellValue()));
                            break;
                        case 7 : uploadDetails.setTube_no(fmt.formatCellValue(cell));
                            break;
                        case 8 : uploadDetails.setSht_no(fmt.formatCellValue(cell));
                            break;
                        case 9: uploadDetails.setCrd_type(fmt.formatCellValue(cell));
                            break;
                        case 10 : uploadDetails.setData_source(fmt.formatCellValue(cell));
                            break;
                        case 11 : uploadDetails.setAccuracy(fmt.formatCellValue(cell));
                            break;
                        case 12 : uploadDetails.setDate_captured(fmt.formatCellValue(cell));
                            break;
                        case 13 : uploadDetails.setTower_no(fmt.formatCellValue(cell));
                            break;
                        case 14 :
                            uploadDetails.setLat(cell.getNumericCellValue());
                            break;
                        case 15 :
                            uploadDetails.setLong_(cell.getNumericCellValue());
                            break;
                        case 16 :
                            uploadDetails.setHeight(cell.getNumericCellValue());
                            break;
                        default :
                            break;

                    }
                    cellIndex++;
                }
                details.add(uploadDetails);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }

        return details;
    }
}
