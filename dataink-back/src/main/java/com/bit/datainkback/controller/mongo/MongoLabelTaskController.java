package com.bit.datainkback.controller.mongo;

import com.bit.datainkback.dto.ResponseDto;
import com.bit.datainkback.dto.mongo.TaskSearchCriteria;
import com.bit.datainkback.entity.CustomUserDetails;
import com.bit.datainkback.entity.User;
import com.bit.datainkback.entity.mongo.Folder;
import com.bit.datainkback.entity.mongo.Tasks;
import com.bit.datainkback.repository.mongo.MongoLabelTaskRepository;
import com.bit.datainkback.service.mongo.MongoLabelTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mongo/tasks")
public class MongoLabelTaskController {

    @Autowired
    private MongoLabelTaskService mongoLabelTaskService;

    @Autowired
    private MongoLabelTaskRepository mongoLabelTaskRepository;

    @GetMapping
    public List<Tasks> getAllTasks() {
        return mongoLabelTaskService.getAllTasks();
    }

    @PostMapping
    public Tasks createTask(@RequestBody Tasks task) {
        return mongoLabelTaskService.saveTask(task);
    }

    @GetMapping("/{id}")
    public Tasks getTaskById(@PathVariable String id) {
        return mongoLabelTaskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        mongoLabelTaskService.deleteTask(id);
    }

    // 특정 폴더의 Task 목록 가져오기
    @GetMapping("/folder/{parentFolderId}")
    public ResponseEntity<List<Tasks>> getTasksByFolderId(@PathVariable String parentFolderId) {
        List<Tasks> tasks = mongoLabelTaskRepository.findByParentFolderId(parentFolderId);
        return ResponseEntity.ok(tasks);
    }

    // tasks 컬렉션 데이터들의 status를 submitted로 변경, label_table 테이블에 데이터 생성
    @PutMapping("/update-submit")
    public ResponseEntity<?> updateTaskStatus(@RequestBody List<String> taskIds,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDto<Tasks> responseDto = new ResponseDto<>();
        User joinedUser = customUserDetails.getUser();

        try {
            List<Tasks> submittedTasks = mongoLabelTaskService.submitForReview(taskIds, joinedUser);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");
            responseDto.setItems(submittedTasks);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("id-check error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<Tasks>> searchTasks(@RequestBody TaskSearchCriteria criteria) {
        // 프론트엔드에서 전달된 폴더 구조 사용
        List<Folder> folderItems = criteria.getFolderItems();

        // 폴더 ID 수집
        List<String> folderIds = mongoLabelTaskService.getFolderIdsBasedOnCategories(criteria, folderItems);

        // 해당 폴더의 Task 검색
        List<Tasks> tasks = mongoLabelTaskService.searchTasks(folderIds, criteria.getSearchKeyword(), criteria.getWorkStatus());
        return ResponseEntity.ok(tasks);
    }
}
