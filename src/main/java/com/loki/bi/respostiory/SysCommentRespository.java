package com.loki.bi.respostiory;

import com.loki.bi.entity.SysCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysCommentRespository extends JpaRepository<SysCommentEntity, Long> {

}
