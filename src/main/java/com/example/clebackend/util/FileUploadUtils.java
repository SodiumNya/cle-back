package com.example.clebackend.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.clebackend.common.ResponseCode;
import com.example.clebackend.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class FileUploadUtils {

    @Value("${uploadFileMode}")
    private String model;

    @Value("${fileSave.path}")
    private String path;

    @Value("${server.port}")
    private String serverPort;

    @Value("${fileSave.address}")
    private String serverAddress;


    public String uploadFile(MultipartFile file) {
        if (StrUtil.equals(model, "local")) {
            try {
                return uploadFileLocal(file);
            } catch (IOException e) {
                throw new ServiceException(ResponseCode.FAIL.getCode(), "上传失败");
            }
        }
        return "";
    }

    public List<String> uploadFileList(List<MultipartFile> files){
        List<String> urlList = new ArrayList<>();

        if (StrUtil.equals(model, "local")) {
            try {
                for (MultipartFile file : files) {
                    String url = uploadFileLocal(file);
                    urlList.add(url);
                }
            } catch (IOException e) {
                throw new ServiceException(ResponseCode.FAIL.getCode(), "上传失败");
            }
        }

        return urlList;
    }


    private String uploadFileLocal(MultipartFile file) throws IOException {
        String originFileName = file.getOriginalFilename();
        String mainName = FileUtil.mainName(originFileName);
        String extName = FileUtil.extName(originFileName);

        if(!FileUtil.exist(path)){
            FileUtil.mkdir(path);
        }

        if(FileUtil.exist(path + File.separator + originFileName)) {
            originFileName = System.currentTimeMillis() + "_" + mainName + "." + extName;
        }

        File saveFile = new File(path + originFileName);
        file.transferTo(saveFile);

        return "http://" + serverAddress + ":" + serverPort + "/files/" + originFileName;
    }
}
