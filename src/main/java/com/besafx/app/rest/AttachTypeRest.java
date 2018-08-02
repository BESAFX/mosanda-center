package com.besafx.app.rest;

import com.besafx.app.entity.AttachType;
import com.besafx.app.service.AttachTypeService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/attachType/")
public class AttachTypeRest {

    @Autowired
    private AttachTypeService attachTypeService;

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AttachType> findAll() {
        return Lists.newArrayList(attachTypeService.findAll());
    }

}
