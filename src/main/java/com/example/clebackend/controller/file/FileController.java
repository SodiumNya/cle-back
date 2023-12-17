package com.example.clebackend.controller.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.clebackend.common.RestBean;
import com.example.clebackend.util.FileUploadUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;

@RestController
@ResponseBody
public class FileController {

    @Value("${fileSave.path}")
    private String path;
    @Resource
    FileUploadUtils fileUtil;

    @GetMapping("/files/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse resp) throws IOException {
        resp.addHeader("Content-Disposition", "inline;filename="+ URLEncoder.encode(filename, StandardCharsets.UTF_8));
        String filePath = path + filename;
        if(!FileUtil.exist(filePath)) {
            return;
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    @PostMapping("api/files/upload")
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        if ((file == null || file.isEmpty())){
            throw new ServerException("参数不合法");
        }
        String url = fileUtil.uploadFile(file);

        if(StrUtil.isBlank(url)){
            return RestBean.error(401, "上传失败，请重试").asJsonString();
        }
        return RestBean.success(url).asJsonString();

    }
}
