package org.ati.web.controller;

import org.ati.core.model.*;
import org.ati.core.service.CommentService;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private CommentService commentService;

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
        String concatenatedUserTaskId = currentUser.getId() + ":" + task.getId();
        //TODO LocalDateTime.now() kiszamithatatlan
        task.setVoteFinished(LocalDateTime.now().plusHours(1).atOffset(ZoneOffset.UTC).isAfter(task.getValidTo().atOffset(ZoneOffset.UTC)));
        String winnerVote = "";

        if (task.isVoteFinished()) {
            String[] array = getMaxEntryInMapBasedOnValue(task.getAnswers()).split("\\:");
            winnerVote = array[1];
            if (task.getTypeEnum().equals(SystemConstants.TypeEnum.TASK)) {
                String[] assigned = getMaxEntryInMapBasedOnValue(task.getVotes()).split("\\:");
                String userId = assigned[0];
                task.setAssignedUser(userService.getOne(Long.parseLong(userId)));
            }
        }
        taskService.save(task);
        boolean isAssignedUser = false;
        if (task.getAssignedUser() != null) {
            isAssignedUser = task.getAssignedUser() == currentUser;
        }


        model.addAttribute("isAssignedUser", isAssignedUser);
        model.addAttribute("task", task);
        model.addAttribute("members", userDTOList);
        model.addAttribute("votedFor", currentUser.getVotedFor().get(concatenatedUserTaskId));
        model.addAttribute("voted", voted);
        model.addAttribute("answered", answered);
        model.addAttribute("optionals", answerList);
        model.addAttribute("userAnswered", currentUser.getAnswered().get(task));
        model.addAttribute("question", task.getTypeEnum() == SystemConstants.TypeEnum.QUESTION);
        model.addAttribute("winnerVote", winnerVote);

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

        //TODO nem lehet még ugyanarra a userre szavazni hozzárendelés esetén, már egyszer jó volt
        if (request.getParameter("vote") != null) {
            UserDTO votedUser = userService.getOne(Long.parseLong(request.getParameter("vote")));

            String concatenatedVotedUserTaskId = votedUser.getId() + ":" + task.getId();
            String concatenatedUserTaskId = userDTO.getId() + ":" + task.getId();

            UserDTO previousVote = userDTO.getVotedFor().get(concatenatedUserTaskId);
            String concatenatedPreviousVoteTaskId;

            if (previousVote == null) {
                userDTO.getVotedFor().put(concatenatedUserTaskId, votedUser);
                if (task.getVotes().containsKey(concatenatedVotedUserTaskId)) {
                    int prevVote = task.getVotes().get(concatenatedVotedUserTaskId);
                    task.getVotes().replace(concatenatedVotedUserTaskId, prevVote + 1);
                } else {
                    task.getVotes().put(concatenatedVotedUserTaskId, 1);
                }
                task.getAuthenticatedUserVoted().put(userDTO, true);
            } else {
                if (votedUser != previousVote) {
                    concatenatedPreviousVoteTaskId = previousVote.getId() + ":" + task.getId();
                    task.getVotes().replace(concatenatedPreviousVoteTaskId, task.getVotes().get(concatenatedPreviousVoteTaskId) - 1);
                    userDTO.getVotedFor().replace(concatenatedUserTaskId, votedUser);
                    if (task.getVotes().containsKey(concatenatedVotedUserTaskId)) {
                        int prevVote = task.getVotes().get(concatenatedPreviousVoteTaskId);
                        task.getVotes().replace(concatenatedVotedUserTaskId, prevVote + 1);
                    } else {
                        task.getVotes().put(concatenatedVotedUserTaskId, 1);
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

    @PostMapping("/finish/{id}")
    public String finishTask(@PathVariable Long id, Principal principal) {
        Task task = taskService.getOne(id);
        task.setStatusEnum(SystemConstants.StatusEnum.DONE);
        taskService.save(task);
        return "redirect:/task/" + id;
    }

    @PostMapping("/saveTask")
    public String saveTask(@ModelAttribute("taskObj") Task task, HttpServletRequest request) {
        Group group = groupService.getOne(Long.parseLong(request.getParameter("groupVar")));
        task.setGroup(group);
        if (request.getParameter("validTime") != null) {
            String str = request.getParameter("validTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
            if (dateTime.atOffset(ZoneOffset.UTC).isAfter(LocalDateTime.now().atOffset(ZoneOffset.UTC))) {
                task.setValidTo(dateTime);
            } else {
                task.setValidTo(LocalDateTime.now().plusDays(1));
            }
        }
        group.getTasks().add(task);

        taskService.save(task);
        groupService.save(group);
        return "redirect:/groups/" + group.getId();
    }

    @PostMapping("/comment/{id}")
    public String comment(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        Task task = taskService.getOne(id);
        UserDTO userDTO = userService.findByUsername(principal.getName());
        String message = request.getParameter("comment");

        if (message != null) {
            Comment comment = new Comment();
            comment.setCommentedBy(userDTO);
            comment.setMessage(message);
            comment.setDate(LocalDateTime.now());
            commentService.save(comment);

            task.getComments().add(comment);
            taskService.save(task);
        }

        return "redirect:/task/" + id;

    }

    /**
     * The user from param votes for answer
     * @param userDTO user, who votes
     * @param task task
     * @param answer answered string
     */
    private void vote(UserDTO userDTO, Task task, String answer) {
        String previousAnswer = userDTO.getAnswered().get(task);
        task.getOptionalAnswers().add(answer);

        String concatenatedAnswerTask = task.getId() + ":" + answer;

        if (previousAnswer == null) {
            userDTO.getAnswered().put(task, concatenatedAnswerTask);
            if (task.getAnswers().containsKey(concatenatedAnswerTask)) {
                int prevAnswerSum = task.getAnswers().get(concatenatedAnswerTask);
                task.getAnswers().replace(concatenatedAnswerTask, prevAnswerSum + 1);
            } else {
                task.getAnswers().put(concatenatedAnswerTask, 1);
            }
        } else {
            if (!concatenatedAnswerTask.equals(previousAnswer)) {
                task.getAnswers().replace(previousAnswer, task.getAnswers().get(previousAnswer) - 1);
                userDTO.getAnswered().replace(task, concatenatedAnswerTask);
                if (task.getAnswers().containsKey(concatenatedAnswerTask)) {
                    task.getAnswers().replace(concatenatedAnswerTask, task.getAnswers().get(answer) + 1);
                } else {
                    task.getAnswers().put(concatenatedAnswerTask, 1);
                }
            }
        }
    }

    public static <K, V extends Comparable<V> > K getMaxEntryInMapBasedOnValue(Map<K, V> map) {
        // To store the result
        Map.Entry<K, V> entryWithMaxValue = null;

        // Iterate in the map to find the required entry
        for (Map.Entry<K, V> currentEntry : map.entrySet()) {
            if (entryWithMaxValue == null || currentEntry.getValue().compareTo(entryWithMaxValue.getValue()) > 0) {
                entryWithMaxValue = currentEntry;
            }
        }
        // Return the entry with highest value
        return entryWithMaxValue.getKey();
    }
}
