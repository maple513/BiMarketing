
package com.loki.bi.respostiory;

import com.loki.bi.entity.NewRankTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewRankHotTaskRespository extends JpaRepository<NewRankTaskEntity, Long> {

}
