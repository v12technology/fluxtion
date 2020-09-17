/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.integration;

import com.fluxtion.integration.etl.CsvEtlPipeline;
import com.fluxtion.integration.etl.Main;
import java.io.StringReader;
import java.io.StringWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
public class EtlViewController {

    @Autowired
    private Main main;

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        log.info("index request");
        buildIndexModel(model);
        return "index";
    }

    private void buildIndexModel(Model model) {
        model.addAttribute("size", main.listModels().size());
        model.addAttribute("pipelines", main.listModels().values());
        model.addAttribute("failedSize", main.listFailedModels().size());
        model.addAttribute("pipelineFailures", main.listFailedModels().values());
    }

    @GetMapping("/bootstrapsample")
    public String bootstrapsample(Model model) {
        return "bootstrapsample";
    }

    @GetMapping("/devtesting")
    public String devtesting(Model model) {
        return "devtesting";
    }

    @GetMapping("/pipelineform")
    public String pipelineForm(Model model) {
        model.addAttribute("classActiveNewPipeline", "active");
        model.addAttribute("etlRequest", new EtlBuildRequest());
        model.addAttribute("pipelines", main.listModels().values());
        String yaml = ""
                + "id: org.greg.Data3\n"
                + "columns:\n"
                + "- {name: age, type: int}\n"
                + "- {name: lastName, type: String, function: 'return input.toString().toUpperCase();' }\n"
                + "derived:\n"
                + "- {name: halfAge, type: int, function: '"
                + "//some comments\n"
                + "return age / 2;'}\n"
                + "postRecordFunction: '//no-op demo callback\n'"
                + "";

        model.addAttribute("sampleYaml", yaml);
        return "pipelineform";
    }

    @PostMapping("/build-newpipeline")
    public String submitNewPipeline(EtlBuildRequest etlRequest, Model model) {
        log.info("form submission:{}", etlRequest);
        main.buildModel(etlRequest.getDefintion());
        buildIndexModel(model);
        return "index";
    }

    @GetMapping("/test-newpipeline")
    public String testPipelineForm(Model model) {
        model.addAttribute("classActiveTestPipeline", "active");
        model.addAttribute("etlRequest", new EtlSampleRun());
        model.addAttribute("pipelines", main.listModels().values());
        return "test-newpipeline";
    }

    @PostMapping("/test-newpipeline")
    public String testPipeline(EtlSampleRun etlRequest, Model model) {
        log.info("test pipeline:{}", etlRequest);
        log.info("model id:{}", etlRequest.id());
        model.addAttribute("etlRequest", etlRequest);
        model.addAttribute("size", main.listModels().size());
        model.addAttribute("pipelines", main.listModels().values());
        log.info("executing pipeline");
        StringWriter writer = new StringWriter();
        StringWriter writerCsv = new StringWriter();
        StringWriter writerErrors = new StringWriter();
        main.executePipeline(etlRequest.id(), new StringReader(etlRequest.getInputData()), writer, writerCsv, writerErrors);
        etlRequest.setProcessed(true);
        etlRequest.setResult(writer.toString());
        etlRequest.setResult_csv(writerCsv.toString());
        etlRequest.setErrorLog(writerErrors.toString());
        return "test-newpipeline";
    }

    @GetMapping("/edit-pipeline")
    public String editPipeline(@RequestParam(value = "id", required = false) String pipelineId, Model model) {
        log.info("edit id:{}", pipelineId);
        CsvEtlPipeline model1 = main.getModel(pipelineId);
        final EtlBuildRequest etlBuildRequest = new EtlBuildRequest();
        etlBuildRequest.setPipelineId(pipelineId);
        etlBuildRequest.setDefintion(model1.getDefintion().toYaml());
        model.addAttribute("etlRequest", etlBuildRequest);
        return "edit-pipeline";
    }

    @GetMapping("/delete-pipeline")
    public String deletePipeline(@RequestParam(value = "id", required = false) String pipelineId, Model model) {
        log.info("delete id:{}", pipelineId);
        main.deletePipeline(pipelineId);
        buildIndexModel(model);
        return "index";
    }

}
