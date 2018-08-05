package com.besafx.app.async;

import com.besafx.app.entity.Account;
import com.besafx.app.init.Initializer;
import com.besafx.app.service.AccountService;
import com.besafx.app.util.CompanyOptions;
import com.besafx.app.util.JSONConverter;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class AsyncContractGenerator {

    private final Logger LOG = LoggerFactory.getLogger(AsyncContractGenerator.class);

    @Autowired
    private AccountService accountService;

    @Async("threadMultiplePool")
    @Transactional
    public Future<JasperPrint> generate(Long accountId) {
        Map<String, Object> map = new HashMap<>();
        CompanyOptions options = JSONConverter.toObject(Initializer.company.getOptions(), CompanyOptions.class);
        map.put("REPORT_TITLE", options.getReportTitle());
        map.put("REPORT_SUB_TITLE", options.getReportSubTitle());
        map.put("REPORT_FOOTER", options.getReportFooter());
        map.put("LOGO", options.getLogo());
        map.put("BACKGROUND", options.getBackground());

        try {
            Account account = accountService.findOne(accountId);
            ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Contract.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(Lists.newArrayList(account)));
            return new AsyncResult<>(jasperPrint);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new AsyncResult<>(null);
        }
    }

    public byte[] getFile(Long accountId) throws Exception {
        return JasperExportManager.exportReportToPdf(generate(accountId).get());
    }
}
