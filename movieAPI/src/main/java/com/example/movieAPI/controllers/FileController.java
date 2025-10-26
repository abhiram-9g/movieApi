package com.example.movieAPI.controllers;

import com.example.movieAPI.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file/")
public class FileController {
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;
    // it is important to set the value for the variable "path", this value is beign fetched from application.yml file

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException{
        // have to ensure the variable used in the argument is same as the variable used in postman while uploading the file.
        // in order to test the functioning of the controller, in postman under bofy section -> form-data ->give the respective key, value
        // under the value select the file option and upload the file.
        String uploadedFileName = fileService.uploadFile(path,file);
        return ResponseEntity.ok("File uploaded "+ uploadedFileName);
    }

    @GetMapping( value = "/{fileName}")
    public void serveFileHandler(@PathVariable String fileName, HttpServletResponse response) throws IOException{
        System.out.println(response);
        InputStream resourceFile = fileService.getResourceFile(path,fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        //the stream utils takes in input stream and output stream as arguments.
        StreamUtils.copy(resourceFile,response.getOutputStream());
    }
}
