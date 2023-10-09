package cc.bearvalley.badminton.product.service;

import cc.bearvalley.badminton.common.Const;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 腾讯COS相关的服务类
 */
@Component
public class CosService {

    /**
     * 上传商品图片
     *
     * @param file 要上传的文件
     * @param id   商品id
     * @return 上传后的图片地址url
     */
    public String uploadItemPic(MultipartFile file, int id) {
        return uploadFileToCos(file, id, "item");
    }

    /**
     * 上传用户头像
     *
     * @param file 要上传的文件
     * @return 上传后的图片地址url
     */
    public String uploadUserPic(MultipartFile file) {
        return uploadFileToCos(file, -1, "avatar");
    }

    /**
     * 上传图片
     *
     * @param file 要上传的文件
     * @param id   商品id
     * @param type 上传类型
     * @return 上传后的图片地址url
     */
    private String uploadFileToCos(MultipartFile file, int id, String type) {
        logger.info("start to upload file {} to cos, id = {}, type = {}", file, id, type);
        try {
            // 获取文件的名字
            String name = file.getOriginalFilename();
            logger.info("get file name = {}", name);
            String suffix = "";
            // 获取文件后缀
            if (name != null && name.contains(".")) {
                suffix = name.substring(name.lastIndexOf("."));
            }
            logger.info("get file suffix name = {}", suffix);
            // 由当前系统时间微秒数生成长传后的文件名
            String localFileName = Const.UPLOAD_TEMP_DIR + System.currentTimeMillis() + suffix.toLowerCase();
            logger.info("get local file name = {}", localFileName);
            // 保存文件
            file.transferTo(new File(localFileName));
            boolean fileExists = new File(localFileName).exists();
            logger.info("{} exists = {}", localFileName, fileExists);
            if (fileExists) {
                String cosFileName;
                if (id > 0) {
                    int first = id % 100;
                    cosFileName = type + "/" + first + "/" + id + "/" + System.currentTimeMillis() + suffix.toLowerCase();
                } else {
                    long time = System.currentTimeMillis();
                    long dir = time % 100;
                    cosFileName = type + "/" + dir + "/" + time + suffix.toLowerCase();
                }
                logger.info("cosFileName = {}", cosFileName);
                if (upload(localFileName, cosFileName)) {
                    logger.info("picture accesss url = {}", this.accessUrl + cosFileName);
                    return this.accessUrl + cosFileName;
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        }
        logger.info("file upload failed");
        return null;
    }

    /**
     * 上传本地文件到cos
     *
     * @param uploadFileName 要上传的本地文件地址
     * @param cosFileName    上传后的cos端地址
     * @return 上传成功返回<code>true</code>，失败返回<code>false</code>
     */
    public boolean upload(String uploadFileName, String cosFileName) {
        logger.info("start to upload local file to cos");
        try {
            COSCredentials cred = new BasicCOSCredentials(this.secretId, this.secretKey);
            Region region = new Region(this.cosRegion);
            ClientConfig clientConfig = new ClientConfig(region);
            clientConfig.setHttpProtocol(HttpProtocol.https);
            COSClient cosClient = new COSClient(cred, clientConfig);
            File localFile = new File(uploadFileName);
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, cosFileName, localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            logger.info("putObjectResult = {}", putObjectResult);
            return !(putObjectResult == null || putObjectResult.getETag() == null);
        } catch (Exception ex) {
            logger.error(ex);
            ex.printStackTrace();
        }
        logger.info("some error happened");
        return false;
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param secretId   COS的SecretId
     * @param secretKey  COS的SecretKey
     * @param cosRegion  COS的地域
     * @param bucketName 存储桶
     * @param accessUrl  访问url的前缀
     */
    public CosService(@Value("${cos.secret-id}") String secretId,
                      @Value("${cos.secret-key}") String secretKey,
                      @Value("${cos.cos-region}") String cosRegion,
                      @Value("${cos.bucket-name}") String bucketName,
                      @Value("${cos.access-url}") String accessUrl) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.cosRegion = cosRegion;
        this.bucketName = bucketName;
        this.accessUrl = accessUrl;
    }

    private final String secretId;
    private final String secretKey;
    private final String cosRegion;
    private final String bucketName;
    private final String accessUrl;

    private final Logger logger = LogManager.getLogger("serviceLogger");
}
