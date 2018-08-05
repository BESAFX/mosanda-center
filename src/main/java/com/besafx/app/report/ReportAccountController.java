package com.besafx.app.report;

import com.besafx.app.component.ReportExporter;
import com.besafx.app.entity.Account;
import com.besafx.app.enums.ExportType;
import com.besafx.app.init.Initializer;
import com.besafx.app.service.AccountService;
import com.besafx.app.util.CompanyOptions;
import com.besafx.app.util.JSONConverter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class ReportAccountController {

    private final static Logger LOG = LoggerFactory.getLogger(ReportAccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReportExporter reportExporter;

    @RequestMapping(value = "/report/AccountByBranches", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByBranches(
            @RequestParam(value = "branchIds") List<Long> branchIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        CompanyOptions options = JSONConverter.toObject(Initializer.company.getOptions(), CompanyOptions.class);
        map.put("REPORT_TITLE", options.getReportTitle());
        map.put("REPORT_SUB_TITLE", options.getReportSubTitle());
        map.put("REPORT_FOOTER", options.getReportFooter());
        map.put("LOGO", options.getLogo());
        map.put("TITLE", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(branchIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("branch").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //End Search
        map.put("ACCOUNTS", getList(predicates, sort));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByMasters", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByMasters(
            @RequestParam(value = "masterIds") List<Long> masterIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        CompanyOptions options = JSONConverter.toObject(Initializer.company.getOptions(), CompanyOptions.class);
        map.put("REPORT_TITLE", options.getReportTitle());
        map.put("REPORT_SUB_TITLE", options.getReportSubTitle());
        map.put("REPORT_FOOTER", options.getReportFooter());
        map.put("LOGO", options.getLogo());
        map.put("TITLE", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(masterIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("master").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //End Search
        map.put("ACCOUNTS", getList(predicates, sort));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    @RequestMapping(value = "/report/AccountByCourses", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void printAccountByCourses(
            @RequestParam(value = "courseIds") List<Long> courseIds,
            @RequestParam(value = "exportType") ExportType exportType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "title") String title,
            Sort sort,
            HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        CompanyOptions options = JSONConverter.toObject(Initializer.company.getOptions(), CompanyOptions.class);
        map.put("REPORT_TITLE", options.getReportTitle());
        map.put("REPORT_SUB_TITLE", options.getReportSubTitle());
        map.put("REPORT_FOOTER", options.getReportFooter());
        map.put("LOGO", options.getLogo());
        map.put("TITLE", title);
        //Start Search
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(courseIds).ifPresent(value -> predicates.add((root, cq, cb) -> root.get("course").get("id").in(value)));
        Optional.ofNullable(startDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(endDate).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //End Search
        map.put("ACCOUNTS", getList(predicates, sort));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/account/Report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        reportExporter.export(exportType, response, jasperPrint);
    }

    private List<Account> getList(List<Specification> predicates, Sort sort) {
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return accountService.findAll(result, sort);
        } else {
            return new ArrayList<>();
        }
    }
}
