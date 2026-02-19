package com.metafit.dto.response.member;

import com.metafit.entity.Member;
import com.metafit.enums.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberListItem {
    private Long id;
    private String fullName;
    private String phone;
    private MemberStatus status;
    private LocalDate membershipEndDate;
    private Integer daysUntilExpiry;
    private Boolean isExpiringLittle;

    public static MemberListItem fromEntity(Member member) {
        MemberListItem item = new MemberListItem();
        item.setId(member.getId());
        item.setFullName(member.getFullName());
        item.setPhone(member.getPhone());
        item.setStatus(member.getStatus());
        item.setMembershipEndDate(member.getMembershipEndDate());

        if (member.getMembershipEndDate() != null) {
            LocalDate today = LocalDate.now();
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, member.getMembershipEndDate());
            item.setDaysUntilExpiry((int) days);
            item.setIsExpiringLittle((days > 0 && days <= 7));
        }

        return item;
    }
}
