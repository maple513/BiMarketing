
package com.loki.bi.respostiory;

import com.loki.bi.entity.BiUpAvInfoEntity;
import com.loki.bi.entity.SysCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiUpAvInfoRespository extends JpaRepository<BiUpAvInfoEntity, Long> {

        List<BiUpAvInfoEntity> findAllByUpid(String upid);

}
