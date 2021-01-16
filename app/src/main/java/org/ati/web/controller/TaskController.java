package org.ati.web.controller;

import org.ati.core.model.Group;
import org.ati.core.model.Task;
import org.ati.core.model.UserDTO;
import org.ati.core.service.GroupService;
import org.ati.core.service.TaskService;
import org.ati.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    @GetMapping("/taskList")
    public String listTasks(Model model, Principal principal) {

        model.addAttribute("taskList", taskService.listTasks(principal.getName()));

        return "mytasks";
    }

    @GetMapping("/task/{id}")
    public String getTask(Model model, @PathVariable Long id, Principal principal) {
        Task task = taskService.getOne(id);
        List<UserDTO> list = new ArrayList<>(task.getGroup().getMembers());
        list.sort(Comparator.comparing(UserDTO::getName));
        boolean voted = task.getAuthenticatedUserVoted().get(userService.findByUsername(principal.getName())) != null && task.getAuthenticatedUserVoted().get(userService.findByUsername(principal.getName()));

        model.addAttribute("task", task);
        model.addAttribute("members", list);
        model.addAttribute("votedFor", userService.findByUsername(principal.getName()).getVotedFor().get(task));
        model.addAttribute("voted", voted);


        return "task";
    }

    @PostMapping("/vote/{id}")
    public String vote(Model model, @PathVariable Long id, Principal principal, HttpServletRequest request) {
        UserDTO userDTO = userService.findByUsername(principal.getName());
        Task task = taskService.getOne(id);

        if (request.getParameter("vote") != null) {
            UserDTO votedUser = userService.getOne(Long.parseLong(request.getParameter("vote")));
            UserDTO previousVote = userDTO.getVotedFor().get(task);
            if (previousVote == null) {
                userDTO.getVotedFor().put(task, votedUser);
                if (task.getVotes().containsKey(votedUser)) {
                    int prevVote = task.getVotes().get(votedUser);
                    task.getVotes().replace(votedUser, prevVote + 1);
                } else {
                    task.getVotes().put(votedUser, 1);
                }
                task.getAuthenticatedUserVoted().put(userDTO, true);
            } else {
                if (votedUser != previousVote) {
                    task.getVotes().replace(previousVote, task.getVotes().get(previousVote) - 1);
                    userDTO.getVotedFor().replace(task, votedUser);
                    if (task.getVotes().containsKey(votedUser)) {
                        int prevVote = task.getVotes().get(votedUser);
                        task.getVotes().replace(votedUser, prevVote + 1);
                    } else {
                        task.getVotes().put(votedUser, 1);
                    }
                }
            }
        }
        task.setVoteFinished(task.getVotes().size() == task.getGroup().getMembers().size());
        taskService.save(task);
        userService.save(userDTO);

        return "redirect:/task/" + id;
    }

    @PostMapping("/saveTask")
    public String saveTask(@ModelAttribute("taskObj") Task task, HttpServletRequest request) {
        Group group = groupService.getOne(Long.parseLong(request.getParameter("groupVar")));
        task.setGroup(group);
        group.getTasks().add(task);

        taskService.save(task);
        groupService.save(group);
        return "redirect:/groups/" + group.getId();
    }
}
