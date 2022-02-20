package hello.service;

import hello.entity.ResourceResult;
import hello.entity.UploadResult;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UploadService {
    private final Path rootLocation;

    public UploadService() {
        this.rootLocation = Paths.get("uploadssss");
    }

    public void init() throws Exception {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new Exception("Could not initialize storage", e);
        }
    }

    public UploadResult store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return UploadResult.failure("似乎没有上传文件呢");
            }
            String newFileName = "czglcjzx" + file.getOriginalFilename();
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(newFileName))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                return UploadResult.failure("只能在当前文件夹上传文件");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
                return UploadResult.Success(newFileName);
            }
        }
        catch (IOException e) {
            return UploadResult.failure("出错了");
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public ResourceResult loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResourceResult.success(resource);
            }
            else {
                return ResourceResult.failure("不能读取文件"+filename);

            }
        }
        catch (MalformedURLException e) {
            return ResourceResult.failure("不能读取文件"+filename);

        }
    }


    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
