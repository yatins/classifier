package com.springer.semantic.classifier.jetty.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Controller
@RequestMapping(value = "/upload")
public class FileUploadController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody 
	public String handleFileUpload(@RequestParam(required = false) String name, @RequestParam("file") MultipartFile file,  MultipartHttpServletRequest request){

		if (!file.isEmpty()) {
			try {
				System.out.println(file.getName());
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
				stream.write(bytes);
				stream.close();
				return "You successfully uploaded " + name + " into " + name + "-uploaded !";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name + " because the file was empty.";
		}

	}
	

}




