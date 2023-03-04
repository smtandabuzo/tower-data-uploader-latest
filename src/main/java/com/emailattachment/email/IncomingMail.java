package com.emailattachment.email;

import com.emailattachment.repository.UploadRepository;
import com.emailattachment.service.impl.UploadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class IncomingMail {
    private UploadServiceImpl uploadImpl;
    private UploadServiceImpl uploader;


    @Autowired
    public IncomingMail( UploadServiceImpl uploader) {
        this.uploader = uploader;
    }
    //UploadServiceImpl uploadImpl = new UploadServiceImpl();
    public IncomingMail() {

    }
    public List<Email> downloadPop3(String host, String user, String pass) throws Exception {
        //Set properties
        final String downloadDir = System.getProperty("user.dir") + File.separator + "uploads";

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "pop3s");
        props.setProperty("mail.pop3s.host", host);
        props.setProperty("mail.pop3s.port", String.valueOf("995"));
        props.setProperty("mail.pop3s.auth", "true");
        //Bypass the SSL authentication
        props.put("mail.smtp.ssl.enable", false);
        props.put("mail.smtp.starttls.enable", false);

//        //Set properties
//        Properties props = new Properties();
//        props.put("mail.store.protocol", "pop3");
//        props.put("mail.pop3.host", "pop3");
//        props.put("mail.pop3.port", "995");
//        props.put("mail.pop3.starttls.enable", "true");


        // Get the store
        //Store store = session.getStore("pop3");
        List<Email> emails = new ArrayList<Email>();


        // Get the Session object.
        Session session = Session.getInstance(props);

        //Create the POP3 store object and connect to the pop store.
        Store store = session.getStore("pop3s");
        store.connect(host, user, pass);

        System.out.println("store is "+store.isConnected());

        // Get folder
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        try {
            // Get directory listing
            Message messages[] = folder.getMessages();
            for (int i = 0; i < messages.length; i++) {

                Email email = new Email();

                // from
                email.from = String.valueOf(messages[i].getFrom());

                // to list
                Address[] toArray = messages[i] .getRecipients(Message.RecipientType.TO);
                for (Address to : toArray) { email.to.add(to.toString()); }

                // cc list
                Address[] ccArray = null;
                try {
                    ccArray = messages[i] .getRecipients(Message.RecipientType.CC);
                } catch (Exception e) { ccArray = null; }
                if (ccArray != null) {
                    for (Address c : ccArray) {
                        email.cc.add(c.toString());
                    }
                }

                // subject
                email.subject = messages[i].getSubject();

                // received date
                if (messages[i].getReceivedDate() != null) {
                    email.received = messages[i].getReceivedDate();
                } else {
                    email.received = new Date();
                }

                // body and attachments
                email.body = "";
                Object content = messages[i].getContent();
                if (content instanceof java.lang.String) {

                    email.body = (String) content;

                } else if (content instanceof Multipart) {

                    Multipart mp = (Multipart) content;

                    for (int j = 0; j < mp.getCount(); j++) {

                        Part part = mp.getBodyPart(j);
                        String disposition = part.getDisposition();

                        if (disposition == null) {

                            MimeBodyPart mbp = (MimeBodyPart) part;
                            if (mbp.isMimeType("text/plain")) {
                                // Plain
                                email.body += (String) mbp.getContent();
                            }

                        } else if ((disposition != null) && (disposition.equals(Part.ATTACHMENT) || disposition .equals(Part.INLINE))) {

                            // Check if plain
                            MimeBodyPart mbp = (MimeBodyPart) part;
                            if (mbp.isMimeType("text/plain")) {
                                email.body += (String) mbp.getContent();
                            } else {
                                EmailAttachment attachment = new EmailAttachment();
                                attachment.name = decodeName(part.getFileName());
                                File savedir = new File(downloadDir);
                                savedir.mkdirs();
                                // File savefile = File.createTempFile( "emailattach", ".atch", savedir);
                                File savefile = new File(downloadDir,attachment.name);
                                attachment.path = savefile.getAbsolutePath();
                                attachment.size = saveFile(savefile, part);
                                email.attachments.add(attachment);
                                //FileInputStream fis = new FileInputStream(new File(String.valueOf(mp)));

                                //uploadImpl.saveAttachToDatabase( mp.getParent().getInputStream());
                            }
                        }
                    } // end of multipart for loop
                } // end messages for loop

                emails.add(email);

                // Finally delete the message from the server.
                messages[i].setFlag(Flags.Flag.DELETED, false);
            }

            // Close connection
            folder.close(true); // true tells the mail server to expunge deleted messages
            store.close();

        } catch (Exception e) {
            folder.close(true); // true tells the mail server to expunge deleted
            store.close();
            throw e;
        }

        return emails;
    }

    private static String decodeName(String name) throws Exception {
        if (name == null || name.length() == 0) {
            return "unknown";
        }
        String ret = java.net.URLDecoder.decode(name, "UTF-8");

        // also check for a few other things in the string:
        ret = ret.replaceAll("=\\?utf-8\\?q\\?", "");
        ret = ret.replaceAll("\\?=", "");
        ret = ret.replaceAll("=20", " ");

        return ret;
    }

    private static int saveFile(File saveFile, Part part) throws Exception {

        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(saveFile));

        byte[] buff = new byte[2048];
        InputStream is = part.getInputStream();
        int ret = 0, count = 0;
        while ((ret = is.read(buff)) > 0) {
            bos.write(buff, 0, ret);
            count += ret;
        }
        bos.close();
        is.close();
        return count;
    }


}
