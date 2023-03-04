package com.emailattachment;

import com.emailattachment.controller.UploadController;
import com.emailattachment.email.Email;
import com.emailattachment.email.EmailAttachment;
import com.emailattachment.entity.Upload;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.emailattachment.email.IncomingMail;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class EmailAttachmentApplication {
    public static  Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }
    public static void main(String[] args) throws IOException {
        SpringApplication.run(EmailAttachmentApplication.class, args);
//        Set<String> myFiles = listFilesUsingFilesList("c:\\Users\\USER\\temp");
//        for (String file : myFiles) {
//            System.out.println(file);
//        }


//        try {
//            List<Email> emails = IncomingMail.downloadPop3("mail.likeanp.co.za", "customercare@likeanp.co.za", "yj}gCU1,7-W~", "C:\\Users\\USER\\temp");
//
//            System.out.println("Started");
//            for ( Email email : emails ) {
//                System.out.println(email.from);
//                System.out.println(email.subject);
//                System.out.println(email.body);
//                List<EmailAttachment> attachments = email.attachments;
//                for ( EmailAttachment attachment : attachments ) {
//                    System.out.println(attachment.path+" "+attachment.name);
//
//                }
//            }
//        } catch (Exception e) { e.printStackTrace(); }

    }

}
