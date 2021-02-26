package s4s.web;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s4s.entity.Request;
import s4s.entity.Status;
import s4s.entity.User;
import s4s.repository.RequestRepository;
import s4s.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class RequestController {
    @Autowired
    RequestRepository req_repo;
    @Autowired
    UserRepository user_repo;

    @GetMapping("/requests")
    public List<Request> getAllRequests(){
        return req_repo.findAll();
    }
    @PostMapping("/requests/create/{sender_id}/{receiver_id}")
    public ResponseEntity<?> createRequest(@PathVariable Long sender_id, @PathVariable Long receiver_id, @RequestBody Request request) throws NotFoundException {
        Optional<User> user_sender = user_repo.findById(sender_id);
        Optional<User> user_receiver = user_repo.findById(receiver_id);
        if (user_receiver.get().getIsOpenForRequests() == false) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (user_sender.isPresent() && user_receiver.isPresent()){
            request.setSender(user_sender.get());
            request.setReceiver(user_receiver.get());
            request.setStatus(Status.SENT);
        } else throw new NotFoundException("users not found");
        req_repo.save(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/requests/in_requests/{user_id}")
    public Set<Request> getInRequests(@PathVariable Long user_id){
        Optional<User> user = user_repo.findById(user_id);
        return req_repo.findAllByReceiver(user.get());
    }

    @GetMapping("/requests/out_requests/{user_id}")
    public Set<Request> getOutRequests(@PathVariable Long user_id){
        Optional<User> user = user_repo.findById(user_id);
        return req_repo.findAllBySender(user.get());
    }

    @PutMapping("/requests/change_status/{id}/{status}")
    public Request changeStatus(@PathVariable Long id, @PathVariable Status status) throws NotFoundException {
        Optional<Request> request = req_repo.findById(id);
        if (request.isPresent()) {
            request.get().setStatus(status);
        } else throw new NotFoundException("request not found");
        return req_repo.save(request.get());
    }

    @DeleteMapping("/requests/cancel_sending/{id}")
    public ResponseEntity<?> cancelSending(@PathVariable Long id) {
        Optional<Request> req = req_repo.findById(id);
        req.get().setSender(null);
        req.get().setReceiver(null);
        req_repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/requests/deny/{id}")
    public ResponseEntity<?> denyRequest(@PathVariable Long id) {
        Optional<Request> req = req_repo.findById(id);
        req.get().setReceiver(null);
        req.get().setStatus(Status.DECLINED);
        req_repo.save(req.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/requests/{id}/close")
    public ResponseEntity<?> closeForRequests(@PathVariable Long id) {
        Optional<User> user = user_repo.findById(id);
        user.get().setIsOpenForRequests(false);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
