package com.campus.market.module.upload;

import com.campus.market.common.exception.BusinessException;
import com.campus.market.common.result.R;
import com.campus.market.common.result.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传接口（本地存储，预留 OSS 扩展）
 */
@Tag(name = "文件上传", description = "图片上传接口（需登录）")
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Value("${upload.allowed-types:jpg,jpeg,png,gif,webp}")
    private String allowedTypes;

    @Value("${upload.max-size:5242880}")
    private long maxSize;

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file,
                                  HttpServletRequest request) {
        // 校验文件非空
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }

        // 校验文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_ERROR);
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename).toLowerCase();
        List<String> allowedList = Arrays.asList(allowedTypes.split(","));
        if (!allowedList.contains(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_ERROR);
        }

        // 生成文件路径：uploads/yyyy-MM-dd/uuid.ext
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String relativePath = datePath + "/" + newFileName;

        // 创建目录并保存文件
        File destDir = new File(uploadPath + "/" + datePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File destFile = new File(destDir, newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }

        // 返回访问 URL
        String baseUrl = getBaseUrl(request);
        String url = baseUrl + urlPrefix + "/" + relativePath;
        return R.ok(url);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        if (("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) {
            return scheme + "://" + host;
        }
        return scheme + "://" + host + ":" + port;
    }
}
