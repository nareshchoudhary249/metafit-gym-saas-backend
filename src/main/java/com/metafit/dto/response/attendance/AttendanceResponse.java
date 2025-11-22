package com.metafit.dto.response.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private UUID id;
    private UUID memberId;
    private String memberName;
    private String memberPhone;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String source;
    private String createdBy;
    private Long durationMinutes; // Duration in minutes

    public static AttendanceResponse fromEntity(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setMemberId(attendance.getMember().getId());
        response.setMemberName(attendance.getMember().getFullName());
        response.setMemberPhone(attendance.getMember().getPhone());
        response.setCheckIn(attendance.getCheckIn());
        response.setCheckOut(attendance.getCheckOut());
        response.setSource(attendance.getSource().name());
        response.setCreatedBy(attendance.getCreatedBy());

        // Calculate duration if checked out
        if (attendance.getCheckOut() != null) {
            long minutes = java.time.Duration.between(
                    attendance.getCheckIn(),
                    attendance.getCheckOut()
            ).toMinutes();
            response.setDurationMinutes(minutes);
        }

        return response;
    }
}