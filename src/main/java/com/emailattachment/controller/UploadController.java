package com.emailattachment.controller;

import com.emailattachment.entity.Upload;
import com.emailattachment.entity.UploadDetails;
import com.emailattachment.entity.User;
import com.emailattachment.repository.UploadRepository;
import com.emailattachment.service.impl.UploadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


@Controller
public class UploadController {


    private UploadRepository uploadRepository;
    private UploadServiceImpl uploader;


    @Autowired
    public UploadController(UploadRepository uploadRepository,UploadServiceImpl uploader) {
        this.uploadRepository = uploadRepository;
        this.uploader = uploader;
    }



    @GetMapping("/upload-file")
    public ModelAndView displayUploadForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("uploads/upload-file");
        return modelAndView;
    }
    @GetMapping("/view-uploads")
    public ModelAndView displayUploadedData(Upload upload, Model model, String keyword) {
        ModelAndView modelAndView = new ModelAndView();
        if(keyword != null) {
            List<UploadDetails> list = uploadRepository.findByAccountNumber(keyword);

            model.addAttribute("uploads", list);
        }else {
            List<UploadDetails> uploadDetails = uploadRepository.findAll();


            model.addAttribute("uploads", uploadDetails);
        }
        modelAndView.setViewName("uploads/uploads");
        return modelAndView;
    }

    @PostMapping("/upload")
    public String uploadData(@RequestParam("file") MultipartFile file) throws Exception{
        if(!file.isEmpty()){
            this.uploader.saveUploadsToDatabase(file);
            return "redirect:upload-file?success";
        }else {
            return "redirect:upload-file";
        }

    }

    @GetMapping("/edit-uploaded/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        UploadDetails uploadDetails = uploadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid  Id:" + id));

        model.addAttribute("user", uploadDetails);
        return "uploads/edit-uploads";
    }

    @PostMapping("/update-data/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid UploadDetails uploadDetails,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            uploadDetails.setId(id);
            return "uploads/edit-uploads";
        }
        uploadRepository.save(uploadDetails);
        return "redirect:/view-uploads?success";
    }

    @GetMapping("/delete-data/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        UploadDetails uploadDetails = uploadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        uploadRepository.delete(uploadDetails);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("uploads/uploads");
        return "redirect:/view-uploads?deleted";
    }


//    @PostMapping("/upload")
//    public ModelAndView uploadData(@RequestParam("file") MultipartFile file, Model model) throws Exception{
//        ModelAndView modelAndView = new ModelAndView();
//        List<UploadDetails> uploadDetails = new ArrayList<>();
//        InputStream inputStream = file.getInputStream();
//        CsvParserSettings settings = new CsvParserSettings();
//        settings.setHeaderExtractionEnabled(true);
//        CsvParser parser = new CsvParser(settings);
//        List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
//        parseAllRecords.forEach( record -> {
//            UploadDetails details = new UploadDetails();
//            details.setTwr_pref((record.getString("twr_pref")));
//            details.setTwr_no(Integer.parseInt((record.getString("twr_no"))));
//            details.setPl_no(Integer.parseInt((record.getString("pl_no"))));
//            details.setIsabend((record.getString("isabend")));
//            details.setTwr_type(Integer.parseInt((record.getString("twr_type"))));
//            details.setSub_type((record.getString("sub_type")));
//            details.setCond_att(Double.parseDouble(record.getString("cond_att")));
//            details.setTube_no(Integer.parseInt((record.getString("tube_no"))));
//            details.setSht_no(Integer.parseInt((record.getString("sht_no"))));
//            details.setCrd_type((record.getString("crd_type")));
//            details.setData_source((record.getString("data_source")));
//            details.setAccuracy(Integer.parseInt((record.getString("accuracy"))));
//            details.setDate_captured(((record.getDate("date_captured"))));
//            details.setTower_no(Integer.parseInt((record.getString("tower_no"))));
//            details.setLat(Double.parseDouble((record.getString("lat"))));
//            details.setLong_(Integer.parseInt(record.getString("long")));
//            details.setHeight((record.getDouble("height")));
//            uploadDetails.add(details);
//        });
//        uploadRepository.saveAll(uploadDetails);
//        modelAndView.setViewName("uploads");
//        return modelAndView;
//    }

}
