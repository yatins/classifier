/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.springer.semantic.classifier.jetty.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.springer.semantic.classifier.jetty.service.ConvertToSeqService;

@Controller
@RequestMapping(value = "/convert")
public class ConvertToSeqController {
	
	@Autowired
	private ConvertToSeqService convertToSeqService;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String classify(@RequestParam(required = true) String abstractVal, @RequestParam(required = true) String resultCount) {
		String inputFileName = "";
		String outputDirName = "";
		try {
			this.convertToSeqService.convertToSeq(inputFileName, outputDirName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}	
}
