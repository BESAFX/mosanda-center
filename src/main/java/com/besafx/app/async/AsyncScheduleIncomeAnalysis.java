package com.besafx.app.async;

import com.besafx.app.entity.Branch;
import com.besafx.app.util.DateConverter;
import net.sf.jasperreports.engine.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class AsyncScheduleIncomeAnalysis {

    private final Logger log = LoggerFactory.getLogger(AsyncScheduleIncomeAnalysis.class);

    private DateTime startDate, endDate;

    @Autowired
    private TransactionalService transactionalService;

    @Async("threadMultiplePool")
    public Future<byte[]> getFile(String timeType, Branch branch) throws Exception {
        StringBuilder title = new StringBuilder();
        switch (timeType) {
            case "Day":
                startDate = new DateTime().withTimeAtStartOfDay();
                endDate = new DateTime().plusDays(1).withTimeAtStartOfDay();
                title.append("تحليل يومي للايرادات من");
                title.append(" ");
                break;
            case "Week":
                startDate = new DateTime(DateConverter.getCurrentWeekStart()).withTimeAtStartOfDay();
                endDate = new DateTime(DateConverter.getCurrentWeekEnd()).plusDays(1).withTimeAtStartOfDay();
                title.append("تحليل اسبوعي للايرادات من");
                title.append(" ");
                break;
            case "Month":
                startDate = new DateTime().withDayOfMonth(1).withTimeAtStartOfDay();
                endDate = startDate.plusMonths(1).minusDays(1);
                title.append("تحليل شهري للايرادات من");
                title.append(" ");
                break;
            case "Year":
                startDate = new DateTime().withDayOfYear(1).withTimeAtStartOfDay();
                endDate = startDate.plusYears(1).minusDays(1);
                title.append("تحليل سنوي للايرادات من");
                title.append(" ");
                break;
        }

        title.append(DateConverter.getHijriStringFromDateLTR(startDate.toDate()));
        title.append(" ");
        title.append("إلى");
        title.append(" ");
        title.append(DateConverter.getHijriStringFromDateLTR(endDate.toDate()));

        Map<String, Object> map = new HashMap<>();
        map.put("title", title.toString());
        map.put("ts", transactionalService);
        map.put("startDate", startDate.getMillis());
        map.put("endDate", endDate.getMillis());
        map.put("masters", transactionalService.getMastersByBranch(branch));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/payment/IncomeAnalysis.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
    }
}
