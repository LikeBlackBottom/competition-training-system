package com.competition.training.module.export;

import com.competition.training.module.timelog.service.TrainingService;
import com.competition.training.util.ExcelExportUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ExportController {
    private final TrainingService service;

    @GetMapping("/api/admin/export/time-logs")
    public void exportTimeLogs(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        String fileName = "展示时长记录_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        ExcelExportUtil.writeTimeLogs(service.exportTimeLogs(params), response.getOutputStream());
    }
}
