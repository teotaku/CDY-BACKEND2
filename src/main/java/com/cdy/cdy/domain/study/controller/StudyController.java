package com.cdy.cdy.domain.study.controller;

import com.cdy.cdy.domain.study.dto.RequestStudy;
import com.cdy.cdy.domain.study.dto.ResponseStudy;
import com.cdy.cdy.domain.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/study")
@RestController
public class StudyController {


    private final StudyService studyService;



    @Operation(summary = "스터디 글 작성", description = """
            스터디글과 스터디글에 포함된 스터디이미지를 작성하는 API
            로그인한 사용자만 접근가능.
            """)
    @PostMapping("/create")
    public ResponseEntity<?> createStudy(Authentication authentication,
                                         @RequestBody RequestStudy dto) {

        studyService.createStudy(authentication.getName(), dto);
        return ResponseEntity.ok("스터디 글이 작성되었습니다.");

    }

    @Operation(summary = "스터디 글 삭제", description = """
            스터디 글 삭제로직, soft_delete 처리 ,
            글의 작성자만 삭제가능
            """)
    @DeleteMapping("/delete/{studyId}")
    public ResponseEntity<?> deleteStudy(Authentication authentication, @PathVariable("studyId") Long studyId) {

        studyService.deleteStudy(authentication.getName(), studyId);
        return ResponseEntity.ok("스터디 글이 삭제 되었습니다.");

    }

    @Operation(summary = "스터디 글 수정", description = """
            스터디 글 수정 로직 , 새로들어온 dto값으로 전체 변경,
            이미지 마찬가지(이미지 list가 비어있을경우 이미지 전체삭제)
            """)

    @PutMapping("/update/studyId")
    public ResponseEntity<?> updateStudy(Authentication authentication, @PathVariable("studyId") Long studyId,
                                         @RequestBody RequestStudy dto) {

        studyService.updateStudy(authentication.getName(), dto, studyId);
        return ResponseEntity.ok("스터디 글이 수정 되었습니다.");
    }

    @Operation(summary = "스터디 글 단건 상세조회", description = """
            스터디 ID를 기준으로 해당하는 스터디글 상세조회 로직
            """)
    @GetMapping("/findById/{studyId}")
    public ResponseEntity<ResponseStudy> findById(@PathVariable("studyId") Long studyId) {

        ResponseStudy responseStudy = studyService.findById(studyId);
        return ResponseEntity.ok(responseStudy);

    }
}
