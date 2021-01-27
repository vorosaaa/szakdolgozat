package org.ati.web.controller;

import org.apache.catalina.User;
import org.ati.core.model.*;
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
import java.util.Map;

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
        UserDTO currentUser = userService.findByUsername(principal.getName());
        List<UserDTO> userDTOList = new ArrayList<>(task.getGroup().getMembers());
        userDTOList.sort(Comparator.comparing(UserDTO::getName));
        List<String> answerList = new ArrayList<>(task.getOptionalAnswers());
        answerList.sort(Comparator.naturalOrder());
        boolean voted = task.getAuthenticatedUserVoted().get(currentUser) != null && task.getAuthenticatedUserVoted().get(currentUser);
        boolean answered = currentUser.getAnswered().get(task) != null;

        model.addAttribute("task", task);
        model.addAttribute("members", userDTOList);
        model.addAttribute("votedFor", currentUser.getVotedFor().get(task));
        model.addAttribute("voted", voted);
        model.addAttribute("answered", answered);
        model.addAttribute("optionals", answerList);
        model.addAttribute("userAnswered", currentUser.getAnswered().get(task));
        model.addAttribute("question", task.getTypeEnum() == SystemConstants.TypeEnum.QUESTION);

        return "task";
    }

    @PostMapping("/vote/{id}")
    public String vote(Model model, @PathVariable Long id, Principal principal, HttpServletRequest request) {
        UserDTO userDTO = userService.findByUsername(principal.getName());
        Task task = taskService.getOne(id);
        String newAnswer = request.getParameter("newAnswer");
        String answer = request.getParameter("answer");

        if (newAnswer != null || answer  != null) {
            String finalAnswer = (newAnswer != null && !newAnswer.isEmpty()) ? newAnswer : answer;
            vote(userDTO, task, finalAnswer);
        }

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
        int sum = 0;
        for (Map.Entry<String, Integer> entry : task.getAnswers().entrySet()) {
            sum += entry.getValue();
        }
//        task.setEverybodyAnswered(sum == task.getGroup().getMembers().size());
//        task.setVoteFinished(task.getVotes().size() == task.getGroup().getMembers().size());
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

    private void vote(UserDTO userDTO, Task task, String answer) {
        String previousAnswer = userDTO.getAnswered().get(task);
        task.getOptionalAnswers().add(answer);

        if (previousAnswer == null) {
            userDTO.getAnswered().put(task, answer);
            if (task.getAnswers().containsKey(answer)) {
                int prevAnswerSum = task.getAnswers().get(answer);
                task.getAnswers().replace(answer, prevAnswerSum + 1);
            } else {
                task.getAnswers().put(answer, 1);
            }
        } else {
            if (!answer.equals(previousAnswer)) {
                task.getAnswers().replace(previousAnswer, task.getAnswers().get(previousAnswer) - 1);
                userDTO.getAnswered().replace(task, answer);
                if (task.getAnswers().containsKey(answer)) {
                    task.getAnswers().replace(answer, task.getAnswers().get(answer) + 1);
                } else {
                    task.getAnswers().put(answer, 1);
                }
            }
        }
    }
}
