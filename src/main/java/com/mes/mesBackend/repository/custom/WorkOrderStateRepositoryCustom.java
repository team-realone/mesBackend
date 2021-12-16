package com.mes.mesBackend.repository.custom;

import com.mes.mesBackend.dto.response.WorkOrderStateResponse;
import com.mes.mesBackend.entity.enumeration.OrderState;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// 8-1. 작업지시 상태 이력 정보
public interface WorkOrderStateRepositoryCustom {
    // 쟉업지시 정보 조회 , 검색조건: 작업장 id, 작업라인 id, 제조오더번호, 품목계정 id, 지시상태, 작업기간 fromDate~toDate, 수주번호
    List<WorkOrderStateResponse> findWorkOrderStateByCondition(
            Long workProcessId,
            Long workLineId,
            String produceOrderNo,
            Long itemAccountId,
            OrderState orderState,
            LocalDate fromDate,
            LocalDate toDate,
            String contractNo
    );

    // 작업지시 정보 단일 조회
    Optional<WorkOrderStateResponse> findWorkOrderStateByIdAndWorkOrderDetail(Long workOrderDetailId);
}
