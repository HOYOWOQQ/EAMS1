package com.eams.Service.score;

import com.eams.Entity.score.Exam;
import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.DTO.ExamDTO;
import com.eams.Entity.score.DTO.ExamPaperDTO;
import com.eams.Repository.score.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExamService {

	@Autowired
	private ExamRepository examRepo;

	@Transactional(readOnly = true)
	public Page<Exam> pageCards(String q, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "examDate", "id"));
		if (q == null || q.isBlank())
			return examRepo.findAll(pageable);
		return examRepo.findByNameContainingIgnoreCase(q, pageable);
	}

	@Transactional(readOnly = true)
	public List<ExamPaperDTO> listPapersOfExam(Integer examId) {
		List<ExamPaper> papers = examRepo.findPapersByExamId(examId);
		return papers.stream().map(com.eams.Entity.score.DTO.ExamPaperDTO::fromEntity).toList();
	}

	@Transactional(readOnly = true)
	public List<Exam> searchWithPapers(String q) {
		return examRepo.searchWithPapers(q == null ? "" : q);
	}
	
	 /** 單筆（DTO） */
    @Transactional(readOnly = true)
    public ExamDTO findExamDTOById(Integer id) {
        if (id == null) return null;
        return examRepo.findById(id)
                .map(ExamDTO::fromEntity)
                .orElse(null);
    }

    /**
     * 清單（DTO）
     * - q 為空：回傳全部（用 Sort 排序）
     * - q 有值：用分頁抓前 limit 筆（避免你現在改 repository）
     */
    @Transactional(readOnly = true)
    public List<ExamDTO> listExamsDTO(String q, int limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "examDate", "id");
        List<Exam> list;
        if (q == null || q.isBlank()) {
            list = examRepo.findAll(sort);
        } else {
            if (limit <= 0) limit = 500; // 安全上限
            Page<Exam> page = examRepo.findByNameContainingIgnoreCase(
                    q, PageRequest.of(0, limit, sort));
            list = page.getContent();
        }
        return list.stream().map(ExamDTO::fromEntity).collect(Collectors.toList());
    }

    /** 新增或更新（DTO 進、DTO 出） */
    @Transactional
    public ExamDTO saveExamDTO(ExamDTO dto) {
        Objects.requireNonNull(dto, "ExamDTO 不可為空");
        Exam entity = (dto.getId() != null)
                ? examRepo.findById(dto.getId()).orElse(new Exam())
                : new Exam();

        // 只覆蓋有傳入的欄位，避免把舊值洗成 null
        if (dto.getName() != null)        entity.setName(dto.getName());
        if (dto.getType() != null)        entity.setType(dto.getType());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getExamDate() != null)    entity.setExamDate(dto.getExamDate());
        // createTime 交由 DB/Entity 設定

        // 如果是新建，補上 id（Exam 的 id 交由 DB 產生）
        if (dto.getId() != null) entity.setId(dto.getId());

        Exam saved = examRepo.save(entity);
        return ExamDTO.fromEntity(saved);
    }

    /** 刪除單筆（回傳是否成功） */
    @Transactional
    public boolean deleteExamById(Integer id) {
        if (id == null || !examRepo.existsById(id)) return false;
        examRepo.deleteById(id);
        return true;
    }
}

