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
    RequestRepository reqRepo;
    @Autowired
    UserRepository userRepo;

    @GetMapping("/requests")
    public List<Request> getAllRequests(){
        return reqRepo.findAll();
    }
    @PostMapping("/requests/create/{sender_id}/{receiver_id}")
    public ResponseEntity<?> createRequest(@PathVariable Long sender_id, @PathVariable Long receiver_id, @RequestBody Request request) throws NotFoundException {
        Optional<User> user_sender = userRepo.findById(sender_id);
        Optional<User> user_receiver = userRepo.findById(receiver_id);
        if (user_receiver.get().getIsOpenForRequests() == false) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (user_sender.isPresent() && user_receiver.isPresent()){
            request.setSender(user_sender.get());
            request.setReceiver(user_receiver.get());
            request.setStatus(Status.SENT);
        } else throw new NotFoundException("users not found");
        reqRepo.save(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/requests/in_requests/{user_id}")
    public Set<Request> getInRequests(@PathVariable Long user_id){
        Optional<User> user = userRepo.findById(user_id);
        return reqRepo.findAllByReceiver(user.get());
    }

    @GetMapping("/requests/out_requests/{user_id}")
    public Set<Request> getOutRequests(@PathVariable Long user_id){
        Optional<User> user = userRepo.findById(user_id);
        return reqRepo.findAllBySender(user.get());
    }

    @PutMapping("/requests/change_status/{id}/{status}")
    public Request changeStatus(@PathVariable Long id, @PathVariable Status status) throws NotFoundException {
        Optional<Request> request = reqRepo.findById(id);
        if (request.isPresent()) {
            request.get().setStatus(status);
        } else throw new NotFoundException("request not found");
        return reqRepo.save(request.get());
    }

    @DeleteMapping("/requests/cancel_sending/{id}")
    public ResponseEntity<?> cancelSending(@PathVariable Long id) {
        Optional<Request> req = reqRepo.findById(id);
        req.get().setSender(null);
        req.get().setReceiver(null);
        reqRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/requests/deny/{id}")
    public ResponseEntity<?> denyRequest(@PathVariable Long id) {
        Optional<Request> req = reqRepo.findById(id);
        req.get().setReceiver(null);
        req.get().setStatus(Status.DECLINED);
        reqRepo.save(req.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/requests/{id}/close")
    public ResponseEntity<?> closeForRequests(@PathVariable Long id) {
        Optional<User> user = userRepo.findById(id);
        user.get().setIsOpenForRequests(false);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
