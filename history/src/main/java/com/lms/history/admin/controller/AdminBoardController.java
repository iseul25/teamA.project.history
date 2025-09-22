package com.lms.history.admin.controller;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.service.BoardService;
import com.lms.history.boards.service.FileUploadService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

    private final BoardService boardService;
    private final FileUploadService fileUploadService;

    /**
     * 관리자 게시글 목록 페이지
     */
    @GetMapping
    public String adminBoardList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "전체") String boardType,
            Model model,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        List<Board> allBoards;

        // 게시글 조회
        if ("전체".equals(boardType)) {
            allBoards = boardService.findAll();
        } else {
            allBoards = boardService.findByBoardType(boardType);
        }

        // 수동 페이징 처리
        int pageSize = 10;
        int totalElements = allBoards.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        List<Board> pagedBoards = allBoards.subList(startIndex, endIndex);

        model.addAttribute("boards", pagedBoards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentBoardType", boardType);

        return "admin/adminBoardList";
    }

    /**
     * 게시글 정보 API (수정 모달용)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Board> getBoard(@PathVariable int id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Board board = boardService.findById(id);
            if (board == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/edit")
    public String editBoard(
            @RequestParam int id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imgDescription", required = false) String imgDescription,
            @RequestParam(defaultValue = "전체") String boardType,
            @RequestParam(defaultValue = "0") int page,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/";
        }

        try {
            Board board = boardService.findById(id);
            if (board != null) {
                board.setTitle(title);

                String cleanContent = removeImageHtmlFromContent(content);
                board.setContent(cleanContent);

                if (imageFile != null && !imageFile.isEmpty()) {
                    String imageUrl = fileUploadService.saveFile(imageFile);
                    board.setImgUrl(imageUrl);

                    if (imgDescription != null && !imgDescription.trim().isEmpty()) {
                        board.setImgDescription(imgDescription.trim());
                    }
                }

                boardService.update(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // boardType과 page 파라미터를 유지하여 리다이렉트
        try {
            String encodedBoardType = URLEncoder.encode(boardType, StandardCharsets.UTF_8);
            return "redirect:/admin/board?page=" + page + "&boardType=" + encodedBoardType;
        } catch (Exception e) {
            // 인코딩 실패 시 기본값으로 리다이렉트
            return "redirect:/admin/board?page=0&boardType=전체";
        }
    }

    private String removeImageHtmlFromContent(String content) {
        if (content == null) return null;

        // 이미지 HTML 태그 패턴 제거 (정규식 사용)
        String pattern = "<img[^>]*>\\s*(<br/>|<br>)*";
        return content.replaceAll(pattern, "").trim();
    }

    /**
     * 게시글 삭제 처리
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteBoard(@PathVariable int id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        try {
            boardService.delete(id);
            return ResponseEntity.ok("삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("삭제에 실패했습니다.");
        }
    }

    /**
     * 관리자 권한 확인 - User 엔티티에 맞춤
     */
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) return false;

        return "관리자".equals(user.getUserType());
    }
}