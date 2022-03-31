package com.mes.mesBackend.repository.impl;

import com.mes.mesBackend.dto.response.InputTestRequestResponse;
import com.mes.mesBackend.dto.response.ItemResponse;
import com.mes.mesBackend.entity.*;
import com.mes.mesBackend.entity.enumeration.InputTestDivision;
import com.mes.mesBackend.entity.enumeration.InspectionType;
import com.mes.mesBackend.entity.enumeration.TestType;
import com.mes.mesBackend.repository.custom.InputTestRequestRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.mes.mesBackend.entity.enumeration.InputTestState.COMPLETION;

// 14-1. 검사의뢰 등록
@RequiredArgsConstructor
public class InputTestRequestRepositoryImpl implements InputTestRequestRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    final QInputTestRequest inputTestRequest = QInputTestRequest.inputTestRequest;
    final QLotMaster lotMaster = QLotMaster.lotMaster;
    final QItem item = QItem.item;
    final QClient client = QClient.client;
    final QWareHouse wareHouse = QWareHouse.wareHouse;
    final QItemForm itemForm = QItemForm.itemForm;
    final QTestCriteria testCriteria = QTestCriteria.testCriteria1;
    final QPurchaseInput purchaseInput = QPurchaseInput.purchaseInput;
    final QOutSourcingInput outSourcingInput = QOutSourcingInput.outSourcingInput;
    final QOutSourcingProductionRequest outSourcingProductionRequest = QOutSourcingProductionRequest.outSourcingProductionRequest;
    final QPurchaseRequest purchaseRequest = QPurchaseRequest.purchaseRequest;


    // 검사의뢰등록 response 단일 조회 및 예외
    @Override
    @Transactional(readOnly = true)
    public Optional<InputTestRequestResponse> findResponseByIdAndDeleteYnFalse(Long id,  InputTestDivision inputTestDivision) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(
                                Projections.fields(
                                        InputTestRequestResponse.class,
                                        inputTestRequest.id.as("id"),
                                        lotMaster.id.as("lotId"),
                                        lotMaster.lotNo.as("lotNo"),
                                        purchaseInput.id.as("purchaseInputNo"),
                                        outSourcingInput.id.as("outsourcingInputNo"),
                                        item.itemNo.as("itemNo"),
                                        item.itemName.as("itemName"),
                                        item.manufacturerPartNo.as("itemManufacturerPartNo"), // 제조사품번: 품목의 제조사품번
//                                      item.clientItemNo.as("itemClientPartNo"),   // 고객사품번: 품목의 거재처품번
                                        client.clientName.as("manufacturerName"),   // 제조사: 품목의 제조사
//                                      client.clientName.as("clientName"),         // 고객사: 제조사와 동일
                                        wareHouse.wareHouseName.as("warehouse"),
                                        itemForm.form.as("itemForm"),
                                        inputTestRequest.inspectionType.as("inspectionType"),
                                        item.testType.as("testType"),
                                        testCriteria.testCriteria.as("testCriteria"),
                                        purchaseInput.inputTestYn.as("inputTestYn"),
                                        purchaseInput.testReportYn.as("testReportYn"),
                                        purchaseInput.coc.as("coc"),
                                        inputTestRequest.createdDate.as("requestDate"),
                                        inputTestRequest.requestAmount.as("requestAmount"),
                                        inputTestRequest.testCompletionRequestDate.as("testCompletionRequestDate")
                                )
                        )
                        .from(inputTestRequest)
                        .innerJoin(lotMaster).on(lotMaster.id.eq(inputTestRequest.lotMaster.id))
                        .innerJoin(item).on(item.id.eq(lotMaster.item.id))
                        .leftJoin(purchaseInput).on(purchaseInput.id.eq(lotMaster.purchaseInput.id))
                        .leftJoin(outSourcingInput).on(outSourcingInput.id.eq(lotMaster.outSourcingInput.id))
                        .leftJoin(client).on(client.id.eq(item.manufacturer.id))
                        .leftJoin(wareHouse).on(wareHouse.id.eq(lotMaster.wareHouse.id))
                        .leftJoin(itemForm).on(itemForm.id.eq(item.itemForm.id))
                        .leftJoin(testCriteria).on(testCriteria.id.eq(item.testCriteria.id))
                        .leftJoin(outSourcingInput).on(outSourcingInput.id.eq(lotMaster.outSourcingInput.id))
                        .leftJoin(outSourcingProductionRequest).on(outSourcingProductionRequest.id.eq(outSourcingInput.productionRequest.id))
                        .where(
                                inputTestRequest.id.eq(id),
                                isInputTestRequestDeleteYnFalse(),
                                isInputTestRequestDivision(inputTestDivision)
                        )
                        .fetchOne()
        );
    }

    // 검사의뢰 리스트 검색 조회
    // 검색조건: 창고 id, LOT 유형 id, 품명|품목, 검사유형, 품목그룹, 요청유형, 의뢰기간
    @Override
    @Transactional(readOnly = true)
    public List<InputTestRequestResponse> findAllByCondition(
            Long warehouseId,
            Long lotTypeId,
            String itemNoAndName,
            InspectionType inspectionType,
            Long itemGroupId,
            TestType testType,
            LocalDate fromDate,
            LocalDate toDate,
            InputTestDivision inputTestDivision
    ) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                InputTestRequestResponse.class,
                                inputTestRequest.id.as("id"),
                                lotMaster.id.as("lotId"),
                                lotMaster.lotNo.as("lotNo"),
                                purchaseInput.id.as("purchaseInputNo"),
                                outSourcingInput.id.as("outsourcingInputNo"),
                                item.id.as("itemId"),
                                item.itemNo.as("itemNo"),
                                item.itemName.as("itemName"),
                                item.manufacturerPartNo.as("itemManufacturerPartNo"), // 제조사품번: 품목의 제조사품번
//                                item.clientItemNo.as("itemClientPartNo"),   // 고객사품번: 품목의 거재처품번
                                client.clientName.as("manufacturerName"),   // 제조사: 품목의 제조사
//                                client.clientName.as("clientName"),         // 고객사: 제조사와 동일
                                wareHouse.wareHouseName.as("warehouse"),
                                itemForm.form.as("itemForm"),
                                testCriteria.testCriteria.as("testCriteria"),
                                purchaseInput.inputTestYn.as("inputTestYn"),
                                purchaseInput.testReportYn.as("tesntReportYn"),
                                purchaseInput.coc.as("coc"),
                                inputTestRequest.createdDate.as("requestDate"),
                                inputTestRequest.requestAmount.as("requestAmount"),
                                inputTestRequest.testCompletionRequestDate.as("testCompletionRequestDate"),
                                inputTestRequest.inspectionType.as("inspectionType"),
                                item.testType.as("testType")
                        )
                )
                .from(inputTestRequest)
                .innerJoin(lotMaster).on(lotMaster.id.eq(inputTestRequest.lotMaster.id))
                .innerJoin(item).on(item.id.eq(lotMaster.item.id))
                .leftJoin(purchaseInput).on(purchaseInput.id.eq(lotMaster.purchaseInput.id))
                .leftJoin(outSourcingInput).on(outSourcingInput.id.eq(lotMaster.outSourcingInput.id))
                .leftJoin(client).on(client.id.eq(item.manufacturer.id))
                .leftJoin(wareHouse).on(wareHouse.id.eq(lotMaster.wareHouse.id))
                .leftJoin(itemForm).on(itemForm.id.eq(item.itemForm.id))
                .leftJoin(testCriteria).on(testCriteria.id.eq(item.testCriteria.id))
                .leftJoin(outSourcingProductionRequest).on(outSourcingProductionRequest.id.eq(outSourcingInput.productionRequest.id))
                .where(
                        isWareHouseEq(warehouseId),
                        isLotTypeEq(lotTypeId),
                        isItemNoAndItemNameContain(itemNoAndName),
                        isItemGroupEq(itemGroupId),
                        isRequestDateBetween(fromDate, toDate),
                        isInputTestRequestDeleteYnFalse(),
                        isInputTestRequestDivision(inputTestDivision),
                        isInspectionTypeEq(inspectionType),
                        isTestTypeEq(testType)
                )
                .fetch();
    }

    // LOT Master 의 생성수량
    @Override
    @Transactional(readOnly = true)
    public Integer findLotMasterCreataeAmountByLotMasterId(Long lotMasterId) {
        return jpaQueryFactory
                        .select(lotMaster.createdAmount)
                        .from(lotMaster)
                        .where(
                                lotMaster.id.eq(lotMasterId),
                                lotMaster.deleteYn.isFalse()
                        )
                        .fetchOne();
    }

    // 검사요청상태값 별 검사요청 조회
    @Override
    public Optional<InputTestRequest> findByIdAndInputTestDivisionAndDeleteYnFalse(
            Long inputTestRequestId,
            InputTestDivision inputTestDivision
    ) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(inputTestRequest)
                        .where(
                                isInputTestRequestDivision(inputTestDivision),
                                inputTestRequest.id.eq(inputTestRequestId),
                                isInputTestRequestDeleteYnFalse()
                        )
                        .fetchOne()
        );
    }

    // lot id 로 생산실적의 workOrderDetailNo 조회
//    @Override
//    public Optional<String> findWorkOrderNoByLotId(Long lotMasterId) {
//        return Optional.ofNullable(
//                jpaQueryFactory
//                        .select(workOrderDetail.orderNo)
//                        .from(productionPerformance)
//                        .leftJoin(lotMaster).on(lotMaster.id.eq(productionPerformance.lotMaster.id))
//                        .leftJoin(workOrderDetail).on(workOrderDetail.id.eq(productionPerformance.workOrderDetail.id))
//                        .where(
//                                lotMaster.id.eq(lotMasterId),
//                                productionPerformance.deleteYn.isFalse(),
//                                workOrderDetail.deleteYn.isFalse(),
//                                lotMaster.deleteYn.isFalse()
//                        )
//                        .fetchOne()
//        );
//    }

    // 해당 lotMasterId 와 같은 검사 id 가져옴
    @Override
    public boolean findInputTestYnByLotMasterId(Long lotMasterId) {
         Integer fetchOne = jpaQueryFactory
                 .selectOne()
                 .from(inputTestRequest)
                 .where(
                         inputTestRequest.deleteYn.isFalse(),
                         inputTestRequest.inputTestState.eq(COMPLETION)
                 )
                 .fetchFirst();
         return fetchOne != null;
//        return Optional.ofNullable(
//                jpaQueryFactory
//                        .select(inputTestRequest.id)
//                        .from(inputTestRequest)
//                        .where(
//                                inputTestRequest.lotMaster.id.eq(lotMasterId),
//                                inputTestRequest.inputTestState.eq(COMPLETION),
//                                inputTestRequest.deleteYn.isFalse()
//                        )
//                        .fetchOne()
//        );
    }

    // 검사의뢰 가능한 품목정보 조회
    @Override
    public List<ItemResponse.noAndName> findInputTestRequestPossibleItems(InputTestDivision inputTestDivision) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                ItemResponse.noAndName.class,
                                item.id.as("id"),
                                item.itemNo.as("itemNo"),
                                item.itemName.as("itemName")
                        )
                )
                .from(purchaseInput)
                .leftJoin(lotMaster).on(lotMaster.purchaseInput.id.eq(purchaseInput.id))
                .leftJoin(purchaseRequest).on(purchaseRequest.id.eq(purchaseInput.purchaseRequest.id))
                .leftJoin(item).on(item.id.eq(purchaseRequest.item.id))
                .where(
                        purchaseInput.inputTestYn.isTrue(),
                        purchaseInput.deleteYn.isFalse(),
                        lotMaster.inputAmount.eq(0),
                        lotMaster.createdAmount.ne(lotMaster.checkRequestAmount)    // lotMaster 의 생성수량과 검사요청수량이 같지 않은거
                )
                .groupBy(item.id)
                .fetch();
    }

    // 검사의뢰 가능한 lotMaster 조회
    @Override
    public List<InputTestRequestResponse> findInputTestRequestPossibleLotMasters(Long itemId) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                InputTestRequestResponse.class,
                                lotMaster.id.as("lotId"),
                                lotMaster.lotNo.as("lotNo"),
                                purchaseInput.id.as("purchaseInputNo"),
                                item.itemNo.as("itemNo"),
                                item.itemName.as("itemName"),
                                item.manufacturerPartNo.as("itemManufacturerPartNo"), // 제조사품번: 품목의 제조사품번
                                client.clientName.as("manufacturerName"),   // 제조사: 품목의 제조사
                                wareHouse.wareHouseName.as("warehouse"),
                                itemForm.form.as("itemForm"),
                                item.testType.as("testType"),
                                testCriteria.testCriteria.as("testCriteria"),
                                purchaseInput.inputTestYn.as("inputTestYn"),
                                purchaseInput.testReportYn.as("testReportYn"),
                                purchaseInput.coc.as("coc"),
                                lotMaster.checkRequestAmount.as("requestAmount"),
                                lotMaster.checkAmount.as("checkAmount")
                        )
                )
                .from(lotMaster)
                .leftJoin(purchaseInput).on(purchaseInput.id.eq(lotMaster.purchaseInput.id))
                .leftJoin(purchaseRequest).on(purchaseRequest.id.eq(purchaseInput.purchaseRequest.id))
                .leftJoin(item).on(item.id.eq(lotMaster.item.id))
                .leftJoin(client).on(client.id.eq(item.manufacturer.id))
                .leftJoin(wareHouse).on(wareHouse.id.eq(lotMaster.wareHouse.id))
                .leftJoin(itemForm).on(itemForm.id.eq(item.itemForm.id))
                .leftJoin(testCriteria).on(testCriteria.id.eq(item.testCriteria.id))
                .where(
                        item.id.eq(itemId),
                        lotMaster.deleteYn.isFalse(),
                        lotMaster.createdAmount.ne(lotMaster.checkRequestAmount),
                        lotMaster.inputAmount.eq(0)
                )
                .fetch();
    }

    // 창고 id
    private BooleanExpression isWareHouseEq(Long wareHouseId) {
        return wareHouseId != null ? wareHouse.id.eq(wareHouseId) : null;
    }
    // LOT 유형 id
    private BooleanExpression isLotTypeEq(Long lotTypeId) {
        return lotTypeId != null ? lotMaster.lotType.id.eq(lotTypeId) : null;
    }
    // 품명|품목,
    private BooleanExpression isItemNoAndItemNameContain(String itemNoAndName) {
        return itemNoAndName != null ? item.itemNo.contains(itemNoAndName).or(item.itemName.contains(itemNoAndName)) : null;
    }
    // 품목그룹
    private BooleanExpression isItemGroupEq(Long itemGroupId) {
        return itemGroupId != null ? item.itemGroup.id.eq(itemGroupId) : null;
    }
    // 의뢰기간
    private BooleanExpression isRequestDateBetween(LocalDate fromDate, LocalDate toDate) {
        return fromDate != null ? inputTestRequest.createdDate.between(fromDate.atStartOfDay(), LocalDateTime.of(toDate, LocalTime.MAX).withNano(0)) : null;
    }
    // 삭제여부
    private BooleanExpression isInputTestRequestDeleteYnFalse() {
        return inputTestRequest.deleteYn.isFalse();
    }
    // 검사방법
    private BooleanExpression isInspectionTypeEq(InspectionType inspectionType) {
        return inspectionType != null ? inputTestRequest.inspectionType.eq(inspectionType) : null;
    }
    // 검사유형
    private BooleanExpression isTestTypeEq(TestType testType) {
        return testType != null ? item.testType.eq(testType) : null;
    }

    // 14-1. 부품수입검사 요청 조회 PART
    // 15-1. 외주수입검사 요청 조회 OUT_SOURCING
    // 16-1. 부품수입검사 요청 조회 PRODUCT
    private BooleanExpression isInputTestRequestDivision(InputTestDivision inputTestDivision) {
        return inputTestRequest.inputTestDivision.eq(inputTestDivision);
    }
}
