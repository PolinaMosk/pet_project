package s4s.web;

import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import s4s.entity.User;
import s4s.repository.UserRepository;
import s4s.storage.FileInfo;
import s4s.storage.FileStorageService;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserRepository user_repo;

    @PostMapping(value = "/user/{id}/uploadAvatar", consumes = {"multipart/form-data"})
    public FileInfo uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws FileUploadException {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        Optional<User> user = user_repo.findById(id);
        user.get().setAvatar_file(fileName);
        user_repo.save(user.get());

        return new FileInfo(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

}