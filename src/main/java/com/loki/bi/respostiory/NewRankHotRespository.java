
package com.loki.bi.respostiory;

import com.loki.bi.entity.NewRankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewRankHotRespository extends JpaRepository<NewRankEntity, Long> {


}
