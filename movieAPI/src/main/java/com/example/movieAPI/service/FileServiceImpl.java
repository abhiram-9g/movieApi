package com.example.movieAPI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // we use mulitparte request when trying to send anything different from text -> img, audio etc.
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()
        ))){
            throw new RuntimeException("File Already exists, please enter another file name");
        }
        // to get the name of the file.
        String filename = file.getOriginalFilename();

        //to get the filepath
        String filepath = path + File.separator + filename;

        //create file object
        File f = new File(path);
        //if the file does not exist, the directory is created
        if(!f.exists()){
            f.mkdir();
        }

        //copy the file or upload the file to the path
        //since the file cannot be directly uploaded, it is converted to input stream and later copied to the path.
        Files.copy(file.getInputStream(), Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws FileNotFoundException {
        String filepath = path + File.separator + filename;

        return new FileInputStream(filepath);
    }
}
