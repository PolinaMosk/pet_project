package s4s.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import s4s.chat.Dialog;
import s4s.chat.Message;
import s4s.entity.Status;
import s4s.entity.User;
import s4s.repository.DialogRepository;
import s4s.repository.UserRepository;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    DialogRepository dialog_repo;
    @Autowired
    UserRepository user_repo;

    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/message")
    public Message send(Message message){
        message.setStatus(Status.SENT);
        message.setSend_date(new Date());
        User u1 = user_repo.findUserByLogin(message.getSender_login()).get();
        User u2 = user_repo.findUserByLogin(message.getReceiver_login()).get();
        Optional<Dialog> dialog1 = dialog_repo.findById(u1.getId() + "_" + u2.getId());
        Optional<Dialog> dialog2 = dialog_repo.findById(u2.getId() + "_" + u1.getId());
        if (dialog1.isPresent()) {
            List<Message> msgs = dialog1.get().getMessages();
            msgs.add(message);
            dialog1.get().setMessages(msgs);
            dialog_repo.save(dialog1.get());
        } else if (dialog2.isPresent()) {
            List<Message> msgs = dialog2.get().getMessages();
            msgs.add(message);
            dialog2.get().setMessages(msgs);
            dialog_repo.save(dialog2.get());
        } else {
            List<Message> msgs = new ArrayList<>();
            msgs.add(message);
            Dialog dialog = new Dialog(u1.getId(), u2.getId(), msgs);
            dialog_repo.save(dialog);
        }
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver_login(), "/msg", message);
        return message;
    }

    @GetMapping("/dialogs/{id1}/{id2}")
    public List<Message> loadDialog(@PathVariable Long id1, @PathVariable Long id2) {
        Optional<Dialog> dialog1 = dialog_repo.findById(id1 + "_" + id2);
        if (dialog1.isPresent()) {
            if (dialog1.get().getMessages().size() > 40) {
                dialog1.get().getMessages().remove(dialog1.get().getMessages().subList(0, 9));
                dialog_repo.save(dialog1.get());
            }
            return dialog1.get().getMessages();
        }
        Optional<Dialog> dialog2 = dialog_repo.findById(id2 + "_" + id1);
        if (dialog2.isPresent()) {
            if (dialog2.get().getMessages().size() > 40) {
                dialog2.get().getMessages().remove(dialog2.get().getMessages().subList(0, 9));
                dialog_repo.save(dialog2.get());
            }
            return dialog2.get().getMessages();
        }
        return null;
    }

    @GetMapping("/all_messages")
    public List<Message> getAllMessages(){
        List<Message> msgs = new ArrayList<>();
        for (Dialog d: dialog_repo.findAll()){
            msgs.addAll(d.getMessages());
        }
        return msgs;
    }
}
