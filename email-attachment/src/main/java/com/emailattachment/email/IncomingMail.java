package com.emailattachment.email;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class IncomingMail {
    public static List<Email> downloadPop3(String host,int port ,String user, String pass, String downloadDir) throws Exception {

        List<Email> emails = new ArrayList<Email>();

        // Create empty properties
        Properties props = new Properties();

        // Get the session
        Session session = Session.getInstance(props, null);

        // Get the store
        Store store = session.getStore("pop3");
        store.connect(host ,user, pass);
        System.out.println((store));

        // Get folder
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        try {
            // Get directory listing
            Message messages[] = folder.getMessages();
            for (int i = 0; i < messages.length; i++) {

                Email email = new Email();

                // from
                email.from = messages[i].getFrom()[0].toString();

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

                        } else if ((disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition .equalsIgnoreCase(Part.INLINE))) {

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
                            }
                        }
                    } // end of multipart for loop
                } // end messages for loop

                emails.add(email);

                // Finally delete the message from the server.
                messages[i].setFlag(Flags.Flag.DELETED, true);
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
