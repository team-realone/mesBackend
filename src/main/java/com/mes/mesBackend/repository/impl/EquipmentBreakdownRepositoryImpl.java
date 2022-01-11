package com.mes.mesBackend.repository.impl;

import com.mes.mesBackend.dto.response.EquipmentBreakdownFileResponse;
import com.mes.mesBackend.dto.response.EquipmentBreakdownResponse;
import com.mes.mesBackend.entity.*;
import com.mes.mesBackend.repository.custom.EquipmentBreakdownRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

// 17-2. 설비 고장 수리내역 등록
@RequiredArgsConstructor
public class EquipmentBreakdownRepositoryImpl implements EquipmentBreakdownRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    final QEquipment equipment = QEquipment.equipment;
    final QEquipmentBreakdown equipmentBreakdown = QEquipmentBreakdown.equipmentBreakdown;
    final QWorkCenter workCenter = QWorkCenter.workCenter;
    final QEquipmentBreakdownFile equipmentBreakdownFile = QEquipmentBreakdownFile.equipmentBreakdownFile;
    final QWorkLine workLine = QWorkLine.workLine;

    // 설비고장 리스트 검색 조회, 검색조건: 작업장 id, 설비유형, 작업기간 fromDate~toDate
    @Override
    public List<EquipmentBreakdownResponse> findEquipmentBreakdownResponsesByCondition(Long workCenterId, Long workLineId, LocalDate fromDate, LocalDate toDate) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                EquipmentBreakdownResponse.class,
                                equipmentBreakdown.id.as("id"),
                                equipmentBreakdown.breakDownDate.as("breakdownDate"),
                                equipment.id.as("equipmentId"),
                                equipment.equipmentCode.as("equipmentCode"),
                                equipment.equipmentName.as("equipmentName"),
                                workLine.workLineName.as("equipmentType"),
                                equipmentBreakdown.reportDate.as("reportDate"),
                                equipmentBreakdown.requestBreakType.as("requestBreakType"),
                                equipmentBreakdown.breakNote.as("breakNote"),
                                equipmentBreakdown.breakReason.as("breakReason"),
                                equipmentBreakdown.causeOfFailure.as("causeOfFailure"),
                                equipmentBreakdown.arrivalDate.as("arrivalDate"),
                                equipmentBreakdown.repairStartDate.as("repairStartDate"),
                                equipmentBreakdown.repairEndDate.as("repairEndDate"),
                                equipmentBreakdown.note.as("note"),
                                workCenter.id.as("workCenterId"),
                                workCenter.workCenterName.as("workCenterName")
                        )
                )
                .from(equipmentBreakdown)
                .leftJoin(equipment).on(equipment.id.eq(equipmentBreakdown.equipment.id))
                .leftJoin(workCenter).on(workCenter.id.eq(equipmentBreakdown.workCenter.id))
                .leftJoin(workLine).on(workLine.id.eq(equipment.workLine.id))
                .where(
                        isWorkCenterEq(workCenterId),
                        isEquipmentTypeContain(workLineId),
                        isWorkDateBetween(fromDate, toDate),
                        isEquipmentBreakdownDeleteYnFalse()
                )
                .fetch();
    }

    // 설비고장 단일조회
    @Override
    public Optional<EquipmentBreakdownResponse> findEquipmentBreakdownResponseById(Long equipmentBreakdownId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(
                                Projections.fields(
                                        EquipmentBreakdownResponse.class,
                                        equipmentBreakdown.id.as("id"),
                                        equipmentBreakdown.breakDownDate.as("breakdownDate"),
                                        equipment.id.as("equipmentId"),
                                        equipment.equipmentCode.as("equipmentCode"),
                                        equipment.equipmentName.as("equipmentName"),
                                        workLine.workLineName.as("equipmentType"),
                                        equipmentBreakdown.reportDate.as("reportDate"),
                                        equipmentBreakdown.requestBreakType.as("requestBreakType"),
                                        equipmentBreakdown.breakNote.as("breakNote"),
                                        equipmentBreakdown.breakReason.as("breakReason"),
                                        equipmentBreakdown.causeOfFailure.as("causeOfFailure"),
                                        equipmentBreakdown.arrivalDate.as("arrivalDate"),
                                        equipmentBreakdown.repairStartDate.as("repairStartDate"),
                                        equipmentBreakdown.repairEndDate.as("repairEndDate"),
                                        equipmentBreakdown.note.as("note"),
                                        workCenter.id.as("workCenterId"),
                                        workCenter.workCenterName.as("workCenterName")
                                )
                        )
                        .from(equipmentBreakdown)
                        .leftJoin(equipment).on(equipment.id.eq(equipmentBreakdown.equipment.id))
                        .leftJoin(workCenter).on(workCenter.id.eq(equipmentBreakdown.workCenter.id))
                        .leftJoin(workLine).on(workLine.id.eq(equipment.workLine.id))
                        .where(
                                isEquipmentBreakdownIdEq(equipmentBreakdownId),
                                isEquipmentBreakdownDeleteYnFalse()
                        )
                        .fetchOne()
        );
    }
    // 설비고장 id 로 수리전 파일들 조회
    @Override
    public List<EquipmentBreakdownFileResponse> findBeforeFileResponsesByEquipmentBreakdownId(Long equipmentBreakdownId) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                EquipmentBreakdownFileResponse.class,
                                equipmentBreakdownFile.id.as("id"),
                                equipmentBreakdownFile.fileUrl.as("fileUrl")
                        )
                )
                .from(equipmentBreakdownFile)
                .innerJoin(equipmentBreakdown).on(equipmentBreakdown.id.eq(equipmentBreakdownFile.equipmentBreakdown.id))
                .where(
                        equipmentBreakdownFile.beforeFileYn.isTrue(),
                        equipmentBreakdownFile.deleteYn.isFalse(),
                        isEquipmentBreakdownIdEq(equipmentBreakdownId)
                )
                .fetch();
    }

    // 설비고장 id 로 수리후 파일들 조회
    @Override
    public List<EquipmentBreakdownFileResponse> findAfterFileResponsesByEquipmentBreakdownId(Long equipmentBreakdownId) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                EquipmentBreakdownFileResponse.class,
                                equipmentBreakdownFile.id.as("id"),
                                equipmentBreakdownFile.fileUrl.as("fileUrl")
                        )
                )
                .from(equipmentBreakdownFile)
                .innerJoin(equipmentBreakdown).on(equipmentBreakdown.id.eq(equipmentBreakdownFile.equipmentBreakdown.id))
                .where(
                        equipmentBreakdownFile.afterFileYn.isTrue(),
                        equipmentBreakdownFile.deleteYn.isFalse(),
                        isEquipmentBreakdownIdEq(equipmentBreakdownId)
                )
                .fetch();
    }

    // 작업장 id
    private BooleanExpression isWorkCenterEq(Long workCenterId) {
        return workCenterId != null ? workCenter.id.eq(workCenterId) : null;
    }

    // 설비유형
    private BooleanExpression isEquipmentTypeContain(Long equipmentType) {
        return equipmentType != null ? workLine.id.eq(equipmentType) : null;
    }
    // 작업기간 fromDate~toDate
    private BooleanExpression isWorkDateBetween(LocalDate fromDate, LocalDate toDate) {
        return fromDate != null ? equipmentBreakdown.repairStartDate.between(fromDate.atStartOfDay(), LocalDateTime.of(toDate, LocalTime.MAX).withNano(0))
                .or(equipmentBreakdown.repairEndDate.between(fromDate.atStartOfDay(), LocalDateTime.of(toDate, LocalTime.MAX).withNano(0))) : null;
    }
    // 설비고장 삭제여부
    private BooleanExpression isEquipmentBreakdownDeleteYnFalse() {
        return equipmentBreakdown.deleteYn.isFalse();
    }
    private BooleanExpression isEquipmentBreakdownIdEq(Long equipmentBreakdownId) {
        return equipmentBreakdown.id.eq(equipmentBreakdownId);
    }
}
