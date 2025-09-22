package com.lms.history.boards.controller;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.entity.Comment;
import com.lms.history.boards.entity.CommentReply;
import com.lms.history.boards.service.BoardService;
import com.lms.history.boards.service.CommentReplyService;
import com.lms.history.boards.service.CommentService;
import com.lms.history.boards.service.FileUploadService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final FileUploadService fileUploadService;
    private final CommentReplyService replyService;

    public BoardController(BoardService boardService, CommentService commentService,
                           FileUploadService fileUploadService, CommentReplyService replyService) {
        this.boardService = boardService;
        this.commentService = commentService;
        this.fileUploadService = fileUploadService;
        this.replyService = replyService;
    }

    // 시대 선택 페이지
    @GetMapping("/topic")
    public String boardTopic(HttpSession session, Model model) {
        return "board/boardTopic";
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public String list(@RequestParam(value="boardType", required=false) String boardType,
                       HttpSession session,
                       Model model) {

        session.setAttribute("selectedBoardType", boardType);

        List<Board> boards = boardService.findByBoardType(boardType);
        model.addAttribute("boards", boards);
        model.addAttribute("selectedBoardType", boardType);

        return "board/listBoard";
    }

    // 게시글 상세보기 (댓글 목록 포함)
    @GetMapping("/detail")
    public String detail(@RequestParam("boardId") int boardId, HttpSession session, Model model) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            Board board = boardService.findById(boardId);
            if (board == null) {
                return "redirect:/board/list?error=notfound";
            }
            List<Comment> comments = commentService.findByBoardId(boardId);

            // 각 댓글의 답글 조회
            Map<Integer, List<CommentReply>> repliesMap = new HashMap<>();
            for (Comment comment : comments) {
                List<CommentReply> replies = replyService.findByCommentId(comment.getCommentId());
                repliesMap.put(comment.getCommentId(), replies);
            }

            model.addAttribute("loginUser", loginUser);
            model.addAttribute("board", board);
            model.addAttribute("comments", comments);
            model.addAttribute("repliesMap", repliesMap);
            return "board/detailBoard";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/board/list?error=database";
        }
    }

    // 댓글 작성 처리
    @PostMapping("/detail")
    public String createComment(@RequestParam("boardId") int boardId,
                                @RequestParam("content") String content,
                                HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/board/detail?boardId=" + boardId;
        }

        try {
            commentService.create(boardId, loginUser.getUserId(), content);
        } catch (IllegalArgumentException e) {
        }

        return "redirect:/board/detail?boardId=" + boardId;
    }

    // 댓글 삭제 처리
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam int commentId,
                                @RequestParam int boardId,
                                HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/board/detail?boardId=" + boardId;
        }

        try {
            replyService.deleteByCommentId(commentId);
            commentService.delete(commentId, loginUser.getUserId(), loginUser.getUserType());
        } catch (IllegalArgumentException e) {
        }

        return "redirect:/board/detail?boardId=" + boardId;
    }

    // 답글 작성 처리
    @PostMapping("/reply")
    public String createReply(@RequestParam("commentId") int commentId,
                              @RequestParam("boardId") int boardId,
                              @RequestParam("content") String content,
                              HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/board/detail?boardId=" + boardId;
        }

        try {
            replyService.create(commentId, loginUser.getUserId(), content);
        } catch (IllegalArgumentException e) {
        }

        return "redirect:/board/detail?boardId=" + boardId;
    }

    // 답글 삭제 처리
    @PostMapping("/reply/delete")
    public String deleteReply(@RequestParam int replyId,
                              @RequestParam int boardId,
                              HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/board/detail?boardId=" + boardId;
        }

        try {
            replyService.delete(replyId, loginUser.getUserId(), loginUser.getUserType());
        } catch (IllegalArgumentException e) {
        }

        return "redirect:/board/detail?boardId=" + boardId;
    }

    // 게시글 등록 폼
    @GetMapping("/create")
    public String createForm(@RequestParam(value="boardType", required=false) String boardType,
                             HttpSession session, Model model) {

        session.setAttribute("selectedBoardType", boardType);

        if (boardType == null) boardType = "default";

        Board board = new Board();
        board.setBoardType(boardType);
        model.addAttribute("board", board);
        return "board/createBoard";
    }

    // 게시글 등록 처리
    @PostMapping("/create")
    public String create(@ModelAttribute Board board,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         @RequestParam(value = "imgDescription", required = false) String imgDescription,
                         RedirectAttributes redirectAttributes,
                         Model model,
                         HttpSession session) {
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser == null) {
                return "redirect:/login";
            }

            // 이미지 파일 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileUploadService.saveFile(imageFile);
                board.setImgUrl(imageUrl);

                if (imgDescription != null && !imgDescription.trim().isEmpty()) {
                    board.setImgDescription(imgDescription.trim());
                }

                // 이미지를 내용 위에 추가 (핵심 변경사항)
                String imageHtml = String.format(
                        "<img src='%s' alt='%s' style='max-width: 100%%; height: auto; margin: 15px 0; border: 1px solid #ddd; border-radius: 5px; display: block;'/>",
                        imageUrl,
                        board.getImgDescription() != null ? board.getImgDescription() : ""
                );

                // 이미지 설명 추가
                if (board.getImgDescription() != null && !board.getImgDescription().trim().isEmpty()) {
                    imageHtml += String.format(
                            "<p style='color: #666; font-style: italic; text-align: center; margin-top: 5px; margin-bottom: 15px;'>%s</p>",
                            board.getImgDescription()
                    );
                }

                // 이미지 HTML을 내용 앞에 추가
                board.setContent(imageHtml + "<br/>" + board.getContent());
            }

            boardService.create(board, loginUser.getUserId());

            if ("공지사항".equals(board.getBoardType())) {
                return "redirect:/notice";
            }

            redirectAttributes.addAttribute("boardType", board.getBoardType());
            return "redirect:/board/list";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("selectedBoardType", board.getBoardType());
            return "board/createBoard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "게시글 등록 중 오류가 발생했습니다.");
            model.addAttribute("selectedBoardType", board.getBoardType());
            return "board/createBoard";
        }
    }

    // 게시글 수정 폼
    @GetMapping("/edit")
    public String editForm(@RequestParam("boardId") int boardId, Model model) {
        Board board = boardService.findById(boardId);
        model.addAttribute("board", board);
        model.addAttribute("selectedBoardType", board.getBoardType());
        return "board/editBoard";
    }

    // 게시글 수정 처리
    @PostMapping("/edit")
    public String edit(@ModelAttribute Board board,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       @RequestParam(value = "imgDescription", required = false) String imgDescription,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            // 새 이미지가 업로드된 경우
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = null;
                try {
                    imageUrl = fileUploadService.saveFile(imageFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                board.setImgUrl(imageUrl);
            }

            // 이미지 설명 처리
            if (imgDescription != null && !imgDescription.trim().isEmpty()) {
                board.setImgDescription(imgDescription.trim());
            }

            boardService.update(board);

            if ("공지사항".equals(board.getBoardType())) {
                return "redirect:/notice";
            }

            redirectAttributes.addAttribute("boardType", board.getBoardType());
            return "redirect:/board/list";

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("selectedBoardType", board.getBoardType());
            return "board/editBoard";
        }
    }

    // 게시글 삭제 처리
    @PostMapping("/delete")
    public String delete(@RequestParam int boardId, RedirectAttributes redirectAttributes) {
        Board board = boardService.findById(boardId);
        String boardType = board.getBoardType();

        // 게시글 삭제 전에 관련 댓글도 삭제
        commentService.deleteByBoardId(boardId);
        boardService.delete(boardId);

        if ("공지사항".equals(board.getBoardType())) {
            return "redirect:/notice";
        }

        redirectAttributes.addAttribute("boardType", boardType);
        return "redirect:/board/list";
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            String imageUrl = fileUploadService.saveFile(file);
            response.put("url", imageUrl);
        } catch (IllegalArgumentException e) {
            response.put("error", Map.of("message", e.getMessage()));
        } catch (Exception e) {
            response.put("error", Map.of("message", "이미지 업로드 실패"));
        }
        return response;
    }
}

